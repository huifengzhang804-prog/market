<script setup lang="ts">
import { ref, watch } from 'vue'
// #ifdef H5
import { QrcodeStream } from 'qrcode-reader-vue3'
// #endif
import QIcon from '@/components/q-icon/q-icon.vue'

const $props = defineProps({
  modelValue: {
    type: Boolean,
    default: true,
  },
})
const $emit = defineEmits(['update:modelValue', 'analysisResult'])
const torchType = ref(false)
const scanLoad = ref(false)
const camera = ref<'auto' | 'rear' | 'off'>('rear')
watch($props, (newVal) => {
  if (newVal.modelValue) {
    camera.value = 'rear'
  } else {
    camera.value = 'off'
    scanLoad.value = false
  }
})

/**
 * 解析结果回调
 */
const handleDecode = (result: string) => {
  $emit('analysisResult', result)
}

function handleCloseScan() {
  camera.value = 'off'
  scanLoad.value = false
  $emit('update:modelValue', false)
}
async function initScan(promise: any) {
  try {
    uni.showLoading({
      title: '摄像头准备中',
    })
    await promise
    scanLoad.value = true
  } catch (error: any) {
    handleCloseScan()
    if (error.name === 'NotAllowedError') {
      uni.showToast({
        icon: 'none',
        title: '请打开浏览器摄像头权限',
        duration: 2000,
      })
    } else if (error.name === 'NotFoundError') {
      uni.showToast({
        icon: 'none',
        title: '本地未发现摄像头设备',
        duration: 2000,
      })
    } else if (error.name === 'NotSupportedError') {
      uni.showToast({
        icon: 'none',
        title: '只支持HTTPS访问',
        duration: 2000,
      })
    } else if (error.name === 'NotReadableError') {
      uni.showToast({
        icon: 'none',
        title: '摄像头使用中，请勿重复操作',
        duration: 2000,
      })
    } else if (error.name === 'OverconstrainedError') {
      uni.showToast({
        icon: 'none',
        title: '本地未发现前置摄像头',
        duration: 2000,
      })
    } else if (error.name === 'StreamApiNotSupportedError') {
      uni.showToast({
        icon: 'none',
        title: '浏览器版本过低,请升级后使用',
        duration: 2000,
      })
    }
    console.log(error)
  } finally {
    uni.hideLoading()
  }
}
</script>
<template>
  <view v-if="$props.modelValue" class="scan">
    <QrcodeStream :torch="torchType" :camera="camera" @decode="handleDecode" @init="initScan" />
    <view v-show="scanLoad" class="scan__back" @click="handleCloseScan">
      <q-icon name="icon-xiajiantou" color="#6c6968" size="20px" />
    </view>
    <view v-show="scanLoad" class="scan__tool">
      <!-- 手电筒 -->
      <!-- <view class="scan__tool--item" @click="handleShowTorch">
                <q-icon name="icon-ziyuan170" color="#fff" size="18px" />
            </view> -->
      <!-- 图片解析 -->
      <view class="scan__tool--item">
        <q-icon name="icon-tupian1" color="#fff" size="18px" />
      </view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
@include b(scan) {
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.2);
  position: absolute;
  top: 0;
  left: 0;
  z-index: 999;
  @include e(tool) {
    width: 100%;
    padding: 0 40rpx;
    position: absolute;
    bottom: 140rpx;
    @include flex(flex-end);
    @include m(item) {
      width: 80rpx;
      height: 80rpx;
      border-radius: 50%;
      background: #6c6968;
      @include flex;
    }
  }
  @include e(back) {
    position: absolute;
    top: 60px;
    left: 20px;
    width: 50rpx;
    height: 50rpx;
    border-radius: 50%;
    background: #fff;
    @include flex;
    transform: rotate(90deg);
  }
}
</style>
