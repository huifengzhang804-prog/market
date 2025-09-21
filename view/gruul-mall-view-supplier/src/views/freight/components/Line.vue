<script setup lang="ts">
const $props = defineProps({
    color: {
        type: String,
        default: '#08CC00',
    },
    name: {
        type: String,
        default: '下单设置',
    },
})
</script>

<template>
    <div class="line">
        <div class="line__column" :style="{ background: $props.color }"></div>
        <div>{{ $props.name }}</div>
    </div>
</template>

<style lang="scss" scoped>
@import '@/assets/css/mixins/mixins.scss';
@include b(line) {
    height: 38px;
    background: #f6f8fa;
    color: #515151;
    font-size: 13px;
    margin: 10px 0;
    @include flex(flex-start);
    @include e(column) {
        width: 4px;
        height: 16px;
        margin: 0 20px;
    }
}
</style>
