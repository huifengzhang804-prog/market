<script setup lang="ts">
import { ref } from 'vue'
import UserInfo from '@pluginPackage/member/views/components/user-info.vue'
import IntegralProgress from '@pluginPackage/member/views/components/integral-progress.vue'
import InterestsInfo from '@pluginPackage/member/views/components/interests-info.vue'
import ChooseMember from '@pluginPackage/member/views/components/choose-member.vue'
import InterestsView from '@pluginPackage/member/views/components/interests-view.vue'
import { doGetMemberCardInfo, doAddonMemberPaidMemberRank, doGetMemberGrow } from '@pluginPackage/member/apis'
import type { ApiMemberCardInfo, ApiMemberPaidMemberRank } from '@/apis/plugin/member/model'
import Auth from '@/components/auth/auth.vue'

const { saveMemberInfo } = useMember()
const memberCardInfo = ref<ApiMemberCardInfo>({
  memberLabel: {} as ApiMemberCardInfo['memberLabel'],
  memberType: 'FREE_MEMBER',
  userHeadPortrait: '',
  userNickname: '',
  growthValue: 0,
  rankCode: 1,
  currentMemberVO: {
    memberCardId: '',
    memberCardValidTime: '',
    memberName: '',
    relevancyRights: void 0,
    rankCode: 0,
  },
  growValue: 0,
})
const memberPaidMemberRank = ref<ApiMemberPaidMemberRank[]>([])

initGetMemberCardInfo()
initMemberPaidMemberRank()

/**
 *  获取会员中心信息
 */
async function initGetMemberCardInfo() {
  const { code, data } = await doGetMemberCardInfo()
  if (code !== 200) {
    uni.showToast({ title: '获取会员中心信息失败', icon: 'none' })
    return
  }
  memberCardInfo.value = data
  saveMemberInfo(data)
  if (data.memberType === 'FREE_MEMBER') {
    const { code: growCode, data: growData } = await doGetMemberGrow(data.currentMemberVO.rankCode)
    if (growCode !== 200) {
      uni.showToast({ title: '获取会员成长值失败', icon: 'none' })
      return
    }
    memberCardInfo.value.growValue = growData.needValue
    memberCardInfo.value.rankCode = growData.rankCode
  }
}
/**
 * 付费会员级别列表
 */
async function initMemberPaidMemberRank() {
  const { code, data } = await doAddonMemberPaidMemberRank()
  if (code !== 200) {
    uni.showToast({ title: '获取付费会员级别列表失败', icon: 'none' })
    return
  }
  if (data) {
    memberPaidMemberRank.value = data
  }
}
</script>
<template>
  <view class="head">
    <user-info :member-info="memberCardInfo" />
    <integral-progress v-if="memberCardInfo?.memberType === 'FREE_MEMBER'" :member-info="memberCardInfo" />
    <interests-info :member-info="memberCardInfo" />
  </view>
  <!-- 开通会员 s -->
  <choose-member v-if="memberPaidMemberRank.length" :paid-member-rank="memberPaidMemberRank" />
  <!-- 开通会员 e -->
  <!-- 会员权益展示 s -->
  <interests-view />
  <!-- 会员权益展示 e -->
  <Auth />
</template>

<style scoped lang="scss">
@include b(head) {
  padding: 30rpx 0;
  background-color: rgba(27, 27, 29, 1);
  text-align: center;
}
</style>
