<template>
  <view class="header">
    <q-icon :name="currentStatus?.icon" size="51rpx" />
    <view style="font-size: 36rpx; font-weight: 700; margin: 0 20rpx 0 16rpx">{{ currentStatus?.name }}</view>
    <view v-if="order.status === 'UNPAID'">还剩 {{ timeSet.hours }}:{{ timeSet.minutes }}:{{ timeSet.seconds }} </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref, type PropType } from 'vue'
import type { IOrderList } from '@/pluginPackage/integral/api/types'

import { useCountdownTime } from '@/hooks'
import { useCountdown } from '@/hooks/useCountdown/useCountdown'
const props = defineProps({
  order: {
    type: Object as PropType<IOrderList>,
    default: () => ({}),
  },
})
const statusMap = {
  UNPAID: {
    name: '未支付',
    icon: 'icon-a-Frame7',
  },
  PAID: {
    name: '待发货',
    icon: 'icon-a-Frame8',
  },
  ON_DELIVERY: {
    name: '待收货',
    icon: 'icon-a-Frame9',
  },
  ACCOMPLISH: {
    name: '已完成',
    icon: 'icon-a-Frame10',
  },
  SYSTEM_CLOSED: {
    name: '已关闭',
    icon: 'icon-a-Frame11',
  },
}

const currentStatus = computed(() => statusMap[props.order.status as Exclude<typeof props.order.status, ''>])

const isCountdown = ref(true)
const time = useCountdownTime(props.order.createTime, props.order?.timeout?.payTimeout)
const { timeSet } = useCountdown(time, { immediate: true, immediateCallback: true }, () => {
  isCountdown.value = false
})
</script>

<style scoped lang="scss">
.header {
  display: flex;
  height: 134rpx;
  background: #f54319;
  font-size: 32rpx;
  color: #fff;
  line-height: 134rpx;
  padding-left: 31rpx;
}
</style>
