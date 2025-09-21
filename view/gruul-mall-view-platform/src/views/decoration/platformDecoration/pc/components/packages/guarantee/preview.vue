<script setup lang="ts">
import guarantee from './guarantee'
import type { PropType } from 'vue'

defineProps({
    formData: {
        type: Object as PropType<typeof guarantee>,
        default: guarantee,
    },
})
</script>

<template>
    <div class="guarantee" :style="{ 'background-image': `url(${formData.img})` }"></div>
</template>

<style lang="scss" scoped>
@include b(guarantee) {
    height: 96px;
    background-size: 100% 100%;
}
</style>
