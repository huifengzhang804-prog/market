<template>
  <view class="skeleton-wrapper">
    <waterfall-skeleton v-if="type === 'waterfall'" :count="count" :columns="columns" />
    <goods-list-skeleton v-else-if="type === 'goods-list'" :count="count" />
  </view>
</template>

<script setup lang="ts">
import WaterfallSkeleton from './waterfall-skeleton.vue'
import GoodsListSkeleton from './goods-list-skeleton.vue'

interface Props {
  type?: 'waterfall' | 'list' | 'goods-list' | 'card' | 'grid' | 'custom'
  count?: number
  columns?: number
  loading?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  type: 'list',
  count: 6,
  columns: 2,
  loading: true,
})
</script>

<style lang="scss" scoped>
.skeleton-wrapper {
  width: 100%;
}
</style>
