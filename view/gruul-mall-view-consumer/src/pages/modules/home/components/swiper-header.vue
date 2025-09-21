<!-- eslint-disable no-undef -->
<!-- eslint-disable no-redeclare -->
<!-- eslint-disable no-import-assign -->
<script setup lang="ts">
import { ref, computed, type PropType, defineAsyncComponent, watch } from 'vue'
import { useStatusBar } from '@/hooks'
import QNav from '@/components/q-nav/q-nav.vue'
import Loading from '@/components/q-loading/q-loading.vue'
import useSwiperItemHooks from './hooks/useSwiperItemHooks'
// #ifndef H5
// @ts-ignore
import Recommend from '@/pages/modules/home/components/recommend/recommend.vue'
// #endif
// #ifdef H5
const Recommend = defineAsyncComponent(() => import('@/pages/modules/home/components/recommend/recommend.vue'))
// #endif
import type { dataItem } from '@/pages/modules/home/types'
import { reactive } from 'vue'
import { composeDecorationStore } from '@/store/modules/composedecoration'

const navbg = ref('-webkit-filter: blur(24rpx);')
const $props = defineProps({
  o2oShow: {
    type: Boolean,
    default: false,
  },
  renderData: {
    type: Array as PropType<dataItem[]>,
    default: () => [
      {
        list: [],
        status: '未配置',
        name: '推荐',
      },
    ],
  },
})

const { refreshSwiper } = useSwiperItemHooks()
const computedRenderData = computed(() => {
  const renderList = $props.renderData.filter((item) => item.status === '成功')
  composeDecorationStore().setTopAreaShow(renderList.length > 1)
  return renderList
})

interface SwiperLoaded {
  [key: number]: boolean
}
const chooseIndex = ref(0)
const tabbarNode = uni.upx2px(100)
const swiperTitleNode = uni.upx2px(134)
const statusBarHeight = useStatusBar()
const safeHeight = useBottomSafe()
const swiperLoaded = reactive<SwiperLoaded>({})

const emit = defineEmits(['initFn'])
const screenHeight = useScreenHeight()

// 可视区域滑动高度计算
const scrollViewHeight = computed(() => {
  let height
  if (computedRenderData.value.length > 1) {
    // #ifdef H5
    height = `${screenHeight.value - (tabbarNode + swiperTitleNode + safeHeight.value)}px`
    // #endif
    // #ifndef H5
    height = `${screenHeight.value - (tabbarNode + statusBarHeight.value + safeHeight.value)}px`
    // #endif
  } else {
    height = `${screenHeight.value - (tabbarNode + safeHeight.value)}px`
  }
  console.log('高度', height)
  return height
})

const setSwiperLoaded = (chooseId: number) => {
  chooseIndex.value = chooseId
  if (!swiperLoaded[chooseId])
    setTimeout(() => {
      swiperLoaded[chooseId] = true // 禁止 cls 偏移
    }, 180)
}

const switchBarHandle = (num: number) => {
  chooseIndex.value = num
  uni.$emit('switchBarHandle', computedRenderData.value[num].name)
  composeDecorationStore().setAutoplay()
  composeDecorationStore().setBackground('')
  if (computedRenderData.value[num].name !== '推荐') {
    composeDecorationStore().setHeadbg('')
    composeDecorationStore().setTabBg('')
  }
}

const handleChange = (e: any) => {
  chooseIndex.value = e.detail.current
  emit('initFn', computedRenderData.value[chooseIndex.value].name)
}

watch(
  () => chooseIndex.value,
  (val) => {
    setTimeout(() => {
      refreshSwiper(val, computedRenderData.value)
    }, 180)
  },
)

defineExpose({
  chooseIndex,
  setSwiperLoaded,
})
</script>

