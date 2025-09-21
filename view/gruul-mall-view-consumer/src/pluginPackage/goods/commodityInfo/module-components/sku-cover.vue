<template>
  <view style="position: relative">
    <ProSwiper
      id="swiper"
      :list="allSwiperPic"
      :current="swiperList.currenuIdx"
      imgMode="aspectFit"
      :height="imgHeight"
      :duration="150"
      mode="number"
      @change="handleChangeSwiper"
      @click="swiperClick"
    ></ProSwiper>
    <!-- 售罄遮罩 s -->
    <view
      v-if="$props.isSellOut"
      class="sell-out"
      :style="{
        height: imgHeight + 'rpx',
      }"
    >
      <image class="sell-out__img" :src="selloutImg" alt="" />
    </view>
    <!-- 售罄遮罩 e -->
    <view class="shareBtn" @click="handleShare">
      <u-icon name="share" color="#fff" size="30rpx"></u-icon>
    </view>
    <view class="shareBtn" style="bottom: 144rpx" @click="handleAssess">
      <u-icon v-if="!isCollection" name="heart" color="#fff" size="30rpx"></u-icon>
      <u-icon v-else name="heart-fill" color="#f54319" size="30rpx"></u-icon>
    </view>
    <view v-if="comProvideInject.goodInfo.value?.videoUrl && swiperList.currenuIdx === 0" class="video-trigger" @click="showVideo">
      <img
        src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/s2b2c_2.0.8/2024/12/9675652dee4b0dd23f6b86f34.png"
        style="width: 86px; height: 86px"
        alt=""
      />
    </view>
    <view v-show="showVideoControl" class="video-container">
      <video id="myVideo" class="video-container__video" :src="videoSrc" :autoplay="true" controls @ended="videoEnd" />
      <view class="video-container__close" @click="videoEnd">
        <QIcon name="icon-icon-close1" size="50rpx" />
      </view>
    </view>
  </view>
  <!-- 分享海报 -->
  <CanvasShare
    ref="posterRef"
    :share-pop-up="sharePopUp"
    :share-data="shareData"
    @canvas-close="handleCanvasClose(() => $emit('showMenu'))"
  ></CanvasShare>
</template>

<script lang="ts" setup>
import type { comDispatcherType } from '@/pluginPackage/goods/commodityInfo/types'
import { useUserStore } from '@/store/modules/user'
import CanvasShare from '@/components/canvas-share/canvas-share.vue'
import useGoodShare from '@/pluginPackage/goods/commodityInfo/module-components/hooks/useGoodShare'
import useGoodCollection from './hooks/useGoodCollection'
import QIcon from '@/components/q-icon/q-icon.vue'
import ProSwiper from '@/pluginPackage/goods/commodityInfo/components/ProSwiper.vue'
import { computed, inject, nextTick, ref, watch } from 'vue'

const selloutImg = 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/s2b2c_2.0.8/2024/12/96756530de4b0dd23f6b86f35.png'

const $props = defineProps({
  isSellOut: {
    type: Boolean,
    default: false,
  },
})
const $emit = defineEmits(['showMenu'])
const comProvideInject = inject('comProvide') as comDispatcherType
const swiperList = computed(() => comProvideInject.swiperList)
const { sharePopUp, posterRef, shareData, handleShare, handleCanvasClose } = useGoodShare()
const { isCollection, handleAssess } = useGoodCollection()
function swiperClick() {
  uni.previewImage({
    current: swiperList.value.currenuIdx,
    urls: allSwiperPic.value,
  })
}

const videoSrc = ref('')
const showVideoControl = ref(false)

const handleChangeSwiper = (currentIndex: number) => {
  swiperList.value.currenuIdx = currentIndex
}

const allSwiperPic = computed(() => {
  const skuImage = (comProvideInject.skuGroup.value.skus.some((v) => v.image) && comProvideInject.skuGroup.value.skus.map((item) => item.image)) || []
  return [...swiperList.value.mainList, ...skuImage]
})

// 利用Map缓存图片宽高比
const cache = new Map()
/**
 * 根据图片地址获取图片宽高比 width/height 结果保留两位小数
 * @param url 图片地址
 */
const getImageRatio = (url: string) => {
  if (cache.has(url)) {
    return cache.get(url)
  } else {
    return new Promise<number>((resolve, reject) => {
      uni.getImageInfo({
        src: url,
        success: (res) => {
          cache.set(url, Number((res.width / res.height).toFixed(2)))
          resolve(Number((res.width / res.height).toFixed(2)))
        },
        fail: (err) => {
          reject(err)
        },
      })
    })
  }
}

/**
 * 计算图片展示高度
 */
const imgHeight = ref(750)

watch(
  () => [swiperList.value.currenuIdx, allSwiperPic.value],
  async () => {
    if (allSwiperPic.value.length === 0) {
      return
    }
    try {
      if (!allSwiperPic.value[swiperList.value.currenuIdx]) {
        return
      }
      const ratio = await getImageRatio(allSwiperPic.value[swiperList.value.currenuIdx])
      imgHeight.value = 750 / ratio
    } catch (error) {
      console.log(error)
      imgHeight.value = 750
      return
    }
    if (imgHeight.value > 1142) {
      imgHeight.value = 1142
    }
    if (imgHeight.value < 750) {
      imgHeight.value = 750
    }
  },
  {
    immediate: true,
    deep: true,
  },
)

watch(
  () => comProvideInject.goodInfo.value,
  (val) => {
    if (val.shopId && val.productId) {
      if (useUserStore().getterToken) {
        isCollection.value = val.whetherCollect
      }
    }
  },
)

const showVideo = () => {
  showVideoControl.value = true
  videoSrc.value = comProvideInject.goodInfo.value.videoUrl as string
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
@include b(shareBtn) {
  position: absolute;
  z-index: 3;
  height: 64rpx;
  width: 64rpx;
  right: 20rpx;
  bottom: 228rpx;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.42);
  text-align: center;
  line-height: 64rpx;
}
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
@include b(sell-out) {
  position: absolute;
  top: 0;
  width: 100%;
  background: rgba(0, 0, 0, 0.5);
  @include flex();
  @include e(img) {
    display: block;
    width: 234rpx;
    height: 181.11rpx;
  }
}
</style>
