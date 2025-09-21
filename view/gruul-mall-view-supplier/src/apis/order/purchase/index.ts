import { get, post, put } from '@/apis/http'

/**
 * @description 获取供应商采购订单列表
 * @param params 查询参数
 * @returns
 */
export const getPurchaseOrderList = (params: any = {}): Promise<any> => {
    return get({ url: 'addon-supplier/supplier/order', params })
}

/**
 * @description 审核采购订单支付状态
 * @param data 传递的参数
 * @returns
 */
export const auditPurchasePayResult = (data: { orderNo: string; success: boolean }) => {
    return put({ url: 'addon-supplier/supplier/order/pay/audit', data })
}

/**
 * @description 取消采购订单
 * @param orderNo 订单号
 * @returns
 */
export const cancelPurchaseOrder = (orderNo: string) => {
    return put({ url: `addon-supplier/supplier/order/close/${orderNo}` })
}

export const fetchNotDeliveryPackage = (orderNo: string): Promise<any> => {
    return get({ url: `addon-supplier/supplier/order/delivery/${orderNo}` })
}

export const putDeliveryPurchaseOrder = (data: any) => {
    return put({ url: 'addon-supplier/supplier/order/delivery', data })
}

/**
 * @description 获取采购单详情
 * @param orderNo 订单号
 * @returns
 */
export const getPurchaseOrderDetails = (orderNo: string): Promise<any> => {
    return get({ url: `addon-supplier/supplier/order/${orderNo}` })
}
/**
 * @description 获取已发货包裹列表
 * @param orderNo 订单编号
 * @returns
 */
export const getDeliveryedPurchasePackages = (orderNo: string) => {
    return get({ url: `addon-supplier/supplier/order/delivery/${orderNo}` })
}
/**
 * @description 批量发货采购订单
 * @param data 发货数据
 * @returns
 */
export const batchDeliveryPurchasePackages = (data: any[]) => {
    return put({ url: 'addon-supplier/supplier/order/delivery/batch', data })
}
/**
 * @description 备注订单信息
 * @param data 备注信息
 * @returns
 */
export const putRemarkPurchaseOrder = (data: any) => {
    return put({ url: 'addon-supplier/supplier/order/remark/batch', data })
}
/**
 * @description 添加聊天联系人
 * @param sender 发送人id号
 * @param targetId 收件人id
 * @returns
 */
export const postAddContact = (sender: string, targetId: string) => {
    return post({ url: `gruul-mall-carrier-pigeon/pigeon/group-chat-rooms/${sender}/${targetId}` })
}
