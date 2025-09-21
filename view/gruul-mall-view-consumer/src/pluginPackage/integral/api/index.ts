import api from '@/libs/request'
import type { AddressItemType } from '@/apis/address/model'
import type { IIntegralOrderListParams, IIntegralReceiverData, IOrderList } from './types'
export interface DoPostIntegralOrderCreateParameter {
  source: 'PRODUCT'
  receiver: Omit<Omit<AddressItemType, 'id'>, 'isDefault'>
  buyerRemark: string
  productId: Long
  num: number
}

const INTEGRAL = 'addon-integral/integral/'
const PRODUCT_BASE_URL = INTEGRAL + 'product/'
const ORDER_BASE_URL = INTEGRAL + 'order/'
enum RULE_TYPE {
  SHARE = '分享',
  LOGIN = '登入',
  SING_IN = '签到',
}
/**
 * 获取积分规则信息
 * @param {any} params
 */
export const doGetIntegralRulesInfo = (params?: any) => {
  return api.get(`${INTEGRAL}rules/info`, params)
}
/**
 * 积分商品列表
 * @param {any} data
 */
export const doGetIntegralProductList = (params: any) => {
  return api.get(`${PRODUCT_BASE_URL}list`, params)
}
/**
 * 获取积分商品详细信息
 * @param {any} params
 */
export const doGetIntegralGoodsInfo = (id: Long) => {
  return api.get(`${PRODUCT_BASE_URL}info`, { id })
}
// 创建未支付的积分订单
/**
 * 创建未支付的积分订单
 * @param {any} params
 */
export const doPostIntegralOrderCreate = (data: DoPostIntegralOrderCreateParameter) => {
  return api.post(`${INTEGRAL}order/create`, data)
}
/**
 * 查询创建情况
 */
export const doGetOrderCreateConditions = (orderNo: any) => {
  return api.get(`${ORDER_BASE_URL}${orderNo}/creation`)
}
/**
 * 积分行为天数
 * @param {keyof} ruleType SHARE = '分享' LOGIN = '登入' SING_IN = '签到'
 */
export const doGetIntegralBehaviorDays = (ruleType?: keyof typeof RULE_TYPE) => {
  return api.get(`${INTEGRAL}behavior/days`, { ruleType })
}
/**
 * 查看剩余积分
 * @param {keyof} ruleType SHARE = '分享' LOGIN = '登入' SING_IN = '签到'
 */
export const doGetUserIntegralSystemtotal = () => {
  return api.get(`gruul-mall-user/user/integral/system/total`)
}
/**
 * 查看积分明细
 * @param
 */
export const doGetUserIntegralDetailInfo = (params: any) => {
  return api.get(`gruul-mall-user/user/integral/detail/info`, params)
}
/**
 * 积分订单列表 List
 * @param
 */
export const doGetIntegralOrderList = (params: IIntegralOrderListParams) => {
  return api.get(`addon-integral/integral/order/list`, params)
}

/**
 * 积分订单信息 详情
 * @param
 */
export const doGetIntegralOrderDetail = (on: string) => {
  return api.get<IOrderList>(`addon-integral/integral/order/get/${on}`)
}

/**
 * 修改积分地址
 * @param
 */
export const doGetIntegralReceiver = (on: string, data: IIntegralReceiverData) => {
  return api.put(`addon-integral/integral/${on}/receiver`, data)
}
/*
 * 确认收货
 * @param
 */
export const doPutCompleteIntegralOrder = (on: string) => api.put(`addon-integral/integral/order/deliver/complete/${on}`)

/* 
取消订单
*/
export const doDelIntegralOrder = (on: string) => api.post(`addon-integral/integral/order/close/${on}`)
