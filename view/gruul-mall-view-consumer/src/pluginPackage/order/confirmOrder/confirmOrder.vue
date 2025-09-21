<script lang="ts" setup>
import { onLoad, onShow } from '@dcloudio/uni-app'
import { useSubscribeStore } from '@/store/modules/subscribe'
// 优惠券 s
import CouponPopup from '@pluginPackage/coupon/components/couponPopup/coupon-popup.vue'
// 优惠券 e
import ConfirmOrderTop from '@/pluginPackage/order/confirmOrder/components/confirm-order-top.vue'
import Order from './components/order/order.vue'
import QPrice from '@/components/q-price/index.vue'
import STORAGE from '@/utils/storage'

import { doGetDefaultAddress } from '@/apis/address'
import { doGenerateOrders, doGetOrderCreateConditions, doGetOrderSettingsDealBatch, doPostBudget } from '@/apis/order'
import { ADD_RESS_TYPES } from '@/apis/address/model'
import { errorOrderProcessing } from './errGoods'
import type { StoragePackage } from '@/pages/modules/car/types'
import dispatcher from '@/pluginPackage/order/confirmOrder/hooks/dispatcher'
import { PAY_TYPE } from '@/apis/paymentDetail/model'
import { useUserStore } from '@/store/modules/user'
//同城配送 s
import IntracitySelect from '@/pluginPackage/intracity/components/intracity-select.vue'
import AddressSelect from '@/pluginPackage/intracity/components/address-select.vue'
//同城配送 e
// 门店自提
import calculate from '@/pluginPackage/order/confirmOrder/hooks/calculate'
import { doPostStoreDistanceList } from '@pluginPackage/shopStore/apis'
import StorePoint from '@pluginPackage/shopStore/components/storePoint.vue'
import { useChosseAddress } from '@/hooks/useChooseAddress'
import type { DoPostStoreDistanceListResponseQuery } from '@/apis/plugin/shopStore/model'
import type { ApishopDealSetting, OrderFormSubmitType, OrderProductType } from '@/pluginPackage/order/confirmOrder/types'
// 门店自提
import type { ApiOrderCouponVO, ProductAmount } from '@/apis/plugin/coupon/model'
import { cloneDeep } from 'lodash'
import useGoodsInfoDispatcherStore from '@/store/dispatcher/useGoodsInfoDispatcher'
import useAddressDispatcher from '@/store/dispatcher/useAddressDispatcher'
import useCartDispatcher from '@/store/dispatcher/useCartDispatcher'

import { Decimal } from 'decimal.js-light'
import type { DistributionKeyType } from '@/apis/good/model'
import { queryStoreCoupon } from '@/pages/plugin/coupon/utils'
import { type Address, type Geometry, GeometryType } from '@/apis/address/type'
import { useLocationStore } from '@/store/modules/location'
import { storeToRefs } from 'pinia'
// #ifdef H5
import AmapChoose from '@/basePackage/pages/addressManage/components/AmapChoose.vue'
// #endif
import { locToAddress } from '@/apis/amap'
import { computed, onMounted, onUnmounted, provide, reactive, ref, watch } from 'vue'

console.log('2进入页面')
const { getterLocation } = storeToRefs(useLocationStore())
const $useAddressDispatcher = useAddressDispatcher()
const $useGoodsInfoDispatcherStore = useGoodsInfoDispatcherStore()
const $useCartDispatcherStore = useCartDispatcher()
const { addDiscount, delDiscount, activeResume } = calculate()
const { orderBenefit, discountSet, discounts, activity, orderType } = dispatcher()
// 地区hooks
// 确认订单页面地址变更
const { confirmOredrAddress } = useChosseAddress()
const { getConfirmAddress, offConfirmAddress, getEdlAddress, offEdlAddress } = confirmOredrAddress()
const commodityList = ref<StoragePackage[]>([])
const submitForm = reactive<OrderFormSubmitType>({
  receiver: {
    name: '',
    mobile: '',
    area: [] as unknown as [string, string, string?],
    address: '',
    isDefault: false,
    location: {
      type: GeometryType.Point,
      coordinates: [0, 0],
    },
  },
  shopPackages: [],
  source: 'CART',
  orderType: 'COMMON',
  activity: {},
  discounts: {},
  extra: {},
  distributionMode: ADD_RESS_TYPES.DISTRIBUTION.EXPRESS,
})

