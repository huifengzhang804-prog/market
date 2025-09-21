<script lang="ts" setup>
import type { PropType } from 'vue'
import type { ApiOrder, ShopOrder } from '@/pluginPackage/order/orderList/types'
import { orderStatusPlus } from '@/hooks'

/**
 * 商铺信息导航
 * @property {isDetail} 详情展示方式 默认列表
 */
defineProps({
  isDetail: {
    type: Boolean,
    default: false,
  },
  info: {
    type: Object as PropType<ShopOrder>,
    default() {
      return {}
    },
  },
  order: { type: Object as PropType<ApiOrder>, default: () => ({}) },
  customStyle: {
    type: Object,
    default() {
      return {}
    },
  },
  showIc: {
    type: Boolean,
    default: true,
  },
})

const handleNavToMerchant = (shopId: Long) => {
  uni.navigateTo({
    url: `/basePackage/pages/merchant/Index?shopId=${shopId}`,
  })
}
</script>

<template>
  <view :style="customStyle" class="shopNav__item-header">
    <view class="shopNav__item-header--left" @click.stop="handleNavToMerchant(info.shopId)">
      <u-image :height="40" :src="info.shopLogo" :width="40" shape="circle" />
      <view class="shopNav__headTitle">
        <text class="f13 fontBold shopNav__headTitle--name">{{ info.shopName }}</text>
        <u-icon class="f13" color="#999999" name="arrow-right" style="padding-left: 2rpx"></u-icon>
      </view>
    </view>
    <view v-if="!isDetail" class="shopNav__item-header--right" style="color: #333">
      {{ orderStatusPlus(info, order, showIc).desc }}
    </view>
  </view>
</template>

<style lang="scss" scoped>
@include b(shopNav) {
  @include e(item-header) {
    padding: 0 20rpx;
    background: #fff;
    @include flex(space-between);
    height: 90rpx;
    @include m(left) {
      flex: 1;
      @include flex(flex-start);
      @include e(headTitle) {
        flex: 1;
        width: 0;
        @include flex(flex-start);
        @include m(name) {
          padding-left: 6rpx;
          //max-width: 20%;
          @include utils-ellipsis(1);
        }
      }
    }
    @include m(right) {
      color: $qszr-red;
    }
  }
}
</style>
