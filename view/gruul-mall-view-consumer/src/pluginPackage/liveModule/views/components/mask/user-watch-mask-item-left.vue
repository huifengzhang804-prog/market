<script setup lang="ts">
import { ref } from 'vue'
import NvueIcon from '@/pluginPackage/components/nvueIcon/nvue-icon.vue'
import { initStyleMarginTop } from '@/utils/util'

defineProps({
  width: {
    type: Number,
    default: 0,
  },
  height: {
    type: Number,
    default: 0,
  },
  isEnd: {
    type: Boolean,
    default: false,
  },
})
const emit = defineEmits(['close', 'back'])
const icon = '\ue697'
const iconw = '\ue626'
const positionTop = ref(initStyleMarginTop() + uni.upx2px(10))
</script>

<template>
  <view :style="{ width: width + 'px', height: height + 'px' }" :class="['user-item-left', { 'blur-15': isEnd }]">
    <view class="user-item-left-back" @click="emit('back')">
      <nvue-icon :icon-name="iconw" font-size="50rpx"></nvue-icon>
    </view>
  </view>
</template>

<style scoped>
.blur-15 {
  filter: blur(15rpx);
}
.user-item-left-close {
  position: absolute;
  right: 20rpx;
  top: 100rpx;
}
.user-item-left-back {
  position: absolute;
  right: 60rpx;
  bottom: 100rpx;
}
</style>
