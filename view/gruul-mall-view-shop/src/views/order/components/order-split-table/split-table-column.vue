<script setup lang="ts">
import type { PropType } from 'vue'
import type { ShopOrderItem } from '@/apis/order/types'

const $props = defineProps({
    prop: {
        type: String,
        default: '',
    },
    align: {
        type: String,
        default: 'left',
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
        type: Array as PropType<Array<ShopOrderItem>>,
        default: () => [],
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
        <slot :row="$props.row" :shopOrderItems="$props.shopOrderItems"></slot>
    </div>
</template>

<style lang="scss" scoped></style>
