import { get, post, put, del, patch } from '../http'
// 获取优惠券列表
export const doGetCouponList = (params: any) => {
  return get({ url: `addon-coupon/coupon/consumer`, params })
}

/**
 * @description: 结算获取优惠券列表
 * @param {string} shopId 查平台传 0
 * @param {any} data
 * @returns {*}
 */
export const doGetOrderShopCouponPage = (data: any): Promise<any> => {
  return post({ url: `addon-coupon/coupon/consumer/order`, data })
}
/**
 * @description: 结算页满减信息
 * @param {} data
 */
export const doPostFullReductionConfirmOrder = (data: any) => {
  return post({ url: `addon-full-reduction/fullReduction/consumer/confirm/order`, data })
}