const submitLoading = ref(false)
// 用于下单表单校验
const isVilidate = ref(true)
//错误订单商品记录
const errGoodsId = ref<Long>('')
//店铺下单配置数据
const shopDealSetting = ref<ApishopDealSetting>({})
const couponShow = ref(false)
const currentCouponPopupShopId = ref<Long>('0')
const productAmounts = ref<ProductAmount[]>([])
const couponPopupRef = ref()
// 如果是 O2O 且是门店自提 自提点无需调门店接口采用店铺地址即可
const o2oData = computed(() => {
  return {
    isO2O: commodityList.value[0].mode === 'O2O',
    msg: '选择门店',
    contractNumber: commodityList.value[0].contractNumber,
  }
})
// 优惠券
const allCoupons = ref<Map<Long, ApiOrderCouponVO>>(new Map<Long, ApiOrderCouponVO>())
const computedCoupon = (item: StoragePackage) => {
  return allCoupons.value.has(item.shopId) ? allCoupons.value.get(item.shopId) : ({} as ApiOrderCouponVO)
}

// 当前店铺的商品图片集合
const currentShopProducts = ref<{ id: Long; image: string }[]>([])
// 门店自提
const storePointRef = ref<{ temporarilyUnableShopStorePoint: () => void; showStore: boolean } | null>(null)
const pointList = ref<DoPostStoreDistanceListResponseQuery[]>([])
const currentPoint = ref<DoPostStoreDistanceListResponseQuery>()
const storesParam = reactive<{
  distributionMode: DistributionKeyType
  contractNumber?: string
  address?: string
  shopStoreId: string
  packUpTime: string
}>({
  distributionMode: ADD_RESS_TYPES.DISTRIBUTION.EXPRESS,
  shopStoreId: '',
  packUpTime: '',
})

/**
 * todo 尽量提到一个公共的类型文件里
 * 订单预计算响应结果数据类型
 */
interface OrderBudget {
  /**
   * 商品总价
   */
  total: Long

  /**
   * 店铺优惠
   */
  shopDiscount: Long

  /**
   * 平台优惠
   */
  platformDiscount: Long

  /**
   * 会员优惠
   */
  memberDiscount: Long

  /**
   * 总运费
   */
  freight: Long

  /**
   * 实付金额
   */
  payAmount: Long

  /**
   * 店铺满减 优惠的金额 key:店铺 id，value 满减优惠金额
   */
  shopFull: Record<Long, Long>

  /**
   * 店铺运费 key:店铺 id，value 运费
   */
  shopFreight?: Record<Long, Long>
}

//订单明细
const budget = ref<OrderBudget>({
  total: 0,
  shopDiscount: 0,
  platformDiscount: 0,
  memberDiscount: 0,
  freight: 0,
  payAmount: 0,
  shopFull: {},
  shopFreight: {},
})

//同城配送运费
const intraFalse = ref(false)
const isIntar = computed(() => submitForm.distributionMode === ADD_RESS_TYPES.DISTRIBUTION.INTRA_CITY_DISTRIBUTION)

watch(
  () => useUserStore().getterToken,
  (val) => {
    initDefaultAddress().then(() => handleBudget('watch useUserStore().getterToken'))
  },
)
onLoad(async (res) => {
  if (!res) return
  const { source } = res
  if (source) {
    submitForm.source = source as 'CART' | 'PRODUCT'
  }
})
getConfirmAddress((e: Address) => {
  submitForm.receiver = e
})
onShow(() => {
  handleBudget('onShow')
})
getEdlAddress(() => {
  initDefaultAddress()
})
onUnmounted(() => {
  $useAddressDispatcher.removeCartSubscriber(initDefaultAddress)
  offConfirmAddress()
  offEdlAddress()
})

const getMapLocation = (res: any) => {
  storePointRef.value!.showStore = true
  locToAddress(res)
  initStoreDistanceList(commodityList.value[0].shopId, res)
}

/**
 * 门店提货点获取
 */
