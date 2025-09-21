import { get, post, put, del, patch } from '../http'
import { IIntegralOrderListParams } from '@/views/integralShop/index'
const INTEGRAL = 'addon-integral/integral/'
const PRODUCT_BASE_URL = INTEGRAL + 'product/'
const ORDER_BASE_URL = INTEGRAL + 'order/'
interface AddressItemType {
  id: string
  address: string
  area: string[]
  isDefault: boolean
  mobile: string
  name: string
  areaStr?: string
}
interface DoPostIntegralOrderCreateParameter {
  source: string
  receiver: Omit<Omit<AddressItemType, 'id'>, 'isDefault'>
  buyerRemark: string
  productId: string
  num: number
}
/**
 * @description: 积分商品列表
 * @param {any} data
 * @returns {*}
 */
export const doGetIntegralProductList = (params: any) => {
  return get({ url: `${PRODUCT_BASE_URL}list`, params })
}
/**
 * @description: 获取积分商品详细信息
 * @param {any} params
 * @returns {*}
 */
export const doGetIntegralGoodsInfo = (id: string): Promise<any> => {
  return get({ url: `${PRODUCT_BASE_URL}info?id=${id}` })
}

/**
 * @description: 查看积分明细
 * @param
 * @returns {*}
 */
export const doGetUserIntegralDetailInfo = (params: any) => {
  return get({ url: `gruul-mall-user/user/integral/detail/info`, params })
}
/**
 * @description: 查看剩余积分
 * @param {keyof} ruleType SHARE = '分享' LOGIN = '登入' SING_IN = '签到'
 * @returns {*}
 */
export const doGetUserIntegralSystemtotal = (): Promise<any> => {
  return get({ url: `gruul-mall-user/user/integral/system/total` })
}
// export enum RULE_TYPE {
//     SHARE = '分享',
//     LOGIN = '登入',
//     SING_IN = '签到',
// }
/**
 * @description: 积分行为天数
 * @param {keyof} ruleType SHARE = '分享' LOGIN = '登入' SING_IN = '签到'
 * @returns {*}
 */
export const doGetIntegralBehaviorDays = (params?: string) => {
  return get({ url: `${INTEGRAL}behavior/days?ruleType=${params}` })
}
/**
 * @description: 积分行为触发
 * @param {keyof} ruleType SHARE = '分享' LOGIN = '登入' SING_IN = '签到'
 */
export const doGetIntegralBehaviorSave = (ruleType: string) => {
  return get({ url: `${INTEGRAL}behavior/save?ruleType=${ruleType}` })
}
/**
 * @description: 获取积分规则信息
 * @param {any} params
 * @returns {*}
 */
export const doGetIntegralRulesInfo = (params?: any) => {
  return get({ url: `${INTEGRAL}rules/info`, params })
}
/**
 * @description: 积分订单列表 List
 * @param
 * @returns {*}
 */
export const doGetIntegralOrderList = (params: IIntegralOrderListParams) => {
  return get({ url: `addon-integral/integral/order/list`, params })
}
/*
 * @description: 确认收货
 * @param
 * @returns {*}
 */
export const doPutCompleteIntegralOrder = (on: string) => put({ url: `addon-integral/integral/order/deliver/complete/${on}` })
/* 
取消订单
*/
export const doDelIntegralOrder = (on: string) => post({ url: `addon-integral/integral/order/close/${on}` })
/**
 * @description: 物流轨迹
 * @param {*} companyCode 物流公司名字
 * @param {*} waybillNo 物流单号
 * @returns {*}
 */
export const doGetLogisticsTrajectoryByWaybillNo = (companyCode: string, waybillNo: string, shopId: string, recipientsPhone?: string) => {
  return get({
    url: `gruul-mall-freight/logistics/node?companyCode=${companyCode}&waybillNo=${waybillNo}&recipientsPhone=${recipientsPhone}&${
      shopId ? { 'Shop-Id': shopId } : {}
    }`,
  })
}
// 支付方式
enum ORDERPAYMENT {
  WECHAT = 'WECHAT',
  ALIPAY = 'ALIPAY',
  BALANCE = 'BALANCE',
}
/**
 * @description: 积分订单支付
 * @param BALANCE
 * @returns {*}
 */
export const doPostIntegralOrderPay = (integralOrderNo: string, payType: keyof typeof ORDERPAYMENT) => {
  return post({ url: `${INTEGRAL}order/pay/get`, data: { integralOrderNo, payType } })
}
// 创建未支付的积分订单
/**
 * @description: 创建未支付的积分订单
 * @param {any} params
 * @returns {*}
 */
export const doPostIntegralOrderCreate = (data: DoPostIntegralOrderCreateParameter) => {
  console.log(data, 'data')
  return post({ url: `${INTEGRAL}order/create`, data })
}
/**
 * @description: 查询创建情况
 */
export const doGetOrderCreateConditions = (orderNo: any) => {
  return get({ url: `${ORDER_BASE_URL}${orderNo}/creation` })
}
