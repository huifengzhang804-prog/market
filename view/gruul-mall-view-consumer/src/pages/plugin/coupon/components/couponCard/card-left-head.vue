<script setup lang="ts">
defineProps({
  msg: {
    type: String,
    default: '',
  },
})
</script>

<template>
  <view v-if="msg" class="container">
    <text class="container--text">{{ msg }}</text>
  </view>
</template>

<style scoped lang="scss">
@include b(container) {
  position: absolute;
  left: 0;
  top: 0;
  width: 92rpx;
  height: 36rpx;
  z-index: 3;
  color: #fff;
  border-bottom-right-radius: 15rpx;
  border-top-left-radius: 15rpx;
  background: rgba(246, 87, 85);
  text-align: center;
  @include flex();
  @include m(text) {
    font-size: 16rpx;
    line-height: 26rpx;
  }
}
</style>
