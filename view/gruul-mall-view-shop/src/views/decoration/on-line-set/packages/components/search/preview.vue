<script setup lang="ts">
import type { PropType } from 'vue'
import defaultSearchData from './search'
const $props = defineProps({
    formData: {
        type: Object as PropType<typeof defaultSearchData>,
        default: defaultSearchData,
    },
})
</script>

<template>
    <div
        class="search"
        :style="{
            color: $props.formData.color,
            background: $props.formData.background,
            'border-color': $props.formData.borderColor,
            'border-radius': `${$props.formData.borderRadius}px`,
        }"
    >
        <i class="search__iconfont el-icon-search"></i>
        <span>{{ $props.formData.keyWord }}</span>
    </div>
</template>

<style lang="scss" scoped>
.search {
    border: 1px solid transparent;
}
</style>
