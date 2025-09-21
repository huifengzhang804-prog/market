<!-- max-img-model 最大父盒子不添加任何类名 子级添加类名 max-img-model -->
<!-- a-line-two-model 最大父盒子添加 a-line-two-model 类名 子级添加类名 a-line-two-model__item-->
<!-- horizontal-scroll-model 最大父盒子添加 horizontal-scroll-model 类名 子级添加类名 horizontal-scroll-model__item-->
<!--  -->
<script setup lang="ts">
import { ref, reactive } from 'vue'
import LiveStatus from '@plugin/live/components/decorationComponents/studio-status.vue'
import { wxLiveRoomUrlFn } from '@plugin/live/components/decorationComponents'
import { doGetLiveList } from '@/apis/plugin/live'
import type { FormData } from '@plugin/live/components/decorationComponents/types'
import type { ApiLiveItem } from '@pluginPackage/live/components/types'
import type { PropType } from 'vue'

const $props = defineProps({
  decorationProperties: {
    type: Object as PropType<FormData>,
    default() {
      return {}
    },
  },
})
const liveList = ref<ApiLiveItem[]>([])
const pageConfig = reactive({
  current: 1,
  pages: 1,
})

initLiveList(false)

/**
 * 直播列表
 */
async function initLiveList(isLoad = false) {
  if (!isLoad) {
    // 刷新
    pageConfig.current = 1
    liveList.value = await getLiveList()
  } else if (isLoad && pageConfig.current < pageConfig.pages) {
    // 更新
    pageConfig.current++
    liveList.value = liveList.value.concat(await getLiveList())
  }
}
async function getLiveList() {
  const { code, data, msg } = await doGetLiveList({ ...pageConfig, size: $props.decorationProperties.listNum })
  if (code !== 200) {
    uni.showToast({ title: `${msg ? msg : '获取直播列表失败'}`, icon: 'none' })
    return []
  }
  pageConfig.pages = data.pages
  return data.records
}
/**
 * 直播二级页面
 */
const handleNavToLive = () => {
  uni.navigateTo({
    url: '/pluginPackage/live/views/Studio',
  })
}
/**
 * 横向滚动出发
 */
const handleScrolltolower = () => {
  console.log('横向滚动出发')
}
/**
 * 跳转到指定直播间
 */
const handleClick = (wechatRoomId: string) => {
  uni.navigateTo({
    url: wxLiveRoomUrlFn([wechatRoomId]),
  })
}
</script>

