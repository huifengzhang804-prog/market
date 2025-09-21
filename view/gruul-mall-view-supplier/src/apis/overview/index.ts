import { del, get, post } from '../http'
export enum DATE_TYPE {
    TODAY,
    NEARLY_A_WEEK,
    NEARLY_A_MONTH,
    CUSTOM,
}
type TradeStatisticsParams = {
    beginTime: string
    endTime: string
    dateRangeType: keyof typeof DATE_TYPE
    tradeStaticType: 'TRADE_NUMBER' | 'TRADE_AMOUNT'
}

/**
 * @description: 获取访客数量
 */
export const doGetVisitNumber = () => {
    return get({
        url: `addon-supplier/supplier-overview/inquiry-number`,
    })
}
/**
 * @description: 获取新增商品数量
 */
export const doGetNewCommodityNumber = () => {
    return get({
        url: `addon-supplier/supplier-overview/new-created-product/TODAY`,
    })
}
/**
 * @description:获取商品交易量
 * @param {Partial} params
 */
export const doGetTradeVolume = (params: Partial<TradeStatisticsParams>) => {
    return get({
        url: 'addon-supplier/supplier-overview/supplier-goods-trade-num',
        params,
    })
}
/**
 * @description: 获取商品交易额
 */
export const doGetTransactionAmount = (params: Partial<TradeStatisticsParams>) => {
    return get({
        url: 'addon-supplier/supplier-overview/supplier-goods-trade-amount',
        params,
    })
}
/**
 * @description: 获取交易统计
 */
export const doGetTradeStatistics = (params: Partial<TradeStatisticsParams>) => {
    return get({
        url: 'addon-supplier/supplier-overview/supplier-trade',
        params,
    })
}
/**
 * @description: 获取订单数量
 */
export const doGetOrderInfo = () => {
    return get({
        url: 'addon-supplier/supplier-overview/to-do-list/TODAY',
    })
}
/**
 * @description 获取下载中心列表
 * @param params 参数
 */
export const doGetDownloadCenterList = (params: any = {}): Promise<any> => {
    return get({ url: 'gruul-mall-overview/export/list', params })
}
/**
 * @description 删除下载中心列表
 * @param id 下载中心列表id
 */
export const doDeleteDownloadCenterFile = (id: string) => {
    return del({ url: `gruul-mall-overview/export/${id}/remove` })
}
/**
 * @description 批量删除下载中心列表
 * @param data 数据信息
 */
export const doDeleteBatchDownloadCenterFiles = (data: any) => {
    return del({ url: 'gruul-mall-overview/export/batchRemove', data })
}

/**
 * @description 获取下载中的数量
 */
export const doGetDownloadingFileCount = () => {
    return get({ url: 'gruul-mall-overview/export/count' })
}

/**
 * @description 导出对账单
 */
export const doPostExportStatementData = (data: any) => {
    return post({ url: 'gruul-mall-overview/overview/statement/export', data })
}

/**
 * @description 导出供应商结算列表
 */
export const doPostExportSupplierWithdrawData = (data: any) => {
    return post({ url: 'gruul-mall-overview/overview/withdraw/supplier/export', data })
}
