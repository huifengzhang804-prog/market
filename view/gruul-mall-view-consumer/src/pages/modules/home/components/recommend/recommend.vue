<!-- eslint-disable no-redeclare -->
<!-- eslint-disable no-import-assign -->
<!--
 * 推荐页
-->
<script setup lang="ts">
import type { PropType } from 'vue'
// #ifndef H5
// @ts-ignore
import DecorationList from './components/decorationList.vue'
// #endif
// #ifdef H5
const DecorationList = defineAsyncComponent(() => import('./components/decorationList.vue'))
// #endif
import { useGotop } from '@/components/q-gotop/useQGotop'

import qService from '@/components/q-service/q-service.vue'

import type { DecorateItemType, lodingType } from '../../types'
import { defineAsyncComponent } from 'vue'
import NoDecoration from '@/components/no-decoration/index.vue'

const { topItem, handleGoTop, scroll, show } = useGotop()
const $props = defineProps({
  decorationList: {
    type: Array<DecorateItemType>,
    required: true,
  },
  isShowLoading: {
    type: String as PropType<lodingType>,
    default: '加载中',
  },
  chooseIndex: {
    type: Number,
    default: 0,
  },
})
const scrolltolower = () => {
  // 发布事件
  if ($props.chooseIndex === 0) {
    uni.$emit('scrolltolower')
  }
}
</script>

<template>
  <scroll-view scroll-y style="height: 100%" scroll-with-animation :scroll-into-view="topItem" @scroll="scroll" @scrolltolower="scrolltolower">
    <view id="top"></view>
    <view v-if="isShowLoading === '加载中'" class="loading">
      <view style="margin-bottom: 20rpx"> <u-loading show size="80" mode="flower"> </u-loading></view>
      服务正在加载中...
    </view>

    <view v-else-if="isShowLoading === '未配置'">
      <NoDecoration />
    </view>
    <view v-else-if="isShowLoading === '失败'">加载失败</view>
    <DecorationList v-else :decorateList="$props.decorationList" :chooseIndex="chooseIndex" />
    <view class="utilsBox">
      <view class="gotop">
        <q-service />
      </view>
      <view v-if="show" class="gotop" style="margin-top: 20rpx" @click="handleGoTop">
        <q-icon size="60rpx" color="#999" name="icon-huidaodingbu" />
      </view>
    </view>
  </scroll-view>
</template>

<style lang="scss" scoped>
@include b(swiper) {
  margin-top: 16rpx;
}
@include b(category) {
  position: relative;
  @include e(tabs) {
    width: calc(100vw - 130rpx);
  }
  @include e(tabs) {
    flex: 1;
  }
  @include e(nav) {
    position: absolute;
    right: 0;
    top: 0;
    bottom: 0;
    width: 130rpx;
    @include flex;
    box-shadow: -4rpx 0rpx 2rpx 0rpx rgba(0, 0, 0, 0.04);
    @include m(text) {
      margin-left: 5px;
    }
  }
}
@include b(loading) {
  text-align: center;
  margin-top: 80rpx;
}
@include b(utilsBox) {
  position: fixed;
  bottom: 100rpx;
  right: 20rpx;
  z-index: 33333;
}
@include b(gotop) {
  width: 96rpx;
  height: 96rpx;
  text-align: center;
  line-height: 100rpx;
  border-radius: 50rpx;
  box-shadow:
    0px 6px 28px 4px rgba(0, 0, 0, 0.05),
    0px 16px 20px 2px rgba(0, 0, 0, 0.06),
    0px 10px 10px -6px rgba(0, 0, 0, 0.1);
  background-color: #fff;
}
</style>
