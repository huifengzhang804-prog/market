import api from '@/libs/request'

const BASE_URL = 'addon-rebate/'
/**
 * 获取用户消费返利相关余额
 */
export const doGetRebateTransactions = () => {
  return api.get(BASE_URL + `rebateTransactions`)
}
/**
 * 获取返利订单列表
 */
export const doGetRebateOrderList = (data: any) => {
  return api.get(`${BASE_URL}rebateOrder`, data)
}
/**
 * 获取返利明细列表
 */
export const doGetRebateDetailsList = (data: any) => {
  return api.get(`${BASE_URL}rebateDetails`, data)
}
/**
 * 提现返利
 */
export const doPostWithdrawRebate = (data: any) => {
  return api.post(`${BASE_URL}rebateTransactions/withdraw`, data)
}

/**
 * 获取配置信息
 */
export const doRebateConf = () => {
  return api.get(`${BASE_URL}rebateConf`)
}

/**
 * 查询消费返利是否已禁用
 */
export const doGetRebateEnabled = () => {
  return api.get<boolean>(`${BASE_URL}/rebateTransactions/enabled`)
}
