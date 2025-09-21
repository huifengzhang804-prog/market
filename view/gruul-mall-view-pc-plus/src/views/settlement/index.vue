<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { SuccessFilled, ArrowDown } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import Storage from '@/libs/storage'
import { StoragePackage } from '@/views/shoppingcart/types/index'
import { useConversionPrice } from '@/utils/useConversionPrice'
import { commodityPrice } from '@/utils/formatCouponPrice'
import { errorOrderProcessing } from './errGoods'
import {
  doGetAddressList,
  doSetAddressDefault,
  doDelAddress,
  doEditAddress,
  doNewAddress,
  doPostBudget,
  doGetAddressByLocation,
} from '@/apis/address'
import { doGenerateOrders, doGetDeliveryTime, doGetOrderCreateConditions, doGetOrderSettingsDealBatch, doPostStoreDistanceList } from '@/apis/order'
import { initCommodityLogisticsInfo } from '@/views/detail/freight'
import { doGetFreightCalculation } from '@/apis/order'
import type { OrderType, OrderFormSubmitType, ApishopDealSetting, OrderDetails } from '@/views/settlement/types/index'
import type { ApiOrderCouponVO } from '@/types/coupon/index'
import CusForm from './cus-form.vue'
import useConvert from '@/composables/useConvert'
import { doGetOrderShopCouponPage } from '@/apis/mycoupon'
import { formattingCouponRules } from '@/views/personalcenter/assets/types/mycoupon'
import { doGetSetMealDetail } from '@/apis/setMeal'
import { useCardInfo } from '@/store/modules/cart'
import useDispatcherHooks from './hooks/useDispatcherHooks'
import useCalculateHooks from './hooks/useCalculateHooks'
import { AddressFn } from '@/components/q-address'
import { cloneDeep } from 'lodash-es'
import { useGDRegionDataStore } from '@/store/modules/GDRegionData'
import { usePropertiesListStore } from '@/store/modules/propertiesList'
import { storeToRefs } from 'pinia'
import { Checked, Geometry, GeometryType } from '@/apis/address/types'
import AmapChooseDialog from '../personalcenter/set/address/components/AmapChooseDialog.vue'
import { MOBILE } from '@/utils/RegexPool'

const { getterPropertiesList } = storeToRefs(usePropertiesListStore())

const regionData = useGDRegionDataStore().getterGDRegionData

const storage = new Storage()

const { orderBenefit, discountSet, activity, orderType } = useDispatcherHooks()
const { delDiscount, activeResume, addDiscount } = useCalculateHooks()
const { divTenThousand } = useConvert()
const $cardInfo = useCardInfo()
interface AddressItemType {
  id: string
  address: string
  area: [string, string, string?]
  isDefault: boolean
  mobile: string
  name: string
  fakeAddress: string // 假的地址，用于展示门牌号等信息(不可包含字符串: '~' )
  showSet: boolean
  location: Geometry
}
const formRef = ref()
const showChooseMap = ref(false)
const $router = useRouter()
const $route = useRoute()
const addressFlag = ref(false)
const checked1 = ref(false)
const openCoupons = ref(false)
const isCouponsAvailable = ref(true)
const addressList = ref<AddressItemType[]>([])
const addressinfo = ref<AddressItemType>({
  id: '',
  address: '',
  area: [] as unknown as [string, string, string?],
  isDefault: false,
  mobile: '',
  name: '',
  showSet: false,
  fakeAddress: '',
  location: { type: GeometryType.Point, coordinates: [] as unknown as [number, number] },
})
const rules = ref({
  name: [
    { required: true, message: '请输入姓名', trigger: 'change' },
    { min: 2, max: 5, message: '长度在 2 到 5 个字符', trigger: 'change' },
  ],
  mobile: [
    { required: true, message: '请输入手机号', trigger: 'change' },
    { pattern: MOBILE, message: '手机号格式不正确', trigger: 'change' },
  ],
  area: [{ required: true, message: '请选择定位', trigger: 'change' }],
  fakeAddress: [
    {
      required: true,
      message: '请填写详细地址',
      trigger: 'change',
    },
    {
      validator: (rule: any, value: any, callback: any) => {
        if (value.includes('~')) {
          callback(new Error('详细地址不能包含~号'))
        } else {
          callback()
        }
      },
      trigger: 'change',
    },
  ],
})
const freightInfo = ref()
const commodityList = ref<OrderType[]>([])
const submitForm = reactive<OrderFormSubmitType>({
  receiver: {
    id: '',
    name: '',
    mobile: '',
    area: [] as unknown as [string, string, string?],
    address: '',
    location: { type: GeometryType.Point, coordinates: [] as unknown as [number, number] },
  },
  shopPackages: [],
  source: 'CART',
  orderType: 'COMMON',
  activity: {
    activityId: '',
    extra: {
      userNum: '',
      setMealProducts: [
        {
          activityId: '',
          productAttributes: 'MAIN_PRODUCT',
          productId: '',
          shopId: '',
          skuId: '',
        },
      ],
      activityId: '',
    },
  },
  discounts: {},
  extra: {
    distributionMode: 'EXPRESS',
    shopStoreId: '',
    packUpTime: '',
  },
  distributionMode: 'EXPRESS',
  secKillCode: '',
})
const topComponents = ref()
topComponents.value = getterPropertiesList.value

// 获取地址信息
const addRessInfoCn = ref('')
const allCoupons = ref<Map<string, ApiOrderCouponVO>>(new Map<string, ApiOrderCouponVO>())
const fullReductions = ref<Map<string, ApiOrderCouponVO>>(new Map<string, ApiOrderCouponVO>())
const costCalculate = computed(() => commodityPrice(commodityList.value, allCoupons.value, freightInfo.value, fullReductions.value))
const isLoading = ref(false)
//错误订单商品记录
const errGoodsId = ref('')
// 是否是团购
const groupBuying = ref(false)

onMounted(() => {
  initList()
  sortCommodityInfo()
})
// 满减优惠
let budget = ref<OrderDetails>({
  freight: 0,
  memberDiscount: 0,
  payAmount: 0,
  platformDiscount: 0,
  shopDiscount: 0,
  shopFreight: {},
  shopFull: {},
  total: 0,
})
//获取价格预算
async function handleBudget() {
  submitForm.activity = activity.value
  submitForm.orderType = orderType as any
  const cloneSubmitForm = cloneDeep(submitForm)
  if (groupBuying.value) {
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
  }
  if (addressList.value.length > 0) {
    const { data, code, msg } = await doPostBudget({ ...cloneSubmitForm })
    if (code === 200) {
      budget.value = data
      fullReductions.value.set(commodityList.value[0].shopId, data.shopFull[commodityList.value[0].shopId])
    } else {
      ElMessage.error(msg)
    }
  } else {
    ElMessage.error('请添加收货地址')
  }
}
/**
 * 拼团
 */
