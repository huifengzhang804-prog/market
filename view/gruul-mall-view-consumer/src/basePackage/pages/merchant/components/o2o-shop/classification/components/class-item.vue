<script setup lang="ts">
import type { PropType } from 'vue'
import ClassItemInfo from './class-item-info.vue'
import type { CommodityItem } from '@/basePackage/pages/merchant/types'
import type { GetShopInfoRes } from '@/apis/o2oshop/model'

const $props = defineProps({
  isLarge: {
    type: Number,
    default: 1,
  },
  info: {
    type: Object as PropType<CommodityItem>,
    default() {
      return {}
    },
  },
  shopInfo: {
    type: Object as PropType<GetShopInfoRes & { id: Long }>,
    default() {
      return {}
    },
  },
})

const handleNavTodetail = () => {
  uni.navigateTo({
    url: `/pluginPackage/o2o-goods/o2o-goods?goodId=${$props.info.id}&shopId=${$props.shopInfo.id}`,
  })
}
</script>

<template>
  <view v-if="$props.isLarge === 3" class="large" :style="{ marginBottom: 20 + 'rpx' }" @click="handleNavTodetail">
    <image class="large__image" :src="$props.info.pic" />
    <class-item-info height="108rpx" :info="$props.info" />
  </view>
  <view v-else class="small" :style="{ marginBottom: 20 + 'rpx' }" @click="handleNavTodetail">
    <image class="small__image" :src="$props.info.pic" />
    <class-item-info :info="$props.info" width="300rpx" height="160rpx" />
  </view>
</template>
<style lang="scss" scoped>
@include b(large) {
  margin-bottom: 14rpx;
  padding-bottom: 16rpx;
  width: 498rpx;
  margin: 0 auto;
  box-shadow: 0px 0px 10px -3px rgba(0, 0, 0, 0.25);
  border-radius: 10rpx;
  @include e(image) {
    display: block;
    width: 498rpx;
    height: 370rpx;
    border-radius: 10rpx 10rpx 0 0;
    margin-bottom: 14rpx;
  }
}
@include b(small) {
  margin-bottom: 30rpx;
  width: 498rpx;
  margin: 0 auto;
  box-shadow: 0px 0px 10px -3px rgba(0, 0, 0, 0.25);
  border-radius: 10rpx;
  @include flex(space-between);
  @include e(image) {
    width: 184rpx;
    height: 184rpx;
    border-radius: 10rpx 0 0 10rpx;
    margin-right: 10rpx;
  }
}
</style>
