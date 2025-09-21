<script setup lang="ts">
import type { PropType } from 'vue'

const $props = defineProps({
    prop: {
        type: String,
        default: '',
    },
    align: {
        type: String,
        default: 'left',
    },
    hasSlots: {
        type: Boolean,
        default: false,
    },
    row: {
        type: Object as PropType<any>,
        default() {
            return {}
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
        <slot v-if="$props.hasSlots" :row="$props.row"></slot>
        <div v-else>{{ $props.row[$props.prop] }}</div>
    </div>
</template>

<style lang="scss" scoped></style>
