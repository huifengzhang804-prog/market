import { get, post, put, del, patch } from '../http'

/**
 * 售后工单列表
 */
export const doGetAfsList = (data?: any) => {
    return get({
        url: 'gruul-mall-afs/afs/order',
        params: data,
    })
}
/**
 * 通过orderNo获取订单详情
 */
export const doGetOrderDetails = (orderNo: string, params: any): Promise<any> => {
    return get({ url: `/gruul-mall-order/order/${orderNo}`, params })
}
/**
 * 协商历史
 */
export const doGetAfssHistory = <T>(afsNo: string) => {
    return get<T[]>({
        url: `gruul-mall-afs/afs/order/${afsNo}/history`,
    })
}
/**
 * 查询买家昵称与头像
 */
export const doGetUserDataByBuyerId = (userId: string) => {
    return get({
        url: `gruul-mall-uaa/uaa/user/data/${userId}`,
    })
}
/**
 * 查询第一个已发货的包裹
 */
export const doGetFirstDeliveryPage = (orderNo: string, shopOrderNo: string, packageId: string): Promise<any> => {
    return get({ url: `gruul-mall-order/order/${orderNo}/shopOrder/${shopOrderNo}/delivered/01`, params: { packageId } })
}
/**
 * 物流轨迹
 */
export const doGetLogisticsTrajectoryByWaybillNo = (
    companyCode: string,
    waybillNo: string,
    packageId?: string,
    recipientsPhone?: string,
): Promise<any> => {
    return get({ url: 'gruul-mall-freight/logistics/node', params: { companyCode, waybillNo, packageId, recipientsPhone } })
}
/**
 * 查询所有已发货的包裹
 */
export const doGetDeliveryPackage = <T>(orderNo: string, shopOrderNo: string, shopId: string) => {
    return get<T[]>({
        url: `gruul-mall-order/order/${orderNo}/shopOrder/${shopOrderNo}/delivered/package`,
        params: {
            shopId,
        },
    })
}
