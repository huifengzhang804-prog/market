<script setup lang="ts">
import { type PropType, computed, nextTick, onMounted, onUnmounted, reactive, ref } from 'vue'
import ClassItem from './class-item.vue'
import type { RetrieveCommodity } from '@/apis/good/model'
import type { ApiCategoryData, DeCategoryType } from '@/pages/platform/types'
import QIcon from '@/components/q-icon/q-icon.vue'
import { composeDecorationStore } from '@/store/modules/composedecoration'
// #ifndef H5
import linkNavTo from '@/libs/linkNavTo'
// #endif

const $props = defineProps({
  height: {
    type: Number,
    default: 1000,
  },
  info: {
    type: Object as PropType<ApiCategoryData>,
    default() {
      return {}
    },
  },
  large: {
    type: Number,
    default: 1,
  },
  list: {
    type: Array as PropType<RetrieveCommodity[]>,
    default() {
      return []
    },
  },
  goodsShow: {
    type: Array as PropType<number[]>,
    default() {
      return []
    },
  },
  config: {
    type: Object as PropType<DeCategoryType>,
    default() {
      return {}
    },
  },
  slideNavIndex: {
    type: Number,
    default: 0,
  },
})
const scrollOptions = reactive({
  old: 0,
  new: 0,
})
// #ifndef H5
const isH5 = ref(true)
// #endif
const scrollSecondOptions = ref(0)
const currentChooseIndex = ref(0)
const liveTwoShow = ref(false)
const isProgrammaticScroll = ref(false) // 添加标志来区分程序化滚动和用户手动滚动
const $emit = defineEmits(['listLoadMore', 'changeCategory', 'scrolltolower'])
const scrollContainer = ref<HTMLElement | null>(null)
const secondId = ref('')

const handleChooseUnit = (index: number, item?: ApiCategoryData) => {
  if ($props.large === 2 && item?.id) {
    secondId.value = item?.id
    $emit('changeCategory', item?.id)
  } else {
    // 设置程序化滚动标志
    isProgrammaticScroll.value = true
    // 滚动到对应位置
    nextTick(() => {
      // 动态计算滚动位置
      const childrenLength = $props.info?.children?.length || 0
      if (childrenLength > 0) {
        // 基础高度计算：考虑padding、margin和标题高度
        const basePadding = 28 + 35 // 上下padding (rpx)
        const baseMargin = 14 // 底部margin (rpx)
        const titleHeight = 30 // 标题高度 (rpx)
        const baseHeight = (basePadding + baseMargin + titleHeight) * 0.5 // 转换为px

        const goodsPerRow = 3 // 每行3个商品

        // 计算当前项之前所有项的总高度
        let totalScrollTop = 0
        for (let i = 0; i < index; i++) {
          const prevItem = $props.info?.children?.[i]
          const prevGoodsCount = prevItem?.children?.length || 0
          const prevRows = Math.ceil(prevGoodsCount / goodsPerRow)
          const prevGoodsHeight = prevRows * 115
          totalScrollTop += baseHeight + prevGoodsHeight
        }

        // 设置垂直滚动位置
        scrollOptions.new = scrollOptions.old
        nextTick(() => {
          scrollOptions.new = totalScrollTop
          // 滚动完成后重置标志
          setTimeout(() => {
            isProgrammaticScroll.value = false
          }, 500)
        })
      }
    })
  }
  currentChooseIndex.value = index
  liveTwoShow.value = false
}

const handleSecondChooseUnit = (index: number, item?: ApiCategoryData) => {
  handleChooseUnit(index, item)
  // 根据样式计算：padding(8rpx + 20rpx) + margin-right(20rpx) + 文字宽度
  const itemPadding = (8 + 20) * 0.5 // 转换为px
  const itemMargin = 20 * 0.5 // 转换为px
  const itemWidth = itemPadding * 2 + itemMargin + 80 // 80px为文字预估宽度
  const containerWidth = 200 // 容器宽度（根据实际布局调整）
  const targetScrollLeft = Math.max(0, index * itemWidth - containerWidth / 2 + itemWidth / 2)
  // 设置水平滚动位置
  scrollSecondOptions.value = targetScrollLeft
}

