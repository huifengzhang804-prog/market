<script setup lang="ts">
import { onLoad } from '@dcloudio/uni-app'
import { nextTick, ref } from 'vue'
import StockModal from '@/pages/modules/car/components/stock-modal.vue'
// 组件
import headNavOther from '@/pluginPackage/goods/commodityInfo/components/head-nav/head-nav-other.vue'
import SetMealList from '../../components/setMealList.vue'
// 接口类型
import type { TParamsGetSetMealDetail } from '../../apis/model/api-params-types'
import type { TResponseGetSetMealDetail, setMealProductDetails, TResponseGproductDelivery } from '../../apis/model/api-response-type'
// 接口
import { doGetSetMealDetail } from '../../apis/index'
import { doGetGproductDelivery, doGetShopInfo } from '@/apis/good'
import { doPostBudget, toConfirmOrderValid } from '@/apis/order'
// 工具
import STORAGE from '@/utils/storage'
import { doGetDefaultAddress } from '@/apis/address'
import type { Address, Geometry } from '@/apis/address/type'
import Auth from '@/components/auth/auth.vue'
import Countdown from '@/pages/plugin/secKill/components/decorationComponents/countdown.vue'

const showStockModal = ref(false)
const stockModalData = ref({})
// 价格除以10000
const { divTenThousand } = useConvert()
// 套餐组件
const setMealListRef = ref<InstanceType<typeof SetMealList>>()
// 商铺id
const childrenShopId = ref('')
const onLoadData = ref({ shopId: '', setMealId: '' })
onLoad((res) => {
  uni.$emit('updateTitle')
  if (!res) return
  const { shopId, setMealId } = res
  onLoadData.value.setMealId = setMealId
  onLoadData.value.shopId = shopId
  handleGetSetMealDetail({ shopId, setMealId })
  childrenShopId.value = shopId
})
const init = () => {
  handleGetSetMealDetail({ ...onLoadData.value })
}
// 套餐详细信息
const setMealData = ref<TResponseGetSetMealDetail>()

// 获取套餐详细信息
const handleGetSetMealDetail = async (params: TParamsGetSetMealDetail) => {
  const res = await doGetSetMealDetail(params)
  const { code, data, msg } = res
  if (code !== 200 || !data) return uni.showToast({ title: `${msg || '获取套餐基本信息失败'}`, icon: 'none' })
  setMealData.value = data
  handleOnlySpac(data.setMealProductDetails)
}
// 节省的价格
const sparePrice = ref(0)
const matchingPrice = ref(0)
// 主商品 id
let mainProductId: Long = ''
/**
 * 找到单规格的商品保存入子组件中并计算节省的价格
 * @param {*} setMealDetail
 */
const handleOnlySpac = (setMealDetail: setMealProductDetails[]) => {
  const onlySpacs: Obj = {}
  setMealDetail.forEach((item) => {
    if (item.setMealProductSkuDetails.length === 1) {
      const onlySpac = item.setMealProductSkuDetails[0]

      const obj = {
        ...onlySpac.storageSku,
        salePrice: onlySpac.matchingPrice,
        stock: onlySpac.matchingStock,
        id: onlySpac.skuId,
        specs: onlySpac.skuName,
        stockType: onlySpac.stockType,
        saveAtLeast: onlySpac.saveAtLeast,
        productName: item.productName,
        productAttributes: item.productAttributes,
      }
      if (item.productAttributes === 'MAIN_PRODUCT' || setMealData.value?.setMealType === 'FIXED_COMBINATION') {
        nextTick(() => {
          setMealListRef.value?.saveCoppleteSku(item.productId, obj)
        })
      }
      onlySpacs[item.productId] = obj
    }
    if (item.productAttributes === 'MAIN_PRODUCT') {
      mainProductId = item.productId
    }
  })
  nextTick(() => {
    setMealListRef.value?.saveOnlySkus(onlySpacs)
  })
}
const discountFlag = ref(false)
// 节省价格计算
const countSparePrice = (price: number[], selected: boolean) => {
  discountFlag.value = selected
  sparePrice.value = price[0]
  matchingPrice.value = price[1]
}

// 点击立即购买进行校验并跳转页面
const handlePay = () => {
  const check = setMealListRef.value?.checkSpac()
  if (!check) return
  // 价格sku里面计算
  handleBuyNow()
}

