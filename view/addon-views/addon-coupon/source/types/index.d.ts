import { CouponType } from '../'

export interface ResData {
  current: number
  extraMap: { [key: string]: any }
  keywords: string
  optimizeCountSql: boolean
  orders: any[]
  pages: number
  records: Coupon[]
  searchCount: boolean
  size: number
  total: number
}

export interface Coupon {
  amount?: string
  createTime?: string
  effectType?: string
  endDate?: string
  id: string
  limit?: number
  name?: string
  num?: string
  platformDeleteFlag?: boolean
  productType?: string
  shopDeleteFlag?: boolean
  shopId?: string
  startDate?: string
  status?: string
  stock?: string
  type: keyof typeof CouponType
  usedCount?: string
}

/**
 * 优惠券记录类型
 */
export interface VoucherRecordType {
  collectType: string
  collectTypeText: string
  couponUserId: string
  createTime: string
  discount: number
  effectType: string
  endDate: string
  id: string
  name: string
  parValue: string
  queryStatus: string
  queryStatusText: string
  shopId: string
  type: string
  typeDescription: string
  used: boolean
  userId: string
  userNickname: string
  userPhone: string
}

export interface VoucherRecordSearchForm {
  /**
   * 查询状态
   */
  status: string

  /**
   * 关联使用优惠券的订单编号
   */
  associatedOrderNo: string

  /**
   * 优惠券名称
   */
  name: string

  /**
   * 优惠券用户手机号
   */
  userPhone: string

  /**
   * 赠券用户手机号
   */
  giftUserPhone: string

  /**
   * 优惠券ID
   */
  couponUserId: string
}
