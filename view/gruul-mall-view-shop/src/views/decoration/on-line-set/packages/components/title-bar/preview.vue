<script setup lang="ts">
import type { PropType } from 'vue'
import defaultTitleBarData from './title-bar'

const $props = defineProps({
    formData: {
        type: Object as PropType<typeof defaultTitleBarData>,
        default: defaultTitleBarData,
    },
})
const inlineStyle = computed(() => {
    const { backgroundColor } = $props.formData
    return {
        'background-color': backgroundColor,
    }
})
</script>

<template>
    <!-- 标题栏 -->
    <div class="titleBar" :style="inlineStyle">
        <div
            v-if="$props.formData.showStyle === 'is-style-one'"
            class="titleBar__item"
            :class="$props.formData.showStyle"
            :style="{ color: $props.formData.color, fontWeight: 600 }"
        >
            {{ $props.formData.titleName }}
        </div>
        <div
            v-if="$props.formData.showStyle === 'is-style-two'"
            class="titleBar__item"
            :class="$props.formData.showStyle"
            :style="{ color: $props.formData.color }"
        >
            <div style="font-weight: 600">{{ $props.formData.titleName }}</div>
            <div>查看更多></div>
        </div>
    </div>
</template>

<style lang="scss" scoped>
@import '@/assets/css/decoration/titleBar.scss';
</style>
