<script setup lang="ts">
import type { PropType } from 'vue'
import PPTag from '@/pluginPackage/goods/commodityInfo/goodsPreferentialPopup/p-p-tag.vue'
import type { ProductPriceItem } from '@/apis/good/model'

const $props = defineProps({
  idx: {
    type: Number,
    default: 0,
  },
  length: {
    type: Number,
    default: 0,
  },
  info: {
    type: Object as PropType<ProductPriceItem>,
    default() {
      return {}
    },
  },
})
</script>

<template>
  <view class="main-item">
    <p-p-tag :price="$props.info.price" :text="$props.info.desc" />
    <u-icon v-if="$props.idx !== $props.length" name="minus" margin="0" color="#F12F22" size="28rpx" style="transform: scale(0.6); padding: 0 5rpx" />
  </view>
</template>

<style scoped lang="scss">
@include b(main-item) {
  display: inline-flex;
  align-items: center;
  flex-shrink: 0;
}
</style>
