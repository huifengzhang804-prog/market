<script setup lang="ts">
import ForecastItem from '@/pluginPackage/goods/commodityInfo/goodsPreferentialPopup/forecast-item.vue'
import type { comDispatcherType } from '@/pluginPackage/goods/commodityInfo/types'
import { computed, inject } from 'vue'

export type Forecast = { price: Long; text: string }
const $comProvide = inject('comProvide') as comDispatcherType
const discountList = computed(() => {
  const list = $comProvide.goodInfo.value.price.items
  return list
})
</script>

<template>
  <view class="arrow">
    <u-icon name="arrow-up" margin="0" class="arrow--icon" color="#F12F22" />
  </view>
  <view class="forecast-info">
    <view class="forecast-info__title">现在购买，享受以下优惠</view>
    <!-- 68rpx 父组件 20rpx*2 padding +  14rpx*2 padding -->
    <scroll-view
      scroll-x
      enhanced
      :show-scrollbar="false"
      style="white-space: nowrap; width: calc(100vw - 68rpx); height: 120rpx; padding-top: 20rpx"
    >
      <forecast-item v-for="(i, idx) in discountList" :key="i.desc" :idx="idx" :length="discountList.length - 1" :info="i" />
    </scroll-view>
  </view>
</template>

<style scoped lang="scss">
@include b(arrow) {
  position: relative;
  height: 40rpx;
  @include m(icon) {
    position: absolute;
    left: 50%;
    transform: translateX(-50%);
    bottom: -9rpx;
    z-index: 9;
  }
}
@include b(forecast-info) {
  position: relative;
  padding: 40rpx 14rpx;
  border-radius: 10rpx;
  background-color: rgba(255, 255, 255, 1);
  text-align: center;
  border: 1px solid #f54319;
  &::before {
    content: '';
    position: absolute;
    left: 50%;
    transform: translateX(-50%);
    display: inline-block;
    width: 20rpx;
    top: -2rpx;
    height: 3px;
    background: #fff;
    z-index: 8;
  }
  @include e(title) {
    color: rgba(16, 16, 16, 1);
    font-size: 24rpx;
    font-family: PingFangSC-regular;
  }
  @include e(main) {
    display: flex;
    margin-top: 25rpx;
    align-items: center;
    justify-content: center;
  }
}
</style>
