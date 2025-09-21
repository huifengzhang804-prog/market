<script setup lang="ts">
import { ref, type PropType } from 'vue'
import UserWatchMaskItemRight from './user-watch-mask-item-right.vue'
import UserWatchMaskItemLeft from './user-watch-mask-item-left.vue'
import LiveCloseMask from '../mask/live-close-mask.vue'
import type { LiveRoomDetail } from '../../../apis/CreateLive/model'

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
  liveRoomDetail: { type: Object as PropType<LiveRoomDetail>, default: () => ({}) },
})
const current = ref(1)

const handleClose = () => {
  current.value = 1
}
const handleBack = () => {
  current.value = 1
}
const handleSwiperChange = (e: any) => {
  current.value = e.detail.current
}
const handleClosemask = () => {
  uni.navigateBack({ delta: 1 })
}
</script>

<template>
  <view class="user-watch-mask" :style="{ width: width + 'px', height: height + 'px' }">
    <live-close-mask v-if="isEnd" msg="直播已结束！" @close="handleClosemask"></live-close-mask>
    <swiper class="swiper" :style="{ width: width + 'px', height: height + 'px' }" :current="current" @change="handleSwiperChange">
      <swiper-item>
        <user-watch-mask-item-left
          :width="width"
          :height="height"
          :is-end="isEnd"
          @close="handleClose"
          @back="handleBack"
        ></user-watch-mask-item-left>
      </swiper-item>
      <swiper-item>
        <user-watch-mask-item-right :live-room-detail="liveRoomDetail" :is-end="isEnd"></user-watch-mask-item-right>
      </swiper-item>
    </swiper>
  </view>
</template>

<style scoped>
.user-watch-mask {
  background: rgba(0, 0, 0, 0.5);
}
</style>
