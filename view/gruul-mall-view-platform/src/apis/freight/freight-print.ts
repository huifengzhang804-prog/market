import { get, post, put, del } from '../http'
/**
 * 打印机信息新增
 * @param {string} deviceNo
 * @param {string} printName
 */
export const doAddPrint = (deviceNo: string, printName: string, defSelfShop: string, defSelfSupplier: string) => {
    return post({
        url: 'gruul-mall-freight/logistics/print/save',
        data: { deviceNo, printName, defSelfShop, defSelfSupplier },
    })
}
/**
 * 打印机信息修改
 * @param {string} deviceNo
 * @param {string} printName
 * @param {string} id
 */
export const doUpdatePrint = (deviceNo: string, printName: string, id: string, defSelfShop: string, defSelfSupplier: string) => {
    return post({
        url: 'gruul-mall-freight/logistics/print/update',
        data: { deviceNo, printName, id, defSelfShop, defSelfSupplier },
    })
}
/**
 * 打印机列表
 * @param {*} data
 */
export const doGetPrintList = (params: any): Promise<any> => {
    return get({
        url: 'gruul-mall-freight/logistics/print/list',
        params,
    })
}
/**
 * 打印机删除
 * @param {*} id
 */
export const doDelPrintById = (id: string) => {
    return del({
        url: `gruul-mall-freight/logistics/print/del/${id}`,
    })
}
