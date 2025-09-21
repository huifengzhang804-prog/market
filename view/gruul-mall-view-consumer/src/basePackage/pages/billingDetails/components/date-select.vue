<script setup lang="ts">
import { computed, type PropType } from 'vue'
import QIcon from '@/components/q-icon/q-icon.vue'
import { changeTypeCn } from '@/basePackage/pages/billingDetails/types'
import type { UserPaymentHistoryStatisticsList, userPaymentHistoryStatisticsItem } from '@/basePackage/pages/billingDetails/types'

const { divTenThousand } = useConvert()
const $emit = defineEmits(['click'])
const $props = defineProps({
  queryTime: { type: String, default: '' },
  paymentHistoryStatisticsList: {
    type: Array as unknown as PropType<UserPaymentHistoryStatisticsList>,
    default: () => [
      { changeType: 'INCREASE', statisticsMoney: '0' },
      { changeType: 'REDUCE', statisticsMoney: '0' },
    ],
  },
})

const initQueryTime = computed(() => {
  if (!$props.queryTime) return
  return $props.queryTime.replace('-', '年') + '月'
})
const changeTypeOne = computed(() => (item: userPaymentHistoryStatisticsItem) => {
  if (item?.changeType) {
    return changeTypeCn[item.changeType]
  }
  return '收入'
})
</script>

<template>
  <view class="date-select">
    <view class="date-select__month" @click="$emit('click')"
      ><text>{{ initQueryTime }}</text
      ><q-icon name="icon-xiajiantou" color="#000" />
    </view>
    <view class="date-select__detail">
      <text v-for="(item, index) in $props.paymentHistoryStatisticsList" :key="index">
        {{ changeTypeOne(item) }}￥{{ divTenThousand(item.statisticsMoney || '0') }}
      </text>
    </view>
  </view>
</template>

<style scoped lang="scss">
@include b(date-select) {
  @include flex;
  justify-content: space-between;
  padding: 30rpx 46rpx 30rpx 13rpx;
  @include e(detail) {
    color: rgba(153, 153, 153, 1);
    font-size: 24rpx;
    & > text:nth-child(1) {
      margin-right: 5rpx;
    }
  }
  @include e(month) {
    @include flex;
    color: rgba(16, 16, 16, 1);
    font-size: 26rpx;
  }
}
</style>
