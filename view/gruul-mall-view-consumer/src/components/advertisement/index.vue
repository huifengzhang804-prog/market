<template>
  <view>
    <view v-if="openAdv.imageList.length">
      <u-popup
        v-model="isOpenAdv"
        mode="center"
        :mask="false"
        :safeAreaInsetBottom="true"
        :maskCloseAble="false"
        width="100%"
        height="100dvh"
        @close="handleClose"
      >
        <img :src="currentImage" alt="" class="advertisementImg" @click="handleClick(openAdv.imageList[currentIndex])" />
        <view v-if="openAdv.skipWay !== 'NO_SKIP'" class="cloesAdvertisement">
          <view
            v-if="openAdv.skipWay === 'MANUAL_SKIP_AFTER_SECONDS'"
            :class="{
              white: true,
              disabled: clearTime > 0,
            }"
            @click="skip"
            >跳过</view
          >
          <view v-if="clearTime > 0" class="time">{{ clearTime }}</view>
        </view>
      </u-popup>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onBeforeUnmount, ref, watch } from 'vue'
import type { ImageListType } from '@/apis/concern/types'
import { useAdvStore } from '@/store/modules/adv'
import { storeToRefs } from 'pinia'
import { useSettingStore } from '@/store/modules/setting'
import linkNavTo from '@/libs/linkNavTo'

const adv = useAdvStore()

const { openAdv, getOpenAdvIsPlay, popAdv } = storeToRefs(adv)
const $settingStore = useSettingStore()

const isOpenAdv = ref(false)
const handleClose = () => {
  isOpenAdv.value = false
  useAdvStore().SET_OPEN_ADV_FLAG(false)
}
watch(
  () => getOpenAdvIsPlay.value,
  (newValue) => {
    isOpenAdv.value = newValue
  },
  {
    immediate: true,
    deep: true,
  },
)

// 当前图片索引
const currentIndex = ref(-1)
// 当前图片
const currentImage = ref('')
// 当前图片展示时间
const advertisementTime = ref()

// 图片切换函数
const switchImage = () => {
  console.log('切换图片')
  currentIndex.value++
  if (currentIndex.value === openAdv.value.imageList.length) {
    currentIndex.value = openAdv.value.imageList.length - 1
    return
  }
  currentImage.value = openAdv.value.imageList[currentIndex.value]?.url
  advertisementTime.value = openAdv.value.imageList[currentIndex.value]?.showTime * 1000
}
const changeImg = ref()
// 每隔一定时间切换图片
watch(
  () => currentIndex,
  (val) => {
    console.log('watch条件', timeOut.value)
    clearInterval(changeImg.value)
    changeImg.value = setInterval(switchImage, openAdv.value.imageList[val.value]?.showTime * 1000)
  },
  {
    deep: true,
  },
)

onBeforeUnmount(() => {
  console.log('卸载intervalId', intervalId)
  console.log('卸载changeImg', changeImg.value)
  clearInterval(intervalId)
  clearInterval(changeImg.value)
  console.log(popAdv.value.imageInfo.url)
  if (popAdv.value.imageInfo.url) {
    console.log('开始首页弹窗')
    useAdvStore().SET_POP_ADV_FLAG(true)
  }
})

/**
 * 计算展示时间
 */
const timeOut = ref(0)
const clearTime = ref(0)
const openTime = () => {
  openAdv.value.imageList.forEach((v) => {
    timeOut.value += v.showTime
  })
  if (openAdv.value.skipWay === 'NO_SKIP') {
    clearTime.value = timeOut.value
  } else if (openAdv.value.skipWay === 'MANUAL_SKIP_AFTER_SECONDS' || openAdv.value.skipWay === 'AUTO_SKIP_AFTER_SECONDS') {
    clearTime.value += openAdv.value.skipSecond
  }
}
// 监听 openAdv.value 的变化
// 监听openAdv.value 的变化除了openAdv.value中的isPlay变化不监听外，其他变化都监听

watch(
  () => openAdv.value,
  (newValue) => {
    // 当 openAdv.value 有数据时调用方法
    if (newValue) {
      switchImage()
      openTime()
    }
  },
  {
    immediate: true,
  },
)

/**
 * 开屏广告倒计时
 */
const updateCount = () => {
  console.log('计时器', timeOut.value)
  if (timeOut.value >= 1) {
    clearTime.value--
    timeOut.value--
    if (openAdv.value.skipWay === 'AUTO_SKIP_AFTER_SECONDS' && clearTime.value <= 0) {
      useAdvStore().SET_OPEN_ADV_FLAG(false)
    }
  } else {
    useAdvStore().SET_OPEN_ADV_FLAG(false)
  }
}
/**
 * 跳转图片链接
 * @param imgInfo
 */
const handleClick = (imgInfo: ImageListType) => {
  const link = JSON.parse(imgInfo?.link || '{}')
  if (link.type === '') {
    return
  }
  if (link.url === '/') {
    $settingStore.SET_CUR_SWIPER_ID(link.id)
    $settingStore.SET_LOADING(true, link.id)
    uni.setNavigationBarTitle({
      title: link.name,
    })
    skip()
  } else if (['https://', ''].includes(link.url)) {
    return
  } else {
    linkNavTo(link).then(() => {
      skip()
    })
  }
}

// 每秒调用一次 updateCount 函数
let intervalId = setInterval(updateCount, 1000)

/**
 * 关闭开屏广告
 */
const skip = () => {
  if (clearTime.value > 0) {
    return
  }
  adv.SET_OPEN_ADV_FLAG(false)
  clearInterval(intervalId)
  console.log('首页弹窗图片', popAdv.value.imageInfo.url)
  if (popAdv.value.imageInfo.url) {
    console.log('开始首页弹窗')
    useAdvStore().SET_POP_ADV_FLAG(true)
  }
}
</script>

<style lang="scss" scoped>
.cloesAdvertisement {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 70px;
  line-height: 30px;
  position: fixed;
  z-index: 100;
  font-size: 16px;
  color: red;
  background-color: rgb(0, 0, 0, 0.5);
  border-radius: 15px;
  bottom: 5%;
  right: 4%;
  text-align: center;
  .time {
    color: #fd9224;
    margin-left: 5px;
  }
  .white {
    color: #fff;
  }
  .disabled {
    color: #606060;
  }
}
.advertisementImg {
  z-index: 99;
  position: fixed;
  top: 0;
  width: 100%;
  height: 100dvh;
}
:deep(.u-mode-center-box) {
  background-color: transparent;
}
</style>
