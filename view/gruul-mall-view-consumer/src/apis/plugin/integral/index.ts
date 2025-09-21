import api from '@/libs/request'
import { ORDERPAYMENT } from '@/apis/paymentDetail/model'

const INTEGRAL = 'addon-integral/integral/'
enum RULE_TYPE {
  SHARE = '分享',
  LOGIN = '登入',
  SING_IN = '签到',
}
/**
 * 积分行为触发
 * @param {keyof} ruleType SHARE = '分享' LOGIN = '登入' SING_IN = '签到'
 */
export const doGetIntegralBehaviorSave = (ruleType: keyof typeof RULE_TYPE) => {
  return api.get(`${INTEGRAL}behavior/save`, { ruleType })
}
/**
 * 积分订单支付
 * @param BALANCE
 */
export const doPostIntegralOrderPay = (integralOrderNo: string, payType: keyof typeof ORDERPAYMENT) => {
  return api.post(`${INTEGRAL}order/pay/get`, { integralOrderNo, payType })
}

/**
 * 查询积分订单是否支付完成
 * @param {string} outTradeNo 订单号
 */
export const doGetIntegralOrderIsPaySuccess = (outTradeNo: string) => {
  return api.get(`${INTEGRAL}order/pay/success/${outTradeNo}`)
}
