<script setup lang="ts">
import { computed, nextTick, onMounted, onUnmounted, reactive, ref, watch } from 'vue'
import GoodSku from './components/good-sku.vue'
import QNav from '@/components/q-nav/q-nav.vue'
import qEmpty from '@/components/qszr-core/packages/components/q-empty/q-empty.vue'
import CarTop from '@/pages/modules/car/components/car-top.vue'
import StockModal from './components/stock-modal.vue'
// 满减 s
import { doPostFullReductionShopCart } from '@/apis/plugin/fullDiscount'
// 满减 e
import GoodSpec from '@/components/good-spec/good-spec.vue'
import CountNumber from '@/components/count-number/count-number.vue'
import { Decimal } from 'decimal.js-light'
import { useSettingStore } from '@/store/modules/setting'
import { useUserStore } from '@/store/modules/user'

// 门店自提
import CartPointPopup from '@plugin/shopStore/components/cartPointPopup.vue'
import { navigateToConfirmOrder } from '@/pages/modules/car'
import CanvasShare from '@/components/canvas-share/canvas-share.vue'
// 门店自提
import { doDeleteShopCarData, doEmptyShopCarData, doGetShopCarList, doUpdateShopCarGood } from '@/apis/shopCar'
import { doGetGoodDetail, doGetGoodSku } from '@/apis/good'
import { EMPTY_GB } from '@/constant'
import { doAddAssess } from '@/apis/consumer/footprint'
import type { ProductSpecsSkusVO, StorageSku } from '@/pluginPackage/goods/commodityInfo/types'
import type { GoodItemType, GoodListType, StoragePackage } from './types'
import type { ShareData } from '@/components/canvas-share/components/useGetQrcode'
// 满减
import type { CartFullReductionResponseParameters } from '@/apis/plugin/fullDiscount/model'
import useCartDispatcher from '@/store/dispatcher/useCartDispatcher'
import Auth from '@/components/auth/auth.vue'
import CartCouponPopup from '@plugin/coupon/components/cartCouponPopup/cart-coupon-popup.vue'
import type { ProductAmount } from '@/apis/plugin/coupon/model'
import { queryStoreCoupon } from '@/pages/plugin/coupon/utils'
import { TOKEN_DEL_KEY } from '@/utils/tokenConfig'
import { useStatusBar } from '@/hooks'
import { isEqual } from 'lodash'
import type { productAttribute } from '@/apis/o2oshop/model'

type CusGoodSkus = {
  num: number
} & StorageSku

const goodsSkuKeyMap = reactive({
  valid: 0,
  invalid: 0,
})
const $useCartDispatcher = useCartDispatcher()

const pages = getCurrentPages()
const $userStore = useUserStore()
const { divTenThousand } = useConvert()
const isEdit = ref(false)
const validList = ref<GoodListType[]>([])
const inValidList = ref<GoodListType[]>([])
const resultLength = ref(0)
const $settingStore = useSettingStore()

// 最终价格与数量计算
const resultPrice = computed(() => {
  if (validList.value.length) {
    const products = validList.value.map((item) => [...item.products]).flat(1)
    const priceArr = products.map((item) => {
      if (item.isChecked) {
        return new Decimal(item?.finalPrice || item.salePrice).mul(item.num)
      } else {
        return 0
      }
    })

    resultLength.value = priceArr.filter(Boolean).length
    return priceArr.reduce((pre, cur) => {
      return new Decimal(pre).add(new Decimal(cur))
    })
  } else {
    return 0
  }
})

const showSpec = ref(false)
const currentSkus = ref<ProductSpecsSkusVO>({
  specGroups: [],
  skus: [],
})
const choosedSpec = ref<CusGoodSkus>({
  id: '',
  image: '',
  initSalesVolume: 0,
  limitNum: 0,
  limitType: 'UNLIMITED',
  price: '0',
  productId: '',
  salePrice: '0',
  shopId: '0',
  specs: [],
  stock: '0',
  stockType: 'UNLIMITED',
  weight: 0,
  num: 0,
  salesVolume: '',
})
const operationId = ref<Long>('')
const operationShopId = ref<Long>('')
const customStyle = { color: '#F54319', width: '200rpx', height: '60rpx', fontSize: '12px' }
// 全选标识
const showStockModal = ref(false)
const stockModalData = ref({})
const isCheckAllTag = ref(false)
const topToolNode = uni.upx2px(90) // 顶部操作栏高度
const bottomToolNode = uni.upx2px(110) // 底部全选/结算按钮高度
const goodSkuRef = ref()
// statisticalQuantity
const statisticalCommodity = computed(() => {
  return validList.value.reduce((cur, next) => {
    return cur + next.products.length
  }, 0)
})
const cartPointPopupShow = ref(false)
const allSelectedGoods = ref<StoragePackage[]>([])
const fullReductionList = ref<CartFullReductionResponseParameters>({})
// 优惠券弹窗
const isShowCartCouponPopup = ref(false)
const currentCouponPopupShopId = ref<Long>('0')
const currentCouponPopupShopName = ref('')
const productAmounts = ref<ProductAmount[]>([])

