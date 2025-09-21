<script setup lang="ts">
import { computed, ref } from 'vue'
import { doGetDiscoverLiveRoomList } from '@/pluginPackage/liveModule/apis/UserLive'
import LiveItem from './live-item.vue'
import { TABBAR_URL } from './index'
import ListEmpty from './list-empty.vue'
import { formatPullAddress } from './index'
import { toUserLive } from '@/pluginPackage/liveModule/views/components/liveList'
import type { Joint_Live_List_Status } from '@/pluginPackage/liveModule/apis/CreateLive/model'
import type { FollowLiveRoomList } from '@/pluginPackage/liveModule/apis/UserLive/model'

const props = defineProps({
  usedHeight: {
    type: Number,
    // 单位 rpx
    default: 0,
  },
})
const emit = defineEmits(['change'])
// 组件默认高度 rpx
const tabbar_height = 110
const pageConfig = ref({
  current: 1,
  pages: 1,
  size: 10,
  status: 'PROCESSING' as Joint_Live_List_Status,
})
const list = [
  {
    iconPath: TABBAR_URL.HOME_ACTIVE,
    selectedIconPath: TABBAR_URL.HOME_UN_ACTIVE,
    text: '直播广场',
    customIcon: false,
  },
  {
    iconPath: TABBAR_URL.LIVE_UN_ACTIVE,
    selectedIconPath: TABBAR_URL.LIVE_ACTIVE,
    text: '直播预告',
    customIcon: false,
  },
]
const current = ref(0)
const liveList = ref<FollowLiveRoomList[]>([])

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
  console.log('liveList.value', liveList.value)
}
async function getLiveList() {
  const { code, data, msg } = await doGetDiscoverLiveRoomList(pageConfig.value)
  if (code !== 200 || !data) {
    uni.showToast({ title: `${msg ? msg : '获取直播列表失败'}`, icon: 'none' })
    return []
  }
  pageConfig.value.pages = data.pages
  return data.records
}
const handleClickItem = (e: FollowLiveRoomList) => {
  if (pageConfig.value.status === 'PROCESSING') {
    const { liveId, status } = e
    toUserLive({ id: liveId, pullAddress: formatPullAddress(e.pullAddress), status })
  }
}
const change = (e: number) => {
  pageConfig.value.status = e === 0 ? 'PROCESSING' : 'NOT_STARTED'
  liveList.value = []
  initLiveList()
}
const height = computed(() => {
  return useScreenHeight().value - useBottomSafe().value - uni.upx2px(props.usedHeight)
})
</script>

<template>
  <view class="follow-widget" :style="{ top: usedHeight + 'rpx' }"> </view>
  <!-- 顶部占位 目的调用页面级触底事件 列表性能 -->
  <view :style="{ height: usedHeight + 'rpx' }"></view>
  <scroll-view scroll-y :style="`padding: 10rpx;height:${height}px`" @scrolltolower="initLiveList(true)">
    <u-waterfall v-if="liveList.length" ref="waterFallRef" v-model="liveList" idKey="liveId">
      <template #left="{ leftList }">
        <view v-for="(item, index) in leftList" :key="index" class="water_item" @click="handleClickItem(item)">
          <u-lazy-load threshold="-450" border-radius="16" :image="item.pic" :index="index"></u-lazy-load>
          <LiveItem
            :image="item.pic"
            :live-title="item.liveTitle"
            :is-reservation="item.isReservation"
            :shop-name="item.shopName"
            :make-an-appointment="current === 1"
            :begin-time="item.beginTime"
            :live-id="item.liveId"
            :active="item.status"
            :shop-id="item.shopId"
            @init-list="initLiveList"
          />
        </view>
      </template>
      <template #right="{ rightList }">
        <view v-for="(item, index) in rightList" :key="index" class="water_item" @click="handleClickItem(item)">
          <u-lazy-load threshold="-450" border-radius="16" :image="item.pic" :index="index"></u-lazy-load>
          <LiveItem
            :image="item.pic"
            :live-title="item.liveTitle"
            :is-reservation="item.isReservation"
            :shop-name="item.shopName"
            :make-an-appointment="current === 1"
            :begin-time="item.beginTime"
            :live-id="item.liveId"
            :active="item.status"
            :shop-id="item.shopId"
            @init-list="initLiveList"
          />
        </view>
      </template>
    </u-waterfall>
    <ListEmpty
      v-if="!liveList.length"
      :used-height="usedHeight + tabbar_height"
      msg="还没有店铺开播，去首页看看吧~~~"
      btn-text="去首页"
      @click="emit('change')"
    />
  </scroll-view>
  <u-tabbar v-model="current" active-color="#fa3534" :list="list" :hide-tab-bar="false" @change="change"></u-tabbar>
</template>

<style scoped lang="scss"></style>
