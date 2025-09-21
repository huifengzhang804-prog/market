<script setup lang="ts">
import { ref, type PropType, watch, nextTick } from 'vue'

import { doGetGoodDetail } from '@/apis/good'
// 组件
import SpecPopup from './spec-popup.vue'
// props类型
import type { setMealProductDetails } from '../apis/model/api-response-type'
import type { StorageSku } from '@/apis/good/model/index'
import type { extra } from '@/pluginPackage/goods/commodityInfo/types'
import useConvert from '@/composables/useConvert'

const props = defineProps({
  setMealSkus: {
    type: Array as PropType<setMealProductDetails[]>,
    default: () => [],
  },
  shopId: {
    type: String,
    required: true,
  },
  setMealType: {
    type: String,
    default: '',
  },
})
// 是否是固定搭配
const isFlxed = ref(false)
const { divTenThousand } = useConvert()
watch(
  () => props.setMealType,
  () => {
    isFlxed.value = props.setMealType === 'FIXED_COMBINATION'
  },
)
// 多选框选中数据
const checkedBox = ref<Long[]>([])
let mainProdId: Long
// 初始化多选框默认数据
const initCheckBox = () => {
  // 固定搭配全部都要
  if (isFlxed.value) {
    checkedBox.value = props.setMealSkus.map((item) => item.productId)
    return
  }
  // 保存主商品
  const mainProd = props.setMealSkus.find((item) => item.productAttributes === 'MAIN_PRODUCT')
  if (mainProd) {
    mainProdId = mainProd.productId
    checkedBox.value.push(mainProd.productId)
  }
}

// 已经选好商品的规格的sku合集
const onlySkys = ref<any>({})
const mainProduct = ref<Long>('')
const selected = ref(false)
const saveOnlySkus = (sku: any) => {
  if (checkedBox.value.length === 0) initCheckBox()
  onlySkys.value = { ...onlySkys.value, ...sku }
  checkboxChange({ detail: { value: checkedBox.value } })
  selected.value = true
}
// 上一次的复选框
let preCheckedBox: string[] = []
// 多出来的复选框
const checkboxChange = (e: any) => {
  checkedBox.value = e.detail.value
  if (preCheckedBox.length > checkedBox.value.length) {
    const more = preCheckedBox.find((item) => !checkedBox.value.includes(item))
    if (more && more !== mainProdId) saveCoppleteSku(more, onlySkys.value[more], false)
  } else {
    checkedBox.value.forEach((item) => {
      if (onlySkys.value[item] && (item !== mainProdId || !completeSku.value[mainProdId])) {
        saveCoppleteSku(item, onlySkys.value[item])
      }
    })
  }
  preCheckedBox = e.detail.value
}

// 弹窗组件
const specPopupRef = ref<InstanceType<typeof SpecPopup>>()
/**
 * 打开弹出框
 * @param {*} productId
 * @param {*} setMealProductSku
 */
// 当前商品全部sku
const setMealProductSkuDetails = ref<StorageSku[]>([])
interface StorageSkuPro extends StorageSku {
  productName: string
  productAttributes: string
}
let currentProductId: Long
const openSpec = async (details: setMealProductDetails) => {
  const { productId, productName, productAttributes, setMealProductSkuDetails: setMealProductSku } = details
  currentProductId = productId.toString()
  const skuArr: StorageSkuPro[] = []
  setMealProductSku.forEach((item) => {
    if (item.matchingStock === '0') return
    skuArr[skuArr.length] = {
      ...item.storageSku,
      salePrice: item.matchingPrice,
      stock: item.matchingStock,
      id: item.skuId,
      specs: item.skuName,
      stockType: item.stockType,
      productName,
      productAttributes,
    }
  })

  const { code, data } = await doGetGoodDetail(productId, props.shopId)
  if (code === 200 && data) {
    setMealProductSkuDetails.value = skuArr.map((v, index) => {
      return {
        ...v,
        salePrices: data.salePrices[index],
      }
    })
    // 商品属性
    const skuId = completeSku.value[productId]?.id
    specPopupRef.value?.open(productId, props.shopId, skuId, data.extra)
  } else {
    uni.showToast({
      title: '获取商品信息失败',
      icon: 'none',
    })
  }
}

// 已经选择完成的商品sku
const completeSku = ref<any>({})
// 通过完成的商品sku查找商品属性
const findSpac = (productId: Long) => {
  const allfeatureValues = allAttr.value[productId]?.flatMap((item: any) => item.featureValues) || []
  const values = allfeatureValues.map((item: any) => item.firstValue + item.secondValue / 10000 + '元')
  if (onlySkys.value[productId]?.specs?.length) {
    return '' + [...onlySkys.value[productId]!.specs, ...values].join('， ')
  } else if (values.length) {
    return '' + [...values].join('， ')
  } else if (mainProduct.value === productId && (values.length === 0 || !onlySkys.value)) {
    props.setMealSkus.forEach((item) => {
      if (item.productId === productId) {
        item.flag = true
      }
    })
  } else {
    return '请选择'
  }
}
const emit = defineEmits(['count-price'])

