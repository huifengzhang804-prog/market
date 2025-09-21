<script setup lang="ts">
import type { PropType } from 'vue'
import { AddressFn } from '@/components/q-address'
import { useGDRegionDataStore } from '@/store/modules/GDRegionData'

const regionData = useGDRegionDataStore().getterGDRegionData

const $props = defineProps({
    address: {
        type: Array as PropType<string[]>,
        default: () => [],
    },
})
</script>

<template>
    <span>{{ AddressFn(regionData, $props.address) }}</span>
</template>

<style scoped lang="scss"></style>
