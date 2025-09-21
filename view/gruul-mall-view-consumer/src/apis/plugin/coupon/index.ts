import api from '@/libs/request'
import type { CouponQueryDTO } from './model'

/**
 * 获取优惠券列表
 * @param {} params
 */
export const doGetCouponList = (params: CouponQueryDTO) => {
  return api.get(`addon-coupon/coupon/consumer`, params)
}
/**
 * 用户领取优惠券
 * @param {Long} shopId
 * @param {string} couponId
 */
export const doPostConsumerCollectCoupon = (shopId: Long, couponId: string) => {
  return api.post(`addon-coupon/coupon/consumer/collect/shop/${shopId}/${couponId}`)
}
