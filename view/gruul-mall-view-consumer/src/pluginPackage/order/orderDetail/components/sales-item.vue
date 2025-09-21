<script setup lang="ts">
import { ref, type PropType, computed } from 'vue'
import OrderGoodsSpec from '@/components/order/order-goods-spec.vue'
import type { ShopOrderItem } from '@/pluginPackage/order/orderList/types'
import type { SetStorageSyncExtra } from '@/pluginPackage/order/orderList/hooks/afsHooks'

const $props = defineProps({
  info: {
    type: Object as PropType<ShopOrderItem>,
    default: () => ({}),
  },
  index: {
    type: Number,
    default: 0,
  },
  extra: {
    type: Object as PropType<SetStorageSyncExtra>,
    default: () => ({}),
  },
})
const status = computed(() => {
  if ($props.info.packageId) {
    return
  }
  return '未发货'
})
</script>

<template>
  <view class="item">
    <view v-if="index" class="item__top">包裹{{ index }}</view>
    <view v-else class="item__top">{{ status }}</view>
    <order-goods-spec
      :order-no="extra.orderNo"
      jump
      is-footer
      :pay-status="extra.payStatus"
      :distribution-mode="extra.distributionMode"
      :info="info"
      is-afs-pages
      :is-show-evaluation-btn="false"
    />
  </view>
</template>

<style scoped lang="scss">
@include b(item) {
  @include e(top) {
    display: flex;
    padding: 24rpx;
    align-items: center;
  }
}
</style>
