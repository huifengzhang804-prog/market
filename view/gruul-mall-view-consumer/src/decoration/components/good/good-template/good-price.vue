<template>
  <text class="price">
    <text class="price__decimal">￥</text>
    <template v-if="computedPrice.moreThanTenThousand">
      <text class="print__point">{{ computedPrice.tenThousandPrice }}万</text>
    </template>
    <template v-else>
      <text class="price__integer">{{ computedPrice.integer }}</text>
      <text v-if="computedPrice.decimal" class="price__point">.</text>
      <text v-if="computedPrice.decimal" class="price__decimal">{{ computedPrice.decimal }}</text>
    </template>
  </text>
</template>

<script lang="ts" setup>
import type { PropType } from 'vue'
import { computed } from 'vue'
import type { GoodItemType } from '../good'
import { Decimal } from 'decimal.js-light'

const $props = defineProps({
  price: {
    type: [String, Number] as PropType<Long>,
    default: '',
  },
  memberInfo: {
    type: Object as PropType<GoodItemType['memberInfo']>,
    default: () => {},
  },
})
const { divTenThousand } = useConvert()
const computedPrice = computed(() => {
  let moreThanTenThousand = false,
    tenThousandPrice = ''
  let actualPrice = $props.price
  if ($props.memberInfo?.relevancyRights?.extendValue) {
    actualPrice = new Decimal(actualPrice).mul($props.memberInfo.relevancyRights.extendValue).div(1000).toString()
  }
  let priceArray: string[] = []
  if (String(actualPrice).includes('.')) {
    priceArray = String(actualPrice)
      ?.replace(/\.?0+$/, '')
      .split('.')
  } else {
    priceArray = String(actualPrice)?.split('.')
  }
  const integer = divTenThousand(priceArray?.[0]).toString() || ''
  let decimal = (priceArray?.[1] || '').slice(0, 2)
  if (integer === '0' && decimal === '00') {
    decimal = '01'
  }
  if (Number(integer) > 10000) {
    moreThanTenThousand = true
    tenThousandPrice = divTenThousand(Number(integer))
      .toFixed(2)
      ?.replace(/\.?0+$/, '')
  }
  return {
    integer,
    decimal,
    moreThanTenThousand,
    tenThousandPrice,
  }
})
</script>

<style lang="scss" scoped>
@include b(price) {
  color: #f54319;
  font-weight: 500;
  font-size: 36rpx;
  @include e(decimal) {
    font-size: 28rpx;
  }
}
</style>
