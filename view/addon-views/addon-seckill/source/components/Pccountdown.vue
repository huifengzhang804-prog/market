<script setup lang="ts">
import { ref, defineProps, defineEmits, onBeforeUnmount, watch, defineExpose } from 'vue'
import { FIXED_POINT_TIME, toHHmmss } from '../index'
import DateUtils from '../index'

const $props = defineProps({
  startTime: { type: String, default: '' },
  isStart: { type: Boolean, default: false },
})
const $emit = defineEmits(['end'])
let interval: NodeJS.Timeout | null = null
const countdownTimeS = ref<string[]>(['00', '00', '00'])
const dateTool = new DateUtils()

watch(
  () => $props.startTime,
  () => {
    loadCountdown()
  },
)
loadCountdown()

function loadCountdown() {
  interval && clearInterval(interval)
  countdownTimeS.value = ['00', '00', '00']
  initSuccessTime($props.isStart)
}
onBeforeUnmount(() => {
  interval && clearInterval(interval)
})
function initSuccessTime(isStart: boolean) {
  if (!$props.startTime) return
  const currentTime = new Date().getTime()
  const startTimeS = dateTool.getTime(new Date($props.startTime))
  let countdownTime = isStart ? startTimeS - currentTime : startTimeS + FIXED_POINT_TIME - currentTime
  if (countdownTime < 0) return
  interval = setInterval(() => {
    // 倒计时结束
    if (countdownTime < 1000) {
      $emit('end')
      interval && clearInterval(interval)
      return
    }
    countdownTime = countdownTime - 1000
    countdownTimeS.value = toHHmmss(countdownTime).split(':')
  }, 1000)
}
function dayFormat(time: string) {
  const currentTime = new Date()
  const rangeDateNum = (new Date(time).getTime() - currentTime.getTime()) / (1000 * 3600 * 24)
  const day = Math.floor(rangeDateNum) < 0 ? '' : Math.floor(rangeDateNum) + '天'
  return Math.floor(rangeDateNum) < 0 ? '' : Math.floor(rangeDateNum)
}
defineExpose({ loadCountdown })
</script>

<template>
  <view class="countdown-view">
    <slot name="default" :time-arr="countdownTimeS">
      <template v-if="dayFormat(startTime)">
        <text class="countdown-view--item countdown-view--item-day"> {{ dayFormat(startTime) }}</text>
        <text class="countdown-view--item-pendant">天</text>
      </template>
      <text class="countdown-view--item">{{ countdownTimeS[0] }}</text>
      <text class="countdown-view--item-pendant">:</text>
      <text class="countdown-view--item">{{ countdownTimeS[1] }}</text>
      <text class="countdown-view--item-pendant">:</text>
      <text class="countdown-view--item">{{ countdownTimeS[2] }}</text>
    </slot>
  </view>
</template>

<style scoped lang="scss">
@include b(countdown-view) {
  display: inline-flex;
  color: #000;
  @include m(item) {
    height: 46rpx;
    line-height: 46rpx;
    width: 46rpx;
    border-radius: 50%;
    font-size: 28rpx;
    text-align: center;
    color: #ffffff;
  }
  @include m(item-pendant) {
    height: 46rpx;
    line-height: 46rpx;
    margin: 0 5rpx;
    color: #ffffff;
  }
  @include m(item-day) {
    margin-right: 5rpx;
  }
}
</style>
