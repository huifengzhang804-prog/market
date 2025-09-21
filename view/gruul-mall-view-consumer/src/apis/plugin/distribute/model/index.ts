enum Level {
  ONE,
  TWO,
  THREE,
}
/**
 * 分佣类型
 * @param UNIFIED 统一配置
 * @param RATE 百分比
 * @param FIXED_AMOUNT 固定金额
 */
export enum SHARETYPE {
  UNIFIED,
  RATE,
  FIXED_AMOUNT,
}
enum CONDITION {
  APPLY,
  CONSUMPTION,
}
enum IDENTY {
  UserType,
  AFFAIRSUserType,
}
/**
 * 分销配置保存
 * @param level 分销层级
 * @param protocol 分销协议
 * @param purchase 是否开起内购
 * @param poster 推广海报
 * @param shareType 分佣类型
 */
export interface DistributeConfigType {
  id: string
  level: keyof typeof Level
  protocol: string
  condition: {
    types: Array<keyof typeof CONDITION>
    requiredAmount?: number
  }
  purchase: boolean
  poster: string
  shareType: keyof typeof SHARETYPE
  one: string
  two?: string
  three?: string
}

export interface ApiDistributorInfoType {
  avatar: string
  code: string
  config: DistributeConfigType
  createTime: string
  id: string
  identity: keyof typeof IDENTY
  mobile: string
  name: string
  nickname: string
  statistics: { customer: string; order: string; bonus: string }
  total: string
  undrawn: string
  unsettled: string
  userId: string
  referrer: string
}

export interface ApiDisCustomerItem {
  avatar: string
  code: string
  consumption: string
  createTime: string
  level: string
  name: string
  nickname: string
  orderCount: string
  userId: string
}

enum COMMISSION_STATUS {
  APPLYING,
  SUCCESS,
  CLOSED,
  FORBIDDEN,
}
export interface ApiCommissionType {
  createTime: string
  status: keyof typeof COMMISSION_STATUS
  updateTime: string
  drawType: {
    alipayAccount?: string
    bank?: string
    cardNo?: string
    amount: string
    name?: string
    type: 'BANK_CARD' | 'WECHAT' | 'ALIPAY'
  }
}
