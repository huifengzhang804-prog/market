<script setup lang="ts">
import type { PropType } from 'vue'
import DefaultEmpty from '@/components/qszr-core/packages/components/q-empty/default-empty.vue'
import { EMPTY_GB } from '@/constant'
import type { ConcernItem } from '@/basePackage/pages/concern/types'

defineProps({
  list: {
    type: Array as PropType<ConcernItem[]>,
    default: () => [],
  },
  reachBottomStatus: { type: String, default: '' },
  scrollTop: { type: Number, default: 0 },
  scrollHeight: { type: Number, default: 0 },
})
const emit = defineEmits(['on-scrolltolower'])

const handleNavToShop = (shopId: Long) => {
  uni.navigateTo({
    url: `/basePackage/pages/merchant/Index?shopId=${shopId}`,
  })
}
</script>

<template>
  <scroll-view scroll-y :style="{ height: `${scrollHeight}px` }" :scroll-top="scrollTop" @scrolltolower="emit('on-scrolltolower')">
    <view v-for="(item, index) in list" :key="index" class="concern">
      <view class="concern__left" @click="handleNavToShop(item.shopId)">
        <image class="concern__left-image" :src="item.logo"> </image>
        <view class="concern__left-text">
          <text class="concern__left-text--name">{{ item.shopName }}</text>
          <text class="concern__left-text--num">{{ item.numberFollowers }}人关注</text>
        </view>
      </view>
      <view v-if="item.newProducts" class="concern__right">
        <view class="concern__right--round"></view>
        <view clss="concern__right--text">有上新</view>
      </view>
    </view>
    <DefaultEmpty v-if="!list.length" :background="EMPTY_GB.CART_EMPTY" height="700rpx" />
  </scroll-view>
</template>

<style scoped lang="scss">
@include b(concern) {
  height: 94rpx;
  margin: 54rpx 0;
  @include flex(space-between);
  @include e(left) {
    width: 480rpx;
    @include flex(space-between);
  }
  @include e(left-image) {
    width: 94rpx;
    height: 94rpx;
    border-radius: 52rpx;
    margin: 0 22rpx 0 28rpx;
  }
  @include e(left-text) {
    height: 94rpx;
    @include flex(space-between, flex-start);
    flex-direction: column;
    @include m(name) {
      width: 336rpx;
      font-size: 28rpx;
      text-align: LEFT;
      color: #2f2f2f;
      overflow: hidden;
      white-space: nowrap; //内容超出不换行
      text-overflow: ellipsis;
    }
    @include m(mun) {
      font-size: 22rpx;
      color: #9c9c9c;
    }
  }
  @include e(right) {
    margin-right: 34rpx;
    @include flex;
    @include m(round) {
      width: 28rpx;
      height: 28rpx;
      background: #ffffff;
      border: 2rpx solid #11a741;
      border-radius: 50%;
      margin-right: 10rpx;
      position: relative;
      &::before {
        content: ' ';
        display: block;
        width: 20rpx;
        height: 20rpx;
        background: #11a741;
        border-radius: 50%;
        position: absolute;
        left: 50%;
        top: 50%;
        transform: translate(-50%, -50%);
      }
    }
  }
}
@include b(reach-bottom-status) {
  height: 160rpx;
  text-align: center;
  line-height: 160rpx;
}
</style>
