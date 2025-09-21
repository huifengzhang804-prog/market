<script setup lang="ts">
const $props = defineProps({
  title: {
    type: String,
    default: '',
  },
  background: {
    type: String,
    default: '',
  },
  width: {
    type: String,
    default: '100vw',
  },
  height: {
    type: String,
    default: '100dvh',
  },
  top: {
    type: String,
    default: '0',
  },
})
</script>

<template>
  <view class="empty" :style="{ width: $props.width, height: $props.height, top: $props.top }">
    <view>
      <image :src="$props.background" mode="aspectFit" class="empty__icon" />
    </view>
    <view class="empty__title">{{ $props.title }}</view>
    <slot></slot>
  </view>
</template>

<style lang="scss" scoped>
@include b(empty) {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  @include e(icon) {
    width: 548rpx;
    height: 450rpx;
  }
  @include e(title) {
    font-size: 24rpx;
    color: #b7b7b7;
    margin-top: 10rpx;
  }
}
</style>
