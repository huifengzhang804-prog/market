<script setup lang="ts">
const $props = defineProps({
  msg: { type: String, required: true },
  isMine: { type: Boolean, required: true },
})
</script>

<template>
  <view class="chat-body-content" :class="$props.isMine ? '' : 'coustomer-body-content'">
    <text>
      {{ $props.msg }}
    </text>
  </view>
</template>

<style scoped lang="scss">
$content-bg: #fff;
$coustomer-bg: #94eb6e;
@include b(chat-body-content) {
  position: relative;
  border-radius: 15rpx;
  background: $content-bg;
  margin-top: 10rpx;
  padding: 20rpx;
  margin-left: 20rpx;
  &::after {
    content: '';
    position: absolute;
    left: -10rpx;
    top: 30rpx;
    height: 0;
    width: 0;
    border-top: 10rpx solid transparent;
    border-right: 10rpx solid $content-bg;
    border-bottom: 10rpx solid transparent;
  }
}
@include b(coustomer-body-content) {
  background: $coustomer-bg;
  margin: 0 20rpx 0 0;
  margin-top: 10rpx;
  &::after {
    display: none;
  }
  &::before {
    content: '';
    position: absolute;
    top: 30rpx;
    right: -10rpx;
    height: 0;
    width: 0;
    border-top: 10rpx solid transparent;
    border-left: 10rpx solid $coustomer-bg;
    border-bottom: 10rpx solid transparent;
  }
}
</style>
