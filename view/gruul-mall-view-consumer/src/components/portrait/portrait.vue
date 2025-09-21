<script setup lang="ts">
import { ref } from 'vue'

const $props = defineProps({
  src: { type: String, default: '' },
  title: { type: String, default: '卖家' },
  time: { type: String, default: '2018-06-24' },
  detail: { type: String, default: '明细' },
  sku: { type: String, default: '' },
  rate: { type: Number, default: null },
})
</script>

<template>
  <view class="seller">
    <view class="portrait">
      <view class="portrait__left">
        <image class="portrait__left--image" :src="$props.src" />
      </view>
      <view class="portrait__right">
        <view class="portrait__right--identity">
          <text>{{ $props.title }}</text>
          <u-rate v-if="$props.rate !== null" :count="5" :model-value="$props.rate" disabled />
        </view>
        <text class="portrait__right--time">
          {{ $props.time }}<text v-if="sku" style="margin-left: 26rpx">{{ $props.sku }}</text>
        </text>
      </view>
    </view>
    <slot></slot>
  </view>
</template>

<style scoped lang="scss">
@include b(seller) {
  padding: 30rpx;
  background: #fff;
  font-size: 30rpx;
  color: #000;
  margin-bottom: 10rpx;
  @include e(reply) {
    margin-top: 25rpx;
  }
}
@include b(portrait) {
  @include flex(flex-start);
  @include e(left) {
    height: 100rpx;
    width: 100rpx;
    min-width: 100rpx;
    @include m(image) {
      width: 100%;
      height: 100%;
      border-radius: 55rpx;
    }
  }
  @include e(right) {
    margin-left: 20rpx;
    @include flex(space-around, flex-start);
    flex-direction: column;
    height: 100rpx;
    @include m(identity) {
      display: flex;
      justify-content: space-between;
      width: 100%;
    }
    @include m(time) {
      color: #9b9b9b;
      font-size: 20rpx;
      margin-right: 20rpx;
    }
  }
}
</style>
