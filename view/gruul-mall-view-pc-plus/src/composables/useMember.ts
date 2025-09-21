import { computed } from 'vue'
import { useAppStore } from '@/store/modules/app'
import { useUserStore } from '@/store/modules/user'
// import { doGetMemberCardInfo } from '@/apis/sign'
import type { BENEFIT_TYPE, MemberBenefitItem } from '@/store/modules/user/state'
import useConvert from './useConvert'
import { doGetMemberCardInfo } from '@/apis/GroupPurchase'
import { ElMessage } from 'element-plus'
import useMemberStore from '@/store/modules/member'
import Decimal from 'decimal.js'
const { divThousand, divTenThousand, fixedUp } = useConvert()
export type ApiMemberInfoType = {
  memberType?: string
  currentMemberVO?: {
    [key: string]: any
    memberName: string
    relevancyRights?: Record<keyof typeof BENEFIT_TYPE, MemberBenefitItem>
  }
}
function memberPrice(val: string | number | Decimal) {
  if (includeDiscount()) {
    const memberInfo = useMemberStore().getterMemberInfo
    const preferentialRate = memberInfo?.memberBenefit?.GOODS_DISCOUNT.extendValue
    return fixedUp(divTenThousand(val).mul(divThousand(preferentialRate)))
  } else {
    return fixedUp(divTenThousand(val))
  }
}
const isExist = computed(() => !!useAppStore().GET_PLUGIN('addon-full-reduction'))
/**
 * @description: 获取会员信息
 */
async function doRequest() {
  try {
    if (!useUserStore().getterToken) return false
    const { code, data } = await doGetMemberCardInfo()
    if (code === 200 && data) {
      // 更新会员缓存信息
      saveMemberInfo(data)
      return true
    } else {
      ElMessage.error({ message: '获取会员信息失败' })
      return false
    }
  } catch (error) {
    ElMessage.error({ message: '获取会员信息失败' })
    return false
  }
}

/**
 * @description: 存储会员信息
 */
function saveMemberInfo(data: ApiMemberInfoType) {
  const tempObj = {
    memberType: '',
    memberBenefit: null,
    memberName: '',
    rankCode: '',
    memberCardValidTime: '',
  }
  if (data.memberType) {
    tempObj.memberType = data.memberType
  }
  tempObj.rankCode = data.currentMemberVO?.rankCode || ''
  tempObj.memberCardValidTime = data.currentMemberVO?.memberCardValidTime || ''
  if (data.currentMemberVO && data.currentMemberVO.relevancyRights) {
    tempObj.memberBenefit = data.currentMemberVO.relevancyRights as any
    tempObj.memberName = data.currentMemberVO.memberName
  }
  useMemberStore().SET_MEMBER_INFO(tempObj)
}
/**
 * @description: 是否是付费会员
 * @returns {*}
 */
function isPaidMember() {
  if (useUserStore().member) {
    return useUserStore().member?.memberType === 'PAID_MEMBER'
  }
  return false
}
/**
 * @description: 判断是否存在商品折扣减免
 */
function includeDiscount() {
  const memberInfo = useMemberStore().getterMemberInfo
  if (!memberInfo || !memberInfo.memberBenefit) {
    return false
  }
  if (memberInfo.memberBenefit['GOODS_DISCOUNT']) {
    return true
  } else {
    return false
  }
}
function includeBenefit(str: keyof typeof BENEFIT_TYPE) {
  const memberInfo = useMemberStore().getterMemberInfo
  if (memberInfo && memberInfo.memberBenefit) {
    return Boolean(memberInfo.memberBenefit[str])
  } else {
    return false
  }
}
export default function useMember() {
  return {
    memberPrice,
    saveMemberInfo,
    isPaidMember,
    includeDiscount,
    includeBenefit,
    doRequest,
    isExist,
  }
}
