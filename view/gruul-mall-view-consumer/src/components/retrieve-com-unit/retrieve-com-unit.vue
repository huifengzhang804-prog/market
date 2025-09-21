<script setup lang="ts">
import MemberPrice from '@plugin/member/components/decorationComponents/memberPrice.vue'
import type { PropType } from 'vue'

const $props = defineProps({
  goodName: {
    type: String,
    default: '',
  },
  price: {
    type: Array as PropType<Long[]>,
    default: () => ['0'],
  },
  saleVolume: {
    type: [String, Number],
    default: 0,
  },
})
const { salesVolumeToStr } = useConvert()
const { min } = usePriceRange()
</script>

<template>
  <view class="good good-right">
    <view class="good__content">
      <view class="good__content--title">{{ $props.goodName }}</view>
      <view class="good__content-price">
        <view v-if="$props.price" class="good__content-price--number">{{ Number(min($props.price)).toFixed(2) }}</view>
        <view v-if="+salesVolumeToStr($props.saleVolume) > 0" class="good__content-price--count">已售 {{ salesVolumeToStr($props.saleVolume) }}</view>
      </view>
      <member-price :price="Array.isArray($props.price) ? $props.price[0] : $props.price" />
    </view>
  </view>
</template>

<style lang="scss" scoped>
@include b(good) {
  box-sizing: border-box;
  border-radius: 0 0 16rpx 16rpx;
  margin-bottom: 10rpx;
  background-color: #ffffff;
  padding: 20rpx;
  position: relative;
  @include e(content) {
    @include m(title) {
      margin-top: 5px;
      font-size: 24rpx;
      color: #000000;
    }
  }
  @include e(content-price) {
    margin: 10rpx 0 4rpx 0;
    @include flex(space-between);
    @include m(number) {
      font-size: 34rpx;
      color: #f83f30;
      white-space: nowrap;
      &::before {
        content: '￥';
        font-size: 24rpx;
        display: inline-block;
        margin-right: 2rpx;
      }
    }
    @include m(count) {
      font-size: 22rpx;
      color: #9a9a9a;
    }
  }
}
@include b(good-left) {
  margin-left: 0;
}
@include b(good-right) {
  margin-right: 0;
}
</style>
