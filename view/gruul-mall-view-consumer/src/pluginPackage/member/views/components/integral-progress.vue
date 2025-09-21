<script setup lang="ts">
import { type PropType, computed } from 'vue'
import type { ApiMemberCardInfo } from '@/apis/plugin/member/model'

const $props = defineProps({
  memberInfo: { type: Object as PropType<ApiMemberCardInfo>, default: () => ({}) },
})

function computedGrowthValue() {
  if ($props.memberInfo.growValue && $props.memberInfo.growthValue) {
    const growValue = $props.memberInfo.growValue - $props.memberInfo.growthValue
    return growValue <= 0 ? 0 : growValue
  }
  return 0
}
const growPercent = computed(() => {
  return Math.floor(($props.memberInfo.growthValue / $props.memberInfo.growValue) * 100)
})
</script>

<template>
  <view class="top-title">
    <text>成长值</text>
    <text>升级LV{{ memberInfo.rankCode }}</text>
  </view>
  <view style="padding: 0 30rpx">
    <u-line-progress active-color="#FFE6B4" :percent="growPercent * 1" :round="false" height="8" :show-percent="false" inactive-color="#666666" />
  </view>
  <view class="describe">
    <view class="describe__right">
      <text>
        <text class="describe--current">{{ memberInfo.growthValue }}</text>
        <text>/{{ memberInfo.growValue }}</text>
      </text>
    </view>
    <view>还差{{ computedGrowthValue() }}</view>
  </view>
</template>

<style scoped lang="scss">
@include b(top-title) {
  padding: 0 30rpx;
  margin-top: 30rpx;
  color: #cccccc;
  font-size: 24rpx;
  @include flex(space-between);
  align-items: center;
}
@include b(describe) {
  padding: 0 30rpx;
  margin-top: 12rpx;
  @include flex(space-between);
  align-items: center;
  color: #cccccc;
  font-size: 24rpx;
  @include e(right) {
    @include flex(space-between);
  }
  @include m(current) {
    font-size: 34rpx;
    color: #ffffff;
    font-weight: bold;
  }
}
</style>
