<script lang="ts" setup>
import { computed, ref, type PropType } from 'vue'
import QIcon from '@/components/q-icon/q-icon.vue'
import DecorateList from './decorateList.vue'
import GoodList from './goodList.vue'
import { useNavBack, useStatusBar } from '@/hooks'
import { doCancelAttentionAndAttention } from '@/apis/concern'
import useCollectionDispatcher from '@/store/dispatcher/useCollectionDispatcher'
import type { GetShopInfoRes } from '@/apis/o2oshop/model'
import { Decimal } from 'decimal.js-light'

const $collectionDispatcherStore = useCollectionDispatcher()
const $props = defineProps({
  shopInfo: {
    type: Object as PropType<GetShopInfoRes & { id: Long }>,
    required: true,
  },
})
const follow = ref(new Decimal($props.shopInfo.follow))

const safeHeight = useBottomSafe()
const isConcern = ref($props.shopInfo.isFollow)
// 首页内swiper的index
const tabIdx = ref(0)
const statusBarHeight = useStatusBar()
const merchantNodeHeight = uni.upx2px(300)
const navNodeHeight = uni.upx2px(100)

const scrollViewHeight = computed(() => {
  return useScreenHeight().value - merchantNodeHeight - navNodeHeight - statusBarHeight.value - safeHeight.value
})
/**
 * 关注
 */
const handleConcern = async () => {
  var value = isConcern.value
  const { code, msg } = await doCancelAttentionAndAttention($props.shopInfo.name, $props.shopInfo.id, !value, $props.shopInfo.logo)
  if (code !== 200)
    return uni.showToast({
      title: `${msg ? msg : `${value ? '取消' : '关注'}失败`}`,
      icon: 'none',
    })
  uni.showToast({
    title: `${value ? '已取消' : '已关注'}`,
    icon: 'none',
  })
  isConcern.value = !value
  follow.value = follow.value.add(value ? -1 : 1)
  $collectionDispatcherStore.updateShopData()
}
/**
 * 切换tabs
 * @param {*} idx
 */
const handleChangeTab = (idx: number) => {
  tabIdx.value = idx
}
/**
 * 首页swiper切换
 */
const handleChangeSwiper = (e: { detail: { current: number } }) => {
  tabIdx.value = e.detail.current
}
const handleNavBack = () => {
  useNavBack()
}
const contentHeight = useScreenHeight()
const GoodListRef = ref()
const initList = () => {
  GoodListRef.value?.loadMore()
}

const goToShopInfo = () => {
  uni.navigateTo({
    url: `/basePackage/pages/merchant/StoreInformation?shopId=${$props.shopInfo.id}`, // 目标页面的路径
  })
}
</script>

<template>
  <view>
    <!-- 店铺头部 -->
    <view
      :style="{
        'background-image': `url(${$props.shopInfo.headBackground})`,
        height: statusBarHeight + merchantNodeHeight + 'px',
        paddingTop: statusBarHeight + 25 + 'px',
      }"
      class="merchant"
    >
      <view class="merchant__fir">
        <view class="merchant__fir-info">
          <q-icon class="merchant__fir-info--nav" color="#f0f0f0" name="icon-xiajiantou" size="30px" @click="handleNavBack" />
          <view class="merchant__fir-info-content" @click.stop="goToShopInfo">
            <image :src="shopInfo.logo" class="merchant__fir-info-content--img" mode="scaleToFill" />
            <view>
              <view class="merchant__fir-info-content--name">{{ shopInfo.name }}</view>
              <view class="merchant__fir-info-content--sale">{{ follow }}人关注</view>
              <view class="merchant__fir-info-content--sale">{{ shopInfo.productNum }}款在售商品</view>
            </view>
          </view>
        </view>
        <view :class="['merchant__fir-btn', isConcern && 'merchant__fir-btn--active']" @click="handleConcern"> {{ isConcern ? '已' : '' }}关注 </view>
      </view>
      <view class="merchant__sec">
        <view class="tab">
          <view :class="['tab__pane', tabIdx === 0 && 'tab__pane--active']" @click="handleChangeTab(0)">
            <view>首页</view>
          </view>
          <view :class="['tab__pane', tabIdx === 1 && 'tab__pane--active']" @click="handleChangeTab(1)">
            <view>商品</view>
          </view>
        </view>
      </view>
    </view>
    <scroll-view scroll-y :style="`height: ${contentHeight - merchantNodeHeight}px;`" @scrolltolower="initList">
      <DecorateList v-if="tabIdx === 0" :scrollHeight="scrollViewHeight" :shopId="$props.shopInfo.id" />
      <GoodList v-else ref="GoodListRef" :scrollHeight="scrollViewHeight" :shopInfo="$props.shopInfo" />
    </scroll-view>
  </view>
</template>

<style lang="scss" scoped>
@include b(merchant) {
  width: 100%;
  box-sizing: border-box;
  position: relative;
  background-repeat: no-repeat;
  z-index: 11;
  background-size: cover;
  background-position: center;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  &::after {
    content: ' ';
    position: absolute;
    left: 0;
    right: 0;
    top: 0;
    bottom: 0;
    background: rgba(17, 17, 17, 0.2);
    z-index: -1;
  }
  @include e(fir) {
    @include flex(space-between);
    padding: 0 14rpx 0 28rpx;
  }
  @include e(fir-btn) {
    width: 126rpx;
    height: 50rpx;
    background: #f54319;
    border-radius: 32rpx;
    font-size: 28rpx;
    color: #ffffff;
    line-height: 50rpx;
    text-align: center;
    @include m(active) {
      background: #ccc;
    }
  }
  @include e(fir-info) {
    @include flex();
    @include m(nav) {
      transform: rotate(90deg);
    }
  }
  @include e(fir-info-content) {
    margin-left: 16rpx;
    @include flex();
    @include m(img) {
      width: 108rpx;
      height: 108rpx;
      border-radius: 10rpx;
      margin-right: 10rpx;
    }
    @include m(name) {
      font-size: 24rpx;
      color: #fff;
    }
    @include m(sale) {
      color: #fff;
      font-size: 16rpx;
      padding: 12rpx 10rpx;
      // background-color: #fff;
      border-radius: 24rpx;
      margin-top: 10rpx;
      display: inline-block;
    }
  }
  @include e(sec) {
    width: 100%;
    height: 80rpx;
    @include flex(flex-start);
    background-color: rgba($color: #000000, $alpha: 0.2);
  }
  @include e(sec-search) {
    width: 160rpx;
    height: 50rpx;
    border-radius: 32rpx;
    line-height: 50rpx;
    text-align: center;
    background: rgba(255, 255, 255, 0.15);
    color: #fff;
    margin-left: 28rpx;
  }
}

@include b(tab) {
  @include flex();
  flex-shrink: 0;
  @include e(pane) {
    position: relative;
    padding: 0 20rpx;
    height: 80rpx;
    line-height: 80rpx;
    font-size: 28rpx;
    color: #fff;
    flex-shrink: 0;
    @include m(active) {
      &::after {
        content: '';
        width: 100%;
        height: 4rpx;
        background: #fff;
        border-radius: 2rpx;
        position: absolute;
        bottom: 4rpx;
        left: 0;
      }
    }
  }
}
</style>
