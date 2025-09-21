<script setup lang="ts">
import { ref, watch, type PropType } from 'vue'

const props = defineProps({
  // 滑动动画时长
  duration: {
    type: [Number, String],
    default: 500,
  },
  // 源数据
  list: {
    type: Array as PropType<string[]>,
    default: () => [],
  },
  // 指示器的模式，rect|dot|number|round
  mode: {
    type: String,
    default: 'round',
  },
  // 源数据的下标 初始化时，默认显示第几项
  current: {
    type: Number,
    default: 0,
  },
  // 容器的高度，单位rpx
  height: {
    type: [Number, String],
    default: 250,
  },
  // 图片的裁剪模式
  imgMode: {
    type: String,
    default: 'aspectFill',
  },
  // 背景颜色
  bgColor: {
    type: String,
    default: '#f3f4f6',
  },
})
const emits = defineEmits(['click', 'change'])

const originIndex = ref(0)

// swiper需要的数据
const displaySwiperList = ref<string[]>([])
// 用于显示swiper的真正的下标数值只有：0，1，2。
const displayIndex = ref(0)
/**
 * 初始一个显示的swiper数据
 * @param index 从源数据的哪个开始显示默认0，如从其他页面跳转进来，要显示第n个，这个参数就是他的下标
 */
const initSwiperData = (index = 0) => {
  originIndex.value = index
  const originListLength = props.list.length // 源数据长度
  const displayList: string[] = []
  displayList[displayIndex.value] = props.list[index]
  displayList[displayIndex.value - 1 === -1 ? 2 : displayIndex.value - 1] = props.list[index - 1 === -1 ? originListLength - 1 : index - 1]
  displayList[displayIndex.value + 1 === 3 ? 0 : displayIndex.value + 1] = props.list[index + 1 === originListLength ? 0 : index + 1]
  displaySwiperList.value = displayList
}
/**
 * swiper滑动时候
 */
const swiperChange = (e: any) => {
  const { current: currentIndex } = e.detail
  const originListLength = props.list.length // 源数据长度
  // =============向后==========
  if (displayIndex.value - currentIndex === 2 || displayIndex.value - currentIndex === -1) {
    originIndex.value = originIndex.value + 1 === originListLength ? 0 : originIndex.value + 1
    displayIndex.value = displayIndex.value + 1 === 3 ? 0 : displayIndex.value + 1
    initSwiperData(originIndex.value)
  }
  // ======如果两者的差为-2或者1则是向前滑动============
  else if (displayIndex.value - currentIndex === -2 || displayIndex.value - currentIndex === 1) {
    originIndex.value = originIndex.value - 1 === -1 ? originListLength - 1 : originIndex.value - 1
    displayIndex.value = displayIndex.value - 1 === -1 ? 2 : displayIndex.value - 1
    initSwiperData(originIndex.value)
  }
  // 通知外部
  emits('change', originIndex.value)
}

watch(
  () => [props.list, props.current],
  () => {
    if (props.list.length) {
      initSwiperData(props.current)
    }
  },
  { immediate: true, deep: true },
)

defineExpose({
  initSwiperData,
})
</script>

<template>
  <view class="swiper_content_wrap">
    <swiper
      circular
      :duration="duration"
      :style="{
        height: height + 'rpx',
        backgroundColor: bgColor,
      }"
      @change="swiperChange"
    >
      <swiper-item v-for="(item, index) in displaySwiperList" :key="index" class="swiper_item">
        <view class="list_image_wrap" @tap.stop.prevent="emits('click', index)">
          <image class="swiper_image" :src="item" :mode="imgMode"></image>
        </view>
      </swiper-item>
    </swiper>
    <view class="swiper_indicator">
      <block v-if="mode === 'number'">
        <view class="indicator_item_number">{{ originIndex + 1 }}/{{ list.length }}</view>
      </block>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.swiper_content_wrap {
  position: relative;
  overflow: hidden;
  transform: translateY(0);
  .swiper_item {
    display: flex;
    overflow: hidden;
    align-items: center;
    flex: 1;
    .list_image_wrap {
      width: 100%;
      height: 100%;
      flex: 1;
      transition: all 0.5s;
      overflow: hidden;
      box-sizing: content-box;
      position: relative;
      .swiper_image {
        width: 100%;
        will-change: transform;
        height: 100%;
        /* #ifndef APP-NVUE */
        display: block;
        /* #endif */
        /* #ifdef H5 */
        pointer-events: none;
        /* #endif */
      }
    }
  }
  .swiper_indicator {
    padding: 0 24rpx;
    position: absolute;
    width: 100%;
    z-index: 1;
    display: flex;
    bottom: 12rpx;
    justify-content: flex-end;
    .indicator_item_number {
      padding: 6rpx 16rpx;
      line-height: 1;
      background-color: rgba(0, 0, 0, 0.3);
      border-radius: 100rpx;
      font-size: 26rpx;
      color: rgba(255, 255, 255, 0.8);
    }
  }
}
</style>
