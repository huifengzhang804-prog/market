<template>
  <div class="price">
    <span class="price__decimal">￥</span>
    <template v-if="computedPrice.moreThanTenThousand">
      <span class="print__point" style="white-space: nowrap">{{ computedPrice.tenThousandPrice }}万</span>
    </template>
    <template v-else>
      <span class="price__integer">{{ computedPrice.integer }}</span>
      <span v-if="computedPrice.decimal" class="price__point">.</span>
      <span v-if="computedPrice.decimal" class="price__decimal">{{ computedPrice.decimal }}</span>
    </template>
  </div>
</template>

<script lang="ts" setup>
import { computed } from 'vue'
import Decimal from 'decimal.js'
import useConvert from '@/composables/useConvert'

const $props = defineProps({
  price: {
    type: String,
    default: '',
  },
  memberInfos: {
    type: Object,
    default: () => ({}),
  },
})
const { divTenThousand } = useConvert()
const computedPrice = computed(() => {
  let moreThanTenThousand = false,
    tenThousandPrice = ''
  let actualPrice = $props.price
  if ($props.memberInfos?.relevancyRights?.extendValue) {
    actualPrice = new Decimal(actualPrice).mul($props.memberInfos.relevancyRights.extendValue).div(1000).toString()
  }
  let priceArray: string[] = []
  if (actualPrice.includes('.')) {
    priceArray = actualPrice?.replace(/\.?0+$/, '').split('.')
  } else {
    priceArray = actualPrice?.split('.')
  }
  const integer = priceArray?.[0] || ''
  const decimal = (priceArray?.[1] || '').slice(0, 2)
  if (Number(integer) > 10000) {
    moreThanTenThousand = true
    tenThousandPrice = divTenThousand(actualPrice)
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
  font-weight: bold;
  @include e(decimal) {
    font-size: 12px;
  }
  @include e(point) {
    font-size: 14px;
  }
  @include e(integer) {
    font-size: 16px;
  }
}
</style>
