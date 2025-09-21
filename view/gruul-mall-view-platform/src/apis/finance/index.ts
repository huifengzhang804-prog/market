import { get, post, put, del, patch } from '../http'

export const doGetFinance = (data: any) => {
    return post({
        url: 'gruul-mall-overview/overview/statement',
        data,
    })
}
/**
 * 获取提现工单
 */
export const doGetWithdrawList = (params: any): Promise<any> => {
    return get({
        url: 'gruul-mall-overview/overview/withdraw',
        params,
    })
}

/**
 * 结算/拒绝申请
 */
export const doGetCheckWithdraw = (orderNo: string, data: any) => {
    return put({
        url: `gruul-mall-overview/overview/withdraw/audit/${orderNo}`,
        data,
    })
}
