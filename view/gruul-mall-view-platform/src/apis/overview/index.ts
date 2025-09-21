import { get, del, post } from '../http'
import { L } from '../http.type'
import { DownloadList } from './types'

export enum DATE_TYPE {
    TODAY,
    NEARLY_A_WEEK,
    NEARLY_A_MONTH,
    CUSTOM,
}
export enum ShopMode {
    COMMON = 'COMMON',
    SUPPLIER = 'SUPPLIER',
    O2O = 'O2O',
}

type TradeStatisticsParams = {
    startDate: string
    endDate: string
    dateType: keyof typeof DATE_TYPE
    shopMode: keyof typeof ShopMode
}
/**
 * 获取热门店铺top5
 */
export const doGetShopStatistics = (params: Partial<TradeStatisticsParams>): Promise<any> => {
    return get({
        url: '/gruul-mall-overview/overview/deal/shop',
        params,
    })
}
/**
 * 获取热门商品top5
 * @param {Partial} params
 */
export const doGetCommodityStatistics = (params: Partial<TradeStatisticsParams>): Promise<any> => {
    return get({
        url: '/gruul-mall-overview/overview/deal/product/sales',
        params,
    })
}
/**
 * 获取交易统计
 */
export const doGetTradeStatistics = (params: Partial<TradeStatisticsParams>): Promise<any> => {
    return get({
        url: '/gruul-mall-overview/overview/deal/statistics',
        params,
    })
}
/**
 * 获取商品数量/违规商品
 */
export const doGetCommodityNumber = () => {
    return get<Obj>({
        url: '/gruul-mall-goods/manager/product/quantity',
    })
}
/**
 * 获取新增商品数量
 */
export const doGetNewCommodityNumber = (): Promise<any> => {
    return get({
        url: '/gruul-mall-goods/manager/product/today/quantity',
    })
}
/**
 * 获取店铺数量和待审核数量
 */
export const doGetShopNumber = (): Promise<any> => {
    return get({
        url: '/gruul-mall-shop/shop/shopQuantity',
    })
}
/**
 * 获取今日新增店铺数量
 */
export const doGetNewShopNumber = (): Promise<any> => {
    return get({
        url: 'gruul-mall-shop/shop/today/shopQuantity',
    })
}

/**
 * 获取访客数量
 */
export const doGetVisitNumber = (): Promise<any> => {
    return get({
        url: 'gruul-mall-user/user/uv',
    })
}
/**
 * 获取订单数量
 */
export const doGetOrderCount = (): Promise<any> => {
    return get({
        url: 'gruul-mall-order/order/overview/platform',
    })
}
/**
 * @description 获取下载中心列表
 * @param params 参数
 */
export const doGetDownloadCenterList = (params: Obj = {}) => {
    return get<L<DownloadList>>({ url: 'gruul-mall-overview/export/list', params })
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
    return get<number>({ url: 'gruul-mall-overview/export/count' })
}

/**
 * @description 导出对账单
 */
export const doPostExportStatementData = (data: any) => {
    return post({ url: 'gruul-mall-overview/overview/statement/export', data })
}
/**
 * @description 导出提现工单
 */
export const doPostExportWithdrawData = (data: any) => {
    return post({ url: 'gruul-mall-overview/overview/withdraw/export', data })
}
