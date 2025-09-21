import { get, post, put, del, patch } from '../http'
import { R } from '../http.type'
import { CouponResponse } from './types'
// 分页查询拼团活动商品列表
export const groupListApi = (params?: any) => {
  return get({ url: 'addon-team/team/product', params })
}
// 获取商品拼团状态与数据
export const groupPurchaseNum = (shopId: string, productId: string): Promise<any> => {
  return get({ url: `addon-team/team/product/${shopId}/${productId}` })
}
// 倒计时
export const timerApi = (shopId: string, productId: string) => {
  return get({ url: `gruul-mall-search/search/product/activity/${shopId}/${productId}` })
}
// 分页查询商品凑团订单列表
export const ordersApi = (shopId: string, productId: string, current: number, size: number) => {
  return get({ url: `addon-team/team/product/${shopId}/${productId}/orders?current=${current}&size=${size}` })
}
// 获取会员中心信息
export const doGetMemberCardInfo = () => {
  return get({ url: 'gruul-mall-user/user/member/card/info' })
}

// 获取优惠券列表
export const doGetCouponList = (params: any) => {
  return get<CouponResponse>({ url: `addon-coupon/coupon/consumer`, params })
}
