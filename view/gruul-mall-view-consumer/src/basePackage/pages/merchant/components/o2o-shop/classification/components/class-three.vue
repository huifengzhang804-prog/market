<script setup lang="ts">
import { type PropType, ref } from 'vue'
import ClassItem from './class-item.vue'
import type { ApiCategoryData, DeCategoryType, CommodityItem } from '@/basePackage/pages/merchant/types'
import type { GetShopInfoRes } from '@/apis/o2oshop/model'

const $props = defineProps({
  height: {
    type: Number,
    default: 1000,
  },
  firstCateList: {
    type: Object as PropType<ApiCategoryData>,
    default() {
      return {}
    },
  },
  config: {
    type: Object as PropType<DeCategoryType>,
    default() {
      return {}
    },
  },
  currentChooseIndex: {
    type: Number,
    default: 0,
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
const $emit = defineEmits(['changeTab', 'listLoadMore'])
const uDropdownRef = ref()
const uDropdownShow = ref(false)

const hanleChangeTab = (idx: number) => {
  $emit('changeTab', idx)
}
const scrolltolower = () => {
  $emit('listLoadMore')
}
const handledDropdownOpen = () => {
  uDropdownShow.value = true
}
const handledDropdownClose = () => {
  uDropdownShow.value = false
}
const hanleDropdownChangeTab = (idx: number) => {
  uDropdownRef.value.close()
  hanleChangeTab(idx)
}
</script>

<template>
  <view :style="{ height: $props.height + 'px' }" class="classificate">
    <view class="container">
      <u-dropdown ref="uDropdownRef" border-radius="15" class="dropdown" @open="handledDropdownOpen" @close="handledDropdownClose">
        <u-dropdown-item title="">
          <view class="dropdown-box">
            <text
              v-for="(item, index) in firstCateList.children"
              :key="item.id"
              :style="{
                borderBottom: $props.currentChooseIndex === index ? '2px solid #FA3534' : '',
                // color: $props.currentChooseIndex === index ? $props.config.fontSelected : $props.config.fontNotSelected,
                // background: $props.currentChooseIndex === index ? $props.config.bgSelected : $props.config.bgNotSelected,
              }"
              class="classificate__sort-item classificate__sort-item-y"
              @click="hanleDropdownChangeTab(index)"
            >
              {{ item.name }}
            </text>
          </view>
        </u-dropdown-item>
      </u-dropdown>
      <view class="align-cate">
        <scroll-view v-show="!uDropdownShow" scroll-x enhanced :show-scrollbar="false" style="white-space: nowrap">
          <text
            v-for="(item, index) in firstCateList.children"
            :key="item.id"
            :style="{
              borderBottom: $props.currentChooseIndex === index ? '2px solid #FA3534' : '',
              // color: $props.currentChooseIndex === index ? $props.config.fontSelected : $props.config.fontNotSelected,
              // background: $props.currentChooseIndex === index ? $props.config.bgSelected : $props.config.bgNotSelected,
            }"
            class="classificate__sort-item"
            @click="hanleChangeTab(index)"
          >
            {{ item.name }}
          </text>
        </scroll-view>
        <text v-show="uDropdownShow" class="msg">更多精彩好物等您发现~</text>
      </view>
    </view>
    <view class="classificate__list">
      <scroll-view scroll-y :style="{ height: $props.height - 50 + 'px' }" scroll-left="0" @scrolltolower="scrolltolower">
        <class-item v-for="item in $props.list" :key="item.id" :is-large="$props.config.style" :shop-info="$props.shopInfo" :info="item" />
      </scroll-view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
@include b(container) {
  position: relative;
}
@include b(dropdown) {
  background: rgba(0, 0, 0, 0);
}
@include b(dropdown-box) {
  display: flex;
  flex-wrap: wrap;
  background: #fff;
  // padding-bottom: 50rpx;
}
@include b(classificate) {
  width: 538rpx;
  position: relative;
  overflow: hidden;
  @include e(sort) {
    padding: 0 16rpx;
    box-sizing: border-box;
    white-space: nowrap;
    // @include flex(flex-start);
  }
  @include e(sort-item) {
    white-space: nowrap;
    font-size: 28rpx;
    padding: 8rpx 12rpx;
    background: #fff;
    margin-right: 20rpx;
  }
  @include e(sort-item-y) {
    margin: 15rpx;
  }
  @include e(arrow) {
    width: 52rpx;
    height: 52rpx;
    background: #f2f2f2;
    opacity: 0.8;
    position: absolute;
    z-index: 9;
    right: 0;
    top: 0;
    @include flex();
  }
  @include e(list) {
    padding: 20rpx 0rpx 20px 17rpx;
    @include m(tips) {
      // width
    }
  }
}
@include b(align-cate) {
  background: #fff;
  position: absolute;
  top: 0;
  height: 80rpx;
  line-height: 80rpx;
  width: calc(100% - 60rpx);
  z-index: 99;
  @include m(text) {
    font-size: 24rpx;
    color: #606266;
    text-indent: 1em;
  }
}
@include b(msg) {
  font-size: 24rpx;
  color: #606266;
  margin-left: 20rpx;
}
:deep(.u-dropdown__menu__item) {
  justify-content: flex-end;
  padding: 0 20rpx 0 0;
}
</style>
