import useConvert from '@/composables/useConvert'
import { ApiOrderCouponVO } from '@/types/coupon'
const { divTenThousand } = useConvert()
import type { FullReductionRules } from '@/views/settlement/plugins/fullDiscount/model'

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
  ASSIGNED = 'ASSIGNED',
  ASSIGNED_NOT = 'ASSIGNED_NOT',
}
export type ProductTypeJointType = keyof typeof ProductType
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

/**
 * @description:
 * @param couponUserId 用户与优惠券对应关系id
 * @param shopId
 * @param shopName
 * @param id 优惠券id
 * @param name 优惠券名称
 * @param type 优惠券类型
 * @param startDate 开始日期
 * @param endDate 结束日期
 * @param requiredAmount 满减/满折 优惠券需要的金额
 * @param amount 优惠额度
 * @param discount 优惠折扣
 * @param productType 商品类型
 * @param createTime 最后一条数据的创建时间（后端返回）
 */
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

// 满减优惠券
export function formatFullDiscountRules(cou: FullReductionRules | undefined | any) {
  let text
  if (!cou || !Object.values(cou).length) {
    text = ''
    return text
  }
  switch (cou.fullReductionRule) {
    case 'FULL_REDUCTION':
      text = `满${divTenThousand(cou.conditionAmount)}减${divTenThousand(cou.orderDiscount)}`
      break
    case 'FULL_DISCOUNT':
      text = `满${divTenThousand(cou.conditionAmount)}元打${cou.discountRatio}折`
      break
    default:
      break
  }
  return text
}
