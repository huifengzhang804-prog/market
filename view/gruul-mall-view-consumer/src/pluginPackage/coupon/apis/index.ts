import api from '@/libs/request'
import type { CouponQueryDTO } from '@/apis/plugin/coupon/model'

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

/**
 * 结算获取优惠券列表
 * @param {any} data
 */
export const doGetOrderShopCouponPage = (data: any) => {
  return api.post(`addon-coupon/coupon/consumer/order`, data)
}

/**
 * 商品详情页获取优惠券列表
 * @param {Long} shopId
 * @param {any} data
 */
export const doGetGoodsDetailsCouponPage = (data: { shopId: Long; productId: Long; amount: Long; size?: number; current?: number }) => {
  return api.post(`addon-coupon/coupon/consumer/product`, data)
}
