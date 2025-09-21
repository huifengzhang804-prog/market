<script setup lang="ts">
import { PropType } from 'vue'
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
  <span>{{ $props.address?.join('') }}</span>
</template>

<style scoped lang="scss"></style>
