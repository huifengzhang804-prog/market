<script setup lang="ts">
import { useSettingStore } from '@/store/modules/setting'
import { navDate } from './activeHeightScroll'
import { useNavBack } from '@/hooks/useNavBack'
import useGoodCollection from '@/pluginPackage/goods/commodityInfo/module-components/hooks/useGoodCollection'
import { ref } from 'vue'

defineProps({
  isShowAssess: {
    type: Boolean,
    default: true,
  },
})

const emits = defineEmits(['changeNav'])
const { isCollection, handleAssess } = useGoodCollection()
const $settingStore = useSettingStore()

const navToCarHandle = () => {
  $settingStore.NAV_TO_INDEX('购物车')
}
const handleBack = () => {
  useNavBack()
}
const handleToolnav = (index: number) => {
  navDate.trigger = index
  emits('changeNav', index)
}
const totalEvaluate = ref('0')
uni.$on('totalEvaluateChange', (val) => {
  totalEvaluate.value = val
})
</script>

<template>
  <view class="head-tool" :style="{ height: `calc(${navDate.navH}px + 100rpx)`, backgroundColor: `rgba(255, 255, 255, ${navDate.styleOpacity})` }">
    <view :style="{ height: navDate.navH + 'px' }"></view>
    <view class="head-tool__main">
      <view class="head-tool__main-back" @touchstart.prevent="handleBack"><u-icon name="arrow-left" size="24rpx" color="black" /></view>
      <view v-show="!!navDate.styleOpacity" class="head-tool__main-tool" :style="{ opacity: navDate.styleOpacity }">
        <template v-for="(item, index) in navDate.nameS" :key="item.name">
          <view
            v-if="(totalEvaluate !== '0' && item.name === '评价') || item.name !== '评价'"
            class="head-tool__main-tool-item"
            @touchstart.prevent="handleToolnav(index)"
          >
            <text class="head-tool__main-tool-item--text" :class="{ 'head-tool__active': navDate.trigger === index }">{{ item.name }}</text>
            <text :class="{ 'head-tool__main-tool-item--index': navDate.trigger === index }"></text>
          </view>
        </template>
      </view>
      <!-- #ifndef MP-WEIXIN -->
      <view v-if="isShowAssess" class="head-tool__right">
        <q-icon name="icon-gouwucheshangcheng-xianxing" size="40rpx" color="#101010" @touchstart.prevent="navToCarHandle" />
        <view v-if="isShowAssess" class="head-tool__right--shoucang" @touchstart.prevent="handleAssess">
          <q-icon v-if="isCollection" name="icon-pingfen" size="40rpx" color="red" />
          <q-icon v-else name="icon-shoucang2" size="40rpx" />
        </view>
        <view v-else class="head-tool__right--isShowAssess"></view>
      </view>
      <!-- #endif -->
    </view>
  </view>
</template>

<style scoped lang="scss">
@include b(head-tool) {
  position: fixed;
  top: 0rpx;
  right: 0;
  left: 0;
  z-index: 999;
  @include e(main) {
    display: flex;
    justify-content: space-between;
    align-items: center;
    height: 100rpx;
  }
  @include e(main-back) {
    margin: 0 20rpx;
    padding: 10rpx;
    border-radius: 50%;
    @include flex;
    background-color: rgba(255, 255, 255, 1);
    border: 1px solid rgba(255, 255, 255, 1);
  }
  @include e(main-tool) {
    display: flex;
    justify-content: space-around;
    align-items: center;
    flex: 1;
    height: 100rpx;
  }
  @include e(main-tool-item) {
    position: relative;
    display: flex;
    align-items: center;
    height: 100%;
    @include m(text) {
    }
    @include m(index) {
      position: absolute;
      bottom: 10rpx;
      left: 0;
      right: 0;
      height: 10rpx;
      margin: 0 5rpx;
      border-radius: 10rpx;
      background: red;
    }
  }
  @include e(right) {
    position: relative;
    padding: 10rpx 20rpx;
    border-radius: 60rpx;
    background-color: rgba(255, 255, 255, 0.76);
    margin-right: 20rpx;
    @include m(shoucang) {
      display: inline-block;
      margin: 0 30rpx;
    }
    @include m(isShowAssess) {
      display: inline-block;
      // margin: 0 15rpx;
    }
  }
  @include e(active) {
    color: #e31436;
  }
}
</style>
