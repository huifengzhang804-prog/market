import type { BENEFIT_TYPE, MemberBenefitItem } from '@/store/modules/user/state'

enum MERBER_TYPE {
  FREE_MEMBER = '免费会员',
  PAID_MEMBER = '付费会员',
}
type MemberTypeJoint = keyof typeof MERBER_TYPE
/**
 * 会员权益扩展值
 */
enum RIGHTS_TYPE {
  GOODS_DISCOUNT = '商品抵扣',
  INTEGRAL_MULTIPLE = '积分加倍',
  LOGISTICS_DISCOUNT = '物流优惠',
  PRIORITY_SHIPMENTS = '优先发货',
  QUICKNESS_AFS = '极速售后',
  EXCLUSIVE_SERVICE = '专属客服',
  USER_DEFINED = '自定义',
}
type RightsTypeJoint = keyof typeof RIGHTS_TYPE
/**
 * 有效的时间类型
 */
enum EFFECTIVE_DURATION_TYPE {
  ONE_MONTH = '一个月',
  THREE_MONTH = '三个月',
  TWELVE_MONTH = '12个月',
  THREE_YEAR = '三年',
  FIVE_YEAR = '五年',
}
type EfficientTimeTypeJoint = keyof typeof EFFECTIVE_DURATION_TYPE
/**
 * 会员卡片展示数据
 * @param: memberType 用户所使用得会员卡类型
 * @param: userHeadPortrait 用户头像
 * @param: userNickname 用户昵称
 * @param: growthValue 用户成长值
 * @param: currentMemberVO 当前所使用得会员
 * @param: rankCode 下一级的会员
 */
export interface ApiMemberCardInfo {
  memberLabel: {
    fontColor: string
    id: string
    labelColor: string
    name: string
    priceFontColor: string
    priceLabelColor: string
    priceLabelName: string
  }
  memberType?: MemberTypeJoint
  userHeadPortrait: string
  userNickname: string
  growthValue: number
  currentMemberVO?: CurrentMemberVO
  growValue: number
  rankCode: number
}
/**
 * 当前所使用得会员
 * @param: memberCardId 会员卡id
 * @param: memberCardValidTime 会员卡有效时长
 * @param: relevancyRights 会员级别关联权益
 * @param: rankCode 会员等级
 */
interface CurrentMemberVO {
  memberCardId: string
  memberCardValidTime: string
  relevancyRights?: Record<keyof typeof BENEFIT_TYPE, MemberBenefitItem>
  rankCode: number
  memberName: string
}
/**
 * 会员级别关联权益
 * @param: memberRightsId 会员权益id
 * @param: extendValue 会员权益扩展值
 * @param: rightsType 权益类型
 * @param: rightsName 权益名称
 * @param: rightsExplain 权益说明
 * @param: rightsIcon 权益图标
 */
export interface RelevancyRights {
  memberRightsId: string
  extendValue: string
  rightsExplain: string
  rightsType: RightsTypeJoint
  rightsName: string
  rightsIcon: string
}
// 会员列表展示
/**
 * 会员付费会员等级
 * @param {*} id 付费会员id
 * @param {*} paidMemberName 付费会员名称
 * @param {*} paidRuleJson 付费规则
 * @param {*} relevancyRightsList 会员关联权益VO
 */
export interface ApiMemberPaidMemberRank {
  id: string
  paidMemberName: string
  paidRuleJson: PaidRuleJson[]
  relevancyRightsList: RelevancyRights[]
}
export interface PaidRuleJson {
  effectiveDurationType: EfficientTimeTypeJoint
  id: string
  price: Long
}