const initStoreDistanceList = async (shopId: Long, res?: any) => {
  const params = { shopId, point: { coordinates: res ? [res.longitude, res.latitude] : getterLocation.value?.coordinates || [0, 0], type: 'Point' } }
  const { code, data, msg } = await doPostStoreDistanceList(params)
  if (code === 500 && msg === '当前店铺无可用门店,请稍后再试') {
    // 店铺未设置门店 不予下单
    if (!storePointRef.value) {
      uni.showToast({
        title: '当前店铺无可用门店',
        icon: 'none',
        duration: 2500,
        success: () => {},
      })
    } else {
      storePointRef.value.temporarilyUnableShopStorePoint()
    }
  } else if (code !== 200) {
    uni.showToast({ title: `${msg || '门店提货点获取失败'}`, icon: 'none' })
  } else {
    pointList.value = data
  }
  // }
}

const handleGetPoint = (e: DoPostStoreDistanceListResponseQuery) => {
  currentPoint.value = e
  storesParam.shopStoreId = currentPoint.value.id
}
const validateSubmitHandler: Record<ADD_RESS_TYPES.DISTRIBUTION, () => boolean> = {
  [ADD_RESS_TYPES.DISTRIBUTION.SHOP_STORE]: () => {
    // 门店自提
    if (o2oData.value.isO2O && o2oData.value.msg) {
      storesParam.address = o2oData.value.msg
      storesParam.contractNumber = o2oData.value.contractNumber
      return true
    }
    if (!storesParam.shopStoreId) {
      uni.showToast({ title: '请先选择门店', icon: 'none' })
      return false
    }
    if (!storesParam.packUpTime) {
      uni.showToast({ title: '请先选择提货时间', icon: 'none' })
      return false
    }
    return true
  },
  [ADD_RESS_TYPES.DISTRIBUTION.EXPRESS]: () => {
    if (!submitForm.receiver?.area?.length) {
      uni.showToast({ title: '请先选择地址', icon: 'none' })
      return false
    }
    return true
  },
  [ADD_RESS_TYPES.DISTRIBUTION.INTRA_CITY_DISTRIBUTION]: () => {
    if (!submitForm.receiver?.area?.length) {
      uni.showToast({ title: '请先选择地址', icon: 'none' })
      return false
    }
    return true
  },
  [ADD_RESS_TYPES.DISTRIBUTION.VIRTUAL]: () => {
    return true
  },
}

/**
 * 批量根据店铺id查询店铺交易设置
 */
const initOrderSettingsDealBatch = async () => {
  const ids = submitForm.shopPackages.map((item) => item.id)
  const { code, data } = await doGetOrderSettingsDealBatch(ids)
  if (code !== 200) return uni.showToast({ title: '获取店铺下单配置失败', icon: 'none' })
  shopDealSetting.value = data
}
const checkOrderCreation = (data: { extra: Record<string, string>; orderNo: string }): Promise<void> => {
  return new Promise((resolve, reject) => {
    loopCheckOrderCreation(1, data.orderNo, resolve, reject)
  })
}
const loopCheckOrderCreation = (count: number, orderNo: string, resolve = () => {}, reject = () => {}) => {
  if (count >= 20) {
    reject()
    return
  }
  doGetOrderCreateConditions(orderNo).then(({ code, data }) => {
    if (code !== 200 || !data) {
      setTimeout(() => {
        loopCheckOrderCreation(count + 1, orderNo, resolve, reject)
      }, 550)
      return
    }
    resolve()
  })
}
/**
 * 获取价格预算
 */
