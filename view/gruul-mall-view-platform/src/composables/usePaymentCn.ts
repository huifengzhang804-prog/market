import { ORDERPAYMENT } from '@/views/order/types/order'
const payType: Record<keyof typeof ORDERPAYMENT, string> = {
    WECHAT: '微信',
    ALIPAY: '支付宝',
    BALANCE: '储值',
}
export const payTypeFn = (str: keyof typeof ORDERPAYMENT) => {
    return payType[str]
}
