<script setup lang="ts">
import type { PropType } from 'vue'
import ClassItem from './class-item.vue'
import type { ApiCategoryData, CommodityItem } from '@/basePackage/pages/merchant/types'
import type { GetShopInfoRes } from '@/apis/o2oshop/model'

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
    type: Array as PropType<CommodityItem[]>,
    default() {
      return []
    },
  },
  shopInfo: {
    type: Object as PropType<GetShopInfoRes & { id: Long }>,
    default() {
      return {}
    },
  },
})
const $emit = defineEmits(['listLoadMore'])

const scrolltolower = () => {
  $emit('listLoadMore')
}
const handleNavToRetrive = (id: string) => {
  uni.navigateTo({
    url: `/basePackage/pages/searchRetrieve/SearchRetrieve?shopId=${$props.shopInfo.id}&categoryThirdId=${id}&isGoods=true`,
  })
}
</script>

<template>
  <scroll-view scroll-y :style="{ height: $props.height + 'px' }" class="classificate" @scrolltolower="scrolltolower">
    <view v-for="item in $props.info.children" :key="item.id" class="classificate__unit">
      <view class="classificate__unit-title">{{ item.name }}</view>
      <view class="classificate__unit-content">
        <view v-for="ite in item.children" :key="ite.id" class="classificate__unit-content-item" @click="handleNavToRetrive(ite.id)">
          <image :src="ite.categoryImg" class="classificate__unit-content-item--image" />
          <view class="classificate__unit-content-item--name">{{ ite.name }}</view>
        </view>
      </view>
    </view>
    <div v-if="$props.large === 2" class="classificate__list--title">
      <div style="position: absolute; z-index: 10">热门商品</div>
      <div class="classificate__list--red"></div>
    </div>

    <view v-if="$props.large === 2" class="classificate__list">
      <class-item v-for="item in $props.list" :key="item.productId" :shop-info="$props.shopInfo" :is-large="$props.large" :info="item" />
    </view>
  </scroll-view>
</template>

<style lang="scss" scoped>
@include b(classificate) {
  position: relative;
  background: #fff;
  width: 570rpx;
  padding: 28rpx 10rpx 0 10rpx;
  box-sizing: border-box;
  background: #fff;
  color: #3c3c3c;
  &::-webkit-scrollbar {
    width: 0 !important;
  }
  @include e(unit) {
    width: 498rpx;
    margin: 0 auto;
    border-radius: 20rpx;
    margin-top: 20rpx;
    box-shadow: 0px 0px 15px -3px rgba(0, 0, 0, 0.25);
    // border: 1px solid rgba(0, 0, 0, 0.25);
    padding-left: 17rpx;
    padding-bottom: 10rpx;
  }
  @include e(unit-title) {
    line-height: 88rpx;
    font-weight: bold;
    font-size: 28rpx;
  }
  @include e(unit-content) {
    display: grid;
    grid-template-columns: repeat(3, 130rpx);
    grid-column-gap: 26rpx;
    grid-row-gap: 20rpx;
  }
  @include e(unit-content-item) {
    @include m(image) {
      width: 130rpx;
      height: 130rpx;
      border-radius: 20rpx;
    }
    @include m(name) {
      font-size: 22rpx;
      width: inherit;
      text-align: center;
    }
  }
  @include e(list) {
    @include m(title) {
      position: relative;
      margin-top: 30rpx;
      margin-bottom: 7px;
      width: 56px;
      height: 34rpx;
      font-size: 14px;
    }
    @include m(red) {
      position: absolute;
      height: 10rpx;
      width: 112rpx;
      border-radius: 20rpx;
      z-index: 90;
      bottom: -10rpx;
      background-color: #fa3534;
    }
  }
}
</style>
