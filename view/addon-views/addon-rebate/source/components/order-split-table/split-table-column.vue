<script setup lang="ts">
import { PropType } from 'vue'
import { RebateOrderList } from '../types'

const $props = defineProps({
  prop: {
    type: String,
    default: '',
  },
  align: {
    type: String,
    default: 'center',
  },
  row: {
    type: Object as PropType<any>,
    default() {
      return {}
    },
  },
  packageId: {
    type: String,
    default() {
      return undefined
    },
  },
  shopOrderItems: {
    type: Object as PropType<RebateOrderList['rebateOrderItems']>,
    default() {
      return []
    },
  },
})

const transformAlign = (val: string) => {
  switch (val) {
    case 'left':
      return 'flex-start'
    case 'right':
      return 'flex-end'
    default:
      return 'center'
  }
}
</script>

<template>
  <div class="item__content" :style="{ justifyContent: transformAlign($props.align) }">
    <slot :row="$props.row" :shop-order-items="$props.shopOrderItems"></slot>
  </div>
</template>

<style lang="scss" scoped></style>