const scrolltolower = () => {
  // 如果是程序化滚动，不触发scrolltolower事件
  if (isProgrammaticScroll.value) {
    return
  }

  if ($props.large === 2) {
    $emit('listLoadMore', secondId.value || $props.info?.children?.[0]?.id, true)
  } else {
    // #ifndef H5
    let isScroll = false
    isScroll =
      $props.large === 2 && $props.list.length < 6
        ? true
        : $props.large !== 2 &&
            $props.info?.children &&
            (($props.info?.children?.length <= 3 && $props.info?.children.every((item) => item.children && item.children?.length < 3)) ||
              ($props.info?.children?.length < 2 && $props.info?.children[0]?.children && $props.info?.children[0]?.children?.length < 12))
          ? true
          : false
    $emit('scrolltolower', isScroll)
    // #endif
  }
}

function handleBackTop() {
  // 设置程序化滚动标志
  isProgrammaticScroll.value = true
  scrollOptions.new = scrollOptions.old
  nextTick(() => {
    scrollOptions.new = 0
    currentChooseIndex.value = 0
    // 滚动完成后重置标志
    setTimeout(() => {
      isProgrammaticScroll.value = false
    }, 500)
  })
}
function handleScroll(e: any) {
  scrollOptions.old = e.detail.scrollTop
}
const handleThreeShow = () => {
  liveTwoShow.value = true
}

const handleSecondScroll = (e: any) => {
  scrollSecondOptions.value = e.detail.scrollLeft
}

// 点击广告
const handleTap = (idx: number) => {
  const currentSwiperItem = $props.info?.ads?.[idx]
  const itemLink = JSON.parse(currentSwiperItem?.link)
  // #ifndef H5
  linkNavTo(itemLink)
  // #endif
  // #ifdef H5
  import('@/libs/linkNavTo').then(({ default: linkNavTo }) => {
    linkNavTo(itemLink)
  })
  // #endif
}

// 点击三级类目
const handleNavToDetail = (item: ApiCategoryData) => {
  const str = `platformCategoryThirdName=${item.name}&platformCategoryThirdId=${item.id}&isGoods=${true}`
  uni.navigateTo({
    url: `/basePackage/pages/searchRetrieve/SearchRetrieve?${str}`,
  })
}

defineExpose({
  handleBackTop,
  currentChooseIndex,
})
</script>