const handleBudget = async (from: string) => {
  console.log('调用函数: ', from)
  if (
    !submitForm?.receiver?.area?.length &&
    !([ADD_RESS_TYPES.DISTRIBUTION.SHOP_STORE, ADD_RESS_TYPES.DISTRIBUTION.VIRTUAL] as (typeof submitForm.distributionMode)[]).includes(
      submitForm.distributionMode,
    )
  ) {
    return
  }
  submitForm.activity = activity.value
  submitForm.discounts = discounts.value
  submitForm.orderType = orderType.value
  submitForm.extra = storesParam

  const cloneSubmitForm = cloneDeep(submitForm)
  cloneSubmitForm.shopPackages.forEach((shopPackages) => {
    shopPackages.products.forEach((product: any) => {
      const valueList = product.productFeaturesValue
      const valueMap: any = {}
      valueList?.forEach((value: any) => {
        valueMap[value.id] = value?.featureValues?.map((item: any) => item.featureValueId)
      })
      product.productFeaturesValue = valueMap
    })
  })
  try {
    const { data, code, msg } = await doPostBudget(cloneSubmitForm)
    if (code !== 200) {
      uni.showToast({
        title: `${msg}`,
        icon: 'none',
      })
      intraFalse.value = false
      return
    } else {
      intraFalse.value = true
    }
    budget.value = data
  } catch (e) {
    submitLoading.value = false
  }
}

const handleSubmit = async () => {
  if (!submitForm.receiver?.area?.length && !['VIRTUAL', 'SHOP_STORE'].includes(storesParam.distributionMode)) {
    uni.showToast({ title: '请先选择地址', icon: 'none' })
    const timer = setTimeout(() => {
      uni.navigateTo({
        url: `/basePackage/pages/addressManage/AddressManage?callKey=callConfirm`,
      })
      clearTimeout(timer)
    }, 1000)
    return
  }
  // 是否通过表单校验
  await uni.$emit('validate', isVilidate)

  if (!isVilidate.value) {
    return
  }
  if (!validateSubmitHandler[storesParam.distributionMode]()) {
    return
  }

  // #ifdef  MP-WEIXIN
  // 调起客户端小程序订阅消息界面，返回用户订阅消息的操作结果。当用户勾选了订阅面板中的“总是保持以上选择，不再询问”时，模板消息会被添加到用户的小程序设置页。
  const $SubscribeStore = useSubscribeStore()
  await $SubscribeStore.SEND_SUBSCRIBE_MESSAGE(['ORDER_PAY'])
  // #endif

  submitForm.activity = activity.value
  submitForm.discounts = discounts.value
  submitForm.orderType = orderType.value
  submitForm.extra = storesParam
  submitLoading.value = true

  const cloneSubmitForm = cloneDeep(submitForm)
  cloneSubmitForm.shopPackages.forEach((shopPackages) => {
    shopPackages.products.forEach((product: any) => {
      const valueList = product.productFeaturesValue
      const valueMap: any = {}
      valueList?.forEach((value: any) => {
        valueMap[value.id] = value?.featureValues?.map((item: any) => item.featureValueId)
      })
      product.productFeaturesValue = valueMap
    })
  })

  if (cloneSubmitForm.extra?.distributionMode === ADD_RESS_TYPES.DISTRIBUTION.SHOP_STORE && !cloneSubmitForm.extra?.packUpTime) {
    return uni.showToast({ title: '请选择提货时间', icon: 'none' })
  }
  try {
    const { data, code, msg } = await doGenerateOrders(cloneSubmitForm)
    switch (code) {
      case 200:
        uni.showLoading({
          title: '数据处理中...',
        })
        checkOrderCreation(data)
          .then(() => {
            uni.hideLoading()
            $useCartDispatcherStore.updateCartData()
            $useGoodsInfoDispatcherStore.updateData()
            //是否不需要支付
            const noNeedPay = new Decimal(budget.value.payAmount).cmp(0) < 1
            if (Object.keys(activity.value).length) {
              const extra = encodeURIComponent(JSON.stringify({ ...data.extra, activeType: submitForm.orderType }))
              // activeType
              if (noNeedPay) {
                const redirectToObj = {
                  BARGAIN: `/basePackage/pages/paySuccess/Index?orderNo=${data.orderNo}&payFrom=ORDER`,
                  TEAM: `/pluginPackage/group/views/OwnGroup?teamNo=${data.extra.teamNo}`,
                  SPIKE: `/basePackage/pages/paySuccess/Index?orderNo=${data.orderNo}&payFrom=ORDER`,
                  PACKAGE: `/basePackage/pages/paySuccess/Index?orderNo=${data.orderNo}&payFrom=ORDER`,
                  COMMON: `/basePackage/pages/paySuccess/Index?orderNo=${data.orderNo}&payFrom=ORDER`,
                }
                uni.redirectTo({
                  url: redirectToObj[submitForm.orderType],
                })
                return
              }
              uni.redirectTo({
                url: `/basePackage/pages/pay/Index?no=${data.orderNo}&orderType=${PAY_TYPE.ORDER}&extra=${extra}`,
              })
            } else {
              if (noNeedPay) {
                uni.redirectTo({ url: `/basePackage/pages/paySuccess/Index?orderNo=${data.orderNo}&payFrom=ORDER` })
                return
              }
              uni.redirectTo({
                url: `/basePackage/pages/pay/Index?no=${data.orderNo}&orderType=${PAY_TYPE.ORDER}`,
              })
            }
          })
          .catch(() => {
            uni.hideLoading()
          })
        submitLoading.value = false
        return
      case 30000:
        //收货人不能为空
        errGoodsId.value = errorOrderProcessing(commodityList.value, data.orderNo, msg!, ' 收货人不能为空')
        break
      case 30001:
        // 物流公司不能为空
        errGoodsId.value = errorOrderProcessing(commodityList.value, data.orderNo, msg!, ' 物流校验失败')
        break
      case 30002:
        // 物流公司名称与物流公司代码不能为空
        errGoodsId.value = errorOrderProcessing(commodityList.value, data.orderNo, msg!, ' 物流校验失败')
        break
      case 30003:
        // 快递单号不能为空
        errGoodsId.value = errorOrderProcessing(commodityList.value, data.orderNo, msg!, ' 快递单号不能为空')
        break
      case 30004:
        // 发货地址不能为空
        errGoodsId.value = errorOrderProcessing(commodityList.value, data.orderNo, msg!, ' 发货地址不能为空')
        break
      case 30005:
        // 店铺表单校验失败
        errGoodsId.value = errorOrderProcessing(commodityList.value, data.orderNo, msg!, ' 店铺表单校验失败')
        break
      case 30006:
        // 商品不可用
        errGoodsId.value = errorOrderProcessing(commodityList.value, data.orderNo, msg!, ' 商品不可用')
        break
      case 30007:
        // 商品库存不足
        errGoodsId.value = errorOrderProcessing(commodityList.value, data.orderNo, msg!, ' 商品库存不足')
        break
      case 30008:
        // 商品超过限购次数
        errGoodsId.value = errorOrderProcessing(commodityList.value, data.orderNo, msg!, ' 商品超限购')
        break

      default:
        uni.showToast({
          icon: 'none',
          title: `${msg ? msg : '订单提交失败'}`,
        })
        break
    }
    setTimeout(() => uni.navigateBack(), 500)
    submitLoading.value = false
  } catch (e) {
    submitLoading.value = false
  }
}

