<script setup lang="ts">
import type { PropType } from 'vue'
import type { IntegralGoodsInfoType } from '@/apis/plugin/integral/model'
import price from '../price.vue'

const $props = defineProps({
  info: {
    type: Object as PropType<IntegralGoodsInfoType>,
    default() {
      return {}
    },
  },
})
</script>

<template>
  <view class="order">
    <view class="order__list">
      <view class="good">
        <u-image :width="128" :height="128" border-radius="12" :src="$props.info.albumPics?.[0]" />
        <view class="good__info">
          <view class="good__info--title">{{ $props.info.name }}</view>
          <view class="good__info--bottom">
            <price :integral="info.integralPrice" :sale-price="info.salePrice" />
            <view class="good__info--count">1</view>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
@import '@/assets/css/animate.scss';
@include b(order) {
  background: #fff;
  border-radius: 20rpx;
  margin-bottom: 22rpx;
  @include e(list) {
    padding: 20rpx;
  }
}
@include b(good) {
  @include flex(space-between);
  @include e(info) {
    margin-left: 16rpx;
    @include flex();
    justify-content: space-around;
    flex-direction: column;
    height: 128rpx;

    @include m(title) {
      font-size: 28rpx;
      color: #000;
      width: 526rpx;

      @include utils-ellipsis(1);
    }
    @include m(bottom) {
      width: 526rpx;

      @include flex(space-between);
    }
    @include m(price) {
      font-size: 30rpx;
      font-weight: 700;
      color: #f83f30;
    }
    @include m(count) {
      font-size: 24rpx;
      color: #121212;
      &::before {
        content: 'X';
        display: inline-block;
        font-size: 20rpx;
      }
    }
  }
}
</style>
