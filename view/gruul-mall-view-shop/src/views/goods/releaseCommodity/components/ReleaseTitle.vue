<template>
    <div class="title">
        <div class="title__release">
            {{ $props.title }}
        </div>
        <slot></slot>
    </div>
</template>

<script lang="ts" setup>
const $props = withDefaults(defineProps<{ title: string }>(), {
    title: '',
})
</script>

<style lang="scss" scoped>
@include b(title) {
    @include flex(flex-start, center);
    margin: 50px 0 10px;
    @include e(release) {
        height: 20px;
        color: #333;
        font-size: 16px;
        line-height: 20px;
        border-left: 4px solid #4b80ff;
        padding-left: 8px;
    }
}
</style>
