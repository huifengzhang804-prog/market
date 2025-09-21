<script setup lang="ts">
import { ref } from 'vue'
import NvueIcon from '@/pluginPackage/components/nvueIcon/nvue-icon.vue'
import FooterSkinCare from './footer-skin-care.vue'

const cameraIconName = '\ue68b'
const emit = defineEmits(['camera', 'skin-care', 'start'])
const skinCareShow = ref(false)

const handleClick = () => {
  skinCareShow.value = true
  emit('skin-care')
}
const skinCareClose = () => {
  skinCareShow.value = false
}
</script>

<template>
  <view class="footer" :style="{ opacity: Number(!skinCareShow) }">
    <view class="footer__left" @click="handleClick">
      <nvue-icon font-size="30px" active />
    </view>
    <text class="footer__center" @click="emit('start')"> 立即开播 </text>
    <view class="footer__right" @click="emit('camera')">
      <nvue-icon font-size="30px" :icon-name="cameraIconName" active />
    </view>
  </view>
  <!-- 美颜组件 s-->
  <footer-skin-care v-if="skinCareShow" @close="skinCareClose"></footer-skin-care>
  <!-- 美颜组件 e-->
</template>

<style scoped>
.footer {
  height: 100rpx;
  background: #404040;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
  padding: 0 80rpx;
  z-index: 0;
  transition: opacity 0.4s;
}
.footer__center {
  color: #fff;
  border: 1px solid #fff;
  border-radius: 59rpx;
  padding: 15rpx 49rpx;
}
.footer__center:active {
  background: #fa3534;
}
.target {
  transform: translateY(100px);
  transition: transform 1s;
}
.origin {
  transform: translateY(0px);
  transition-property: transform;
  transition-duration: 1s;
}
</style>
