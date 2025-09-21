<script setup lang="ts">
import type { PropType } from 'vue'
import type { GetUserMessage } from '@/pluginPackage/liveModule/apis/CreateLive/model'

defineProps({
  anchorIcon: {
    type: String,
    default: 'https://up.enterdesk.com/edpic_source/a6/d9/12/a6d91218fcb87dca6cb09d812e9bc0c2.jpg',
  },
  followCount: {
    type: [Number, String],
    default: 111,
  },
  viewership: {
    type: [Number, String],
    default: 111,
  },
  anchorNickname: { type: String, default: '酸豆角的小毛牛' },
  height: { type: [Number, String], default: 330 },
  status: { type: String as PropType<GetUserMessage['status']>, default: 'NORMAL' },
})

const handleStartLive = (status: GetUserMessage['status']) => {
  if (status === 'NORMAL') return uni.navigateTo({ url: '/pluginPackage/liveModule/views/ImmediatelyStarted/Index' })
  uni.showToast({ title: '主播已被禁播', icon: 'none' })
}
const handleRelease = (status: GetUserMessage['status']) => {
  if (status === 'NORMAL') return uni.navigateTo({ url: '/pluginPackage/liveModule/views/ReleaseNotice/Index' })
  uni.showToast({ title: '主播已被禁播', icon: 'none' })
}
</script>

<template>
  <view class="release" :style="{ height: height + 'rpx' }">
    <view class="release__top">
      <view class="release__top_image">
        <u-image shape="circle" width="100%" height="100%" :src="anchorIcon"></u-image>
      </view>
      <view class="release__top_content">
        <text class="release__top_content--title">{{ anchorNickname }}</text>
        <view style="color: #666666">
          <text>粉丝</text>
          <text class="release__top_content--num">{{ followCount }}</text>
          <text>观看</text>
          <text class="release__top_content--num">{{ viewership }}</text>
        </view>
      </view>
    </view>
    <view class="release__bottom">
      <view class="release__bottom_start release__bottom_flex">
        <view>今天一起开播哦</view>
        <view class="btn" @click="handleStartLive(status)">立即开播</view>
      </view>
      <view class="release__bottom-center-line"></view>
      <view class="release__bottom_release release__bottom_flex">
        <view>发布预告吸引更多粉丝</view>
        <view class="btn" style="background: #ff794d" @click="handleRelease(status)">发布预告</view>
      </view>
    </view>
  </view>
</template>

<style scoped lang="scss">
@include b(release) {
  width: 750rpx;
  background: #ffffff;

  @include e(top) {
    height: 50%;
    @include flex;
    justify-content: flex-start;
  }

  @include e(top_image) {
    width: 120rpx;
    height: 120rpx;
    margin: 0 30rpx;
  }

  @include e(top_content) {
    @include flex;
    flex-direction: column;
    justify-content: space-between;
    padding: 10rpx 0;
    height: 120rpx;
    align-items: flex-start;

    @include m(title) {
      color: #333333;
      font-size: 34rpx;
    }

    @include m(num) {
      margin: 0 30rpx 0 10rpx;
      color: #333333;
      font-weight: 700;
    }
  }

  @include e(bottom) {
    height: 50%;
    margin: 0 50rpx;
    border-top: 1px solid #f2f2f2;
    @include flex;
    justify-content: space-around;
  }

  @include e(bottom_start) {
    height: 100%;
  }

  @include e(bottom-center-line) {
    width: 1rpx;
    height: 50rpx;
    background: #f2f2f2;
  }

  @include e(bottom_release) {
    height: 100%;
  }

  @include e(bottom_flex) {
    width: 50%;
    @include flex;
    flex-direction: column;
    justify-content: space-evenly;
    align-items: center;
    color: #666666;
  }
}

@include b(btn) {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 9rpx 23rpx;
  width: 182rpx;
  height: 66rpx;
  background: #fa3534;
  /* background: #ff794d; */
  color: #fff;
  border-radius: 4rpx;

  &:active {
    opacity: 0.5;
  }
}
</style>