<template>
  <view class="swiper" :style="{ height: `${screenHeight}px` }">
    <!-- 顶部组合组件背景色 start-->
    <block v-if="computedRenderData.length > 1">
      <view
        v-if="
          composeDecorationStore().background &&
          $props.renderData[chooseIndex].list[0].formData?.swiper?.bgType !== 'color' &&
          $props.renderData[chooseIndex].list[0].formData?.swiper
        "
        class="navbg"
        :style="`${navbg}background-size: cover;background-image:url(${composeDecorationStore().background})`"
      >
      </view>
      <view v-else class="navbg"></view>
    </block>
    <!-- 顶部组合组件背景色 end-->
    <!-- #ifdef H5 -->
    <view
      v-if="computedRenderData.length > 1"
      class="swiper__title"
      :style="`visibility:${computedRenderData.length > 1 ? 'unset' : 'hidden'};${composeDecorationStore().headbg};`"
    >
      <view class="tab-list" :style="composeDecorationStore().tabBg">
        <view
          v-for="(item, index) in computedRenderData"
          :key="item.name"
          :class="['swiper__title--item', chooseIndex === index ? 'is-choosed' : '']"
          @click="switchBarHandle(index)"
        >
          {{ item.renderName }}
        </view>
      </view>
    </view>
    <!-- #endif -->
    <!-- #ifndef H5 -->
    <q-nav :show-back="false" bg-color="">
      <template #title>
        <view class="swiper__title" :style="`visibility:${computedRenderData.length > 1 ? 'unset' : 'hidden'};${composeDecorationStore().headbg};`">
          <view class="tab-list-wx" :style="composeDecorationStore().tabBg">
            <view
              v-for="(item, index) in computedRenderData"
              :key="item.name"
              :class="['swiper__title--item', chooseIndex === index ? 'is-choosed' : '']"
              @click="switchBarHandle(index)"
            >
              {{ item.renderName }}
            </view>
          </view>
        </view>
      </template>
    </q-nav>

    <!-- #endif -->
    <Loading :loading="!swiperLoaded[chooseIndex]" class="swiper--loaded" />
    <swiper
      v-show="swiperLoaded[chooseIndex]"
      :current="chooseIndex"
      easing-function="easeInOutCubic"
      :style="{ height: `${scrollViewHeight}` }"
      disable-touch
      @change="handleChange"
    >
      <swiper-item v-for="item in computedRenderData" :key="item.name" :item-id="item.name" @touchmove.stop>
        <Recommend
          v-if="item.active && (item.name === '推荐' || item.name === '同城')"
          :decorationList="item.list"
          :isShowLoading="item.status"
          :chooseIndex="chooseIndex"
        />
      </swiper-item>
    </swiper>
  </view>
</template>

<style lang="scss" scoped>
@import '@/assets/css/mixins/mixins.scss';

.navbg {
  z-index: -1;
  overflow: hidden;
  position: absolute;
  top: 0;
  width: 100%;
  background: linear-gradient(180deg, rgb(245, 67, 25), rgb(249, 249, 249) 87.786%);
  height: 554rpx;
}

.tab-list-wx {
  border-radius: 20px;
  display: flex;
  background: rgba(52, 52, 52, 0.1);
  view {
    padding: 9.5rpx 0;
  }
}

.tab-list {
  margin: 40rpx 0 30rpx 0;
  border-radius: 20px;
  display: flex;
  background: rgba(52, 52, 52, 0.1);
  view {
    padding: 9.5rpx 0;
  }
}

@include b(swiper) {
  @include e(title) {
    position: relative;
    font-size: 32rpx;
    color: #fff;
    // background: #fff;
    text-align: center;
    // #ifdef H5
    // height: 100rpx;
    // #endif
    @include flex();

    @include m(item) {
      width: fit-content;
      height: 64rpx;
      display: flex;
      align-items: center;
      justify-content: center;
      // margin-right: 36rpx;
      text-align: center;
      padding: 0 36rpx !important;
    }
  }

  @include when(choosed) {
    background: #fff;
    color: #f54319;
    border-radius: 40rpx;
    font-weight: bold;
    transition: all 0.7s;
  }

  @include m(loaded) {
    height: calc(100vh - 100rpx - 100rpx);
  }
}

.swiper__title {
  // #ifdef H5
  height: 134rpx;
  // #endif
  font-size: 28rpx;
}
</style>
