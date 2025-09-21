import { onLoad } from '@dcloudio/uni-app'
import hooksOfGroupFn from '@pluginPackage/group/hooks/dispatcher'
import hooksOfSecKillFn from '@pluginPackage/scondsKill/hooks/dispatcher'
import hooksOfBargainFn from '@/pluginPackage/bargain/hooks/dispatcher'
import hooksOfCoupon from '@pluginPackage/coupon/hooks/getCouponInfo'
import useMember from '@/composables/useMember'
import { requestGoodsInfo } from '@/pluginPackage/goods/commodityInfo/hooks/request'
import { DCurrentActivity, DCurrentChoosedSku, DGoodInfo, DSetOperation, DSkuGroup, DSwiperList } from './defaultDispatcher'
import type { ActivityDetailVO, ProductFeaturesValueDTO, ProductResponse, ProductSpecsSkusVO, StorageSku } from '@/apis/good/model'
import { ActivityType } from '@/apis/good/model'
import type { ChainHandleParam } from '@plugin/types'
import type { comDispatcherType, couponType, OrderParamsType, SetOption } from '@/pluginPackage/goods/commodityInfo/types'
import { doGetGoodsDetailsCouponPage } from '@/pluginPackage/coupon/apis'
import { computed, nextTick, provide, reactive, ref, unref } from 'vue'

export interface DiscountHooksFn {
  'addon-coupon': ReturnType<typeof hooksOfCoupon>
  'addon-member': ReturnType<typeof useMember>
}

const orderParams = ref(new Map<string, OrderParamsType>())

