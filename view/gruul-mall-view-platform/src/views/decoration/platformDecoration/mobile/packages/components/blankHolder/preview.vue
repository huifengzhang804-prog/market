<script setup lang="ts">
const $props = defineProps({
    formData: {
        type: Object,
        default() {
            return {}
        },
    },
})
const statusBarHeight = 50
const inlineStyle = computed(() => {
    return `height:${$props.formData.height + ($props.formData.isExtendStatusBarHeight ? statusBarHeight : 0)}px`
})
</script>

<template>
    <!-- 空白占位 -->
    <div :style="inlineStyle"></div>
</template>

<style lang="scss" scoped></style>
