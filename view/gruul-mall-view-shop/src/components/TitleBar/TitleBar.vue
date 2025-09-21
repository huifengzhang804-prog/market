<script setup lang="ts">
const $props = defineProps({
    color: {
        type: String,
        default: '#555CFD',
    },
    name: {
        type: String,
        default: '',
    },
})
</script>

<template>
    <div class="line">
        <div class="line__column" :style="{ background: $props.color }"></div>
        <div>{{ $props.name }}</div>
        <div class="right">
            <slot name="right"></slot>
        </div>
    </div>
</template>

<style lang="scss" scoped>
@include b(line) {
    background: #f6f8fa;
    color: #515151;
    font-size: 14px;
    margin: 15px 0;
    padding-right: 13px;
    padding-left: 17px;
    padding-top: 15px;
    padding-bottom: 15px;

    @include flex(flex-start);
    @include e(column) {
        width: 4px;
        height: 16px;
        margin-right: 6px;
    }
    .right {
        margin-left: auto;
    }
}
</style>
