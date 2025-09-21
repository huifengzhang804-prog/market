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
    <view v-if="goodsItem.name" class="goods__name">{{ goodsItem.name }}</view>
    <view v-if="goodsItem.price" class="goods__price">{{ goodsItem.price && (+goodsItem.price).toFixed(2) }}</view>
  </view>
</template>

<script setup lang="ts">
import type { PropType } from 'vue'
import { goods } from '../../shop-goods-default'
import { jumpGoods } from '@/utils/navigateToShopInfo'
import { cropImg } from '@/utils'
import LazyLoad from '@/components/lazy-load/lazy-load.vue'

const props = defineProps({
  goodsItem: {
    type: Object as PropType<(typeof goods)[0]>,
    default: goods[0],
  },
  shopId: {
    type: String,
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
  width: 210rpx;
  display: inline-block;
  margin: 0 12rpx;
  @include e(name) {
    color: rgba(16, 16, 16, 1);
    font-size: 26rpx;
    @include utils-ellipsis(2);
    margin: 18rpx 0 6rpx;
    white-space: wrap;
    height: 34rpx;
  }
  @include e(img) {
    height: 200rpx;
    width: 200rpx;
    border-radius: 8rpx;
    border: 1px solid #f9f9f9;
    overflow: hidden;
  }
  @include e(price) {
    color: #f54319;
    font-size: 28rpx;
    font-weight: 600;
    &::before {
      content: '￥';
      font-weight: 400;
      font-size: 20rpx;
    }
  }
}
</style>
