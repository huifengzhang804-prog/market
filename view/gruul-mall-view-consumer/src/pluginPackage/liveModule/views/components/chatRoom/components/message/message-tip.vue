<template>
  <view class="message-tip">
    <text class="message-tip_text">{{ data.text }}</text>
  </view>
</template>

<script lang="ts" setup>
defineProps({
  data: {
    type: Object,
    default: () => {
      return { text: '' }
    },
  },
})
</script>
<style scoped>
.message-tip {
  background: rgba(0, 0, 0, 0.2);
  padding: 19rpx 30rpx;
  display: flex;
  border-radius: 20rpx;
  margin-bottom: 20rpx;
  font-size: 24rpx;
  color: #4dd4ff;
}
.message-tip_text {
  color: #4dd4ff;
  font-size: 30rpx;
}
</style>