async function initList() {
  submitForm.activity.activityId = $route.query.activityId || ''
  if ($route.query.user) {
    submitForm.activity.extra!.userNum = $route.query.user
  }
  submitForm.orderType = ($route.query.orderType as any) || 'COMMON'
  submitForm.source = $route.query.source as 'CART' | 'PRODUCT'
}
const getSetMealDetail = async (shopId: string, setMealId: string) => {
  const { data, code } = await doGetSetMealDetail(shopId, setMealId)
  if (code === 200) return data
  else return []
}
// 地址
const getAddressList = async () => {
  const { data, code, success, msg } = await doGetAddressList()
  if (code === 200 && success) {
    addressList.value = data.records
    // 取默认地址
    const defaultAddressIdx = data.records.findIndex((item: AddressItemType) => item.isDefault) as number
    if (defaultAddressIdx !== -1) {
      const defaultAddress = data.records[defaultAddressIdx]
      submitForm.receiver = defaultAddress
      // 取到默认地址之后获取运费
      initFreightPrice(commodityList.value)
      if (defaultAddressIdx > 2) {
        // 一行展示三个 超出此范围往前调整数据
        data.records.splice(defaultAddressIdx, 1)
        data.records.unshift(defaultAddress)
      }
    }
  } else {
    ElMessage.error(msg)
  }
}
/**
 * 删除地址
 */