const initDefaultAddress = async () => {
  if (['VIRTUAL', 'SHOP_STORE'].includes(storesParam.distributionMode)) return Promise.reject()
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
  try {
    const { data, code } = await doGetDefaultAddress()
    if (code === 200 && data?.area?.length) {
      receiver = data
      return Promise.resolve()
    } else {
      uni.showToast({ icon: 'none', title: '请添加收货地址' })
      return Promise.reject('请添加收货地址')
    }
  } finally {
    submitForm.receiver = receiver
  }
}

onMounted(() => {
  console.log('4页面挂载')
  $useAddressDispatcher.addCartSubscriber(initDefaultAddress)
})
/**
 * 整理提交表单格式
 */
const integrationData = (shopProducsList: StoragePackage[]): void => {
  submitForm.shopPackages = shopProducsList.map((shopProducs) => {
    const { shopId, shopName, shopLogo, products } = shopProducs
    return {
      id: shopId,
      name: shopName,
      logo: shopLogo,
      remark: {},
      products: products.map(({ id, skuId, num, productFeaturesValue }) => ({ id, skuId, num, productFeaturesValue })),
    }
  })
}

/**
 * 获取商品信息且整理提交表单
 */
const sortCommodityInfo = async () => {
  console.log('3获取商品信息且整理提交表单')
  const storageCommodity: StoragePackage[] = STORAGE.get('commodityInfo') || []
  if (storageCommodity.length) {
    storesParam.distributionMode = storageCommodity[0].distributionMode
    submitForm.distributionMode = storageCommodity[0].distributionMode
    if (
      storesParam.distributionMode === ADD_RESS_TYPES.DISTRIBUTION.SHOP_STORE ||
      storesParam.distributionMode === ADD_RESS_TYPES.DISTRIBUTION.VIRTUAL
    ) {
      // 删除 receiver
      Reflect.deleteProperty(submitForm, 'receiver')
      if (storesParam.distributionMode === ADD_RESS_TYPES.DISTRIBUTION.SHOP_STORE) {
        // 门店自提查询自提点
        initStoreDistanceList(storageCommodity[0].shopId)
      }
    }
    const allProducts = storageCommodity.map((item) => item.products).flat(1)
    const tempArr = allProducts.map((item) => {
      return {
        shopId: '0',
        productId: item.productId,
        productPrice: item.salePrice,
        quantity: item.num,
        productFeaturesValue: item.productFeaturesValue,
      }
    })
    addDiscount('0', {
      name: 'addon-member',
      productInfoArr: tempArr,
      rule: null,
      priority: 3,
    })
    discountSet.value = activeResume()
    // 获取默认地址
    commodityList.value = storageCommodity.map((item) => ({ ...item, coupon: '' }))
    initDefaultAddress().then(() => {
      handleBudget('initDefaultAddress.then')
    })
    integrationData(storageCommodity)
    provide('praentShop', { shopPackages: submitForm.shopPackages, shopDealSetting: shopDealSetting.value })
    // 查询店铺交易设置
    initOrderSettingsDealBatch()
  } else {
    uni.showToast({
      icon: 'none',
      title: '获取商品数据失败',
      duration: 3000,
      success: () => {
        let time = setTimeout(() => {
          uni.navigateBack({
            delta: 1,
          })
          clearTimeout(time)
        }, 2000)
      },
    })
  }
}
// 整理提交表单格式
sortCommodityInfo()

