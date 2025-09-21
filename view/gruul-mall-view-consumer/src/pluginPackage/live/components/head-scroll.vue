<script setup lang="ts">
import { ref, reactive } from 'vue'
import CircularStudio from '@pluginPackage/live/components/circular-studio.vue'
import { doGetLiveList } from '@/apis/plugin/live'
import { wxLiveRoomUrlFn } from '@plugin/live/components/decorationComponents'
import type { ApiLiveItem } from '@pluginPackage/live/components/types'

const liveList = ref<ApiLiveItem[]>([])
const pageConfig = reactive({
  size: 5,
  current: 1,
  pages: 1,
  status: 'loadmore',
})

initLiveList()

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
const handleScrolltolower = () => {
  initLiveList(true)
}
const handleClick = (wechatRoomId: string) => {
  uni.navigateTo({
    url: wxLiveRoomUrlFn([wechatRoomId]),
  })
}
</script>

<template>
  <scroll-view scroll-x enhanced :show-scrollbar="false" style="white-space: nowrapp" class="room-head" @scrolltolower="handleScrolltolower">
    <view class="room-head__circular">
      <CircularStudio v-for="item in liveList" :key="item.id" :live-item="item" @click="handleClick(item.wechatRoomId)" />
    </view>
  </scroll-view>
</template>

<style scoped lang="scss">
@include b(room-head) {
  @include e(circular) {
    @include flex;
    justify-content: flex-start;
    padding: 20rpx;
  }
}
</style>