// CarTop 组件高度
const carTopHeight = uni.upx2px(80)
// tabbar 组件高度
const tabbarHeight = uni.upx2px(100)

const scrollViewHeight = computed(() => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  if (currentPage.route === 'pages/platform/Index') {
    return `${useScreenHeight().value - useBottomSafe().value - tabbarHeight - bottomToolNode - carTopHeight - useStatusBar().value}px`
  } else {
    return `${useScreenHeight().value - useBottomSafe().value - bottomToolNode - carTopHeight}px`
  }
})
// 分享弹窗控制
const canvasShareRef = ref<InstanceType<typeof CanvasShare> | null>(null)
const sharePopup = ref(false)
const shareData = ref<ShareData>({
  goodId: '',
  image: '',
  productName: '',
  salePrice: '',
  shopId: '',
})
const handleShare = (e: ShareData) => {
  if (canvasShareRef.value) {
    shareData.value = e
    sharePopup.value = true
    canvasShareRef.value.openShare()
  }
}
//解决页面渲染触发进步器change事件 参数
const changenumISok = ref(false)

watch(
  () => $userStore.userInfo.token,
  (val) => {
    // 登录状态更改，刷新数据
    isEdit.value = false
    initList()
  },
)
const showOn = () => {
  if ($userStore.userInfo.token) {
    isEdit.value = false
    initList()
  } else {
    uni.$emit(TOKEN_DEL_KEY)
  }
}
isEdit.value = false
isCheckAllTag.value = false
setTimeout(() => {
  changenumISok.value = true
}, 500)

const resetData = () => {
  validList.value = []
  inValidList.value = []
  fullReductionList.value = {}
  allSelectedGoods.value = []
}
const handleChangeOpration = () => {
  goodSkuRef.value.initIsCountNumberComponentShow()
  isEdit.value = !isEdit.value
}
/**
 * 单选回调
 * @param {Long} shopId
 */
const handleGoodChecked = (shopId: Long) => {
  const currentArr = validList.value.filter((item) => item.shopId === shopId)
  const products = currentArr[0].products
  nextTick(() => {
    if (products.every((item) => item.isChecked)) {
      currentArr[0].isAllChecked = true
    } else {
      currentArr[0].isAllChecked = false
    }
  })
  isCheckAllHandle()
}
/**
 * 全选回调
 * @param {Long} shopId
 */
const handleGoodAllChecked = (shopId: Long) => {
  const currentArr = validList.value.filter((item) => item.shopId === shopId)
  const allStatus = currentArr[0].isAllChecked
  currentArr[0].products = currentArr[0].products.map((item) => {
    if ((!item.needUpdateNum || item.num <= Number(item.skuStock.stock)) && item.distributionMode?.length) {
      item.isChecked = !allStatus
    }
    return item
  })
  isCheckAllHandle()
}
/**
 * 删除购物车
 */
const handleDelete = () => {
  const list = validList.value
  const submitArr = assemblyData(list)
  if (!submitArr.length)
    return uni.showToast({
      title: '请选择商品',
      icon: 'none',
    })
  uni.showModal({
    title: '提示',
    content: '是否删除该商品',
    success: async (res) => {
      if (res.confirm) {
        const { code, success } = await doDeleteShopCarData(submitArr)
        if (code === 200 && success) {
          uni.showToast({
            title: '删除成功',
            icon: 'none',
          })
          initList()
        }
      }
    },
  })
}
/**
 * 清空购物车失效商品
 */
