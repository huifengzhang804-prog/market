<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import HeadScroll from '@pluginPackage/live/components/head-scroll.vue'
import MainFigure from '@pluginPackage/live/components/main-figure.vue'
import { wxLiveRoomUrlFn } from '@plugin/live/components/decorationComponents'
import { doGetLiveList } from '@/apis/plugin/live'
import type { ApiLiveItem } from '@pluginPackage/live/components/types'

const reachLoading = ref(false)

const liveList = ref<ApiLiveItem[]>([])
const pageConfig = reactive({
  size: 5,
  current: 1,
  pages: 1,
})

initLiveList()

const loadMore = async () => {
  reachLoading.value = true
  await initLiveList(true)
  reachLoading.value = false
}

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
  const { code, data, msg } = await doGetLiveList(pageConfig)
  if (code !== 200) {
    uni.showToast({ title: `${msg ? msg : '获取直播列表失败'}`, icon: 'none' })
    return []
  }
  pageConfig.pages = data.pages
  return data.records
}
const handleClick = (wechatRoomId: string) => {
  uni.navigateTo({
    url: wxLiveRoomUrlFn([wechatRoomId]),
  })
}
const height = computed(() => {
  return useScreenHeight().value - useBottomSafe().value
})
</script>

<template>
  <scroll-view scroll-y :style="{ height: `${height}px` }" @scrolltolower="loadMore">
    <HeadScroll />
    <view class="title">精选直播</view>
    <view class="container">
      <MainFigure v-for="item in liveList" :key="item.id" :live-item="item" @click="handleClick(item.wechatRoomId)" />
    </view>
    <view class="reach-loading">
      <u-loading mode="flower" size="50" :show="reachLoading" />
    </view>
  </scroll-view>
</template>
<style scoped lang="scss">
@include b(title) {
  margin: 0 30rpx 30rpx 35rpx;
  font-weight: 700;
  font-size: 28rpx;
}
@include b(container) {
  padding: 20rpx;
  display: grid;
  grid-template-columns: calc(50% - 10rpx) calc(50% - 10rpx);
  grid-gap: 20rpx;
}
@include b(reach-loading) {
  text-align: center;
  height: 120rpx;
  line-height: 120rpx;
}
</style>
