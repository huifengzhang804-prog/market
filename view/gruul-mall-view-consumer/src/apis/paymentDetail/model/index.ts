enum PAY_TYPE {
  ORDER = 'ORDER',
  BALANCE = 'BALANCE',
  MEMBER = 'MEMBER',
  INTEGRAL = 'INTEGRAL',
}
type PayType = keyof typeof PAY_TYPE
// 支付方式
enum ORDERPAYMENT {
  WECHAT = 'WECHAT',
  ALIPAY = 'ALIPAY',
  BALANCE = 'BALANCE',
}
export { PAY_TYPE, type PayType, ORDERPAYMENT }