const hanleEmpty = () => {
  uni.showModal({
    title: '提示',
    content: '确定清空失效商品吗？',
    success: async (res) => {
      if (res.confirm) {
        const { code, success } = await doEmptyShopCarData()
        if (code === 200 && success) {
          uni.showToast({
            title: '清空成功',
            icon: 'none',
          })
          inValidList.value = []
        }
      }
    },
  })
}
/**
 * 切换商品sku
 */
const currentSpecs = ref<any[]>([])
const currentExtra = ref<any>({})
const handleChangeSku = async (shopId: Long, goodId: Long, oldSkuId: Long, num: number, item: any) => {
  const { code: rescode, data: res } = await doGetGoodDetail(goodId, shopId)
  const { code, success, data } = await doGetGoodSku(goodId, res?.sellType === 'CONSIGNMENT' ? (res?.supplierId as Long) : shopId)
  if (code === 200 && rescode === 200 && success && data && res) {
    currentSkus.value = data
    const currentChoose = matchCurrentSpec(oldSkuId, data.skus as CusGoodSkus[])
    // 全部商品参数
    currentExtra.value = res.extra
    // 商品参数默认值
    currentSpecs.value = item.productAttributes || []
    // 初始化商品数量
    currentChoose.num = num
    // 当前选中sku
    currentChoose.image = currentChoose.image || item.productImage
    choosedSpec.value = currentChoose
    // 存储当前操作的uniqueId
    operationId.value = item.uniqueId
    // 存储当前操作的shopId
    operationShopId.value = shopId
    showSpec.value = true
  } else {
    uni.showToast({
      title: '获取商品信息失败',
      icon: 'none',
    })
  }
}
const handleChooseSpec = (e: CusGoodSkus[]) => {
  const tempSpec = e[0]
  choosedSpec.value = { ...tempSpec, num: choosedSpec.value.num || 1, image: tempSpec.image || choosedSpec.value.image }
}

const GoodSpecRef = ref()

const handleAddToCar = async () => {
  const { productId, id, num } = choosedSpec.value
  if (!num) {
    return uni.showToast({
      title: '请选择商品数量',
      icon: 'none',
    })
  }
  const { code, success, data, msg } = await doUpdateShopCarGood(operationId.value, {
    productId,
    skuId: id,
    num,
    shopId: operationShopId.value,
    productAttributes: GoodSpecRef.value.productAttributes?.length ? GoodSpecRef.value.productAttributes : null,
  })
  if (code === 200 && success) {
    showSpec.value = false
    initList()
  } else {
    uni.showToast({
      title: `${msg || '修改失败'}`,
      icon: 'none',
    })
  }
}
/**
 * 商品数量变化
 */
const handleChangeCount = async (shopId: Long, goodId: Long, oldSkuId: Long, num: number, item: any) => {
  if (!changenumISok.value) return
  const { code, success, data, msg } = await doUpdateShopCarGood(item.uniqueId, {
    productId: goodId,
    skuId: oldSkuId,
    num,
    shopId,
    productAttributes: item.productAttributes,
  })

  await getNewCartItem(shopId, item.id, item.productId, item.productAttributes, (orgProduct, product) => {
    if (code === 200 && success) {
      orgProduct.uniqueId = product.uniqueId
    } else {
      if (code === 11004) {
        orgProduct.num = product.num
      }
      uni.showToast({ title: `${msg ? msg : '修改数量失败'}`, icon: 'none' })
    }
  })
}

/**
 * 获取最新的购物车数据
 * @param shopId 店鋪ID
 * @param id 购物车ID
 * @param productId 商品ID
 * @param productAttributes 商品属性列表
 * @param fn 成功回调函数
 * 购物车ID + 商品ID + 商品属性列表才能确定唯一值
 */
const getNewCartItem = async (
  shopId: GoodListType['shopId'],
  id: GoodItemType['id'],
  productId: GoodItemType['productId'],
  productAttributes: productAttribute[],
  fn?: (orgProduct: GoodItemType, newProduct: GoodItemType, orgShopList?: GoodListType, newShopList?: GoodListType) => void,
) => {
  const { code, data, success } = await doGetShopCarList()
  if (code === 200 && success) {
    const getProduct = (data: GoodListType[]): { product: GoodItemType | undefined; shopList: GoodListType | undefined } => {
      const shopList = data?.find((item: GoodListType) => item.shopId === shopId)
      const product = shopList?.products.find(
        (product: GoodItemType) => product.id === id && product.productId === productId && isEqual(productAttributes, product.productAttributes),
      )
      return { product, shopList }
    }

    // 获取源
    const { product: orgProduct, shopList: orgShopList } = getProduct(validList.value)
    // 获取最新的数据
    const { product, shopList } = getProduct(data.valid)

    if (orgProduct && product && fn) {
      fn(orgProduct, product, orgShopList as GoodListType, shopList as GoodListType)
    }
  }
}

