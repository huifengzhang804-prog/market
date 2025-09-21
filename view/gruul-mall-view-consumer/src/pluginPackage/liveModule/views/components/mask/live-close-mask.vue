<script setup lang="ts">
import { ref } from 'vue'
import { initStyleMarginTop } from '@/utils/util'

const $props = defineProps({
  msg: {
    type: String,
    default: '直播间涉嫌违规，已关闭！',
  },
})
const emit = defineEmits(['close'])
const positionTop = ref(initStyleMarginTop() + uni.upx2px(10))
</script>

<template>
  <view class="live-close-mask" @click.stop>
    <view :style="positionTop ? { top: `${positionTop}px` } : {}" class="live-close-mask_close" @click.stop="emit('close')">
      <text :style="{ fontFamily: 'iconfont', color: '#fff' }" class="icon live-close__icon">&#xe697;</text>
    </view>
    <text class="live-close-mask-message">{{ $props.msg }}</text>
  </view>
</template>

<style scoped>
.live-close-mask {
  position: fixed;
  top: 0;
  right: 0;
  left: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}
.live-close__icon {
  font-size: 48rpx;
  line-height: 48rpx;
}
.live-close-mask-message {
  font-weight: 700;
  color: #fff;
  font-size: 40rpx;
}
.live-close-mask_close {
  position: absolute;
  right: 35rpx;
  top: 100rpx;
}
</style>
