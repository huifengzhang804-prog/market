import { get, post, put, del, patch } from '../http'

/**
 * 获取发票设置
 */
export const doGetinvoiceSettings = <T>(params: { invoiceSettingsClientType: 'SHOP' | 'SUPPLIER'; shopId: Long }) => {
    return get<T>({ url: `addon-invoice/invoice/invoiceSettings`, params })
}
/**
 * 编辑发票设置
 */
export const doPostinvoiceSettings = (data: any) => {
    return post({ url: `addon-invoice/invoice/invoiceSettings`, data })
}
/**
 * 分页查询开票申请
 */
export const doGetinvoiceRequestList = (params: any): Promise<any> => {
    return get({ url: `addon-invoice/invoice/invoiceRequest`, params })
}
/**
 * 查询发票详情
 */
export const doGetinvoiceDetail = (id: string) => {
    return get({ url: `addon-invoice/invoice/invoiceRequest/${id}` })
}
/**
 * 拒绝开票申请
 */
export const doPostrefuseInvoiceRequest = (data: any) => {
    return post({ url: `addon-invoice/invoice/invoiceRequest/refuseInvoiceRequest`, data })
}
/**
 * 上传发票附件/重新上传发票附件
 */
export const doPostinvoiceAttachment = (data: any) => {
    return post({ url: `addon-invoice/invoice/invoiceAttachment/upload `, data })
}
