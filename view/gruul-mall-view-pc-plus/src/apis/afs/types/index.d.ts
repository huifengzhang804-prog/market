/**
 * 退货退款订单商品
 */
export interface AfsOrderItem {
  createTime: string
  dealPrice: number
  image: string
  num: number
  productId: string
  productName: string
  salePrice: number
  services: any[] // 根据实际数据类型修改
  skuId: string
  specs: any[] // 根据实际数据类型修改
  updateTime: string
  version: number
}
/**
 * 退货退款订单协商历史
 */
export interface History {
  afsStatus: string
  createTime: string
  id: string
  packageStatus: string
  updateTime: string
  remark?: string // 可选属性
  evidences?: any[] // 根据实际数据类型修改
}
/**
 * 退货退款订单协商历史
 */
export interface KeyNodeTimeout {
  confirmReturnedTimeout: number
  requestAgreeTimeout: number
  returnedTimeout: number
}
/**
 * 退货退款订单协商历史
 */
export interface RefundRequestType {
  afsOrderItem: AfsOrderItem
  buyerAvatar: string
  buyerId: string
  buyerNickname: string
  buyerPhone: string
  createTime: string
  explain: string
  histories: History[]
  id: string
  keyNodeTimeout: KeyNodeTimeout
  no: string
  orderNo: string
  packageStatus: string
  quicknessAfs: boolean
  reason: string
  refundAmount: number
  shopId: string
  shopLogo: string
  shopName: string
  shopOrderItemId: string
  status: string
  supplierId: string
  type: string
  updateTime: string
  version: number
}
