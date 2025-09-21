<template>
  <view class="goodsItem">
    <view class="goodsItem__img">
      <image :src="info.image" style="width: 128rpx; height: 128rpx" />
    </view>
    <view style="flex: 1">
      <view class="goodsItem__name">{{ info.productName }}</view>
      <view class="goodsItem__spec">
        <view class="goodsItem__price">
          <price :integral="info?.price" :sale-price="info?.salePrice" />
        </view>
        <text>x{{ info.num }}</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import type { PropType } from 'vue'
import type { IOrderList } from '@/pluginPackage/integral/api/types'
import price from '@/pluginPackage/integral/mall/components/price.vue'

defineProps({
  info: {
    type: Object as PropType<IOrderList>,
    default() {
      return {}
    },
  },
})
</script>

<style scoped lang="scss">
@include b(goodsItem) {
  display: flex;
  font-size: 24rpx;
  margin-top: 20rpx;
  color: #333;
  @include e(img) {
    width: 128rpx;
    height: 128rpx;
    margin-right: 16rpx;
  }
  @include e(name) {
    margin-top: 10rpx;
    width: 526rpx;
    @include utils-ellipsis(1);
  }
  @include e(spec) {
    display: flex;
    justify-content: space-between;
    color: #666666;
    margin-top: 30rpx;
  }
  @include e(price) {
    font-size: 28rpx;
    font-weight: 700;
    color: #fa3534;
  }
}
</style>
