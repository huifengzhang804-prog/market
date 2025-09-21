<script lang="ts" setup>
import { computed, type PropType } from 'vue'
import { ADD_RESS_TYPES } from '@/apis/address/model'
import { deliveryType } from '@/pluginPackage/order/hooks/useAfssStatus'
import type { ApiOrder } from '@/pluginPackage/order/orderList/types'
import storage from '@/utils/storage'

const $props = defineProps({
  info: {
    type: Object as PropType<ApiOrder>,
    required: true,
  },
})
const address = computed(() => {
  if ($props.info.shopOrders[0].orderReceiver) {
    return $props.info.shopOrders[0].orderReceiver
  }
  if ($props.info.orderReceiver) {
    return $props.info.orderReceiver
  }
  return {
    address: '',
    id: '',
    mobile: '',
    name: '',
    area: [],
  }
})
const addressType = computed(() => {
  if ($props.info?.extra?.distributionMode) {
    return $props.info?.extra?.distributionMode
  }
  return ADD_RESS_TYPES.DISTRIBUTION.EXPRESS
})

const showAddress = computed(() => {
  return addressType.value !== 'VIRTUAL' && addressType.value !== 'SHOP_STORE'
})

function receiver(no: string) {
  if ($props.info.status !== 'UNPAID') return
  if (['VIRTUAL', 'SHOP_STORE'].includes($props.info.extra.distributionMode)) {
    return
  }
  storage.set('setAddRessOrderNo', no)
  uni.navigateTo({ url: `/basePackage/pages/addressManage/AddressManage?callKey=calltOrderBtn` })
}
</script>

<template>
  <view class="address-container f12">
    <view v-if="showAddress" class="fcenter">
      <q-icon color="#FA3534" name="icon-dizhi2" size="50rpx" style="margin-right: 10rpx" />
      <view class="fdc">
        <view class="flex-space-between address-container__top">
          <text class="address-container__name">{{ address.name }}&nbsp;&nbsp;</text>
          <text class="address-container__name" style="font-weight: 500">{{ address.mobile }}</text>
        </view>
        <view class="address-container__info flex-space-between" style="margin: 15rpx 0; font-size: 24rpx" @click="receiver(info.no)">
          <text>{{ `${address.area?.join('')} ${address?.address?.replace(/\s/g, '')}` }}</text>
          <u-icon
            v-if="info.status === 'UNPAID' && !['VIRTUAL', 'SHOP_STORE'].includes($props.info.extra.distributionMode)"
            name="arrow-right"
            color=""
            size="25"
          ></u-icon>
        </view>
      </view>
    </view>
    <view :style="{ paddingTop: showAddress ? '15rpx' : '0' }" class="address-container__fn flex-space-between f13">
      <text>配送方式</text>
      <text>{{ addressType && deliveryType[addressType] }}</text>
    </view>
  </view>
</template>

<style lang="scss" scoped>
@include b(address-container) {
  background: #fff;
  margin: 15rpx 10rpx;
  padding: 20rpx;
  border-radius: 15rpx;
  .fcenter {
    display: flex;
    align-items: center;
    border-bottom: 1px dashed #bdbdbd;
  }
  .fdc {
    flex-direction: column;
  }
  @include e(top) {
    justify-content: flex-start;
  }
  @include e(info) {
  }
  @include e(name) {
    color: #222;
    font-size: 34rpx;
    font-weight: 700;
  }
  @include e(fn) {
    font-size: 24rpx;
    color: #999;
  }
}
</style>
