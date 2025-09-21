<script setup lang="ts">
import { computed } from 'vue'
import { useStatusBar } from '@/hooks/useStatusBar'

const $props = defineProps({
  decorationProperties: {
    type: Object,
    default: () => ({ height: 0, isExtendStatusBarHeight: false, backgroundColor: 'transparent' }),
  },
})
</script>

<template>
  <view :style="{ height: `${$props.decorationProperties.height * 2}rpx`, backgroundColor: '#fff' }"></view>
</template>

<style lang="scss" scoped></style>
