import { Obj } from '@/utils/types'

/**
 * @param UNIFIED 统一配置
 * @param RATE 百分比
 * @param FIXED_AMOUNT 固定金额
 */
export enum SHARETYPE {
  UNIFIED,
  RATE,
  FIXED_AMOUNT,
}

export enum ORDERSTATUS {
  PAID,
  COMPLETED,
  CLOSED,
}

/**
 * @param ENABLE 上架
 * @param DISABLE 下架
 * @param FORBIDDEN 违规禁用
 */
// REFUSE(-2, "已拒绝"),
// UNDER_REVIEW(-1, "审核中"),
// SELL_OFF(0, "下架"),
// SELL_ON(1, "上架"),
// SELL_OUT(2, "已售完"),
// PLATFORM_SELL_OFF(3, "违规下架"),
// UNUSABLE(4, "店铺不可用");
export enum DISTRIBUTESTATUS {
  ENABLE,
  DISABLE,
  FORBIDDEN,
  REFUSE,
  UNDER_REVIEW,
  SELL_OFF,
  SELL_ON,
  SELL_OUT,
  PLATFORM_SELL_OFF,
  UNUSABLE,
}

export enum DISTRIBUTIONSTATUS {
  CANCEL_DISTRIBUTION,
  IN_DISTRIBUTION,
}
export interface DistributeType {
  comInfo: {
    name: string
    pic: string
  }
  shareType: keyof typeof SHARETYPE
  one: string
  two: string
  three: string
}

export interface DistributeParams {
  shareType: keyof typeof SHARETYPE
  one?: string | null
  two?: string | null
  three?: string | null
  productIds: string[]
  listId: string
}

export enum Level {
  ONE,
  TWO,
  THREE,
}
export enum CONDITION {
  APPLY,
  CONSUMPTION,
}

export enum PRECOMPUTE {
  ALL,
  NEVER,
  DISTRIBUTOR,
}

export enum DISTRIBUTORSTATUS {
  SUCCESS,
  APPLYING,
  CLOSED,
}
/**
 * 分销配置保存
 * @param level 分销层级
 * @param condition 分销商条件
 * @param protocol 分销协议
 * @param purchase 是否开起内购
 * @param poster 推广海报
 * @param bonusConfig 分销配置
 */
export interface DistributeConfigParamsType {
  level: keyof typeof Level
  condition: {
    types: Array<keyof typeof CONDITION>
    requiredAmount?: number
  }
  protocol: string
  playMethods: string
  purchase: boolean
  poster: string
  shareType: keyof typeof SHARETYPE
  one: number
  two?: number
  three?: number
  precompute: keyof typeof PRECOMPUTE
}

export interface ApiDistributeComItem {
  createTime: string
  id: string
  name: string
  pic: string
  productId: string
  salePrices: string[]
  shareType: keyof typeof SHARETYPE
  shopId: string
  status: keyof typeof DISTRIBUTESTATUS
  one?: string
  two?: string
  three?: string
  shopName: string
  distributionStatus: keyof typeof DISTRIBUTIONSTATUS | (string & Obj)
}
export interface ApiDistributorItem {
  avatar: string
  code: string
  createTime: string
  id: string
  identity: string
  mobile: string
  nickname: string
  one: string
  status: keyof typeof DISTRIBUTORSTATUS
  three: string
  total: string
  two: string
  undrawn: string
  userId: string
  referrer?: string
  orderCount: string
}
// 分佣比例
export interface BonusShare {
  one: number
  shareType: string
}
// 分佣金额
export interface OneBonus {
  bonus: number
}
// 商品信息
export interface Item {
  bonusShare: BonusShare
  dealPrice: number
  image: string
  num: number
  one: OneBonus
  orderNo: string
  orderStatus: string
  productId: string
  productName: string
  purchase: boolean
  shopId: string
  skuId: string
  specs: string[]
  three: OneBonus
  two: OneBonus
}
// 订单信息
export interface DistributionList {
  buyerAvatar: string
  buyerId: string
  buyerName: string
  buyerNickname: string
  createTime: string
  items: Item[]
  orderNo: string
  payAmount: number
  purchase: boolean
  shopId: string
  shopName: string
  status: string
  checked: boolean
}
// 分页信息
export interface Page {
  buyerNickname: string
  current: number
  orderNo: string
  pages: number
  productName: string
  records: DistributionList[]
  shopName: string
  size: number
  total: number
}
// 统计信息
export interface Statistic {
  invalid: number | string
  total: number | string
  unsettled: number | string
}
// 响应信息
export interface DistributionResponse {
  page: Page
  statistic: Statistic
}
