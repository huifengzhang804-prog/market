<script setup lang="ts">
const $props = defineProps({
  price: {
    type: String,
    default: '0',
  },
  text: {
    type: String,
    default: '预估到手',
  },
})
const { divTenThousand } = useConvert()
</script>

<template>
  <view style="display: inline-block">
    <view class="forecast-tag">
      <text style="font-weight: bold" class="forecast-tag--price">
        {{ $props.price && divTenThousand($props.price).toFixed(2) }}
      </text>
      <text>{{ $props.text }}</text>
    </view>
  </view>
</template>

<style scoped lang="scss">
$price-color: #e31436; // 价格颜色
@include b(forecast-tag) {
  display: flex;
  justify-content: space-between;
  flex-direction: column;
  padding: 10rpx 30rpx;
  border-radius: 10rpx;
  background-color: rgba(255, 243, 244, 1);
  color: $price-color;
  font-size: 24rpx;
  text-align: center;
  @include m(price) {
    margin-bottom: 5rpx;
    &::before {
      content: '￥';
      color: $price-color;
    }
  }
}
</style>
