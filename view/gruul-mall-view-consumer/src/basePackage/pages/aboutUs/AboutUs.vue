<script setup lang="ts">
const logo = import.meta.env.VITE_APP_LOGO
const version = import.meta.env.VITE_APP_VERSION
const name = import.meta.env.VITE_APP_NAME

const handleNavToPrivateAgreement = () => {
  uni.navigateTo({
    url: '/basePackage/pages/privateAgreement/PrivateAgreement',
  })
}
</script>

<template>
  <view class="aboutUs">
    <image :src="logo" class="aboutUs__logo"></image>
    <view class="aboutUs__name">{{ name }}</view>
    <!-- #ifdef  APP-PLUS  -->
    <view class="aboutUs__version">Version {{ version }}</view>
    <!-- #endif -->
  </view>
  <view style="margin: 0 0 0 10px">
    <u-cell-group>
      <u-cell-item title="隐私协议" bg-color="rgb(251 251 251)" @click="handleNavToPrivateAgreement"></u-cell-item>
    </u-cell-group>
  </view>
  <view class="copyRight">
    <view>CopyRight©2023</view>
    <view>启山科技版权所有</view>
  </view>
</template>

<style lang="scss" scoped>
@include b(aboutUs) {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 100rpx 0 50rpx 0;
  @include e(logo) {
    width: 120rpx;
    height: 120rpx;
    border-radius: 20rpx;
    background: rgba(#ccc, 0.3);
  }
  @include e(name) {
    margin: 30rpx 0;
    font-size: 40rpx;
    font-weight: bold;
  }
  @include e(version) {
    font-size: 30rpx;
  }
}
@include b(copyRight) {
  width: 100%;
  color: #ccc;
  font-size: 22rpx;
  text-align: center;
  position: fixed;
  bottom: 50px;
}
</style>