<template>
  <view class="live" :style="{ padding: `5rpx ${decorationProperties.listMargin * 2}rpx` }">
    <view class="live__title">
      <text class="live__title--h1">{{ decorationProperties.listTitle }}</text>
      <text class="live__title--more" @click="handleNavToLive">查看更多</text>
    </view>
    <view
      v-if="decorationProperties.codeStyle < 3"
      :class="decorationProperties.codeStyle === 1 ? '' : 'a-line-two-model'"
      @scrolltolower="handleScrolltolower"
    >
      <view
        v-for="item in liveList"
        :key="item.id"
        :style="{
          margin: `${decorationProperties.upDownMargin ? decorationProperties.upDownMargin * 2 : 1}rpx 0rpx ${
            decorationProperties.upDownMargin ? decorationProperties.upDownMargin * 2 : 1
          }rpx 0`,
          borderRadius: `${decorationProperties.borderRadius ? 10 : 0}rpx`,
        }"
        :class="
          decorationProperties.codeStyle === 1
            ? 'max-img-model'
            : decorationProperties.codeStyle === 2
              ? 'a-line-two-model__item'
              : 'horizontal-scroll-model__item'
        "
        @click="handleClick(item.wechatRoomId)"
      >
        <u-image :src="item.coverImg" width="100%" height="100%"></u-image>
        <live-status :active="item.status" />
        <view class="max-img-model__title">
          <u-image :src="item.shopLogo" :width="40" :height="40" shape="circle"></u-image>
          <text class="max-img-model__title--name">{{ item.shopName }}</text>
        </view>
        <!-- <view class="max-img-model__nickname">{{ item.shopName }}</view> -->
        <!-- <view class="max-img-model__love">97</view> -->
      </view>
    </view>
    <!-- 横向滚动 -->
    <scroll-view
      v-if="decorationProperties.codeStyle === 3"
      class="horizontal-scroll-model"
      scroll-x
      enhanced
      :show-scrollbar="false"
      style="white-space: nowrap"
      @scrolltolower="handleScrolltolower"
    >
      <view
        v-for="item in liveList"
        :key="item.id"
        :src="item"
        :style="{
          margin: `${decorationProperties.upDownMargin ? decorationProperties.upDownMargin * 2 : 1}rpx ${
            decorationProperties.codeStyle === 3 ? 10 : 0
          }rpx ${decorationProperties.upDownMargin ? decorationProperties.upDownMargin * 2 : 1}rpx 0`,
          borderRadius: `${decorationProperties.borderRadius ? 10 : 0}rpx`,
        }"
        class="horizontal-scroll-model__item"
        @click="handleClick(item.wechatRoomId)"
      >
        <u-image :src="item.coverImg" width="100%" height="100%"></u-image>
        <live-status :active="item.status" />
        <view class="max-img-model__title">
          <u-image :src="item.shopLogo" :width="40" :height="40" shape="circle"></u-image>
          <text class="max-img-model__title--name">{{ item.shopName }}</text>
        </view>
        <view class="max-img-model__nickname">{{ item.roomName }}</view>
        <!-- <view class="max-img-model__love">97</view> -->
      </view>
    </scroll-view>
  </view>
</template>

<style lang="scss" scoped>
@include b(live) {
  box-sizing: border-box;
  background: #fff;
  @include e(title) {
    color: #000;
    @include flex(space-between);
    padding: 30rpx 0;
    @include m(h1) {
      font-size: 36rpx;
      font-weight: bold;
    }
    @include m(more) {
      &::after {
        content: '>';
        display: inline-block;
        margin-left: 4rpx;
        font-size: 16rpx;
      }
    }
  }
}
/* 大图模式 */
@include b(max-img-model) {
  color: #000;
  position: relative;
  width: 100%;
  height: 400rpx;
  background-color: rgba(102, 102, 102);
  margin-bottom: 1rpx;
  &:last-child {
    margin-bottom: 0;
  }
  @include e(title) {
    font-size: 26rpx;
    display: flex;
    align-items: center;
    position: absolute;
    bottom: 100rpx;
    left: 20rpx;
    right: 0;
    @include m(name) {
      @include utils-ellipsis;
      display: inline-block;
      margin-left: 10rpx;
    }
  }
  @include e(nickname) {
    width: 150rpx;
    font-size: 24rpx;
    position: absolute;
    left: 30rpx;
    bottom: 60rpx;
    @include utils-ellipsis;
  }
  @include e(love) {
    position: absolute;
    right: 50rpx;
    bottom: 60rpx;
    height: 30rpx;
    line-height: 30rpx;
    &::before {
      content: '❤️';
      display: inline-block;
      text-align: center;
      border-radius: 50%;
      background: #fff;
      height: 40rpx;
      line-height: 40rpx;
      width: 40rpx;
      text-align: center;
      margin-right: 10rpx;
    }
  }
}
/* 一行两个 */
@include b(a-line-two-model) {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  color: #000;
  @include e(item) {
    position: relative;
    width: calc(50% - 10rpx);
    height: 400rpx;
    background-color: rgba(102, 102, 102);
    margin-bottom: 1rpx;
  }
}
/* 横向滚动 */
@include b(horizontal-scroll-model) {
  display: flex;
  width: 100%;
  color: #000;
  justify-content: flex-start;
  @include e(item) {
    display: inline-block;
    flex-shrink: 0;
    position: relative;
    width: calc(50% - 10rpx);
    height: 400rpx;
  }
}
</style>
