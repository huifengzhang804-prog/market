<script setup lang="ts">
const $props = defineProps({
  loading: {
    type: Boolean,
    default: false,
  },
})
</script>

<template>
  <!-- #ifndef MP-WEIXIN -->
  <view v-show="$props.loading" class="scroll__loading">
    <u-loading mode="flower" size="48"></u-loading>
    <text class="scroll__loading--text">加载中...</text>
  </view>
  <!-- #endif -->
  <!-- #ifdef MP-WEIXIN -->
  <view v-if="$props.loading" class="scroll__loading">
    <u-loading mode="flower" size="48"></u-loading>
    <text class="scroll__loading--text">加载中...</text>
  </view>
  <!-- #endif -->
</template>

<style lang="scss" scoped>
@include b(scroll) {
  @include e(loading) {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 100dvh;
    color: #666;
    @include m(text) {
      padding-top: 10rpx;
      font-size: 28rpx;
    }
  }
}
</style>
