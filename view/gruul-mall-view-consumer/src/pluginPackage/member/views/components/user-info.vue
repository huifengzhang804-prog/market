<script setup lang="ts">
import { type PropType, watch } from 'vue'
import type { ApiMemberCardInfo } from '@/apis/plugin/member/model'

const $props = defineProps({
  memberInfo: { type: Object as PropType<ApiMemberCardInfo>, default: () => ({}) },
})

watch(
  () => $props.memberInfo,
  () => {
    console.log('$props.memberInfo', $props.memberInfo)
  },
)
</script>

<template>
  <view class="info">
    <u-image :width="120" :height="120" :src="memberInfo.userHeadPortrait" shape="circle" />
    <view class="info__right">
      <text class="info__right--name">{{ memberInfo.userNickname }}</text>
      <view>
        <u-tag
          v-if="memberInfo.currentMemberVO"
          :text="memberInfo.currentMemberVO.memberName"
          plain
          size="mini"
          border-color="transparent"
          :color="memberInfo?.memberLabel?.fontColor"
          style="font-weight: bold"
          :bg-color="memberInfo?.memberLabel?.labelColor"
        />
        <!-- <u-tag
                    v-if="memberInfo.memberType !== 'FREE_MEMBER'"
                    text="付费"
                    plain
                    shape="circle"
                    size="mini"
                    border-color="transparent"
                    color="#ff564b"
                    style="font-weight: bold; margin-left: 10rpx"
                    bg-color="rgb(224, 192, 167)"
                /> -->

        <!-- <u-tag
                    :text="memberInfo.memberType === 'FREE_MEMBER' ? '免费' : '付费'"
                    plain
                    shape="circle"
                    size="mini"
                    border-color="transparent"
                    :color="memberInfo.memberType === 'FREE_MEMBER' ? '#333333' : '#ff564b'"
                    style="font-weight: bold; margin-left: 10rpx"
                    :bg-color="memberInfo.memberType === 'FREE_MEMBER' ? '#fff' : 'rgb(224, 192, 167)'"
                /> -->
        <text v-if="memberInfo.memberType === 'FREE_MEMBER'" class="info__right--msg">您还不是付费会员~</text>
        <text v-else class="info__right--msg">{{ memberInfo.currentMemberVO?.memberCardValidTime || new Date().toLocaleDateString() }} 到期</text>
      </view>
    </view>
  </view>
</template>

<style scoped lang="scss">
@include b(info) {
  padding: 0 30rpx;
  @include flex;
  justify-content: flex-start;
  @include e(right) {
    height: 120rpx;
    @include flex;
    flex-direction: column;
    justify-content: space-around;
    align-items: flex-start;
    padding: 0 20rpx;
    @include m(name) {
      text-align: left;
      // width: 200rpx;
      color: rgba(255, 255, 255, 1);
      font-size: 30rpx;
    }
    @include m(msg) {
      color: rgba(204, 204, 204, 1);
      font-size: 24rpx;
      padding: 0 10rpx;
    }
  }
}
</style>