const handleDelAddress = async (id: string) => {
  const isValidate = await ElMessageBox.confirm('确定删除该地址吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
  if (!isValidate) return
  const { code } = await doDelAddress(id)
  if (code !== 200) {
    ElMessage.error('删除失败')
    return
  }
  ElMessage.success('删除成功')
  initList()
  getAddressList()
}
/**
 * 新增地址
 */
const handleAddAddress = async () => {
  addressFlag.value = true
  await nextTick()
  formRef.value.clearValidate()
}
/**
 * 修改地址
 */
const handlePutAddress = async (address: AddressItemType) => {
  addressFlag.value = true
  await nextTick()
  addressinfo.value = JSON.parse(JSON.stringify(address))
  addressinfo.value.fakeAddress = address.address.split('~')[2] || ''
  addressinfo.value.address = `${address.address.split('~')[0] || ''}~${address.address.split('~')[1] || ''}`
  formRef.value.clearValidate()
}
/**
 * 设置默认地址
 */
const handleChangeCheck = async (id: string) => {
  const { code, success } = await doSetAddressDefault(id, true)
  if (code === 200 && success) {
    ElMessage.success('设置成功')
    initList()
    getAddressList()
  }
}

const newAddress = () => {
  formRef.value.validate((valid: boolean) => {
    if (valid) {
      // 处理下假地址
      const copyForm = JSON.parse(JSON.stringify(addressinfo.value))
      copyForm.address = `${copyForm.address}~${copyForm.fakeAddress}`
      doNewAddress(copyForm).then((res) => {
        if (res.code === 200 && res.success) {
          ElMessage.success('添加成功')
          initList()
          getAddressList()
          addressFlag.value = false
        } else {
          ElMessage.error(res.msg ? res.msg : '添加失败')
        }
      })
    }
  })
}

const editAddress = () => {
  formRef.value.validate((valid: boolean) => {
    if (valid) {
      const copyForm = JSON.parse(JSON.stringify(addressinfo.value))
      copyForm.address = `${copyForm.address}~${copyForm.fakeAddress}`
      doEditAddress(addressinfo.value.id, copyForm).then((res) => {
        if (res.code === 200 && res.success) {
          ElMessage.success('修改成功')
          initList()
          getAddressList()
          addressFlag.value = false
        } else {
          ElMessage.error(res.msg ? res.msg : '修改失败')
        }
      })
    }
  })
}
const deliveryOption = ref<string>()
const shopIdStore = ref('')
/**
 * 获取商品信息且整理提交表单
 */
async function sortCommodityInfo(flag?: boolean) {
  const storageCommodity = storage.getItem(`commodityInfo`)
  if (storageCommodity) {
    // 组装秒杀数据
    assemblySecKillData(storageCommodity)
    groupBuying.value = storageCommodity[0].groupBuying
    // submitForm = submitForm.activity
    commodityList.value = storageCommodity.map((item: OrderType) => ({ ...item, coupon: '' }))
    for (let i = 0; i < storageCommodity.length; i++) {
      let ele = storageCommodity[i]
      // 组合配送方式
      if ($route.query.source === 'CART') {
        deliveryOption.value = ele.distributionMode
        submitForm.distributionMode = ele.distributionMode
        submitForm.extra.distributionMode = ele.distributionMode
      } else {
        ele.products.forEach((item: { distributionMode: string }) => (deliveryOption.value = item?.distributionMode || item?.distributionMode?.[0]))
        submitForm.distributionMode = storageCommodity[0].distributionMode || deliveryOption.value
        submitForm.extra.distributionMode = storageCommodity[0].distributionMode || deliveryOption.value
      }
      // '门店自提'
      if (deliveryOption.value === 'SHOP_STORE') {
        shopIdStore.value = ele.shopId
        storeDistanceList(ele.shopId)
      }
      if (ele.orderType !== 'PACKAGE') {
        if (ele.orderType !== 'PACKAGE' && ele.orderType !== 'TEAM' && ele.orderType !== 'SPIKE') submitForm.activity = {}
        delete submitForm.secKillCode
        break
      }
      for (let j = 0; j < ele.products?.length; j++) {
        let el = ele.products[j]
        const infoList = {
          activityId: storageCommodity?.[0].activityParam.activityId,
          productAttributes: 'MAIN_PRODUCT',
          productId: '',
          shopId: ele.shopId,
          skuId: '',
        }
        infoList.skuId = el.skuId
        infoList.productId = el.productId
        const attributes = await getSetMealDetail(infoList.shopId, infoList.activityId)
        infoList.activityId = attributes.setMealProductDetails?.[0].setMealProductSkuDetails[0].storageSku.activityId
        const list = attributes.setMealProductDetails?.filter((item: any) => item.productId === el.productId && el.productName === item.productName)
        infoList.productAttributes = list?.[0]?.productAttributes ?? ''
        if (submitForm.activity.extra?.setMealProducts && submitForm.activity.extra.setMealProducts.length - 1 === ele.products?.length) break
        if (submitForm.activity.extra?.setMealProducts) {
          submitForm.activity.extra.setMealProducts.push(infoList as any)
        }
      }
      submitForm.orderType = ele.orderType
      delete submitForm.activity.extra?.userNum
      if (submitForm.secKillCode) {
        delete submitForm.secKillCode
      }
      if (submitForm.activity.extra?.setMealProducts) {
        submitForm.activity.extra.setMealProducts.shift()
        delete submitForm.activity.extra.activityId
      }
    }
    if (deliveryOption.value === 'SHOP_STORE') {
      submitForm.extra.packUpTime = pointTime.value
      submitForm.extra.shopStoreId = PlaceItemId.value
    }
    if (deliveryOption.value === 'INTRA_CITY_DISTRIBUTION' && submitForm.receiver.address) {
      if (submitForm.receiver.address === addressItem.value) return
      submitForm.receiver.address = addressItem.value
    }
    if (submitForm.orderType === 'SPIKE') submitForm.activity.extra = { activityId: submitForm.activity.activityId }
    else if (submitForm.orderType === 'TEAM')
      submitForm.activity.extra = { userNum: $route.query.user as string, teamNo: $route.query.teamNo as string | number }
    integrationData(commodityList.value)
    getAddressList().then(() => {
      if (!flag) {
        handleBudget()
      }
    })
  } else {
    ElMessage.error('获取商品数据失败')
    submitForm.activity!.extra!.setMealProducts = []
    let time = setTimeout(() => {
      $router.back()
      clearTimeout(time)
    }, 2000)
  }
}
/**
 * 整理提交表单格式
 */
//店铺下单配置数据
let shopid = ref('')
const remark = ref<any>([])
const shopDealSetting = ref<ApishopDealSetting>({})
let shopRemark = ref<{ [key: string]: { [key: string]: string }[] }>({})
let remarkObj = ref<{ [key: string]: string }>({})
async function integrationData(shopProducsList: StoragePackage[]) {
  submitForm.shopPackages = shopProducsList.map((shopProducs) => {
    const { shopId, shopName, shopLogo, products } = shopProducs
    for (let i = 0; i < remark.value.length; i++) {
      if (Object.values(remark.value[i])?.[0] === '') continue
      remarkObj.value = { ...remarkObj.value, ...remark.value[i] }
    }
    shopid.value = shopId
    return {
      id: shopId,
      name: shopName,
      logo: shopLogo,
      remark: remarkObj.value || {},
      products: products.map(({ id, skuId, num, productFeaturesValue }) => ({ id, skuId, num, productFeaturesValue })),
    }
  })
  const ids = submitForm.shopPackages.map((item) => item.id)
  const res = await doGetOrderSettingsDealBatch(ids)
  shopDealSetting.value = res.data
  for (const key in res.data) {
    shopRemark.value[key] = res.data[key].map((item: any) => ({ [item.key]: '' }))
  }
}
let codeIntraCity = ref<number>()
/**
 * 获取运费
 */
async function initFreightPrice(shopProducsList: StoragePackage[]) {
  const res = initCommodityLogisticsInfo(shopProducsList, submitForm.receiver.area, addRessInfoCn.value)
  const { data, code, msg } = await doGetFreightCalculation({
    ...res,
    area: submitForm.receiver.area,
    distributionMode: [submitForm.distributionMode],
    location: submitForm.receiver.location,
  })
  if (code !== 200) return ElMessage.error(msg || '运费获取失败')
  freightInfo.value = data[submitForm.distributionMode]
  if (submitForm.distributionMode !== 'INTRA_CITY_DISTRIBUTION') return
}
/**
 * 组装秒杀数据
 */
function assemblySecKillData(storageCommodity: any[]) {
  if (storageCommodity[0].activityId) {
    submitForm.secKillCode = storageCommodity[0].secKillCode
  }
}
const checkOrderCreation = (orderNo: any): Promise<void> => {
  return new Promise((resolve, reject) => {
    loopCheckOrderCreation(1, orderNo, resolve, reject)
  })
}
const loopCheckOrderCreation = (count: number, orderNo: any, resolve = () => {}, reject = () => {}) => {
  if (count >= 20) {
    isLoading.value = false
    reject()
    return
  }
  if (orderNo?.orderNo) {
    doGetOrderCreateConditions(orderNo.orderNo).then(({ code, data }) => {
      if (code !== 200 || !data) {
        setTimeout(() => {
          loopCheckOrderCreation(count + 1, orderNo?.orderNo, resolve, reject)
        }, 550)
        return
      }
      isLoading.value = false
      resolve()
    })
  } else {
    doGetOrderCreateConditions(orderNo).then(({ code, data }) => {
      if (code !== 200 || !data) {
        setTimeout(() => {
          loopCheckOrderCreation(count + 1, orderNo, resolve, reject)
        }, 550)
        return
      }
      isLoading.value = false
      resolve()
    })
  }
}
// 提交表单按钮触发
const handleSubmit = async () => {
  if (!submitForm.receiver?.area?.length) {
    return ElMessage.error('请先选择地址')
  }

  if (deliveryOption.value === 'SHOP_STORE') {
    if (!PlaceItemId.value) return ElMessage.error('请选择门店')
    if (!pointTime.value) return ElMessage.error('请选择提货时间')
  }
  sortCommodityInfo(true)
  if (submitForm.distributionMode === 'VIRTUAL') submitForm.activity = activity.value
  const cloneSubmitForm = cloneDeep(submitForm)
  if (groupBuying.value) {
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
  }
  const { data, code, msg } = await doGenerateOrders({ ...cloneSubmitForm })
  switch (code) {
    case 200:
      isLoading.value = true
      // 清除购物车id号
      try {
        await $cardInfo.INIT_CARD()
      } finally {
        checkOrderCreation(data).then(() => {
          $router.replace({
            path: '/pay',
            query: { no: data.orderNo },
          })
          isLoading.value = false
        })
      }
      break
    case 31009:
      //该商品购买数量已超过限购数量
      errGoodsId.value = errorOrderProcessing(commodityList.value, data, msg, ' 已超过限购数量')
      break
    case 50001:
      // 不在包邮范围内
      errGoodsId.value = errorOrderProcessing(commodityList.value, data, msg, ' 不在包邮范围内')
      break
    case 31008:
      // 下单表单校验失败
      errGoodsId.value = errorOrderProcessing(commodityList.value, data, msg, ' 表单校验失败')
      break
    // case 31004:
    case 30007:
      // 超出库存限制
      errGoodsId.value = errorOrderProcessing(commodityList.value, data, msg, ' 商品库存不足')
      $router.back()
      break
    case 30008:
      // 已超出限购次数
      ElMessage.error(msg || '已超出限购次数')
      $router.back()
      break
    default:
      ElMessage.error(msg || '订单提交失败')
      break
  }
}
const addressItem = ref('')
const handleChooseAddress = (address: AddressItemType) => {
  submitForm.receiver = {
    ...address,
  }
  addressItem.value = address.address
  // 取到地址之后获取当前运费
  initFreightPrice(commodityList.value)
  handleBudget()
}
const gotoshoppingcart = () => {
  $router.replace({
    path: '/shoppingcart',
    query: {},
  })
}

const cusFormChange = (e: object) => {
  remark.value = e
}
const loading = ref(true)
const pageConfig = reactive({ current: 1, size: 10, pages: 1 })
async function getCouponList() {
  loading.value = true
  const productAmounts = ref([{ productId: '0', amount: '0' }])
  productAmounts.value[0].amount = costCalculate.value?.totalAmount
  const { code, data } = await doGetOrderShopCouponPage({
    size: pageConfig.size,
    current: pageConfig.current,
    productAmounts: productAmounts.value,
    // shopId: shopid.value,
    shopId: '0',
  })
  if (code !== 200) {
    loading.value = false
    return []
  }
  loading.value = false
  pageConfig.pages = data.pages
  return data.records
}
// 优惠券
const couponList = ref<ApiOrderCouponVO[]>([])
const openCouponsFn = async (visible: boolean) => {
  if (visible) {
    couponList.value = await getCouponList()
  }
}
// 选择优惠券拿id
const moneys = ref(0)
const platformDiscounts = ref()
const radioCouponUserFn = (item: any, money: number, shopId = '0') => {
  if (shopId === '0') moneys.value = money
  if (!submitForm.discounts?.COUPON) {
    submitForm.discounts = { COUPON: {} }
  }
  if (item?.couponUserId) {
    submitForm.discounts.COUPON[shopId] = item.couponUserId
  } else {
    delete submitForm.discounts.COUPON[shopId]
  }
}

const changeCouponCallback = (checkedCoupon: ApiOrderCouponVO, shopId: string) => {
  if (!orderBenefit.value.coupon || !checkedCoupon?.id) {
    if (shopId) {
      delDiscount(shopId, shopId === '0' ? 'addon-platform-coupon' : 'addon-coupon')
    }
    discountSet.value = activeResume()
    if (shopId) {
      allCoupons.value.delete(shopId)
    }
    nextTick(() => {
      handleBudget()
    })
    return
  }
  if (shopId !== '0') {
    const choosedProduct = commodityList.value.find((item) => item.shopId === shopId)?.products || []
    const tempArr = choosedProduct.map((item) => {
      const tempObj = {
        shopId,
        productId: item.productId,
        productPrice: item.salePrice,
        quantity: item.num,
      }
      return tempObj
    })
    if (shopId) {
      addDiscount(shopId, {
        name: 'addon-coupon',
        productInfoArr: tempArr as any,
        rule: checkedCoupon,
        priority: 1,
      })
    }
  } else {
    const allProducts = commodityList.value.map((item) => item.products).flat(1)
    const tempArr = allProducts.map((item) => {
      const tempObj = {
        shopId,
        productId: item.productId,
        productPrice: item.salePrice,
        quantity: item.num,
      }
      return tempObj
    })
    addDiscount('0', {
      name: 'addon-platform-coupon',
      productInfoArr: tempArr as any,
      rule: checkedCoupon,
      priority: 1,
    })
  }
  discountSet.value = activeResume()
  if (checkedCoupon?.id) {
    allCoupons.value.set(shopId, checkedCoupon)
    nextTick(() => {
      handleBudget()
    })
  } else {
    allCoupons.value.delete(shopId)
  }
}

const getShopCouponList = async (item: OrderType) => {
  const { code, data } = await doGetOrderShopCouponPage({
    size: 999,
    current: 1,
    shopId: item.shopId,
    productAmounts: item.products?.map((item) => ({
      productId: item?.productId,
      amount: item?.num * item?.salePrice,
    })),
  })
  if (code !== 200) return
  item.couponOrderList = [...(data.records || [])]
}

const handleChangeCoupon = (couponInfo = {} as ApiOrderCouponVO, index: number) => {
  commodityList.value[index].checkedItemCoupon = couponInfo
  platformDiscounts.value = couponInfo
}

// 确认优惠券
const handleConfirmCheckCoupon = (item: OrderType) => {
  changeCouponCallback(item.checkedItemCoupon, item!.shopId)
  radioCouponUserFn(item.checkedItemCoupon, +item.checkedItemCoupon!.discountAmount / 10000, item?.shopId)
}

const locationData = reactive({
  latitude: 0,
  longitude: 0,
  address: '',
})
// 门店提货点获取
const storeDistanceList = async (shopId: string) => {
  if (navigator.geolocation) {
    // 获取位置的代码
    navigator.geolocation.getCurrentPosition(showPosition, showError)
  } else {
    // 浏览器不支持 geolocation API
    return ElMessage.error('浏览器不支持获取当前位置')
  }
}

// 定位是否成功
const localizationSuccessful = ref(false)

watch(
  () => locationData.latitude,
  async () => {
    if (locationData.latitude !== 0 || locationData.longitude !== 0) {
      localizationSuccessful.value = true
      PickUpPlaceFn()
    }
  },
)

onMounted(() => {
  if (!localizationSuccessful.value && deliveryOption.value === 'SHOP_STORE') {
    PickUpPlaceFn()
  }
})

// 获取位置   拿到经纬度
function showPosition(position: any) {
  locationData.latitude = position.coords.latitude
  locationData.longitude = position.coords.longitude
}
function showError(error: any) {
  switch (error.code) {
    case error.PERMISSION_DENIED:
      ElMessage.error('用户拒绝请求地理定位')
      break
    case error.POSITION_UNAVAILABLE:
      ElMessage.error('位置信息不可用')
      break
    case error.TIMEOUT:
      ElMessage.error('请求获取用户位置超时')
      break
    case error.UNKNOWN_ERROR:
      ElMessage.error('发生未知错误')
      break
    default:
      break
  }
}
const PickUpPlaceList = ref<{ detailedAddress: string; distance: number; functionaryPhone: string; id: string; storeName: string } | any>({
  detailedAddress: '',
  distance: 0,
  functionaryPhone: '',
  id: '',
  storeName: '',
})

const PickUpPlaceFn = async () => {
  let shopId = shopIdStore.value
  const params = {
    shopId,
    point: { coordinates: locationData.longitude && locationData.latitude ? [locationData.longitude, locationData.latitude] : [], type: 'Point' },
  }
  const { code, data, msg } = await doPostStoreDistanceList(params)
  if (code === 200) PickUpPlaceList.value = data
  else {
    ElMessage.error(msg || '获取自提点失败')
  }
}

const storeDistance = ref(0)
const deliveryTime = ref<{
  businessEndTime: string
  businessStartTime: string
  endDeliveryDay: number
  startDeliveryDay: number
}>()
const handleConfirmPickUpPlace = async (key: string) => {
  storeDistance.value = PickUpPlaceList.value.find((item: any) => item.id === key)?.distance
  if (!PlaceItemId.value) return ElMessage.error('请选择自提点')
  const { code, data, msg } = await doGetDeliveryTime(shopIdStore.value, PlaceItemId.value)
  if (code === 200) deliveryTime.value = data
  else ElMessage.error(msg)
}
// 提货时间
const pointTime = ref('')
// 日期
const pointDate = computed(() => {
  if (!deliveryTime.value) return []
  let dates = []
  let currentDate = new Date()
  currentDate.setUTCDate(currentDate.getUTCDate() + deliveryTime.value.startDeliveryDay)
  let index = 0
  while (index < deliveryTime.value.endDeliveryDay - deliveryTime.value.startDeliveryDay + 1) {
    dates.push(currentDate.toISOString().slice(0, 10))
    currentDate.setUTCDate(currentDate.getUTCDate() + 1)
    index++
  }
  return dates
})
const pointTimeArr = computed(() => {
  if (deliveryTime.value?.businessStartTime) {
    const timeArr = []
    const { businessStartTime, businessEndTime } = deliveryTime.value
    const e = businessEndTime.substring(2)
    const s = businessStartTime.substring(2)
    const start = Number(businessStartTime.substring(0, 2))
    const end = Number(businessEndTime.substring(0, 2))
    const between = end + 1 - start
    for (let index = 0; index < between; index++) {
      const startTime = start + index
      const endTime = start + index
      const newStartTime = startTime >= 10 ? startTime : '0' + startTime
      const newEndTime = endTime >= 10 ? endTime : '0' + endTime
      timeArr.push({ time: `${newStartTime}${s} - ${newEndTime}${e}`, type: false, endTime: endTime })
    }
    return timeArr
  }
  return []
})

const PlaceItemId = ref('')
const PickUpPlaceItemFn = (id: string) => {
  PlaceItemId.value = id
}
// 选择提货时间
const showPickupTime = ref(false)
const pointTimeFn = () => {
  showPickupTime.value = true
}
const dArrActiveIndex = ref(0)
const timeArrActiveIndex = ref(0)
const timeScrollTop = ref(0)
const handleDateClick = (idx: number) => {
  dArrActiveIndex.value = idx
  timeScrollTop.value = timeScrollTop.value ? 0 : 1
  pointTimeArr.value.forEach((item) => {
    item.type = false
  })
  pointTime.value = ''
}
const handleTimeClick = (idx: number, type: boolean) => {
  if (!type) {
    pointTimeArr.value.forEach((item, index) => {
      if (index !== idx) {
        item.type = false
      }
    })
    timeArrActiveIndex.value = idx
    const mm_dd = pointDate.value[dArrActiveIndex.value]
    const time = pointTimeArr.value[timeArrActiveIndex.value].time
    const params = mm_dd + ' ' + time
    pointTime.value = params
    showPickupTime.value = false
  } else {
    pointTime.value = ''
    showPickupTime.value = false
  }
}
watch(
  () => timeArrActiveIndex.value,
  () => {
    if (timeArrActiveIndex.value) {
      showPickupTime.value = false
    }
  },
)

/**
 * 重置表单
 */
const reset = async () => {
  addressinfo.value = {
    id: '',
    address: '',
    area: [] as unknown as [string, string, string?],
    isDefault: false,
    mobile: '',
    name: '',
    fakeAddress: '',
    location: {
      type: GeometryType.Point,
      coordinates: [] as unknown as [number, number],
    },
    showSet: false,
  }
  formRef.value.clearValidate()
}

const toChooseAddress = () => {
  showChooseMap.value = true
}
const handleChooseRes = async (res: Checked) => {
  if (!res.longitude) return
  if (!res.name) return
  if (!res.address) return
  const checkedLocation: Geometry = {
    type: GeometryType.Point,
    coordinates: [Number(res.longitude), Number(res.latitude)],
  }
  addressinfo.value.location = checkedLocation

  const { area, address } = await doGetAddressByLocation(checkedLocation.coordinates, false)
  if (area) {
    addressinfo.value.area = area
  }
  if (address) {
    addressinfo.value.address = `${address}~${res.name}`
  }
}
const placeChoosed = (place: Checked) => {
  handleChooseRes(place)
}

const selectedCoupon = ref()
</script>

<template>
  <div>
    <div bg-white>
      <div c-w-1200 c-h-110 flex ma justify-between items-center>
        <router-link to="/home">
          <div
            class="log"
            :style="{
              backgroundImage: 'url(' + topComponents?.topComponents?.[1]?.formData?.logo + ')',
            }"
          />
        </router-link>
        <el-steps c-w-500 :active="2" align-center>
          <el-step title="我的购物车" :icon="SuccessFilled" />
          <el-step title="核对订单信息" :icon="SuccessFilled" />
          <el-step title="订单提交成功" />
        </el-steps>
      </div>
    </div>
    <div c-w-1200 ma bg-white c-mt-17 style="padding: 24px 24px 32px 24px">
      <div text-center c-fs-18 c-pb-24 c-c-3 fw-700>确认订单</div>
      <div style="padding: 0 24px">
        <!-- 快递配送 -->
        <div text-left c-pb-10>
          <div c-fs-16 e-c-3 c-pt-14 fw-700>收货信息</div>
          <!-- 横线 -->
          <div c-h-1 c-w-1150 c-mt-14 style="position: relative; left: -24px; background-color: rgb(235, 234, 230)" />
          <div c-fs-14 e-c-3 c-mt-24 c-ml-17>
            配送方式&emsp;{{
              deliveryOption === 'EXPRESS'
                ? '快递配送'
                : deliveryOption === 'INTRA_CITY_DISTRIBUTION'
                ? '同城配送'
                : deliveryOption === 'SHOP_STORE'
                ? '到店自提'
                : deliveryOption === 'VIRTUAL' || deliveryOption === 'WITHOUT'
                ? '无需配送'
                : ''
            }}
          </div>
          <div c-fs-14 e-c-3 c-mt-20 flex items-center c-ml-17>
            <span v-if="deliveryOption === 'EXPRESS' || deliveryOption === 'INTRA_CITY_DISTRIBUTION'">收货地址&emsp;</span>
            <span v-if="deliveryOption === 'SHOP_STORE'">自提门店：</span>
            <!-- 快递配送 -->
            <el-select
              v-if="deliveryOption === 'EXPRESS' || deliveryOption === 'INTRA_CITY_DISTRIBUTION'"
              v-model="submitForm.receiver.id"
              placeholder="请选择"
              style="width: 920px"
            >
              <!-- 自定义选中后显示的内容 -->
              <el-option
                v-for="item in addressList"
                :key="item.id"
                :value="item.id"
                :label="
                  (item.isDefault ? '(默认)' : '') +
                  item.name +
                  ' ' +
                  item.mobile +
                  ' ' +
                  item.area?.join('') +
                  ' ' +
                  (item.address.split('~')[0] ? item.address.split('~')[0] : '') +
                  ' ' +
                  (item.address.split('~')[1] ? item.address.split('~')[1] : '') +
                  ' ' +
                  (item.address.split('~')[2] ? item.address.split('~')[2] : '')
                "
              >
                <template #default>
                  <div
                    flex
                    items-center
                    justify-between
                    relative
                    @mouseleave="item.showSet = false"
                    @mouseenter="item.showSet = true"
                    @click="handleChooseAddress(item)"
                  >
                    <div>
                      <span v-if="item.isDefault">(默认)</span>
                      <span>{{ item.name }},{{ item.mobile }},</span>
                      <span style="overflow: hidden; text-overflow: ellipsis; white-space: nowrap">
                        {{ item.area?.join('') }}
                        {{ item.address.split('~')[0] }}{{ item.address.split('~')[1] }} {{ item.address.split('~')[2] }}
                      </span>
                    </div>
                    <!-- <div class="cursor-pointer" e-c-6 style="position: absolute; right: 80px" @click.stop="handlePutAddress(item)">修改</div>
                    <div class="cursor-pointer" e-c-6 style="position: absolute; right: 40px" @click.stop="handleDelAddress(item.id)">删除</div>
                    <div
                      v-if="item.isDefault"
                      style="
                        color: #fff;
                        line-height: 18px;
                        padding: 0 3px;
                        background-color: #f07979;
                        font-size: 10px;
                        position: absolute;
                        right: -15px;
                      "
                    >
                      默认
                    </div>
                    <div
                      v-if="!item.isDefault && item.showSet"
                      style="
                        color: #fff;
                        line-height: 18px;
                        padding: 0 3px;
                        background-color: #f07979;
                        font-size: 10px;
                        position: absolute;
                        right: -15px;
                        z-index: 10;
                      "
                      @click.stop="handleChangeCheck(item.id)"
                    >
                      默认地址
                    </div> -->
                  </div>
                </template>
              </el-option>
            </el-select>

            <!-- 到店自提 -->
            <template v-if="deliveryOption === 'SHOP_STORE'">
              <div flex items-center>
                <el-select
                  v-if="PickUpPlaceList.length > 0"
                  v-model="PlaceItemId"
                  placeholder="请选择"
                  style="width: 920px"
                  @change="handleConfirmPickUpPlace"
                >
                  <el-option
                    v-for="item in PickUpPlaceList"
                    :key="item.id"
                    :value="item.id"
                    :label="item?.storeName + ' ' + item?.functionaryPhone + ' ' + item?.detailedAddress"
                    @click="PickUpPlaceItemFn(item.id)"
                  >
                    <template #default>
                      <div flex items-center relative>
                        <p c-mr-10>{{ item?.storeName }}</p>
                        <p c-mr-10>{{ item?.functionaryPhone }}</p>
                        <p>{{ item?.detailedAddress }}</p>
                        <div v-if="item?.distance?.toFixed(2)" absolute right-0>
                          距您：<span style="color: #f54319">{{ item?.distance?.toFixed(2) }} km</span>
                        </div>
                      </div>
                    </template>
                  </el-option>
                </el-select>
                <div v-if="storeDistance?.toFixed(2) && storeDistance" c-ml-10>
                  距您：<span style="color: #f54319">{{ storeDistance?.toFixed(2) }}</span> km
                </div>
              </div>
            </template>

            <!-- 新增收货地址 -->
            <div
              v-if="deliveryOption === 'EXPRESS' || deliveryOption === 'INTRA_CITY_DISTRIBUTION'"
              c-fs-14
              c-c-0066FF
              cursor-pointer
              c-ml-17
              style="border-color: #ccc; padding: 5px 5px"
              @click="handleAddAddress"
            >
              新增
            </div>
          </div>
          <div v-if="deliveryOption === 'SHOP_STORE' && deliveryTime" c-fs-14 e-c-0 c-mt-15 flex items-center c-ml-17>
            <span>提货时间：</span>
            <span cursor-pointer c-c-3176F1 @click="pointTimeFn">
              {{ pointTime || `请选择提货时间` }}<QIcon v-if="!pointTime" name="icon-xiajiantou" />
            </span>
          </div>
        </div>

        <!-- 商品信息 -->
        <div text-left e-c-3 c-mt-34 c-fs-16 fw-700>商品信息</div>
        <div c-h-1 c-w-1150 c-mt-14 style="position: relative; left: -24px; background-color: rgb(235, 234, 230)" />
        <div v-for="(item, index) in commodityList" :key="item.shopName" c-mt-10 c-fs-14 c-mb-10>
          <!-- 店铺信息 -->
          <div c-lh-40 text-left e-c-3 flex items-center justify-between>
            <div flex items-center c-w-400 c-ml-24>
              <QIcon name="icon-dianpu5" style="font-size: 18px" color="#999" />
              <template v-if="item.shopType">
                <span v-if="item.shopType === 'SELF_OWNED'" c-ml-5 c-c-f54319 c-fs-12 c-lh-16 c-bg-fb232f class="shop_tag">自营</span>
                <span v-if="item.shopType === 'PREFERRED'" c-ml-5 c-c-00bb2c c-fs-12 c-lh-16 c-bg-8239f6 class="shop_tag">优选</span>
              </template>
              <span c-ml-5 c-fs-14>
                <text>{{ item.shopName }}</text>
              </span>
            </div>
            <div style="flex: 1; display: flex; justify-content: end">
              <div v-if="budget.shopFull[item.shopId]" c-mr-20 c-fs-12>
                <span>满减优惠 </span>
                <span c-c-fd0505>-{{ divTenThousand(budget.shopFull[item.shopId]).toFixed(2) }}</span>
              </div>
              <div c-mr-20 c-fs-12>
                <span>运费 </span>
                <span v-if="budget.shopFreight[item.shopId] > 0" c-c-fd0505>{{
                  budget.shopFreight && divTenThousand(budget.shopFreight[item.shopId])
                }}</span>
                <text v-else c-c-fd0505>包邮</text>
              </div>
              <div c-fs-12>
                <span>店铺优惠 </span>
                <el-select
                  v-model="item.showShopCouponList"
                  placeholder="请选择优惠券"
                  popper-class="coupon-select"
                  style="width: 150px"
                  @visible-change="getShopCouponList(item)"
                >
                  <el-option
                    key="no-coupon"
                    :value="0"
                    label="不使用优惠券"
                    @click="()=>
                     { handleChangeCoupon({} as ApiOrderCouponVO, index)
                      handleConfirmCheckCoupon(item)}
                    "
                  >
                    <div class="coupon-option">
                      <div class="no-coupon">不使用优惠券</div>
                    </div>
                  </el-option>
                  <el-option
                    v-for="coupon in item.couponOrderList"
                    :key="coupon.couponUserId"
                    :value="coupon.couponUserId"
                    :label="'-' + divTenThousand(coupon.discountAmount) + '元'"
                    @click="
                      () => {
                        handleChangeCoupon(coupon, index)
                        handleConfirmCheckCoupon(item)
                      }
                    "
                  >
                    <div class="coupon-option">
                      <span>{{ formattingCouponRules(coupon, true) }}</span>
                      <span
                        v-if="coupon.type === 'PRICE_REDUCE' || coupon.type === 'REQUIRED_PRICE_REDUCE'"
                        class="shop-coupon__line--discount"
                        c-ml-20
                        >-{{ coupon.discountAmount ? divTenThousand(coupon.discountAmount) : '' }}</span
                      >
                      <span v-else class="shop-coupon__line--discount" c-ml-20>{{ coupon.discount }}折</span>
                    </div>
                  </el-option>
                </el-select>
              </div>
            </div>
          </div>

          <div flex e-c-6 c-bg-f7f7f7 c-h-42 c-lh-40>
            <div c-w-684 text-center c-ml-17>商品</div>
            <div text-left c-w-140 c-pl-16>单价（元）</div>
            <div text-left c-w-140 c-pl-16>数量</div>
            <div text-left c-w-140 c-pl-16>金额（元）</div>
          </div>

          <div>
            <div
              v-for="(sku, key) in item.products"
              :key="sku.id"
              c-h-86
              flex
              items-center
              :style="{ borderBottom: key === item.products.length - 1 ? 'none' : '1px dashed #ebeae6' }"
            >
              <div c-w-684 flex items-center c-pl-17>
                <img :src="sku.image" c-h-60 c-w-60 />
                <div text-left c-ml-24>
                  <div e-c-3 c-mb-8 c-w-536 truncate>
                    {{ sku.productName }}
                  </div>
                  <div e-c-9 c-w-536 truncate>
                    {{ sku.specs?.join(';') }}
                  </div>
                </div>
              </div>
              <div c-w-140 c-pl-16 text-left>
                <div e-c-3 ma>￥{{ useConversionPrice(sku.salePrice) }}</div>
                <div v-if="false" c-w-60 c-h-21 c-lh-21 c-c-e31436 b-1 c-bc-E31436 c-bg-fdecef ma>秒杀价</div>
              </div>
              <div e-c-3 c-w-140 c-pl-16 text-left ma>
                {{ sku.num }}
              </div>
              <div c-w-140 c-pl-16 text-left e-c-3>￥{{ (sku.num * Number(useConversionPrice(sku.salePrice))).toFixed(2) }}</div>
            </div>
            <div style="padding: 0px 20px 0">
              <cus-form
                c-mt-10
                :shop-deal-setting="shopDealSetting"
                :shop-remark="shopRemark"
                :idx="index"
                :shop-id="item.shopId"
                @update:model-value="cusFormChange"
              />
            </div>
          </div>
        </div>
        <div v-if="false" flex justify-end items-center c-fs-12 e-c-3 c-mr-20>
          <el-checkbox v-model="checked1" size="large" c-pr-5 />
          部分商品在特定条件下不支持7天无理由退货，确认购买
        </div>
        <div text-left e-c-3 c-mt-34 c-mb-14 fw-700>付款详情</div>
        <div c-h-1 c-w-1150 c-mt-14 style="position: relative; left: -24px; background-color: rgb(235, 234, 230)" />
        <div c-bc-ebeae6 c-fs-12 c-pl-20 c-pt-13 c-pb-23>
          <!-- 折扣展示 -->
          <div c-mt-10>
            <div flex justify-end items-center c-mb-20 c-fs-14>
              <!-- <img src="@/assets/images/icon/platform_discounts.png" alt="" c-h-16 c-w-16 c-mr-5 /> -->
              <span e-c-3 c-fs-12 c-mr-5>平台优惠</span>
              <el-select
                v-model="selectedCoupon"
                placeholder="请选择优惠券"
                popper-class="coupon-select"
                style="width: 150px"
                @visible-change="openCouponsFn"
              >
                <el-option key="no-coupon" :value="0" label="不使用优惠券" @click="radioCouponUserFn({}, 0)">
                  <div class="coupon-option">
                    <div class="no-coupon">不使用优惠券</div>
                  </div>
                </el-option>
                <el-option
                  v-for="item in couponList"
                  :key="item.couponUserId"
                  :value="item.couponUserId"
                  :label="'-' + divTenThousand(item.discountAmount) + '元'"
                  @click="radioCouponUserFn(item, +divTenThousand(item.discountAmount))"
                >
                  <div class="coupon-option">
                    <span>{{ formattingCouponRules(item, true) }}</span>
                    <span v-if="item.type === 'PRICE_REDUCE' || item.type === 'REQUIRED_PRICE_REDUCE'" class="shop-coupon__line--discount" c-ml-20
                      >-{{ item.discountAmount ? divTenThousand(item.discountAmount) : '' }}</span
                    >
                    <span v-else class="shop-coupon__line--discount" c-ml-20>{{ item.discount }}折</span>
                  </div>
                </el-option>
              </el-select>
            </div>
            <div flex justify-end c-mb-7 c-fs-14>
              <div e-c-9>商品数：</div>
              <span c-w-96 flex items-center justify-end>
                <span c-c-F54319>
                  {{
                    commodityList.reduce((pri, item) => {
                      return pri + item.products.length
                    }, 0)
                  }}
                </span>
                <span c-c-999 c-ml-3>件</span>
              </span>
            </div>
            <div flex justify-end c-mb-7 c-fs-14>
              <div e-c-9><span></span>商品总价：</div>
              <div c-w-96 flex justify-end e-c-3 items-center>
                <span c-fs-12>￥</span><text c-fs-16>{{ divTenThousand(budget.total).toFixed(2) }}</text>
              </div>
            </div>
            <div flex justify-end c-mb-12 c-fs-14>
              <div e-c-9>运费：</div>
              <div v-if="budget.freight" c-w-96 flex justify-end e-c-3>{{ divTenThousand(budget.freight).toFixed(2) }}</div>
              <text v-else c-w-96 flex justify-end e-c-3> 包邮 </text>
            </div>
            <div flex justify-end c-mb-12 c-fs-14>
              <div e-c-9>店铺优惠：</div>
              <div c-w-96 flex justify-end e-c-3><span>-</span>{{ divTenThousand(budget.shopDiscount).toFixed(2) }}</div>
            </div>
            <div flex justify-end c-mb-12 c-fs-14>
              <div e-c-9>平台优惠：</div>
              <div c-w-96 flex justify-end e-c-3><span>-</span> {{ moneys.toFixed(2) }}</div>
            </div>
            <div flex justify-end c-mb-12 c-fs-14>
              <div e-c-9>会员折扣：</div>
              <div c-w-96 flex justify-end e-c-3><span>-</span> {{ divTenThousand(budget.memberDiscount).toFixed(2) }}</div>
            </div>
            <div flex justify-end c-mb-12 c-fs-14>
              <div e-c-9>总优惠：</div>
              <div c-w-96 flex justify-end e-c-3>
                <span>-</span>{{ (budget.memberDiscount / 10000 + moneys + budget.shopDiscount / 10000).toFixed(2) }}
              </div>
            </div>
          </div>
        </div>
        <div c-pt-24>
          <div c-lh-56 c-bg-f5f5f5 text-right c-pr-16>
            <div e-c-3 flex items-center style="justify-content: space-between">
              <span c-ml-16> 应付金额</span>
              <div c-c-e31436 fw-700 c-fs-16>
                ￥<text c-fs-30>{{
                  +useConversionPrice(budget.payAmount).minus(moneys) < 0
                    ? '0.00'
                    : (+useConversionPrice(budget.payAmount).minus(moneys)).toFixed(2).split('.')[0]
                }}</text
                ><text>.</text
                ><text c-fs-16>{{
                  +useConversionPrice(budget.payAmount).minus(moneys) < 0
                    ? '0.00'
                    : (+useConversionPrice(budget.payAmount).minus(moneys)).toFixed(2).split('.')[1]
                }}</text>
              </div>
            </div>
          </div>

          <div flex justify-end items-center c-pt-24 c-pb-24>
            <div v-if="submitForm.source === 'CART'" c-fs-14 c-c-0066FF c-mr-16 cursor-pointer @click="gotoshoppingcart">返回购物车</div>
            <el-button
              c-w-112
              c-h-36
              c-lh-36
              c-bg-FF0000
              e-c-f
              c-fs-16
              fw-500
              cursor-pointer
              c-br-2
              :disabled="codeIntraCity === 60001 || codeIntraCity === 60002"
              type="danger"
              @click="handleSubmit"
            >
              提交订单
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
  <el-dialog v-model="addressFlag" :title="addressinfo.id ? '编辑收货地址' : '新增收货地址'" width="500" @close="reset">
    <el-form ref="formRef" :model="addressinfo" label-width="80px" :rules="rules">
      <el-form-item label="收货人" prop="name">
        <el-input v-model="addressinfo.name" placeholder="请输入真实姓名" />
      </el-form-item>
      <el-form-item label="手机号" prop="mobile">
        <el-input v-model="addressinfo.mobile" placeholder="请输入正确的11位手机号码" :maxlength="11" />
      </el-form-item>
      <el-form-item label="定位地址" prop="area" class="address_form_item">
        <div class="fcenter cup" @click="toChooseAddress">
          <div class="address_area">
            <div v-if="addressinfo.address" class="address_name">{{ addressinfo.address.split('~')[1] || '' }}</div>
            <div v-else style="color: #f54319">打开地图选择定位</div>
            <div v-if="addressinfo.area" class="address_detail_name">{{ addressinfo.area?.join?.('') }}{{ addressinfo.address.split('~')[0] }}</div>
          </div>
          <el-icon style="color: #f54319" class="mla"><ArrowRight /></el-icon>
        </div>
      </el-form-item>
      <el-form-item label="详细地址" prop="fakeAddress">
        <el-input v-model="addressinfo.fakeAddress" :rows="5" type="textarea" placeholder="例如: [3单元203室]" :maxlength="20" />
      </el-form-item>
      <el-form-item label="" prop="isDefault">
        <el-checkbox v-model="addressinfo.isDefault" label="设为默认收货地址" size="large" />
      </el-form-item>
    </el-form>
    <template #footer>
      <div>
        <el-button type="primary" @click="() => (addressinfo.id ? editAddress() : newAddress())">保存并使用</el-button>
      </div>
    </template>
  </el-dialog>
  <AmapChooseDialog v-model="showChooseMap" :initLocation="addressinfo.location.coordinates" @placeChoose="placeChoosed"></AmapChooseDialog>
  <el-dialog v-model="showPickupTime" :width="550" title="选择提货时间" destroy-on-close>
    <template #header>
      <div align="left">
        <span>选择提货时间</span>
      </div>
    </template>
    <div class="showPickupTime">
      <div class="showPickupTime__left">
        <p
          v-for="(item, index) in pointDate"
          :key="index"
          class="showPickupTime__left--hou"
          :class="dArrActiveIndex === index ? 'bgc' : ''"
          @click="handleDateClick(index)"
        >
          {{ item }}
        </p>
      </div>
      <div class="showPickupTime__right">
        <div v-for="(item, index) in pointTimeArr" :key="index">
          <div
            v-if="dArrActiveIndex !== 0 || new Date().getHours() < item.endTime + 1"
            class="showPickupTime__right--time"
            :class="{ timeArrActive: timeArrActiveIndex === index }"
            @click="handleTimeClick(index, item.type)"
          >
            {{ item?.time }}
          </div>
        </div>
      </div>
    </div>
  </el-dialog>
