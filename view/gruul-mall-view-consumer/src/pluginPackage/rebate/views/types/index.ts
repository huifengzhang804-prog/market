export interface RebateOrder {
  buyerId: string
  buyerNickname: string
  createTime: string
  expired: string
  id: string
  orderNo: string
  pendingSettlement: string
  settled: string
  shopId: Long
  shopName: string
  shopTotalRebate: string
  status: keyof typeof PaidStatus
  totalExpired: string
  totalPendingSettlement: string
  totalRebate?: string
  rebateOrderItems: RebateOrderItem[]
}

export enum PaidStatus {
  UNPAID = '待付款',
  PAID = '已付款',
  COMPLETED = '已完成',
  CLOSED = '已关闭',
}

export enum SettlementStatus {
  PENDING_SETTLEMENT = '待结算',
  EXPIRED = '已失效',
  SETTLED = '已结算',
}

interface RebateOrderItem {
  image: string
  num: 1
  orderItemId: string
  platformServiceFee: string
  productId: Long
  productName: string
  rebateCalculation: string
  salePrice: Long
  settlementStatus: keyof typeof SettlementStatus
  skuId: Long
  estimatedRebate: string
  specs: string[]
}

export interface RebateOrderDetails {
  amount: string
  changeType: 'REDUCE' | 'INCREASE' // 收入支出类型 RDDUCE 收入 INCREASE 支出
  createTime: string
  deleted: boolean
  id: string
  rebateName: string
  rebateType: keyof typeof RebateTypeEnum
  updateTime: string
  userId: string
  version: number
}

export enum RebateTypeEnum {
  INCOME = '收入',
  EXPENDITURE = '支出',
  REFUND = '退款',
  WITHDRAW = '提现',
}
export enum RebateTypeColorEnum {
  INCOME = '#2BA471',
  EXPENDITURE = '#E37318',
  REFUND = '#D54941',
  WITHDRAW = '#0052D9',
}