export function useGoodsInfo() {
  // 刷新页面标志
  const refreshPageFlag = ref(true)
  // 刷新页面
  const refreshPage = async () => {
    refreshPageFlag.value = false
    await initGoodsInfo()
    await nextTick()
    refreshPageFlag.value = true
  }
  const goodsInfo = ref<ProductResponse>(DGoodInfo())
  // 优惠项集合
  const discountSet = ref<ChainHandleParam[]>([])

  const discountMap = ref<comDispatcherType['discountMap']>(
    ref({
      VIP: {
        discount: '',
        data: {
          isVip: false,
        },
      },
      COUPON: {
        discount: '',
        data: {},
      },
      FULL: {
        discount: '',
        data: {},
      },
    }),
  )
  // 存储商品规格信息
  const skuGroup = ref<ProductSpecsSkusVO>(DSkuGroup())
  const swiperList = reactive<{
    list: string[]
    currenuIdx: number
    swiperList: string[]
    mainList: string[]
  }>(DSwiperList())
  const currentChoosedSku = ref<StorageSku>(DCurrentChoosedSku())
  const currentChoosedSkuArr = ref<StorageSku[]>([DCurrentChoosedSku()])

  const extraQuery = ref({})
  // 当前商品活动信息
  const currentActivity = ref<ActivityDetailVO>(DCurrentActivity())

  const setOperation = reactive<SetOption>(DSetOperation())

  // 附加优惠项插件
  const discountPlugin: DiscountHooksFn = {
    'addon-coupon': hooksOfCoupon(currentChoosedSku),
    'addon-member': useMember(),
  }

  // 活动插件
  const activitePlugin = {
    [ActivityType.COMMON]: {
      getSkuIds: {
        value: [],
      },
      getPrice: () => null,
      doRequest: () => {},
    },
    [ActivityType.TEAM]: hooksOfGroupFn(currentChoosedSku, goodsInfo.value, currentChoosedSkuArr, addOrderParam),
    [ActivityType.SPIKE]: hooksOfSecKillFn(goodsInfo.value, addOrderParam),
    [ActivityType.BARGAIN]: hooksOfBargainFn(currentChoosedSku, goodsInfo.value, addOrderParam, refreshPage),
    [ActivityType.PACKAGE]: {
      getSkuIds: {
        value: [],
      },
      getPrice: () => null,
      doRequest: () => {},
    },
  }

  // 是否是活动商品
  const isActivityProduct = computed(() => {
    return Boolean(currentActivity.value && currentActivity.value.type && currentActivity.value.activityId)
  })

  // 商品详情优惠弹窗入口显隐
  const isExistDiscount = computed(() => discountPlugin && Object.values(discountPlugin).some((item: any) => unref(item.isExist)))

  // 会员优惠的价格
  const memberPriceC = computed(() => {
    if (discountSet.value.length) {
      return discountSet.value[0].preferTreatment['addon-member']?.discount || '0'
    }
    return '0'
  })
  /**
   * 除了会员优惠后价格
   */
  const forecastPrice = ref<Long>('0')

  /**
   * 获取活动sku的disable选项
   */
  const activityDisSkuOptions = computed(() => {
    if (currentActivity.value && currentActivity.value.type) {
      const disableSkus: string[] = []
      const activitySkuIds = goodsInfo.value.specsSkus.skus.map((item) => item.id)
      skuGroup.value.skus.forEach((item) => {
        const tag = activitySkuIds.includes(item.id)
        if (!tag) {
          disableSkus.push(item.specs.join(','))
        }
      })
      return disableSkus
    } else {
      return []
    }
  })

  // 初始化商品信息
  const initGoodsInfo = async (skuId?: Long) => {
    if (currentChoosedSku.value.id === skuId) {
      return true
    }
    // 获取商品信息 当前商品的sku + 添加足迹
    const {
      goodsSku,
      goodsInfo: goodsRes,
      currentSkuArr,
    } = await requestGoodsInfo({
      goodId: productId.value,
      shopId: shopId.value,
      skuId,
    })
    if (!skuId && goodsSku) {
      skuGroup.value = goodsSku
    }
    if (currentSkuArr) {
      currentChoosedSkuArr.value = currentSkuArr
    }
    //当前选中的sku 直接使用商品接口返回的skuid即可
    const index = skuIds.value
      ? currentSkuArr?.findIndex((item: any) => item.id === skuIds.value)
      : currentSkuArr?.findIndex((item: any) => item.id === goodsRes.skuId)
    currentChoosedSku.value = index > -1 ? currentSkuArr[index] : currentSkuArr[0]
    // 切换sku不需要更换skus
    if (goodsRes) {
      goodsInfo.value = goodsRes
      forecastPrice.value = goodsRes?.price.estimate
      // 初始化优惠卷信息
      current = 1
      discountMap.value = goodsRes?.discountMap || {}
      initInjectActivityInfo(goodsRes)
    }
    // 切换sku不需要更换图片
    if (!skuId) {
      swiperList.mainList = goodsRes.albumPics
      swiperList.currenuIdx = 0
    }

    return true
  }
  const initInjectActivityInfo = (goodsInfo: ProductResponse) => {
    const ActivityDetailVO = goodsInfo?.activity
    if (ActivityDetailVO?.type) {
      currentActivity.value = ActivityDetailVO
      const oldExtra = getParam(productId.value)?.extra
      activitePlugin[ActivityDetailVO.type].doRequest(
        {
          shopId: shopId.value,
          goodId: productId.value,
          extra: extraQuery.value,
          oldExtra: oldExtra,
        },
        goodsInfo,
      )
    }
  }

  // 获取当前商品可用优惠券
  let current = 1
  const getProductCoupons = async (flag: boolean, params: couponType) => {
    current++
    const { data, code } = await doGetGoodsDetailsCouponPage({ ...params, current: current })
    if (code === 200) {
      discountMap.value.COUPON.data = discountMap.value.COUPON.data.concat(data.records)
    }
  }

  // #region TODO: 核心代码
  // 初始化活动获取商品信息
  const initactive = async () => {
    try {
      // 初始化商品信息 + 添加足迹
      await initGoodsInfo().then(() => {
        if (instruction.value) {
          setOperation.control = true
        }
      })
      // 核心代码 ---- 优惠计算 ----
      // discountSet.value = await handleDiscount(currentChoosedSku, discountPlugin)
    } catch (error) {
      console.log('error', error)
    }
  }

  let productId = ref('')
  let shopId = ref('')
  let skuIds = ref('')
  let instruction = ref(false) // 首页商品跳转进来时的标识 （仅限第一次进入页面展示） 关闭弹窗时 清除标记
  onLoad(async (param = {}) => {
    productId.value = param?.goodId.split('?')[0] ? param?.goodId.split('?')[0] : param.goodId
    shopId.value = param?.shopId.split(',')[0] ? param?.shopId.split(',')[0] : param.shopId
    skuIds.value = param.skuId || undefined
    instruction.value = !!param.instruction
    if (param.extra) {
      // 活动商品携带的额外参数
      const extra = JSON.parse(decodeURIComponent(param.extra))
      extraQuery.value = extra
    }
    // 初始化活动获取商品信息
    initactive()
  })

  //#endregion

  // #region 工具函数
  /**
   * 计算当前选中的sku价格
   * @param {StorageSku} param sku信息
   * @param {Obj[]} currentSpecs 当前选中的规格
   */
  const computedCurrentChoosedSkuPrice = (param: StorageSku, currentSpecs = [] as Obj[]) => {
    let salePrice = param?.salePrice || '0'
    let activePrice = param.activePrice || salePrice
    if (goodsInfo.value.activityOpen && goodsInfo.value.skuActivity) {
      if (goodsInfo.value.activity?.type === 'SPIKE') {
        // 秒杀活动
        activePrice = goodsInfo.value.activity?.activityPrice || '0'
      } else if (goodsInfo.value.activity?.type === 'TEAM') {
        // 拼团活动
        const teamSku = activitePlugin.TEAM.groupInfo.value.skus.find((item) => item.skuId === param.id)
        if (teamSku) {
          // 当前第几阶梯拼团
          activePrice = teamSku.prices[activitePlugin.TEAM.groupIndex.value]
          if (currentSpecs.length) {
            // 当前第几阶梯拼团
            currentSpecs.forEach((item) => {
              item.featureValues.forEach((i: any) => {
                activePrice += i.secondValue
              })
            })
          }
        }
      }
    } else {
      salePrice = goodsInfo.value.activityOpen ? param?.price : param?.salePrice || '0'
      activePrice = salePrice
    }
    currentChoosedSku.value = {
      ...param,
      salePrice,
      activePrice,
    }
  }

  /**
   * 更新SKU 主要更新价格 拼团和秒杀价需要单独计算
   * @param {StorageSku} param sku信息
   * @param {boolean} isUpdate 是否更新商品信息
   * @param {Obj[]} currentSpecs 当前选中的规格
   */
  async function updateSku(param: StorageSku, isUpdate = true, currentSpecs = [] as Obj[]) {
    if (!param) return
    goodsInfo.value.skuId = param.id
    if (isUpdate) {
      await updateSkuCalculate(param, currentSpecs)
      computedCurrentChoosedSkuPrice(param, currentSpecs)
    } else {
      computedCurrentChoosedSkuPrice(param, currentSpecs)
    }
  }

  const updateSkuCalculate = async (param: StorageSku, currentSpecs = [] as Obj[]) => {
    await initGoodsInfo(param.id)
    return true
  }

  /**
   * 获取discountPlugin
   */

  /**
   * 获取param
   */
  const getParam = (productId: Long) => {
    return activitePlugin.TEAM.isJoinForGroup.value || activitePlugin.BARGAIN.isJoinForBargain.value || activitePlugin.SPIKE.isJoinSecKill.value
      ? orderParams.value.get(`${productId}`)
      : undefined
  }
  /**
   * 添加订单项
   */
  const addParam = (param: OrderParamsType) => {
    orderParams.value.set(`${param.productId}`, param)
  }
  /**
   * 删除订单参数
   */
  const delParam = (productId: Long) => {
    if (orderParams.value.has(`${productId}`)) {
      orderParams.value.delete(`${productId}`)
    }
  }
  /**
   * 清空订单参数
   */
  const resetParam = () => {
    orderParams.value = new Map()
  }
  /**
   * 获取订单支付价格
   */
  const getOrderPrice = (): Long => {
    const product = goodsInfo.value
    if (!product) {
      return '0'
    }
    const activity = product.activity
    if (!activity || (!product.activityOpen && !product.skuActivity) || !product.skuActivity) {
      return currentChoosedSku.value.salePrice
    }
    return activity.activityPrice || '0'
  }

  /**
   * 更新购物车标识
   * @param {boolean} e
   */
  function updateIsCar(e: boolean) {
    setOperation.isCar = e
  }

  /**
   * 更新首页商品组件进入标识
   */
  function updateInstruction() {
    if (instruction.value) {
      instruction.value = false
    }
  }

  /**
   * 初始化hooks回调添加orderparam方法
   */
  function addOrderParam(param: OrderParamsType) {
    console.log(param, 'param')
    addParam(param)
  }

  /**
   * 当前属性信息
   */
  const currentGoodsExtraData = ref<{
    currentParams: string[]
    currentSpecs: ProductFeaturesValueDTO[]
  }>({
    currentParams: [], // 当前属性数组
    currentSpecs: [], // 当前选中的属性
  })

  // #endregion

  const exportSlice: comDispatcherType = {
    instruction,
    forecastPrice,
    setOperation,
    discountPlugin,
    isExistDiscount,
    isActivityProduct,
    ...activitePlugin,
    productId,
    shopId,
    goodInfo: goodsInfo,
    skuGroup,
    currentChoosedSku,
    currentGoodsExtraData,
    swiperList,
    activityDisSkuOptions,
    extraQuery,
    currentActivity,
    updateInstruction,
    getParam,
    addParam,
    delParam,
    resetParam,
    getOrderPrice,
    discountSet,
    updateIsCar,
    updateSku,
    ...hooksOfCoupon(currentChoosedSku),
    memberPriceC,
    discountMap,
    initGoodsInfo,
    refreshPage,
    refreshPageFlag,
    getProductCoupons,
  }
  provide('comProvide', exportSlice)
  return exportSlice
}