</template>
<style lang="scss" scoped>
.address_form_item {
  .fcenter {
    width: 100%;
    .address_area {
      padding: 1px 11px;
      display: flex;
      flex-direction: column;
      width: calc(100% - 80px);
      text-align: left;
      line-height: 18px;
      .address_name {
        font-size: 16px;
        font-weight: bold;
      }
      .address_detail_name {
        color: #a8abb2;
      }
    }
  }
}
@include b(border) {
  border: 1px solid #e31436 !important;
}
@include b(bgc) {
  background-color: #fff;
  border: 1px solid #f5f5f5;
}
@include b(showPickupTime) {
  height: 300px;
  overflow: auto;
  display: flex;
  @include e(left) {
    width: 150px;
    background-color: #f5f5f5;
    overflow: auto;
    @include m(hou) {
      padding: 15px 10px;
    }
  }
  @include e(right) {
    overflow: auto;
    width: 100%;
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    @include m(time) {
      display: inline-block;
      text-align: left;
      margin: 8px 20px;
      padding: 10px 15px;
      border-radius: 5px;
    }
    @include b(timeArrActive) {
      border: 1px solid #e31436;
      background-color: #feece8;
      color: #e31436;
    }
    @include m(ico) {
      transform: translateY(2px);
      margin-left: 50px;
    }
  }
}

@include b(shop-coupon) {
  max-height: 216px;
  overflow-y: scroll;
  @include e(line) {
    @include flex(space-between);
    padding: 3px 15px;
    line-height: 26px;
    user-select: none;
    margin-top: 3px;
    box-sizing: border-box;
    @include m(label) {
      font-weight: 600;
    }
    @include m(discount) {
      color: #f54319;
      font-size: 1.1em;
    }
  }
  @include b(checked) {
    border: 1px solid #fd0505;
    background-color: #fdd8cf;
    color: #fd0505;
  }
}
.log {
  width: 220px;
  height: 88px;
  background-size: contain;
  background-repeat: no-repeat;
  background-position: left center;
}

.shop_tag {
  display: inline-block;
  font-size: 10px;
  zoom: 0.9;
  border-radius: 3px;
  overflow: hidden;
  padding: 1px 4px;
  color: #fff;
}

:deep(.el-icon svg) {
  color: #e31436;
}

:deep(.el-step__title.is-wait) {
  color: #333333;
  font-size: 14px;
}

:deep(.el-step.is-center .el-step__head) {
  height: 31.5px;
}

:deep(.el-step.is-horizontal .el-step__line) {
  height: 1px;
}
</style>
