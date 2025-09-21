export interface ApiProductSpecs {
  id: string
  name: string
  children: ApiProductSpecs[]
}

/**
 * @description: 商品sku组合
 */
export interface ApiGoodSkuCombination {
  skus: ApiGoodSkus[]
  specGroups: ApiProductSpecs[]
}

/**
 * @description: 商品sku规格table
 * @param initSalesVolume 初始销量
 */
export interface ApiGoodSkus {
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
  specs: string[]
  salesVolume: string
  secKillPrice?: string // 秒杀价格
  attributePirce?: string // 添加属性价格
}

export interface ProductExtraDTO {
  //店铺类目 key
  shopCategory: CategoryLevel
  //平台类目 key
  platformCategory: CategoryLevel
  //平台服务费比率
  customDeductionRatio: string
  //商品属性
  productAttributes?: ProductFeaturesValueDTO[]
  //商品参数
  productParameters?: ProductFeaturesValueDTO[]
  //代销价格设置 todo 目前是冗余信息 暂不需处理
  consignmentPriceSetting?: ConsignmentPriceSettingDTO
  //供应啥折扣比率 todo 目前是冗余信息 暂不需处理
  supplierCustomDeductionRatio?: string
  auditTime: string
  submitTime: string
}

enum Services {
  /**
   * 全场包邮
   */
  NO_FREIGHT,
  /**
   * 7天退换
   */
  SEVEN_END_BACK,
  TWO_DAY_SEND,
  THREE_DAY_SEND,
  SEVEN_DAY_SEND,
  FIFTEEN_DAY_SEND,
  /**
   * 假一赔十
   */
  FAKE_COMPENSATE,
  /**
   * 正品保证
   */
  ALL_ENSURE,
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
export type ServicesType = keyof typeof Services

/**
 * 商品销售类型
 */
export enum SellType {
  //采购商品
  PURCHASE = 'PURCHASE',
  //代销商品
  CONSIGNMENT = 'CONSIGNMENT',
  //自由商品
  OWN = 'OWN',
}

/**
 * 预计赚类型
 */
export enum ProductType {
  //真实商品
  REAL_PRODUCT = 'REAL_PRODUCT',
  //虚拟商品
  VIRTUAL_PRODUCT = 'VIRTUAL_PRODUCT',
}

/**
 * 配送方式
 */
export enum DistributionType {
  //商家配送
  MERCHANT = '商家配送',
  //快递配送
  EXPRESS = '快递配送',
  //同城配送
  INTRA_CITY_DISTRIBUTION = '同城配送',
  //门店配送
  SHOP_STORE = '门店配送',
  //虚拟配送 虚拟发货
  VIRTUAL = '虚拟发货',
}
export type DistributionKeyType = keyof typeof DistributionType

/**
 * 预计赚类型
 */
export enum EarningType {
  //返利
  REBATE = 'REBATE',
  //分销佣金
  DISTRIBUTE = 'DISTRIBUTE',
}

/**
 * 三级类目 id
 */
export interface CategoryLevel {
  //一级类目 id
  one: string
  //二级类目 id
  two?: string
  //三级类目 id
  three?: string
}

/**
 * 商品属性参数
 */
export interface ProductFeaturesValueDTO {
  //属性 id
  id: string
  //属性名称
  featureName: string
  //是否必选
  isRequired: boolean
  //是否多选
  isMultiSelect: boolean
  //属性选项值 集合
  featureValues: FeatureValueDTO[]
  // 标记是否选中
  _selected?: boolean
}

/**
 * 商品属性选项值
 */
export interface FeatureValueDTO {
  //属性值 id
  featureValueId: string
  //属性值名称
  firstValue: string
  //属性对应价格
  secondValue: string
  // 标记是否选中
  _selected?: boolean
}

/**
 * 代销价格设置
 */
export interface ConsignmentPriceSettingDTO {
  //价格计算方式
  type: PricingType
  //销售价
  sale: string
  //划线价
  scribe: string
}

/**
 * 代销价格计算方式枚举
 */
export enum PricingType {
  RATE = 'RATE',
  REGULAR = 'REGULAR',
}

/**
 * 商品规格与 sku 信息
 */
export interface ProductSpecsSkusVO {
  //规格组信息
  specGroups: ProductSpecVO[]
  //sku 列表
  skus: StorageSku[]
}

export interface ProductSpecVO {
  //规格 id
  id: string
  //规格名称
  name: string
  //排序字段
  order: number
  //子规格 最多值关联到二级
  children: ProductSpecVO[]
}

/**
 * 商品 sku 信息
 */
export interface StorageSku {
  activityId?: string
  id: string
  //库存类型
  stockType: keyof typeof StockType
  //剩余库存
  stock: string
  //销量
  salesVolume: string
  // 初始销量
  initSalesVolume: number
  //限购类型
  limitType: keyof typeof LimitType
  //限购数量
  limitNum: number
  //sku 规格信息
  specs: string[]
  //sku 图片
  image: string
  //sku 划线价
  price: string
  //sku 销售价
  salePrice: string
  // 重量
  weight: number
  // 店铺id
  shopId: string
  // 商品id
  productId: string
  activePrice?: string
}

/**
 * 库存类型
 */
export enum StockType {
  //无线库存
  UNLIMITED = 'UNLIMITED',
  //有限库存
  LIMITED = 'LIMITED',
}

/**
 * sku 限购类型
 */
export enum LimitType {
  //不限购
  UNLIMITED = 'UNLIMITED',
  //商品限购
  PRODUCT_LIMITED = 'PRODUCT_LIMITED',
  //sku 限购
  SKU_LIMITED = 'SKU_LIMITED',
}

export interface RageTime<T> {
  //开始时间
  start: T
  //结束时间
  end: T
}

/**
 * 叠加优惠信息
 */
export interface StackableDiscount {
  //是否可叠加会员优惠
  vip: boolean
  //是否可叠加优惠券优惠
  coupon: boolean
  //是否可叠加满减优惠
  full: boolean
}

/**
 * 套餐活动信息
 */
export interface SetMealBasicInfoVO {
  //套餐活动 Id
  setMealId: string
  //套餐活动主图
  setMealMainPicture: string
  //套餐活动名称
  setMealName: string
  //套餐活动结束时间
  endTime: string
  //套餐活动最少可省
  saveAtLeast: string
  //套餐活动描述
  setMealDescription: string
}

/**
 * 商品折扣类型
 */
export enum DiscountType {
  //会员
  VIP = '会员',
  // 优惠券
  COUPON = '优惠券',
  //满减
  FULL = '满减',
}

/**
 * 商品折扣信息
 */
export interface ProductDiscountVO {
  //折扣金额
  discount: string
  //折扣额外数据
  data: any
}

/**
 * 商品价格 及价格计算逻辑
 */
export interface ProductPrice {
  //划线价
  underlined: string
  //预估价
  estimate: string
  //预估价格计算逻辑
  items: ProductPriceItem[]
}

export interface ProductPriceItem {
  //价格
  price: string
  //描述
  desc: string
}

/**
 * @description:
 * @param {number} productId 商品id
 * @param {number} productPic 展示图片
 * @param {number} productName 商品名称
 * @param {number} productPrice 商品价格
 * @param {number} shopId 店铺id
 * @param {number} footMarkDate 日期
 * @returns {*}
 */
export interface ApiUserFootprint {
  productId: string
  productPic: string
  productName: string
  productPrice: number
  shopId: string
  footMarkDate?: string
  platformCategoryId: string
}
/**
 * @description:
 * @param {number} shopId: 店铺id
 * @param {number} productId: 商品id

 * @returns {*}
 */
export interface ApiAssess {
  shopId: string
  productId: string
}
/**
 * @description: 评价枚举
 */
export enum EvaluationType {
  //有内容
  CONTENT,
  //有图片
  IMAGE,
}
/**
 * @description:
 * @param {*} contentCount 有内容数量
 * @param {*} evaluate  Evaluate
 * @param {*} mediaCount 图片数量
 * @param {*} praiseCount 好评数量
 * @param {*} praiseRatio 好评率
 * @param {*} totalCount 好评总数
 */
export interface ApiEvaluation {
  contentCount: string
  evaluate: Evaluate
  mediaCount: string
  praiseCount: string
  praiseRatio: string
  totalCount: string
  rate: number
}
/**
 * @description:
 * @param {*} comment 文本描述
 * @param {*} image  商品图片
 * @param {*} isExcellent 精选
 * @param {*} medias 评价图片
 * @param {*} name 商品名称
 * @param {*} productId 产品id
 * @param {*} rate 评价星级
 * @param {*} specs 规格
 * @param {*} shopReply 商家回复
 */
export interface Evaluate {
  comment: string
  createTime: string
  deleted: boolean
  id: string
  image: string
  isExcellent: boolean
  itemId: string
  medias: string[]
  name: string
  orderNo: string
  packageId: string
  productId: string
  rate: 5
  shopId: string
  skuId: string
  specs: string[]
  updateTime: string
  userId: string
  shopReply?: string
  avatar?: string
  nickname?: string
}
export type ApiGoodSku = {
  limitNum: number
  limitType: keyof typeof Limit
  stock: string
  stockType: keyof typeof Stock
}
export interface LogisticsFreights {
  // logisticsId: string
  skuInfos: SkuInfos[]
  templateId: string
}
export interface SkuInfos {
  weight: string | number
  price: string | number
  num: string | number
  skuId: string | undefined
}

// export type Resume = ReturnType<ReturnType<typeof calculateFn>['resume']>

// 同城配送post接口数据类型
export interface IintraCity {
  shopGoods: {
    shopId: string
    userId?: string | null
    productCarts: IproductCarts[]
  }[]
  address: string
  receiverAreaCode: string[]
}

export interface IproductCarts {
  id: string
  price: string | number
  salePrice: string | number
  weight: string | number
  num: string | number
}
export interface ProductInfo {
  evaluated: number
  pic: string
  productId: string
  productName: string
  productPrice: number
  shopId?: string
  salesVolume?: number
}