async function initList() {
  isCheckAllTag.value = false
  try {
    const { code, data, success } = await doGetShopCarList()
    if (code === 200 && success) {
      initShopFullReductionInformationByShopId(data.valid.map((item: GoodListType) => item.shopId))
      validList.value = injectionOfTag(data.valid)
      inValidList.value = injectionOfTag(data.invalid)
      console.log('validList.value', validList.value)
      console.log('inValidList.value', inValidList.value)
      // 需要进行组件强制重载，否则内部的swipe-action组件无法正确计算高度，可以延时浏览器的反应时间，也就是16.66666666，向上取整也就是17
      setTimeout(() => {
        goodsSkuKeyMap.valid = Date.now()
        goodsSkuKeyMap.invalid = Date.now()
      }, 17)
    } else {
      resetData()
    }
  } catch (err) {
    resetData()
  } finally {
    $settingStore.SET_LOADING(false)
  }
}
/**
 * 通过shopId获取店铺满减信息
 * @param {*} shopIds
 */
async function initShopFullReductionInformationByShopId(shopIds: string[]) {
  if (!shopIds.length) return
  const { code, data } = await doPostFullReductionShopCart(shopIds)
  if (code !== 200) return
  fullReductionList.value = data
}
function injectionOfTag(data: GoodListType[]) {
  // skuStock
  return data.map((item) => {
    item.isAllChecked = false
    if (item.products && item.products.length > 0) {
      item.products = item.products.map((ite) => {
        ite.isChecked = false
        // 控制滑动
        ite.swipeAction = false
        ite.isCountNumberComponentShow = true
        ite.slide = false
        ite.image = ite.image || ite.productImage || ''
        return ite
      })
    }
    return item
  })
}
/**
 * 整理提交可用或不可用数据
 * @param {GoodListType[]} list
 */
function assemblyData(list: GoodListType[]) {
  const tempArr = []
  for (let i = 0; i < list.length; i++) {
    const tempObj = {
      shopId: '' as Long,
      skuIds: [] as Long[],
      uniqueIds: [] as string[],
    }
    if (list[i].products.length > 0) {
      tempObj.shopId = list[i].shopId
      for (let j = 0; j < list[i].products.length; j++) {
        // 对应可用商品判断是否checked 对应不可用商品检测enable
        if (list[i].products[j].isChecked || !list[i].enable) {
          tempObj.uniqueIds.push(list[i].products[j].uniqueId)
          tempObj.skuIds.push(list[i].products[j].id)
        }
      }
      if (tempObj.skuIds.length > 0) {
        tempArr.push(tempObj)
      }
    }
  }
  return tempArr
}
/**
 * 匹配当前选中规格
 */
function matchCurrentSpec(oldSkuId: Long, skus: CusGoodSkus[]) {
  return skus.filter((item) => item.id === oldSkuId)[0]
}

/**
 * 整合商品数据存储缓存供提交订单使用
 */
const sortData = (data: GoodListType[]): StoragePackage[] => {
  const tempArr = []
  for (let i = 0; i < data.length; i++) {
    const { shopId, shopLogo, shopName } = data[i]
    let outObject: StoragePackage = {
      distributionMode: 'EXPRESS',
      shopId,
      shopName,
      shopLogo,
      mode: 'COMMON',
      address: '',
      products: [],
      contractNumber: '',
    }
    for (let j = 0, innerProducts = data[i].products; j < innerProducts.length; j++) {
      if (innerProducts[j].isChecked) {
        const {
          id,
          image,
          num,
          price,
          salePrice,
          productId,
          productName,
          specs,
          freightTemplateId,
          weight,
          distributionMode,
          productAttributes,
          sellType,
          supplierId,
        } = innerProducts[j]
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
          sellType,
          supplierId,
        })
      }
    }
    if (outObject.products.length) {
      tempArr.push(outObject)
    }
  }
  return tempArr
}

