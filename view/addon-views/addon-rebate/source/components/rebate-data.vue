<script setup lang="ts">
import { defineProps } from 'vue'
import useConvert from '@/composables/useConvert'

const { divTenThousand } = useConvert()
const $props = withDefaults(defineProps<{ rebateDetailStatistic: { totalExpired: string; totalPendingSettlement: string; totalRebate: string } }>(), {
  rebateDetailStatistic: () => ({
    totalExpired: '',
    totalPendingSettlement: '',
    totalRebate: '',
  }),
})
</script>

<template>
  <div class="rebate-data">
    <div class="rebate-data__left">
      <div class="rebate-data__item">
        <p>累计总返利：</p>
        <span class="rebate-data__item--price">{{ divTenThousand($props.rebateDetailStatistic.totalRebate) }}</span>
      </div>
      <div class="rebate-data__item">
        <p>待结算总返利：</p>
        <span class="rebate-data__item--price" style="color: red; font-weight: bold">{{
          divTenThousand($props.rebateDetailStatistic.totalPendingSettlement)
        }}</span>
      </div>
      <div class="rebate-data__item">
        <p>已失效总返利：</p>
        <span class="rebate-data__item--price" style="color: #999; font-weight: bold">{{
          divTenThousand($props.rebateDetailStatistic.totalExpired)
        }}</span>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
@include b(rebate-data) {
  padding: 16px 0;
  @include flex();
  justify-content: space-between;
  @include e(left) {
    @include flex();
  }
  @include e(item) {
    color: #333;
    margin-right: 50px;
    @include flex();
    @include m(price) {
      font-size: 16px;
      font-weight: bold;
      &::before {
        content: '￥';
        font-size: 12px;
      }
    }
  }
}
</style>
