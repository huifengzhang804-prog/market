/**
 * 返利用户
 */
export enum REBATE_USERS {
  PAID_MEMBER = 'PAID_MEMBER',
  ALL_MEMBERS = 'ALL_MEMBERS',
}
export enum MEMBER_TYPE {
  PAID_MEMBER = 'PAID_MEMBER',
  FREE_MEMBER = 'FREE_MEMBER',
}
/**
 * 返利设置响应数据
 * @params rebateUsers 返利用户类型
 */
export interface ResRebateConf {
  allRebateUsers: AllRebateUsers[]
  payRebateUsers: PayRebateUsers[]
  createTime: string
  id: string
  rebateStatus: boolean
  rebateUsers: keyof typeof REBATE_USERS
  updateTime: string
}
interface AllRebateUsers {
  id: string
  memberName: string
  memberType: keyof typeof MEMBER_TYPE
  rankCode: string
  rebatePaymentPercentage: string | number
  rebatePercentage: string | number
  withdrawalThreshold: string | number
}
interface PayRebateUsers {
  id: string
  memberName: string
  memberType: keyof typeof MEMBER_TYPE
  rankCode: boolean
  rebatePaymentPercentage: string | number
  rebatePercentage: string | number
  withdrawalThreshold: string | number
}

interface RebateUser {
  id?: string
  memberType: 'PAID_MEMBER' | 'FREE_MEMBER'
  memberName: string
  rankCode: number
  rebatePercentage: string
  rebatePaymentPercentage: string
  withdrawalThreshold: string
}
export interface PutRebateConfData {
  id?: string
  rebateStatus: boolean
  rebateUsers: 'PAID_MEMBER' | 'ALL_MEMBERS'
  payRebateUsers: RebateUser[]
  allRebateUsers: RebateUser[]
}
