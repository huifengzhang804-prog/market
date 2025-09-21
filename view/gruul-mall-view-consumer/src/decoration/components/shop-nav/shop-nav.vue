<script setup lang="ts">
import { type PropType, computed, ref } from 'vue'
import linkNavTo from '@/libs/linkNavTo'
import { LIVE_ROUTER_BLACK_LIST, LIVE_ANCHOR_ROUTER_BLACK_LIST } from '@/config/live-icon-https'
import { useAppStore } from '@/store/modules/app'
import { cropImg } from '@/utils'
import type { NavigationDecorationProp } from '../types'
import { cloneDeep } from 'lodash'

const $props = defineProps({
  decorationProperties: {
    type: Object as PropType<NavigationDecorationProp>,
    default() {
      return {}
    },
  },
})
const $useAppStore = useAppStore()
const newDecorationProperties = computed(() => {
  // #ifdef H5
  // 过滤 APP 直播
  return $props.decorationProperties?.navigationList.filter((item) => !LIVE_ROUTER_BLACK_LIST.includes(item.link.url)) || []
  // #endif
  // eslint-disable-next-line no-unreachable
  if (!$useAppStore.IS_ANCHOR())
    return $props.decorationProperties?.navigationList.filter((item) => !LIVE_ANCHOR_ROUTER_BLACK_LIST.includes(item.link.url)) || []
  return $props.decorationProperties?.navigationList || []
})

const handleTap = (groupIndex: number, index: number) => {
  const link = computedShopNavGroups.value[groupIndex][index].link
  if (link.url === '/basePackage/pages/merchant/Index') {
    uni.$emit('handleChangeTabCall', { index: 0, link })
    return
  }
  linkNavTo(link)
}
const computedShopNavGroups = computed(() => {
  let pageNavTotal = 0
  if ($props.decorationProperties?.rows === 2) {
    if ($props.decorationProperties.rowNums === 4) {
      pageNavTotal = 8
    } else {
      pageNavTotal = 10
    }
  } else if ($props.decorationProperties.rowNums === 4) {
    pageNavTotal = 4
  } else {
    pageNavTotal = 5
  }
  let currentPageTotal = 0
  const navGroups: NavigationDecorationProp['navigationList'][] = []
  let groupList: NavigationDecorationProp['navigationList'] = []
  newDecorationProperties.value.forEach((item) => {
    if (currentPageTotal < pageNavTotal) {
      groupList.push(item)
      currentPageTotal++
    } else {
      navGroups.push(cloneDeep(groupList))
      groupList = []
      groupList.push(item)
      currentPageTotal = 0
    }
  })
  if (groupList?.length) navGroups.push(groupList)
  return navGroups
})
const shopNavItemClasses = computed(() => (index: number) => {
  const classNames: string[] = []
  if ($props.decorationProperties.rowNums === 4) {
    classNames.push('four-mr')
    if ([3, 7].includes(index)) classNames.push('n-mr')
    if (index > 3) classNames.push('four-mt')
  } else {
    classNames.push('five-mr')
    if ([4, 9].includes(index)) classNames.push('n-mr')
    if (index > 4) classNames.push('five-mt')
  }
  return classNames
})
const swiperCurrent = ref(0)
const handleChangeSwiper = (e: any) => {
  swiperCurrent.value = e.detail.current
}
const swiperPercent = computed(() => {
  return (((swiperCurrent.value + 1) / computedShopNavGroups.value.length) * 100).toFixed(0)
})
const computedSwiperHeight = computed(() => {
  if ($props.decorationProperties.rowNums === 4) {
    if (computedShopNavGroups.value?.[0]?.length > 4) {
      // 一行4个，显示两行
      return 300
    } else {
      // 一行4个，显示一行
      return 150
    }
  } else if (computedShopNavGroups.value?.[0]?.length > 5) {
    // 一行5个，显示两行
    return 246
  } else {
    // 一行5个，显示一行
    return 118
  }
})
</script>

<script lang="ts">
export default {
  options: { styleIsolation: 'shared' },
}
</script>

<template>
  <view class="shopNav">
    <swiper :style="{ height: computedSwiperHeight + 'rpx' }" class="shopNav__swiper" :current="swiperCurrent" @change="handleChangeSwiper">
      <swiper-item
        v-for="(group, groupIndex) in computedShopNavGroups"
        :key="groupIndex"
        class="shopNav__swiper--item"
        :class="decorationProperties.rowNums === 4 ? 'nav-four' : 'nav-five'"
      >
        <view
          v-for="(item, index) in group"
          :key="index"
          :class="shopNavItemClasses(index)"
          :data-index="index"
          @click.stop="handleTap(groupIndex, index)"
        >
          <!-- #ifndef H5 -->
          <image
            v-if="item.navIcon"
            :class="decorationProperties.rowNums === 4 ? 'img-four' : 'img-five'"
            mode="aspectFit"
            :src="cropImg(item.navIcon, 100, 100)"
            :lazy-load="true"
          ></image>
          <!-- #endif -->
          <!-- #ifdef H5 -->
          <img
            v-if="item.navIcon"
            :class="decorationProperties.rowNums === 4 ? 'img-four' : 'img-five'"
            mode="aspectFit"
            :src="cropImg(item.navIcon, 100, 100)"
            :lazy-load="true"
          />
          <!-- #endif -->
          <view :class="decorationProperties.rowNums === 4 ? 'font-four' : 'font-five'" :style="'color:' + item.fontColor">{{ item.navName }}</view>
        </view>
      </swiper-item>
    </swiper>
    <view v-if="computedShopNavGroups.length > 1" class="shopNav__progress">
      <u-line-progress
        height="8"
        inactive-color="rgba(245, 67, 25, 0.28)"
        :percent="Number(swiperPercent)"
        active-color="#F54319"
        :show-percent="false"
        class="percentage"
      ></u-line-progress>
    </view>
  </view>
</template>

<style lang="scss" scoped>
@include b(shopNav) {
  margin: 20rpx auto 0;
  width: 710rpx;
  background-color: #fff;
  border-radius: 16rpx;
  padding-top: 32rpx;
  padding-bottom: 32rpx;
  margin-bottom: 20rpx;
  @include e(swiper) {
    @include m(item) {
      @include flex(flex-start, flex-start);
      flex-wrap: wrap;
    }
  }
  @include e(progress) {
    width: 76rpx;
    margin: 20rpx auto 0;
    border-radius: 24rpx;
  }
}
@include b(nav-four) {
  box-sizing: border-box;
  padding-left: 10rpx;
  padding-right: 12rpx;
}
@include b(nav-five) {
  box-sizing: border-box;
  padding-left: 4rpx;
  padding-right: 4rpx;
}
@include b(img-four) {
  width: 88rpx;
  height: 88rpx;
  border-radius: 50%;
}
@include b(img-five) {
  width: 74rpx;
  height: 74rpx;
  border-radius: 50%;
}
@include b(four-mr) {
  margin-right: 52rpx;
  @include flex();
  flex-direction: column;
}
@include b(five-mr) {
  margin-right: 18rpx;
  @include flex();
  flex-direction: column;
}
@include b(four-mt) {
  margin-top: 20rpx;
}
@include b(five-mt) {
  margin-top: 10rpx;
}
@include b(n-mr) {
  margin-right: 0;
}
@include b(font-four) {
  font-size: 28rpx;
  line-height: 40rpx;
  width: 132rpx;
  text-align: center;
  margin-top: 16rpx;
}
@include b(font-five) {
  font-size: 26rpx;
  line-height: 36rpx;
  width: 126rpx;
  text-align: center;
  margin-top: 8rpx;
}
</style>
