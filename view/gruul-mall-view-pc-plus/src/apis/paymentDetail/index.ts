import { get, post, put, del, patch } from '../http'
/**
 * @description: 获取用户支付历史记录
 * @param {any} params
 */
export const doGetPaymentHistory = (params: any) => {
  return get({ url: 'gruul-mall-payment/user/payment/history', params })
}
/**
 * @description: 获取储值管理信息
 * @param {any} params
 */
export const doGetSavingManage = () => {
  return get({ url: 'gruul-mall-user/user/saving/manage/get' })
}
/**
 * @description: 用户储值充值
 * @param {*} payType 支付渠道< BALANCE WECHAT ALIPAY
 * @param {*} payAmount 支付金额
 * @param {*} ruleId 储值规则id
 * @returns {*}
 */
export const doGetSavingPay = (payType: 'WECHAT' | 'ALIPAY', payAmount: any, ruleId: string) => {
  return post({ url: 'gruul-mall-user/user/saving/pay', data: { payType, payAmount: payAmount * 10000, ruleId } })
}
