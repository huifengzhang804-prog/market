<script setup lang="ts">
import type { PropType } from 'vue'
import PreviewImage from '@/components/preview-image/preview-image.vue'

const $props = defineProps({
  time: { type: String, required: true },
  rate: { type: [String, Number], required: true },
  comment: { type: String, required: true },
  shopReply: { type: String, default: '' },
  images: { type: Array as PropType<string[]>, default: () => [] },
  name: { type: String, required: true },
  sku: { type: Array as PropType<string[]>, default: () => [] },
  image: { type: String, required: true },
})
</script>

<template>
  <view class="container">
    <view class="container__title">
      <text class="container__title--time">{{ $props.time }}</text>
      <view>
        商品质量 <text class="container__title--quality">{{ $props.rate }}.0</text>
      </view>
    </view>
    <view class="container__content">
      {{ $props.comment }}
    </view>
    <preview-image v-if="$props.images?.length" :images="$props.images" />
    <view v-if="$props.shopReply" class="container__shop-content">
      {{ $props.shopReply }}
    </view>
    <view class="container__order">
      <u-image style="flex-shrink: 0" :width="88" :height="88" border-radius="10" :src="$props.image" />
      <view class="container__order-right">
        <view class="container__order-right--name">{{ $props.name }}</view>
        <text v-if="$props.sku.length" class="container__order-right--sku">{{ `[ ${$props.sku.join(' ')} ]` }}</text>
      </view>
    </view>
  </view>
</template>

<style scoped lang="scss">
@include b(container) {
  background: #ffffff;
  border-radius: 12rpx;
  padding: 14rpx 12rpx 34rpx 12rpx;
  @include e(images) {
    @include flex();
    justify-content: space-between;
    flex-wrap: wrap;
  }
  @include e(title) {
    @include flex();
    justify-content: space-between;
    color: #959595;
    font-size: 22rpx;
    @include m(time) {
    }
    @include m(quality) {
      color: #ff2e27;
      font-weight: bold;
    }
  }
  @include e(content) {
    margin-top: 18rpx;
    font-size: 26rpx;
    color: #000;
  }
  @include e(shop-content) {
    margin-top: 30rpx;
    padding: 20rpx;
    border-radius: 12rpx;
    font-size: 26rpx;
    color: #838383;
    background: #f5f5f5;
  }
  @include e(order) {
    margin-top: 30rpx;
    border-radius: 12rpx;
    font-size: 26rpx;
    color: #838383;
    background: #f5f5f5;
    @include flex;
    justify-content: space-between;
  }
  @include e(order-right) {
    flex: 1;
    width: 0;
    margin-left: 10rpx;
    padding: 12rpx 0;
    @include m(name) {
      @include utils-ellipsis();
      color: #333333;
    }
    @include m(sku) {
    }
  }
}
</style>
