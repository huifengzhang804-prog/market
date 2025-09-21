import type { SetOption } from '@/pluginPackage/goods/commodityInfo/types'
import { ProductType, SellType, ActivityType, LimitType, StockType, EarningType } from '@/apis/good/model'
import type { StorageSku, ProductResponse, ActivityDetailVO, ProductSpecsSkusVO } from '@/apis/good/model'

// 存储商品规格信息
const DSkuGroup = (): ProductSpecsSkusVO => ({
  skus: [],
  specGroups: [],
})
// 轮播图
const DSwiperList = () => ({
  list: [],
  currenuIdx: 0,
  swiperList: [],
  mainList: [],
})
// 当前商品默认值
const DCurrentChoosedSku = (): StorageSku => {
  return {
    //库存类型
    stockType: StockType.UNLIMITED,
    //剩余库存
    stock: '0',
    //销量
    salesVolume: '0',
    // 初始销量
    initSalesVolume: '0',
    //限购类型
    limitType: LimitType.UNLIMITED,
    //限购数量
    limitNum: 0,
    //sku 规格信息
    specs: [''],
    //sku 图片
    image: '',
    //sku 划线价
    price: '0',
    //sku 销售价
    salePrice: '0',
    id: '',
    weight: 0,
    shopId: '',
    productId: '',
  }
}

// 购物车
const DSetOperation = (): SetOption => ({
  type: 'BUY',
  control: false,
  source: 'COMMON',
  isCar: false,
  immediately: false,
  loading: false,
})

/**
 * 商品详情默认值
 */
const DGoodInfo = (): ProductResponse => ({
  id: '',
  activityOpen: false,
  albumPics: [],
  detail: '',
  discountMap: {
    COUPON: {
      data: {
        coupons: [],
      },
      discount: 0,
    },
    FULL: {
      data: {
        desc: '',
      },
      discount: 0,
    },
    VIP: {
      data: {
        isVip: false,
      },
      discount: 0,
    },
  },
  distributionMode: ['EXPRESS'],
  earningMap: {
    [EarningType.REBATE]: '',
    [EarningType.DISTRIBUTE]: '',
  },
  extra: {
    auditTime: '',
    customDeductionRatio: 0,
    platformCategory: {
      one: 0,
      two: 0,
      three: 0,
    },
    productAttributes: [],
    productParameters: [],
    shopCategory: {
      one: 0,
      two: 0,
      three: 0,
    },
    submitTime: '',
  },
  freightTemplateId: 0,
  name: '',
  packages: [],
  pic: '',
  price: {
    estimate: '',
    items: [],
    underlined: '',
  },
  productId: '',
  productType: ProductType.REAL_PRODUCT,
  sale: 0,
  sellType: SellType.OWN,
  serviceIds: ['NO_FREIGHT'],
  shopId: '',
  skuActivity: false,
  skuId: 0,
  specsSkus: {
    skus: [],
    specGroups: [],
  },
  stackable: {
    coupon: true,
    full: true,
    vip: true,
  },
  videoUrl: '',
  whetherCollect: false,
  widePic: '',
})

// 当前商品活动信息
const DCurrentActivity = (): ActivityDetailVO => ({
  type: ActivityType.COMMON,
  activityId: '',
  time: {
    start: '',
    end: '',
  },
  stackable: {
    coupon: false,
    full: false,
    vip: false,
  },
  data: {},
})
export { DGoodInfo, DSetOperation, DCurrentChoosedSku, DSwiperList, DSkuGroup, DCurrentActivity }
