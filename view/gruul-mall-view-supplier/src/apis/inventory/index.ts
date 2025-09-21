import { get, post, put, del, patch } from '../http'

/**
 * @description: 获取仓储管理订单列表
 * @param {any} params
 */
export const doGetStorageList = (params: any): Promise<any> => {
    return get({
        url: 'gruul-mall-storage/storage/management/order/page',
        params,
    })
}
/**
 * @description: 仓储管理订单创建
 * @param {any} data
 */
export const doPostStorageOrderCreate = (data: any) => {
    return post({
        url: 'gruul-mall-storage/storage/management/order/create',
        data,
    })
}
/**
 * @description: 仓储管理订单修改
 * @param {any} data
 */
export const doPostStorageOrderedit = (data: any) => {
    return post({
        url: 'gruul-mall-storage/storage/management/order/edit',
        data,
    })
}
/**
 * @description: 仓储管理订单取消
 * @param {any} id
 */
export const doPutStorageOrdercancel = (id: string) => {
    return put({
        url: `gruul-mall-storage/storage/management/order/cancel/${id}`,
    })
}
/**
 * @description: 仓储管理订单完成
 * @param {any} id
 */
export const doPutStorageOrdercomplete = (id: string) => {
    return put({
        url: `gruul-mall-storage/storage/management/order/complete/${id}`,
    })
}
/**
 * @description: 获取仓储管理订单详情
 * @param {any} id
 */
export const doGetStorageOrderDetail = (id: string): Promise<any> => {
    return get({
        url: `gruul-mall-storage/storage/management/order/get/${id}`,
    })
}
/**
 * @description: 获取库存明细
 * @param {any} id
 */
export const doGetStorageFlow = (params: any): Promise<any> => {
    return get({
        url: `gruul-mall-storage/storage/detail/page`,
        params,
    })
}

/**
 * @description: 导出库存明细
 * @param {any} params
 */
export const doPostExportStorageFlow = (params: any): Promise<any> => {
    return post({
        url: `gruul-mall-storage/storage/detail/page/export`,
        data: params,
    })
}
