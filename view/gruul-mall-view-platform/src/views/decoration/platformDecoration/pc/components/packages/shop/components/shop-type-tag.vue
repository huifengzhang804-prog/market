<script setup lang="ts">
import { shopTypeMap } from '@/components/q-select-shop/type'
import type { ShopType } from '@/components/q-select-shop/type'

defineProps<{
    shopType: ShopType
}>()
</script>

<template>
    <div class="tag">{{ shopTypeMap[shopType] }}</div>
</template>

<style lang="scss" scoped>
@include b(tag) {
    width: 32px;
    height: 19px;
    padding: 1px 4px;
    margin-right: 8px;
    border-radius: 2px;
    background-color: #f543190d;
    color: #f54319;
}
</style>
