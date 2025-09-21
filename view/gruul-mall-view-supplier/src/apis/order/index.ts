import { get, put, post } from '../http'

/**
 * @description: 获取订单列表 usePackage 是否根据
 * @param usePackage true 按包裹查询
 * @param packageId 为空查询未发货 否则查询已发货
 * @returns {*}
 */
export const doGetOrderList = (params: any, orderNo?: string): Promise<any> => {
    if (orderNo) {
        return get({ url: `gruul-mall-order/order/${orderNo}`, params })
    }
    return get({ url: `gruul-mall-order/order`, params })
}
/**
 * @description: 通过orderNo获取订单详情
 * @param {*} orderId
 */
export const doGetOrderDetails = (orderNo: string, params: any) => {
    return get({ url: `/gruul-mall-order/order/${orderNo}`, params })
}
/**
 * @description: 通过orderNo获取未发货订单
 * @param {string} orderNo
 * @param {string} shopOrderNo
 * @returns {*}
 */
export const dogetOrderNotDetailsByOrderNo = (orderNo: string, shopOrderNo: string) => {
    return get({ url: `/gruul-mall-order/order/${orderNo}/shopOrder/${shopOrderNo}/undelivered` })
}

/**
 * @description: 商品发货
 * @param {string} orderNo
 * @returns {*}
 */
export const doPutOrderDetailsByOrderNo = (orderNo: string, data) => {
    return put({ url: `/gruul-mall-order/order/${orderNo}/deliver`, data })
}
/**
 * @description: 商品批量发货
 * @param {string} orderNo
 * @returns {*}
 */
export const doPostOrderBatchDeliver = (data) => {
    return put({ url: `gruul-mall-order/order/batch/deliver`, data })
}
/**
 * @description: 关闭店铺订单
 * @param {string} orderNo
 * @param {string} shopOrderNo
 * @returns {*}
 */
export const doPutCloseShopOrder = (orderNo: string, shopOrderNo: string) => {
    return put({ url: `gruul-mall-order/order/${orderNo}/shopOrder/${shopOrderNo}/close` })
}
/**
 * @description 获取平台发货配置信息
 * @deprecated 已废弃 第三方配送上线后 自营店铺平台不再发货 请删除相关逻辑
 */
export const doGetShopDeliveryConfig = (): Promise<any> => {
    return get({ url: '/gruul-mall-shop/shop/deliver' })
}
