<template>
  <view>
    <view style="border-top: 1rpx solid rgba(0, 0, 0, 0.1)"></view>
    <u-tabs
      v-model="current"
      :list="list"
      font-size="28"
      height="96"
      active-color="#FA3534"
      bar-width="58"
      bar-height="8"
      item-width="150"
      @change="change"
    ></u-tabs>
    <view style="height: 20rpx"></view>
    <scroll-view scroll-y :style="{ height: `${height}px` }" :lower-threshold="100" @click="openId = ''" @scrolltolower="handleGetOrderList">
      <view style="padding: 0 20rpx 20rpx">
        <OrderItem v-for="item in orderList" :key="item.id" :order="item" @init-list="initList" />
      </view>
    </scroll-view>
  </view>
  <Auth />
</template>

<script setup lang="ts">
import { ref, provide, computed } from 'vue'
import OrderItem from './components/orderItem.vue'
import { doGetIntegralOrderList } from '@/pluginPackage/integral/api'
import type { IOrderList, IIntegralOrderListParams } from '@/pluginPackage/integral/api/types'
import Auth from '@/components/auth/auth.vue'

const list = ref([{ name: '全部' }, { name: '待支付' }, { name: '待发货' }, { name: '待收货' }, { name: '已完成' }])
const statusArr: ['', 'UNPAID', 'PAID', 'ON_DELIVERY', 'ACCOMPLISH', 'SYSTEM_CLOSED'] = [
  '',
  'UNPAID',
  'PAID',
  'ON_DELIVERY',
  'ACCOMPLISH',
  'SYSTEM_CLOSED',
]
const current = ref(0)

const openId = ref('')
const handleOpen = (id: string) => {
  if (openId.value === id) {
    openId.value = ''
  } else {
    openId.value = id
  }
}
provide('handleOpen', handleOpen)
provide('openId', openId)
// 更改tab栏
const change = (index: number) => {
  params.status = statusArr[index]
  initList()
}

const initList = () => {
  params.current = 1
  params.pages = 1
  loadMore = true
  orderList.value = []
  handleGetOrderList()
}

const params: IIntegralOrderListParams = {
  size: 6,
  current: 1,
  pages: 1,
  status: '',
}

// 订单列表数据
const orderList = ref<IOrderList[]>([])

let loadMore = true
// 获取订单列表数据
const handleGetOrderList = async () => {
  if (!loadMore) return
  const { code, data, msg } = await doGetIntegralOrderList(params)
  if (code !== 200 || !data) {
    return uni.showToast({ title: `${msg ? msg : '获取积分明细列表失败'}`, icon: 'none' })
  }
  params.pages = data.pages
  if (params.current < params.pages) {
    params.current++
  } else {
    loadMore = false
  }
  orderList.value = orderList.value.concat(data.records)
}
handleGetOrderList()
const height = computed(() => {
  let contentHeight = useScreenHeight().value - useBottomSafe().value - uni.upx2px(96)
  return contentHeight
})
</script>

<style scoped lang="scss"></style>
