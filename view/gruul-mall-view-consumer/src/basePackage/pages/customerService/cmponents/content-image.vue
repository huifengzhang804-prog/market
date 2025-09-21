<script setup lang="ts">
import PreviewImage from '@/components/preview-image/preview-image.vue'

const $props = defineProps({
  msg: {
    type: String,
    default: '',
  },
  isMine: { type: Boolean, required: true },
})
</script>

<template>
  <view class="message-content" :class="$props.isMine ? '' : 'consumer'">
    <PreviewImage :images="[$props.msg]" width="340"></PreviewImage>
  </view>
</template>

<style scoped lang="scss">
@include b(message-content) {
  margin: 0 20rpx;
  width: 50vw;
}
@include b(consumer) {
}
</style>
