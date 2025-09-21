<script setup lang="ts">
const $props = defineProps({
  text: {
    type: String,
    default: '完成',
  },
  bgColor: {
    type: String,
    default: '#ffffff',
  },
  fixed: {
    type: Boolean,
    default: false,
  },
})
const $emit = defineEmits(['click'])
const customStyle = {
  width: '85vw',
  height: '78rpx',
  background: 'linear-gradient(90deg, #F54319 2%, #fb4b5c 70%, #ff5c56 98%)',
  borderRadius: '36rpx',
  fontSize: '28rpx',
  textAlign: 'CENTER',
  color: '#ffffff',
  lineHeight: '40rpx',
}
</script>

<template>
  <view class="determine" :style="{ background: $props.bgColor }" :class="{ fixed: $props.fixed }">
    <view style="padding: 0 30rpx">
      <u-button type="error" :custom-style="customStyle" @click="$emit('click')">{{ $props.text }}</u-button>
    </view>
  </view>
</template>

<style scoped lang="scss">
@include b(determine) {
  margin: 30rpx 0;
}
@include b(fixed) {
  position: fixed;
  bottom: 0;
  width: 100%;
  height: 146rpx;
  z-index: 10;
  margin: 0 !important;
  @include flex;
}
</style>
