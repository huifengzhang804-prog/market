export interface CouponStatusType {
  status: string
  description: string
}

export const COUPON_STATUS: CouponStatusType[] = [
  {
    status: '待使用',
    description: '该优惠券未使用',
  },
  {
    status: '已使用',
    description: '该优惠券已被用于订单',
  },
  {
    status: '已过期',
    description: '优惠券已超过有效期且未使用',
  },
  {
    status: '平台赠送',
    description: '平台运营人员赠送的优惠券',
  },
  {
    status: '店铺赠送',
    description: '店铺运营人员赠送的优惠券',
  },
  {
    status: '手动领取',
    description: '用户自行领取的优惠券',
  },
  {
    status: '关联订单',
    description: '该优惠券在哪笔订单中使用',
  },
  {
    status: '赠送用户',
    description: '该优惠券是赠送给哪个用户门店的',
  },
]
