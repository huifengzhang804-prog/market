import { get, put, post } from '../http'
/**
 * 订单备注
 * @param {string} ids  总订单号 或者 店铺订单号 平台端 为总订单号列表 商家端 为店铺订单号列表
 * @param {string} status
 */
export const doPutOrderRemark = (nos: string[], remark: string) => {
    return put({
        url: `gruul-mall-order/order/remark/batch`,
        data: {
            nos,
            remark,
        },
    })
}
/**
 * 售后订单备注
 * @param {string} ids
 * @param {string} status
 */
export const doPutAfsOrderRemark = (nos: string[], remark: string) => {
    return put({
        url: `gruul-mall-afs/afs/order/remark/batch`,
        data: {
            nos,
            remark,
        },
    })
}
/**
 * 提现工单备注
 */
export const doPutWithdrawRemark = (nos: string[], remark: string) => {
    return put({
        url: 'gruul-mall-overview/overview/withdraw/batch/remark',
        data: {
            nos,
            remark,
        },
    })
}
/**
 * 储值订单批量备注
 */
export const doPostpaymentHistoryRemark = (ids: string[], remark: string) => {
    return put({
        url: 'gruul-mall-payment/user/payment/history/savings/order/remark',
        data: {
            ids,
            remark,
        },
    })
}
/**
 * 积分订单批量备注
 */
export const doPostIntegralOrderRemark = (nos: string[], remark: string) => {
    return put({
        url: 'addon-integral/integral/order/remark/batch',
        data: {
            nos,
            remark,
        },
    })
}
