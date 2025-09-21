<script setup lang="ts">
import { ref, defineProps, defineEmits, onBeforeUnmount, watch, defineExpose } from 'vue'
import DateUtils from '@/utils/date'
/**
 * @description: 59 分 59 秒 用于计算 秒杀结束时间
 */
const FIXED_POINT_TIME = 60 * 60 * 1000 - 1000
/**
 * @description: 时间格式化工具（倒计时转化）
 * @param {number} data
 * @returns {*}
 */
function toHHmmss(data: number) {
  let time
  let hours = parseInt(((data % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)).toString())
  let minutes = parseInt(((data % (1000 * 60 * 60)) / (1000 * 60)).toString())
  let seconds = parseInt(((data % (1000 * 60)) / 1000).toString())
  time = (hours < 10 ? '0' + hours : hours) + ':' + (minutes < 10 ? '0' + minutes : minutes) + ':' + (seconds < 10 ? '0' + seconds : seconds)
  return time
}

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
        <text :class="['countdown-view--item', 'countdown-view--item-day']"> {{ dayFormat(startTime) }}</text>
        <text class="countdown-view--item-pendant">天</text>
      </template>
      <text :class="['countdown-view--item']">{{ countdownTimeS[0] }}</text>
      <text class="countdown-view--item-pendant">:</text>
      <text :class="['countdown-view--item']">{{ countdownTimeS[1] }}</text>
      <text class="countdown-view--item-pendant">:</text>
      <text :class="['countdown-view--item']">{{ countdownTimeS[2] }}</text>
    </slot>
  </view>
</template>

<style lang="scss" scoped>
@include b(countdown-view) {
  display: flex;
  align-items: center;
  @include m(item) {
    border-radius: 50%;
    text-align: center;
  }
  @include m(item-pendant) {
    margin: 0 2px;
  }
  @include m(item-day) {
    margin-right: 5rpx;
  }
}
</style>