enum ShowType {
  SHOP_STORE = 'SHOP_STORE', //到店自提
  INTRA_CITY_DISTRIBUTION = 'INTRA_CITY_DISTRIBUTION', // 同城配送
}
const handleSettlement = async () => {
  if (!resultLength.value) {
    uni.showToast({
      icon: 'none',
      title: '请选择商品',
    })
  } else {
    // 重新获取购物车数据，避免获取不是最新商品数据
    const { code, data, success } = await doGetShopCarList()
    let checkCart = [] as GoodListType[]
    if (code === 200 && success) {
      checkCart = injectionOfTag(data.valid)
      validList.value.forEach((item1) => {
        item1.products.forEach((subItem1) => {
          if (subItem1.isChecked) {
            // 在第二个数组的子项数组中找到对应的项，并将其 isChecked 设置为 true
            const subItem2 = checkCart.findIndex((item2) => item2.shopId === item1.shopId)
            const findIndex = checkCart.find((item2) => item2.shopId === item1.shopId)?.products.findIndex((v) => v.id === subItem1.id) as number
            subItem1.salePrice = checkCart[subItem2].products[findIndex].salePrice
          }
        })
      })
    }

    const carList = sortData(validList.value)
    allSelectedGoods.value = carList

    // 判断是否需要打开选择提货方式弹窗 start
    let shopDisMap: string[] = []
    carList.forEach((shopProducts) => {
      shopProducts.products.forEach((product) => {
        shopDisMap.push(...product.distributionMode)
      })
    })
    // 去重
    shopDisMap = [...new Set(shopDisMap)]

    /**
     * 1. 如果选中的商品是两个或以上店铺的,进一步判断这些商品是否存在 除了快递外 的配送方式,存在就弹出框让选择配送方式,否则进入订单页
     * 2. 如果选中的商品是一个店铺,进一步判断这些商品是否存在 不同 的配送方式,存在则弹出框让选择配送方式,否则进入订单页
     */
    if (carList.length > 1) {
      // 除了快递以外的配送类型
      const showType: (keyof typeof ShowType)[] = ['SHOP_STORE', 'INTRA_CITY_DISTRIBUTION']
      if (showType.find((type) => shopDisMap.includes(type))) {
        cartPointPopupShow.value = true
        return
      }
    } else if (shopDisMap.length > 1) {
      cartPointPopupShow.value = true
      return
    }
    // 判断是否需要打开选择提货方式弹窗 end

    carList.forEach((item) => {
      item.distributionMode = item.products[0].distributionMode[0]
    })
    const newCartList = carList.filter((cart) => cart.products?.length > 0)
    const res = await navigateToConfirmOrder(newCartList)
    if (!res.success) {
      changeshowStockModal(res)
    }
  }
}
const handleLookTo = () => {
  $settingStore.NAV_TO_INDEX('首页')
}
/**
 * 全选
 */
const handleCheckAll = () => {
  validList.value = validList.value.map((item) => {
    item.isAllChecked = isCheckAllTag.value
    item.products = item.products.map((ite) => {
      if (!ite.needUpdateNum || ite.num <= Number(ite.skuStock.stock)) {
        ite.isChecked = isCheckAllTag.value
      }
      return ite
    })
    return item
  })
}
/**
 * 全选判断
 */
function isCheckAllHandle() {
  nextTick(() => {
    const unCheckArr = validList.value.filter((item) => {
      return item.isAllChecked
    })
    if (unCheckArr.length === validList.value.length) {
      isCheckAllTag.value = true
    } else {
      isCheckAllTag.value = false
    }
  })
}
/**
 * 移入收藏
 */
const handleMovedFavorites = (list: GoodListType[], filterFailGoods = true) => {
  const submitAssessArrs = assemblyAssessData(list, filterFailGoods)
  if (!submitAssessArrs.length)
    return uni.showToast({
      title: '请选择需要加入收藏的商品',
      icon: 'none',
    })
  uni.showModal({
    title: '提示',
    content: '确定移入到收藏夹吗?',
    success: async (res) => {
      if (res.confirm) {
        const { code, msg } = await doAddAssess({ whetherCollect: true, userCollectDTOList: submitAssessArrs })
        if (code !== 200) return uni.showToast({ title: `${msg ? msg : '移入收藏失败'}`, icon: 'none' })
        uni.showToast({
          title: '收藏成功',
          icon: 'none',
        })
        if (filterFailGoods) {
          isEdit.value = false
        } else {
          emptyInvalidList()
        }
      }
    },
  })
}
const handelDelSku = (delData: { shopId: Long; skuIds: Long[]; uniqueIds: string[] }[]) => {
  uni.showModal({
    title: '提示',
    content: '是否删除该商品',
    success: async (res) => {
      if (res.confirm) {
        const { code, success } = await doDeleteShopCarData(delData)
        if (code === 200 && success) {
          uni.showToast({
            title: '删除成功',
            icon: 'none',
          })
          initList()
        }
      }
    },
  })
}
/**
 * 打开当前滑动关闭其他
 * @param {*} id
 */