// 获取店铺信息
const handleBuyNow = async () => {
  const { code, data, msg } = await doGetShopInfo({ shopId: childrenShopId.value, type: 'SHOP_HOME' })
  if (code !== 200 || !data) return uni.showToast({ title: `${msg || '获取店铺基本信息失败'}`, icon: 'none' })
  // 已完成选择后所有商品sku
  const completeSku = setMealListRef.value?.completeSku
  // 已完成选择后所有商品的属性
  const completeAttr: { [key: string]: any } | undefined = setMealListRef.value?.allAttr
  // 通过所有商品sku筛选出需要数据
  const res = await handleDeliveryData(completeSku)
  if (!res) return
  const { mainProduict, deliveryData } = res
  // 活动Id
  const activityId = setMealData.value?.setMealId
  // param需要数据
  const products = []
  const setMealProducts = []
  let key: string
  for (key in completeSku) {
    const gproduct = deliveryData.find((item) => item.productId === key)
    const { image, price, salePrice, productName, id, productAttributes, skuStock, specs } = completeSku[key]
    const productsObj = {
      skuId: id,
      image,
      price,
      id: key,
      salePrice,
      productId: key,
      distributionMode: mainProduict?.distributionMode,
      productName,
      num: 1,
      freightTemplateId: gproduct?.freightTemplateId,
      weight: gproduct?.weight ?? '0',
      productFeaturesValue: completeAttr?.[key] || null,
      specs,
    }

    const setMealProductsObj = {
      activityId,
      productAttributes,
      shopId: childrenShopId.value,
      skuId: id,
      skuStock,
      productId: key,
    }
    products.push(productsObj)
    setMealProducts.push(setMealProductsObj)
  }
  const param = [
    {
      ...data,
      shopLogo: data.logo,
      shopName: data.name,
      shopId: childrenShopId.value,
      products,
      distributionMode: setMealData.value?.distributionMode,
      activityParam: {
        type: 'PACKAGE',
        activityId,
        extra: {
          setMealProducts,
        },
        stackable: setMealData.value?.stackable,
      },
    },
  ]
  STORAGE.set(`commodityInfo`, param)

  // 跳转支付页面前校验
  const shopPackages = param.map((shopProducs) => {
    const { shopId, shopName, shopLogo, products } = shopProducs
    return {
      id: shopId,
      name: shopName,
      logo: shopLogo,
      remark: {},
      products: products.map(({ id, skuId, num, productFeaturesValue }) => ({ id, skuId, num, productFeaturesValue })),
    }
  })
  shopPackages.forEach((item) => {
    item.products.forEach((product: any) => {
      const valueList = product.productFeaturesValue
      const valueMap: any = {}
      valueList?.forEach((value: any) => {
        valueMap[value.id] = value?.featureValues?.map((item: any) => item.featureValueId)
      })
      product.productFeaturesValue = valueMap
    })
  })
  let receiver: Address = {
    name: '',
    mobile: '',
    area: [] as unknown as [string, string, string?],
    address: '',
    location: {
      type: 'Point',
      coordinates: [121.489551, 29.936806],
    } as Geometry,
    isDefault: false,
  }
  const address = await doGetDefaultAddress()
  if (address.code === 200 && address.data?.area?.length) {
    receiver = address.data
  } else {
    uni.showToast({ icon: 'none', title: '请添加收货地址' })
  }
  const datas = {
    activity: {
      activityId,
      extra: {
        setMealProducts,
      },
    },
    discounts: {},
    distributionMode: setMealData.value?.distributionMode,
    extra: {
      distributionMode: setMealData.value?.distributionMode,
      packUpTime: '',
      shopStoreId: '',
    },
    orderType: 'PACKAGE',
    receiver: receiver,
    shopPackages: shopPackages,
    source: 'PRODUCT',
  }
  const checkData = await doPostBudget(datas as any)
  if (checkData.code === 20025) {
    uni.showToast({
      title: `${checkData.msg}`,
      icon: 'none',
    })
    return
  }
  const toConfirmOrderValidRes = await toConfirmOrderValid(param)
  if (toConfirmOrderValidRes?.success) {
    uni.navigateTo({
      url: `/pluginPackage/order/confirmOrder/confirmOrder?source=PRODUCT`,
    })
    return
  }
  stockModalData.value = toConfirmOrderValidRes
  showStockModal.value = true
}