/**
 * 查询店铺优惠券
 */
const handleChooseShopCoupon = (shopProducts: OrderProductType) => {
  if (!orderBenefit.value.coupon) {
    uni.showToast({ title: '当前商品不参与优惠券优惠哦', icon: 'none' })
    return
  }
  currentShopProducts.value = shopProducts.products.map((priduct) => ({
    id: priduct.productId,
    image: priduct.image,
  }))
  QueryCoupons(shopProducts.shopId, queryStoreCoupon(shopProducts))
}

/**
 * 查询平台优惠券
 */
const handleQueryCoupon = () => {
  currentShopProducts.value = []
  const productAmounts = [
    {
      productId: '0',
      amount: budget.value.total as string,
    },
  ]
  QueryCoupons('0', productAmounts)
}
const QueryCoupons = async (shopId: Long, productAmount: ProductAmount[]) => {
  currentCouponPopupShopId.value = shopId
  productAmounts.value = productAmount
  couponShow.value = true
}
/**
 * 选择优惠
 */
const handlechooseCoupon = (coupon: ApiOrderCouponVO, shopId: Long) => {
  if (!orderBenefit.value.coupon || !coupon) {
    delDiscount(shopId, shopId === '0' ? 'addon-platform-coupon' : 'addon-coupon')
    discountSet.value = activeResume()
    allCoupons.value.delete(shopId)
    return
  }
  if (shopId !== '0') {
    const choosedProduct = commodityList.value.find((item) => item.shopId === shopId)?.products || []
    const tempArr = choosedProduct.map((item) => {
      return {
        shopId,
        productId: item.productId,
        productPrice: item.salePrice,
        quantity: item.num,
      }
    })
    addDiscount(shopId, {
      name: 'addon-coupon',
      productInfoArr: tempArr,
      rule: coupon,
      priority: 1,
    })
  } else {
    const allProducts = commodityList.value.map((item) => item.products).flat(1)
    const tempArr = allProducts.map((item) => {
      return {
        shopId,
        productId: item.productId,
        productPrice: item.salePrice,
        quantity: item.num,
      }
    })
    addDiscount('0', {
      name: 'addon-platform-coupon',
      productInfoArr: tempArr,
      rule: coupon,
      priority: 1,
    })
  }
  discountSet.value = activeResume()
  allCoupons.value.set(shopId, coupon)
}

