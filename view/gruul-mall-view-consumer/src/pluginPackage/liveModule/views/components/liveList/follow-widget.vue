<script setup lang="ts">
import { computed, ref } from 'vue'
import { doGetFollowLiveRoomList } from '@/pluginPackage/liveModule/apis/UserLive'
import type { Joint_Live_List_Status } from '@/pluginPackage/liveModule/apis/CreateLive/model'
import { toUserLive } from '@/pluginPackage/liveModule/views/components/liveList'
import LiveItem from './live-item.vue'
import ListEmpty from './list-empty.vue'
import { formatPullAddress } from './index'
import type { FollowLiveRoomList } from '@/pluginPackage/liveModule/apis/UserLive/model'

const props = defineProps({
  usedHeight: {
    type: Number,
    // 单位 rpx
    default: 0,
  },
})
const emit = defineEmits(['change'])
const current = ref(0)
const u_tabs_height = 80
const pageConfig = ref({
  current: 1,
  pages: 1,
  size: 10,
  status: 'PROCESSING' as Joint_Live_List_Status,
})
const liveList = ref<FollowLiveRoomList[]>([])
const list = [
  {
    name: '直播中',
  },
  {
    name: '预告清单',
  },
]

initLiveList(false)
/**
 * 直播列表
 */
async function initLiveList(isLoad = false) {
  if (!isLoad) {
    // 刷新
    pageConfig.value.current = 1
    liveList.value = await getLiveList()
  } else if (isLoad && pageConfig.value.current < pageConfig.value.pages) {
    // 更新
    pageConfig.value.current++
    liveList.value = liveList.value.concat(await getLiveList())
  }
}
async function getLiveList() {
  const { code, data, msg } = await doGetFollowLiveRoomList(pageConfig.value)
  if (code !== 200 || !data) {
    uni.showToast({ title: `${msg ? msg : '获取直播列表失败'}`, icon: 'none' })
    return []
  }
  pageConfig.value.pages = data.pages
  return data.records
}
const handleClickItem = (e: FollowLiveRoomList) => {
  if (e.status === 'PROCESSING') {
    const { liveId, status } = e
    toUserLive({ id: liveId, pullAddress: formatPullAddress(e.pullAddress), status })
  }
}
const handleTabsChange = (e: number) => {
  pageConfig.value.status = e === 0 ? 'PROCESSING' : 'NOT_STARTED'
  liveList.value = []
  initLiveList()
}

const height = computed(() => {
  return useScreenHeight().value - useBottomSafe().value - uni.upx2px(props.usedHeight)
})
</script>

<template>
  <view class="follow-widget" :style="{ top: usedHeight + 'rpx' }">
    <view style="width: 375rpx">
      <u-tabs v-model="current" :list="list" :is-scroll="false" active-color="#fa3534" style="width: 375rpx" @change="handleTabsChange"></u-tabs>
    </view>
  </view>
  <!-- 顶部占位 目的调用页面级触底事件 列表性能 -->
  <view :style="{ height: usedHeight + u_tabs_height + 'rpx' }"></view>
  <scroll-view scroll-y :style="`padding: 10rpx;height:${height}px`" @scrolltolower="initLiveList(true)">
    <u-waterfall v-if="liveList.length" ref="waterFallRef" v-model="liveList" idKey="liveId">
      <template #left="{ leftList }">
        <view v-for="(item, index) in leftList" :key="index" class="water_item" @click="handleClickItem(item)">
          <u-lazy-load threshold="-450" border-radius="16" :image="item.pic" :index="index"></u-lazy-load>
          <LiveItem
            :make-an-appointment="current === 1"
            :image="item.pic"
            :live-title="item.liveTitle"
            :shop-name="item.shopName"
            :active="item.status"
            :live-id="item.liveId"
            :shop-id="item.shopId"
            :is-reservation="item.isReservation"
            :begin-time="item.beginTime"
            @init-list="initLiveList"
          ></LiveItem>
        </view>
      </template>
      <template #right="{ rightList }">
        <view v-for="(item, index) in rightList" :key="index" class="water_item" @click="handleClickItem(item)">
          <u-lazy-load threshold="-450" border-radius="16" :image="item.pic" :index="index"></u-lazy-load>
          <LiveItem
            :make-an-appointment="current === 1"
            :image="item.pic"
            :live-title="item.liveTitle"
            :shop-name="item.shopName"
            :active="item.status"
            :live-id="item.liveId"
            :shop-id="item.shopId"
            :is-reservation="item.isReservation"
            :begin-time="item.beginTime"
            @init-list="initLiveList"
          ></LiveItem>
        </view>
      </template>
    </u-waterfall>
    <ListEmpty v-if="!liveList.length" :used-height="usedHeight + u_tabs_height" @click="emit('change')"></ListEmpty>
  </scroll-view>
</template>

<style scoped lang="scss">
@include b(follow-widget) {
  background: #fff;
  position: fixed;
  width: 750rpx;
  z-index: 9;
}
</style>
