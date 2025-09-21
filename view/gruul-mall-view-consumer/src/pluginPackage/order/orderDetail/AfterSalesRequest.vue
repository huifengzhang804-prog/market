<script setup lang="ts">
import { ref, computed } from 'vue'
import SalesItem from '@/pluginPackage/order/orderDetail/components/sales-item.vue'
import { ADD_RESS_TYPES } from '@/apis/address/model'
import { useOrderDetailToAfs, type ShopOrderMap, type SetStorageSyncExtra } from '@/pluginPackage/order/orderList/hooks/afsHooks'
import { onLoad } from '@dcloudio/uni-app'
import Auth from '@/components/auth/auth.vue'

interface OrderData extends ShopOrderMap {
  extra: SetStorageSyncExtra
}

const { getStorageSync, toAfsPage } = useOrderDetailToAfs()
const orderData = ref<OrderData>({
  items: [],
  merged: {} as OrderData['merged'],
  extra: {
    distributionMode: ADD_RESS_TYPES.DISTRIBUTION.EXPRESS,
    orderNo: '',
    payStatus: 'PAID',
  },
})

onLoad(() => {
  initOrderData()
})
const orderDataComputed = computed(() => {
  if (orderData.value.items?.length) {
    return {
      unSend: sendStatusfilter(false),
      send: sendStatusfilter(true),
    }
  }
  return {
    unSend: [],
    send: [],
  }
})

function initOrderData() {
  orderData.value = getStorageSync()
}
function sendStatusfilter(status: false | true) {
  return orderData.value.items.filter((item) => (status ? item.packageId : !item.packageId))
}
</script>

<template>
  <view class="container">
    <view v-for="(item, index) in orderDataComputed.unSend" :key="index">
      <SalesItem :info="item" :extra="orderData.extra"></SalesItem>
    </view>
    <view v-for="(item, index) in orderDataComputed.send" :key="index">
      <SalesItem :info="item" :index="index + 1" :extra="orderData.extra"></SalesItem>
    </view>
  </view>
  <Auth />
</template>

<style scoped lang="scss">
@include b(container) {
  margin: 20rpx;
  padding-bottom: 20rpx;
  border-radius: 20rpx;
  background: #fff;
  min-height: 90vh;
}
</style>
