<template>
  <text class="member-label" :style="{ backgroundColor: bgColor, color: fontColor }">
    <slot></slot>
  </text>
</template>

<script lang="ts" setup>
defineProps({
  bgColor: {
    type: String,
    default: '',
  },
  fontColor: {
    type: String,
    default: '',
  },
})
</script>

<script lang="ts">
export default {
  options: { styleIsolation: 'shared' },
}
</script>

<style lang="scss" scoped>
@include b(member-label) {
  font-size: 24rpx;
  line-height: 32rpx;
  padding: 4rpx 12rpx;
  border-radius: 176rpx;
}
</style>
