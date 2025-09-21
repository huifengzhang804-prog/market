<script setup lang="ts">
import { ref, type PropType, onMounted } from 'vue'
import type { SwiperFormData, SwiperListItem } from '../types'
// #ifndef H5
import linkNavTo from '@/libs/linkNavTo'
// #endif
import { composeDecorationStore } from '@/store/modules/composedecoration'
import { useStatusBar } from '@/hooks'

const $props = defineProps({
  decorationProperties: {
    type: Object as PropType<SwiperFormData>,
    default() {
      return {}
    },
  },
  shopId: {
    type: String as PropType<Long>,
    default: '',
  },
})
const type = ref('')
const swiperList = ref<SwiperListItem[]>([])
const padding = ref(10)
/** 图片样式 1常规 2投影 */
const imageStyle = ref(1)
/** 图片倒角 1直角 2圆角 */
const imageAngle = ref(1)
/** 指示器 1样式一 2样式二 3样式三 */
const indicator = ref(1)
/** 样式 */
const ImageClass = ref('')
/** 当前轮播序列号 */
const curent = ref(0)
/** 图片高度 */
const height = ref('185rpx')

onMounted(() => {
  getProperties()
  getImageClass()
})

const handleTap = (idx: number) => {
  const currentSwiperItem = swiperList.value[idx]
  const itemLink = currentSwiperItem.link
  // #ifndef H5
  linkNavTo(itemLink)
  // #endif
  // #ifdef H5
  import('@/libs/linkNavTo').then(({ default: linkNavTo }) => {
    linkNavTo(itemLink)
  })
  // #endif
}

const onChange = (e: { detail: { current: number } }) => {
  curent.value = e.detail.current
}
function getProperties() {
  type.value = $props.decorationProperties.type
  swiperList.value = $props.decorationProperties.swiperList
  padding.value = $props.decorationProperties.padding
  imageStyle.value = $props.decorationProperties.imageStyle
  imageAngle.value = $props.decorationProperties.imageAngle
  indicator.value = $props.decorationProperties.indicator

  let headerHeight = 0
  const bottomHeight = useBottomSafe().value + uni.upx2px(100)
  if ($props.shopId) {
    headerHeight = useStatusBar().value + uni.upx2px(300)
  } else {
    // #ifdef H5
    headerHeight = composeDecorationStore().topAreaShow ? uni.upx2px(134) + useStatusBar().value : 0
    // #endif
    // #ifndef H5
    headerHeight = composeDecorationStore().topAreaShow ? useStatusBar().value : 0
    // #endif
  }
  height.value =
    $props.decorationProperties.height <= 1200
      ? `${$props.decorationProperties.height}rpx`
      : `${useScreenHeight().value - bottomHeight - headerHeight}px`
  console.log('图片高度:', height.value)
}
function getImageClass() {
  // 1常规 2投影
  const styles = ['homeSwiper-swiper__boxNM', 'homeSwiper-swiper__boxCP']
  // 图片倒角 1直角 2圆角
  const angles = ['homeSwiper-swiper__corners', 'homeSwiper-swiper__angle']
  const gs = styles[+imageStyle.value - 1]
  const as = angles[+imageAngle.value - 1]
  ImageClass.value = `${gs} ${as}`
}
</script>

<template>
  <view>
    <view v-if="swiperList.length > 0" class="homeSwiper">
      <swiper
        class="homeSwiper-swiper"
        :indicator-dots="false"
        :autoplay="true"
        :circular="true"
        :vertical="false"
        :interval="2000"
        :duration="500"
        :previous-margin="0 + 'px'"
        :next-margin="0 + 'px'"
        :style="{ height }"
        @change="onChange"
      >
        <swiper-item
          v-for="(item, index) in swiperList"
          :key="index"
          class="homeSwiper-swiper__item"
          :data-index="index"
          @tap.stop="handleTap(index)"
        >
          <view class="homeSwiper-swiper__image" :style="`padding: 0 ${padding}px`">
            <image
              style="width: 100%; height: 100%; display: block"
              mode="aspectFill"
              :src="item.img"
              :data-index="index"
              :data-key="item.img"
              :class="'skeleton--animate ' + ('homeSwiper-swiper--angle' + imageAngle)"
              :lazy-load="true"
            ></image>
            <button v-if="item.link.type === 0 && item.link.id === '8'" open-type="contact" class="contact-style"></button>
          </view>
        </swiper-item>
      </swiper>
      <view v-if="indicator === 1" class="homeSwiper-swiper__indicator homeSwiper-swiper__icat1">
        <text v-for="(ix, idx) in swiperList.length" :key="idx" :class="curent === idx ? 'homeSwiper-swiper__icat1--active' : ''"></text>
      </view>
      <view v-if="indicator === 3" class="homeSwiper-swiper__indicator homeSwiper-swiper__icat3">
        <text v-for="(ix, idx) in swiperList.length" :key="idx" :class="curent === idx ? 'homeSwiper-swiper__icat1--active' : ''"></text>
      </view>
      <view v-if="indicator === 2" class="homeSwiper-swiper__indicator homeSwiper-swiper__icat2" :style="'right: ' + (padding + 5) + 'px'">
        <text>{{ curent + 1 }}/{{ swiperList.length }}</text>
      </view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
@include b(homeSwiper) {
  background-color: #ffffff;
  position: relative;
}

@include b(homeSwiper-swiper) {
  height: 100%;

  @include e(item) {
    height: 100%;
  }

  @include e(image) {
    width: 100%;
    height: 100%;
    box-sizing: border-box;
    padding-top: 2px;
    display: flex;
    align-items: center;
    justify-content: center;
    image,
    img {
      display: block;
      width: 100%;
    }
    image {
      height: 100%;
    }
  }

  @include m(angle1) {
    border-radius: 0rpx;
  }

  @include m(angle2) {
    border-radius: 20rpx;
  }

  @include e(boxNM) {
    box-shadow: none;
  }

  @include e(boxCP) {
    box-shadow: 0 0 4px rgba(0, 0, 0, 0.2);
  }

  @include e(corners) {
    border-radius: 0px;
  }

  @include e(angle) {
    border-radius: 7px;
  }

  @include e(indicator) {
    position: absolute;
    display: flex;
    align-items: center;
    justify-content: center;
    bottom: 10px;
    // border: 1px solid red;
    height: 20px;
    width: 100%;
    box-sizing: border-box;
  }

  @include e(icat1) {
    text {
      display: inline-block;
      width: 8px;
      height: 8px;
      background-color: #ebedf0;
      opacity: 0.3;
      margin-right: 6px;
      border-radius: 50%;
    }

    @include m(active) {
      opacity: 0.8 !important;
      background-color: #fff !important;
    }
  }

  @include e(icat2) {
    position: absolute;
    right: 0;
    bottom: 15px;
    display: block;

    text {
      float: right;
      box-sizing: border-box;
      padding: 5px 12px;
      background-color: rgba(0, 0, 0, 0.2);
      color: #fff;
      font-size: 13px;
      border-radius: 16px;
    }
  }

  @include e(icat3) {
    text {
      display: inline-block;
      width: 22px;
      height: 3px;
      background-color: #ebedf0;
      opacity: 0.3;
      margin-right: 6px;
      border-radius: 4px;
    }

    @include m(active) {
      opacity: 0.8 !important;
      background-color: #fff !important;
    }
  }
}

@include b(contact-style) {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  opacity: 0;
}
</style>
