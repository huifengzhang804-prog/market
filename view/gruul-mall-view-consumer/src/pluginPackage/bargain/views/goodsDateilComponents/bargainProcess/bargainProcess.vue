<script setup lang="ts">
import QIcon from '@/components/q-icon/q-icon.vue'

const iconConfig = [
  { icon: 'icon-dianjitianjia', color: '#ccc', text: '点击砍价' },
  { icon: 'icon-chengyuan', color: '#ccc', text: '找人砍价' },
  { icon: 'icon-tubiaoxiajiangqushi', color: '#ccc', text: '价格合适' },
  { icon: 'icon-gouwuche4', color: '#ccc', text: '下单付款' },
]
</script>

<template>
  <view class="fontBold f14 process_title">砍价流程</view>
  <view class="bargain_process">
    <view v-for="(item, index) in iconConfig" :key="index" class="bargain_process__item">
      <view class="bargain_process__item_left">
        <view class="bargain_process__item_left--icon">
          <q-icon :name="item.icon" size="28px" :color="item.color"></q-icon>
        </view>
        <text class="bargain_process__item_left--text">{{ item.text }}</text>
      </view>
      <view v-if="index !== iconConfig.length - 1" class="bargain_process__item_right f16">
        <text>></text>
        <text>></text>
        <text>></text>
      </view>
    </view>
  </view>
</template>

<style scoped lang="scss">
@include b(bargain_process) {
  padding: 20rpx 30rpx 60rpx 30rpx;
  @include flex;
  justify-content: space-around;
  .f16 {
    font-size: 32rpx;
  }
  @include e(item) {
    @include flex;
    margin: 20rpx 0;
  }
  @include e(item_left) {
    @include flex;
    flex-direction: column;
    margin-right: 20rpx;
    position: relative;
    @include m(icon) {
      @include flex;
      width: 80rpx;
      height: 80rpx;
      border-radius: 50%;
      border: 1px solid #ccc;
    }
    @include m(text) {
      position: absolute;
      left: 50%;
      transform: translateX(-50%);
      color: #ccc;
      bottom: -50rpx;
      width: 120rpx;
    }
  }
  @include e(item_right) {
    & > text:nth-child(1) {
      color: #bbbbbb;
    }
    & > text:nth-child(2) {
      color: #d7d7d7;
    }
    & > text:nth-child(3) {
      color: #ececec;
    }
  }
}
@include b(process_title) {
  margin: 30rpx 0 0 30rpx;
}
</style>
