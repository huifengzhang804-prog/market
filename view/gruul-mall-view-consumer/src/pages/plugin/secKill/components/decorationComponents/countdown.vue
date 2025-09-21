<script lang="ts" setup>
import { onBeforeUnmount, ref, watch } from 'vue'
import { toHHmmss } from './countdown'
import DateUtils from '@/utils/date'

const $props = defineProps({
  startTime: { type: String, default: '' },
  isStart: { type: Boolean, default: true },
  bg: { type: Boolean, default: true },
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
  if (interval) {
    clearInterval(interval)
  }
  countdownTimeS.value = ['00', '00', '00']
  initSuccessTime($props.isStart)
}

onBeforeUnmount(() => {
  if (interval) {
    clearInterval(interval)
  }
})

function initSuccessTime(isStart: boolean) {
  console.log($props.startTime, 'startTime')
  if (!$props.startTime) return
  const currentTime = new Date().getTime()
  const startTimeS = dateTool.getTime($props.startTime)
  let countdownTime = startTimeS - currentTime
  if (countdownTime < 0) return
  interval = setInterval(() => {
    // 倒计时结束
    if (countdownTime < 1000) {
      $emit('end')
      if (interval) {
        clearInterval(interval)
      }
      return
    }
    countdownTime = countdownTime - 1000
    countdownTimeS.value = toHHmmss(countdownTime).split(':')
  }, 1000)
}

function dayFormat(time: string) {
  const currentTime = new Date()
  //修复部分IOS不兼容new Date('yyyy-MM-dd hh:mm:ss') 的问题 转成 yyyy/MM/dd hh:mm:ss格式处理
  const rangeDateNum = (new Date(time.replace(/-/g, '/')).getTime() - currentTime.getTime()) / (1000 * 3600 * 24)
  // const day = Math.floor(rangeDateNum) < 0 ? '' : Math.floor(rangeDateNum) + '天'
  return Math.floor(rangeDateNum) < 0 ? '' : Math.floor(rangeDateNum) < 10 ? `0${Math.floor(rangeDateNum)}` : Math.floor(rangeDateNum)
}

defineExpose({ loadCountdown })
</script>

<template>
  <view class="countdown-view">
    <slot :day="dayFormat(startTime)" :timeArr="countdownTimeS" name="default">
      <template v-if="+dayFormat(startTime) > 0">
        <text :class="[bg && 'bg', 'countdown-view--item', 'countdown-view--item-day']"> {{ dayFormat(startTime) }}</text>
        <text class="countdown-view--item-pendant">天</text>
      </template>
      <text :class="[bg && 'bg', 'countdown-view--item']">{{ countdownTimeS[0] }}</text>
      <text class="countdown-view--item-pendant">:</text>
      <text :class="[bg && 'bg', 'countdown-view--item']">{{ countdownTimeS[1] }}</text>
      <text class="countdown-view--item-pendant">:</text>
      <text :class="[bg && 'bg', 'countdown-view--item']">{{ countdownTimeS[2] }}</text>
    </slot>
  </view>
</template>

<style lang="scss" scoped>
@include b(countdown-view) {
  display: inline-flex;
  color: #333;
  @include m(item) {
    height: 46rpx;
    line-height: 46rpx;
    width: 46rpx;
    border-radius: 50%;
    font-size: 24rpx;
    text-align: center;
  }
  .bg {
    background: #000;
    color: #ffffff;
  }
  @include m(item-pendant) {
    height: 46rpx;
    line-height: 46rpx;
    margin: 0 5rpx;
  }
  @include m(item-day) {
    margin-right: 5rpx;
  }
}
</style>
