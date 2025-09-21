<script setup lang="ts">
import type { PropType } from 'vue'
import { efficientTimeTypeCn, efficientTimeNumber } from '@pluginPackage/member/views'
import { Decimal } from 'decimal.js-light'
import type { PaidRuleJson } from '@/apis/plugin/member/model'

const $props = defineProps({
  active: {
    type: Boolean,
    default: false,
  },
  paidRule: { type: Object as PropType<PaidRuleJson>, default: () => ({}) },
})
const { divTenThousand } = useConvert()

const strategyPatternHeader = {
  ONE_MONTH: {
    everyDayPrice: (paidRule: PaidRuleJson) => {
      if (!paidRule.price) return 0
      return Number(divTenThousand(paidRule.price).div(efficientTimeNumber[paidRule.effectiveDurationType]).toFixed(2)) || '-'
    },
  },
  THREE_MONTH: {
    everyDayPrice: (paidRule: PaidRuleJson) => {
      if (!paidRule.price) return 0
      return Number(divTenThousand(new Decimal(paidRule.price).div(efficientTimeNumber[paidRule.effectiveDurationType]).toString()).toFixed(2)) || '-'
    },
  },
  TWELVE_MONTH: {
    everyDayPrice: (paidRule: PaidRuleJson) => {
      if (!paidRule.price) return 0
      return Number(divTenThousand(new Decimal(paidRule.price).div(efficientTimeNumber[paidRule.effectiveDurationType]).toString()).toFixed(2)) || '-'
    },
  },
  THREE_YEAR: {
    everyDayPrice: (paidRule: PaidRuleJson) => {
      if (!paidRule.price) return 0
      return Number(divTenThousand(new Decimal(paidRule.price).div(efficientTimeNumber[paidRule.effectiveDurationType]).toString()).toFixed(2)) || '-'
    },
  },
  FIVE_YEAR: {
    everyDayPrice: (paidRule: PaidRuleJson) => {
      if (!paidRule.price) return 0
      return Number(divTenThousand(new Decimal(paidRule.price).div(efficientTimeNumber[paidRule.effectiveDurationType]).toString()).toFixed(2)) || '-'
    },
  },
}
</script>

<template>
  <view class="price-box" :class="$props.active && 'active'">
    <text>{{ efficientTimeTypeCn[paidRule.effectiveDurationType] }}</text>
    <text>{{ paidRule.price && divTenThousand(paidRule.price) }}</text>
    <text>
      <text style="transform: scale(0.8); display: inline-block">
        每天仅需￥{{ strategyPatternHeader[paidRule.effectiveDurationType].everyDayPrice(paidRule) }}
      </text>
    </text>
    <text v-show="$props.active" class="mask">当前</text>
  </view>
</template>

<style scoped lang="scss">
@include b(price-box) {
  position: relative;
  display: inline-flex;
  margin: 30rpx 15rpx;
  flex-direction: column;
  justify-content: space-evenly;
  width: 240rpx;
  height: 232rpx;
  border-radius: 30rpx;
  background-color: #fff;
  color: #101010;
  font-size: 28rpx;
  text-align: center;
  border: 1px solid #e8e8e8;
  & > text:nth-child(1) {
    color: #333333;
    font-size: 24rpx;
  }
  & > text:nth-child(2) {
    color: #333333;
    font-size: 42rpx;
    &::before {
      content: '￥';
      color: #333;
      font-size: 24rpx;
    }
  }
  & > text:nth-child(3) {
    font-size: 20rpx;
    background: #f4f4f4;
    color: #bababa;
    border-radius: 20rpx;
    margin: 0 25rpx;
  }
}
@include b(active) {
  background-color: #fdf6f6;
  border-color: #ff564b;
  & > text:nth-child(1) {
    color: #a77f31;
  }
  & > text:nth-child(2) {
    color: #ff564b;
    &::before {
      color: #ff564b;
    }
  }
  & > text:nth-child(3) {
    background: #ffdada;
    color: #ff564b;
  }
}
@include b(mask) {
  position: absolute;
  left: -2rpx;
  top: -10rpx;
  width: 120rpx;
  color: #fff;
  font-size: 24rpx;
  background: #ff564b;
  border-radius: 20rpx 0 20rpx 0;
}
</style>
