<script lang="ts" setup>
import { type PropType, ref } from 'vue'
import { imgPreview, isVideo } from '@/libs/formatImage'

const previewVideoSrc = ref('')
const $props = defineProps({
  images: { type: Array as PropType<string[]>, default: () => [] },
  width: { type: [String, Number], default: '' },
  height: { type: [String, Number], default: '' },
})
// #ifndef H5
const autoplayVideo = ref(false)

// #endif

function previewVideoClose() {
  // #ifndef H5
  autoplayVideo.value = false
  // #endif
  previewVideoSrc.value = ''
}

function handleImgPreview(src: string, index: number) {
  if (isVideo(src)) {
    previewVideo(src, index)
  } else {
    imgPreview(src, $props.images)
  }
}

function previewVideo(src: string, index: number) {
  previewVideoSrc.value = src
  // #ifndef H5
  autoplayVideo.value = true
  // #endif
  // #ifdef H5
  const videoContext = uni.createVideoContext('assessVideo-' + index)
  videoContext.requestFullScreen()
  videoContext.play()
  // #endif
}

const fullScreen = ref(false)
const handleScreen = (e: any) => {
  fullScreen.value = e.detail.fullScreen
}
</script>

<template>
  <view class="images">
    <view
      v-for="(i, index) in images"
      :key="index"
      :style="{
        width: width ? width + 'rpx' : images.length === 3 ? 'calc(33.3% - 10rpx * 2)' : 'calc(50% - 10rpx * 2)',
      }"
      style="margin: 10rpx; position: relative"
    >
      <!-- #ifdef H5 -->
      <video
        v-if="isVideo(i)"
        :id="'assessVideo-' + index"
        :controls="fullScreen"
        :show-center-play-btn="false"
        :src="i"
        :style="{
          height: ($props.height || ($props.images.length === 3 ? '220' : '330')) + 'rpx',
        }"
        class="assessVideo"
        x5-video-player-type="h5-page"
        @fullscreenchange="handleScreen"
      ></video>
      <!-- #endif -->
      <!-- #ifndef H5 -->
      <view
        v-if="isVideo(i)"
        :style="{
          height: $props.height || ($props.images.length === 3 ? '220' : '330') + 'rpx',
        }"
        class="assessVideo"
      />
      <!-- #endif -->
      <view v-if="isVideo(i)" class="autoplay-btn" @click.stop="previewVideo(i, index)">
        <u-icon color="#ffffff" name="play-right-fill" size="40rpx" />
      </view>
      <u-image v-else :src="i" mode="widthFix" border-radius="14rpx" duration="450" fade width="100%" @click="handleImgPreview(i, index)"> </u-image>
    </view>
  </view>
  <!-- #ifndef H5 -->
  <u-mask :custom-style="{ backgroundColor: '#000', display: 'flex', alignItems: 'center' }" :show="autoplayVideo" @click="previewVideoClose">
    <video v-if="autoplayVideo" :src="previewVideoSrc" class="preview-video" object-fit="contain" @click.stop="() => {}" />
  </u-mask>
  <!-- #endif -->
</template>

<style lang="scss" scoped>
@include b(images) {
  width: 100%;
  @include flex();
  justify-content: space-between;
  flex-wrap: wrap;
}

@include b(preview-video) {
  width: 100vw;
}

@include b(autoplay-btn) {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  border-radius: 50%;
  border: 2px solid #fff;
  // #ifdef H5
  padding: 5rpx 2rpx 5rpx 8rpx;
  // #endif
  // #ifndef H5
  padding: 5rpx 3rpx 5rpx 7rpx;
  // #endif
  @include flex;
  z-index: 666;
}

@include b(assessVideo) {
  width: 100%;
  border-radius: 14rpx;
  background-color: #000;
}
</style>
