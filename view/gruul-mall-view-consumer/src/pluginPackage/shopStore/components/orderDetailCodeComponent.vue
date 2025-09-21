<script setup lang="ts">
import { ref, type PropType, watch, reactive, computed } from 'vue'
import { doGetOrderGetCodeByStoreId } from '@pluginPackage/shopStore/apis'
import { DistributionMode } from '@/pluginPackage/goods/commodityInfo/types'
import QIcon from '@/components/q-icon/q-icon.vue'
import SelfPickup from '@/pluginPackage/order/orderDetail/components/self-pickup.vue'
import { type OrderStatusPlus, getOrderDetailStatusPlus } from '@/hooks'
import type { ApiOrder } from '@/pluginPackage/order/orderList/types'
interface StoreInfo {
  code: string
  getPickUpTime: string
  shopStore: { storeName: string; functionaryPhone: string; detailedAddress: string }
}
interface WriteOffProps {
  code?: string
  personName: string
  personPhone: string
  pickupTime?: string
  orderStatus: OrderStatusPlus
}

const orderStatus = computed(() => getOrderDetailStatusPlus($props.info))

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

const $props = defineProps({
  info: {
    type: Object as PropType<ApiOrder>,
    required: true,
  },
})

const writeOffInfo = reactive<WriteOffProps>({
  code: '',
  personName: address.value.name,
  personPhone: address.value.mobile,
  pickupTime: $props.info.extra?.packUpTime,
  orderStatus: orderStatus.value,
})
const storeInfo = ref<StoreInfo>({
  code: '',
  getPickUpTime: '',
  shopStore: { storeName: '', functionaryPhone: '', detailedAddress: '' },
})

watch(
  () => $props.info.extra,
  (val) => {
    if (val && val.shopStoreId) {
      initOrderGetCodeByStoreId()
    }
  },
  {
    immediate: true,
  },
)

async function initOrderGetCodeByStoreId() {
  if ($props.info.extra?.shopStoreId) {
    const { code, data } = await doGetOrderGetCodeByStoreId($props.info.extra.shopStoreId, $props.info.no)
    if (code !== 200) {
      uni.showToast({ title: '获取门店核销码失败', icon: 'none' })
      return
    }
    storeInfo.value = data
    writeOffInfo.code = data.code || ''
  }
}
const copyCode = () => {
  uni.setClipboardData({
    data: storeInfo.value.code,
    // success: function () {
    //     console.log('success');
    // }
  })
}
</script>

<template>
  <view class="storeinfo">
    <view class="storeinfo__title">自提点信息</view>
    <view class="storeinfo__location">
      <q-icon name="icon-icon-address" size="48rpx" color="#FA3534" />
      <view class="storeinfo__location--info">
        <text class="store-name">{{ storeInfo?.shopStore.storeName || info.shopOrders[0].shopName }}</text>
        <text>{{ storeInfo?.shopStore.functionaryPhone || info?.extra.contractNumber }}</text>
      </view>
    </view>
    <view class="storeinfo__address">{{ storeInfo?.shopStore.detailedAddress || info?.extra.address }}</view>
    <view class="storeinfo__distribution">
      <text>配送方式</text>
      <text>{{ $props.info.extra?.distributionMode && DistributionMode[$props.info.extra.distributionMode] }}</text>
    </view>
  </view>
  <self-pickup :info="writeOffInfo" />
</template>

<style scoped lang="scss">
@include b(storeinfo) {
  background-color: #fff;
  margin: 20rpx auto 0;
  width: 710rpx;
  border-radius: 20rpx;
  box-sizing: border-box;
  padding: 20rpx;
  @include e(title) {
    font-size: 28rpx;
    line-height: 39rpx;
    color: #333;
    padding: 0 10rpx;
  }
  @include e(location) {
    @include flex(flex-start);
    line-height: 48rpx;
    padding: 0 10rpx;
    margin-top: 20rpx;
    @include m(info) {
      margin-left: 10rpx;
      color: #333;
      font-size: 34rpx;
      @include b(store-name) {
        font-weight: 500;
        margin-right: 20rpx;
      }
    }
  }
  .storeinfo__address {
    margin-top: 20rpx;
    color: #666;
    font-size: 28rpx;
    line-height: 37rpx;
    padding: 0 10rpx;
    padding-bottom: 22rpx;
    border-bottom: 1rpx dashed #bdbdbd;
  }
  .storeinfo__distribution {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 20rpx 10rpx 0;
    font-size: 26rpx;
    line-height: 36rpx;
    color: #999;
  }
}
@include b(WriteOffCode) {
  padding: 40rpx 30rpx 0;
  background: #fff;
  @include e(title) {
    font-size: 28rpx;
    font-weight: 700;
    text-align: center;
    margin-bottom: 24rpx;
  }
  @include e(code) {
    @include flex;
    margin: 20px auto;
  }
  @include e(word) {
    width: 690rpx;
    height: 92rpx;
    line-height: 92rpx;
    text-align: center;
    color: #515151;
    border-radius: 4rpx;
    border: 1px solid #f2f2f2;
  }
}
</style>
