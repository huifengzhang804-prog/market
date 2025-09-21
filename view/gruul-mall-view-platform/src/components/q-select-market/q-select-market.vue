<script setup lang="ts">
import type { PropType } from 'vue'
import { useVModel } from '@vueuse/core'

const props = defineProps<{
    link: any
}>()

const emit = defineEmits(['update:link'])
const linkSelectItem = useVModel(props, 'link', emit)
const tableData = [
    { id: '领券中心', name: '领券中心' },
    { id: '限时秒杀', name: '限时秒杀' },
    { id: '积分商城', name: '积分商城' },
]
</script>

<template>
    <select-main v-model:select="linkSelectItem" :table-data="tableData" :loading="true" :no-more="true" />
</template>

<style lang="scss" scoped></style>
