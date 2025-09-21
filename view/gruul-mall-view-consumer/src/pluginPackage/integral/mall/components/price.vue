<template>
  <view class="price" :style="{ fontSize }">
    <text style="color: #ff794d">{{ integral }}积分</text>
    <text> + ￥{{ divTenThousand(salePrice) }}</text>
  </view>
</template>

<script setup lang="ts">
import type { PropType } from 'vue'

defineProps({
  salePrice: {
    type: String as PropType<Long>,
    default: '',
  },
  integral: {
    type: String as PropType<Long>,
    default: '',
  },
  fontSize: {
    type: String,
    default: '28rpx',
  },
})
const { divTenThousand } = useConvert()
</script>

<style scoped>
.price {
  display: inline-block;
  font-size: 28rpx;
  font-weight: 400;
  color: #fa3534;
}
</style>
