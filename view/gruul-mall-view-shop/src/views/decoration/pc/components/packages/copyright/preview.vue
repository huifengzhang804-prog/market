<script setup lang="ts">
import copyright from './copyright'
import type { PropType } from 'vue'

defineProps({
    formData: {
        type: Object as PropType<typeof copyright>,
        default: copyright,
    },
})
</script>

<template>
    <div class="copyright">
        <div v-for="(item, index) in formData" :key="index" class="copyright__item cp">{{ item.title }}</div>
    </div>
</template>

<style lang="scss" scoped>
@include b(copyright) {
    display: flex;
    padding: 0 120px;
    align-items: center;
    justify-content: center;
    height: 40px;
    background: #666;
    color: #fff;
    font-size: 12px;
    @include e(item) {
        margin-left: 20px;
        @include utils-ellipsis;
    }
}
</style>
