<script setup lang="ts">
import { computed, type PropType, useSlots } from 'vue'
import { useSettingStore } from '@/store/modules/setting'
import { useStatusBar } from '@/hooks'

enum TEXTALIGN {
  start,
  end,
  left,
  right,
  center,
  justify,
}
type TextAlign = keyof typeof TEXTALIGN

const $props = defineProps({
  icon: { type: String, default: 'arrow-left' },
  title: {
    type: String,
    default: '',
  },
  height: {
    type: String,
    default: '44',
  },
  bgColor: {
    type: String,
    default: '#fff',
  },
  color: {
    type: String,
    default: '#000',
  },
  textAlign: { type: String as PropType<TextAlign>, default: 'center' },
  showBack: {
    type: Boolean,
    default: true,
  },
  paddingBottom: {
    type: String,
    default: '18rpx',
  },
})
const $settingStore = useSettingStore()
// 状态栏高度
let statusBarHeight = useStatusBar()

const pages = getCurrentPages()
const existTitleSlot: any = useSlots().title
const isHome = computed(() => {
  let homeFlag = false
  // #ifdef H5
  homeFlag = !history.state?.back
  // #endif
  // #ifndef H5
  homeFlag = !pages[pages.length - 2]
  // #endif
  return homeFlag
})
let navStyle: any
// #ifndef H5
navStyle = {
  height: `${statusBarHeight.value}px`,
  background: $props.bgColor,
  color: $props.color,
  textAlign: $props.textAlign,
}
// #endif
// #ifdef H5
navStyle = {
  height: `${Number($props.height) + statusBarHeight.value}px`,
  lineHeight: `${Number($props.height) + statusBarHeight.value}px`,
  background: $props.bgColor,
  color: $props.color,
  textAlign: $props.textAlign,
}
// #endif

const navBack = () => {
  // #ifdef H5
  if (history.length > 1) {
    history.back()
  } else {
    $settingStore.NAV_TO_INDEX('首页')
  }
  // #endif
  // #ifndef H5
  if (pages[pages.length - 2]) {
    uni.navigateBack({
      delta: 1,
    })
  } else {
    $settingStore.NAV_TO_INDEX('首页')
  }
  // #endif
}
</script>

<template>
  <view class="nav_container">
    <view class="nav" :style="{ ...navStyle, paddingBottom: paddingBottom }">
      <view class="nav__arrow" @click="navBack">
        <u-icon v-if="!isHome && $props.showBack" :color="$props.color" :name="$props.icon" style="margin-left: 10rpx" size="36" />
      </view>
      <text v-if="!existTitleSlot">{{ $props.title }}</text>
      <slot v-else name="title"></slot>
    </view>
    <!-- #ifndef H5-->
    <view :style="{ height: `${statusBarHeight}px` }"></view>
    <!-- #endif -->
  </view>
</template>

<style lang="scss" scoped>
.nav_container {
  position: sticky;
  top: 0;
  left: 0;
  z-index: 99999999;
}
// #ifdef H5
@include b(nav) {
  position: relative;
  text-align: center;
  font-weight: 700;
  font-size: 30rpx;
  @include e(arrow) {
    position: absolute;
    top: 50%;
    left: 0;
    margin-top: -22rpx;
    @include flex();
  }
}
// #endif
// #ifndef H5
@include b(nav) {
  width: 750rpx;
  position: fixed;
  top: 0;
  left: 0;
  font-weight: 700;
  font-size: 30rpx;

  display: flex;
  justify-content: center;
  align-items: flex-end;
  // background: #fff;
  z-index: 9999;
  @include e(arrow) {
    position: absolute;
    bottom: 24rpx;
    left: 4rpx;
    @include flex();
  }
}
// #endif
@include b(right) {
  position: absolute;
  right: 35rpx;
  color: rgb(16, 16, 16) 100%;
  font-weight: normal;
  font-size: 28rpx;
  position: sticky;
}
</style>
