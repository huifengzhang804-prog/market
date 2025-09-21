export { IntegralOrderItem, IntegralOrderStatus, IntegralOrderDeliverStatus, IntegralOrderInfo }
interface IntegralOrderItem {
  sellerRemark?: string
  buyerRemark?: string
  integralOrderDeliverType?: IntegralOrderDeliverStatus
  expressCompanyName?: string
  expressName?: string
  payTime: string
  deliveryTime: string
  source?: IntegralOrderSourceStatus
  accomplishTime: string
  expressNo?: string
  buyerId: string
  buyerNickname: string
  createTime: string
  freightPrice: number
  image: string
  integralOrderReceiverVO: { name: string; mobile: string; area: Array<string>; address: string }
  integralOrderReceiver?: { name: string; mobile: string; area: Array<string>; address: string }
  no: string
  num: number
  price: string
  productName: string
  status: IntegralOrderStatus
  salePrice: string
  productType: 'REAL_PRODUCT' | 'VIRTUAL_PRODUCT'
  affiliationPlatform?: keyof AffiliationPlatform
  checked?: boolean
}
export enum AffiliationPlatform {
  WECHAT_MINI_APP = '微信小程序',
  WECHAT_MP = '公众号',
  PC = 'PC商城',
  H5 = '移动端端h5',
  IOS = 'IOS',
  ANDROID = '安卓',
  HARMONY = '鸿蒙',
}
interface IntegralOrderInfo extends Omit<IntegralOrderItem, 'integralOrderReceiverVO'> {
  integralOrderReceiver: {
    address: string
    area: string[]
    createTime: string
    id: string
    mobile: string
    name: string
    orderNo: string
    updateTime: string
  }
}
type IntegralOrderStatus = keyof typeof INTEGARL_ORDER_STATUS
type IntegralOrderSourceStatus = keyof typeof INTEGARL_ORDER_SOURCE_STATUS
type IntegralOrderDeliverStatus = keyof typeof INTEGARL_ORDER_DELIVER_STATUS
enum INTEGARL_ORDER_STATUS {
  UNPAID = '未支付',
  PAID = '已支付',
  ON_DELIVERY = '发货中',
  ACCOMPLISH = '已完成',
  SYSTEM_CLOSED = '系统关闭',
}
enum INTEGARL_ORDER_SOURCE_STATUS {
  PRODUCT = '商品详情',
}
enum INTEGARL_ORDER_DELIVER_STATUS {
  WITHOUT = '无需物流发货',
  EXPRESS = '普通发货',
}
