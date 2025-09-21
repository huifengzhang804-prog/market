<template>
  <view>
    <!-- 常规商品展示价格 s-->
    <view class="line">
      <O2oSellingInfo :good-info="props.info" :name="props.info?.name" :sku="props.currentChoosedSku"> </O2oSellingInfo>
      <view class="line-describe"> {{ props.info?.saleDescribe }} </view>
    </view>
    <!-- 常规商品展示价格 e-->
    <!-- 会员信息 商品参数 优惠券 s -->
    <O2oInfo :info="info" :o2o-shop-id="o2oShopId" :current-choosed-sku="currentChoosedSku" />
    <!-- 会员信息 商品参数 优惠券 e -->
  </view>
</template>

<script setup lang="ts">
import O2oSellingInfo from '@/pluginPackage/o2o-goods/components/o2o-selling-info.vue'
import O2oInfo from '@/pluginPackage/o2o-goods/components/o2o-info.vue'
import type { ProductResponse, StorageSku } from '@/pluginPackage/goods/commodityInfo/types'
import type { PropType } from 'vue'

const props = defineProps({
  info: {
    type: Object as PropType<ProductResponse>,
    default() {
      return {}
    },
  },
  sku: {
    type: Array as PropType<StorageSku[]>,
    default() {
      return []
    },
  },
  currentChoosedSku: {
    type: Object as PropType<StorageSku>,
    default() {
      return {}
    },
  },
  o2oShopId: {
    type: String,
    default() {
      return ''
    },
  },
})
</script>

<style scoped lang="scss">
@include b(line) {
  padding: 0 32rpx;
  padding-bottom: 30rpx;
  background: #fff;
  @include e(name) {
    font-size: 32rpx;
    line-height: 52rpx;
    color: #1e1c1c;
  }
  @include e(describe) {
    margin-top: 10rpx;
    color: #fa6134;
    font-size: 24rpx;
    @include utils-ellipsis(1);
  }
  @include e(ctx) {
    @include flex(space-between);
    margin-top: 18rpx;
  }
  @include e(ctx-left) {
    font-size: 26rpx;
    @include flex();
    @include m(title) {
      font-weight: bold;
    }
    @include m(item) {
      color: #7c7c7c;
      margin-left: 30rpx;
      font-weight: bold;
    }
    @include m(red) {
      color: #fb375e;
    }
  }
  @include e(ctx-right) {
    color: #7c7c7c;
    & > .icon {
      margin-right: 8rpx;
    }
  }
  @include e(desc) {
    padding: 16rpx;
    background: rgb(228, 228, 228);
    border-radius: 10rpx;
    color: #6c6a6a;
    word-break: break-all;
  }
}
</style>
