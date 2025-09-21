import { get, post, put, del } from '../http'
/**
 * 获取所有展示分类
 * @param data
 */
// 获取物流地址列表
export const doGetLogisticsList = (data: any): Promise<any> => {
    return get({
        url: 'gruul-mall-shop/shop/logistics/address/list',
        params: data,
    })
}
// 根据id删除地址
export const delLogisticsById = (id: string) => {
    return del({
        url: `gruul-mall-shop/shop/logistics/address/del/${id}`,
    })
}
//修改或者新增地址
export const setLogisticsDddress = (data: any) => {
    return post({
        url: 'gruul-mall-shop/shop/logistics/address/set',
        data,
    })
}
/**
 * @description: 获取物流公司列表
 */
export const doGetLogisticsCompany = () => {
    return get({
        url: 'gruul-mall-freight/fright/list',
        params: {
            current: 1,
            size: 1000,
        },
    })
}
/**
 * @description: 获取物流服务列表
 */
export const doGetLogisticsCompanyByShopIdList = () => {
    return get({
        url: 'gruul-mall-freight/fright/by/shopId/list',
    })
}
/**
 * @description: 获取物流服务收货地址
 */
export const doGetDeliveryAddress = () => {
    return get({
        url: 'gruul-mall-shop/shop/logistics/address/list',
        params: {
            current: 1,
            size: 1000,
        },
    })
}
/**
 * @description: 查询可用的物流服务
 * @param {*} id
 * @returns {*}
 */
export const doGetLogisticsExpressUsableList = (params: any): Promise<any> => {
    return get({
        url: `gruul-mall-freight/logistics/express/usable/list`,
        params,
    })
}
/**
 * @description: 新增物流服务
 */
export const doSaveLogisticsServe = (data: any) => {
    return post({
        url: 'gruul-mall-freight/logistics/express/save',
        data,
    })
}
/**
 * @description: 获取物流服务列表
 * @param {number} current
 * @param {number} size
 */
export const doGetLogisticServeList = (current: number, size: number) => {
    return get({
        url: 'gruul-mall-freight/logistics/express/page',
        params: {
            current,
            size,
        },
    })
}
/**
 * @description: 修改物流服务
 * @param {any} data
 * @returns {*}
 */
export const doUpdateLogisticServe = (data: any) => {
    return post({
        url: 'gruul-mall-freight/logistics/express/update',
        data,
    })
}
/**
 * @description: 删除物流服务
 * @returns {*}
 */
export const doDelLogisticsServe = (ids: string) => {
    return del({
        url: `gruul-mall-freight/logistics/express/del/${ids}`,
    })
}
/**
 * @description: 设置默认收发货地址
 * @param {*} id
 * @param {*} type
 * @returns {*}
 */
export const doLogisticsSetDef = (id, type) => {
    return put({ url: `gruul-mall-shop/shop/logistics/address/set/def/address/${id}/${type}` })
}
