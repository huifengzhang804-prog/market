<template>
  <view v-if="productList.length === 3" class="three-images">
    <image :src="productList?.[0]?.pic" class="three-images__big" />
    <view class="three-images__small">
      <image
        v-for="product in productList.slice(1)"
        :key="product.id"
        class="three-images__small--img"
        :src="product.pic"
        @click="jumpGoods(shopId, product.id)"
      />
    </view>
  </view>
  <scroll-view v-else scroll-x enhanced :show-scrollbar="false" style="width: 540rpx">
    <view class="other-images">
      <image
        v-for="product in productList"
        :key="product.id"
        :src="product.pic"
        :style="computedStyle"
        class="other-image margin"
        @click="jumpGoods(shopId, product.id)"
      />
    </view>
  </scroll-view>
</template>

<script lang="ts" setup>
import { computed } from 'vue'
import type { PropType } from 'vue'
import type { ProductItem } from '../types'
import { jumpGoods } from '@/utils/navigateToShopInfo'

const $props = defineProps({
  productList: {
    type: Array as PropType<ProductItem[]>,
    default: () => [],
  },
  shopId: {
    type: String as PropType<Long>,
    required: true,
  },
})

const computedStyle = computed(() => {
  if ($props.productList.length === 1) {
    return { width: '554rpx', height: '554rpx', borderRadius: '8rpx' }
  } else if ([2, 4].includes($props.productList.length)) {
    return { width: '268rpx', height: '268rpx', borderRadius: '8rpx' }
  } else if ($props.productList.length === 3) {
    return {}
  } else {
    return { width: '172rpx', height: '172rpx', borderRadius: '8rpx' }
  }
})
</script>

<style lang="scss" scoped>
@include b(three-images) {
  @include flex(space-between);
  @include e(big) {
    width: 364rpx;
    height: 366rpx;
    border-radius: 8rpx;
    flex-shrink: 0;
  }
  @include e(small) {
    width: 176rpx;
    height: 366rpx;
    @include flex(space-between);
    flex-direction: column;
    margin-left: 14rpx;
    @include m(img) {
      width: 176rpx;
      height: 176rpx;
      border-radius: 8rpx;
    }
  }
}
.other-images {
  display: flex;
  align-items: center;
  .other-image {
    flex-shrink: 0;
  }
}
@include b(margin) {
  margin-right: 14rpx;
  margin-bottom: 14rpx;
}
</style>
