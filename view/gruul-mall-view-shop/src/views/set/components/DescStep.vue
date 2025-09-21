<script setup lang="ts">
defineProps({
    steps: {
        type: Array,
        default: () => [],
    },
})
</script>

<template>
    <div class="desc_step_container">
        <div v-for="(item, index) in steps" :key="index" class="step_item">{{ item }}</div>
    </div>
</template>

<style lang="scss" scoped>
.desc_step_container {
    display: flex;
    flex-wrap: wrap;
    .step_item {
        height: 24px;
        line-height: 24px;
        background: rgba(85, 92, 253, 0.1);
        color: rgb(51, 51, 51);
        font-size: 13px;
        font-weight: 400;
        position: relative;
        flex-shrink: 0;
        width: fit-content;
        padding-left: 8px;
        padding-right: 13px;
    }
    .step_item::after {
        position: absolute;
        content: '';
        width: 0;
        height: 0;
        border-width: 12px 0 12px 6px;
        border-style: solid;
        border-color: #f6f7ff #f6f7ff #f6f7ff transparent;
        right: 0px;
    }
    .step_item::before {
        position: absolute;
        content: '';
        width: 0;
        height: 0;
        border-width: 12px 0 12px 6px;
        border-style: solid;
        border-color: transparent transparent transparent #f6f7ff;
        left: 0px;
    }
    .step_item:first-child:before {
        display: none;
    }
}
</style>
