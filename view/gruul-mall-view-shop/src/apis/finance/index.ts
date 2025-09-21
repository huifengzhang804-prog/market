import { get, post, put, del, patch } from '../http'
export const doGetFinance = (data: any): Promise<any> => {
    return post({
        url: 'gruul-mall-overview/overview/statement',
        data,
    })
}
/**
 * 查询店铺余额
 */
export const doGetShopBalance = (): Promise<any> => {
    return get({
        url: 'gruul-mall-overview/overview/shop/balance',
    })
}
/**
 * 获取店铺提现申请列表
 */

export const doGetWithdrawList = (params: any): Promise<any> => {
    delete params.date
    return get({
        url: 'gruul-mall-overview/overview/withdraw/shop',
        params,
    })
}
/**
 * 提现申请
 */
export const doPostWithdraw = (amount: number, type: string) => {
    return post({
        url: 'gruul-mall-overview/overview/shop/balance/withdraw',
        data: {
            amount,
            type,
        },
    })
}
