<script setup lang="ts">
import { ref, type PropType } from 'vue'
import type { ApiMemberPaidMemberRank } from '@/apis/plugin/member/model'

const $props = defineProps({
  paidMemberRank: { type: Array as PropType<ApiMemberPaidMemberRank[]>, default: () => [] },
  currentMember: { type: Object as PropType<ApiMemberPaidMemberRank>, default: () => {} },
})
const currentIndex = ref(0)
const emit = defineEmits(['chooseMember', 'update:currentMember'])

const handleChooseMember = (item: ApiMemberPaidMemberRank, idx: number) => {
  currentIndex.value = idx
  emit('update:currentMember', item)
  emit('chooseMember')
}
</script>

<template>
  <view class="member-box">
    <view v-for="(item, idx) in paidMemberRank" :key="item.id" class="member-box__container" @click="handleChooseMember(item, idx)">
      <view :class="currentIndex === idx && 'active'" class="member-box__item">{{ item.paidMemberName }}</view>
    </view>
  </view>
</template>

<style scoped lang="scss">
@include b(member-box) {
  position: relative;
  height: 80rpx;
  border-radius: 30rpx 30rpx 0 0;
  background-color: #454545;
  width: fit-content;
  @include e(container) {
    display: inline-block;
    &:nth-child(1) {
      border-radius: 30rpx 0 0 0;
    }
    &:last-child {
      border-radius: 0 30rpx 0 0;
    }
  }
  @include e(item) {
    font-size: 24rpx;
    color: #ffe6b4;
    width: 200rpx;
    display: inline-block;
    height: 80rpx;
    line-height: 80rpx;
    text-align: center;
    border-radius: 30rpx 30rpx 0 0;
    font-family: PingFangSC-regular;
    z-index: 9;
  }
}
@include b(active) {
  border-radius: 30rpx 30rpx 0 0;
  font-size: 24rpx;
  background: #fff;
  color: #101010;
}
</style>