// 保存选择好的suku
const saveCoppleteSku = (key: Long, sku: any, isMore = true) => {
  if (!isMore) {
    Reflect.deleteProperty(completeSku.value, key)
  } else {
    completeSku.value[key] = sku
  }

  // 节省价格
  let countSpare = 0
  // 套餐价格
  let countPrice = 0
  for (const key in completeSku.value) {
    if (completeSku.value[key].pirceFlag) {
      countSpare +=
        Number(completeSku.value[key].salePrices) + Number(completeSku.value[key].attributePirce) - Number(completeSku.value[key].salePrice)
    }
    countPrice += Math.max(0, parseInt(completeSku.value[key].salePrice))
  }
  emit('count-price', [countSpare, countPrice], selected.value)
}
// 检查商品校验
const checkSpac = () => {
  if (checkedBox.value.length === 0) initCheckBox()

  if (checkedBox.value.length < 2) {
    uni.showToast({ title: '主商品必须搭配一个副商品', icon: 'none' })
    return false
  }

  const isAllSelectSpac = checkedBox.value.every((item) => {
    return completeSku.value[item]
  })
  if (!isAllSelectSpac) {
    uni.showToast({ title: '请先为商品选择规格', icon: 'none' })
    return false
  }
  return true
}
const allAttr = ref<Obj>({})
const saveOnlyAttr = async (attr: { key: string; value: extra['productAttributes'] }) => {
  mainProduct.value = currentProductId
  const { key, value } = attr
  allAttr.value[key] = value
  if (checkedBox.value.includes(currentProductId)) return
  checkedBox.value.push(currentProductId)
  await nextTick()
  saveCoppleteSku(currentProductId, onlySkys.value[currentProductId])
}

defineExpose({
  checkSpac,
  saveCoppleteSku,
  completeSku,
  saveOnlySkus,
  allAttr,
})
</script>

<template>
  <div class="goods-card">
    <!-- 商品列表 s -->
    <checkbox-group ref="checkboxGroupRef" style="display: flex; flex-direction: column" @change="checkboxChange">
      <view
        v-for="(item, index) in setMealSkus"
        :key="item.productId"
        class="goodsItem"
        :style="{ order: item.productAttributes === 'MAIN_PRODUCT' ? '1' : `${5 + index}` }"
      >
        <checkbox
          color="#f4887f"
          :checked="isFlxed || item.productAttributes === 'MAIN_PRODUCT' || checkedBox.includes(item.productId)"
          :disabled="isFlxed || item.productAttributes === 'MAIN_PRODUCT'"
          :value="item.productId"
          style="transform: scale(0.7)"
        />
        <view class="goodsItem__image">
          <image :src="item.productPic" style="width: 100%; height: 100%" />
        </view>
        <view style="flex: 1">
          <text class="goodsItem__title">{{ item.productName }}</text>
          <view class="goodsItem__select">
            <view v-if="item.setMealProductSkuDetails.length === 0 || item.flag" class="goodsItem__select--more" @click="openSpec(item)">默认</view>
            <view v-else class="goodsItem__select--more" @click="openSpec(item)">
              {{ findSpac(item.productId) }}
            </view>
          </view>
          <view v-if="onlySkys[item.productId]?.pirceFlag" class="goodsItem__price">
            <text>￥{{ onlySkys[item.productId] && divTenThousand(onlySkys[item.productId].salePrice).toFixed(2) }}</text>
            <text v-if="completeSku[item.productId]" style="color: #333">x 1</text>
          </view>
        </view>
      </view>
    </checkbox-group>
    <!-- 商品列表 e -->
    <SpecPopup
      ref="specPopupRef"
      :skus="setMealProductSkuDetails"
      :allChooseAttr="allAttr"
      :setMenu="true"
      @save-sku="saveOnlySkus"
      @save-attr="saveOnlyAttr"
    />
  </div>
</template>

<style scoped lang="scss">
@include b(goods-card) {
  margin: 10rpx;
  overflow: hidden;
  border-radius: 16rpx;
  background-color: rgba(255, 255, 255, 1);
}
@include b(goodsItem) {
  font-size: 24rpx;
  @include flex;
  padding: 18rpx;
  @include e(image) {
    width: 200rpx;
    height: 200rpx;
    margin-left: 10rpx;
    margin-right: 24rpx;
  }
  @include e(title) {
    @include utils-ellipsis(2);
  }
  @include e(select) {
    font-size: 24rpx;
    color: rgba(153, 153, 153, 1);
    margin-top: 30rpx;
    display: flex;
    justify-content: space-between;
    align-items: center;
    @include m(more) {
      position: relative;
      @include utils-ellipsis(1);
      max-width: 350rpx;
      padding: 10rpx;
      padding-right: 35rpx;
      background-color: rgba(243, 243, 243, 1);
      box-sizing: border-box;
      &::after {
        position: absolute;
        font-family: 'iconfont';
        content: '\e665';
        right: 10rpx;
        top: 15rpx;
        text-align: right;
      }
    }
  }
  @include e(price) {
    font-size: 26rpx;
    margin-top: 30rpx;
    color: rgb(222, 50, 36);
    display: flex;
    justify-content: space-between;
  }
}
uni-checkbox {
  &:deep(.uni-checkbox-input) {
    border-radius: 23rpx;
  }
}
uni-checkbox:not([disabled]) {
  &:deep(.uni-checkbox-input:hover) {
    border-color: #d1d1d1;
  }
}
</style>
