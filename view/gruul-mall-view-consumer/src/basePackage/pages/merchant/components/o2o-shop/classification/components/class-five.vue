<script setup lang="ts">
import { watch, ref, inject } from 'vue'

import type { ApiCategoryData, DeCategoryType, CommodityItem } from '@/pages/platform/types'
import QIcon from '@/components/q-icon/q-icon.vue'
import type { PropType } from 'vue'
type ShopInfoKey = 'id' | 'logo' | 'name' | 'newTips' | 'status' | 'headBackground'
type ShopInfoType = Record<ShopInfoKey, string>

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
  config: {
    type: Object as PropType<DeCategoryType>,
    default() {
      return {}
    },
  },
  shopInfo: {
    type: Object as PropType<ShopInfoType>,
    default() {
      return {}
    },
  },
})
const $emit = defineEmits(['changeTab', 'listLoadMore'])
const { divTenThousand } = useConvert()
const showmore = ref(false)
const firstList = ref<ApiCategoryData>($props.firstCateList)

watch(
  () => $props.firstCateList,
  () => {
    initfirstCateList()
    console.log('firstList', firstList.value)
  },
)

async function initfirstCateList() {
  firstList.value = $props.firstCateList
  if (firstList.value?.children && firstList.value?.children?.length > 10) {
    firstList.value.children.push({
      id: '-1',
      categoryImg: '',
      name: '收起',
      children: [],
    })
  }
}
const hanleChangeTab = (idx: number) => {
  $emit('changeTab', idx)
}
const scrolltolower = () => {
  $emit('listLoadMore')
}
const handleNavTodetail = (info: CommodityItem) => {
  uni.navigateTo({
    url: `/pluginPackage/o2o-goods/o2o-goods?shopId=${info.shopId}&goodId=${info.id}`,
  })
}
const handleNavToRetrive = (id: string) => {
  uni.navigateTo({
    url: `/basePackage/pages/searchRetrieve/SearchRetrieve?shopId=${$props.shopInfo.id}&categorySecondId=${id}&isGoods=true`,
  })
}
const selectSpec = inject('selectSpec') as (info: string) => void
</script>

<template>
  <div class="classificate">
    <div class="classificate__sort">
      <div
        v-for="(item, index) in $props.config.categoryList"
        :key="item.platformCategoryFirstId"
        class="classificate__sort-item"
        @click="hanleChangeTab(index)"
      >
        <div style="position: relative; z-index: 99">{{ item.name }}</div>
        <div v-if="$props.currentChooseIndex === index" class="classificate__sort-item--red"></div>
      </div>
    </div>
    <scroll-view scroll-y :style="{ height: $props.height - 40 + 'px' }" @scrolltolower="scrolltolower">
      <div class="classificate__sec" :style="showmore ? {} : { overflow: 'hidden', maxHeight: '344rpx' }">
        <div v-for="(item, index) in firstList.children" :key="item.id" @click="handleNavToRetrive(item.id)">
          <div
            v-if="
              (index !== 9 || firstList.children?.length === 10 || showmore) &&
              ((firstList.children && firstList.children?.length <= 10) || firstList.children?.length !== index + 1)
            "
            class="classificate__sec-item"
          >
            <image :src="item.categoryImg" class="classificate__sec-item--pic"></image>
            <div class="classificate__sec-item--name">{{ item.name }}</div>
          </div>
          <div
            v-else-if="showmore && firstList.children && firstList.children?.length > 10 && firstList.children?.length === index + 1"
            class="classificate__sec-item"
            @click.stop="showmore = false"
          >
            <div class="classificate__sec-item--pic">
              <q-icon size="84rpx" color="#DEDEDE;" name="icon-jiantoushang" />
            </div>
            <div class="classificate__sec-item--name" style="color: #999">收起</div>
          </div>
          <div
            v-else-if="!showmore && firstList.children && firstList.children?.length > 10"
            class="classificate__sec-item"
            @click.stop="showmore = true"
          >
            <div class="classificate__sec-item--pic">
              <q-icon size="84rpx" color="#DEDEDE;" name="icon-xiajiantou" />
            </div>
            <div class="classificate__sec-item--name" style="color: #999">查看更多</div>
          </div>
        </div>
      </div>
      <div class="classificate__list--title">
        <div style="position: absolute; z-index: 99">热门商品</div>
        <div class="classificate__list--red"></div>
      </div>
      <div class="classificate__list">
        <div v-for="item in $props.list" :key="item.productId" class="classificate__list-item" @click="handleNavTodetail(item)">
          <image :src="item.pic" class="classificate__list-item--pic"></image>
          <div class="classificate__list-item-info">
            <div class="classificate__list-item-info--name">{{ item.name }}</div>
            <view>
              <view class="bottom">
                <view class="bottom__price">{{ divTenThousand(item.salePrices[0]) }}</view>
                <view style="height: 70rpx; width: 70rpx; line-height: 70rpx; text-align: center" @click.stop="selectSpec(item.id)">
                  <q-icon name="icon-gouwuche8" size="40rpx" color="#FA3534" />
                </view>
              </view>
            </view>
          </div>
        </div>
      </div>
    </scroll-view>
  </div>
