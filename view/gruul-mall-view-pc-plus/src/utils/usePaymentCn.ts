import { ORDERPAYMENT } from '@/views/personalcenter/order/myorder/types'
export const payType: Record<keyof typeof ORDERPAYMENT, string> = {
  WECHAT: '微信',
  ALIPAY: '支付宝',
  BALANCE: '余额',
}
export const usePaymentCn = (str: keyof typeof ORDERPAYMENT = 'WECHAT') => {
  return payType[str]
}
