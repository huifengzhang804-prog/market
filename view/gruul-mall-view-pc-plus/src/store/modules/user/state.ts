// import type { ApiUserData } from '@/hooks'
import storage from '@/libs/storage'

interface ApiUserData {
  avatar: string
  createTime: string
  deleted: false
  gender: string
  id: string
  nickname: string
  updateTime: string
  userId: string
  token: string
}
interface UserInfo {
  token: string
  info: ApiUserData
  loginType: boolean
  refreshToken: string
}
export type MemberType = 'FREE_MEMBER' | 'PAID_MEMBER'
export interface UserStateType {
  userInfo: UserInfo
  member?: {
    memberType: MemberType | string
    memberBenefit: Record<keyof typeof BENEFIT_TYPE, MemberBenefitItem> | null
    memberName: string | null
  } | null
}
const defaultState: UserStateType = {
  userInfo: {
    loginType: false,
    token: new storage().getItem('CLIENT-userStore') || '',
    refreshToken: new storage().getItem('CLIENT-userStoreRefreshToken') || '',
    info: new storage().getItem('CLIENT-userStoreInfo') || {
      avatar: '',
      createTime: '',
      deleted: false,
      gender: '',
      id: '',
      nickname: '',
      updateTime: '',
      userId: '',
    },
  },
}

export enum BENEFIT_TYPE {
  GOODS_DISCOUNT = '商品抵扣',
  INTEGRAL_MULTIPLE = '积分加倍',
  LOGISTICS_DISCOUNT = '物流优惠',
  PRIORITY_SHIPMENTS = '优先发货',
  QUICKNESS_AFS = '极速售后',
  EXCLUSIVE_SERVICE = '专属客服',
  USER_DEFINED = '自定义',
}
export interface MemberBenefitItem {
  extendValue: string
  memberRightsId: string
  rightsName: string
  rightsType: keyof typeof BENEFIT_TYPE
  memberName: string
}
export default defaultState
