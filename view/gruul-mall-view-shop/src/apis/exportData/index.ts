import { get, post, put, del } from '../http'
// 导出 订单中心
export const doGetExportData = (data: any) => {
    return post({
        url: 'gruul-mall-order/order/export',
        data,
    })
}
// 导出 售后工单
export const doPostAfsExport = (data: any) => {
    return post({
        url: 'gruul-mall-afs/afs/order/export',
        data,
    })
}
