<script setup lang="ts">
import type { PropType } from 'vue'
import defaultSeparatorData from './separator'

const $props = defineProps({
    formData: {
        type: Object as PropType<typeof defaultSeparatorData>,
        default() {
            return defaultSeparatorData
        },
    },
})
const inlineBox = computed(() => {
    const { hasMargin } = $props.formData
    return `box-sizing: border-box; padding: 10px ${hasMargin ? 15 : 0}px`
})
const inlineStyle = computed(() => {
    const { borderColor, borderStyle } = $props.formData
    return `border-bottom:1px ${borderStyle} ${borderColor};`
})
</script>

<template>
    <div :style="inlineBox">
        <div :style="inlineStyle"></div>
    </div>
</template>

<style lang="scss" scoped></style>
