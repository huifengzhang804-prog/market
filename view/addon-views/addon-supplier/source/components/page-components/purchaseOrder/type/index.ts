/**
 * 申请开票接口类型
 */
interface InvoiceRequest {
  id?: string
  applicantId: string
  applicantShopId: string
  invoiceOwnerType: keyof typeof INVOICE_OWNER_TYPE
  shopSupplierName: string
  applicationTime: string
  orderNo: string
  invoiceAmount: string
  invoiceType: keyof typeof INVOICE_TYPE
  invoiceStatus: keyof typeof INVOICE_STATUS
  billingRemarks: string
  denialReason: string
  invoiceHeaderId: string
  invoiceHeader?: object
}
enum INVOICE_OWNER_TYPE {
  // 用户端
  USER = 'USER',
  // 商家端
  SHOP = 'SHOP',
}
enum INVOICE_TYPE {
  '' = '',
  // 增值税电子普通发票
  VAT_GENERAL = 'VAT_GENERAL',
  // 增值税电子专用发票
  VAT_SPECIAL = 'VAT_SPECIAL',
}
enum INVOICE_STATUS {
  START = 'START',
  // 开票中
  REQUEST_IN_PROCESS = 'REQUEST_IN_PROCESS',
  //开票成功
  SUCCESSFULLY_INVOICED = 'SUCCESSFULLY_INVOICED',
  //  开票失败
  FAILED_INVOICE_REQUEST = 'FAILED_INVOICE_REQUEST',
}
/**
 * 分页查询发票抬头响应类型
 * @param {*}  type: 发票类型
 * @param {*}  ownerId: 发票所属id
 * @param {*}  ownerType 发票抬头所属方类型
 * @param {*}  header: 发票抬头
 * @param {*}  taxIdentNo: 税号
 * @param {*}  openingBank: 开户行
 * @param {*}  bankAccountNo: 银行账号
 * @param {*}  enterprisePhone: 企业电话
 * @param {*}  enterpriseAddress: 企业地址
 * @param {*}  email: 邮箱地址
 */
interface PageInvoiceHeader {
  id: string
  createTime: string
  updateTime: string
  type: keyof typeof PAGE_INVOICE_HEADER_TYPE
  ownerId: string
  ownerType: InvoiceRequest['invoiceOwnerType']
  header: string
  taxIdentNo: string
  openingBank: string
  bankAccountNo: string
  enterprisePhone: string
  enterpriseAddress: string
  email: string
}

enum PAGE_INVOICE_HEADER_TYPE {
  // 个人
  PERSONAL = 'PERSONAL',
  // 企业
  ENTERPRISE = 'ENTERPRISE',
}
const PageInvoiceHeaderTypeCn = {
  // 个人
  PERSONAL: '个人',
  // 企业
  ENTERPRISE: '企业',
}
const invoiceTypeCn = { VAT_GENERAL: '增值税电子普通发票', VAT_SPECIAL: '增值税电子专用发票' }
export { InvoiceRequest, INVOICE_OWNER_TYPE, INVOICE_TYPE, INVOICE_STATUS, invoiceTypeCn, PageInvoiceHeader, PageInvoiceHeaderTypeCn }
