import { get, post, put, del, patch } from '@/apis/http'
import { ResData, VoucherRecordType } from './types'
import { L } from '@/utils/types'

const baseUrl = 'addon-coupon/coupon/'
/**
 * 优惠券列表
 */
export const doGetCouponList = (params) => {
  return get<ResData>({
    url: 'addon-coupon/coupon',
    params,
  })
}

/**
 * 删除优惠券
 */
export const doDelCoupon = (ids: string[]) => {
  return del({
    url: `addon-coupon/coupon/shop/batch`,
    data: ids,
  })
}

/**
 * 平台删除优惠券
 */
export const doDelCouponByPlatform = (couponKeys: { shopId: string; couponId: string }[]) => {
  return del({
    url: `addon-coupon/coupon/batch`,
    data: couponKeys,
  })
}

/**
 * 添加优惠券
 */
export const doAddonCoupon = (data) => {
  return post({
    url: 'addon-coupon/coupon',
    data,
  })
}
/**
 * 获取优惠券信息
 */
export const doGetCouponInfoById = (shopId: string, couponId: string): Promise<any> => {
  return get({
    url: `addon-coupon/coupon/shop/${shopId}/${couponId}`,
  })
}
/**
 * 编辑优惠券
 */
export const doPostonCouponEditByCouponId = (couponId: string, data) => {
  return put({
    url: `addon-coupon/coupon/${couponId}`,
    data,
  })
}

/**
 * 编辑优惠券
 */
export const doPostCouponWorkingEditByCouponId = (couponId: string, data) => {
  return put({
    url: `addon-coupon/coupon/${couponId}/working`,
    data,
  })
}
/**
 * 商家端指定用户发送优惠券
 */
export const doPostCouponShopGifts = (data: any) => {
  return post({ url: 'addon-coupon/coupon/gifts', data })
}

/**
 * 批量下架
 */
export const doPutBanCoupon = (ids: { shopId: string; couponId: string }[]) => {
  return put({
    url: `addon-coupon/coupon/ban/batch`,
    data: ids,
  })
}

/**
 * 单个下架
 */
export const doPutBanCouponSingle = (data: { shopId: string; couponId: string }) => {
  return put({
    url: `addon-coupon/coupon/shop/offShelf`,
    data,
  })
}

/**
 * 违规下架
 */
export const doIllegalDelisting = (data: { shopId: string; couponId: string; violationReason: string }) => {
  return put({
    url: `addon-coupon/coupon/ban/batch`,
    data,
  })
}

/**
 * 违规原因
 */
export const doGetIllegalReason = (id: string): Promise<any> => {
  return get({
    url: `${baseUrl}illegal/${id}/reason`,
  })
}

/* pc */
// 获取优惠券列表
export const doGetCouponListPc = (params: any) => {
  return get({ url: `addon-coupon/coupon/consumer`, params })
}
//  用户领取优惠券
export const doPostConsumerCollectCouponPc = (shopId: string, couponId: string) => {
  return post({ url: `addon-coupon/coupon/consumer/collect/shop/${shopId}/${couponId}` })
}

/**
 * 结算获取优惠券列表
 * @param {string} shopId 查平台传 0
 */
export const doGetOrderShopCouponPagePc = (data: any) => {
  return post({ url: `addon-coupon/coupon/consumer/order`, data })
}

/**
 * 获取违规原因
 */
export const doGetCouponeason = (couponId: string) => {
  return get({
    url: `addon-coupon/coupon/illegal/${couponId}/reason`,
  })
}
/**
 * 店铺下架优惠卷
 */
export const doPutCoupon = (data: any) => {
  return put({
    url: `addon-coupon/coupon/shop/offShelf`,
    data,
  })
}

/**
 * 获取优惠券记录
 */
export const doGetCouponRecord = (params: any) => {
  return get<L<VoucherRecordType>>({
    url: `addon-coupon/coupon/consumer/use/record`,
    params,
  })
}

/**
 * 导出优惠券记录
 */
export const doPostExportCouponRecord = (data: any) => {
  return post({ url: `addon-coupon/coupon/consumer/export`, data })
}
