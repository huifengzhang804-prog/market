<script setup lang="ts">
import type { PropType } from 'vue'
import linkNavTo from '@/libs/linkNavTo'
import type { ResizeImageFormData } from '../types'

const $props = defineProps({
  decorationProperties: {
    type: Object as PropType<ResizeImageFormData>,
    default() {
      return {}
    },
  },
})

const pxTransform = (param: any) => {
  if (typeof param === 'number') {
    return `${param * 2}rpx`
  }
  const num: any = param.substring(0, param.length - 2)
  const plusNum = num * 2
  if (!isNaN(plusNum)) {
    return `${plusNum}rpx`
  } else {
    return param
  }
}
const handleLink = () => {
  linkNavTo($props.decorationProperties.link)
}
</script>

<template>
  <view v-if="$props.decorationProperties.img" class="boxContent">
    <image
      :style="{
        width: pxTransform($props.decorationProperties.width),
        height: pxTransform($props.decorationProperties.height),
        marginLeft: pxTransform($props.decorationProperties.left),
        marginTop: pxTransform($props.decorationProperties.top),
      }"
      :src="$props.decorationProperties.img"
      @click="handleLink"
    >
    </image>
  </view>
</template>