// 通过所有商品sku筛选出需要数据
const handleDeliveryData = async (
  completeSku: any,
): Promise<{ mainProduict: TResponseGproductDelivery | undefined; deliveryData: TResponseGproductDelivery[] }> => {
  const deliveryParams = Object.keys(completeSku).map((item) => {
    return {
      shopId: childrenShopId.value,
      productId: item,
      skuId: completeSku[item].id,
    }
  })
  const res = await doGetGproductDelivery(deliveryParams)
  if (res.code !== 200 || !res.data) return uni.showToast({ title: `${res.msg || '获取店铺基本信息失败'}`, icon: 'none' })
  const deliveryData: TResponseGproductDelivery[] = res.data
  const mainProduict: TResponseGproductDelivery | undefined = deliveryData.find((item) => item.productId === mainProductId)
  return { mainProduict, deliveryData }
}
</script>
<template>
  <!-- #ifdef H5 -->
  <q-nav title="套餐详情" />
  <!-- #endif -->
  <!-- #ifndef H5 -->
  <headNavOther ref="commodityNavRef" :is-show-assess="false" />
  <!-- #endif -->
  <image :src="setMealData?.setMealMainPicture" style="width: 750rpx; height: 750rpx" />
  <!-- 商品规格加大图 s-->
  <view class="setMealDetial">
    <view>{{ setMealData?.setMealName }}</view>
    <view class="setMealDetial__additional">
      <!-- <view class="setMealDetial__additional-price">
        <text class="f12" style="margin-right: 12rpx">最多可省</text>
        <text class="f18"> ￥{{ divTenThousand(setMealData?.saveAtLeast) }} </text>
      </view> -->
      <view>
        <text class="f12">距离结束：</text>
        <Countdown :startTime="setMealData?.endTime" :bg="false" class="time" />
      </view>
    </view>
    <text style="color: rgb(153, 153, 153); font-size: 12px">
      {{ setMealData?.setMealDescription }}
    </text>
  </view>
  <!-- 商品规格加大图 e-->

  <!-- 套装优惠列表 s-->
  <SetMealList
    ref="setMealListRef"
    :setMealSkus="setMealData?.setMealProductDetails"
    :shop-id="childrenShopId"
    :set-meal-type="setMealData?.setMealType"
    @count-price="countSparePrice"
  />
  <!-- 套装优惠列表 e-->

  <StockModal v-model="showStockModal" :stockModalData="stockModalData" @init="init" />
  <view class="footer-pay-placeholder"></view>
  <!-- 底部结算商品 s-->
  <view class="footerPay">
    <view>
      <view class="footerPay__reality">
        套餐价
        <text style="color: rgba(222, 50, 36, 1); font-family: AlibabaSans-regular; font-size: 28rpx"
          >￥<text style="font-size: 38rpx">{{ String(divTenThousand(matchingPrice)).split('.')[0] }}</text>
          <text v-if="String(divTenThousand(matchingPrice)).split('.')[1]">.{{ String(divTenThousand(matchingPrice)).split('.')[1] }}</text>
        </text>
      </view>
      <view v-if="discountFlag" class="footerPay__preferential">优惠 -{{ divTenThousand(sparePrice) }}（不包含运费）</view>
    </view>
    <u-button type="error" size="medium" @click="handlePay">立即购买</u-button>
  </view>
  <!-- 底部结算商品 s-->
  <Auth></Auth>
</template>
<style scoped lang="scss">
@include b(setMealDetial) {
  background-color: #fff;
  color: rgba(16, 16, 16, 1);
  font-size: 32rpx;
  padding: 26rpx;
  box-sizing: border-box;
  @include e(describe) {
    position: relative;
    color: rgba(153, 153, 153, 1);
    font-size: 12px;
    margin: 12rpx 0;
    @include m(icon) {
      position: absolute;
      right: 0;
      top: 0;
      width: 114rpx;
      height: 70rpx;
      background: linear-gradient(-89deg, rgba(255, 255, 255, 1) 48.55%, rgba(255, 255, 255, 0) 116.81%);
    }
  }
  @include e(additional) {
    display: flex;
    justify-content: space-between;
    align-items: center;
    .f18 {
      font-size: 36rpx;
    }
  }
  @include e(additional-price) {
    display: flex;
    align-items: center;
    color: rgba(222, 50, 36, 1);
    line-height: 54rpx;
  }
}
@include b(footerPay) {
  position: fixed;
  display: flex;
  align-items: center;
  justify-content: space-between;
  bottom: 0;
  left: 0;
  right: 0;
  height: 170rpx;
  line-height: 20px;
  background-color: rgba(255, 255, 255, 1);
  padding-left: 16rpx;
  padding-right: 16rpx;
  padding-top: 20rpx;
  font-size: 24rpx;
  // 底部安全距离
  padding-bottom: env(safe-area-inset-bottom);
  padding-bottom: constant(safe-area-inset-bottom);

  @include e(reality) {
    color: rgba(51, 51, 51, 1);
    vertical-align: middle;
  }
  @include e(preferential) {
    color: rgba(51, 51, 51, 1);
  }
}

@include b(footer-pay-placeholder) {
  height: 170rpx;
}

.u-btn {
  margin: 0;
}
.time {
  color: red !important;
  font-size: 24rpx;
}
</style>
