<template>
  <view>
    <view class="bar" @click="openPopup">
      <view style="display: flex; align-items: center">
        <view style="position: relative">
          <u-badge :count="totalNum" absolute :offset="[13, 13]"></u-badge>
          <image
            src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/s2b2c_2.0.8/2025/1/267762286e4b0dd23f6b8842e.png"
            style="width: 95rpx; height: 100rpx; transform: translateY(-10rpx)"
          >
          </image>
        </view>
        <view class="bar__price">{{ divTenThousand(price) }}</view>
      </view>

      <view style="display: flex; align-items: center">
        <text style="margin-right: 37rpx"> 起送{{ deliveryMoney }}元 </text>
        <view
          class="bar__button"
          :style="{
            backgroundColor: payAacive && currentCartTab === '购物车' ? '#fa3534' : '#999',
          }"
          @click.stop="handleSubmit(payAacive)"
        >
          去结算
        </view>
      </view>
    </view>
    <CarPopup
      ref="carPopupRef"
      :current-list="currentList"
      :valid-num="totalNum"
      :nvalid-num="carInvalidList.length"
      :shop-id="shopId"
      @init-car-data="initCarData"
      @change-car-data="changeCarData"
      @change-swip-open="handleChangeSwipe"
      @get-price="getPrice"
    />
    <StockModal v-model="showStockModal" :stock-modal-data="stockModalData" @init="initCarData" />
  </view>
</template>

<script setup lang="ts">
import { ref, computed, type PropType, onMounted, watch, onUnmounted } from 'vue'

import CarPopup from './car-popup.vue'
import { navigateToConfirmOrder } from '@/pages/modules/car'
import { doGetShopCarList, doGetShopCarPrice } from '@/apis/o2oshop'
import { ADD_RESS_TYPES } from '@/apis/address/model'
import type { GetShopInfoRes, GoodItemSpec } from '@/apis/o2oshop/model'
import StockModal from '@/pages/modules/car/components/stock-modal.vue'
import storage from '@/utils/storage'
import type { StoragePackage } from '@/pages/modules/car/types'
import { useUserStore } from '@/store/modules/user'

const { divTenThousand } = useConvert()
const showStockModal = ref(false)
const stockModalData = ref({})
const currentCartTab = ref<'购物车' | '失效商品'>('购物车')
const props = defineProps({
  shopInfo: {
    type: Object as PropType<GetShopInfoRes & { id: Long }>,
    default: () => ({}),
  },
  deliveryMoney: {
    type: Number,
    default: 0,
  },
})
const openPopup = () => {
  if (!carPopupRef.value) return
  carPopupRef.value.isOpenCarPopup = true
}

// 弹框ref
const carPopupRef = ref<InstanceType<typeof CarPopup>>()

// 激活价格按钮
const payAacive = computed(() => Number(divTenThousand(price.value)) >= props.deliveryMoney && Number(divTenThousand(price.value)) !== 0)
// 价格
const price = ref(0)
// 获取价格
const getPrice = async () => {
  const { data } = await doGetShopCarPrice(shopId)
  price.value = data
}

const changeCarData = (title: '购物车' | '失效商品') => {
  currentCartTab.value = title
  currentList.value = (title === '购物车' ? carValidList.value : carInvalidList.value).map((item) => ({ ...item, show: false }))
}

const totalNum = computed(() => {
  let totalNum = 0
  carValidList.value.forEach((cartValid) => {
    totalNum += cartValid.num
  })
  return totalNum
})

// 可用商品
let carValidList = ref<GoodItemSpec[]>([])
// 失效商品
let carInvalidList = ref<GoodItemSpec[]>([])
// 当前选中列表的商品
interface GoodItemSpecShow extends GoodItemSpec {
  show: boolean
}
const currentList = ref<GoodItemSpecShow[]>([])

// 初始化数据
const initCarData = async () => {
  if (shopId && useUserStore().userInfo.token) {
    const { data } = await doGetShopCarList(shopId)
    // 可用商品
    carValidList.value = data.valid[0]?.products || []
    // 失效商品
    carInvalidList.value = data.invalid[0]?.products || []
    // 当前选中列表的商品
    currentList.value = data.valid[0]?.products || []
    // 初始化商品
    getPrice()
  }
}

/**
 * 整合商品数据存储缓存供提交订单使用
 */
const sortData = (data: GoodItemSpec[]): StoragePackage[] => {
  const { logo, name, shopMode, address, contractNumber } = props.shopInfo
  let outObject: StoragePackage = {
    distributionMode: ADD_RESS_TYPES.DISTRIBUTION.INTRA_CITY_DISTRIBUTION,
    shopId,
    shopName: name,
    shopLogo: logo,
    mode: shopMode,
    address,
    products: [],
    contractNumber,
  }
  for (let j = 0; j < data.length; j++) {
    const { id, image, num, price, salePrice, productId, productName, specs, freightTemplateId, weight, distributionMode, productAttributes } =
      data[j]
    const parasList = productAttributes?.flatMap((item) => item.featureValues) || []
    const parasPrice = parasList.reduce((pre, item) => {
      return +pre + +item.secondValue
    }, 0)
    const allPrice = parasPrice + +salePrice > 0 ? parasPrice + +salePrice : 0
    outObject.products.push({
      id: productId,
      image,
      price,
      num,
      salePrice: allPrice.toString(),
      productId,
      skuId: id,
      productName,
      specs,
      freightTemplateId,
      weight,
      distributionMode,
      productFeaturesValue: productAttributes,
      sellType: '',
    })
  }
  return [outObject]
}
const handleSubmit = async (rule: boolean) => {
  if (!rule) return
  if (currentCartTab.value === '失效商品') return
  // if (!rule) return
  const carList = sortData(carValidList.value)
  if (carList[0].products.some((ite) => ite.distributionMode.length > 1)) {
    // 多配送方式跳转页面
    storage.set('carList', carList)
    uni.navigateTo({
      url: '/pluginPackage/cart-point/cart-point',
    })
    // 跳转页面
    return
  }
  // 没有多配送方式直接去结算
  const res = await navigateToConfirmOrder(carList)
  if (!res.success) {
    stockModalData.value = res
    showStockModal.value = true
  }
}

const handleChangeSwipe = (uniqueId: string) => {
  currentList.value.forEach((item) => {
    item.show = item.uniqueId === uniqueId
  })
}

let shopId: Long = ''
watch(
  () => props.shopInfo,
  (val) => {
    if (val) {
      shopId = val.id
      initCarData()
    }
  },
  {
    immediate: true,
  },
)
onMounted(() => {
  uni.$on('initCarData', initCarData)
})
onUnmounted(() => {
  uni.$off('initCarData', initCarData)
})
defineExpose({
  initCarData,
})
</script>

<style lang="scss" scoped>
@include b(bar) {
  position: fixed;
  display: flex;
  bottom: 18rpx;
  left: 20rpx;
  right: 20rpx;
  border-radius: 67px;
  background: #595754;
  width: 710rpx;
  height: 90rpx;
  padding-left: 28rpx;
  justify-content: space-between;
  font-weight: normal;
  color: #ffffff;
  z-index: 101;

  @include e(price) {
    font-weight: 700;
    margin-left: 19rpx;
    font-size: 32rpx;

    &::before {
      content: '￥';
      font-size: 24rpx;
    }
  }

  @include e(button) {
    width: 192rpx;
    height: 90rpx;
    border-radius: 95rpx;
    background: #fa3534;
    font-size: 34rpx;
    line-height: 90rpx;
    text-align: center;
  }
}
</style>
