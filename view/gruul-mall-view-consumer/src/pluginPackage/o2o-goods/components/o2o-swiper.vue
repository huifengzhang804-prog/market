<template>
  <view style="overflow-y: scroll; position: relative">
    <u-swiper
      id="swiper"
      type="image"
      :list="swiperList"
      mode="number"
      height="750"
      border-radius="0"
      :autoplay="false"
      :current="currenuIdx"
      @change="currenuIdx = $event"
    />
    <sku-image-scroll :sku="skus" :info="info" @sku-image-change="handleSkusChange" />
    <view v-if="info.videoUrl && currenuIdx === swiperList.length - 1" class="video-trigger" @click="showVideo">
      <img
        src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/s2b2c_2.0.8/2024/12/9675652dee4b0dd23f6b86f34.png"
        alt=""
        style="width: 86px; height: 86px"
      />
    </view>
    <view v-show="showVideoControl" class="video-container">
      <video id="myVideo" class="video-container__video" :src="videoSrc" :autoplay="true" controls @ended="videoEnd" />
      <view class="video-container__close" @click="videoEnd">
        <QIcon name="icon-icon-close1" size="50rpx" />
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, type PropType, nextTick } from 'vue'
import type { ProductResponse, StorageSku } from '@/pluginPackage/goods/commodityInfo/types'
import SkuImageScroll from '@/pluginPackage/goods/commodityInfo/components/skuImageScroll/skuImageScroll.vue'
import QIcon from '@/components/q-icon/q-icon.vue'

const props = defineProps({
  swiperList: {
    type: Array as PropType<string[]>,
    default: () => [],
  },
  info: {
    type: Object as PropType<ProductResponse>,
    default() {
      return {}
    },
  },
  skus: {
    type: Array as PropType<StorageSku[]>,
    default: () => [],
  },
})
const emit = defineEmits(['choosedSkuChange'])
const currenuIdx = ref(0)
const videoSrc = ref('')
const showVideoControl = ref(false)
const handleSkusChange = (sku: StorageSku) => {
  if (props.skus.length > 1) currenuIdx.value = 0
  emit('choosedSkuChange', sku)
}

const showVideo = () => {
  showVideoControl.value = true
  videoSrc.value = props.info.videoUrl as string
  nextTick(() => {
    const videoContext = uni.createVideoContext('myVideo')
    videoContext.play()
    videoContext.requestFullScreen({ direction: 0 })
  })
}
const videoEnd = () => {
  const videoContext = uni.createVideoContext('myVideo')
  videoContext.exitFullScreen()
  videoSrc.value = ''
  showVideoControl.value = false
}
</script>

<style lang="scss" scoped>
@include b(video-trigger) {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translateX(-50%) translateY(-50%);
  @include flex(center);
}
@include b(video-container) {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 999;
  @include e(close) {
    position: absolute;
    top: 200rpx;
    right: 60rpx;
    color: #fff;
  }
  @include e(video) {
    height: 100%;
    width: 100%;
  }
}
</style>
