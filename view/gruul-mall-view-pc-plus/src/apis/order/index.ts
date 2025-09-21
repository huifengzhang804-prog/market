import { get, post, put, del, patch } from '../http'
import { OrderCount, OrderPaymentType } from './types'

/**
 * @description: 生成订单
 * @param {any} data
 */
export const doGenerateOrders = (data: any): Promise<any> => {
  return post({ url: 'gruul-mall-order/order', data })
}
/**
 * @description: 获取订单信息
 */
export const doGetOrder = (params: any) => {
  return get({ url: 'gruul-mall-order/order', params })
}
/**
 * @description: 获取已支付订单信息
 * @param {string} orderNo
 * @param {string} shopOrderNo
 * @returns {*}
 */
export const doGetHavePayOrder = (orderNo: string, shopOrderNo: string) => {
  return get({ url: `gruul-mall-order/order/${orderNo}/shopOrder/${shopOrderNo}` })
}
/**
 * @description: 获取订单详情信息
 * @param {string} orderNo
 * @param {string} shopId 传该值已支付
 * @returns {*}
 */
export const doGetOrderInfo = (orderNo: string, shopId?: string, packageId?: string, usePackage = true): Promise<any> => {
  const params: any = { shopId, usePackage }
  if (packageId) {
    params.packageId = packageId
  }
  return get({ url: `gruul-mall-order/order/${orderNo}`, params })
}
/**
 * @description:查询未支付的订单支付信息
 */
export const doGetOrderPayment = (orderNo: any) => {
  return get<OrderPaymentType>({ url: `gruul-mall-order/order/${orderNo}/payment` })
}
/**
 * @description: 查询创建情况
 */
export const doGetOrderCreateConditions = (orderNo: any) => {
  return get({ url: `gruul-mall-order/order/${orderNo}/creation` })
}
type PayType = { orderNo: string; payType: string }
/**
 * @description: 支付
 * @param {any} data
 * @returns {*}
 */
export const doGetOrderPayPage = (data: PayType) => {
  return post({ url: `gruul-mall-order/order/pay/page`, data })
}
/**
 * @description: 查询第一个已发货的包裹
 * @param {string} orderNo
 * @param {string} shopOrderNo
 * @param {string} packageId
 * @returns {*}
 */
export const doGetFirstDeliveryPage = (orderNo: string, shopOrderNo: string, packageId: string): Promise<any> => {
  return get({ url: `gruul-mall-order/order/${orderNo}/shopOrder/${shopOrderNo}/delivered/01`, params: { packageId } })
}
/**
 * @description: 查询所有已发货的包裹  outTradeNo
 * @param {string} orderNo
 * @param {string} shopOrderNo
 * @returns {*}
 */
export const doGetDeliveryPageAll = (orderNo: string, shopOrderNo: string, packageId: string) => {
  return get({ url: `gruul-mall-order/order/${orderNo}/shopOrder/${shopOrderNo}/delivered`, params: { packageId } })
}
/**
 * @description: 物流轨迹
 * @param {*} companyCode 物流公司名字
 * @param {*} waybillNo 物流单号
 * @returns {*}
 */
export const doGetLogisticsTrajectoryByWaybillNo = (companyCode: string, waybillNo: string, recipientsPhone?: string): Promise<any> => {
  return get({ url: 'gruul-mall-freight/logistics/node', params: { companyCode, waybillNo, recipientsPhone } })
}
/**
 * @description: 查询订单是否支付完成
 * @param {string} outTradeNo 订单号
 * @returns {*}
 */
export const doGetOrderIsPaySuccess = (outTradeNo: string) => {
  return get({ url: `gruul-mall-payment/merchant/pay/order/status`, params: { outTradeNo }, showLoading: false })
}
/**
 * @description: 订单修改地址
 * @param {object} params
 * @returns {*}
 */
export const doPutOrderReceiver = (
  orderNo: string,
  params: { shopOrderNo?: string; name: string; mobile: string; area: string[]; address: string },
) => {
  return put({ url: `gruul-mall-order/order/${orderNo}/receiver`, params })
}
/**
 * @description: 取消订单
 * @param {string} orderNo 订单号
 * @returns {*}
 */
export const doPutCloseOrderByOrderNo = (orderNo: string) => {
  return put({ url: `gruul-mall-order/order/${orderNo}/close` })
}
/**
 * @description: 订单备注
 * @param {string} ids  总订单号 或者 店铺订单号 平台端 为总订单号列表 商家端 为店铺订单号列表
 * @param {string} status
 */
export const doPutOrderRemark = (nos: string[], remark: string) => {
  return put({
    url: `gruul-mall-order/order/remark/batch`,
    params: {
      nos,
      remark,
    },
  })
}
/**
 * @description: 批量根据店铺id查询店铺交易设置
 * @param {string} ids
 * @returns {*}
 */
export const doGetOrderSettingsDealBatch = (ids: string[]): Promise<any> => {
  // return get({ url: `gruul-mall-shop/shop/deal/settings/get/batch`, params: { shopIds: ids.join() } })
  return get({ url: `gruul-mall-order/order/config/form/batch`, params: { shopIds: ids.join() } })
}
/**
 * @description: 订单统计
 * @returns {*}
 */
export const doGetMyCount = () => {
  return get<OrderCount>({ url: `gruul-mall-order/order/my/count` })
}
/**
 * @description: 查询运费
 * @returns {*}
 */
// export const doGetFreightCalculation = (data: any) => {
//     return post({ url: 'gruul-mall-freight/logistics/template/freightCalculation', data })
// }
export const doGetFreightCalculation = (data: any): Promise<any> => {
  return post({ url: 'gruul-mall-order/order/distribution/cost', data })
}

/**
 * @description: 积分订单信息 详情
 * @param
 * @returns {*}
 */
export const doGetIntegralOrderDetail = (on: string) => {
  return get({ url: `addon-integral/integral/order/get/${on}` })
}
/**
 * @description: 获取门店提货点 按距离排序
 * @returns {*}
 */
export const doPostStoreDistanceList = (data: any) => {
  return post({ url: `addon-shop-store/store/distance/list`, data })
}
/**
 * @description: 根据门店提货点查询提货时间
 * @param {string} shopId
 * @param {string} params
 * @returns {*}
 */
export const doGetDeliveryTime = (shopId: string, id: string): Promise<any> => {
  return get({ url: `addon-shop-store/store/optional/delivery/time/${shopId}?id=${id}` })
}
/**
 * @description: 获取门店核销码
 * @param {string} storeId
 * @param {string} orderNo
 * @returns {*}
 */
export const doGetOrderGetCodeByStoreId = (storeId: string, orderNo: string) => {
  return get({ url: `addon-shop-store/store/order/get/code/${storeId}/${orderNo}` })
}