</template>

<style lang="scss" scoped>
@include b(bottom) {
  width: inherit;
  padding-right: 40rpx;
  @include flex(space-between);
  @include e(price) {
    font-size: 16px;
    color: #dd3324;
    &::before {
      content: '￥';
      display: inline-block;
      font-size: 10px;
      color: #dd3324;
    }
  }
}
@include b(classificate) {
  position: relative;
  @include e(sort) {
    padding: 0 8px;
    background: #fff;
    box-sizing: border-box;
    @include flex(flex-start);
    overflow-x: auto;
    &::-webkit-scrollbar {
      display: none;
      width: 0 !important;
    }
  }
  @include e(sort-item) {
    flex-shrink: 0;
    font-size: 14px;
    padding: 4px 6px;
    background: #fff;
    margin-right: 10px;
    cursor: pointer;
    position: relative;
    @include m(red) {
      position: absolute;
      height: 10rpx;
      width: calc(100% - 25rpx);
      border-radius: 20rpx;
      bottom: 10rpx;
      background-color: #fa3534;
    }
  }
  @include e(sec) {
    width: 710rpx;
    background: #ffffff;
    border-radius: 20rpx;
    margin: 0 auto;
    display: flex;
    flex-wrap: wrap;
    margin: 20rpx 10rpx 30rpx 10rpx;
    padding: 20rpx 22rpx;
    gap: 60rpx;
  }
  @include e(sec-item) {
    width: 84rpx;
    display: flex;
    flex-direction: column;
    align-items: center;
    @include m(pic) {
      width: 84rpx;
      height: 84rpx;
      margin-bottom: 10px;
      border-radius: 50%;
    }
    @include m(name) {
      overflow: hidden;
      width: 130rpx;
      color: #333333;
      font-size: 12px;
      font-weight: 500;
      text-align: center;
    }
  }
  @include e(list) {
    width: 355px;
    margin: 15px auto 0;
    display: flex;
    justify-content: space-between;
    flex-wrap: wrap;

    @include m(title) {
      position: relative;
      margin-top: 30rpx;
      margin-bottom: 7px;
      width: 56px;
      height: 34rpx;
      font-size: 14px;
      margin-left: 20rpx;
    }
    @include m(red) {
      position: absolute;
      height: 10rpx;
      width: 112rpx;
      border-radius: 20rpx;
      z-index: 90;
      bottom: 0;
      background-color: #fa3534;
    }
  }
  @include e(list-item) {
    background: #fff;
    width: 172px;
    height: 219px;
    margin-bottom: 14rpx;
    border-radius: 20rpx;
    @include m(pic) {
      border-radius: 20rpx 20rpx 0 0;
      width: 172px;
      height: 163px;
    }
  }
  @include e(list-item-info) {
    height: 59px;
    padding: 10px;
    font-size: 11px;
    @include m(name) {
      width: 155px;
      margin-bottom: 6rpx;
      @include utils-ellipsis();
    }
    @include m(price) {
      color: #fa3534;
    }
  }
}
</style>
