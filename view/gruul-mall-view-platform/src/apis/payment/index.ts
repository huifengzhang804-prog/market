import { PaymentHistoryItem } from '@/views/payment/types'
import { get, post, put, del, patch } from '../http'
import { L } from '../http.type'
/**
 *
 * @param 开始时间
 * @param 结束时间
 * @param 会员昵称
 */
interface PaymentParams {
    startTime: string
    endTime: string
    userNickname: string
    size: number
    current: number
}
/**
 * 储值订单列表
 * @param {any} data
 */
export const doGetPaymentHistory = (data?: PaymentParams) => {
    return get<L<PaymentHistoryItem>>({
        url: 'gruul-mall-payment/user/payment/history/savings/order',
        params: data,
    })
}
