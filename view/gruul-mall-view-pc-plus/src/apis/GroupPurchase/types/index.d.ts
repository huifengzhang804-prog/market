export interface CouponListType {
  claimedStatus: string
  couponProductList: any[]
  createTime: string
  days?: number
  discount?: number
  effectType: string
  id: string
  name: string
  productType: string
  shopId: string
  type: string
  amount?: number
  endDate?: string
  startDate?: string
}

export interface CouponResponse {
  current: number
  isPlatform: boolean
  pages: number
  records: CouponListType[]
  shopId: string
  size: number
  status: string
  total: number
}
