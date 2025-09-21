<script setup lang="ts">
import type { PropType } from 'vue'
import { dealTypeCn } from '@/basePackage/pages/billingDetails/types'
import type { BillingDetailsItem } from '@/basePackage/pages/billingDetails/types'

const { divTenThousand } = useConvert()
const $props = defineProps({
  item: { type: Object as PropType<BillingDetailsItem>, default: () => ({}) },
})

/*
 *function INCREASE REDUCE
 */
</script>

<template>
  <view class="table-detail">
    <view class="table-detail__main">
      <view class="flex">
        <text>{{ dealTypeCn[$props.item.dealType] }}</text> <text style="margin-left: 15rpx">（{{ $props.item.createTime }}）</text>
      </view>
      <view class="table-detail--price">
        {{ $props.item.changeType === 'INCREASE' ? '+' : '-' }}
        {{ divTenThousand($props.item.money)?.toFixed(2) }}
      </view>
    </view>
    <u-line class="u-line" length="93%" margin="0 0 0 26rpx" />
  </view>
</template>

<style scoped lang="scss">
@include b(table-detail) {
  background: #fff;
  @include e(main) {
    @include flex(space-between);
    padding: 50rpx 26rpx;
    background: #fff;
    color: #101010;
    font-size: 26rpx;
  }
  @include m(price) {
    color: #333333;
    font-size: 32rpx;
    font-weight: bold;
  }
}
</style>
