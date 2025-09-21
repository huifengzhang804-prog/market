<template>
  <view class="goods-item" @click="handleNavToDetail(goodsInfo.productId, goodsInfo.shopId)">
    <view class="goods-item__image">
      <image :src="goodsInfo.pic" alt :lazy-load="true" mode="aspectFill"></image>
    </view>
    <good-item-foot :form-data="formData" :goods-info="goodsInfo" />
  </view>
</template>

<script lang="ts" setup>
import type { PropType } from 'vue'
import type { GoodFormData } from '../../types'
import { jumpGoods } from '@/utils/navigateToShopInfo'
import type { GoodItemType } from '../good'
import goodItemFoot from './good-item-foot.vue'

defineProps({
  goodsInfo: {
    type: Object as PropType<GoodItemType>,
    default: () => ({}),
  },
  formData: {
    type: Object as PropType<GoodFormData>,
    default: () => ({}),
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
.goods-item {
  border-radius: 16rpx;
  margin-top: 20rpx;
  overflow: hidden;
  &__image {
    height: 406rpx;
    image {
      width: 100%;
      height: 100%;
    }
  }
}
</style>
