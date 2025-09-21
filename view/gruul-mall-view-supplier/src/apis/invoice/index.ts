import { get, post, put, del, patch } from '../http'

/**
 * @description 获取发票设置
 * @param
 * @returns
 */
export const doGetinvoiceSettings = (params: { invoiceSettingsClientType: 'SHOP' | 'SUPPLIER'; shopId: string }): Promise<any> => {
    return get({ url: `addon-invoice/invoice/invoiceSettings`, params })
}
/**
 * @description 编辑发票设置
 * @param
 * @returns
 */
export const doPostinvoiceSettings = (data: any) => {
    return post({ url: `addon-invoice/invoice/invoiceSettings`, data })
}
/**
 * @description 分页查询开票申请
 * @param
 * @returns
 */
export const doGetinvoiceRequestList = (params: any): Promise<any> => {
    return get({ url: `addon-invoice/invoice/invoiceRequest`, params })
}
/**
 * @description 查询发票详情
 * @param
 * @returns
 */
export const doGetinvoiceDetail = (id: string) => {
    return get({ url: `addon-invoice/invoice/invoiceRequest/${id}` })
}
/**
 * @description 拒绝开票申请
 * @param
 * @returns
 */
export const doPostrefuseInvoiceRequest = (data: any) => {
    return post({ url: `addon-invoice/invoice/invoiceRequest/refuseInvoiceRequest`, data })
}
/**
 * @description 上传发票附件/重新上传发票附件
 * @param
 * @returns
 */
export const doPostinvoiceAttachment = (data: any) => {
    return post({ url: `addon-invoice/invoice/invoiceAttachment/upload `, data })
}
