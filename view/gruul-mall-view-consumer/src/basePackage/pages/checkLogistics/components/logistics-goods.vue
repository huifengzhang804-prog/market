<script setup lang="ts">
const $props = defineProps({
  num: {
    type: [String, Number],
    default: 0,
  },
  src: { type: String, default: '' },
  expressCompanyName: { type: String, default: '' },
  expressNo: { type: String, default: '' },
  phone: { type: String, default: '暂无' },
  mask: { type: Boolean, default: true },
})
</script>

<template>
  <view class="company">
    <view class="company__left">
      <image style="width: 100%; height: 100%" :src="$props.src" />
      <view v-if="$props.mask" class="company__left--mask">{{ $props.num }}件商品</view>
    </view>
    <view class="company__right">
      <slot>
        <view>承运公司：{{ $props.expressCompanyName }}</view>
        <view>运单编号：{{ $props.expressNo }}</view>
        <view>官方电话：{{ $props.phone }}</view>
      </slot>
    </view>
  </view>
  <view class="company__placeholder"></view>
</template>

<style scoped lang="scss">
@include b(company) {
  background: #fff;
  @include flex(flex-start);
  padding: 26rpx;
  @include e(placeholder) {
    height: 10px;
    background: #f4f4f5;
  }
  @include e(left) {
    height: 146rpx;
    width: 146rpx;
    margin-right: 18rpx;
    position: relative;
    @include m(mask) {
      position: absolute;
      height: 48rpx;
      line-height: 48rpx;
      width: 146rpx;
      color: #fff;
      text-align: center;
      left: 0;
      bottom: 0;
      opacity: 0.7;
      background: #000;
    }
  }
  @include e(right) {
    flex: 1;
    line-height: 40rpx;
    font-size: 24rpx;
    color: #333333;
  }
}
</style>