const handleSwipeActionChange = (uniqueId: string) => {
  validList.value.forEach((item) => {
    item.isAllChecked = isCheckAllTag.value
    item.products.forEach((ite) => {
      if (ite.uniqueId === uniqueId) {
        ite.swipeAction = true
      } else {
        nextTick(() => {
          ite.swipeAction = false
        })
      }
    })
  })
}
/**
 * 整理提交可用或不可用数据移入收藏
 * @param {GoodListType[]} list
 * @param {boolean} failure
 */
function assemblyAssessData(list: GoodListType[], filterFailGoods = true) {
  const tempArr = []
  for (let i = 0; i < list.length; i++) {
    if (list[i].products.length > 0) {
      for (let j = 0; j < list[i].products.length; j++) {
        const conditions = filterFailGoods ? list[i].products[j].isChecked || !list[i].enable : true
        // 对应可用商品判断是否checked 对应不可用商品检测enable
        if (conditions) {
          const currentShopId = list[i].shopId
          const currentGoods = list[i].products[j]
          const { salePrice, productName, image, productId, supplierId } = currentGoods
          const newProductGoods = {
            shopId: currentShopId,
            productId,
            productPrice: salePrice.toString(),
            productName,
            productPic: image,
            supplierId,
          }
          if (newProductGoods.productId) {
            tempArr.push(newProductGoods)
          }
        }
      }
    }
  }
  return tempArr
}
/**
 * 收藏失效商品后清空失效列表
 */
async function emptyInvalidList() {
  const { code } = await doEmptyShopCarData()
  if (code !== 200) return uni.showToast({ icon: 'none', title: '清空失败' })
  inValidList.value = []
}

const showBottomActionBar = ref(true)
const changeCouponShowCallback = (val: boolean) => {
  showBottomActionBar.value = !val
}
const changeshowStockModal = (res: { success: boolean; data: { title?: string; content?: string } }) => {
  showStockModal.value = !res.success
  stockModalData.value = res
  cartPointPopupShow.value = false
}
const dispatcher = () => initList()
onMounted(() => {
  $useCartDispatcher.addCartSubscriber(dispatcher)
  showOn()
})
onUnmounted(() => $useCartDispatcher.removeCartSubscriber(dispatcher))
const isSinglePage = computed(() => pages[pages.length - 1].route === 'pages/modules/car/car')

const handleOpenClick = (item: GoodListType) => {
  currentCouponPopupShopId.value = item.shopId
  currentCouponPopupShopName.value = item.shopName
  productAmounts.value = queryStoreCoupon(item)
  isShowCartCouponPopup.value = true
}

watch(
  () => isShowCartCouponPopup.value,
  (val) => changeCouponShowCallback(val),
  { immediate: true },
)
defineExpose({
  showOn,
})
</script>

