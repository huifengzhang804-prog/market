<script lang="ts" setup>
import type { PropType } from 'vue'
import PreviewImage from '@/components/preview-image/preview-image.vue'
import type { RemarkItem } from '@pluginPackage/order/orderList/types'

defineProps({
  remark: { type: Object as PropType<Array<RemarkItem>>, required: true },
})
</script>

<template>
  <view v-for="{ key, value, type } in remark" :key="key" class="remark-view">
    <text class="remark-view--key">{{ key }}：</text>
    <view v-if="type === 'IMAGE'" class="remark-view--image">
      <PreviewImage :images="[value]" height="80" width="100" />
    </view>
    <text v-else class="remark-view--remarkKey"> {{ value }}</text>
  </view>
</template>

<style lang="scss" scoped>
@include b(remark-view) {
  padding: 20rpx 40rpx 0 20rpx;
  align-items: center;
  @include m(key) {
    color: #676666;
  }
  @include m(image) {
    margin-bottom: 20rpx;
  }
  @include m(remarkKey) {
    color: #777777;
  }
}
</style>
