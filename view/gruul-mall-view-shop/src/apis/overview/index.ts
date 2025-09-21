import { del, get, post } from '../http'
export enum DATE_TYPE {
    TODAY,
    NEARLY_A_WEEK,
    NEARLY_A_MONTH,
    CUSTOM,
}
type TradeStatisticsParams = {
    startDate: string
    endDate: string
    dateType: keyof typeof DATE_TYPE
}

interface VisitNumberResponse {
    code: number
    data: any
    msg?: string
    success: boolean
}
/**
 * 获取访客数量
 */
export const doGetVisitNumber = (): Promise<VisitNumberResponse> => {
    return get({
        url: 'gruul-mall-shop/shop/visitor/get',
    })
}
/**
 * 获取新增商品数量
 */
export const doGetNewCommodityNumber = (): Promise<VisitNumberResponse> => {
    return get({
        url: '/gruul-mall-goods/manager/product/today/quantity',
    })
}
/**
 * 获取商品交易量
 * @param {Partial} params
 */
export const doGetTradeVolume = (params: Partial<TradeStatisticsParams>): Promise<VisitNumberResponse> => {
    return get({
        url: '/gruul-mall-overview/overview/deal/product/sales',
        params,
    })
}
/**
 * 获取商品交易额
 */
export const doGetTransactionAmount = (params: Partial<TradeStatisticsParams>): Promise<VisitNumberResponse> => {
    return get({
        url: '/gruul-mall-overview/overview/deal/product/money',
        params,
    })
}
/**
 * 获取交易统计
 */
export const doGetTradeStatistics = (params: Partial<TradeStatisticsParams>): Promise<VisitNumberResponse> => {
    return get({
        url: '/gruul-mall-overview/overview/deal/statistics',
        params,
    })
}
/**
 * 获取订单数量
 */
export const doGetOrderInfo = (): Promise<VisitNumberResponse> => {
    return get({
        url: 'gruul-mall-order/order/overview/shop',
    })
}
/**
 * 获取下载中心列表
 * @param params 参数
 */
export const doGetDownloadCenterList = (params: any = {}): Promise<any> => {
    return get({ url: 'gruul-mall-overview/export/list', params })
}
/**
 * 删除下载中心列表
 * @param id 下载中心列表id
 */
export const doDeleteDownloadCenterFile = (id: string) => {
    return del({ url: `gruul-mall-overview/export/${id}/remove` })
}
/**
 * 批量删除下载中心列表
 * @param data 数据信息
 */
export const doDeleteBatchDownloadCenterFiles = (data: any) => {
    return del({ url: 'gruul-mall-overview/export/batchRemove', data })
}
/**
 * 获取下载中的数量
 */
export const doGetDownloadingFileCount = () => {
    return get({ url: 'gruul-mall-overview/export/count' })
}
/**
 * 导出对账单
 * @param data
 */
export const doPostExportStatementData = (data: any) => {
    return post({ url: 'gruul-mall-overview/overview/statement/export', data })
}
/**
 * 导出店铺结算列表
 * @param data
 */
export const doPostExportShopWithdrawData = (data: any) => {
    return post({ url: 'gruul-mall-overview/overview/withdraw/shop/export', data })
}
