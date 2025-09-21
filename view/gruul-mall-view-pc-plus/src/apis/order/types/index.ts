// 订单统计数据接口
export interface OrderCount {
  // 待评价订单数量
  uncommented: number
  // 待发货订单数量
  undelivered: number
  // 待处理售后订单数量
  unhandledAfs: number
  // 待付款订单数量
  unpaid: number
  // 待收货订单数量
  unreceived: number
}
// 订单商品项接口

interface Extra {
  rebatePay: boolean
}

interface Timeout {
  afsAuditTimeout: number
  commentTimeout: number
  confirmTimeout: number
  payTimeout: number
}

interface OrderExtra {
  packUpTime: string
  shopStoreId: string
  distributionMode: string
}

interface Order {
  activityId: number
  buyerAvatar: string
  buyerId: string
  buyerNickname: string
  createTime: string
  deleted: boolean
  distributionMode: string
  extra: OrderExtra
  id: string
  isPriority: boolean
  no: string
  platform: string
  source: string
  status: string
  timeout: Timeout
  type: string
  updateTime: string
  version: number
}

// 订单支付类型
export interface OrderPaymentType {
  createTime: string
  deleted: boolean
  discountAmount: number
  extra: Extra
  freightAmount: number
  id: string
  order: Order
  orderNo: string
  payAmount: number
  totalAmount: number
  updateTime: string
  version: number
}
