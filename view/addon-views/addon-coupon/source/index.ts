import useConvert from '@/composables/useConvert'
import Decimal from 'decimal.js'

const { divTenThousand, mulTenThousand } = useConvert()
// 首页下拉选择
export const couponIndexStatus = {
  NOT_OPEN: '未开始',
  OPEN: '进行中',
  CLOSED: '已结束',
  SHOP_BANED: '已下架',
  BANED: '违规下架',
}
// 首页下拉类型
export const couponIndexType = {
  PRICE_DISCOUNT: '无门槛折扣券',
  PRICE_REDUCE: '无门槛现金券',
  REQUIRED_PRICE_DISCOUNT: '满折券',
  REQUIRED_PRICE_REDUCE: '满减券',
} as const

/**
 * @param PRICE_REDUCE 无门槛现金券
 * @param PRICE_DISCOUNT 无门槛折扣券
 * @param REQUIRED_PRICE_REDUCE 满减券
 * @param REQUIRED_PRICE_DISCOUNT 满折券
 */
export enum CouponType {
  PRICE_REDUCE = 'PRICE_REDUCE',
  PRICE_DISCOUNT = 'PRICE_DISCOUNT',
  REQUIRED_PRICE_REDUCE = 'REQUIRED_PRICE_REDUCE',
  REQUIRED_PRICE_DISCOUNT = 'REQUIRED_PRICE_DISCOUNT',
}

export type CouponJointType = keyof typeof CouponType

/**
 * @param PERIOD 固定日期
 * @param IMMEDIATELY 立即生效
 */
export enum EffectType {
  PERIOD = 'PERIOD',
  IMMEDIATELY = 'IMMEDIATELY',
}

export type EffectTypeJointType = keyof typeof EffectType
/**
 * @param ALL 全部商品
 * @param SHOP_ALL 店铺全部商品
 * @param ASSIGNED 指定商品
 * @param ASSIGNED_NOT 指定商品不生效
 */
export enum ProductType {
  ALL = 'ALL',
  SHOP_ALL = 'SHOP_ALL',
  ALL_PRODUCT = 'ALL_PRODUCT',
  ASSIGNED = 'ASSIGNED',
  ASSIGNED_NOT = 'ASSIGNED_NOT',
}
export type ProductTypeJointType = keyof typeof ProductType

/** 进行中的优惠券
 * @param name 优惠券名称
 * @param days 领券立即生效 持续时间
 * @param endDate 固定日期段 结束日期
 * @param productType 作用的商品类型 平台全部商品 店铺全部商品 点不指定商品生效 店铺指定商品不生效
 * @param productIds 商品 生效/不生效的 id 列表
 */
export interface WorkingCoupon {
  name: string
  days: number
  endDate: string
  productType: keyof typeof ProductType
  productIds: string[]
}

/**
 * @param requiredAmount 满减满折需要的满足的金额
 * @param discount 折扣力度
 * @param amount 优惠金额 现金券金额
 * @param effectType 优惠券生效类型
 * @param startDate 固定日期段 开始日期
 * @param num 优惠券发行量
 * @param limit 每人发送数量
 * @param dates 日期组件占位专用
 */
export interface CouponDTO extends WorkingCoupon {
  type: keyof typeof CouponType
  requiredAmount: number
  discount: number
  days: number
  createTime: string
  amount: number
  effectType: keyof typeof EffectType
  startDate: string
  num: number
  limit: number
  id: string
  status: string
  shopId: string
  usedCount: string
  stock: string
  statusText: '未开始' | '进行中' | '已结束' | '已下架' | '违规下架'
}

export interface ApiGoodsRetrieve {
  categoryFirstId: string
  categorySecondId: string
  categoryThirdId: string
  createTime: string
  hotScore: string
  id: string
  name: string
  productName: string
  pic: string
  platformCategoryFirstId?: string
  platformCategorySecondId?: string
  platformCategoryThirdId?: string
  salePrices: string[]
  salesVolume: string
  shopId: string
  shopName: string
  isCheck: boolean
}

/**
 * 价格转毫上传
 */
export function pricePostHaoFormat(price: number) {
  return price ? mulTenThousand(price) : ''
}

/**
 * 接口价格转毫
 */
export function priceApiFormat(price?: number) {
  return price ? divTenThousand(price).toString() : ''
}

/**
 * 优惠券接口返回处理
 */