<template>
  <view
    :style="{
      width: 'calc(100vw - 200rpx)',
      height: $props.height + 'px',
      position: 'relative',
    }"
  >
    <view class="classificate">
      <view
        v-if="$props.info?.ads && $props.info?.ads?.length > 0 && $props.config.categoryShow"
        :style="{ width: '100%', height: '120px', paddingRight: '20rpx', marginTop: '10px', borderRadius: '10rpx' }"
      >
        <swiper
          :indicator-dots="false"
          :autoplay="composeDecorationStore().autoplay"
          :circular="true"
          :vertical="false"
          :interval="2000"
          :duration="500"
          :height="120"
        >
          <swiper-item
            v-for="(item, index) in $props.info?.ads || []"
            :key="index"
            style="height: 120px; border-radius: 15rpx"
            :data-index="index"
            @tap.stop="handleTap(index)"
          >
            <image :src="item.img" :data-index="index" style="height: 120px; border-radius: 15rpx" :lazy-load="true"></image>
          </swiper-item>
        </swiper>
      </view>

      <view style="position: relative">
        <view v-if="$props.config.navShow" style="padding-right: 17rpx; background-color: #f4f4f4">
          <scroll-view scroll-x enhanced :show-scrollbar="false" :scroll-left="scrollSecondOptions" @scroll="handleSecondScroll">
            <view class="classificate__unit">
              <view
                v-for="(item, index) in $props.info?.children || []"
                :key="item.id"
                class="classificate__unit-title"
                :style="{
                  backgroundColor: currentChooseIndex === index ? '#FFECEC' : '#fff',
                  color: currentChooseIndex === index ? '#FA3534' : '#232323',
                  borderColor: currentChooseIndex === index ? '#FA3534' : '#fff',
                }"
                @click="handleChooseUnit(index, item)"
              >
                {{ item.name }}
              </view>
              <view
                style="border-radius: 50%; padding: 16rpx 22rpx; text-align: center; background-color: #f4f4f4; font-size: 20rpx; color: #979797"
              ></view>
            </view>
          </scroll-view>
        </view>
        <q-icon
          v-if="$props.info?.children?.length && $props.info?.children?.length >= 3 && $props.config.navShow"
          name="icon-xiala-copy"
          size="20rpx"
          class="classificate__icon"
          @click="handleThreeShow"
        ></q-icon>
      </view>
      <view
        :style="{
          borderRadius: '30rpx',
          height: '100%',
          backgroundColor: '#f4f4f4',
          paddingRight: $props.large === 2 ? '20rpx' : '',
          marginTop: $props.config.navShow ? 0 : '10px',
        }"
        class="classificate__content-container"
      >
        <scroll-view
          scroll-y
          enhanced
          :show-scrollbar="false"
          :style="{
            height: $props.height - ($props.config.navShow ? 52 : 10) - ($props.config.categoryShow && $props.info?.ads?.length ? 130 : 0) + 'px',
            backgroundColor: $props.large === 2 ? '#fff' : '#f4f4f4',
            borderRadius: $props.large === 2 ? '10rpx' : '0',
          }"
          :scroll-top="scrollOptions.new"
          @scroll="handleScroll"
          @scrolltolower="scrolltolower"
        >
          <view style="height: calc(100% + 1rpx); display: flex; flex-direction: column; justify-content: space-between">
            <template v-if="$props.large === 2">
              <view v-if="list.length > 0" class="classificate__shop-list">
                <view v-for="item in list" :key="item.productId">
                  <class-item :info="item" :goodsShow="$props.goodsShow"> </class-item>
                </view>
              </view>
              <view
                v-else
                class="classificate__shop-list-empty"
                :style="{
                  height: $props.config?.categoryList[$props.slideNavIndex + 1]?.platformCategoryFirstName && isH5 ? 'calc(100% - 40px)' : '100%',
                }"
              >
                <view class="classificate__shop-list-empty-text">暂无商品</view>
              </view>
            </template>
            <view v-else ref="scrollContainer" class="classificate__content">
              <view v-for="item in $props.info?.children || []" :key="item.id" :data-category-id="item.id" class="classificate__content-item">
                <view class="classificate__content-item-title">{{ item.name }}</view>
                <view class="classificate__content-item-content">
                  <view
                    v-for="(val, key) in item.children"
                    :key="key"
                    class="classificate__content-item-content-item"
                    @click="handleNavToDetail(val)"
                  >
                    <image :src="val.categoryImg" class="classificate__content-item-content-item-image" />
                    <view class="classificate__content-item-content-item-name">{{ val.name }}</view>
                  </view>
                </view>
              </view>
            </view>
            <view v-if="$props.config?.categoryList[$props.slideNavIndex + 1]?.platformCategoryFirstName && isH5" class="classificate__bottom">
              <q-icon
                name="icon-youjiantou_huaban classificate__bottom__icon"
                :style="{ transform: 'rotate(90deg)', display: 'inline-block' }"
                size="26rpx"
                color="rgb(246, 85, 47)"
              ></q-icon>
              上拉继续浏览 {{ $props.config.categoryList[$props.slideNavIndex + 1].platformCategoryFirstName }}
            </view>
          </view>
        </scroll-view>
      </view>
    </view>
    <u-popup v-model="liveTwoShow" mode="top" border-radius="20" height="auto">
      <view class="classificate__three">
        <view class="classificate__three-item">
          <view class="classificate__three-item-title">
            <view class="classificate__three-item-title-text"> {{ $props.info?.name }}</view>
          </view>
          <view class="classificate__three-item-content">
            <view
              v-for="(item, index) in $props.info?.children"
              :key="item.id"
              class="classificate__three-item-content-item"
              :style="{
                backgroundColor: currentChooseIndex === index ? '#FFECEC' : '#f4f4f4',
                color: currentChooseIndex === index ? '#FA3534' : '#232323',
                borderColor: currentChooseIndex === index ? '#FA3534' : '#f4f4f4',
              }"
              @click="handleSecondChooseUnit(index, item)"
            >
              <view>{{ item.name }}</view>
            </view>
          </view>
        </view>
      </view>
    </u-popup>
  </view>
