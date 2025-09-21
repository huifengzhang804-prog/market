<script setup lang="ts">
import { ref } from 'vue'

const livePusherLayout = ref({
  width: 0,
  height: 0,
})
defineProps({
  padding: { type: String, default: '20rpx' },
  activeColor: {
    type: String,
    default: '#FA3534',
  },
  backgroundColor: {
    type: String,
    default: '#fff',
  },
  strokeWidth: {
    type: String,
    default: '6',
  },
  borderRadius: {
    type: String,
    default: '16',
  },
})
const percent = ref(20)
const startX = ref(0)
//初始化点击位置的x坐标

uni.getSystemInfo({
  success: (res) => {
    const { windowWidth, windowHeight } = res
    livePusherLayout.value.width = windowWidth
    livePusherLayout.value.height = windowHeight
  },
})

function touchStart(e: Obj) {
  if (e.touches.length === 1) {
    //设置触摸起始点水平方向位置
    startX.value = e.touches[0].screenX
  }
}
function touchEnd(e: Obj) {
  if (e.changedTouches.length === 1) {
    //手指移动结束后水平位置
    var endX = e.changedTouches[0].screenX
    let diff = endX - startX.value
    if (Math.abs(diff) > 20) {
      if (diff > 0) {
        console.log('左滑...')
        if (percent.value !== 100) {
          percent.value += 20
        }
      } else {
        console.log('右滑...')
        if (percent.value > 0) {
          percent.value -= 20
        }
      }
    }
  }
}
</script>

<template>
  <view :style="{ padding: padding }" @touchstart="touchStart" @touchend="touchEnd">
    <progress
      :border-radius="borderRadius"
      :percent="percent"
      :stroke-width="strokeWidth"
      :activeColor="activeColor"
      :backgroundColor="backgroundColor"
    />
  </view>
</template>

<style scoped lang="scss"></style>
