<script setup lang="ts">
import type { PropType } from 'vue'
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
  <view class="circular" @click="emit('click')">
    <view class="circular__view">
      <u-image :width="104" mode="scaleToFill" :height="104" :src="$props.liveItem.coverImg" shape="circle" class="circular__img" />
      <text class="circular__tag">热门主播</text>
    </view>
    <view class="circular--name">{{ $props.liveItem.shopName }}</view>
  </view>
</template>

<style scoped lang="scss">
@include b(circular) {
  margin: 0 10rpx;
  width: 132rpx;
  font-size: 24rpx;
  @include m(name) {
    @include utils-ellipsis(1);
    width: 132rpx;
    margin: 14rpx 0;
    color: #333333;
    text-align: center;
  }
  @include e(view) {
    position: relative;
    width: 132rpx;
    height: 132rpx;
    background-color: #ffffff;
    text-align: center;
    border: 1px solid #f54319;
    border-radius: 50%;
  }
  @include e(img) {
    position: absolute;
    left: 50%;
    top: 50%;
    transform: translate(-50%, -50%);
  }
  @include e(tag) {
    position: absolute;
    color: #101010;
    left: 50%;
    transform: translate(-50%, 0);
    width: 120rpx;
    bottom: 10rpx;
    font-weight: 700;
    background: #fff;
  }
}
</style>
