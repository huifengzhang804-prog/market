<template>
  <view>
    <u-popup v-model="params.isShow" :round="10" mode="center" border-radius="14" safe-area-inset-bottom>
      <view style="padding: 40rpx 20rpx 20rpx 20rpx">
        <view
          :style="{
            height: captchaRequest.bgImageHeight * props.scale + 'rpx',
            width: captchaRequest.bgImageWidth * props.scale + 'rpx',
            backgroundImage: 'url(' + params.captcha.bgImage + ')',
            backgroundSize: '100% 100%',
            position: 'relative',
          }"
        >
          <view
            :src="params.captcha.sliderImage"
            style="color: #4ceed6; border: #4cd964; position: absolute"
            :style="{
              backgroundImage: 'url(' + params.captcha.sliderImage + ')',
              backgroundSize: 'cover',
              transform: `translateX(${sliderImageLeft}rpx)`,
              height: captchaRequest.sliderImageHeight * props.scale + 'rpx',
              width: captchaRequest.sliderImageWidth * props.scale + 'rpx',
            }"
            @touchstart.stop="down"
            @touchend="up"
            @touchmove.stop.prevent="moving"
          />
        </view>
        <progress
          :percent="((sliderImageLeft / props.scale) * 100) / (captchaRequest.bgImageWidth - captchaRequest.sliderImageWidth)"
          activeColor="#4cd964"
          stroke-width="6"
        ></progress>
      </view>
    </u-popup>
  </view>
</template>

<script setup lang="ts">
import { reactive, computed, type PropType, watch, onMounted, onUnmounted } from 'vue'
import { throttle } from 'lodash'
import DateUtil from '@/utils/date'
import { TrackType } from './Captcha'
import type { ImageCaptchaTrack, CaptchaResponse, CaptchaRequest } from './Captcha'

const props = defineProps({
  modelValue: {
    type: Boolean,
    required: true,
  },
  scale: {
    type: Number,
    default: 2,
  },
  getForm: {
    type: Function as PropType<() => any>,
    required: true,
  },
  doSubmit: {
    type: Function as PropType<(request: CaptchaRequest<any>) => Promise<any>>,
    required: true,
  },
  captchaList: {
    type: Object as PropType<CaptchaResponse>,
    required: true,
  },
})
const emits = defineEmits(['update:modelValue', 'success'])
const sliderImageLeft = computed(() => {
  const trackList = captchaRequest.trackList
  const length = trackList.length
  if (!trackList || length <= 0) {
    return 0
  }
  const min = 0
  const max = (captchaRequest.bgImageWidth - captchaRequest.sliderImageWidth) * props.scale
  const left = trackList[length - 1].x * props.scale
  return left <= min ? min : left >= max ? max : left
})

const params = reactive({
  isShow: true,
  sliderValue: 0,
  captcha: {
    id: '',
    startX: 0,
    startY: 0,
    bgImage: '',
    sliderImage: '',
  },
})

const captchaRequest = reactive<ImageCaptchaTrack>({
  bgImageWidth: 0,
  bgImageHeight: 0,
  sliderImageWidth: 0,
  sliderImageHeight: 0,
  startSlidingTime: '',
  endSlidingTime: '',
  startTime: new Date(),
  trackList: [],
})
const refresh = async (data: CaptchaResponse) => {
  if (data) {
    initCaptcha(data)
  }
}
watch(
  () => props.modelValue,
  (value) => {
    params.isShow = value
    if (value) {
      refresh(props.captchaList)
    }
  },
  { immediate: true },
)
watch(
  () => params.isShow,
  (value) => emits('update:modelValue', value),
)
const initCaptcha = ({ id, captcha }: CaptchaResponse) => {
  params.sliderValue = 0
  params.captcha.id = id
  params.captcha.startX = 0
  params.captcha.startY = 0
  params.captcha.bgImage = captcha.backgroundImage
  params.captcha.sliderImage = captcha.templateImage
  captchaRequest.startTime = new Date()
  captchaRequest.bgImageWidth = captcha.backgroundImageWidth / props.scale
  captchaRequest.bgImageHeight = captcha.backgroundImageHeight / props.scale
  captchaRequest.sliderImageWidth = captcha.templateImageWidth / props.scale
  captchaRequest.sliderImageHeight = captcha.templateImageHeight / props.scale
  captchaRequest.startSlidingTime = new DateUtil().getYMDHMSs(captchaRequest.startTime)
  captchaRequest.trackList = []
}

const down = (event: TouchEvent) => {
  const point = event.touches[0]
  params.captcha.startX = Math.round(point.pageX)
  params.captcha.startY = Math.round(point.pageY)
  captchaRequest.trackList.push({
    x: 0,
    y: 0,
    type: TrackType.DOWN,
    t: new Date().getTime() - captchaRequest.startTime.getTime(),
  })
}

const move = (event: TouchEvent) => {
  const point = event.touches[0]
  captchaRequest.trackList.push({
    x: Math.round(point.pageX) - params.captcha.startX,
    y: Math.round(point.pageY) - params.captcha.startY,
    type: TrackType.MOVE,
    t: new Date().getTime() - captchaRequest.startTime.getTime(),
  })
}
const moving = throttle(move, 30)
const up = (event: TouchEvent) => {
  const point = event.changedTouches[0]
  captchaRequest.endSlidingTime = new DateUtil().getYMDHMSs(new Date())
  captchaRequest.trackList.push({
    x: Math.round(point.pageX) - params.captcha.startX,
    y: Math.round(point.pageY) - params.captcha.startY,
    type: TrackType.UP,
    t: new Date().getTime() - captchaRequest.startTime.getTime(),
  })
  props
    .doSubmit({
      form: props.getForm(),
      id: params.captcha.id,
      captchaTrack: captchaRequest,
    } as CaptchaRequest<any>)
    .then((response) => {
      const { code, msg } = response
      if (code === 200) {
        emits('success', response)
        return
      }
      if (msg) {
        uni.showToast({ title: `${msg}`, icon: 'none' })
      }
      refresh(props.captchaList)
      return
    })
}

let startX = 0
let startY = 0

const touchstartFn = (e: TouchEvent) => {
  startX = e.targetTouches[0].pageX
  startY = e.targetTouches[0].pageY
}

const touchmoveFn = (e: TouchEvent) => {
  var moveX = e.targetTouches[0].pageX
  var moveY = e.targetTouches[0].pageY

  if (Math.abs(moveX - startX) > Math.abs(moveY - startY)) {
    e.preventDefault()
  }
}

// #ifdef H5
onMounted(() => {
  document.addEventListener('touchstart', touchstartFn)
  document.addEventListener('touchmove', touchmoveFn, { passive: false })
})
onUnmounted(() => {
  document.removeEventListener('touchstart', touchstartFn)
  document.removeEventListener('touchmove', touchmoveFn)
})
// #endif
</script>

<style scoped lang="scss"></style>
