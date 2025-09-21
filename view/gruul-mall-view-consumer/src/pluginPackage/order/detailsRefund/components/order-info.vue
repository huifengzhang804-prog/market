<script setup lang="ts">
const $props = defineProps({
  src: {
    type: String,
    default: '',
  },
  name: {
    type: String,
    default: '',
  },
  sku: {
    type: String,
    default: '',
  },
  price: { type: [String, Number], default: '' },
  num: { type: [String, Number], default: '' },
  bgColor: { type: String, default: '#fafafa' },
})
</script>

<template>
  <view class="info" :style="{ background: $props.bgColor }">
    <view class="info__left">
      <u-image :src="$props.src" :width="200" height="100%" border-radius="30" />
    </view>
    <view class="info__right">
      <view class="info__right--name">{{ $props.name }}</view>
      <view class="info__right--sku">{{ $props.sku }}</view>
      <view class="info__right-footer">
        <text class="info__right-footer--price">{{ $props.price }}</text>
        <text class="info__right-footer--num">数量*{{ $props.num }}</text>
      </view>
    </view>
  </view>
</template>

<style scoped lang="scss">
@include b(info) {
  @include flex(flex-start);
  padding: 10rpx 20rpx 20rpx 20rpx;
  margin-bottom: 20rpx;
  @include e(left) {
    height: 200rpx;
    width: 200rpx;
    @include e(right) {
      height: 200rpx;
      margin-left: 25rpx;
      @include flex(space-between, flex-start);
      flex-direction: column;
      @include m(name) {
        color: #000;
        font-size: 30rpx;
        white-space: normal;
        word-break: break-all;
        word-wrap: break-word;
      }
      @include m(sku) {
        color: #8a8a8a;
        font-size: 24rpx;
      }
    }
    @include e(right-footer) {
      @include flex(space-between);
      width: 450rpx;
      @include m(price) {
        color: #fc5060;
        font-size: 30rpx;
        &::before {
          content: '￥';
          font-size: 20rpx;
          color: #fc5060;
        }
      }
      @include m(num) {
        color: #000;
        font-size: 25rpx;
      }
    }
  }
}
</style>
