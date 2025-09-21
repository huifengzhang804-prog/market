<script setup lang="ts">
import { navDate } from '@/pluginPackage/goods/commodityInfo/components/head-nav/activeHeightScroll'
import { useNavBack } from '@/hooks/useNavBack'

const { statusBarHeight } = uni.getSystemInfoSync()
const handleBack = () => {
  useNavBack()
}
const getTitleHeight = () => {
  return uni.getMenuButtonBoundingClientRect().height
}
</script>

<template>
  <view class="head-tool">
    <view :style="{ height: navDate.navH + (statusBarHeight || 0) + 'px' }"></view>
    <view class="head-tool__main">
      <view class="head-tool__main-back"><u-icon name="arrow-left" size="24rpx" color="black" @click="handleBack" /></view>
      <!-- #ifndef MP-WEIXIN -->
      <!-- #endif -->
    </view>
  </view>
</template>

<style scoped lang="scss">
@include b(head-tool) {
  position: fixed;
  top: 0rpx;
  right: 0;
  left: 0;
  z-index: 999;
  @include e(main) {
    display: flex;
    justify-content: space-between;
    align-items: center;
    height: 80rpx;
  }
  @include e(main-back) {
    margin: 0 20rpx;
    padding: 10rpx;
    border-radius: 50%;
    @include flex;
    background-color: rgba(255, 255, 255, 1);
    border: 1px solid rgba(255, 255, 255, 1);
  }
  @include e(main-tool) {
    display: flex;
    justify-content: space-around;
    align-items: center;
    flex: 1;
    height: 80rpx;
  }
  @include e(main-tool-item) {
    position: relative;
    display: flex;
    align-items: center;
    height: 100%;
    @include m(text) {
    }
    @include m(index) {
      position: absolute;
      bottom: 10rpx;
      left: 0;
      right: 0;
      height: 10rpx;
      margin: 0 5rpx;
      border-radius: 10rpx;
      background: red;
    }
  }
  @include e(right) {
    position: relative;
    padding: 10rpx 20rpx;
    border-radius: 60rpx;
    background-color: rgba(255, 255, 255, 0.76);
    margin-right: 20rpx;
    @include m(shoucang) {
      display: inline-block;
      margin: 0 30rpx;
    }
  }
  @include e(active) {
    color: #e31436;
  }
}
</style>
