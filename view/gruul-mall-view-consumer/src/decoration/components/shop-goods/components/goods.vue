<template>
  <view class="goods">
    <view class="goods__img" @click="handlejumpGoods">
      <!-- #ifndef H5 -->
      <image :src="cropImg(goodsItem.logo, 208, 208)" style="width: 100%; height: 100%" :lazy-load="true"></image>
      <!-- #endif -->
      <!-- #ifdef H5 -->
      <lazy-load>
        <image :src="cropImg(goodsItem.logo, 208, 208)" alt style="width: 100%; height: 100%"></image>
      </lazy-load>
      <!-- #endif -->
    </view>
    <view v-if="goodsItem.name" class="goods__name">{{ goodsItem.name.length > 6 ? goodsItem.name.slice(0, 6) : goodsItem.name }}</view>
    <view v-if="goodsItem.price" class="goods__price">
      <text v-if="String(goodsItem.price)?.includes('.')">{{ String(goodsItem.price).split('.')[0] }}.</text>
      <text v-if="String(goodsItem.price)?.includes('.')" style="font-size: 12px">{{ String(goodsItem.price).split('.')[1] }}</text>
      <text v-else>{{ goodsItem.price }}</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import type { PropType } from 'vue'
import { goods } from '../shop-goods-default'
import { jumpGoods } from '@/utils/navigateToShopInfo'
import { cropImg } from '@/utils'
import LazyLoad from '@/components/lazy-load/lazy-load.vue'

const props = defineProps({
  goodsItem: {
    type: Object as PropType<(typeof goods)[0]>,
    default: goods[0],
  },
  shopId: {
    type: String as PropType<Long>,
    default: '',
  },
})

/**
 * 跳转到商品详情
 */
const handlejumpGoods = () => {
  const { shopId, goodsItem } = props
  jumpGoods(shopId, goodsItem.id)
}
</script>

<style scoped lang="scss">
@include b(goods) {
  width: 100%;
  margin-bottom: 22rpx;
  background-color: #fafafa;
  border-radius: 10rpx;
  padding-bottom: 10rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  @include e(name) {
    color: #333;
    font-size: 26rpx;
    overflow: hidden;
    width: 90%;
    text-align: center;
    text-overflow: clip;
    white-space: nowrap;
    margin: 18rpx 0 6rpx;
    font-family: Arial, sans-serif;
    font-weight: 400;
  }
  @include e(img) {
    height: 220rpx;
    width: 100%;
    border-radius: 10rpx;
    border: 1px solid #f9f9f9;
    overflow: hidden;
  }
  @include e(price) {
    padding: 0 10rpx;
    text-align: center;
    color: #000;
    font-size: 28rpx;
    font-weight: 500;
    font-family: Arial, sans-serif;
    &::before {
      content: '￥';
      font-size: 28rpx;
      font-weight: 400;
      font-family: Arial, sans-serif;
    }
  }
}
</style>
