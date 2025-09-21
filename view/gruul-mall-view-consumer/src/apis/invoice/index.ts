import api from '@/libs/request'

/**
 * 创建/编辑发票抬头
 */
export const doPostInvoiceHeader = (data: any) => {
  return api.post('addon-invoice/invoice/invoice-headers/invoice-header', data)
}
/**
 * 分页查询发票抬头
 */
export const doGetInvoiceHeader = () => {
  return api.get('addon-invoice/invoice/invoice-headers/pageInvoiceHeader', {
    size: 500,
    ownerType: 'USER',
  })
}
/**
 * 设置默认抬头
 */
export const doPutsetDefault = (data: any) => {
  return api.put(`addon-invoice/invoice/invoice-headers/default-invoice-header`, data)
}
/**
 * 获取默认抬头
 */
export const doGetDefault = () => {
  return api.get(`addon-invoice/invoice/invoice-headers/getDefaultInvoiceHeader`, { invoiceHeaderOwnerType: 'USER' })
}
/**
 * 删除发票抬头
 */
export const doDeleteInvoiceHeader = (invoiceHeaderId: string) => {
  return api.delete(`addon-invoice/invoice/invoice-headers/${invoiceHeaderId}`)
}
/**
 * 获取发票抬头详情
 */
export const doGetInvoiceHeaderDetail = (params: any) => {
  return api.get('addon-invoice/invoice/invoice-headers', params)
}

/**
 * 申请开票
 */
export const doPostInvoiceRequest = (data: any) => {
  return api.post('addon-invoice/invoice/invoiceRequest', data)
}
/**
 * 查询发票详情
 */
export const doGetInvoiceRequest = (id: string) => {
  return api.get(`addon-invoice/invoice/invoiceRequest/${id}`)
}
/**
 * 获取发票设置
 */
export const doGetInvoiceSettings = (params: any) => {
  return api.get(`addon-invoice/invoice/invoiceSettings`, params)
}
/**
 * 获取发票状态
 */
export const doGetInvoiceTryRequest = (params: any) => {
  return api.get(`addon-invoice/invoice/invoiceRequest/pre-request`, params)
}
/**
 * 撤销开票
 */
export const doPutwithdraw = (Id: string) => {
  return api.put(`addon-invoice/invoice/invoiceRequest/${Id}`)
}
/**
 * 重发发票
 */
export const doPostReSend = (data: any) => {
  return api.post('addon-invoice/invoice/invoiceAttachment/re-send', data)
}
