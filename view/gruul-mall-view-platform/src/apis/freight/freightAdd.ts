import { ApiLogisticCompany } from '@/views/order/deliveryList/deliveryList.vue'
import { get, post, put, del } from '../http'
import { L } from '../http.type'

/**
 * 获取物流公司列表
 */
export const doGetLogisticsCompany = () => {
    return get<L<ApiLogisticCompany>>({
        url: 'gruul-mall-freight/fright/list',
        params: {
            current: 1,
            size: 1000,
        },
    })
}

/**
 * 查询可用的物流服务
 * @param {*} id
 */
export const doGetLogisticsExpressUsableList = (params: any) => {
    return get<L<ApiLogisticCompany>>({
        url: `gruul-mall-freight/logistics/express/usable/list`,
        params,
    })
}
/**
 * 新增物流服务
 */
export const doSaveLogisticsServe = (data: any) => {
    return post({
        url: 'gruul-mall-freight/logistics/express/save',
        data,
    })
}
/**
 * 获取物流服务列表
 * @param {number} current
 * @param {number} size
 */
export const doGetLogisticServeList = (current: number, size: number): Promise<any> => {
    return get({
        url: 'gruul-mall-freight/logistics/express/page',
        params: {
            current,
            size,
        },
    })
}
/**
 * 修改物流服务
 * @param {any} data
 */
export const doUpdateLogisticServe = (data: any) => {
    return post({
        url: 'gruul-mall-freight/logistics/express/update',
        data,
    })
}
/**
 * 删除物流服务
 */
export const doDelLogisticsServe = (ids: string) => {
    return del({
        url: `gruul-mall-freight/logistics/express/del/${ids}`,
    })
}
