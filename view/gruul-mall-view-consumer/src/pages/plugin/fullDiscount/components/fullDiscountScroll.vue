<script setup lang="ts">
import type { PropType } from 'vue'

defineProps({
  fullDiscountList: {
    type: Array as PropType<string[]>,
    default: () => [],
  },
})
</script>

<template>
  <scroll-view scroll-x enhanced :show-scrollbar="false" style="white-space: nowrap">
    <u-tag
      v-for="(item, index) in fullDiscountList"
      :key="index"
      class="full-discount-tag"
      :text="item"
      type="success"
      bg-color="#fff3f4"
      color="#e31436"
      border-color="#fff3f4"
    />
  </scroll-view>
</template>

<style scoped lang="scss">
.full-discount-tag {
  margin-right: 20rpx;
  &:last-child {
    margin-right: 0;
  }
}
</style>
