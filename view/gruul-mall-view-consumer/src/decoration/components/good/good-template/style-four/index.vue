<template>
  <view class="goods-item" :class="{ ml0: itemIndex === 0 }" style="width: 304rpx" @click="handleNavToDetail(item.productId, item.shopId)">
    <view class="goods-item__image">
      <!-- #ifndef H5 -->
      <image :src="cropImg(item.pic, 304, 304)" style="width: 100%; height: 100%" mode="aspectFill" :lazy-load="true"></image>
      <!-- #endif -->
      <!-- #ifdef H5 -->
      <lazy-load>
        <image :src="cropImg(item.pic, 304, 304)" mode="aspectFill" alt style="width: 100%; height: 100%"></image>
      </lazy-load>
      <!-- #endif -->
    </view>
    <GoodItemFoot :goods-info="item" :form-data="formData" />
  </view>
</template>

<script lang="ts" setup>
import GoodItemFoot from '../good-item-foot.vue'
import { jumpGoods } from '@/utils/navigateToShopInfo'
import { cropImg } from '@/utils'
import type { PropType } from 'vue'
import type { GoodItemType } from '../../good'
import type { GoodFormData } from '../../../types'
import LazyLoad from '@/components/lazy-load/lazy-load.vue'

defineProps({
  item: {
    type: Object as PropType<GoodItemType>,
    default: () => {},
  },
  formData: {
    type: Object as PropType<GoodFormData>,
    default: () => ({}),
  },
  itemIndex: {
    type: Number,
    default: 0,
  },
})
const handleNavToDetail = (id: Long, shopId: Long, instruction?: number) => {
  let url = `/pluginPackage/goods/commodityInfo/InfoEntrance?goodId=${id}&shopId=${shopId}`
  if (instruction) {
    url += `&instruction=${instruction}`
    uni.navigateTo({
      url,
    })
  } else {
    jumpGoods(shopId, id)
  }
}
</script>

<style lang="scss" scoped>
@import './index.scss';
@include b(goods-item) {
  display: flex;
  margin-left: 20rpx;
  flex-direction: column;
  justify-content: space-between;
  &__image {
    width: 304rpx;
    height: 304rpx;
    border-radius: 16rpx;
    overflow: hidden;
    image {
      width: 100%;
      height: 100%;
    }
  }
}
@include b(ml0) {
  margin-left: 0;
}
</style>