</template>

<style lang="scss" scoped>
.classificate {
  display: flex;
  flex-direction: column;
  height: 100%;

  &__content-container {
    :deep(.uni-scroll-view-content) {
      display: flex;
      flex-direction: column;
      justify-content: space-between;
    }
  }

  &__shop-list {
    padding: 20rpx 20rpx 0 20rpx;
    display: flex;
    flex-direction: column;

    &-empty {
      display: flex;
      justify-content: center;
      margin-right: 20rpx;

      &-text {
        font-size: 28rpx;
        color: #979797;
        font-family: Arial, sans-serif;
        font-weight: 400;
        line-height: 600rpx;
      }
    }
  }

  &__unit {
    display: flex;
    align-items: center;
    height: 52px;
  }

  &__icon {
    border-radius: 50%;
    width: 48rpx;
    line-height: 48rpx;
    text-align: center;
    background-color: #fff;
    color: #979797;
    position: absolute;
    box-shadow:
      -10rpx 0rpx 20rpx 10rpx rgba(255, 255, 255, 0.5),
      20rpx 0rpx 20rpx 0rpx rgba(255, 255, 255, 0.5),
      -50rpx 0rpx 20rpx 5rpx rgba(255, 255, 255, 0.5);
    right: 10rpx;
    top: 25rpx;
  }

  &__unit-title {
    padding: 8rpx 20rpx;
    border: 2rpx solid #fff;
    border-radius: 30rpx;
    font-size: 22rpx;
    margin-right: 20rpx;
    white-space: nowrap;
  }

  &__unit:nth-child(4) {
    margin-right: 0;
  }

  &__three {
    // #ifdef MP-WEIXIN
    margin-top: 87px;
    // #endif
    padding: 26rpx 31rpx 0 31rpx;
    &-item {
      &-title {
        font-size: 30rpx;
        color: #000;
        font-weight: 500;
        font-family: PingFang SC;
        width: 100%;
      }
      &-content {
        margin-top: 22rpx;
        display: flex;
        flex-wrap: wrap;
        gap: 26rpx;
        &-item {
          width: 30%;
          padding: 17rpx 0;
          text-align: center;
          border-radius: 30rpx;
          background-color: #f4f4f4;
          margin-bottom: 26rpx;
          font-size: 22rpx;
          border: 2rpx solid #f4f4f4;
          &:nth-child(3n) {
            margin-right: 0;
          }
        }
      }
    }
  }

  &__bottom {
    font-size: 26rpx;
    color: #979797;
    text-align: center;
    line-height: 40px;
    margin-top: auto;
  }

  &__content {
    background-color: #f4f4f4;
    display: flex;
    flex-direction: column;
    &-item {
      background-color: #fff;
      border-radius: 15rpx;
      margin-bottom: 14rpx;
      margin-right: 20rpx;
      padding: 20rpx 20rpx 26rpx 20rpx;
      &-title {
        font-size: 30rpx;
        color: #000;
        font-family: PingFang SC;
        font-weight: 500;
      }
      &-content {
        display: flex;
        flex-wrap: wrap;
        &-item {
          width: 25.5%;
          margin: 20rpx 0 0 0;
          display: flex;
          flex-direction: column;
          margin-right: 11.75%;
          &:nth-child(3n) {
            margin-right: 0;
          }
          &-image {
            width: 100%;
            height: 132rpx;
            border-radius: 10rpx;
          }
          &-name {
            width: 100%;
            font-size: 24rpx;
            color: #333;
            font-family: Arial, sans-serif;
            font-weight: 400;
            margin-top: 12rpx;
            text-align: center;
            @include utils-ellipsis();
          }
        }
      }
    }
  }
}
</style>
