<!--
 * 魔方渲染组件
-->
<script setup lang="ts">
import { onMounted, type PropType, ref } from 'vue'
import linkNavTo from '@/libs/linkNavTo'
import type { CubeBoxFormData, IBanners } from '../types'

const $props = defineProps({
  decorationProperties: {
    type: Object as PropType<CubeBoxFormData>,
    default() {
      return {}
    },
  },
})
const borderWidth = ref(0)
const layoutWidth = ref(0)
const layoutHeight = ref(0)
const showMethod = ref(0)
const pageMargin = ref(0)
const width = ref(0)
const subEntry = ref<IBanners[]>([])
const showCubeListWrap = ref<any[]>([])
const pageMarginStyle = ref<any>({
  style: `height:200px`,
})

getProperties()
onMounted(() => {
  drawCube()
})

function getProperties() {
  borderWidth.value = $props.decorationProperties.borderWidth
  layoutWidth.value = $props.decorationProperties.layoutWidth
  layoutHeight.value = $props.decorationProperties.layoutHeight
  showMethod.value = $props.decorationProperties.showMethod
  pageMargin.value = $props.decorationProperties.pageMargin
  width.value = $props.decorationProperties.width
  subEntry.value = $props.decorationProperties.subEntry
}
/**
 * 获取系统信息
 */
async function drawCube() {
  uni.getSystemInfo({
    success: (res) => {
      console.log($props.decorationProperties)
      const perviewLayoutWidth = res.windowWidth
      const wrapWith = perviewLayoutWidth + borderWidth.value - pageMargin.value * 2
      const styleWidth = wrapWith / layoutWidth.value
      const styleHeight = layoutHeight.value !== 1 ? perviewLayoutWidth / layoutHeight.value : styleWidth
      drawCubeWrap(styleWidth, styleHeight, wrapWith)
    },
  })
}
function drawCubeWrap(divWidth: number, divHeight: number, wrapWith: number) {
  const tempShowCubeListWrap = []
  let maxY = 0,
    maxIndex = 0,
    maxHeight = 0
  if (subEntry.value.length) {
    for (let i = 0; i < subEntry.value.length; i++) {
      const a = subEntry.value[i]
      const coverDiv = {
        top: a.y * divHeight + 'px',
        left: a.x * divWidth + pageMargin.value + 'px',
        width: divWidth * a.width - borderWidth.value + 'px',
        height: divHeight * a.height - borderWidth.value + 'px',
        paddingTop: (divHeight * a.height) / 2 + 'px',
        img: a[`img`] ? a[`img`] : '',
        borderWidth: borderWidth.value / 2 + 'px',
        style: `grid-column-start: ${a.x + 1};grid-column-end: ${a.x + a.width + 1};grid-row-start: ${a.y + 1};grid-row-end: ${a.y + a.height + 1};`,
      }
      if (maxY <= a.y) {
        maxY = a.y
        maxIndex = i
      }
      tempShowCubeListWrap.push(coverDiv)
    }
    maxHeight = maxY + subEntry.value[maxIndex].height < layoutHeight.value ? maxY + subEntry.value[maxIndex].height : layoutHeight.value
    console.log(maxHeight)
    showCubeListWrap.value = tempShowCubeListWrap
    pageMarginStyle.value = {
      width: wrapWith,
      height: divHeight * maxHeight,
      margin: `-${borderWidth.value / 2}px`,
      style: `height:${divHeight * maxHeight}px;grid-template-columns: repeat(${
        layoutWidth.value
      }, 1fr);grid-template-rows: repeat(${maxHeight}, 1fr);grid-gap: ${borderWidth.value}px;padding: 0 ${pageMargin.value}px`,
    }
  }
}
const handleTap = (idx: number) => {
  const currentSwiperItem = subEntry.value[idx]
  const itemLink = currentSwiperItem.link
  linkNavTo(itemLink)
}
</script>

<template>
  <view class="rc-design-react-preview rc-design-component-default-preview">
    <view v-if="showCubeListWrap.length > 0" class="cap-cube-wrap">
      <view class="cap-cube" :style="pageMarginStyle.style">
        <view
          v-for="(item, index) in showCubeListWrap"
          :key="index"
          class="cap-cube__item"
          :style="item.style"
          :data-index="index"
          @click="handleTap(index)"
        >
          <image :src="item.img" />
          <button v-if="subEntry[index].link.type === 0 && +subEntry[index].link.id === 8" open-type="contact" class="contact-style"></button>
        </view>
      </view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.rc-design-react-preview {
  position: relative;
  width: 100%;
  overflow: hidden;
  background: #f9f9f9;
}

.cap-cube__item {
  // background-repeat: no-repeat;
  // background-size: contain;
  // background-position: 50%;
  overflow: hidden;
}

image {
  width: 100%;
  height: 100%;
}

.cap-cube {
  display: grid;
  width: 100%;
}

@include b(contact-style) {
  position: relative;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  opacity: 0;
}
</style>
