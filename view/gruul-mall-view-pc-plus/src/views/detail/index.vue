<script setup lang="ts">
import { ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import GoodSpec from '@/components/good-spec/good-spec.vue'
import Storage from '@/libs/storage'
import { serviceHandler } from '@/views/detail/utils/index'
import formatRichText from '@/utils/formatRichText'
import QIcon from '@/components/q-icon/q-icon.vue'
import MagnifyingGlass from '@/components/magnifyingGlass/magnifyingGlass.vue'
import { useConversionPrice } from '@/utils/useConversionPrice'
import { doPostBrowseCommodity, doAddTocar, doGetOrderEvaluateInfo, doGetShopBaseInfo, doGetOrderEvaluateInfoList, doGetProduct } from '@/apis/goods'
import { doGetDefaultAddress } from '@/apis/address'
import { useCardInfo } from '@/store/modules/cart'
import { doPostUserFootprint, doAddAssess, doCancelAssessOrder, doGetAssessOrder } from '@/apis/consumer'
import { doGetGuessYouLike, doGetlookAndSee, doGetshopHotSales, doGetpopularAttention, doGetSetMealBasicInfo } from '@/apis/user'
import type {
  ApiGoodType,
  StorageSku,
  ProductSpecsSkusVO,
  ApiEvaluation,
  EvaluationType,
  Evaluate,
  ReceiverAreaDataParams,
  ProductExtraDTO,
  ProductFeaturesValueDTO,
  ProductInfo,
} from './types'
import type { ApiSeckILLGoodsDetails, ApplyTypesJoint, SetMealNum } from '@/pluginPackage/seckill/components/types/goods-details-secods-kill'
import { groupPurchaseNum } from '@/apis/GroupPurchase'
import PcSeckilldetalInfo from '@/q-plugin/seckill/PcSeckilldetalInfo.vue'
import PcSetMeal from '@/q-plugin/setMeal/PcSetMeal.vue'
import PcCouponGoodDetails from '@/q-plugin/coupon/PcCouponGoodDetails.vue'
import ToTopGoCar from '@/views/toTopGoCar/index.vue'
import { doGetIntegralGoodsInfo } from '@/apis/integral/index'
import { IntegralGoodsInfoType } from '@/views/integralShop/index'
import { useUserStore } from '@/store/modules/user'
import { doGetFreightCalculation } from '@/apis/order'
import { doGetUserIntegralSystemtotal } from '@/apis/integral'
import useMember from '@/composables/useMember'
import { EXPRESS_CODE, INTRA_CITY_DISTRIBUTION_CODE_1, INTRA_CITY_DISTRIBUTION_CODE_2 } from '@/views/detail/freight/model/index'
import { useGDRegionDataStore } from '@/store/modules/GDRegionData'
import { usePropertiesListStore } from '@/store/modules/propertiesList'
import { storeToRefs } from 'pinia'
import { doGetMessagesChatRoom } from '@/apis/consumerSever'
import { handleParams } from '@/utils/goodsUtils'
import { useRouterNewWindow } from '@/utils/useRouter'

const regionData = useGDRegionDataStore().getterGDRegionData
const { openNewWindow } = useRouterNewWindow()
const storage = new Storage()
const $userStore = useUserStore()
const { includeBenefit } = useMember()
document.querySelector('#toTop')?.scrollIntoView()
const { getterPropertiesList } = storeToRefs(usePropertiesListStore())
const $router = useRouter()
const $route = useRoute()
const regionDatas = regionData // 定义地区数据
const swiperList = ref<any[]>([])
const swiperimg = ref('')
const swiperimgIndex = ref(0)
const integralSwiperList = ref<any>([])
const integralSwiperimg = ref('')
const radio = ref('')
const isCollection = ref(false)
const isEnough = ref(false)
const goodInfo = ref<ApiGoodType>()
const shopInfo = ref({
  shopId: '',
  shopLogo: '',
  shopName: '',
  newTips: '',
  status: '',
  shopMode: '',
  shopType: '',
})
const count = ref(1)
// 存储商品规格信息
const skuGroup = ref<ProductSpecsSkusVO>({
  skus: [],
  specGroups: [],
})
const choosedSpec = ref<StorageSku>({
  id: '',
  image: '',
  initSalesVolume: 0,
  limitNum: 0,
  limitType: 'UNLIMITED',
  price: '0',
  productId: '',
  salePrice: '0',
  shopId: '0',
  specs: [''],
  stock: '0',
  stockType: 'UNLIMITED',
  weight: 0,
  salesVolume: '0',
})
const evaluateInfo = ref<ApiEvaluation>({
  contentCount: '',
  evaluate: {
    comment: '',
    createTime: '',
    deleted: false,
    id: '',
    image: '',
    isExcellent: false,
    itemId: '',
    medias: [],
    name: '',
    orderNo: '',
    packageId: '',
    productId: '',
    rate: 5,
    shopId: '',
    skuId: '',
    specs: [],
    updateTime: '',
    userId: '',
    shopReply: '',
    avatar: '',
    nickname: '',
  },
  mediaCount: '',
  praiseCount: '',
  praiseRatio: '',
  totalCount: '',
  rate: 5,
})
const evaluateInfoList = ref<Evaluate[]>([])
const area = ref<string[]>()
const location = ref({
  type: '',
  coordinates: [],
})
const CascaderRef = ref()
const $cardInfo = useCardInfo()
const page = reactive({ current: 1, size: 10, total: 0 })
const tabsIndex = ref(1)

const interestsConfig = reactive<any>({
  // 发货
  delivery: {
    sendTime: '',
    // 预计收货时间
    receiveTime: '',
  },
  // 运费
  // freight: '',
  // 运费配置
  freight: {
    EXPRESS: { price: 0, label: '快递', msg: '免费', errCode: [EXPRESS_CODE], code: 0 },
    // 60001 超出配送范围 60002 不足配送金额 不展示同城运费信息
    INTRA_CITY_DISTRIBUTION: { price: 0, label: '同城', msg: '免费', errCode: [INTRA_CITY_DISTRIBUTION_CODE_1], code: 0 },
    SHOP_STORE: { price: 0, label: '自提', msg: '免费', errCode: [INTRA_CITY_DISTRIBUTION_CODE_1], code: 0 },
    VIRTUAL: { price: 0, label: '', msg: '', errCode: [INTRA_CITY_DISTRIBUTION_CODE_1], code: 0 },
  },
  service: '',
})
const likeData = ref<
  Array<{
    lowestPrice: string
    productAlbumPics: string
    productId: string
    productName: string
    salesVolume: string
    shopId: string
    evaluated: string
  }>
>([])
const lookSeeData = ref<ProductInfo[][]>([])
const shopHotsaleData = ref<ProductInfo[]>([])
const PopularAttentionData = ref<ProductInfo[]>([])
const secKillGoodsInfo = ref<ApiSeckILLGoodsDetails>()
const showVideoControl = ref(false)
const playButton = ref(true)
const myVideo = ref(null)
const setMealNum = ref<SetMealNum[]>([])
watch(
  () => $route.query.productId,
  (val) => {
    document.querySelector('#toTop')?.scrollIntoView()
    if (val) init()
  },
)
init()
const showVideo = () => {
  showVideoControl.value = true
  nextTick(() => {
    playButton.value = false
  })
}
const videoEnd = () => {
  showVideoControl.value = false
  playButton.value = true
}

// 打开评论视频
const videoSrc = ref('')
const commentVideo = ref(null)
const showVideoComment = ref(false)
const showCommentVideo = (videoUrl: string) => {
  showVideoComment.value = true
  videoSrc.value = videoUrl
  playCommentVideo()
}
const playCommentVideo = () => {
  if (commentVideo.value) {
    ;(commentVideo.value as HTMLVideoElement).play()
  }
}
const videoCommentEnd = () => {
  videoSrc.value = ''
  showVideoComment.value = false
}

async function init() {
  if ($route.query.integralId) return
  tabsIndex.value = 1
  await initInfo()
  // initSku()
  await initShopInfo()
  // await initSeckill()
  formatService()
  initFootprint()
  initBrowsingTheRecord()
  initEvaluateInfo()
  initEvaluateInfoList()
  initGuessYouLike()
  initlookAndSee()
  initshopHotSales()
  initpopularAttention()
  getGetSetMealBasicInfo()
}
watch(
  () => choosedSpec.value?.id,
  (val) => {
    // 获取 默认地址 并查询运费
    if (val) initDefaultAddress()
  },
)
const address = ref('')
/**
 * @description: 获取默认收货地址
 * @returns {*}
 */
async function initDefaultAddress() {
  if (!$userStore.getterToken) return
  if (!goodInfo.value) {
    return
  }
  const { data, code } = await doGetDefaultAddress()
  if (code === 200 && data) {
    address.value = data.address
    area.value = data.area
    location.value = data.location
    getFreightCalculation()
    return
  }
  // ElMessage.error('获取地址信息失败')
}

const getGetSetMealBasicInfo = async () => {
  const { code, data } = await doGetSetMealBasicInfo($route.query.shopId as string, $route.query.productId as string)
  if (code === 200) {
    setMealNum.value = data
    tabsIndex.value = setMealNum.value.length > 0 ? 2 : 1
  }
}
/**
 * @description: 货运信息
 * @returns {*}
 */
async function getFreightCalculation() {
  let { weight, price, shopId } = choosedSpec.value
  let skuId = evaluateInfo.value.evaluate?.skuId
  let templateId = goodInfo.value!.freightTemplateId
  let distributionMode = goodInfo.value!.distributionMode
  const params: ReceiverAreaDataParams = {
    address: address.value,
    area: area.value!,
    location: location.value,
    //freeRight 是否有会员免运费
    // freeRight: includeBenefit('LOGISTICS_DISCOUNT'),
    freeRight: false,
    shopFreights: [{ shopId, freights: [{ templateId, skuInfos: [{ skuId, weight, price, num: 1 }] }] }],
    distributionMode,
  }
  const { data: res, code, msg } = await doGetFreightCalculation(params)
  if (code !== 200 && code !== 100002) {
    return ElMessage.error(msg || '运费获取失败')
  }
  for (const key in res) {
    if (res[key].code) {
      interestsConfig.freight[key].code = res[key]?.code
      if (interestsConfig.freight[key].errCode.includes(res[key].code)) {
        interestsConfig.freight[key].msg = '超出配送范围'
      }
      continue
    }
    interestsConfig.freight[key].price = Object.values(res[key]).length ? Object.values(res[key])[0] : 0
  }
  // }
}
/**
 * @description: 地区选中change
 * @returns {*}
 */
const handleCascaderChange = async () => {
  if (area.value && Array.isArray(area.value)) {
    await getFreightCalculation()
  }
}
// 当前弹框商品选中的sku
const currentChoosedSku = ref<StorageSku>({
  id: '',
  image: '',
  initSalesVolume: 0,
  limitNum: 0,
  limitType: 'UNLIMITED',
  price: '0',
  productId: '',
  salePrice: '0',
  shopId: '0',
  specs: [''],
  stock: '0',
  stockType: 'UNLIMITED',
  weight: 0,
  salesVolume: '0',
})
// 当前商品属性
const currentSpecs = ref<ProductExtraDTO>()

// 活动是否开始
const activitiesBegan = ref(false)

enum ORDER_TYPE {
  COMMON = '普通商品',
  SPIKE = '秒杀',
  TEAM = '拼团',
  BARGAIN = '砍价',
  PACKAGE = '套餐',
}

/**
 * @description: 获取商品信息
 * @returns {*}
 */
const myTeamKey = ref(0)
// 商品不存在或已下架直接跳页面
async function initGoodsInfo(goodId: string, shopId: string) {
  const { data, code, msg } = await doGetProduct({ productId: goodId, shopId: shopId })
  if (code !== 200) {
    ElMessage.error(msg || '商品详情获取失败')
    const time = setTimeout(() => {
      $router.go(-1)
      clearTimeout(time)
    }, 1500)
    return
  }
  return data
}

async function requestGoodsInfo(params: { goodId: string; shopId: string }) {
  const goodsInfo = await initGoodsInfo(params.goodId, params.shopId)
  // 规格总信息
  const goodsSku = goodsInfo.specsSkus
  // 秒杀价格
  const flashSaleSku = goodsInfo?.activity?.type === 'SPIKE' ? goodsInfo?.activity.activityPrice : null
  // 当前参与的活动类型
  const activityType = goodsInfo.activity?.type || 'COMMON'
  // 活动是否够进行中
  const activityIsStart = activityType !== 'COMMON' && goodsInfo.activityOpen
  // 当前sku信息
  const currentSku = goodsSku.skus[0]
  // 全部sku信息
  const currentSkuArr = goodsSku.skus
  return {
    currentSku,
    goodsSku,
    goodsInfo,
    currentSkuArr,
    flashSaleSku,
    activityType,
    activityIsStart,
  }
}
async function initInfo() {
  // const { data, code, msg } = await doGetGoodDetail($route.query.productId as string, $route.query.shopId as string)
  const { currentSku, goodsSku, goodsInfo, currentSkuArr, flashSaleSku, activityType, activityIsStart } = await requestGoodsInfo({
    goodId: $route.query.productId as string,
    shopId: $route.query.shopId as string,
  })
  activitiesBegan.value = activityIsStart
  currentSpecs.value = goodsInfo?.extra
  goodInfo.value = { ...goodsInfo }
  if (goodInfo.value) {
    goodInfo.value.albumPics = [...goodsInfo.albumPics]
  }
  // 优惠
  const { coupon, full, vip } = goodsInfo.stackable
  const result: ApplyTypesJoint[] = []
  if (coupon) {
    result.push('APPLY_COUPON')
  }
  if (full) {
    result.push('APPLY_FULL_REDUCED')
  }
  if (vip) {
    result.push('APPLY_USER_PRICE')
  }
  if (activityType === 'SPIKE') {
    secKillGoodsInfo.value = {
      start: goodsInfo.activity.time.start, // 开始时间
      end: goodsInfo.activity.time.end, // 结束时间
      secKillId: goodsInfo.activity.activityId, // 秒杀活动id
      productId: goodsInfo.productId, // 商品id
      applyTypes: result, // 优惠类型
      productStock: goodsInfo.price.items.price, // 商品库存
      secKillProductSkuVOList: goodsInfo.specsSkus.skus, // 秒杀商品sku
    }
  }
  myTeamKey.value = Date.now()
  let list = []
  let albumPics: string[] = goodsInfo.albumPics
  const len = Math.ceil(albumPics.length / 4)
  for (let i = 0; i < len; i++) {
    let index = 4 > albumPics.length ? albumPics.length : 4
    let tmpList = albumPics.splice(0, index)
    list.push(tmpList)
  }
  swiperList.value = list
  swiperimg.value = list[0][0]
  initAssessStatus(goodsInfo.shopId, goodsInfo.productId)
  deliveryMethod()
  couponList.value = goodsInfo.discountMap.COUPON?.data ?? []
  // choosedSpec.value = currentSku
  seckillFn(currentSku.id)
  skuGroup.value = goodsInfo?.specsSkus
  skuGroup.value.skus =
    activityType !== 'SPIKE' || !flashSaleSku || !activityIsStart
      ? goodsSku.skus
      : goodsSku.skus.map((v: StorageSku) => {
          return {
            ...v,
            salePrice: flashSaleSku,
          }
        })
  skuList.value = goodsInfo?.specsSkus
  // 默认商品规格
  const item = currentSkuArr?.find((item: StorageSku) => item.stockType === 'UNLIMITED' || parseInt(item.stock) > 0)
  currentChoosedSku.value = item ?? currentSku
  checkIsEmpty()
}

// 活动信息
const activityInfo = computed(() => {
  if (goodInfo.value?.activity) {
    return goodInfo.value.activity
  }
  return {
    type: 'COMMON',
    activityId: '',
    stackable: {
      coupon: false,
      full: false,
      vip: false,
    },
    time: {
      start: '',
      end: '',
    },
    data: {},
  }
})
// 活动类型
const activityType = computed(() => {
  return ORDER_TYPE[activityInfo.value.type as keyof typeof ORDER_TYPE]
})

const couponList = ref([])
const isEmpty = ref(false)

const checkIsEmpty = () => {
  isEmpty.value = !skuGroup.value.skus.some((item) => item.stockType === 'UNLIMITED' || parseInt(item.stock) > 0)
}

/**
 * @description: 浏览记录
 * @returns {*}
 */
async function initBrowsingTheRecord() {
  if (!goodInfo.value) return
  const { shopId, productId } = goodInfo.value
  await doPostBrowseCommodity(productId, shopId, shopInfo.value.shopName)
}
/**
 * @description: 添加足迹
 * @returns {*}
 */
async function initFootprint() {
  if (goodInfo.value) {
    if (!$userStore.getterToken) return
    const { albumPics, productCategory, shopId, productId } = goodInfo.value
    const newAlbumPics = Array.isArray(albumPics) ? albumPics[0] : albumPics
    await doPostUserFootprint({
      productPic: newAlbumPics,
      productId: productId,
      productName: productCategory.name,
      productPrice: Number(choosedSpec.value?.salePrice) / 10000,
      shopId,
      platformCategoryId: productCategory.id,
    })
  }
}

/**
 * @description: 添加购物车方法
 */
const handleAddToCar = async () => {
  if (isEmpty.value) return
  currentSpec = GoodSpecRef.value?.productAttributes || []
  // 处理选择校验
  if (!handleProductAttributes(currentSpec)) return
  // if (goodInfo.value?.distributionMode.indexOf('EXPRESS') === -1) return ElMessage('仅支持快递配送，该商品暂不支持加入购物车')
  let oAddTocarLists = {}
  if (currentSpec.length > 0) {
    oAddTocarLists = {
      skuId: choosedSpec.value.id,
      productId: choosedSpec.value.productId,
      num: count.value,
      shopId: goodInfo.value?.shopId,
      productAttributes: currentSpec,
    }
  } else
    oAddTocarLists = {
      skuId: choosedSpec.value.id,
      productId: choosedSpec.value.productId,
      num: count.value,
      shopId: goodInfo.value?.shopId,
    }
  const { code, success, msg } = await doAddTocar(oAddTocarLists)
  if (code === 200 && success) {
    // 添加成功更新购物车 count
    $cardInfo.INIT_CARD()
    ElMessage.success('添加成功')
  } else {
    ElMessage.error(msg)
  }
}

// 处理选择校验
const handleProductAttributes = (productAttributes: ProductFeaturesValueDTO[]) => {
  const length = productAttributes?.length
  for (let i = 0; i < length; i++) {
    const item = productAttributes?.[i]
    if (item.isRequired && item.featureValues.length === 0) {
      ElMessage.error(`${item.featureName}为必选`)
      return false
    }
    if (!item.isMultiSelect && item.featureValues.length > 1) {
      ElMessage.error(`${item.featureName}为单选`)
      return false
    }
  }
  return true
}

// 当前属性数组
let currentParams: string[] = []
// 当前选中的属性
let currentSpec: ProductFeaturesValueDTO[] = []
/**
 * @description: 立即购买方法
 */
const handleBuyNow = async () => {
  if (isEmpty.value || !deliveryOptions.value || ruleMax.value <= 0) return
  // if (goodInfo.value?.distributionMode.indexOf('EXPRESS') === -1) return ElMessage('仅支持快递配送，该商品暂不支持下单')
  currentSpec = GoodSpecRef.value?.productAttributes || []
  // 处理选择校验
  if (!handleProductAttributes(currentSpec)) return
  if (!goodInfo.value) return
  if (+ruleMax.value === 0 && ruleMax.value) return ElMessage.error('该商品暂无库存')
  if (!choosedSpec.value?.id) return
  const { id: skuId, image, price, salePrice, weight } = choosedSpec.value
  const { name: name, productId, freightTemplateId } = goodInfo.value
  const secKillCode = secKillGoodsInfo?.value?.secKillCode
  const applyTypes = secKillGoodsInfo?.value?.applyTypes
  const activityId =
    goodInfo.value.activity?.activityId && activitiesBegan.value && activityInfo.value.type === 'SPIKE' ? goodInfo.value.activity.activityId : ''
  const orderType = goodInfo.value.activity?.activityId && activitiesBegan.value && activityInfo.value.type === 'SPIKE' ? 'SPIKE' : 'COMMON'
  const params = {
    skuId,
    image,
    price,
    salePrice,
    productId,
    productName: name,
    id: productId,
    freightTemplateId,
    weight,
    specs: [...choosedSpec.value.specs, ...handleParams(currentSpec)],
  }
  let productFeaturesValue = <any>{}
  currentSpec.forEach((item) => {
    productFeaturesValue[item.id] = item.featureValues.map((ite) => ite.featureValueId)
  })
  if (activityType.value === '秒杀' && activitiesBegan.value) {
    // params.salePrice = seckillList.value?.[0]?.secKillPrice
    params.price = seckillList.value?.[0]?.price
  }
  storage.setItem(
    `commodityInfo`,
    [
      Object.assign(
        {},
        shopInfo.value,
        { applyTypes, activityId, secKillCode, orderType },
        {
          activityParam: activityId
            ? { type: orderType, activityId: activityId, extra: { activityId: activityId } }
            : { type: orderType, stackable: goodInfo.value.stackable },
        },
        {
          products: [{ ...params, num: count.value, productFeaturesValue: productFeaturesValue, distributionMode: deliveryOptions.value }],
        },
      ),
    ],
    60 * 60 * 2,
  )
  $router.push({
    path: '/settlement',
    query: {
      source: 'PRODUCT',
    },
  })
}
async function IntegralEnough() {
  const { data, msg, code } = await doGetUserIntegralSystemtotal()
  if (code !== 200) ElMessage.error(msg || '获取积分总额失败')
  if ($route.query.integralId && Number(integralGoodsInfo.value?.integralPrice) < data) {
    isEnough.value = false
  } else {
    isEnough.value = true
  }
}
/**
 * @description: 积分兑换
 * @returns {*}
 */
const handleIntegralExchange = () => {
  if (isEnough.value) return
  storage.setItem(`integralOrderInfo`, integralGoodsInfo.value, 60 * 60 * 2)
  $router.push({
    path: '/integralConfirm',
  })
}
/**
 * @description: 获取评论概况
 */
async function initEvaluateInfo(type?: keyof typeof EvaluationType) {
  const { code, msg, data } = await doGetOrderEvaluateInfo(goodInfo.value?.shopId as string, goodInfo.value?.productId as string, type)
  if (code !== 200) return
  if (data.praiseRatio < 20) {
    data.rate = 0
  } else if (data.praiseRatio < 40) {
    data.rate = 1
  } else if (data.praiseRatio < 60) {
    data.rate = 2
  } else if (data.praiseRatio < 80) {
    data.rate = 3
  } else if (data.praiseRatio < 100) {
    data.rate = 4
  } else {
    data.rate = 5
  }
  evaluateInfo.value = data
}
/**
 * @description: 获取评价列表
 */
async function initEvaluateInfoList() {
  if (goodInfo.value?.shopId && goodInfo.value?.productId) {
    const { code, msg, data } = await doGetOrderEvaluateInfoList(goodInfo.value.shopId, goodInfo.value.productId, page.current, radio.value)
    if (code !== 200) return
    // evaluateInfoList.value = [...evaluateInfoList.value, ...data.records]
    evaluateInfoList.value = [...data.records]
    page.total = data.total
  }
}

/**
 * 处理筛选评价
 */
const handleAssessFilter = () => {
  evaluateInfoList.value = []
  setTimeout(() => {
    initEvaluateInfoList()
  }, 300)
}

const handleCurrentChange = (val: number) => {
  page.current = val
  initEvaluateInfoList()
}
const seckillList = ref<any>()
const GoodSpecRef = ref()
/**
 * @description: 处理选择规格
 * @returns {*}
 */
const handleChooseSpec = (e: StorageSku[]) => {
  if (e[0] && goodInfo.value?.activity?.activityId) seckillFn(e[0].id)
  count.value = 1
  choosedSpec.value = e[0]
}
// 拿秒杀的数据
const seckillFn = (id: string) => {
  seckillList.value = secKillGoodsInfo.value?.secKillProductSkuVOList.filter((item) => item.skuId === id)
  limitNumNum.value = seckillList.value?.[0]?.secKillStock
}

/**
 * @description: 获取店铺信息
 * @returns {*}
 */
async function initShopInfo() {
  if (goodInfo.value?.shopId) {
    const { code, data, msg } = await doGetShopBaseInfo(goodInfo.value.shopId)
    if (code !== 200) return ElMessage.error(msg ? msg : '未获取店铺信息')
    shopInfo.value.shopId = data.id
    shopInfo.value.shopName = data.name
    shopInfo.value.shopLogo = data.logo
    shopInfo.value.shopMode = data.shopMode
    shopInfo.value.shopType = data.shopType
  }
}

const ruleMax = computed(() => {
  if (choosedSpec.value?.limitType !== 'UNLIMITED' && choosedSpec.value?.stockType === 'UNLIMITED') {
    return Number(choosedSpec.value?.limitNum)
  } else if (choosedSpec.value?.stockType !== 'UNLIMITED' && choosedSpec.value?.limitType === 'UNLIMITED') {
    return Number(choosedSpec.value?.stock)
  } else if (
    choosedSpec.value?.stockType !== 'UNLIMITED' &&
    choosedSpec.value?.limitType !== 'UNLIMITED' &&
    Number(choosedSpec.value?.limitNum) <= Number(choosedSpec.value?.stock)
  ) {
    return Number(choosedSpec.value?.limitNum)
  } else if (
    choosedSpec.value?.stockType !== 'UNLIMITED' &&
    choosedSpec.value?.limitType !== 'UNLIMITED' &&
    Number(choosedSpec.value?.limitNum) > Number(choosedSpec.value?.stock)
  ) {
    return Number(choosedSpec.value?.stock)
  } else {
    return 999
  }
})

// 商品介绍
const activeName = ref('orderIntroduce')

const handleShowImg = (index: number, i: number) => {
  swiperimg.value = swiperList.value[index][i]
  swiperimgIndex.value = i
  showVideoControl.value = false
  if (i === 0) playButton.value = true
}
// 积分商品详情轮播切换
const handleIntegralShowImg = (index: number, i: number) => {
  integralSwiperimg.value = integralSwiperList.value[index][i]
  swiperimgIndex.value = i
  showVideoControl.value = false
  if (i === 0) playButton.value = true
}
/**
 * @description: 产品说明
 * @returns {*}
 */
const formatService = () => {
  const services: string[] = []
  goodInfo.value?.serviceIds.forEach((item) => {
    const currentHandler = serviceHandler[item]
    services.push(currentHandler.name)
    if (currentHandler.isSendTimeService) {
      interestsConfig.delivery.sendTime = currentHandler.name
      interestsConfig.delivery.receiveTime = currentHandler.sendTime()
    }
  })
  interestsConfig.service = services.join(' · ')
}
/**
 * @description: 猜你喜欢
 * @returns {*}
 */
async function initGuessYouLike() {
  const { code, data, msg } = await doGetGuessYouLike({ size: 4 })
  if (code !== 200) return ElMessage.error(`${msg ? msg : '获取猜你喜欢失败'}`)
  likeData.value = data.records
}
/**
 * @description: 看了又看
 * @returns {*}
 */
async function initlookAndSee() {
  const { code, data, msg } = await doGetlookAndSee({ size: 18, excludeProductId: goodInfo.value?.productId }, shopInfo.value.shopId)
  if (code !== 200) return ElMessage.error(`${msg ? msg : '获取看了又看失败'}`)
  let list = []
  for (let i = 0; i < 3; i++) {
    let tmpList = data.records.splice(0, 6)
    list.push(tmpList)
  }
  lookSeeData.value = list
}
/**
 * @description: 店铺热销
 * @returns {*}
 */
async function initshopHotSales() {
  const { code, data, msg } = await doGetshopHotSales(shopInfo.value.shopId)
  if (code !== 200) return ElMessage.error(`${msg ? msg : '获取店铺热销失败'}`)
  shopHotsaleData.value = data
}
/**
 * @description: 热门关注
 * @returns {*}
 */
async function initpopularAttention() {
  if (!$userStore.getterToken) return
  const { code, data, msg } = await doGetpopularAttention(shopInfo.value.shopId)
  if (code !== 200) return ElMessage.error(`${msg ? msg : '获取热门关注失败'}`)
  PopularAttentionData.value = data
}
const gotoDetail = (productId: string, shopId?: string) => {
  $router.push({
    path: '/detail',
    query: { productId, shopId: shopId ? shopId : shopInfo.value.shopId },
  })
}
const gotoshop = () => {
  $router.push({
    path: '/shop',
    query: { shopId: shopInfo.value.shopId },
  })
}

/**
 * @description: 客服
 */
const gotoCustomerService = async () => {
  await doGetMessagesChatRoom(shopInfo.value.shopId, $userStore.getterUserInfo.userId)
  openNewWindow('/personalcenter/set/customerservice', { shopId: $route.query.shopId, productId: $route.query.productId })
}

/**
 * @description: 查询收藏状态
 * @param {*} shopId
 * @param {*} goodsId
 * @returns {*}
 */
async function initAssessStatus(shopId: string, goodsId: string) {
  if (!$userStore.getterToken) return
  if ($route.query.integralId) return
  const { code, msg, data } = await doGetAssessOrder(shopId, goodsId)
  // if (code !== 200) return ElMessage.error(`${msg ? msg : '获取收藏状态失败'}`)
  isCollection.value = data
}
/**
 * @description: 添加收藏
 * @returns {*}
 */
async function handleAddAssess() {
  if (!goodInfo.value) {
    return
  }
  const { shopId, productId, albumPics: productPic, name: productName, specsSkus } = goodInfo.value
  let productPrice = 0
  if (specsSkus.skus?.length) {
    const prices = specsSkus.skus.map((price) => Number(price.price))
    productPrice = Math.min(...prices)
  }
  const { code, msg, data } = await doAddAssess({
    userCollectDTOList: [{ shopId, productId, productPic, productName, productPrice }],
    whetherCollect: !isCollection.value,
  })
  if (code !== 200) return ElMessage.error(`${msg ? msg : '收藏失败'}`)
  initAssessStatus(shopId, productId)
  ElMessage.success('收藏成功')
}
/**
 * @description: 取消收藏
 * @returns {*}
 */
async function handleCancelAssessOrder() {
  if (!goodInfo.value) {
    return
  }
  const { shopId, productId } = goodInfo.value

  const { code, msg, data } = await doCancelAssessOrder(shopId, productId)
  if (code !== 200) return ElMessage.error(`${msg ? msg : '取消收藏失败'}`)
  initAssessStatus(shopId, productId)
  ElMessage.success('取消收藏成功')
}

// 拼团模块 groupListApi
interface params {
  current: number
  size: number
}
const param = ref<params>({
  current: 1,
  size: 99,
})
// #region 工具函数
function timeToCompare(starttime: string) {
  const start = new Date(starttime).getTime()
  const currentTime = new Date().getTime()
  return currentTime >= start
}
//  获取商品拼团状态与数据
export interface usersPrice {
  users: string
  prices: string
}
const pricesList = ref([])
const limitNumNum = ref('0')
// const seckillCount = ref('')
const usersPrices = ref<usersPrice>({ users: '', prices: '' })
const groupListList = async () => {
  const res = await groupPurchaseNum($route.query.shopId as string, $route.query.productId as string)
  limitNumNum.value = res?.data?.skus?.[0].stock
  usersPrices.value.users = res?.data?.users
  usersPrices.value.prices = res?.data?.skus?.[0]?.prices?.[0]
  pricesList.value = res?.data?.skus?.[0].prices
}
groupListList()
// 商品的sku
const skuList = ref()
// 积分
const integralGoodsInfo = ref<IntegralGoodsInfoType>()
const integralId = ref()
const integralGoodsInfoInt = async () => {
  if ($route.query.integralId) {
    integralId.value = $route.query.integralId
    const { code, data, msg } = await doGetIntegralGoodsInfo(integralId.value)
    if (code === 200) integralGoodsInfo.value = data
    let list = []
    let albumPics: any = data.albumPics?.split(',')
    const len = Math.ceil(albumPics.length / 4)
    for (let i = 0; i < len; i++) {
      let index = 4 > albumPics.length ? albumPics.length : 4
      let tmpList = albumPics.splice(0, index)
      list.push(tmpList)
    }
    integralSwiperList.value = list
    integralSwiperimg.value = list[0][0]
    IntegralEnough()
  } else return false
}
integralGoodsInfoInt()
// 看了又看
const changeFn = () => {
  lookSeeData.value = lookSeeData.value.filter((item: ProductInfo[]) => item[0])
}

watch(
  () => skuGroup.value,
  (newVal, oldVal) => {
    skuGroup.value = newVal
    myTeamKey.value = Date.now()
  },
)
// 配送方式
const deliveryOptions = ref<string>('')
const deliveryType = {
  EXPRESS: '快递配送',
  INTRA_CITY_DISTRIBUTION: '同城配送',
  SHOP_STORE: '到店自提',
  VIRTUAL: '无需配送',
  WITHOUT: '无需配送',
}
const freightPriceFormat = (item: string) => {
  return Number(interestsConfig.freight[item]?.price) > 0 ? interestsConfig.freight[item]?.price : interestsConfig.freight[item]?.msg
}
function deliveryMethod() {
  if (goodInfo.value?.distributionMode.length === 0) {
    deliveryOptions.value = goodInfo.value?.distributionMode[0]
  }
  if (goodInfo.value?.distributionMode?.indexOf('EXPRESS') !== -1) {
    deliveryOptions.value = 'EXPRESS'
  } else if (goodInfo.value?.distributionMode?.indexOf('SHOP_STORE') !== -1) {
    deliveryOptions.value = 'SHOP_STORE'
  } else if (goodInfo.value?.distributionMode?.indexOf('INTRA_CITY_DISTRIBUTION') !== -1) {
    deliveryOptions.value = 'INTRA_CITY_DISTRIBUTION'
  } else if (goodInfo.value.productType === 'VIRTUAL_PRODUCT') {
    deliveryOptions.value = 'VIRTUAL'
  } else {
    deliveryOptions.value = 'EXPRESS'
  }
}

// 活动是否开始
const presaleSeckill = computed(() => {
  const startTime = secKillGoodsInfo.value!.start
  // 当前时间大于活动开始时间 活动开始
  return Date.now() > new Date(startTime).getTime()
})
</script>

<template>
  <div v-if="!integralId" flex justify-between items-center c-w-1190 m-auto c-fs-12 c-h-50>
    <div flex items-center>
      <QIcon v-if="shopInfo.shopName" name="icon-dianpu5" size="20px" />
      <span v-if="shopInfo.shopType === 'SELF_OWNED'" c-ml-3 c-c-f54319 c-fs-12 c-lh-16 class="shop_tag" style="background-color: #fb232f">自营</span>
      <span v-if="shopInfo.shopType === 'PREFERRED'" c-ml-3 c-c-00bb2c c-fs-12 c-lh-16 class="shop_tag" style="background-color: #8239f6">优选</span>
      <span c-fs-14 c-ml-5>{{ shopInfo.shopName }}</span>
    </div>
    <div style="display: flex; align-items: center">
      <div
        v-if="getterPropertiesList?.otherData?.service"
        style="display: flex; align-items: center"
        cursor-pointer
        e-c-9
        @click.stop="gotoCustomerService"
      >
        <QIcon name="icon-lianxikefu" color="rgb(245, 67, 25)" style="margin-left: 20px" />
        &ensp;联系客服
      </div>
      <span e-c-3 c-ml-20 cursor-pointer @click="gotoshop">进店看看>></span>
    </div>
  </div>

  <div bg-white c-fs-12>
    <div c-w-1190 style="margin: 0 auto">
      <div c-w-1190 flex justify-between m-auto c-pt-11 c-pb-38>
        <!-- 左 -->
        <div class="carousel">
          <div c-w-400 c-h-400 style="box-shadow: 1px 3px 10px 0px #f2f2f2">
            <MagnifyingGlass v-if="integralId" :src="integralSwiperimg" :is-empty="isEmpty" />
            <MagnifyingGlass v-else :src="swiperimg" :is-empty="isEmpty" />
            <div v-if="goodInfo?.videoUrl && swiperimgIndex === 0" class="videoBox">
              <span v-if="goodInfo?.videoUrl && swiperimgIndex === 0 && playButton" class="video-trigger" @click="showVideo">
                <q-icon name="icon-bofang1" :svg="true" style="width: 86px; height: 86px" />
              </span>
              <div class="masking">
                <video
                  v-if="showVideoControl"
                  ref="myVideo"
                  class="productVideo"
                  c-w-400
                  c-h-400
                  :src="goodInfo?.videoUrl"
                  muted
                  autoplay
                  controls
                  @ended="videoEnd"
                ></video>
              </div>
            </div>
          </div>
          <el-carousel
            v-if="!integralId && swiperList[0]?.length > 1"
            c-mt-16
            trigger="click"
            height="66px"
            indicator-position="none"
            arrow="always"
            :autoplay="false"
          >
            <el-carousel-item v-for="(item, index) in swiperList" :key="item">
              <div flex c-w-360 ma c-gap-20 c-pl-25>
                <img v-for="(img, i) in item" :key="img" c-w-64 c-h-64 cursor-pointer :src="img" @click="handleShowImg(index, i)" />
              </div>
            </el-carousel-item>
          </el-carousel>
          <el-carousel
            v-if="integralId && integralSwiperList[0]?.length > 1"
            c-mt-16
            trigger="click"
            height="66px"
            indicator-position="none"
            arrow="always"
            :autoplay="false"
          >
            <el-carousel-item v-for="(item, index) in integralSwiperList" :key="item">
              <div flex c-w-360 ma c-gap-20 c-pl-25>
                <img v-for="(img, i) in item" :key="img" c-w-64 c-h-64 cursor-pointer :src="img" @click="handleIntegralShowImg(index, i)" />
              </div>
            </el-carousel-item>
          </el-carousel>
        </div>
        <!-- 右 -->

        <div text-left e-c-9>
          <div>
            <!-- 名称 -->
            <div>
              <div v-if="integralId" class="c-ellipsis-2 c-w-713 c-h-36 fw-900 c-fs-18 e-c-3">{{ integralGoodsInfo?.name }}</div>
              <div v-else class="c-ellipsis-2 c-w-713 c-h-36 fw-900 c-fs-18 e-c-3">{{ goodInfo?.name }}</div>
              <div v-if="!integralId && goodInfo?.saleDescribe" class="c-fs-13 c-c-222 c-w-713 c-ellipsis-1 c-mb-18">
                {{ goodInfo?.saleDescribe }}
              </div>
            </div>
            <PcSeckilldetalInfo
              v-if="activityType === '秒杀'"
              :sec-kill-goods-info="secKillGoodsInfo"
              :choosed-spec="choosedSpec"
              :interests-config="interestsConfig"
              :seckill-list="seckillList"
              :sku-group="skuGroup"
              :good-info="goodInfo"
              :seckill-id="goodInfo?.activity?.activityId"
              :region-datas="regionDatas"
            />
            <!-- 商品信息 -->
            <div v-if="(activityType === '秒杀' && !activitiesBegan) || activityType !== '秒杀'" c-w-713 c-bg-f2f2f2 c-pl-16 c-pt-8 c-pb-12>
              <div flex items-center c-mb-5>
                <span v-if="integralId" c-fs-26 c-c-e31436 fw-700 c-ml-14>
                  {{ integralGoodsInfo?.integralPrice }}积分
                  <span v-if="integralGoodsInfo?.salePrice !== '0'"> + ￥{{ useConversionPrice(integralGoodsInfo?.salePrice).toFixed(2) }} </span>
                </span>
                <span v-else>
                  <span c-c-e31436 fw-700 c-fs-16>
                    ￥<text c-fs-30>{{ useConversionPrice(choosedSpec?.salePrice).toFixed(2).split('.')[0] }}</text>
                    <text>.</text>
                    <text c-fs-16>{{ useConversionPrice(choosedSpec?.salePrice).toFixed(2).split('.')[1] }}</text>
                  </span>
                  <span style="text-decoration-line: line-through" c-fs-14 c-ml-10> ￥{{ useConversionPrice(choosedSpec?.price).toFixed(2) }} </span>
                </span>
              </div>
              <div v-if="integralId">
                市 场 价<span c-ml-14 c-fs-14 style="text-decoration: line-through">￥{{ integralGoodsInfo?.price }} </span>
              </div>
              <!-- 说明 -->
              <div v-if="interestsConfig.service" flex items-center>
                <div c-w-43 e-tj c-mr-23>说明</div>
                <div e-c-3>{{ interestsConfig.service }}</div>
              </div>
              <div v-if="!integralId" flex>
                <PcCouponGoodDetails v-if="couponList.length" :coupon-list="couponList" :cart-coupon="true" />
              </div>
            </div>
            <div c-pt-16 c-pl-16>
              <!-- 送至 -->
              <div v-if="!integralId" flex items-center>
                <div c-w-43 e-tj c-mr-23>送至</div>
                <el-cascader
                  ref="CascaderRef"
                  v-model="area"
                  :props="{
                    label: 'label',
                    value: 'label',
                  }"
                  :style="{ width: '250px', height: '34px' }"
                  placeholder="请选择省/市/区"
                  :options="regionDatas"
                  filterable
                  @change="handleCascaderChange"
                />
                <div v-if="goodInfo && goodInfo.distributionMode?.length === 1">
                  <template v-for="item in goodInfo?.distributionMode" :key="item">
                    <div
                      v-if="interestsConfig.freight[item]?.label && interestsConfig.freight[item]?.code !== INTRA_CITY_DISTRIBUTION_CODE_2"
                      style="margin-left: 10px; display: inline-block"
                    >
                      <span>&ensp;{{ interestsConfig.freight[item]?.label }}</span>
                      <span :style="freightPriceFormat(item) && typeof freightPriceFormat(item) === 'number' && 'color:red'"
                        ><span v-if="freightPriceFormat(item) && typeof freightPriceFormat(item) === 'number'">￥</span
                        >{{ freightPriceFormat(item) }}</span
                      >
                    </div>
                  </template>
                </div>
              </div>
              <GoodSpec
                v-if="goodInfo && (skuGroup.specGroups.length > 0 || currentSpecs?.productAttributes)"
                ref="GoodSpecRef"
                :key="myTeamKey"
                :params="currentSpecs"
                :sku-group="skuGroup"
                :current-choose="currentChoosedSku.specs"
                @chooseSpec="handleChooseSpec"
              />
              <!-- 数量 -->
              <div flex c-mt-13>
                <div c-w-43 e-tj c-mr-23 c-mt-5>数量</div>
                <div>
                  <el-input-number v-model="count" :disabled="integralId" :min="1" :max="ruleMax" size="small" />
                  <span v-if="goodInfo && skuGroup.skus?.length >= 1 && choosedSpec?.limitType !== 'UNLIMITED'" e-c-3 c-ml-12
                    >每人限购<span c-c-e31436> {{ choosedSpec?.limitNum }}</span
                    >件</span
                  >
                  <span v-if="!integralId && choosedSpec?.stockType !== 'UNLIMITED'" v-show="+choosedSpec?.stock < 11" c-ml-16 e-c-9
                    >剩余：{{ choosedSpec?.stock || '0' }}</span
                  >

                  <!-- 配送方式 -->
                  <div v-if="goodInfo && goodInfo.distributionMode?.length > 1" c-mt-20 style="transform: translateX(-67px)">
                    <!-- 删除数组指定元素 -->
                    <span c-mr-15> 配送方式 </span>
                    <el-radio-group v-model="deliveryOptions">
                      <el-radio
                        v-for="(item, index) in goodInfo?.distributionMode.filter((item) => item !== 'INTRA_CITY_DISTRIBUTION')"
                        :key="index"
                        :value="item"
                      >
                        {{ deliveryType[item as keyof typeof deliveryType] }}
                        <span
                          v-if="
                            goodInfo &&
                            goodInfo.distributionMode?.length > 1 &&
                            freightPriceFormat(item) &&
                            typeof freightPriceFormat(item) === 'number'
                          "
                          :style="(freightPriceFormat(item) && typeof freightPriceFormat(item) === 'number' && 'color:red; ', 'font-size: 16px')"
                        >
                          (<span v-if="freightPriceFormat(item) && typeof freightPriceFormat(item) === 'number'">￥</span>
                          <span>{{ freightPriceFormat(item) }}</span
                          >)
                        </span>
                      </el-radio>
                    </el-radio-group>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="operateProduct">
            <div style="display: flex; flex-direction: row; position: relative">
              <div class="btns">
                <template v-if="integralId">
                  <button class="bugNow" :class="{ disabled: isEnough }" @click="handleIntegralExchange">
                    <span class="btn-text"> 立即兑换 </span>
                  </button>
                </template>
                <template v-else>
                  <button
                    class="bugNow"
                    :disabled="isEmpty || !deliveryOptions || ruleMax <= 0"
                    :class="{
                      disabled: isEmpty || !deliveryOptions || ruleMax <= 0,
                    }"
                    @click="handleBuyNow"
                  >
                    <span class="btn-text"> 立即购买 </span>
                  </button>
                  <button
                    v-if="(activityType !== '秒杀' || !presaleSeckill) && shopInfo.shopMode !== 'O2O'"
                    class="addCart"
                    :disabled="isEmpty || ruleMax <= 0"
                    :class="{ disabled: isEmpty || ruleMax <= 0 }"
                    @click="handleAddToCar"
                  >
                    <span class="btn-text"> 加入购物车 </span>
                  </button>
                </template>
              </div>
              <div v-if="!integralId" style="display: flex; flex-wrap: nowrap">
                <div v-if="!isCollection" class="collection" @click="handleAddAssess">
                  <q-icon name="icon-shoucang2" size="24px" /> <span class="collection-text">收藏</span>
                </div>
                <div v-else class="collection" @click="handleCancelAssessOrder">
                  <q-icon name="icon-shoucang1" size="24px" color="rgb(255, 80, 0)" />
                  <span style="color: rgb(255, 80, 0)" class="collection-text">收藏</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div v-if="lookSeeData.length !== 0" :style="{ height: tabsIndex === 1 ? '324px' : '', minHeight: '300px' }" c-w-1190 b-1 c-bc-d5d5d5 ma>
        <div c-w-1188 c-bg-f2f2f2 c-h-34 style="display: flex">
          <div v-if="setMealNum.length !== 0" :class="[tabsIndex === 2 && setMealNum.length > 0 ? 'tabsActions' : '', 'tabs']" @click="tabsIndex = 2">
            优惠套餐
          </div>
          <div :class="[tabsIndex === 1 && 'tabsActions', 'tabs']" @click="tabsIndex = 1">看了又看</div>
        </div>
        <!-- 套餐 setMeal -->
        <PcSetMeal v-if="tabsIndex === 2" :good-info="goodInfo" :shop-info="shopInfo" />
        <!-- 看了又看 -->
        <el-carousel v-else height="288px" :autoplay="false" indicator-position="none" bg-white arrow="always" @change="changeFn">
          <el-carousel-item v-for="(item, index) in lookSeeData" :key="index">
            <div c-pt-15 c-pl-60 c-pr-60 flex text-left>
              <div v-for="(ite, inx) in item" :key="inx" c-ml-20 class="detail" @click="gotoDetail(ite.productId)">
                <img :src="ite.pic" c-w-160 c-h-160 bg-black c-mb-12 />
                <div c-w-160 c-ellipsis-2 e-c-3>{{ ite.productName }}</div>
                <div c-c-e31436 fw-700 c-fs-16 c-lh-34>￥ {{ useConversionPrice(ite.productPrice) }}</div>
                <div v-if="ite.evaluated" e-c-9>
                  已评价 <text c-c-e31436>{{ ite.evaluated }}</text>
                </div>
              </div>
            </div>
          </el-carousel-item>
        </el-carousel>
      </div>
      <!-- 商品介绍 -->
      <div flex justify-center c-mt-15>
        <!-- 左 -->
        <div v-if="!integralId" c-w-190 c-mr-18>
          <!-- 店铺 -->
          <div c-h-185 b-1 c-bc-e0e0e0 c-pt-19>
            <img c-w-90 c-h-90 ma :src="shopInfo.shopLogo" />
            <div e-c-3 c-mt-7 c-mb-7>{{ shopInfo.shopName }}</div>
            <div c-w-90 c-h-24 c-lh-24 e-c-f c-bg-333 ma cursor-pointer @click="gotoshop">进店看看</div>
          </div>
          <!-- 店铺热销 -->
          <div c-mt-15 b-1 c-bc-e0e0e0 c-fs-16 c-h-41 c-lh-41 c-pl-14 text-left fw-700>店铺热销</div>
          <div b-1 c-bc-e0e0e0 text-left>
            <div v-for="item in shopHotsaleData" :key="item.productId" c-pt-9 c-ml-16 c-mr-16 c-pb-16 @click="gotoDetail(item.productId)">
              <img :src="item.pic" c-w-160 c-h-160 c-mb-12 />
              <div c-w-160 c-ellipsis-2 e-c-3>{{ item.productName }}</div>
              <div c-c-e31436 fw-700 c-fs-18 c-lh-34>￥ {{ useConversionPrice(item.productPrice) }}</div>
              <div v-if="item.evaluated" e-c-9>
                已评价 <text c-c-e31436>{{ item.evaluated }}</text>
              </div>
            </div>
          </div>
          <!-- 热门关注 -->
          <div v-if="PopularAttentionData.length > 0" c-mt-15 b-1 c-bc-e0e0e0 c-fs-16 c-h-41 c-lh-41 c-pl-14 text-left fw-700>热门关注</div>
          <div b-1 c-bc-e0e0e0 text-left>
            <div v-for="item in PopularAttentionData" :key="item.productId" c-pt-9 c-ml-16 c-mr-16 c-pb-16 @click="gotoDetail(item.productId)">
              <img :src="item.pic" c-w-160 c-h-160 c-mb-12 />
              <div c-w-160 c-ellipsis-2 e-c-3>{{ item.productName }}</div>
              <div c-c-e31436 fw-700 c-fs-18 c-lh-34>￥ {{ useConversionPrice(item.productPrice) }}</div>
              <div v-if="item.evaluated" e-c-9>
                <text c-c-e31436>{{ item.evaluated }}</text> 人已评价
              </div>
            </div>
          </div>
        </div>
        <!-- 右 -->
        <div>
          <div :style="integralId ? 'width: 1190px; margin-bottom: 20px' : 'width: 982px'">
            <!-- 商品介绍 -->
            <el-tabs v-model="activeName" type="border-card" class="orderIntroduce">
              <el-tab-pane name="orderIntroduce">
                <template #label>
                  <span class="custom-tabs-label" c-lh-32>
                    <span>商品介绍</span>
                  </span>
                </template>

                <div class="product_parameter">
                  <div class="product_parameter_content">
                    <div
                      class="product_parameter_box"
                      :style="{
                        borderTop:
                          goodInfo?.extra && goodInfo?.extra?.productParameters && goodInfo?.extra?.productParameters.length > 1
                            ? '1px solid #f0f3f5'
                            : '',
                      }"
                    >
                      <div
                        v-for="item in goodInfo?.extra?.productParameters"
                        :key="item.id"
                        class="product_parameter_item"
                        :style="{
                          borderTop:
                            goodInfo?.extra && goodInfo?.extra?.productParameters && goodInfo?.extra?.productParameters.length === 1
                              ? '1px solid #f0f3f5'
                              : '',
                        }"
                      >
                        <div class="product_parameter_item_title">{{ item.featureName }}</div>
                        <div class="product_parameter_item_value">
                          <span v-for="value in item.featureValues" :key="value?.id">{{ value.firstValue }}</span>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
                <!-- 富文本 -->
                <div text-left>
                  <div
                    v-if="integralId"
                    v-dompurify-html="formatRichText(integralGoodsInfo?.detail)"
                    overflow-hidden
                    style="text-align: left; padding: 20px"
                  ></div>
                  <div v-else v-dompurify-html="formatRichText(goodInfo?.detail)" class="details-more" overflow-hidden></div>
                </div>
                <div v-if="formatRichText(goodInfo?.detail) === '<p><br></p>'" c-pt-100 c-pb-60 e-c-6>
                  <img
                    src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul111/2024/8/1066b6c9f8aced3ee24c6839e8.png"
                    c-w-160
                    c-pb-20
                  />
                  <p>空空如也</p>
                </div>
              </el-tab-pane>
              <el-tab-pane name="userEvaluate">
                <template #label>
                  <span class="custom-tabs-label">
                    用户评价 <span v-if="evaluateInfo.totalCount" c-c-e31436> {{ evaluateInfo.totalCount }}</span>
                  </span>
                </template>
                <!-- 评论 -->
                <div v-if="!integralId" id="evaluate" c-mb-17>
                  <!-- 评分 -->
                  <div c-pl-23 flex justify-between items-center c-h-85>
                    <div c-c-e31436 c-fs-22 fw-900 flex>
                      <el-rate v-model="evaluateInfo.rate" disabled />
                      <div>{{ evaluateInfo.praiseRatio }} <span c-fs-13>%好评</span></div>
                    </div>
                    <!-- <div c-w-117 c-h-40 b-l c-bc-d5d5d5 c-c-51b8f1 fw-100 c-lh-40 c-fs-14>发表评价</div> -->
                  </div>
                  <!-- 条件 -->
                  <div c-bg-f2f2f2 c-pl-23 c-h-50 flex items-center>
                    <el-radio-group v-model="radio" @change="handleAssessFilter">
                      <el-radio value="">全部({{ evaluateInfo.totalCount }})</el-radio>
                      <el-radio value="CONTENT">有内容({{ evaluateInfo.contentCount }})</el-radio>
                      <el-radio value="IMAGE">有图片({{ evaluateInfo.mediaCount }})</el-radio>
                    </el-radio-group>
                  </div>
                  <!-- 列表 -->
                  <template v-if="evaluateInfoList.length > 0">
                    <div v-for="item in evaluateInfoList" :key="item.id" c-pl-23 c-pr-16 b-b c-bc-f1f1f1>
                      <div c-pt-25 c-mb-18 flex justify-between>
                        <div flex items-center text-left>
                          <img :src="item.avatar" c-w-40 c-h-40 c-mr-11 />
                          <div>
                            <div>{{ item.nickname }}</div>
                            <div c-c-cecece>
                              {{ item.createTime }} <span c-ml-22>{{ item.specs?.join(' ') }}</span>
                            </div>
                          </div>
                        </div>
                        <div>
                          <el-rate v-model="item.rate" disabled size="small" />
                          <div class="c-c-e31436">{{ item.rate.toFixed(1) }}</div>
                        </div>
                      </div>
                      <div c-fs-13 text-left c-mb-22>{{ item.comment }}</div>
                      <div flex c-mb-35>
                        <div v-for="(img, index) in item.medias" :key="index">
                          <video
                            v-if="img.split('.').pop() === 'mp4'"
                            :src="img"
                            style="width: 75px; height: 75px; margin-right: 13px"
                            @click="showCommentVideo(img)"
                          ></video>
                          <el-image
                            v-else
                            :src="img"
                            :preview-src-list="item.medias"
                            :initial-index="index"
                            style="width: 75px; height: 75px; margin-right: 13px"
                          />
                        </div>
                      </div>
                    </div>
                  </template>
                  <div v-else c-pt-100 c-pb-60 e-c-6>
                    <img
                      src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul111/2024/8/1066b6c9f8aced3ee24c6839e8.png"
                      c-w-160
                      c-pb-20
                    />
                    <p>空空如也</p>
                  </div>
                </div>
                <!-- 分页 -->
                <el-pagination
                  v-if="!integralId && evaluateInfoList.length > 0"
                  size="small"
                  background
                  layout="prev, pager, next"
                  :total="+page.total"
                  class="c-mt-20 flex justify-center c-mb-20"
                  @current-change="handleCurrentChange"
                />
              </el-tab-pane>
            </el-tabs>
          </div>

          <!-- 猜你喜欢 -->
          <div v-if="!integralId" c-mt-16>
            <div c-fs-18 e-c-3 text-left>猜你喜欢 <span c-ml-18 c-fs-14 e-c-9>根据你的浏览记录推荐的商品</span></div>
            <div c-pt-15 flex text-left :style="{ justifyContent: likeData.length >= 4 ? 'space-between' : 'flex-start' }">
              <div
                v-for="item in likeData"
                :key="item.productId"
                b-1
                c-bc-eaeaea
                :style="{ marginRight: likeData.length < 4 ? '55px' : '0px' }"
                @click="gotoDetail(item.productId, item.shopId)"
              >
                <img :src="item.productAlbumPics" c-w-204 c-h-204 c-mb-4 />
                <div c-w-198 c-ellipsis-2 e-c-3 c-ml-6>{{ item.productName }}</div>
                <div c-c-e31436 fw-700 c-fs-16 c-lh-32 c-ml-10>￥ {{ useConversionPrice(item.lowestPrice) }}</div>
                <div v-if="item.evaluated" e-c-9 c-ml-6 c-mb-14>
                  已评价 <text c-c-e31436>{{ item.evaluated }}</text>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <ToTopGoCar />
    </div>
    <div v-show="showVideoComment" class="video-container">
      <div class="video-container__box">
        <div class="video-container__close" @click="videoCommentEnd">
          <QIcon svg name="icon-icon-close1" size="25px" />
        </div>
        <video ref="commentVideo" class="video-container__video" :src="videoSrc" controls @canplay="playCommentVideo"></video>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
