/**
 * @description: 充值
 * @params {*} discountsState  	优惠状态 0无优惠 1有优惠
 * @params {*}  ruleJson 充值数据
 * @params {*} switching 储值管理开关 0关闭 1开启
 */
export interface ApiOnlineTopUp {
  discountsState: boolean
  id: string
  ruleJson: OnlineTopUpItem[]
  switching: boolean
}
export interface OnlineTopUpItem {
  id: string
  ladderMoney: string
  presentedGrowthValue: string
  presentedMoney: string
}
export type DealAggregationType = 'ALL' | 'USER_CONSUME' | 'REFUND_SUCCEED' | 'CHARGING' | string
export interface BillingDetails {
  current: number
  dealAggregationType: DealAggregationType
  optimizeCountSql: boolean
  orders: []
  pages: number
  records: BillingDetailsItem[]
  userPaymentHistoryStatisticsList: userPaymentHistoryStatisticsItem[]
}
export type UserPaymentHistoryStatisticsList = BillingDetails['userPaymentHistoryStatisticsList']
/**
 * 支出收入统计
 */
export type ChangeType = 'INCREASE' | 'REDUCE'
export interface userPaymentHistoryStatisticsItem {
  changeType: ChangeType
  statisticsMoney: string
}
export const changeTypeCn = {
  INCREASE: '收入',
  REDUCE: '支出',
} as const
export const dealTypeCn = {
  SYSTEM_GIVE: '充值赠送',
  PERSONAL_CHARGING: '个人充值',
  SYSTEM_CHARGING: '系统充值',
  SHOPPING_PURCHASE: '购物消费',
  PURCHASE_MEMBER: '购买会员',
  REFUND_SUCCEED: '退款成功',
  WITHDRAW: '提现',
}
export enum DEAL_TYPE {
  /**
   * 系统赠送
   */
  SYSTEM_GIVE,

  /**
   * 个人充值
   */
  PERSONAL_CHARGING,

  /**
   * 系统充值
   */
  SYSTEM_CHARGING,

  /**
   * 购物消费
   */
  SHOPPING_PURCHASE,

  /**
   * 购买会员
   */
  PURCHASE_MEMBER,

  /**
   * 退款成功
   */
  REFUND_SUCCEED,

  /**
   * 提现
   */
  WITHDRAW,
}
export interface BillingDetailsItem {
  changeType: 'REDUCE' | 'INCREASE' | ''
  createTime: ''
  dealType: keyof typeof DEAL_TYPE
  id: '1579365831145185280'
  money: '100000'
  userId: '1'
}

export interface SearchConfig {
  chooseTime: string[]
}

export interface PageConfig {
  current: number
  size: number
  total: number
}

export interface ApiResponse<T> {
  code: number
  data: T
  msg?: string
}

export interface PaymentHistoryResponse {
  records: BillingDetailsItem[]
  total: number
}

export interface FootMarkListType {
  date: string
  id: string
  productId: string
  productName: string
  productPic: string
  productPrice: string
  shopId: string
  status: string
  evaluatePerson: string
}