export function dataFormat(params: Partial<CouponDTO>) {
  const { name, days, type, endDate, productIds, productType, requiredAmount, discount, amount, effectType, startDate, num, limit, id, shopId } =
    params
  const val = {
    shopId,
    id,
    name,
    days,
    endDate,
    productType,
    type,
    requiredAmount: priceApiFormat(requiredAmount).toString(),
    discount,
    amount: priceApiFormat(amount),
    effectType,
    startDate,
    num,
    limit,
    productIds,
  }
  return val
}

/**
 * 优惠券状态
 */
export const couponStatusComputed = (row: CouponDTO) => {
  return row.statusText
}

export interface ApiCouponVO {
  couponUserId: string
  shopId: string
  shopName: string
  id: string
  name: string
  type: CouponJointType
  startDate: string
  endDate: string
  requiredAmount: string
  amount: string
  discount: string
  productType: ProductTypeJointType
  createTime: string
  days?: number
  effectType: EffectTypeJointType
  stock?: string
}

export interface CartApiCouponVO extends ApiCouponVO {
  watermark: boolean
  discountAmount: string
  claimedStatus: keyof typeof CLAIMED_STATUS
}
enum CLAIMED_STATUS {
  CLAIM = '未领取',
  LIMIT = '已领取',
  FINISHED = '已抢光',
}

export interface ApiOrderCouponVO extends ApiCouponVO {
  discountAmount: string
  productIds: string[]
}

// 优惠券规则格式化
export const formattingCouponRules = (cou: ApiOrderCouponVO | CartApiCouponVO | undefined, autocomplete = true) => {
  let text
  if (!cou || !Object.values(cou).length) {
    text = ''
    return text
  }
  switch (cou.type) {
    case 'PRICE_REDUCE':
      text = autocomplete ? `无门槛现金券减${cou.amount && divTenThousand(cou.amount)}元` : `无门槛现金券`
      break
    case 'PRICE_DISCOUNT':
      text = autocomplete ? `无门槛折扣券打${cou.discount}折` : `无门槛折扣券`
      break
    case 'REQUIRED_PRICE_REDUCE':
      text = autocomplete
        ? `满${divTenThousand(cou.requiredAmount)}元减${cou.amount && divTenThousand(cou.amount)}元`
        : `满${divTenThousand(cou.requiredAmount)}元可用`
      break
    case 'REQUIRED_PRICE_DISCOUNT':
      text = autocomplete ? `满${divTenThousand(cou.requiredAmount)}元打${cou.discount}折` : `满${divTenThousand(cou.requiredAmount)}元可用`
      break
    default:
      break
  }
  return text
}
/**
 * @param UNUSED 待使用
 * @param UNCLAIMED 待领取
 * @param USED 已使用
 * @param EXPIRED 已过期
 */
export enum CouponQueryStatus {
  UNUSED = 'UNUSED',
  UNCLAIMED = 'UNCLAIMED',
  USED = 'USED',
  EXPIRED = 'EXPIRED',
}
export type TagItem = { name: string; key: CouponQueryStatusJointType }
export type CouponQueryStatusJointType = keyof typeof CouponQueryStatus | string
export interface CostCalculate {
  //商品总价
  totalAmount: string
  //店铺优惠
  shopDiscount: string
  //平台优惠
  platformDiscount: string
  // 总运费
  totalFreight: string
  //需要支付的费用
  payAmount: string
}
export const commodityPrice = (
  shopProducts: OrderType[],
  allCoupons: Map<string, ApiOrderCouponVO>,
  freightInfo: { [x: string]: string } | undefined,
): CostCalculate => {
  const result = {
    totalAmount: '0',
    shopDiscount: '0',
    platformDiscount: '0',
    totalFreight: '0',
    payAmount: '0',
  }
  if (!shopProducts || shopProducts.length === 0) {
    return result
  }
  return getCalculatedPrice(shopProducts, !allCoupons ? new Map() : allCoupons, freightInfo)
}
export interface OrderType {
  shopId: string
  shopLogo: string
  shopName: string
  products: StorageProducts[]
}
export type StorageProducts = Pick<GoodItemType, ProductsPickItem> & { weight: string | number }
type StorageShop = Pick<GoodListType, 'shopId' | 'shopLogo' | 'shopName'>
export type StoragePackage = {
  activityParam?: { activityId: string; extra: any; type: 'SPIKE' | 'TEAM' | 'BARGAIN' | 'PACKAGE' }
  products: StorageProducts[]
} & StorageShop
type ProductsPickItem = 'id' | 'image' | 'price' | 'num' | 'productId' | 'productName' | 'specs' | 'skuId' | 'freightTemplateId' | 'salePrice'