@include b(detail) {
  &:first-child {
    margin-left: 0;
  }
}

@include b(disabled) {
  background-color: #ccc;
  border-color: #ccc;
  color: #fff;
  cursor: not-allowed;
}

:deep(.el-radio__input.is-checked .el-radio__inner) {
  background: #e31436;
  border-color: #e31436;
}

:deep(.el-radio__input.is-checked + .el-radio__label) {
  color: #e31436;
}

:deep(.el-carousel__arrow) {
  width: 28px;
  height: 58px;
  border: 1px solid #d5d5d5;
  background: #00000033;
  color: #fff;
  font-size: 22px;
}

:deep(.el-carousel__arrow--left) {
  border-radius: 0 40px 40px 0;
  left: 0;
  .el-icon {
    right: 5px;
  }
}

:deep(.el-carousel__arrow--right) {
  border-radius: 40px 0 0 40px;
  right: 0;
  .el-icon {
    left: 5px;
  }
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

@include b(carousel) {
  @include b(videoBox) {
    width: 400px;
    height: 400px;
    position: relative;
    top: -400px;
    z-index: 100;
    // transform: translateX(0) translateY(-50%);
    @include b(masking) {
      background: #000;
      opacity: 1;
    }
  }
  @include b(video-trigger) {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translateX(-50%) translateY(-50%);
    @include flex(center);
    // color: #fff;
    // font-size: 26rpx;
    z-index: 999;
  }
  :deep(.el-carousel__arrow) {
    width: 18px;
    height: 34px;
    font-size: 14px;
  }

  :deep(.el-carousel__arrow--left) {
    .el-icon {
      right: 2px;
    }
  }

  :deep(.el-carousel__arrow--right) {
    .el-icon {
      left: 2px;
    }
  }
}
.yhq-i {
  width: 650px;
  // height: 22px;
  overflow: hidden;
  position: absolute;
  left: 50px;
  z-index: 1;
  span {
    margin: 2px 0;
  }
}

.operateProduct {
  background-color: #fff;
  border: 1px solid transparent;
  bottom: 0;
  display: inline-block;
  margin-left: -12px;
  margin-top: 8px;
  max-width: calc(100% + 10px);
  overflow: hidden;
  padding: 12px;
  position: -webkit-sticky;
  position: sticky;
  z-index: 10;
  .btns {
    border-radius: 12px;
    display: flex;
    flex: 1 1 auto;
    flex-direction: row;
    flex-wrap: nowrap;
    overflow: hidden;
    .bugNow {
      background: linear-gradient(90deg, rgb(255, 119, 0), rgb(255, 73, 0));
      vertical-align: top;
      border: 0;
      cursor: pointer;
      flex: 1 1 auto;
      font-family: PingFang SC;
      height: 48px;
      line-height: 22px;
      outline: 0;
      text-align: center;
      width: 240px;
    }
    .addCart {
      background: linear-gradient(90deg, rgb(255, 203, 0), rgb(255, 148, 2));
      vertical-align: top;
      border: 0;
      cursor: pointer;
      flex: 1 1 auto;
      font-family: PingFang SC;
      height: 48px;
      line-height: 22px;
      outline: 0;
      text-align: center;
      width: 240px;
    }

    .disabled {
      background-color: #ccc;
      border-color: #ccc;
      color: #fff;
      cursor: not-allowed;
    }

    .btn-text {
      display: inline;
      font-size: 16px;
      font-weight: bold;
      color: rgb(255, 255, 255);
    }
  }
  .collection {
    align-items: center;
    color: #1f1f1f;
    cursor: pointer;
    display: flex;
    flex-direction: column;
    font-weight: 400;
    height: 48px;
    justify-content: space-around;
    letter-spacing: 0;
    min-width: 52px;
    margin-left: 8px;

    .collection-text {
      text-wrap: nowrap;
      font-size: 12px;
      font-weight: 400;
      line-height: 16px;
      word-break: keep-all;
    }
  }
}

.activity {
  position: relative;
  top: -24px;
  transform: translateY(15%);
  z-index: 2;
  width: 400px;
  height: 87px;
  border-radius: 14px;
  background: linear-gradient(89.26deg, rgba(238, 30, 50, 1) 0.73%, rgba(238, 78, 30, 1) 99.76%);
  color: rgba(16, 16, 16, 1);
  font-size: 14px;
  font-family: Roboto;
  text-align: left;
  padding: 9px 16px 10px;
  // opacity: 0.9;

  .left {
    float: left;

    p {
      &:nth-of-type(1) {
        margin-top: 3px;
        color: #fcf0f0;
        /* // display: block; */
        height: 35px;
        font-size: 14px;
        font-weight: bold;
        display: table-cell;
        vertical-align: bottom;

        span {
          font-size: 24px;
        }
      }

      &:nth-of-type(2) {
        display: block;
        height: 35px;
        margin-top: 1px;
        color: #d9d9d9;
        font-size: 14px;
        line-height: 35px;

        del {
          float: left;
        }

        & > span {
          font-size: 12px;
          margin-left: 12px;
          color: #101010;
          padding: 2px 11px;
          border-radius: 50px 50px 50px 50px;
          background-color: rgba(255, 237, 199, 1);

          i {
            font-style: normal;
          }
        }
      }
    }
  }

  .right {
    text-align: right;
    float: right;
    color: #fcf0f0;
    font-size: 14px;

    p {
      &:nth-of-type(1) {
        height: 18px;
        font-size: 12px;
        margin-bottom: 7px;
      }

      &:nth-of-type(2) {
        font-weight: bold;
        margin-bottom: 6px;

        span {
          font-size: 12px;
          display: inline-table;
          padding: 1px 2px;
          border-radius: 4px 4px 4px 4px;
          background-color: #920018;
          margin: 0 2px;

          &:last-child {
            margin-right: 0;
          }
        }
      }

      &:nth-of-type(3) {
        font-size: 12px !important;
        display: block;
        height: 18px;

        i {
          font-style: normal;
        }
      }
    }
  }
}
.unity {
  color: #101010;
  .left {
    overflow: hidden;
    float: left;
    width: 333px;
    height: 148px;

    ul {
      li {
        list-style: none;
        height: 24px;
        line-height: 24px;
        // text-align: center;
        position: relative;
        margin-bottom: 15px;

        & > span {
          display: inline-block;
          width: 18px;
          height: 18px;
          border-radius: 50px;
          border: 2px solid #fff;
          overflow: hidden;
          transform: translateY(3px);

          img {
            width: 100%;
            height: 100%;
            transform: translateY(-4px);
          }

          &:nth-of-type(1) {
            position: absolute;
            left: 0;
            left: 0;
            z-index: 999;
          }

          &:nth-of-type(2) {
            position: absolute;
            top: 0;
            left: 12px;
            z-index: 199;
          }

          &:nth-of-type(3) {
            position: absolute;
            top: 0;
            left: 24px;
            z-index: 100;
          }

          &:nth-of-type(4) {
            position: absolute;
            top: 0;
            left: 34px;
            z-index: 99;
          }
        }

        h3 {
          display: inline-block;
          // margin-right: 57px;
          font-size: 14px;

          span {
            color: #e31436;
          }
        }

        .btn {
          margin-right: 30px;
          font-size: 12px;
          text-align: center;
          border-radius: 4px;
          color: #fff;
          float: right;
          width: 70px;
          height: 24px;
          background-color: #e31436;
          cursor: pointer;
        }
      }
    }
  }

  .right {
    overflow: hidden;
    width: 351px;
    height: 148px;
    float: right;

    ul {
      li {
        height: 24px;
        line-height: 24px;
        font-size: 14px;
        margin-bottom: 15px;

        span {
          display: inline-block;
          height: 24px;
          width: 24px;
          overflow: hidden;
          border-radius: 50%;

          img {
            width: 100%;
            height: 100%;
          }
        }

        h3 {
          height: 20px;
          display: inline-block;
          transform: translateY(-4px);
          margin-left: 10px;
          font-size: 14px;
          width: 66px;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
        }

        h4 {
          display: inline-block;
          transform: translateY(-7px);
          margin-left: 65px;
          margin-right: 21px;

          span {
            transform: translateY(7px);
            color: #e31436;
          }
        }

        .btn {
          font-size: 12px;
          text-align: center;
          border-radius: 4px;
          color: #fff;
          float: right;
          width: 70px;
          height: 24px;
          background-color: #e31436;
          cursor: pointer;
        }
      }
    }
  }

  h1 {
    height: 21px;
    font-size: 16px;
    margin-top: 10px;
    margin-bottom: 15px;
  }
}
@include b(content) {
  @include e(rightBox) {
    position: absolute;
    right: 0;
    display: flex;
    justify-content: center;
    flex-direction: column;
    width: 220px;
    height: 218px;
    font-size: 20px;
    @include m(price) {
      margin: 10px 0;
    }
    @include m(btn) {
      width: 180px;
      height: 45px;
      line-height: 45px;
      color: #fff;
      background-color: $setMealBuyColor;
      margin: 10px auto;
      cursor: pointer;
    }
    @include m(over) {
      margin-right: 20px;
      text-align: right;
      font-size: 12px;
      color: #999;
    }
  }
}
@include b(video-container) {
  z-index: 99;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: hsla(0, 0%, 1%, 0.7);
  @include e(box) {
    position: absolute;
    top: 12%;
    left: 50%;
    margin-left: -400px;
    max-width: 800px;
  }
  @include e(close) {
    display: flex;
    display: flex;
    justify-content: flex-end;
    color: #fff;
    margin-right: -22px;
  }
  @include e(video) {
    max-width: 800px;
    max-height: 600px;
    opacity: 1;
  }
}
.details-more {
  margin-top: 16px;
  :deep(img) {
    max-width: 100%;
  }
  :deep(video) {
    max-width: 100%;
  }
}
.tabs {
  line-height: 34px;
  width: 92px;
  font-size: 14px;
  text-align: center;
  cursor: pointer;
}
.tabsActions {
  color: #e31436;
  background-color: #fff;
  border-top: 2px solid #e31436;
}

.orderIntroduce {
  :deep(.el-tabs__content) {
    padding: 0;
  }
}
.product_parameter {
  padding: 20px 20px 3px 20px;
  text-align: left;

  .product_parameter_content {
    width: 100%;

    .product_parameter_box {
      border-left: 1px solid #f0f3f5;
      border-radius: 4px;

      display: flex;
      flex-direction: row;
      flex-wrap: wrap;
      overflow: hidden;
      width: 100%;

      .product_parameter_item {
        --variable-limitLineNumver: 2;
        align-items: center;
        background: #fff;
        border-bottom: 1px solid #f0f3f5;
        border-right: 1px solid #f0f3f5;
        display: flex;
        flex-direction: row;
        justify-content: flex-start;
        position: relative;
        width: 50%;

        .product_parameter_item_title {
          align-items: center;
          background: #f3f6f8;
          color: #11192d;
          display: flex;
          flex-direction: row;
          font-family: PingFangSC-Medium;
          font-size: 14px;
          height: 100%;
          justify-content: flex-start;
          letter-spacing: 0;
          line-height: 18px;
          min-height: 60px;
          padding: 0 24px;
          text-align: left;
          width: 160px;
        }

        .product_parameter_item_value {
          -webkit-box-orient: vertical;
          -webkit-line-clamp: 2;
          color: #11192d;
          display: -webkit-box;
          font-family: PingFangSC-Medium;
          font-size: 14px;
          letter-spacing: 0;
          line-height: 20px;
          margin: 0 24px;
          max-height: 40px;
          overflow: hidden;
          text-align: left;
          width: 240px;
        }
      }
    }
  }
}
</style>
