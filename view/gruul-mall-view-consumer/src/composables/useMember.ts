import { useAppStore } from '@/store/modules/app'
import { useUserStore } from '@/store/modules/user'
import { doGetMemberCardInfo } from '@/apis/sign'
import type { BENEFIT_TYPE, MemberBenefitItem, UserStateType } from '@/store/modules/user/state'
import { computed } from 'vue'
import type { ApiMemberCardInfo, RelevancyRights } from '@/apis/plugin/member/model'

const { divThousand, divTenThousand, fixedUp } = useConvert()

function memberPrice(val: string | number | DecimalType) {
  if (includeDiscount()) {
    const memberInfo = useUserStore().getterMemberInfo
    const preferentialRate = memberInfo?.memberBenefit?.GOODS_DISCOUNT.extendValue
    return fixedUp(divTenThousand(val).mul(divThousand(preferentialRate)))
  } else {
    return fixedUp(divTenThousand(val))
  }
}
const isExist = computed(() => !!useAppStore().GET_PLUGIN('addon-full-reduction'))
/**
 * 获取会员信息
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
      uni.showToast({
        icon: 'none',
        title: '获取会员信息失败',
      })
      return false
    }
  } catch (error) {
    uni.showToast({
      icon: 'none',
      title: '获取会员信息失败',
    })
    return false
  }
}

/**
 * 存储会员信息
 */
function saveMemberInfo(data: ApiMemberCardInfo) {
  const tempObj: UserStateType['member'] = {
    memberType: '',
    memberBenefit: null,
    memberName: '',
    rankCode: 0,
    memberCardValidTime: '',
  }
  if (data.memberType) {
    tempObj.memberType = data.memberType
  }
  tempObj.rankCode = data.currentMemberVO?.rankCode || 0
  tempObj.memberCardValidTime = data.currentMemberVO?.memberCardValidTime || ''
  if (data.currentMemberVO && data.currentMemberVO.relevancyRights) {
    tempObj.memberBenefit = data.currentMemberVO.relevancyRights
    tempObj.memberName = data.currentMemberVO.memberName
  }
  useUserStore().SET_MEMBER_INFO(tempObj)
}
/**
 * 是否是付费会员
 */
function isPaidMember() {
  if (useUserStore().member) {
    return useUserStore().member?.memberType === 'PAID_MEMBER'
  }
  return false
}
/**
 * 判断是否存在商品折扣减免
 */
function includeDiscount() {
  const memberInfo = useUserStore().getterMemberInfo
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
  const memberInfo = useUserStore().getterMemberInfo
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
