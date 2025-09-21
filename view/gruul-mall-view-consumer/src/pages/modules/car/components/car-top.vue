<script setup lang="ts">
defineProps({
  value: {
    type: String,
    default: '',
  },
  num: {
    type: Number,
    default: 0,
  },
})
const $emit = defineEmits(['right-click'])
</script>

<template>
  <view class="flex-space-between car-top f13">
    <view>
      购物车 (<text class="car-top__left">{{ num }}</text
      >)
    </view>
    <text class="car-top__right" @click="$emit('right-click')">{{ value }}</text>
  </view>
</template>

<style scoped lang="scss">
@include b(car-top) {
  align-items: center;
  width: 100%;
  height: 80rpx;
  padding: 0 30rpx;
  background: #fff;
  @include e(left) {
    color: $qszr-red;
  }

  @include e(right) {
    height: 60rpx;
    line-height: 60rpx;
    padding-left: 15rpx;
    border-left: 1px solid #ccc;
  }
  @include e(empty) {
    color: #0f40f5;
  }
}
.ml15 {
  margin-left: 15rpx;
}
</style>
