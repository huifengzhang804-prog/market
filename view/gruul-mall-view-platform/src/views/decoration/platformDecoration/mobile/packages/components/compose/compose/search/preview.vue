<script setup lang="ts">
import defaultSearchData from './search'
import type { PropType } from 'vue'
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
            height: `${$props.formData.height}px`,
            lineHeight: `${$props.formData.height}px`,
        }"
    >
        <i class="search__iconfont el-icon-search"></i>
        <span>{{ $props.formData.keyWord }}</span>
        <div
            :style="{
                lineHeight: `${$props.formData.height - 3}px`,
                'border-radius': `${$props.formData.btnBorderRadius}px`,
                fontSize: `${($props.formData.height * 0.7) / 2}px`,
                backgroundColor: $props.formData.btnBorderColor,
                color: $props.formData.btnFontColor,
            }"
            class="search__text"
        >
            搜&nbsp;索
        </div>
    </div>
</template>

<style lang="scss" scoped>
.search {
    position: relative;
    border: 1px solid transparent;
    &__text {
        position: absolute;
        right: 2px;
        top: 2px;
        bottom: 2px;
        line-height: 68px;
        background-color: red;
        color: #fff;
        padding: 0 24px;
        font-size: 25px;
    }
}
</style>
