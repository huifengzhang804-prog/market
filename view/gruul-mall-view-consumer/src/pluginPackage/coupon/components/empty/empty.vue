<script setup lang="ts">
const props = defineProps({
  show: {
    type: Boolean,
    default: false,
  },
  text: {
    type: String,
    default: '暂无优惠券',
  },
  height: {
    type: String,
    default: '550',
  },
})
</script>

<template>
  <view v-if="props.show" style="font-size: 250rpx">
    <u-empty text="暂无优惠券" mode="coupon" font-size="30" :style="{ height: height + 'rpx' }" />
  </view>
</template>

<style scoped lang="scss"></style>
