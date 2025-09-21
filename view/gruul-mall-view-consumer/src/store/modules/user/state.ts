const defaultState: UserStateType = {
  userInfo: {
    token: '',
    info: { avatar: '', gender: 'UNKNOWN', nickname: '', userId: '', birthday: '' },
    refresh_token: '',
  },
  discode: '',
  member: null,
  refresh_auth_time: new Date(),
  adClosed: false,
}
export default defaultState

export interface UserInfoType {
  refresh_token: string
  token: string
  info: Pick<ApiUserData, 'avatar' | 'gender' | 'nickname' | 'userId' | 'birthday' | 'distributorCode'>
}
/**
 * 获取用户信息接口返回参数
 */
export interface ApiUserData {
  avatar: string
  createTime: string
  deleted: false
  gender: GenderType
  id: string
  nickname: string
  updateTime: string
  userId: string | null
  birthday: string
  distributorCode?: string
}
export type GenderType = 'MALE' | 'FEMALE' | 'UNKNOWN'
export interface UserStateType {
  userInfo: UserInfoType
  discode: string
  member: {
    memberType: MemberType | string
    memberBenefit: Record<keyof typeof BENEFIT_TYPE, MemberBenefitItem[]> | null
    memberName: string | null
    memberCardValidTime: string | null
    rankCode: number | null
  } | null
  refresh_auth_time: Date
  adClosed: boolean
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
type MemberType = 'FREE_MEMBER' | 'PAID_MEMBER'
export interface MemberBenefitItem {
  extendValue: string
  memberRightsId: string
  rightsName: string
  rightsType: keyof typeof BENEFIT_TYPE
  memberName: string
}
