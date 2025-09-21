import type { Ref } from 'vue'
import type { OrderType } from '@/pluginPackage/order/confirmOrder/types'
import type { HooksOfGroupType } from '@pluginPackage/group/hooks/dispatcher'
import type { HooksOfSecKillType } from '@pluginPackage/scondsKill/hooks/dispatcher'
import type { RageTime } from '@/constant/global'
import type { ShopTypeEnum } from '@/pages/modules/home/components/payattention/components/payattention-recommand/recommand'
import type { DistributionMode } from '@/apis/order/types'
import type { GoodItemType, ShopCoupon } from '@/decoration/components/good/good'
import type { STATUS } from '@/basePackage/pages/assessList/types'

enum Limit {
  UNLIMITED,
  PRODUCT_LIMITED,
  SKU_LIMITED,
}

enum Stock {
  UNLIMITED,
  LIMITED,
}

interface ApiProductSpecs {
  id: string
  name: string
  children: ApiProductSpecs[]
}

/**
 * 商品分类图片
 * @param {string} categoryImg
 * @param {string} level LEVEL_3 LEVEL_2 LEVEL_1
 */
interface ApiGoodCategory {
  categoryImg: string
  id: string
  level: string
  name: string
  parentId: string
}

/**
 * 商品sku组合
 */
export interface ApiGoodSkuCombination {
  skus: StorageSku[]
  specGroups: ApiProductSpecs[]
}

export enum ActivityType {
  COMMON = 'COMMON',
  SPIKE = 'SPIKE',
  TEAM = 'TEAM',
  BARGAIN = 'BARGAIN',
  PACKAGE = 'PACKAGE',
}

// 运费想请求参数
export interface TParamsGetGproductDelivery {
  shopId: Long
  productId: Long
  skuId: Long
}

// 二维码请求参数
export interface TdoGetQrcode {
  hash: boolean
  baseUrl: string
  path: string | undefined
  params: any
}

export interface TdoGetshops {
  name: string
  current: number | string
  size: number | string
}

/**
 * 查询商品详情 接口
 */
export interface ProductParam {
  /**
   * 店铺 id
   */
  shopId: Long

  /**
   * 商品 id
   */
  productId: Long

  /**
   * sku id
   * 第一次查询时不填，接口会返回第一个 sku 的 id
   * 切换 sku 时必传，
   */
  skuId?: Long

  /**
   * 选择的 sku 的数量 当 skuid 不为空时必填 可以设置默认为 1
   * 当用户选择 sku 数量后更新
   */
  num?: Long

  /**
   * 选择的商品属性id 及对应的属性值 id
   */
  features?: Record<Long, Long[]>
}

/**
 * 商品详情接口响应数据
 */
export interface ProductResponse {
  id: Long
  //店铺 id
  shopId: Long
  //商品 id
  productId: Long
  //当前sku id
  skuId: Long
  //商品类型
  productType: ProductType
  //商品名称
  name: string
  //商品主图
  pic: string
  //宽屏图片
  widePic: string
  //销量
  sale: Long
  // 价格描述
  saleDescribe?: string
  //支持的配送方式
  distributionMode: DistributionKeyType[]
  //运费模板ID
  freightTemplateId?: Long
  //画册图片
  albumPics: string[]
  //商品视频
  videoUrl?: string
  //服务保障
  serviceIds: ServicesType[]
  //商品详情
  detail?: string
  //其它额外信息
  extra: ProductExtraDTO
  //商品销售类型
  sellType: SellType
  //当前用户是否已收藏该商品
  whetherCollect: boolean
  //规格与 sku 信息
  specsSkus: ProductSpecsSkusVO
  // 活动是否已开启
  activityOpen: boolean
  //当前 sku 是否参与了该活动
  skuActivity: boolean
  //当前 活动sku 库存
  activityStock?: number
  //商品活动信息 无活动不返回该数据
  activity?: ActivityDetailVO
  //套餐信息，商品未参加套餐活动不返回该数据
  packages?: SetMealBasicInfoVO[]
  //商品折扣信息
  discountMap: Record<keyof typeof DiscountType, ProductDiscountVO>
  //商品预计赚、返金额
  earningMap: Record<EarningType, Long>
  //叠加优惠  默认全部可用
  stackable: StackableDiscount
  //商品价格信息
  price: ProductPrice
  supplierId?: Long
}

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

export enum ServiceBarrier {
  NO_FREIGHT = '全场包邮',
  SEVEN_END_BACK = '7天退换',
  TWO_DAY_SEND = '48小时发货',
  FAKE_COMPENSATE = '假一赔十',
  ALL_ENSURE = '正品保证',
}

export type ServicesType = keyof typeof ServiceBarrier

/**
 * 配送方式
 */
