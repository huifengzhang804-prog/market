<script setup lang="ts">
const $props = defineProps({
  goodName: {
    type: String,
    default: '',
  },
  price: {
    type: [String, Number],
    default: '',
  },
  integralPrice: {
    type: String,
    default: '0',
  },
  salePrice: {
    type: String,
    default: '0',
  },
})
const { divTenThousand } = useConvert()
</script>

<template>
  <view class="good good-right">
    <view class="good__content">
      <view class="good__content--title">{{ $props.goodName }}</view>

      <view class="good__content--originalPrice">
        <text style="text-decoration: line-through">{{ price }}</text>
      </view>
      <view class="good__content-price">
        <view class="good__content-price--number">
          {{ $props.integralPrice }}积分<text v-if="salePrice !== '0'">+￥{{ divTenThousand(salePrice) }}</text>
        </view>
      </view>
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
    font-size: 24rpx;
    @include m(title) {
      margin-top: 5px;
      width: 300rpx;
      @include utils-ellipsis(1);
      color: #333333;
      font-weight: 700;
    }
    @include m(originalPrice) {
      margin: 8rpx 0 3rpx;
      color: #999999;
      &::before {
        content: '￥';
      }
    }

    @include e(content-price) {
      font-size: 24rpx;
      margin: 10rpx 0 4rpx 0;
      @include flex(space-between);
      @include m(number) {
        color: #fa3534;
      }
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
