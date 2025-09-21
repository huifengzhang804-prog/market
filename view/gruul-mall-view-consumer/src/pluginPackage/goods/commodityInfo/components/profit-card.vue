<template>
  <view class="profit">
    <text class="profit__text" :class="{ unActive__text: unActive }">{{ text }}</text>
    <text class="profit__price" :class="{ unActive__price: unActive }">￥{{ price || '0.00' }}</text>
  </view>
</template>

<script setup lang="ts">
withDefaults(defineProps<{ price: Long; unActive: boolean; text?: string }>(), {
  price: '',
  unActive: false,
  text: '预计赚',
})
</script>

<style scoped lang="scss">
@include b(profit) {
  display: flex;
  // justify-content: space-between;
  // width: 214rpx;
  height: 45rpx;
  line-height: 45rpx;
  text-align: center;
  font-size: 24rpx;
  color: #fa3534;
  @include e(text) {
    width: 106rpx;
    background: #ffffff;
    flex-shrink: 0;
  }
  @include e(price) {
    display: inline-block;
    border: 1rpx solid #ffffff;
    // width: 110rpx;
    height: 45rpx;
    line-height: 40rpx;
    color: #fff;
    padding: 0 4rpx;
  }
}
@include b(unActive) {
  @include e(text) {
    background: #fa3534;
    color: #fff;
  }
  @include e(price) {
    border: 1rpx solid #fa3534;
    color: #fa3534;
    white-space: nowrap;
  }
}
</style>