enum Limit {
  UNLIMITED,
  PRODUCT_LIMITED,
  SKU_LIMITED,
}
enum Stock {
  UNLIMITED,
  LIMITED,
}
export type ApiGoodSku = {
  limitNum: number
  limitType: keyof typeof Limit
  stock: string
  stockType: keyof typeof Stock
}
export interface GoodItemSpec {
  productId: string
  productName: string
  id: string
  image: string
  price: number
  salePrice: number
  num: number
  specs: string[]
  freightTemplateId: string
  skuStock: ApiGoodSku
}
export interface GoodItemType extends GoodItemSpec {
  editedProductName?: string
  editedImage?: string
  editedPrice?: string
  editedSpecs?: string[]
  reason?: number
  isChecked: boolean
  skuId?: string
  isCountNumberComponentShow?: boolean
  uniqueId: string
}
export interface GoodListType {
  shopId: string
  shopName: string
  products: GoodItemType[]
  isAllChecked: boolean
  enable: boolean
  shopLogo: string
}
/**
 * @description: 策略模式
 * @param {array} productAmount
 * @param {string} productIds
 * @returns {*}
 */
const couponHandler: Record<keyof typeof ProductType, (productAmount: [string, Decimal], productIds: string[]) => boolean> = {
  ALL: () => true,
  SHOP_ALL: () => true,
  ALL_PRODUCT: () => true,
  ASSIGNED: (productAmount, productIds) => productIds.includes(productAmount[0]),
  ASSIGNED_NOT: (productAmount, productIds) => !productIds.includes(productAmount[0]),
}
const getShopDiscount = (productAmountMap: Map<string, Decimal>, coupon: ApiOrderCouponVO): Decimal => {
  const activeProductAmount = Array.from(productAmountMap.entries())
    .filter((productAmount) => couponHandler[coupon.productType](productAmount, coupon.productIds))
    .map((productAmount) => productAmount[1])
    .reduce((pre, cur) => pre.add(cur))
  return activeProductAmount.lessThan(coupon.discountAmount) ? activeProductAmount : new Decimal(coupon.discountAmount)
}
const getCalculatedPrice = (
  shopProducts: OrderType[],
  allCoupons: Map<string, ApiOrderCouponVO>,
  freightInfo: { [x: string]: string } | undefined,
): CostCalculate => {
  //所有商品的总价
  let totalAmount = new Decimal(0)
  //所有店铺的折扣价
  const shopDiscountMap = new Map<string, Decimal>()
  const productFreightMap = new Map<string, Decimal>()
  shopProducts.forEach((shop) => {
    const shopId = shop.shopId
    //所有商品的总价
    const productAmountMap = new Map<string, Decimal>()
    shop.products.forEach((product) => {
      const shopFreightKey = `${shopId}:${product.freightTemplateId}`
      const productId = product.productId
      const salePrice = new Decimal(product.salePrice).mul(product.num)
      const currentAmount = productAmountMap?.get(productId)
      if (freightInfo) {
        productFreightMap?.set(shopFreightKey, new Decimal(freightInfo?.[shopFreightKey] || 0))
      }
      productAmountMap?.set(productId, !currentAmount ? new Decimal(salePrice) : currentAmount.add(salePrice))
      totalAmount = totalAmount.add(salePrice)
    })
    const coupon = allCoupons.get(shopId)
    shopDiscountMap.set(shopId, !coupon ? new Decimal(0) : getShopDiscount(productAmountMap, coupon))
  })

  const shopDiscount = Array.from(shopDiscountMap.values()).reduce((pre, cur) => pre.add(cur))
  const platformDiscountDecimal = allCoupons.get('0')?.discountAmount
  const platformDiscount = !platformDiscountDecimal
    ? new Decimal('0')
    : totalAmount.lessThan(platformDiscountDecimal)
    ? totalAmount
    : platformDiscountDecimal
  const totalFreight = productFreightMap.size ? Array.from(productFreightMap.values()).reduce((pre, cur) => pre.add(cur)) : new Decimal(0)
  const payAmount = totalAmount.lessThan(shopDiscount.add(platformDiscount)) ? new Decimal(0) : totalAmount.sub(shopDiscount).sub(platformDiscount)
  return {
    totalAmount: totalAmount.toString(),
    shopDiscount: shopDiscount.toString(),
    platformDiscount: platformDiscount.toString(),
    totalFreight: totalFreight.toString(),
    payAmount: payAmount.toNumber() < 0 ? mulTenThousand(totalFreight).toString() : payAmount.add(mulTenThousand(totalFreight)).toString(),
  }
}
