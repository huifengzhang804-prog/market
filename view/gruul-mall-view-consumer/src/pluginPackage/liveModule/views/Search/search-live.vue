<script setup lang="ts">
import { ref, reactive, computed, watch } from 'vue'
import { doGetDiscoverLiveRoomList } from '@/pluginPackage/liveModule/apis/UserLive'
import SearchLiveItem from '@/pluginPackage/liveModule/views/Search/search-live-item.vue'
import { toUserLive, formatPullAddress } from '../components/liveList'

import { EMPTY_GB } from '@/constant'
import { LIVE_LIST_STATUS } from '@/pluginPackage/liveModule/apis/CreateLive/model'
import type { FollowLiveRoomList } from '@/pluginPackage/liveModule/apis/UserLive/model'

const $props = defineProps({
  keyword: {
    type: String,
    default: '',
  },
  usedHeight: {
    type: Number,
    default: 122,
  },
})
const searchParams = reactive({
  keyword: '',
  status: LIVE_LIST_STATUS.PROCESSING,
})
const list = ref<Array<FollowLiveRoomList>>([])
const waterFallRef = ref()
const pageConfig = reactive({
  current: 1,
  pages: 1,
  size: 10,
})
const isLoding = ref(false)
const status = computed(() => {
  if (isLoding.value) return 'loading'
  if (pageConfig.size * pageConfig.pages === list.value.length) return 'nomore'
  return 'loadmore'
})

watch(
  () => $props.keyword,
  (val) => {
    initList()
  },
  {
    immediate: true,
  },
)

async function initList(isLoad = false) {
  isLoding.value = true
  if (!isLoad) {
    // 刷新
    pageConfig.current = 1
    pageConfig.pages = 1
    list.value = await getLiveList()
    waterFallRef.value?.refresh()
  } else if (isLoad && pageConfig.current < pageConfig.pages) {
    // 更新
    pageConfig.current++
    list.value = list.value.concat(await getLiveList())
  }
  isLoding.value = false
}
// 获取直播列表
async function getLiveList() {
  searchParams.keyword = $props.keyword
  // @ts-ignore
  const { code, data } = await doGetDiscoverLiveRoomList({ ...searchParams, ...pageConfig })
  if (code !== 200 || !data) {
    uni.showToast({
      icon: 'none',
      title: '获取直播列表失败',
    })
    return []
  }
  pageConfig.pages = data.pages
  return data.records
}
const handleItemClick = (e: FollowLiveRoomList) => {
  const { liveId: id, status } = e
  toUserLive({ id, pullAddress: formatPullAddress(e.pullAddress), status })
}
</script>
<template>
  <scroll-view scroll-y :style="{ height: `calc(100vh - ${usedHeight}rpx)` }" @scrolltolower="initList(true)">
    <view v-if="list.length" style="padding: 10rpx">
      <search-live-item v-for="(item, index) in list" :key="index" :item="item" @to-live="handleItemClick(item)" />
      <view style="padding: 20rpx 0">
        <u-loadmore :status="status" />
      </view>
    </view>
    <u-empty v-if="!list.length" text="没有找到相关的直播间" icon-size="400" :src="EMPTY_GB.COLLECTION_EMPTY"></u-empty>
  </scroll-view>
</template>

<style scoped lang="scss"></style>