<template>
  <!-- #ifndef H5 -->
  <q-nav v-if="!isSinglePage" :show-back="false" title="购物车" />
  <!-- #endif -->
  <CarTop v-if="validList.length" :num="statisticalCommodity" :value="isEdit ? '完成' : '编辑'" @right-click="handleChangeOpration"></CarTop>

  <scroll-view scroll-y :style="{ height: `${scrollViewHeight}` }" class="inset-bottom">
    <!-- 有效商品 -->
    <view v-if="validList.length && goodsSkuKeyMap.valid > 0" class="effectGoods">
      <GoodSku
        :key="goodsSkuKeyMap.valid"
        ref="goodSkuRef"
        :list="validList"
        :fullReductionList="fullReductionList"
        @good-checked="handleGoodChecked"
        @good-all-checked="handleGoodAllChecked"
        @changeSku="handleChangeSku"
        @change-count="handleChangeCount"
        @swipe-action-change="handleSwipeActionChange"
        @del-sku="handelDelSku"
        @share="handleShare"
        @change-coupon-show="changeCouponShowCallback"
        @openClick="handleOpenClick"
      />
    </view>
    <!-- 失效商品列表 -->
    <view v-if="inValidList.length" class="failGoods">
      <view class="failGoods__title">
        <text class="failGoods__title--num">失效商品</text>
        <view>
          <view @click="hanleEmpty"><q-icon name="icon-shanchu" color="red" size="40rpx"></q-icon> </view>
        </view>
      </view>
      <view v-if="goodsSkuKeyMap.invalid > 0" class="car">
        <GoodSku :list="inValidList" :is-overdue="true" />
      </view>
    </view>
  </scroll-view>
  <!-- 操作栏 -->
  <template v-if="showBottomActionBar">
    <view v-if="!isEdit && validList.length" class="bar">
      <view class="flex bar__choose" @click="handleCheckAll">
        <u-checkbox v-model="isCheckAllTag" active-color="#F54319" shape="circle" size="18px" width="20px" style="width: 25px" />
        <text
          :style="{
            color: isCheckAllTag ? '#F54319' : '',
          }"
        >
          全选
        </text>
      </view>
      <view class="bar__compute">
        <view class="f12">
          <text>合计:</text><text class="bar__compute--price">{{ divTenThousand(resultPrice) }}</text>
        </view>
        <view class="bar__compute--btn" @click="handleSettlement">结算({{ resultLength }})</view>
      </view>
    </view>
    <view v-else-if="isEdit && validList.length" class="bar">
      <view class="flex bar__choose" @click="handleCheckAll">
        <u-checkbox v-model="isCheckAllTag" active-color="#F54319" shape="circle" size="18px" />
        <text
          :style="{
            color: isCheckAllTag ? '#F54319' : '',
          }"
          >全选
        </text>
      </view>
      <view class="bar__btn">
        <view class="bar__btn--nobg" @click="handleMovedFavorites(validList)">移入收藏夹</view>
        <view class="bar__btn--fill" @click="handleDelete">删除</view>
      </view>
    </view>
  </template>

  <!-- 规格弹窗 -->
  <u-popup v-model="showSpec" mode="bottom" border-radius="14" height="1000">
    <view class="spec">
      <view class="spec__info">
        <u-image :width="220" :height="220" :fade="true" mode="aspectFit" :src="choosedSpec.image" class="spec__info--image"></u-image>
        <view class="spec__product">
          <view style="display: flex; align-items: flex-end">
            <view class="spec__product--price">
              <span>{{ divTenThousand(choosedSpec.salePrice) }}</span>
            </view>
          </view>
          <view class="spec__product-line">
            <view class="spec__product-line--limit">
              <span v-if="currentSkus.skus.length >= 1 && choosedSpec.limitNum && choosedSpec.limitType !== 'UNLIMITED'">
                限购{{ choosedSpec.limitNum }}件
              </span>
            </view>
          </view>
        </view>
      </view>
      <view v-if="currentSkus.specGroups && currentSkus.specGroups.length > 0" class="spe__select">
        <scroll-view scroll-y style="max-height: 500rpx; overflow: hidden">
          <GoodSpec
            ref="GoodSpecRef"
            :params="currentExtra"
            :currentSpecs="currentSpecs"
            :skuGroup="currentSkus"
            :currentChoose="choosedSpec.specs"
            @chooseSpec="handleChooseSpec"
          />
        </scroll-view>
      </view>
      <!-- 兼容 fixed 保证文档流-->
      <view style="height: 210rpx"></view>
      <!-- 兼容 fixed 保证文档流-->
      <view class="car_popup_footer">
        <view class="spec__number">
          <text>数量</text>
          <CountNumber
            v-model="choosedSpec.num"
            :rule="{
              limitNum: choosedSpec.limitNum,
              limitType: choosedSpec.limitType,
              stockType: choosedSpec.stockType,
              stock: choosedSpec.stock,
              teamStock: '0',
            }"
          />
        </view>
        <view class="spec__btn" @click="handleAddToCar">确认</view>
      </view>
    </view>
  </u-popup>

  <!-- 领券弹窗 -->
  <CartCouponPopup
    v-model:show="isShowCartCouponPopup"
    filterStock
    :shopId="currentCouponPopupShopId"
    :name="currentCouponPopupShopName"
    :product-amounts="productAmounts"
  />

  <CanvasShare ref="canvasShareRef" :share-pop-up="sharePopup" :share-data="shareData" @canvas-close="sharePopup = false"></CanvasShare>
  <!-- 配送方式弹窗 -->
  <CartPointPopup v-model="cartPointPopupShow" :all-selected-goods="allSelectedGoods" @changeshowStockModal="changeshowStockModal"></CartPointPopup>
  <!-- empty -->
  <q-empty v-if="!validList.length && !inValidList.length" title="购物车还是空的，去挑选几件中意的商品吧" :background="EMPTY_GB.CART_EMPTY">
    <view class="m-top"><u-button shape="circle" plain :custom-style="customStyle" @click="handleLookTo">去逛逛</u-button></view>
  </q-empty>
  <StockModal v-model="showStockModal" :stock-modal-data="stockModalData" @init="initList" />
  <Auth v-if="isSinglePage" />