enum DistributionType {
  //快递配送
  EXPRESS = '快递配送',
  //同城配送
  INTRA_CITY_DISTRIBUTION = '同城配送',
  //门店配送
  SHOP_STORE = '门店配送',
  //虚拟配送 虚拟发货
  VIRTUAL = '虚拟发货',
}
export enum DistributionTypeWithIc {
  //快递配送
  EXPRESS = '快递配送',
  //同城配送
  IC_MERCHANT = '商家自配',
  IC_OPEN = 'UU 跑腿配送',
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
 * 叠加优惠信息
 */
interface StackableDiscount {
  //是否可叠加会员优惠
  vip: boolean
  //是否可叠加优惠券优惠
  coupon: boolean
  //是否可叠加满减优惠
  full: boolean
}

export interface ProductExtraDTO {
  //店铺类目 key
  shopCategory: CategoryLevel
  //平台类目 key
  platformCategory: CategoryLevel
  //平台服务费比率
  customDeductionRatio: Long
  //商品属性
  productAttributes?: ProductFeaturesValueDTO[]
  //商品参数
  productParameters?: ProductFeaturesValueDTO[]
  //代销价格设置 todo 目前是冗余信息 暂不需处理
  consignmentPriceSetting?: ConsignmentPriceSettingDTO
  //供应啥折扣比率 todo 目前是冗余信息 暂不需处理
  supplierCustomDeductionRatio?: Long
  auditTime: string
  submitTime: string
}

/**
 * 三级类目 id
 */
interface CategoryLevel {
  //一级类目 id
  one: Long
  //二级类目 id
  two?: Long
  //三级类目 id
  three?: Long
}

/**
 * 商品属性参数
 */
export interface ProductFeaturesValueDTO {
  //属性 id
  id: Long
  //属性名称
  featureName: string
  //是否必选
  isRequired: boolean
  //是否多选
  isMultiSelect: boolean
  //属性选项值 集合
  featureValues: FeatureValueDTO[]
}

/**
 * 商品属性选项值
 */
interface FeatureValueDTO {
  //属性值 id
  featureValueId: Long
  //属性值名称
  firstValue: string
  //属性对应价格
  secondValue: Long
}

/**
 * 代销价格设置
 */
interface ConsignmentPriceSettingDTO {
  //价格计算方式
  type: PricingType
  //销售价
  sale: Long
  //划线价
  scribe: Long
}

/**
 * 代销
 */
enum PricingType {
  RATE = 'RATE',
  REGULAR = 'REGULAR',
}

/**
 * 商品规格与 sku 信息
 */
export interface ProductSpecsSkusVO {
  //规格组信息
  specGroups?: ProductSpecVO[]
  //sku 列表
  skus: StorageSku[]
}

export interface ProductSpecVO {
  //规格 id
  id: Long
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
  activityId?: Long
  id: Long
  //库存类型
  stockType: keyof typeof StockType
  //剩余库存
  stock: Long
  //销量
  salesVolume: Long
  // 初始销量
  initSalesVolume: Long
  //限购类型
  limitType: keyof typeof LimitType
  //限购数量
  limitNum: number
  //sku 规格信息
  specs: string[]
  //sku 图片
  image: string
  //sku 划线价
  price: Long
  //sku 销售价
  salePrice: Long
  // 重量
  weight: number
  // 店铺id
  shopId: Long
  // 商品id
  productId: Long
  activePrice?: Long
  // 销售价
  salePrices?: Long[]
  // 属性价格
  attributePirce?: Long
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

/**
 * 活动详情
 */
export interface ActivityDetailVO {
  //活动类型 目前只能是 秒杀、拼团和砍价
  type: OrderType
  //活动 id
  activityId: Long
  //活动价格 当前 sku 参与活动时不为空 如果未参与则为空
  activityPrice?: Long
  //活动生效时间范围
  time: RageTime<string>
  //活动优惠叠加情况 是否参加 会员、优惠券、满减等叠加优惠
  stackable: StackableDiscount
  //活动额外信息
  data?: Obj
}

/**
 * 套餐活动信息
 */
export interface SetMealBasicInfoVO {
  //套餐活动 Id
  setMealId: Long
  //套餐活动主图
  setMealMainPicture: string
  //套餐活动名称
  setMealName: string
  //套餐活动结束时间
  endTime: string
  //套餐活动最少可省
  saveAtLeast: Long
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
  discount: Long
  //折扣额外数据
  data: any
}

/**
 * 商品价格 及价格计算逻辑
 */
export interface ProductPrice {
  //划线价
  underlined: Long
  //预估价
  estimate: Long
  //预估价格计算逻辑
  items: ProductPriceItem[]
}

export interface ProductPriceItem {
  //价格
  price: Long
  //描述
  desc: string
}

export interface Shops {
  id: Long
  logo: string
  name: string
  shopType: keyof typeof ShopTypeEnum
}

export interface RetrieveCommodityResult {
  endRow: number
  firstPage: boolean
  hasNextPage: boolean
  hasPreviousPage: boolean
  lastPage: boolean
  list: RetrieveCommodity[]
  navigateFirstPage: number
  navigateLastPage: number
  navigatePageNums: number[]
  navigatePages: number
  nextPage: number
  pageNum: number
  pageSize: number
  pages: number
  prePage: number
  size: number
  startRow: number
  total: number
}
export interface RetrieveCommodity {
  categoryFirstId: Long
  categorySecondId: Long
  categoryThirdId: Long
  createTime: string
  distributionMode: (keyof typeof DistributionMode)[]
  freightTemplateId: number
  highLight: string
  id: string
  isDistributed: boolean
  memberInfo?: GoodItemType['memberInfo']
  pic: string
  platformCategoryFirstId: Long
  platformCategorySecondId: Long
  platformCategoryThirdId: Long
  prices: Long[]
  pricingType: string
  productId: Long
  productLabel: Obj
  productName: string
  sale: number
  saleDescribe: string
  salePrices: Long[]
  salesVolume: number
  scribe: number
  searchScore: number
  sellType: keyof typeof SellType
  shopId: Long
  shopName: string
  shopType: keyof typeof ShopTypeEnum
  skuIds: Long[]
  specs: string[]
  status: keyof typeof STATUS
  stockTypes: (keyof typeof StockType)[]
  stocks: (number | StockType.UNLIMITED)[]
  widePic: string
  sellOut?: boolean
  couponList?: ShopCoupon[]
  shopOperationHistory?: string
  skuVOS: { image: string; skuId: string }[]
  activeImg?: number
}
