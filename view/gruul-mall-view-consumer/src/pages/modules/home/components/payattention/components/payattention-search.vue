<script setup lang="ts">
const handleSearch = () => {
  uni.navigateTo({ url: '/basePackage/pages/searchPage/SearchPage' })
}
</script>

<template>
  <view class="search" @click="handleSearch">
    <u-icon name="search" class="search__icon"></u-icon>
    <text class="search__text">请输入您想要的物品</text>
    <view class="search__btn">搜索</view>
  </view>
</template>

<style lang="scss" scoped>
@include b(search) {
  margin: 0 16rpx;
  box-sizing: border-box;
  position: relative;
  border: 1px solid #f54319;
  border-radius: 40rpx;
  height: 72rpx;
  z-index: 10;
  background-color: #fff;
  @include flex(space-between);
  @include e(icon) {
    flex-shrink: 0;
    margin-left: 12rpx;
    font-size: 36rpx;
    color: #666;
  }
  @include e(text) {
    color: #999;
    font-size: 24rpx;
    margin-left: 16rpx;
    flex: 1;
  }
  @include e(btn) {
    width: 128rpx;
    height: 56rpx;
    border-radius: 40rpx;
    line-height: 56rpx;
    text-align: center;
    color: #fff;
    background-color: #f54319;
    font-size: 24rpx;
    margin-right: 8rpx;
  }
}
</style>
