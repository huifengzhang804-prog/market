<script setup lang="ts">
import { ref, type PropType } from 'vue'
import RoomStatus from '@plugin/live/components/decorationComponents/studio-status.vue'
import type { ApiLiveItem } from '@pluginPackage/live/components/types'

const $props = defineProps({
  liveItem: {
    type: Object as PropType<ApiLiveItem>,
    required: true,
  },
})
const emit = defineEmits(['click'])
</script>

<template>
  <view class="main-figure" @click="emit('click')">
    <view class="main-figure__image">
      <u-image width="100%" :height="340" :src="$props.liveItem.coverImg" />
      <u-image width="60rpx" :height="60" :src="$props.liveItem.shopLogo" shape="circle" class="main-figure__image--shop-logo" />
      <room-status :active="$props.liveItem.status" />
    </view>
    <view class="main-figure__info">
      <view class="main-figure__info--shop-name">{{ $props.liveItem.shopName }}</view>
      <view class="main-figure__info--name">{{ $props.liveItem.roomName }}</view>
    </view>
  </view>
</template>

<style scoped lang="scss">
@include b(main-figure) {
  /* width: calc(50% - 26rpx * 2); */
  border-radius: 12rpx;
  /* margin: 26rpx; */
  overflow: hidden;
  background: #fff;
  @include e(image) {
    position: relative;
    @include m(shop-logo) {
      position: absolute;
      bottom: -30rpx;
      left: 15rpx;
    }
  }
  @include e(info) {
    padding: 0 10rpx;
    @include m(shop-name) {
      color: #101010;
      font-size: 28rpx;
      font-weight: 700;
      margin-top: 40rpx;
      @include utils-ellipsis(1);
    }
    @include m(name) {
      color: #101010;
      margin: 20rpx 0;
      font-size: 28rpx;
      @include utils-ellipsis(1);
    }
  }
}
@include b(goods-info) {
  display: flex;
  padding: 20rpx 10rpx;
  @include e(msg) {
    flex: 1;
    padding-left: 10rpx;
    height: 80rpx;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    @include m(name) {
      width: 200rpx;
      color: #6666;
      font-size: 24rpx;
      @include utils-ellipsis(1);
    }
    @include m(price) {
      color: #333;
      font-size: 24rpx;
      font-weight: 700;
      &::before {
        content: '￥';
      }
    }
  }
}
/* @include b(border) {
    border-bottom: 1px dashed #ccc;
} */
</style>
