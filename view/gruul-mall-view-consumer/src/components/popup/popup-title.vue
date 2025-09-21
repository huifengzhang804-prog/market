<script setup lang="ts">
import { ref } from 'vue'

const $props = defineProps({
  title: {
    type: String,
    default: '优惠详情',
  },
})
const emit = defineEmits(['close'])
</script>

<template>
  <view class="popup-title">
    <text>{{ $props.title }}</text>
    <u-icon name="close" size="24" class="popup-icon" @click="emit('close', false)" />
  </view>
</template>

<style scoped lang="scss">
@include b(popup-title) {
  position: relative;
  font-size: 30rpx;
  font-weight: bold;
  text-align: center;
  color: #000000;
  height: 100rpx;
  line-height: 100rpx;
}
@include b(popup-icon) {
  position: absolute;
  right: 40rpx;
  height: 100rpx;
  line-height: 100rpx;
}
</style>
