<script setup lang="ts">
import type { PropType } from 'vue'
import CouponCard from '@plugin/coupon/components/couponCard/coupon-card.vue'
import { isDiscountFn, formattingTime, formattingPrice, productTypeCn } from '@plugin/coupon/utils'
import type { CartApiCouponVO } from '@/apis/plugin/coupon/model'
import { type ApiCouponVO, ProductType } from '@/apis/plugin/coupon/model'

const $props = defineProps({
  couponList: {
    type: Array as PropType<CartApiCouponVO[]>,
    default: () => [],
  },
  name: {
    type: String,
    default: '',
  },
  title: {
    type: String,
    default: '优惠券',
  },
  isCar: { type: Boolean, default: false },
})
const emit = defineEmits(['receiveClick'])
const formattingName = (item: ApiCouponVO) => {
  if (item.productType === ProductType.ALL) {
    return '全场商品通用'
  }
  return `限${$props.name}可用`
}
</script>

<template>
  <view v-if="$props.couponList.length" class="sales">{{ $props.title }}</view>
  <view v-for="item in $props.couponList" :key="item.id" style="margin-bottom: 10rpx; padding: 0 10rpx">
    <coupon-card
      :name="formattingName(item)"
      text=""
      :not-used-status="item.claimedStatus"
      :height="170"
      :watermark="!!item.couponUserId"
      :type="item.type"
      :time="formattingTime(item)"
      :price="formattingPrice(item)"
      :required-amount="item.requiredAmount || '0'"
      :unit="isDiscountFn(item.type) ? '折' : '元'"
      :is-car="$props.isCar"
      @receive-click="emit('receiveClick', item)"
    >
      <view>
        <u-tag text="店铺" type="error" shape="circle" size="mini" border-color="transparent" />
        <text style="margin-left: 10rpx">{{ productTypeCn(item) }}</text>
      </view>
    </coupon-card>
  </view>
</template>

<style scoped lang="scss">
@include b(sales) {
  padding: 20rpx;
  color: rgba(102, 102, 102, 1);
  font-size: 24rpx;
}
</style>