watch(
  () => discounts.value,
  (val) => {
    handleBudget('watch discounts.value')
  },
  {
    deep: true,
  },
)

const disabledSubmitBtn = computed(() => {
  if (!intraFalse.value && isIntar.value) {
    // 超出范围
    return true
  }
  if (['EXPRESS', 'INTRA_CITY_DISTRIBUTION'].includes(submitForm.distributionMode) && !submitForm.receiver.area.length) {
    return true
  } else if (submitForm.distributionMode === 'SHOP_STORE') {
    if (!storesParam.shopStoreId) return true
    if (!storesParam.packUpTime) return true
  }
  return false
})
// #ifdef H5
const showChooseMap = ref(false)
uni.$on('showChooseMap', (val: boolean) => {
  showChooseMap.value = val
})
// #endif
</script>
<template>
  <view>
    <view class="confirm">
      <IntracitySelect v-if="storesParam.distributionMode === ADD_RESS_TYPES.DISTRIBUTION.INTRA_CITY_DISTRIBUTION" :info="submitForm.receiver">
      </IntracitySelect>
      <!-- 快递配送 -->
      <AddressSelect v-else-if="storesParam.distributionMode === ADD_RESS_TYPES.DISTRIBUTION.EXPRESS" :info="submitForm.receiver" />

      <!-- 到店自提 -->
      <StorePoint
        v-else-if="storesParam.distributionMode === ADD_RESS_TYPES.DISTRIBUTION.SHOP_STORE"
        ref="storePointRef"
        :is-o2o="o2oData.isO2O"
        :msg="o2oData.isO2O ? o2oData.msg : '选择门店'"
        :point-list="pointList"
        :shop-id="commodityList[0].shopId"
        @get-point="handleGetPoint"
        @get-date="storesParam.packUpTime = $event"
        @mapChange="getMapLocation"
      />
      <!-- 无需配送 -->
      <ConfirmOrderTop v-else />

      <view class="confirm__list">
        <view v-for="(item, i) in commodityList" :key="i" :class="errGoodsId === item.shopId ? 'err-goods' : ''">
          <Order
            :coupon="computedCoupon(item)"
            :freight="budget.shopFreight ? budget.shopFreight[item.shopId] : '0'"
            :fullDiscount="budget.shopFull[item.shopId]"
            :idx="i"
            :info="item"
            :recordErrGoods="errGoodsId"
            :shop-deal-setting="shopDealSetting"
            @animationend="errGoodsId = ''"
            @end="errGoodsId = $event"
            @chooseShopCoupon="handleChooseShopCoupon(item)"
          />
        </view>
      </view>

      <!--   价格明细   -->
      <view class="settlement">
        <u-cell-group>
          <u-cell-item :arrow="false" :border-bottom="false" :title-style="{ fontWeight: 700, color: '#333333' }" title="价格明细">
            <text style="color: #d5d5d5; font-size: 28rpx">
              共<text style="padding: 0 7rpx">{{
                commodityList.reduce((pri, item) => {
                  return pri + item.products.length
                }, 0)
              }}</text
              >件{{ orderType === 'SPIKE' ? '秒杀价格' : '' }}
            </text>
          </u-cell-item>
          <!-- 商品总价   -->
          <u-cell-item :arrow="false" :border-bottom="false" :title-style="{ color: '#333333' }">
            <template #title>
              <view>
                <text style="color: #333333">商品总价</text>
              </view>
            </template>
            <QPrice :price="budget.total" font-size="40rpx" style="font-weight: 700" unit="￥" />
          </u-cell-item>
          <!-- 平台优惠券 -->
          <u-cell-item
            v-if="orderBenefit.coupon"
            :border-bottom="false"
            :title-style="{ color: '#333333' }"
            title="平台优惠券"
            @click="handleQueryCoupon"
          >
            <QPrice :price="budget.platformDiscount" font-size="28rpx" style="font-weight: 700; color: #f83f30" symbol="-" />
          </u-cell-item>
          <!-- 店铺优惠 -->
          <u-cell-item v-if="orderBenefit.coupon" :arrow="false" :border-bottom="false" :title-style="{ color: '#333333' }" title="店铺优惠">
            <QPrice :price="budget.shopDiscount" font-size="28rpx" style="font-weight: 700; color: #f83f30" symbol="-" />
          </u-cell-item>
          <!-- 会员折扣 -->
          <u-cell-item :arrow="false" :border-bottom="false" :title-style="{ color: '#333333' }" title="会员折扣">
            <QPrice :price="budget.memberDiscount" font-size="28rpx" style="font-weight: 700; color: #f83f30" symbol="-" />
          </u-cell-item>
          <!-- 总运费 -->
          <u-cell-item :arrow="false" :border-bottom="false" :title-style="{ color: '#333333' }" title="应付运费">
            <view style="font-weight: 700; color: #f83f30; font-size: 28rpx">
              <template v-if="new Decimal(budget.freight).cmp(0) > 0">
                <QPrice :price="budget.freight" font-size="28rpx" />
              </template>
              <template v-else>包邮</template>
            </view>
          </u-cell-item>
        </u-cell-group>
      </view>

      <!-- 底部确认bar -->
      <view class="confirm__bar">
        <view class="confirm__bar-price">
          <text>合计：</text>
          <QPrice :price="budget.payAmount" class="confirm__bar-price--total" font-size="40rpx" unit="¥" />
        </view>
        <view class="confirm__bar--btn">
          <u-button
            :custom-style="{
              padding: 0,
              margin: 0,
              width: '100%',
              height: '100%',
              borderRadius: '0',
              background: disabledSubmitBtn ? '#c0bcbc !important' : void 0,
            }"
            :disabled="disabledSubmitBtn"
            :loading="submitLoading"
            throttle-time="1000"
            type="error"
            @click="handleSubmit"
          >
            提交订单
          </u-button>
        </view>
      </view>
    </view>
    <!-- 优惠券 s -->
    <CouponPopup
      ref="couponPopupRef"
      v-model:show="couponShow"
      :images="currentShopProducts"
      :productAmounts="productAmounts"
      :shopId="currentCouponPopupShopId"
      @chooseCoupon="(coupon) => handlechooseCoupon(coupon, currentCouponPopupShopId)"
    />
    <!-- #ifdef H5 -->
    <!-- 地图选点 -->
    <AmapChoose
      v-model:show="showChooseMap"
      :initLocation="[getterLocation?.coordinates?.[0] || [121.489551, 29.936806][0], getterLocation?.coordinates?.[1] || [121.489551, 29.936806][1]]"
      @placeChoose="getMapLocation"
    />
    <!-- #endif -->
    <Auth />
  </view>
