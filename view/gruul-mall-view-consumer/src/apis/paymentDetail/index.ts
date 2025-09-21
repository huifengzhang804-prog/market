import api from '@/libs/request'

/**
 * 获取用户支付历史记录
 * @param {any} params
 */
export const doGetPaymentHistory = (params: any) => {
  return api.get('gruul-mall-payment/user/payment/history', params)
}
/**
 * 获取储值管理信息
 * @param {any} params
 */
export const doGetSavingManage = () => {
  return api.get('gruul-mall-user/user/saving/manage/get')
}
/**
 * 用户储值充值
 * @param {*} payType 支付渠道< BALANCE WECHAT ALIPAY
 * @param {*} payAmount 支付金额
 * @param {*} ruleId 储值规则id
 */
export const doGetSavingPay = (payType: 'WECHAT' | 'ALIPAY', payAmount: any, ruleId: string) => {
  return api.post('gruul-mall-user/user/saving/pay', { payType, payAmount, ruleId })
}
