<script setup lang="ts">
import type { PropType } from 'vue'

defineProps({
    content: {
        type: String,
        default: '',
    },
    tooltipLen: { type: Number, default: 12 },
    width: { type: String, default: '160px' },
    effect: { type: String as PropType<'dark' | 'light'>, default: 'dark' },
})

function interceptionString(str: string, tooltipLen: number) {
    if (!str.length) return []
    const len = str.length / tooltipLen
    let HTLM_Str: string[] = []
    for (let index = 0; index < len; index++) {
        HTLM_Str.push(str.substring(index * tooltipLen, (index + 1) * tooltipLen))
    }
    return HTLM_Str
}
</script>

<template>
    <el-tooltip placement="top" :effect="effect">
        <template #content>
            <div v-for="(item, index) in interceptionString(content, tooltipLen)" :key="index">{{ item }}</div>
        </template>
        <div class="name" :style="{ width: width }">{{ content }}</div>
    </el-tooltip>
</template>

<style scoped lang="scss">
@include b(name) {
    text-align: center;
    @include utils-ellipsis;
}
</style>
