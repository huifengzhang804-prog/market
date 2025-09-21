<template>
  <view v-if="data.text !== ZB_MSAGE" class="message-remind">
    <text class="message-remind_text">{{ data.text }}</text>
  </view>
</template>

<script lang="ts" setup>
import { ZB_MSAGE } from '../../utils/untis'
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
.message-remind {
  background: rgba(0, 0, 0, 0.2);
  padding: 19rpx 30rpx;
  display: flex;
  border-radius: 20rpx;
  margin-bottom: 20rpx;
}
.message-remind_text {
  color: #4dd4ff;
  font-size: 30rpx;
}
</style>
