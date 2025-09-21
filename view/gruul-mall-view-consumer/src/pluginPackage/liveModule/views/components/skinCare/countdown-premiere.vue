<script setup lang="ts">
import { ref } from 'vue'

defineProps({
  top: {
    type: Number,
    default: 0,
  },
  left: {
    type: Number,
    default: 0,
  },
})
const emit = defineEmits(['end'])
const down = ref(3)

downTime()

function downTime(t = 1000) {
  let time = setTimeout(() => {
    if (down.value > 1) {
      down.value -= 1
      downTime()
      clearTimeout(time)
    } else {
      emit('end')
    }
  }, t)
}
</script>

<template>
  <view
    class="countdown"
    :style="{
      top: top + 'px',
      left: left + 'px',
    }"
  >
    <text class="countdown__text">{{ down }}</text>
  </view>
</template>

<style scoped>
.countdown {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
}
.countdown__text {
  font-size: 144rpx;
  color: #fff;
}
</style>
