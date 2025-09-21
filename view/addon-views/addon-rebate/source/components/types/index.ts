export interface RebateOrderList {
  [key: string]: any
  createTime: string
  packageMap: Map<string | undefined, RebateOrderItem[][]>
  buyerId: string
  buyerNickname: string
  expired: string
  id: string
  orderNo: string
  pendingSettlement: string
  rebateOrderItems: RebateOrderItem[]
  settled: string
  shopId: string
  shopName: string
  status: keyof typeof PaidStatus // 需要后端提供
  totalRebate: string
}

export enum PaidStatus {
  UNPAID = '待付款',
  PAID = '已付款',
  COMPLETED = '已完成',
  CLOSED = '已关闭',
}

interface RebateOrderItem {
  [key: string]: any
  num: number
  orderItemId: string
  platformServiceFee: string
  productId: string
  productName: string
  rebateCalculation: string
  salePrice: string
  settlementStatus: 'PENDING_SETTLEMENT' | 'SETTLED' | 'EXPIRED'
  skuId: string
  specs: string[]
  dealPrice: string
  estimatedRebate: string
  image: string
}
