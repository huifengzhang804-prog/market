<script setup lang="ts">
import { useCountdownTime } from '@/utils/useCountdownTime'

const $props = defineProps({
  order: {
    type: Object,
    default: () => {},
  },
})
let interval: NodeJS.Timeout | null = null
const TimeS = ref('')

timeCountdown()
onBeforeUnmount(() => {
  if (interval) {
    clearInterval(interval)
  }
})

function timeCountdown() {
  if (!$props.order.timeout?.payTimeout) return
  let countdownTime = useCountdownTime($props.order.createTime, Number($props.order.timeout?.payTimeout))
  interval = setInterval(() => {
    // 倒计时结束
    if (countdownTime < 1000) {
      if (interval) {
        clearInterval(interval)
      }
      return
    }
    countdownTime = countdownTime - 1000
    let time
    let hours = parseInt(((countdownTime % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)).toString())
    let minutes = parseInt(((countdownTime % (1000 * 60 * 60)) / (1000 * 60)).toString())
    let seconds = parseInt(((countdownTime % (1000 * 60)) / 1000).toString())
    time =
      (hours < 10 ? '0' + hours : hours) + '时' + (minutes < 10 ? '0' + minutes : minutes) + '分' + (seconds < 10 ? '0' + seconds : seconds) + '秒'
    TimeS.value = time
  }, 1000)
}
</script>

<template>
  <div>
    {{ TimeS || '00:00:00' }}
  </div>
</template>

<style lang="scss" scoped></style>
