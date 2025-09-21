<script setup lang="ts">
import { reactive, watch, onUnmounted, onBeforeUnmount } from 'vue'
import { isSameSecond, parseFormat, parseTimeData } from '@/libs/formatTime'

const $emit = defineEmits(['change', 'end', 'finish'])
const $props = defineProps({
  // 倒计时时长，单位ms
  timestamp: {
    type: [String, Number],
    default: 0,
  },
  // 时间格式，DD-日，HH-时，mm-分，ss-秒，SSS-毫秒
  format: {
    type: String,
    default: 'DD:HH:mm:ss',
  },
  // 是否自动开始倒计时
  autoStart: {
    type: Boolean,
    default: true,
  },
  customStyle: {
    type: [String, Object],
    default: '',
  },
  showDays: {
    type: Boolean,
    default: true,
  },
  showHours: { type: Boolean, default: true },
  showMinutes: { type: Boolean, default: true },
  showSeconds: { type: Boolean, default: true },
})
const countDownData = reactive({
  timer: null as unknown as NodeJS.Timeout,
  // 各单位(天，时，分等)剩余时间
  timeData: parseTimeData(0),
  // 格式化后的时间，如"03:23:21"
  formattedTime: '0',
  // 倒计时是否正在进行中
  runing: false,
  endTime: 0, // 结束的毫秒时间戳
  remainTime: 0, // 剩余的毫秒时间
})
defineExpose({
  reset,
  start,
})

watch(
  () => $props.timestamp,
  () => {
    reset()
  },
)
watch(
  () => $props.format,
  () => {
    pause()
    start()
  },
)
init()

function init() {
  reset()
}
/**
 * 开始倒计时
 */
function start() {
  // 标识为进行中
  if (countDownData.runing) return
  countDownData.runing = true
  // 结束时间戳 = 此刻时间戳 + 剩余的时间
  countDownData.endTime = Date.now() + countDownData.remainTime
  toTick()
}
/**
 * 根据是否展示毫秒，执行不同操作函数
 */
function toTick() {
  if ($props.format.indexOf('SSS') > -1) {
    microTick()
  } else {
    macroTick()
  }
}

function macroTick() {
  clearTimeout_()
  // 每隔一定时间，更新一遍定时器的值
  // 同时此定时器的作用也能带来毫秒级的更新
  countDownData.timer = setTimeout(() => {
    // 获取剩余时间
    const remain = getRemainTime()
    // 重设剩余时间
    if (!isSameSecond(remain, countDownData.remainTime) || remain === 0) {
      setRemainTime(remain)
    }
    // 如果剩余时间不为0，则继续检查更新倒计时
    if (countDownData.remainTime !== 0) {
      macroTick()
    }
  }, 30)
}
function microTick() {
  clearTimeout_()
  countDownData.timer = setTimeout(() => {
    setRemainTime(getRemainTime())
    if (countDownData.remainTime !== 0) {
      microTick()
    }
  }, 30)
}
/**
 * 获取剩余的时间
 */
function getRemainTime() {
  // 取最大值，防止出现小于0的剩余时间值
  return Math.max(countDownData.endTime - Date.now(), 0)
}
/**
 * 重置倒计时
 */
function reset() {
  pause()
  countDownData.remainTime = Number($props.timestamp)
  setRemainTime(countDownData.remainTime)
  if ($props.autoStart) {
    start()
  }
}
/**
 * 暂停倒计时
 * @param {*} params
 */
function pause() {
  countDownData.runing = false
  clearTimeout_()
}
/**
 * 设置剩余的时间
 * @param {*} remain
 */
function setRemainTime(remain: any) {
  countDownData.remainTime = remain
  // 根据剩余的毫秒时间，得出该有天，小时，分钟等的值，返回一个对象
  const timeData = parseTimeData(remain)
  $emit('change', timeData)
  // 得出格式化后的时间
  countDownData.formattedTime = parseFormat($props.format, timeData)
  // 如果时间已到，停止倒计时
  if (remain <= 0) {
    pause()
    $emit('end')
    $emit('finish')
  }
}
function clearTimeout_() {
  clearTimeout(countDownData.timer)
  countDownData.timer = null as unknown as NodeJS.Timeout
}
onUnmounted(() => {
  clearTimeout_()
})
onBeforeUnmount(() => {
  clearTimeout_()
})
</script>

<template>
  <view class="u-count-down">
    <slot>
      <text class="u-count-down__text" :style="customStyle">{{ countDownData.formattedTime }}</text>
    </slot>
  </view>
</template>

<style scoped lang="scss"></style>
