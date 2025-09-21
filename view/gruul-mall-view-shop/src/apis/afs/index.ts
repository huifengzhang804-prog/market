import { get, post, put, del, patch } from '../http'
import { ShopInfoVO } from '@/apis/afs/type'

/**
 * 售后工单列表
 * @param {any} data
 */
export const doGetAfsList = (data?: any) => {
    return get({
        url: 'gruul-mall-afs/afs/order',
        params: data,
    })
}

/**
 * 同意或拒绝售后申请
 * @param {string} afsNo
 * @param {boolean} agree 是否同意售后申请
 * @param {string} remark 备注
 * @param {string} receiver 退货退款 同意时需要选择的收货地址
 */
export const doPutAfsRequestAuditByAfsNo = (afsNo: string, agree: boolean, remark: string, receiver?: string) => {
    return put({
        url: `gruul-mall-afs/afs/order/${afsNo}/request/audit`,
        data: { agree, receiver, remark },
    })
}
/**
 * 退货退款确认收货
 * @param {string} afsNo
 * @param {boolean} agree
 * @param {string} remark
 * @param {string} receiver
 */
export const doPutAfsconfirmDeliveryRefunds = (afsNo: string, agree: boolean, remark: string, receiver?: string) => {
    return put({
        url: `gruul-mall-afs/afs/order/${afsNo}/confirm/audit`,
        data: { agree, receiver, remark },
    })
}

/**
 * 协商历史
 * @param {any} afsNo
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
 * 查询 店铺 名称
 * @param {string} shopId
 */
export const doGetShopInfo = (shopId: string) => {
    return get<ShopInfoVO>({ url: `gruul-mall-shop/shop/info/base/${shopId}` })
}

/**
 * 查询所有已发货的包裹
 * @param {string} orderNo
 * @param {string} shopOrderNo
 */
export const doGetDeliveryPackage = (orderNo: string, shopOrderNo: string) => {
    return get({ url: `gruul-mall-order/order/${orderNo}/shopOrder/${shopOrderNo}/delivered/package` })
}
/**
 * 查询第一个已发货的包裹
 * @param {string} orderNo
 * @param {string} shopOrderNo
 */
export const doGetFirstDeliveryPage = (orderNo: string, shopOrderNo: string, packageId: string) => {
    return get({ url: `gruul-mall-order/order/${orderNo}/shopOrder/${shopOrderNo}/delivered/01`, params: { packageId } })
}
/**
 * 物流轨迹
 * @param {*} companyCode 物流公司名字
 * @param {*} waybillNo 物流单号
 */
export const doGetLogisticsTrajectoryByWaybillNo = (companyCode: string, waybillNo: string, recipientsPhone?: string) => {
    const params = { companyCode, waybillNo, recipientsPhone }
    if (companyCode === 'shunfeng' && recipientsPhone) {
        params.recipientsPhone = recipientsPhone
    } else {
        params.recipientsPhone = ''
    }
    return get({ url: 'gruul-mall-freight/logistics/node', params })
}
/**
 * 获取可切换的店铺列表
 */
export const doGetEnableChangeShop = (): Promise<any> => {
    return get({ url: 'gruul-mall-uaa/uaa/auth/switch' })
}
