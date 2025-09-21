<script setup lang="ts">
import 'uno.css'
import { computed } from 'vue'
import QIcon from '@/components/q-icon/q-icon.vue'
import { useConversionPrice } from '../index'
import countdown from '@/pluginPackage/seckill/components/countdown.vue'

const props = defineProps({
  properties: {
    type: Object,
    default: () => {},
  },
})
const $emit = defineEmits(['end'])
// 活动是否开始
const activitiesBegan = computed(() => {
  const startTime = props.properties?.secKillGoodsInfo?.start
  // 当前时间大于活动开始时间 活动开始
  return Date.now() > new Date(startTime).getTime()
})
// 倒计时时间
const getTime = computed(() => {
  const { start, end } = JSON.parse(JSON.stringify(props.properties?.secKillGoodsInfo))
  return activitiesBegan.value ? end : start
})
const handleEnd = () => {
  $emit('end')
}
</script>
<template>
  <!-- 秒杀信息 -->
  <div v-if="activitiesBegan">
    <div flex items-center justify-between e-bg-linear c-h-55 c-pl-20 c-pr-20>
      <div c-fs-20 e-c-f c-lh-55><q-icon name="icon-miaosha1" size="30px" c-mr-10 />限时秒杀</div>
      <div c-fs-16 e-c-f>
        距离结束
        <countdown :start-time="getTime" c-ml-8 :isStart="true" @end="handleEnd">
          <template #default="{ timeArr }">
            <text e-c-f c-fs-20 c-w-34 c-h-34 text-center c-lh-34 c-bg-443b3b>{{ timeArr[0] }}</text>
            <text c-fs-26 e-c-f c-ml-5 c-mr-5>:</text>
            <text e-c-f c-fs-20 c-w-34 c-h-34 text-center c-lh-34 c-bg-443b3b>{{ timeArr[1] }}</text>
            <text c-fs-26 e-c-f c-ml-5 c-mr-5>:</text>
            <text e-c-f c-fs-20 c-w-34 c-h-34 text-center c-lh-34 c-bg-443b3b>{{ timeArr[2] }}</text>
          </template>
        </countdown>
      </div>
    </div>
    <!-- 商品信息 -->
    <div c-w-713 c-bg-f2f2f2 c-pl-16 c-pt-12 c-pb-7>
      <div flex items-center c-mb-5>
        秒 杀 价
        <span flex items-end c-ml-14 c-c-e31436 fw-700 c-fs-16>
          <text c-lh-27>￥</text>
          <text c-fs-30>{{ useConversionPrice(props.properties?.choosedSpec?.salePrice).toFixed(2).split('.')[0] }}</text>
          <text c-lh-27>.</text>
          <text c-lh-27>{{ useConversionPrice(props.properties?.choosedSpec?.salePrice).toFixed(2).split('.')[1] }}</text>
        </span>
        <div flex c-ml-5 items-end style="color: #999; height: 28px">
          <div flex items-center>
            <span>￥</span>
            <span style="text-decoration-line: line-through; line-height: 20px; display: inline-block">{{
              useConversionPrice(props.properties?.choosedSpec?.price)
            }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
  <div v-else>
    <div flex items-center justify-between c-bg-FD9224 c-h-40 c-pl-20 c-pr-20>
      <div c-fs-16 e-c-f flex items-center>
        <svg
          width="19.701050"
          height="18.715820"
          viewBox="0 0 19.701 18.7158"
          fill="none"
          xmlns="http://www.w3.org/2000/svg"
          xmlns:xlink="http://www.w3.org/1999/xlink"
        >
          <desc>Created with Pixso.</desc>
          <defs />
          <path
            id="path"
            d="M18.92 5.83C17.95 3.56 16.12 1.74 13.85 0.77C14.46 0.29 15.24 0 16.08 0C18.08 0 19.7 1.61 19.7 3.6C19.7 4.44 19.4 5.21 18.92 5.83ZM18.77 9.81C18.77 12.18 17.84 14.32 16.33 15.91L17.58 17.16C17.75 17.32 17.85 17.55 17.85 17.8C17.85 18.3 17.44 18.71 16.94 18.71C16.68 18.71 16.46 18.61 16.29 18.44L14.95 17.11C13.5 18.12 11.74 18.71 9.85 18.71C7.95 18.71 6.2 18.12 4.75 17.11L3.42 18.44C3.25 18.61 3.02 18.71 2.77 18.71C2.26 18.71 1.84 18.3 1.84 17.79C1.84 17.54 1.95 17.31 2.11 17.14L3.35 15.91C1.85 14.31 0.92 12.17 0.92 9.81C0.92 4.9 4.92 0.92 9.85 0.92C14.78 0.92 18.77 4.9 18.77 9.81ZM9.85 2.76C5.94 2.76 2.77 5.92 2.77 9.81C2.77 13.71 5.94 16.87 9.85 16.87C13.76 16.87 16.93 13.71 16.93 9.81C16.93 5.92 13.76 2.76 9.85 2.76ZM13.54 10.73L9.85 10.73C9.34 10.73 8.92 10.32 8.92 9.81L8.92 4.9C8.92 4.4 9.34 3.98 9.85 3.98C10.36 3.98 10.77 4.4 10.77 4.9L10.77 8.89L13.54 8.89C14.05 8.89 14.46 9.3 14.46 9.81C14.46 10.32 14.05 10.73 13.54 10.73ZM0.79 5.95C0.29 5.32 0 4.54 0 3.68C0 1.64 1.65 0 3.69 0C4.55 0 5.34 0.29 5.97 0.79C3.65 1.78 1.78 3.63 0.79 5.95Z"
            fill="#FFFFFF"
            fill-opacity="1.000000"
            fill-rule="nonzero"
          />
        </svg>
        <span
          >&ensp;秒杀预售<span fw-700>&emsp;秒杀价￥<span c-fs-20>???</span></span></span
        >
      </div>
      <div c-fs-16 e-c-f>
        距离开始
        <countdown :start-time="getTime" c-ml-8 :isStart="true" @end="handleEnd">
          <template #default="{ timeArr }">
            <text c-c-FD9224 c-fs-20 c-w-30 c-h-30 text-center c-lh-30 c-bg-fff>{{ timeArr[0] }}</text>
            <text c-fs-26 e-c-f c-ml-5 c-mr-5>:</text>
            <text c-c-FD9224 c-fs-20 c-w-30 c-h-30 text-center c-lh-30 c-bg-fff>{{ timeArr[1] }}</text>
            <text c-fs-26 e-c-f c-ml-5 c-mr-5>:</text>
            <text c-c-FD9224 c-fs-20 c-w-30 c-h-30 text-center c-lh-30 c-bg-fff>{{ timeArr[2] }}</text>
          </template>
        </countdown>
      </div>
    </div>
    <!-- 商品信息 -->
  </div>
</template>

<style lang="scss" scoped></style>
