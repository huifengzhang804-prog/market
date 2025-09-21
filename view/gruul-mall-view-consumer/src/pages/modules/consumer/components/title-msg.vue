<script setup lang="ts">
const $props = defineProps({
  contentValue: { type: String, default: '猜你喜欢' },
  rightValue: { type: String, default: '爆品挑选为你倾献' },
})
</script>

<template>
  <view class="like__title">
    <text class="like__title--left">{{ $props.contentValue }}</text>
    <text class="like__title--right">{{ $props.rightValue }}</text>
  </view>
</template>

<style scoped lang="scss">
@include b(like) {
  @include e(title) {
    font-size: 32rpx;
    font-weight: Bold;
    color: #000000;
    margin: 25rpx 28rpx;
    @include m(left) {
      &::after {
        content: '';
        margin-left: 15rpx;
        padding-left: 15rpx;
        border-left: 1rpx solid #ccc;
        height: 30rpx;
        display: inline-block;
        vertical-align: middle;
      }
    }
    @include m(right) {
      font-size: 24rpx;
      font-weight: Normal;
      color: #949494;
    }
  }
}
</style>
