import type { Fn } from '@/utils/types'
import type { Ref } from 'vue'
import type { DiscountPublicAttr } from '@plugin/types'

/**
 * @param PRICE_REDUCE 无门槛现金券
 * @param PRICE_DISCOUNT 无门槛折扣券
 * @param REQUIRED_PRICE_REDUCE 满减券
 * @param REQUIRED_PRICE_DISCOUNT 满折券
 */
enum CouponType {
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
enum EffectType {
  PERIOD = 'PERIOD',
  IMMEDIATELY = 'IMMEDIATELY',
}
type EffectTypeJointType = keyof typeof EffectType
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
enum CouponQueryStatus {
  UNUSED = 'UNUSED',
  UNCLAIMED = 'UNCLAIMED',
  USED = 'USED',
  EXPIRED = 'EXPIRED',
}
export type TagItem = { name: string; key: CouponQueryStatusJointType }
export type CouponQueryStatusJointType = keyof typeof CouponQueryStatus
/**
 * 查询条件 / 参数
 * @param isPlatform 是否查询平台优惠券
 * @param shopId 进店铺时传递
 * @param lastCreateTime 第一页无需传 最后一条数据的创建时间（后端返回）
 * @param EXPIRED 已过期
 */
export interface CouponQueryDTO extends PageConfig {
  isPlatform: boolean
  shopId?: Long
  status: CouponQueryStatusJointType
  lastCreateTime?: string
}
interface PageConfig {
  size: number
  current: number
}
/**
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
  shopId: Long
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
  stock: string
  claimedStatus: keyof typeof CLAIMED_STATUS
}

export interface ApiOrderCouponVO extends ApiCouponVO {
  discountAmount: string
  productIds: Long[]
}
/**
 * 结算查询参数
 * @param {productId}
 * @param {amount}
 */
export interface ProductAmount {
  productId: Long
  amount: string
}
export interface CartApiCouponVO extends ApiCouponVO {
  watermark: boolean
  discountAmount: string
  claimedStatus: keyof typeof CLAIMED_STATUS
}
export enum CLAIMED_STATUS {
  CLAIM = '未领取',
  LIMIT = '已领取',
  FINISHED = '已抢光',
}
export interface CouponConfig {
  style: string
  bgIndex: number
  bg: string
  hideCoupon: boolean
}

export interface GetCouponListParams {
  shopId: Long
  amount: Long
  productId: Long
}

export interface CouponDispatcherType extends DiscountPublicAttr {
  doRequest: Fn
  initGoodsDetailCouponList: (isLoad: boolean, options: GetCouponListParams) => Promise<void>
  goodsDetailCouponList: Ref<CartApiCouponVO[]>
}
