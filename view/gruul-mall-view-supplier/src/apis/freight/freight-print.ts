import { get, post, put, del } from '../http'
/**
 * @description:打印机信息新增
 * @param {string} deviceNo
 * @param {string} printName
 * @returns {*}
 */
export const doAddPrint = (deviceNo: string, printName: string) => {
    return post({
        url: 'gruul-mall-freight/logistics/print/save',
        data: { deviceNo, printName },
    })
}
/**
 * @description: 打印机信息修改
 * @param {string} deviceNo
 * @param {string} printName
 * @param {string} id
 * @returns {*}
 */
export const doUpdatePrint = (deviceNo: string, printName: string, id: string) => {
    return post({
        url: 'gruul-mall-freight/logistics/print/update',
        data: { deviceNo, printName, id },
    })
}
/**
 * @description: 打印机列表
 * @param {*} data
 * @returns {*}
 */
export const doGetPrintList = (params: any): Promise<any> => {
    return get({
        url: 'gruul-mall-freight/logistics/print/list',
        params,
    })
}
/**
 * @description: 打印机删除
 * @param {*} id
 * @returns {*}
 */
export const doDelPrintById = (id: string) => {
    return del({
        url: `gruul-mall-freight/logistics/print/del/${id}`,
    })
}
