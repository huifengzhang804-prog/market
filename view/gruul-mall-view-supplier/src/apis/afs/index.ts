import { get, post, put, del, patch } from '../http'
/**
 * @description:售后工单列表
 * @param {any} data
 * @returns {*}
 */
export const doGetAfsList = (data?: any): Promise<any> => {
    return get({
        url: 'gruul-mall-afs/afs/order',
        params: data,
    })
}

/**
 * @description: 同意或拒绝售后申请
 * @param {string} afsNo
 * @param {boolean} agree 是否同意售后申请
 * @param {string} remark 备注
 * @param {string} receiver 退货退款 同意时需要选择的收货地址
 * @returns {*}
 */
export const doPutAfsRequestAuditByAfsNo = (afsNo: string, agree: boolean, remark: string, receiver?: string) => {
    return put({
        url: `gruul-mall-afs/afs/order/${afsNo}/request/audit`,
        data: { agree, receiver, remark },
    })
}
/**
 * @description: 退货退款确认收货
 * @param {string} afsNo
 * @param {boolean} agree
 * @param {string} remark
 * @param {string} receiver
 * @returns {*}
 */
export const doPutAfsconfirmDeliveryRefunds = (afsNo: string, agree: boolean, remark: string, receiver?: string) => {
    return put({
        url: `gruul-mall-afs/afs/order/${afsNo}/confirm/audit`,
        data: { agree, receiver, remark },
    })
}

/**
 * @description: 协商历史
 * @param {any} afsNo
 */
export const doGetAfssHistory = <T>(afsNo: string) => {
    return get<T[]>({
        url: `gruul-mall-afs/afs/order/${afsNo}/history`,
    })
}

/**
 * @description: 查询买家昵称与头像
 */
export const doGetUserDataByBuyerId = (userId: string) => {
    return get({
        url: `gruul-mall-uaa/uaa/user/data/${userId}`,
    })
}
/**
 * @description:查询 店铺 名称
 * @param {string} shopId
 * @returns {*}
 */
export const doGetShopInfo = (shopId: string): Promise<any> => {
    return get({ url: `gruul-mall-shop/shop/info/base/${shopId}` })
}

/**
 * @description: 查询所有已发货的包裹
 * @param {string} orderNo
 * @param {string} shopOrderNo
 * @returns {*}
 */
export const doGetDeliveryPackage = (orderNo: string, shopOrderNo: string, shopId: string) => {
    return get({ url: `gruul-mall-order/order/${orderNo}/shopOrder/${shopOrderNo}/delivered/package`, params: { shopId } })
}
/**
 * @description: 查询第一个已发货的包裹
 * @param {string} orderNo
 * @param {string} shopOrderNo
 * @returns {*}
 */
export const doGetFirstDeliveryPage = (orderNo: string, shopOrderNo: string, packageId: string) => {
    return get({ url: `gruul-mall-order/order/${orderNo}/shopOrder/${shopOrderNo}/delivered/01`, params: { packageId } })
}
/**
 * @description: 物流轨迹
 * @param {*} companyCode 物流公司名字
 * @param {*} waybillNo 物流单号
 * @returns {*}
 */
export const doGetLogisticsTrajectoryByWaybillNo = (companyCode: string, waybillNo: string) => {
    return get({ url: 'gruul-mall-freight/logistics/node', params: { companyCode, waybillNo } })
}

/**
 * @description 获取可切换的店铺列表
 * @returns
 */
export const doGetEnableChangeShop = (): Promise<any> => {
    return get({ url: 'gruul-mall-uaa/uaa/auth/switch' })
}
