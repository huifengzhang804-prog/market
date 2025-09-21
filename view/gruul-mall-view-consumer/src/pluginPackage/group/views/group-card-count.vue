<script setup lang="ts">
import type { PropType } from 'vue'
import { useCountdown } from '@/hooks/useCountdown/useCountdown'
import type { StorageSku } from '@/pluginPackage/goods/commodityInfo/types'

const $props = defineProps({
  time: {
    type: String,
    default: '',
  },
  currentChoosedSku: {
    type: Object as PropType<StorageSku>,
    default() {
      return {}
    },
  },
  getTip: {
    type: String,
    default: '',
  },
})
const { timeSet } = useCountdown($props.time, { immediate: true }, () => {
  console.log('倒计时结束')
  $emit('end')
})

const $emit = defineEmits(['end'])
</script>

<template>
  <view class="count">
    <view>{{ getTip }}&nbsp;</view>
    <view class="count__time">
      <view class="count__time-box">{{ timeSet.days }}</view>
      <view style="color: #ff5176; margin: 0 4rpx">:</view>
      <view class="count__time-box">{{ timeSet.hours }}</view>
      <view style="color: #ff5176; margin: 0 4rpx">:</view>
      <view class="count__time-box">{{ timeSet.minutes }}</view>
      <view style="color: #ff5176; margin: 0 4rpx">:</view>
      <view class="count__time-box">{{ timeSet.seconds }}</view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
@include b(count) {
  font-size: 28rpx;
  margin: 8rpx 0;
  color: #fff;
  @include flex;
  @include e(time) {
    @include flex(flex-end);
    min-width: 200rpx;
  }
  @include e(time-box) {
    border-radius: 6rpx;
    padding: 4rpx;
    background-color: #ff5176;
    font-size: 24rpx;
  }
}
</style>
