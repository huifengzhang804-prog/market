<script setup lang="ts">
import { ref, reactive, type PropType } from 'vue'
import CreateLiveItem from './create-live-item.vue'
import { doGetLiveRoomAnchorList } from '@/pluginPackage/liveModule/apis/CreateLive'
import type { GetLiveRoomAnchorList, GetUserMessage } from '@/pluginPackage/liveModule/apis/CreateLive/model'
import { onShow } from '@dcloudio/uni-app'

defineProps({
  usedHeight: {
    type: Number,
    default: 330,
  },
  status: { type: String as PropType<GetUserMessage['status']>, default: 'NORMAL' },
})
const pageConfig = reactive({
  size: 5,
  current: 1,
  pages: 1,
})
const isShowEmpty = ref(false)
const liveList = ref<GetLiveRoomAnchorList[]>([])

onShow(() => roomAnchorList())

/**
 * 直播列表
 */
async function roomAnchorList(isLoad = false) {
  isShowEmpty.value = false
  if (!isLoad) {
    // 刷新
    pageConfig.current = 1
    liveList.value = await initLiveRoomAnchorList()
  } else if (isLoad && pageConfig.current < pageConfig.pages) {
    // 更新
    pageConfig.current++
    liveList.value = liveList.value.concat(await initLiveRoomAnchorList())
  }
  isShowEmpty.value = true
}
async function initLiveRoomAnchorList() {
  const { code, data, msg } = await doGetLiveRoomAnchorList(pageConfig)
  if (code !== 200) {
    uni.showToast({ title: `${msg ? msg : '获取直播列表失败'}`, icon: 'none' })
    return []
  }
  if (!data) {
    return []
  }
  pageConfig.pages = data.pages
  return data.records
}
</script>

<template>
  <scroll-view scroll-y :style="{ height: `calc(100vh - ${usedHeight}rpx)` }" @scrolltolower="roomAnchorList(true)">
    <create-live-item
      v-for="(item, idx) in liveList"
      :key="idx"
      :status="status"
      :room-anchor-item="item"
      class="live_item"
      @init-list="roomAnchorList"
    />
    <view style="height: 80rpx"></view>
  </scroll-view>
</template>

<style scoped lang="scss"></style>
