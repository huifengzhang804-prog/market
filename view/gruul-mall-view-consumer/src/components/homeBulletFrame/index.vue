<template>
  <u-popup v-model="isShow" mode="center" :mask="!!popAdv.imageInfo.url" :safeAreaInsetBottom="true" :maskCloseAble="false" bgColor="transparent">
    <view class="cont">
      <view class="imgbox">
        <u-image
          width="600rpx"
          :showLoading="false"
          :showError="false"
          :src="popAdv.imageInfo.url"
          mode="widthFix"
          @click="handleClick(popAdv.imageInfo)"
        ></u-image>
      </view>
      <QIcon name="icon-guanbi3" class="imgClose" size="14px" @click="skip"></QIcon>
    </view>
  </u-popup>
</template>

<script setup lang="ts">
import { onBeforeUnmount, ref, watch } from 'vue'
import type { ImageListType } from '@/apis/concern/types'
import QIcon from '@/components/q-icon/q-icon.vue'
import { useAdvStore } from '@/store/modules/adv'
import { storeToRefs } from 'pinia'
import { useSettingStore } from '@/store/modules/setting'
import linkNavTo from '@/libs/linkNavTo'

const { popAdv } = storeToRefs(useAdvStore())
const $settingStore = useSettingStore()

let timeOut = ref()
const isShow = ref(false)
watch(
  () => popAdv.value.isPlay,
  (newVla) => {
    isShow.value = newVla
    if (newVla) {
      if (timeOut.value) {
        clearTimeout(timeOut.value)
      }
      timeOut.value = setTimeout(() => {
        skip()
      }, popAdv.value.imageInfo.showTime * 1000)
    }
  },
  {
    immediate: true,
    deep: true,
  },
)

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

/**
 * 关闭弹窗广告
 */
const skip = () => {
  useAdvStore().SET_POP_ADV_FLAG(false)
  clearTimeout(timeOut.value)
}
onBeforeUnmount(() => {
  clearTimeout(timeOut.value)
})
</script>

<style lang="scss" scoped>
.cont {
  display: flex;
  flex-direction: column;
  align-items: center;
  .imgbox {
    max-height: calc(100vh - 370rpx);
    overflow: hidden;
    display: flex;
    justify-content: center;
  }
  .imgClose {
    width: 28px;
    height: 28px;
    border-radius: 50%;
    border: 1px solid #fff;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
    margin-top: 40px;
  }
}
</style>
