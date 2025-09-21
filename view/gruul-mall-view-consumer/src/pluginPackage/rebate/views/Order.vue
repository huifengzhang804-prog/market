<script setup lang="ts">
import { ref, onMounted, reactive, computed } from 'vue'
import OrderRebateCard from '@/pluginPackage/rebate/views/components/order-rebate-card.vue'
import { onReady } from '@dcloudio/uni-app'
import { doGetRebateOrderList } from '../apis'
import { type RebateOrder } from './types/index'
import OrderItem from './components/order-item.vue'
import Auth from '@/components/auth/auth.vue'

const paginationOptions = reactive({
  page: 1,
  totalPage: 0,
})
const orderList = ref<RebateOrder[]>([])
const keyword = ref('')
const current = ref(0)
const searchHeight = uni.upx2px(122)
const navBarHeight = ref(0)
const list = [
  {
    key: '',
    name: '全部',
  },
  {
    key: 'PAID',
    name: '已付款',
  },
  {
    key: 'COMPLETED',
    name: '已完成',
  },
  {
    key: 'CLOSED',
    name: '已关闭',
  },
]

onReady(() => {
  navBarHeight.value = getNavBarHeight()
})
const change = (currentPage: number) => {
  current.value = currentPage
  paginationOptions.page = 1
  initialData()
}

const loadMore = () => {
  if (paginationOptions.page < paginationOptions.totalPage) {
    paginationOptions.page++
    initialData()
  }
}
function getNavBarHeight() {
  const systemInfo = uni.getSystemInfoSync()
  return systemInfo.windowTop
}

const initialData = async () => {
  const result = await doGetRebateOrderList({ current: paginationOptions.page, status: list[current.value]?.key, keyword: keyword.value })
  paginationOptions.totalPage = Number(result.data?.pages)
  paginationOptions.page = Number(result?.data?.current)
  if (paginationOptions.page === 1) orderList.value = []
  orderList.value = [...orderList.value, ...(result?.data?.records || [])]
}
onMounted(async () => {
  initialData()
})
const handleSearch = () => {
  paginationOptions.page = 1
  initialData()
  orderList.value = []
}
const isH5 = ref(false)
// #ifdef H5
isH5.value = true
// #endif

const height = computed(() => {
  return useScreenHeight().value - useBottomSafe().value - searchHeight
})
const scrollTop = ref(0)
const topFixed = computed(() => {
  if (scrollTop.value <= searchHeight + 44) {
    return searchHeight + (isH5.value ? 44 : 0) + uni.upx2px(206) - scrollTop.value
  }
  return searchHeight + (isH5.value ? 44 : 0)
})
const scroll = (e: any) => {
  scrollTop.value = e.detail.scrollTop
}
</script>

<template>
  <!-- 搜索 -->
  <view class="search" :style="{ position: 'sticky', top: isH5 ? '44px' : 0, zIndex: 99 }">
    <u-search v-model="keyword" :height="70" placeholder="请输入订单号或商品名称" @search="handleSearch" @custom="handleSearch"></u-search>
  </view>
  <!-- 搜索 -->
  <scroll-view scroll-y :style="`height:${height}px`" @scrolltolower="loadMore" @scroll="scroll">
    <!-- card -->
    <OrderRebateCard />
    <!-- card -->
    <view :style="`top:${topFixed}px`" class="tab_container">
      <u-tabs :model-value="current" active-color="#fa3534" :is-scroll="false" :list="list" @update:model-value="change"></u-tabs>
    </view>
    <view style="padding: 20rpx; padding-top: 50px">
      <view v-for="orderItem in orderList" :key="orderItem.orderNo" class="order-item">
        <OrderItem :order-item="orderItem" />
      </view>
    </view>
  </scroll-view>
  <Auth />
</template>

<style scoped lang="scss">
@include b(search) {
  height: 122rpx;
  background: #fff;
  border-top: 1rpx solid #f2f2f2;
  line-height: 122rpx;
  padding: 0 20rpx;
}

@include b(order-item) {
  /* N-我的订单-卡片样式 */
  background: rgb(255, 255, 255);
  border-radius: 20rpx;
  margin-bottom: 20rpx;
}
.tab_container {
  position: fixed;
  width: 100%;
  left: 0;
  z-index: 99;
}
</style>
