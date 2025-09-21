import { get, post, put, del } from '../http'
/**
 * @description: 获取模板列表
 * @param {number} current
 * @param {number} size
 * @returns {*}
 */
export const doGetLogisticsTemplateList = (current: number, size: number): Promise<any> => {
    return get({
        url: 'gruul-mall-freight/logistics/template/get/list',
        params: {
            current,
            size,
        },
    })
}
/**
 * @description: 添加物流模板
 * @param {*} data
 * @returns {*}
 */
export const doAddLogisticsTemplate = (data: any) => {
    return post({
        url: 'gruul-mall-freight/logistics/template/save/info',
        data,
    })
}
/**
 * @description: 删除物流模板
 * @param {*} id
 * @returns {*}
 */
export const doDelLogisticsTemplate = (id: string) => {
    return del({
        url: `gruul-mall-freight/logistics/template/delete/info/${id}`,
    })
}
/**
 * @description: 获取单个物流模板信息
 * @param {*} id
 * @returns {*}
 */
export const doGetLogisticsTemplateInfoById = (id: string) => {
    return get({
        url: `gruul-mall-freight/logistics/template/get/info`,
        params: { id },
    })
}

/**
 * @description:编辑单个物流模板信息
 * @param {*} data
 * @returns {*}
 */
export const doPutLogisticsTemplateInfoById = (data: any) => {
    return post({
        url: `gruul-mall-freight/logistics/template/update/info`,
        data,
    })
}