</template>

<style lang="scss" scoped>
@include b(m-top) {
  margin-top: 30rpx;
}
@include b(car_popup_footer) {
  height: 210rpx;
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 20rpx;
}
@include b(effectGoods) {
  padding: 12rpx 20rpx;
}

@include b(failGoods) {
  padding: 12rpx 20rpx;
  @include e(title) {
    background: #fff;
    height: 100rpx;
    padding: 0 24rpx;
    @include flex(space-between);
    @include m(num) {
      font-size: 28rpx;
      font-weight: bold;
    }
    @include m(tool) {
      font-size: 24rpx;
      color: #f54319;
      margin-left: 20rpx;
    }
  }
}
@include b(bar) {
  box-sizing: border-box;
  width: 100%;
  height: 110rpx;
  position: fixed;
  bottom: 0;
  background: #fff;
  @include flex(space-between);
  @include e(compute) {
    @include flex();
    @include m(price) {
      color: #f83f30;
      font-size: 34rpx;
      margin-right: 14rpx;
      font-weight: bold;
      &::before {
        content: '￥';
        display: inline-block;
        font-size: 26rpx;
        margin-right: 2rpx;
      }
    }
    @include m(btn) {
      width: 200rpx;
      line-height: 110rpx;
      color: #fff;
      font-size: 32rpx;
      background: #f54319;
      text-align: center;
    }
  }
  @include e(btn) {
    font-size: 26rpx;
    @include flex();
    @include m(nobg) {
      width: 156rpx;
      height: 66rpx;
      line-height: 66rpx;
      text-align: center;
      border: 1px solid #f54319;
      color: #f54319;
      border-radius: 40rpx;
    }
    @include m(fill) {
      width: 160rpx;
      height: 70rpx;
      line-height: 70rpx;
      text-align: center;
      color: #fff;
      background: #f54319;
      border-radius: 40rpx;
      margin: 0 26rpx;
    }
  }
  @include e(choose) {
    margin-left: 42rpx;
  }
}
@include b(iconfont) {
  margin-right: 8rpx;
}
@include b(spec) {
  box-sizing: border-box;
  padding: 0 30rpx;
  overflow: hidden;
  @include e(info) {
    width: 100%;
    height: 250rpx;
    display: flex;
    flex-direction: row;
    box-sizing: border-box;
    align-items: flex-end;
    margin-bottom: 40rpx;
    @include m(image) {
      border-radius: 30rpx;
    }
  }
  @include e(product) {
    @include m(price) {
      font-size: 34rpx;
      font-weight: bold;
      color: #fa5555;
      &::before {
        content: '￥';
        font-weight: normal;
        display: inline-block;
        font-size: 26rpx;
      }
    }
  }
  @include e(product-line) {
    display: flex;
    align-items: center;
    font-size: 26rpx;
    @include m(stock) {
      height: 30rpx;
      line-height: 30rpx;
      margin: 20rpx 20rpx 0 0;
    }
    @include m(limit) {
      color: #fa5555;
    }
  }
  @include e(btn) {
    width: 100%;
    background: #f54319;
    color: #fff;
    text-align: center;
    line-height: 80rpx;
    height: 80rpx;
    border-radius: 60rpx;
    margin: 20rpx 0;
  }
  @include e(number) {
    @include flex(space-between);
  }
}
</style>
