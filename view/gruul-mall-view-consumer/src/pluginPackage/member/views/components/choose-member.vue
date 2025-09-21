<script lang="ts" setup>
import { ref, type PropType, watch } from 'vue'
import ChooseMemberItem from '@pluginPackage/member/views/components/choose-member-item.vue'
import MemberPriceItem from '@pluginPackage/member/views/components/member-price-item.vue'
import { currentMember } from '@pluginPackage/member/views/components/currentMember'
import { PAY_TYPE } from '@/apis/paymentDetail/model'
import type { ApiMemberPaidMemberRank } from '@/apis/plugin/member/model'

const $props = defineProps({
  paidMemberRank: { type: Array as PropType<ApiMemberPaidMemberRank[]>, default: () => [] },
})
const currentRuleIndex = ref(0)

watch(
  () => $props.paidMemberRank,
  (val) => {
    currentMember.value = val[0]
  },
  {
    immediate: true,
  },
)

/**
 *  处理分页查询会员
 */
const handlePagingQueryMember = () => {}
/**
 * 查询会员开通价格
 * @param {*} e
 */
const handleChooseMember = () => {
  console.log('更新会员价格展示', currentMember.value)
}
/**
 * 选中当前价格
 * @param {*} e
 */
const handleChooseMemberPrice = (index: number) => {
  currentRuleIndex.value = index
  console.log('currentMember.value', currentMember.value)
}
/**
 * 处理分页查询更多价格
 */
const handlePagingQueryMorePrice = () => {}
/**
 * 跳转收银台
 */
const handleNavToPay = () => {
  const memberId = currentMember.value?.id
  const ruleId = currentMember.value?.paidRuleJson[currentRuleIndex.value].id
  const memberPrice = currentMember.value?.paidRuleJson[currentRuleIndex.value].price
  if (!memberPrice) {
    return
  }
  const extra = encodeURIComponent(JSON.stringify({ memberId, memberPrice, ruleId }))
  uni.navigateTo({
    url: `/basePackage/pages/pay/Index?orderType=${PAY_TYPE.MEMBER}&&extra=${extra}`,
  })
}
</script>

<template>
  <view class="member-container">
    <scroll-view
      scroll-x
      enhanced
      :show-scrollbar="false"
      style="width: 100%; white-space: nowrap; border-radius: 30rpx 0 0 0"
      @scrolltolower="handlePagingQueryMember"
    >
      <choose-member-item v-model:current-member="currentMember" :paid-member-rank="paidMemberRank" @choose-member="handleChooseMember" />
    </scroll-view>
  </view>
  <view class="member-price-box">
    <scroll-view
      scroll-x
      enhanced
      :show-scrollbar="false"
      style="white-space: nowrap; width: 100%; background: #fff"
      @scrolltolower="handlePagingQueryMorePrice"
    >
      <template v-for="(item, index) in currentMember?.paidRuleJson" :key="item.effectiveDurationType">
        <view style="display: inline-block" @click="handleChooseMemberPrice(index)">
          <member-price-item :active="index === currentRuleIndex" :paid-rule="item" />
        </view>
      </template>
    </scroll-view>
  </view>
  <!-- <view class="msg">到期自动续费，可随时取消.</view> -->
  <view class="open-btn">
    <u-button
      :custom-style="{ background: 'linear-gradient(86.68deg, rgba(255,46,100,1) 2.3%,rgba(255,40,40,1) 100.46%)' }"
      shape="circle"
      type="error"
      @click="handleNavToPay"
    >
      立即开通
    </u-button>
  </view>
</template>

<style lang="scss" scoped>
@include b(member-container) {
  background: #1b1b1d;
}

@include b(open-btn) {
  padding: 20rpx 40rpx;
  background: #fff;
}

@include b(member-price-box) {
  background: #fff;
}

@include b(msg) {
  background: #fff;
  padding: 0 40rpx;
  height: 30rpx;
  color: #999999;
  font-size: 24rpx;
}
</style>
