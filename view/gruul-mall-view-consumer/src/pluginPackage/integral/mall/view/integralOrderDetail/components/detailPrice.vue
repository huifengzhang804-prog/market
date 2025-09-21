<template>
  <view class="baseCard priceMain">
    <view class="priceMain__title">价格明细</view>
    <view class="priceMain__main">
      <view class="priceMain__main-line">
        <text> 商品总价 </text>
        <text> ￥{{ divTenThousand(order.salePrice) }} </text>
      </view>
      <view class="priceMain__main-line">
        <text> 运费 </text>
        <text> ￥{{ divTenThousand(order.freightPrice) }} </text>
      </view>
    </view>
    <view class="priceMain__total">
      <text style="font-size: 30rpx; font-weight: 700"> 应付款 </text>
      <view>
        <price :integral="order.price" :sale-price="totalPrice" />
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { type PropType, computed } from 'vue'
import price from '../../../components/price.vue'
import type { IOrderList } from '@/pluginPackage/integral/api/types'
const props = defineProps({
  order: {
    type: Object as PropType<IOrderList>,
    default: () => ({}),
  },
})
const { divTenThousand } = useConvert()

const totalPrice = computed(() => String(+props.order.salePrice + +props.order.freightPrice))
</script>

<style scoped lang="scss">
@include b(priceMain) {
  padding-left: 30rpx !important;
  @include e(title) {
    font-size: 26rpx;
    font-weight: 700;
  }
  @include e(main) {
    padding: 20rpx 20rpx 10rpx;
  }
  @include e(main-line) {
    margin-bottom: 10rpx;
    display: flex;
    justify-content: space-between;
  }
  @include e(total) {
    display: flex;
    justify-content: space-between;
  }
}
</style>