</template>

<style lang="scss" scoped>
@import '@/assets/css/animate.scss';

@include b(confirm) {
  padding: 10rpx 0 88rpx 0;
  margin: 0 20rpx;
  box-sizing: border-box;
  @include e(list) {
    margin-top: 14rpx;
  }
  @include e(bar) {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    z-index: 99;
    height: 88rpx;
    background: #fff;
    display: flex;
    justify-content: space-between;
    @include m(price) {
      font-size: 36rpx;
    }
    @include m(btn) {
      width: 180rpx;
      background: #f83f30;
      line-height: 88rpx;
      text-align: center;
      color: #fff;
      font-size: 28rpx;
    }
  }
  @include e(bar-price) {
    @include flex();
    font-size: 30rpx;
    margin-left: 20rpx;
    @include m(total) {
      font-weight: bold;
      font-size: 40rpx;
      color: #f83f30;
    }
  }
}

@include b(loading) {
  position: absolute;
  top: 30%;
  left: 50%;
  transform: translate(-50%, -50%);
}

@include b(settlement) {
  background: #fff;
  border-radius: 20rpx;
  margin-bottom: 22rpx;
  overflow: hidden;
}
:deep() {
  .u-cell {
    padding: 0 32rpx;
    height: 90rpx;
  }
  .u-border-bottom:after {
    border-bottom-width: 0px;
  }
  .u-border-bottom {
    padding-bottom: 10rpx;
  }
}
</style>
