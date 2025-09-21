enum AddSerMealStatus {
  NOT_STARTED = '未开始',
  PROCESSING = '进行中',
  OVER = '已结束',
  MERCHANT_SELL_OFF = '已下架',
  ILLEGAL_SELL_OFF = '违规下架',
}
enum SetMealType {
  OPTIONAL_PRODUCT = '自选商品套餐',
  FIXED_COMBINATION = '固定套餐',
}
enum ProductAttributes {
  MAIN_PRODUCT = '主商品',
  MATCHING_PRODUCTS = '搭配商品',
}
/**
 * 新增套餐
 * @param setMealId: 套餐id
 * @param shopId: 店铺id
 * @param shopName: 店铺名称
 * @param setMealName: 套餐名称
 * @param setMealDescription: 套餐描述
 * @param setMealMainPicture: 套餐主图
 * @param setMealType:自选商品套餐 1:固定组合套餐
 * @param setMealStatus: 未开始 1:进行中 2:已结束 3:违规下架
 * @param startTime: 开始时间
 * @param endTime: 结束时间
 * @param stackable coupon vip full
 * @param mainProduct 主商品集合
 * @param matchingProducts 搭配商品集合
 * @param ↓↓↓: 商品集合item
 * @param setMealId: 套餐id
 * @param shopId: 店铺id
 * @param productId: 产品id
 * @param productPic: 商品图片
 * @param productName: 商品名称
 * @param productAttributes: 主商品 1:搭配商品
 * @param skuId: sku_id
 * @param skuName: 规格名称
 * @param skuPrice: sku原价
 * @param skuStock: sku库存
 * @param matchingPrice: 搭配价
 * @param matchingStock: 搭配库存
 */
interface DoPostSetMealQuery {
  setMealId: string
  shopId: string
  shopName: string
  setMealName: string
  setMealDescription: string
  setMealMainPicture: string
  setMealType: keyof typeof SetMealType
  distributionMode?: string
  setMealStatus: keyof typeof AddSerMealStatus
  startTime: string
  endTime: string
  stackable: {
    coupon: boolean
    vip: boolean
    full: boolean
  }
  shopMode?: 'COMMON' | 'O2O' | ''
  mainProduct: {
    setMealId: string
    shopId: string
    productId: string
    productPic: string
    productName: string
    productAttributes: keyof typeof ProductAttributes
    skuId: string
    skuName: string
    skuPrice: string
    skuStock: number
    stockType: string
    matchingPrice: string
    matchingStock: number
  }[]
  matchingProducts: {
    setMealId: string
    shopId: string
    productId: string
    productPic: string
    productName: string
    productAttributes: keyof typeof ProductAttributes
    skuId: string
    skuName: string
    skuPrice: string
    skuStock: number
    stockType: string
    matchingPrice: string
    matchingStock: number
  }[]
}
interface MealListType {
  endTime: string
  id: string
  setMealMainPicture: string
  setMealName: string
  setMealStatus: keyof typeof AddSerMealStatus
  setMealType: keyof typeof SetMealType
  shopId: string
  shopName: string
  startTime: string
  violationExplain: string
}

export interface DoPostAddBargainQuery {
  shopId: string
  shopName: string
  name: string
  startTime: string
  endTime: string
  bargainingPeople: number
  bargainValidityPeriod: number
  isSelfBargain: boolean
  userType: keyof typeof UserType
  activityPreheat: number
  stackable: {
    coupon: boolean
    vip: boolean
    full: boolean
  }
  status: keyof typeof AddBargainStatus
  helpCutAmount: keyof typeof HelpCutAmount
  bargainProducts: {
    activityId: string
    productId: string
    productPic: string
    productName: string
    skuId: string
    skuStock: number
    stock: number
    skuName: string
    floorPrice: number | string
    skuPrice: string
  }[]
  productNum: number
}

enum UserType {
  UNLIMITED = '不限',
  NEW_USER = '新用户',
}

enum AddBargainStatus {
  NOT_STARTED = '未开始',
  PROCESSING = '进行中',
  OVER = '已结束',
  ILLEGAL_SELL_OFF = '违规下架',
}

enum HelpCutAmount {
  RANDOM_BARGAIN = '随机砍价',
  FIX_BARGAIN = '固定砍价',
}

export interface FlatGood {
  floorPrice: number
  isJoin: boolean
  productId: string
  matchingPrice: number
  matchingStock: number
  productName: string
  productPic: string
  rowTag: number
  stock: number
  skuItem: {
    productId: string
    skuId: string
    skuName: string
    skuPrice: string
    skuStock: string
    stockType: 'UNLIMITED' | 'LIMITED'
  }
}

export { AddSerMealStatus, type DoPostSetMealQuery, type MealListType }

export interface SetMealNum {
  endTime: string
  saveAtLeast: string
  setMealId: string
  setMealMainPicture: string
  setMealName: string
}

// 套餐详情响应参数
export interface TResponseGetSetMealDetail {
  distributionMode: keyof typeof DISTRIBUTION
  setMealId: string
  setMealName: string
  setMealDescription: string
  setMealType: keyof typeof SetMealType
  saveAtLeast: string
  endTime: string
  setMealProductDetails: SetMealProductDetails[]
  stackable: {
    payTimeout?: {
      units: any[]
      seconds: number
      nanos: number
    }
    coupon: boolean
    vip: boolean
    full: boolean
  }
  setMealMainPicture: string
}
export enum DISTRIBUTION {
  EXPRESS = 'EXPRESS', //快递配送
  INTRA_CITY_DISTRIBUTION = 'INTRA_CITY_DISTRIBUTION', //同城配送
  SHOP_STORE = 'SHOP_STORE', //店铺门店
  VIRTUAL = 'VIRTUAL', //无需物流
  WITHOUT = 'WITHOUT', //无需物流
}
// 优惠套餐规格数据
export interface SetMealProductDetails {
  productId: string
  productPic: string
  productName: string
  productAttributes: string
  skuName?: string[]
  setMealProductSkuDetails: TSetMealProductSkuDetails[]
  saveAtLeast?: string
  checked?: boolean
  flag?: boolean
}
// 组合套餐sku
export interface TSetMealProductSkuDetails {
  matchingPrice: string
  matchingStock: Long
  skuId: string
  skuName: string[]
  saveAtLeast: string
  stockType: 'UNLIMITED' | 'LIMITED'
  storageSku: ApiGoodSku
}

/**
 * 商品sku规格table
 * @param initSalesVolume 初始销量
 */
export interface ApiGoodSku {
  id: string
  image: string
  initSalesVolume: number
  limitNum: number
  limitType: keyof typeof Limit
  price: string
  productId: string
  salePrice: string
  shopId: string
  stock: string
  stockType: keyof typeof Stock
  totalStock: string
  weight: number
  version: number
  specs: string[]
  salesVolume: string
  sort: number
  minimumPurchase: number
  updateTime: string
  deleted: boolean
  createTime: string
  activityType: string
  activityId: string
}

enum Limit {
  UNLIMITED,
  PRODUCT_LIMITED,
  SKU_LIMITED,
}
enum Stock {
  UNLIMITED,
  LIMITED,
}

/**
 * 商品sku组合
 */
export interface ApiGoodSkuCombination {
  skus: ApiGoodSku[]
  specGroups: ApiProductSpecs[]
}

export interface ApiProductSpecs {
  id: string
  name: string
  children: ApiProductSpecs[]
}
