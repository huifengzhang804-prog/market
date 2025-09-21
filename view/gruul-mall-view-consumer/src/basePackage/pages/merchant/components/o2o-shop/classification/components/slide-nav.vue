<script setup lang="ts">
import type { PropType } from 'vue'
import type { DeCategoryType } from '@/pages/platform/types'

const $props = defineProps({
  height: {
    type: Number,
    default: 1000,
  },
  config: {
    type: Object as PropType<DeCategoryType>,
    default() {
      return {}
    },
  },
  currentChooseIndex: {
    type: Number,
    default: 0,
  },
})
const $emit = defineEmits(['changeTab'])

const handleChangeTab = (index: number) => {
  $emit('changeTab', index)
}
</script>

<template>
  <scroll-view scroll-y class="slide" :style="{ height: $props.height + 'px' }">
    <view
      v-for="(item, index) in $props.config.categoryList"
      :key="item.id"
      class="slide__unit"
      :style="{
        color: index === $props.currentChooseIndex ? '#FA3534' : '#000000',
        background: index === $props.currentChooseIndex ? '#FFFFFF' : '#F2F2F2',
        fontWeight: index === $props.currentChooseIndex ? 'bold' : 'normal',
      }"
      :class="index === $props.currentChooseIndex ? 'br' : ''"
      @click="handleChangeTab(index)"
    >
      <div
        v-if="index === $props.currentChooseIndex"
        :style="{ background: index === $props.currentChooseIndex ? '#FA3534' : '#000000' }"
        class="slide__col"
      ></div>
      <div class="word">
        {{ item.name }}
      </div>
    </view>
  </scroll-view>
</template>

<style lang="scss" scoped>
@include b(slide) {
  width: 212rpx;
  overflow-y: auto;
  background: #f3f3f3;
  @include e(unit) {
    width: 212rpx;
    height: 80rpx;
    text-align: center;
    line-height: 80rpx;
    font-size: 14px;
    color: #333;
    position: relative;
    text-indent: 0.7em;
    // border-radius: 0 18rpx 18rpx 0;
  }
  @include e(col) {
    width: 15rpx;
    height: 44rpx;
    position: absolute;
    left: 2px;
    top: 50%;
    margin-top: -24rpx;
    border-radius: 0px 60rpx 60rpx 0px;
  }
  .word {
    width: 200rpx;
    @include utils-ellipsis;
  }
  .br {
    background: #fff;
    &::before {
      content: '';
      display: block;
      height: 18rpx;
      width: 18rpx;
      position: absolute;
      z-index: 98;
      background: radial-gradient(18rpx at 18rpx 0px, transparent 18rpx, #fff 18rpx);
      right: 0rpx;
      bottom: -18rpx;
      transform: scaleX(-1) scaleY(-1);
    }
    &::after {
      content: '';
      display: block;
      height: 18rpx;
      width: 18rpx;
      position: absolute;
      z-index: 98;
      background: radial-gradient(18rpx at 18rpx 0px, transparent 18rpx, #fff 18rpx);
      right: 0rpx;
      top: -18rpx;
      transform: scaleX(-1);
    }
  }
}
</style>
