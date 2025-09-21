<script setup lang="ts">
import type { PropType } from 'vue'

const $props = defineProps({
    prop: {
        type: String,
        default: '',
    },
    more: { type: Boolean, default: false },
    align: {
        type: String,
        default: 'center',
    },
    hasSlots: {
        type: Boolean,
        default: false,
    },
    columnKey: { type: String, default: '' },
    index: { type: Number, default: 0 },
    row: {
        type: Object as PropType<any>,
        default() {
            return {}
        },
    },
})

const isColumnKey = computed(() => $props.columnKey && $props.row[$props.columnKey] && Array.isArray($props.row[$props.columnKey]))
const row = computed(() => (isColumnKey.value ? $props.row[$props.columnKey] : $props.row))
onMounted(() => {
    nextTick(() => {
        dynamicH()
    })
})
onUpdated(() => {
    dynamicH()
})

/**
 * 动态设置高度
 */
function dynamicH() {
    if (!isColumnKey.value) return
    for (let index = 0; index < row.value.length; index++) {
        const els = document.querySelectorAll(`.content${$props.index}-${index}`)
        if (!els) continue
        let maxH = 0
        for (let j = 0; j < els.length; j++) {
            const currrent = els[j].clientHeight
            maxH = Math.max(maxH, currrent)
        }
        for (let j = 0; j < els.length; j++) {
            const currrentEl = els[j] as unknown as HTMLElement
            currrentEl.style.cssText = `height:${maxH}px`
        }
    }
}
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
    <template v-if="Array.isArray(row)">
        <div
            v-for="(rowItem, idx) in row"
            :key="idx"
            class="item__content more"
            :class="{ [`content${$props.index}-${idx}`]: true }"
            :style="{ justifyContent: transformAlign($props.align) }"
        >
            <slot v-if="$props.hasSlots" :row="rowItem" :parent="$props.row" />
            <div v-else>{{ rowItem[$props.prop] }}</div>
        </div>
    </template>
    <template v-else>
        <div class="item__content" :style="{ justifyContent: transformAlign($props.align) }">
            <slot v-if="$props.hasSlots" :row="row" :parent="row" />
            <div v-else>{{ row[$props.prop] }}</div>
        </div>
    </template>
</template>

<style lang="scss" scoped>
.item__content:nth-child(n + 2) {
    position: relative;
    padding-top: 10px;
    border-top: 1px solid #d8eaf9;
}
.more {
    padding-bottom: 10px;
}
</style>
