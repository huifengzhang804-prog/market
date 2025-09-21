import { get, put, post, del } from '../http'
type Typedata = {
    id: string
    logisticsCompanyName: string
    logisticsCompanyCode: string
    logisticsCompanyStatus: string
}
type TypeupdateData = {
    logisticsCompanyName: string
    logisticsCompanyCode: string
    logisticsCompanyStatus: string
}
// export const doUpdateSellStatus = (ids: string[], status: string) => {
//   return put({
//       url: `gruul-mall-goods/manager/product/updateStatus/${status}`,
//       data: ids,
//   })
// }
/**
 * 初始化物流公司列表
 */
export const doGetLogisticsList = (params: any): Promise<any> => {
    return get({ url: 'gruul-mall-freight/fright/list', params })
}
/**
 * 添加物流公司
 * @param {Typedata} data
 */
export const doUpdateLogistics = (data: TypeupdateData) => {
    return post({ url: 'gruul-mall-freight/fright/add', data })
}
/**
 * 编辑物流公司
 */
export const doEditLogistics = (data: Typedata) => {
    return post({ url: 'gruul-mall-freight/fright/update', data })
}

/**
 * 批量删除物流信息
 * @param {string} arr
 */
export const doDelLogistics = (arr: string[]) => {
    return del({ url: `gruul-mall-freight/fright/del/${arr.join(',')}` })
}
/**
 * 批量禁用物流信息
 * @param {*} state
 * @param {string} ids
 */
export const doForbiddenLogistics = (state: string, ids: string[]) => {
    return put({ url: `gruul-mall-freight/fright/batch/${state}`, data: ids })
}
