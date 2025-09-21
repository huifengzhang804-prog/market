<script setup lang="ts">
import { ref, type PropType } from 'vue'
import type { VideoFormData } from '../types'
import { cropImg } from '@/utils'

const $props = defineProps({
  decorationProperties: {
    type: Object as PropType<VideoFormData>,
    default() {
      return {}
    },
  },
})
const VideoCom = ref('')
const height = ref('')
const link = ref({})
const poster = ref('')
const radio = ref(1)
const radioBL = ref(1)
const radioTC = ref(1)
const video = ref('')
const videoLink = ref('')
const width = ref('')
const videowidth = ref(750)
const videoheight = ref(0)

getProperties()
getvideo()

function getProperties() {
  const decorationProperties = $props.decorationProperties
  VideoCom.value = decorationProperties.VideoCom
  height.value = decorationProperties.height
  link.value = decorationProperties.link
  poster.value = decorationProperties.poster
  radio.value = decorationProperties.radio
  radioBL.value = decorationProperties.radioBL
  radioTC.value = decorationProperties.radioTC
  video.value = decorationProperties.video
  videoLink.value = decorationProperties.videoLink
  width.value = decorationProperties.width
}
function getvideo() {
  if (radioTC.value === 2) {
    videowidth.value = 710
  }
  if (radioBL.value === 1) {
    videoheight.value = (videowidth.value / 16) * 9
  }
  if (radioBL.value === 2) {
    videoheight.value = videowidth.value
  }
  if (radioBL.value === 3) {
    const url = radio.value === 1 ? video.value : videoLink.value
    // const getVideoInfo = this.getVideoInfo;
    uni.downloadFile({
      //需要先下载文件获取临时文件路径 单个文件大小不得超过50M
      url: url,
      success: (res1: any) => {
        getVideoInfo(res1.tempFilePath)
      },
    })
  }
}
function getVideoInfo(url: string) {
  uni.getVideoInfo({
    src: url,
    success: (res) => {
      console.log(res)
      const bit = res.height / res.width
      videoheight.value = videowidth.value * bit
    },
    fail(err) {
      console.log(err)
    },
  })
}
// #ifdef APP-PLUS
const handleShowVideo = () => {
  const videoContext = uni.createVideoContext('video_play')
  videoContext.requestFullScreen()
}
// #endif
</script>

<template>
  <!-- #ifndef APP-PLUS -->
  <view class="video--com" :style="'padding:' + (radioTC === 2 ? 20 : 0) + 'rpx'">
    <video
      :src="radio === 1 ? video : videoLink"
      :style="'width:' + videowidth + 'rpx;height:' + videoheight + 'rpx'"
      :poster="cropImg(poster, videowidth, videoheight)"
    ></video>
  </view>
  <!-- #endif -->
  <!-- #ifdef APP-PLUS -->
  <view class="video--com" :style="'padding:' + (radioTC === 2 ? 20 : 0) + 'rpx'">
    <image :src="cropImg(poster, videowidth, videoheight)" :style="'width:' + videowidth + 'rpx;height:' + videoheight + 'rpx'"></image>
    <view
      class="video__mask"
      :style="{
        width: videowidth + 'rpx',
        height: videoheight + 'rpx',
        left: (radioTC === 2 ? 20 : 0) + 'rpx',
        top: (radioTC === 2 ? 20 : 0) + 'rpx',
      }"
      @click="handleShowVideo"
    >
      <q-icon name="icon-bofang" size="90rpx" color="#f0f0f0"></q-icon>
    </view>
    <video
      v-show="false"
      id="video_play"
      :src="radio === 1 ? video : videoLink"
      x5-video-player-type="h5-page"
      style="width: 100vw; height: 100vh"
    ></video>
  </view>
  <!-- #endif -->
</template>

<style lang="scss" scoped>
.video--com {
  width: 100%;
  background-color: #fff;
  /* #ifdef APP-PLUS */
  position: relative;
  /* #endif */
}
/* #ifdef APP-PLUS */
@include b(video) {
  @include e(mask) {
    position: absolute;
    background: rgba(0, 0, 0, 0.3);
    @include flex;
  }
}
/* #endif */
</style>
