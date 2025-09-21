<script setup lang="ts">
import type { PropType } from 'vue'
import type { Address } from '@/apis/address/type'

const $props = defineProps({
  info: {
    type: Object as PropType<Address>,
    default() {
      return {}
    },
  },
})

const handleNavToAddress = () => {
  uni.navigateTo({
    url: '/basePackage/pages/addressManage/AddressManage?callKey=callConfirm',
  })
}
</script>
<template>
  <view class="content" @click="handleNavToAddress">
    <template v-if="$props.info.area.length">
      <view class="address">
        <view class="address__fir">
          <view class="address__fir--btn">收货地址</view>
          <view class="address__fir--text">{{ $props.info.area.join(' ') }}</view>
        </view>
        <view class="address__sec">
          <view class="address__sec--text">{{ $props.info.address }}</view>
          <u-icon name="arrow-right"></u-icon>
        </view>
        <view class="address__third">
          <text style="margin-right: 40rpx">{{ $props.info.name }}</text>
          <text>{{ $props.info.mobile }}</text>
        </view>
      </view>
      <view class="content__line"></view>
      <view class="content__bottom">
        <view class="content__bottom--text">配送方式</view>
        <view>同城配送</view>
      </view>
    </template>
    <view v-else style="position: relative">
      <u-empty text="请选择收货地址" mode="address" icon-size="80" font-size="24" />
      <u-icon name="arrow-right" class="address__icon" color="rgb(192, 196, 204)"></u-icon>
    </view>
  </view>
</template>

<style lang="scss" scoped>
@include b(content) {
  background: #fff;
  border-radius: 20rpx;
  display: flex;
  flex-direction: column;
  @include e(line) {
    width: 100%;
    height: 4rpx;
    background: #f2f2f2;
  }
  @include e(bottom) {
    display: flex;
    justify-content: space-between;
    height: 124rpx;
    align-items: center;
    font-size: 28rpx;
    color: #000;
    padding: 0 40rpx;
    @include m(text) {
      font-weight: 900;
    }
  }
}

@include b(address) {
  padding: 34rpx 46rpx 48rpx 36rpx;
  background: #fff;

  font-size: 28rpx;
  color: #000;

  @include e(icon) {
    position: absolute;
    right: 0;
    top: 50%;
    transform: translate(-50%, -50%);
  }
  @include e(fir) {
    @include flex(flex-start);
    @include m(btn) {
      width: 160rpx;
      height: 50rpx;
      text-align: center;
      line-height: 50rpx;
      color: #e57a77;
      border: 2rpx solid #e57a77;
      background: #faeeee;
      border-radius: 40rpx;
      margin-right: 28rpx;
    }
  }
  @include e(sec) {
    @include flex(space-between);
    margin: 24rpx 0;
    @include m(text) {
      width: 95%;
      word-break: break-all;
    }
  }
  @include e(third) {
    font-size: 26rpx;
    color: #777777;
  }
}
</style>
