<!-- eslint-disable no-redeclare -->
<!-- eslint-disable no-import-assign -->
<script setup lang="ts">
// @ts-nocheck
// #ifndef H5
import BlankHolder from '@decoration/components/blank-holder/blank-holder.vue'
import CubeBox from '@decoration/components/cube-box/cube-box.vue'
import Good from '@decoration/components/good/good.vue'
import ResizeImage from '@decoration/components/resize-image/resize-image.vue'
import RichTextArea from '@decoration/components/rich-text/rich-text.vue'
import Search from '@decoration/components/search/search.vue'
import Separator from '@decoration/components/separator/separator.vue'
import ShopNav from '@decoration/components/shop-nav/shop-nav.vue'
import CusSwiper from '@decoration/components/swiper/swiper.vue'
import TitleBar from '@decoration/components/title-bar/title-bar.vue'
import CusVideo from '@decoration/components/cus-video/cus-video.vue'
import ShopGoods from '@/decoration/components/shop-goods/shop-goods.vue'
import PositioningStyle from '@/decoration/components/positioning-style/positioning-style.vue'
import ComposeSwiper from '@/decoration/components/compose-swiper/compose-swiper.vue'
import ComposeLocation from '@/decoration/components/compose-location/compose-location.vue'
import ShopGoodsO2o from '@/decoration/components/shop-goods/shop-goods-o2o/shop-goods-o2o.vue'
// 插件引入
import SecKill from '@plugin/secKill/components/decorationComponents/sec-kill.vue'
// #endif
// #ifdef H5
const BlankHolder = defineAsyncComponent(() => import('@decoration/components/blank-holder/blank-holder.vue'))
const CubeBox = defineAsyncComponent(() => import('@decoration/components/cube-box/cube-box.vue'))
const Good = defineAsyncComponent(() => import('@decoration/components/good/good.vue'))
const ResizeImage = defineAsyncComponent(() => import('@decoration/components/resize-image/resize-image.vue'))
const RichTextArea = defineAsyncComponent(() => import('@decoration/components/rich-text/rich-text.vue'))
const Search = defineAsyncComponent(() => import('@decoration/components/search/search.vue'))
const Separator = defineAsyncComponent(() => import('@decoration/components/separator/separator.vue'))
const ShopNav = defineAsyncComponent(() => import('@decoration/components/shop-nav/shop-nav.vue'))
const CusSwiper = defineAsyncComponent(() => import('@decoration/components/swiper/swiper.vue'))
const TitleBar = defineAsyncComponent(() => import('@decoration/components/title-bar/title-bar.vue'))
const CusVideo = defineAsyncComponent(() => import('@decoration/components/cus-video/cus-video.vue'))
const ShopGoods = defineAsyncComponent(() => import('@/decoration/components/shop-goods/shop-goods.vue'))
const PositioningStyle = defineAsyncComponent(() => import('@/decoration/components/positioning-style/positioning-style.vue'))
const SecKill = defineAsyncComponent(() => import('@plugin/secKill/components/decorationComponents/sec-kill.vue'))
const ComposeSwiper = defineAsyncComponent(() => import('@decoration/components/compose-swiper/compose-swiper.vue'))
const ComposeLocation = defineAsyncComponent(() => import('@decoration/components/compose-location/compose-location.vue'))
const ShopGoodsO2o = defineAsyncComponent(() => import('@/decoration/components/shop-goods/shop-goods-o2o/shop-goods-o2o.vue'))
// #endif
// #ifdef MP-WEIXIN
import LiveComponent from '@plugin/live/components/decorationComponents/live.vue'
// #endif
import { defineAsyncComponent, type PropType, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { watch } from 'vue'

interface DecorateItemType {
  formData: any
  icon: string
  id: string
  label: string
  value: string
}

const $props = defineProps({
  decorateList: {
    type: Array as PropType<DecorateItemType[]>,
    default() {
      return []
    },
  },
  chooseIndex: {
    type: Number,
    default: 0,
  },
})

const showComposeSwiper = ref(true)

// onLoad(() => {
//   uni.$on('switchBarHandle', onSwitchBar)
// })

watch(
  () => $props.chooseIndex,
  (newVal) => {
    showComposeSwiper.value = !showComposeSwiper.value
  },
  {
    deep: true,
  },
)
</script>
<template>
  <view>
    <view v-for="(item, index) in $props.decorateList" :key="index">
      <!-- 组合组件 -->
      <view v-if="item.value === 'compose'" :key="item.id">
        <Search v-if="item.formData?.search.show" :decorationProperties="item.formData.search" />
        <ComposeLocation v-if="item.formData?.positionStyle?.show" :decorationProperties="item.formData.positionStyle" />
        <ComposeSwiper
          v-if="item.formData?.swiper.swiperList.length > 0 && showComposeSwiper"
          :decorationProperties="item.formData.swiper"
        ></ComposeSwiper>
      </view>
      <BlankHolder v-if="item.value === 'blankPaceholder'" :key="item.id" :decorationProperties="item.formData" />
      <CubeBox v-if="item.value === 'cubeBox'" :key="item.id" :decorationProperties="item.formData" />
      <Good v-if="item.value === 'goods'" :decorationProperties="item.formData" />
      <ResizeImage v-if="item.value === 'resizeImage'" :key="item.id" :decorationProperties="item.formData" />
      <RichTextArea v-if="item.value === 'richText'" :key="item.id" :decorationProperties="item.formData" />
      <Search v-if="item.value === 'search'" :key="item.id" :decorationProperties="item.formData" />
      <Separator v-if="item.value === 'separator'" :key="item.id" :decorationProperties="item.formData" />
      <ShopNav v-if="item.value === 'navigation'" :key="item.id" :decorationProperties="item.formData" />
      <CusSwiper v-if="item.value === 'swiper'" :key="item.id" :decorationProperties="item.formData" />
      <TitleBar v-if="item.value === 'titleBar'" :key="item.id" :decorationProperties="item.formData" />
      <CusVideo v-if="item.value === 'video'" :key="item.id" :decorationProperties="item.formData" />
      <SecKill v-if="item.value === 'secKill'" :key="item.id" :decorationProperties="item.formData" />
      <ShopGoods v-if="item.value === 'shopGoods'" :key="item.id" :item="item" :decorationProperties="item.formData" />
      <PositioningStyle v-if="item.value === 'positioningStyle'" :key="item.id" :decorationProperties="item.formData" />
      <!-- #ifdef MP-WEIXIN -->
      <LiveComponent v-if="item.value === 'live'" :key="item.id" :decorationProperties="item.formData" />
      <!-- #endif -->
    </view>
  </view>
</template>

<style lang="scss" scoped></style>
