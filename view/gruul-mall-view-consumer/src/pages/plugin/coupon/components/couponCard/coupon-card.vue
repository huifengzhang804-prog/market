<script setup lang="ts">
import type { PropType } from 'vue'
import CardLeftTopView from '@/pages/plugin/coupon/components/couponCard/card-left-head.vue'
import CouponPriceInfo from '@/pages/plugin/coupon/components/couponCard/card-price-info.vue'
import CardMainInfo from '@/pages/plugin/coupon/components/couponCard/card-main-info.vue'
import { couponStyleConfig, couponQueryStatusCn, claimedStatus } from '@plugin/coupon/utils'
import type { CouponJointType, CouponQueryStatusJointType } from '@/apis/plugin/coupon/model'
import { CLAIMED_STATUS } from '@/apis/plugin/coupon/model'

const $props = defineProps({
  text: {
    type: String,
    default: '折扣券',
  },
  watermark: {
    type: Boolean,
    default: false,
  },
  name: {
    type: String,
    default: '', // 限店铺名称部分商品可用
  },
  permissions: {
    type: String,
    default: '',
  },
  time: {
    type: String,
    default: '2022.03.26-2022.07.14',
  },
  type: {
    type: String as PropType<CouponJointType>,
    default: 'PRICE_REDUCE',
  },
  unit: {
    type: String as PropType<'元' | '折'>,
    default: '元',
  },
  price: { type: [String, Number], default: '0' },
  requiredAmount: { type: String, default: '0' },
  // 有用状态
  notUsedStatus: {
    type: String as PropType<keyof typeof CLAIMED_STATUS>,
    default: 'CLAIM',
  },
  // 没用状态
  usedStatus: {
    type: String as PropType<CouponQueryStatusJointType>,
    default: 'UNCLAIMED',
  },
  height: {
    type: [String, Number],
    default: '184',
  },
  status: {
    type: String as PropType<CouponQueryStatusJointType>,
    default: 'UNCLAIMED',
  },
  // 是否购物车
  isCar: { type: Boolean, default: false },
  // 是否领券中心
  couponCenter: { type: Boolean, default: false },
})
const $emit = defineEmits(['receiveClick', 'maskClick'])
</script>

<template>
  <view
    class="card"
    :style="{ color: couponStyleConfig[$props.type].color, borderColor: couponStyleConfig[$props.type].border, height: $props.height + 'rpx' }"
  >
    <CardLeftTopView :msg="$props.text" />
    <CouponPriceInfo
      style="height: 100%"
      :background-c="couponStyleConfig[$props.type].backgroundColor"
      :type="$props.type"
      :unit="$props.unit"
      :price="$props.price"
      :required-amount="$props.requiredAmount || '0'"
    />
    <CardMainInfo
      :watermark="$props.watermark"
      :name="$props.name"
      :permissions="$props.permissions"
      :time="$props.time"
      :used-status="$props.usedStatus"
      :status="$props.status"
      :not-used-status="$props.notUsedStatus"
      :coupon-center="$props.couponCenter"
      style="flex: 1; height: 100%"
      @receive-click="$emit('receiveClick', $event)"
    >
      <slot name="default"></slot>
    </CardMainInfo>
    <mask
      v-if="$props.isCar && !claimedStatus[$props.notUsedStatus].isUseCoupon"
      class="card-mask"
      @click="$emit('maskClick', couponQueryStatusCn[$props.usedStatus])"
    />
    <mask
      v-if="$props.couponCenter && !claimedStatus[$props.notUsedStatus].isUseCoupon"
      class="card-mask"
      @click="$emit('maskClick', couponQueryStatusCn[$props.usedStatus])"
    />
    <mask
      v-else-if="['已使用', '已过期'].includes(couponQueryStatusCn[$props.usedStatus])"
      class="card-mask"
      @click="$emit('maskClick', couponQueryStatusCn[$props.usedStatus])"
    />
  </view>
</template>

<style scoped lang="scss">
@include b(card) {
  position: relative;
  border: 1rpx solid #ffe5d1;
  border-radius: 16rpx;
  @include flex;
  justify-content: space-between;
  box-sizing: content-box;
}
@include b(card-mask) {
  border-radius: 16rpx;
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  top: 0;
  z-index: 2;
  opacity: 0.5;
  background-color: #fff;
}
@include b(watermark) {
  position: absolute;
  top: 1rpx;
  right: 1rpx;
  width: 80rpx;
  height: 80rpx;
  background-color: rgba(252, 236, 237, 1);
  font-size: 14rpx;
  text-align: center;
  border: 2px solid #ffb3a5;
  border-radius: 50%;
  text-align: center;
  &::before {
    content: '';
    display: block;
    position: absolute;
    left: 50%;
    top: 50%;
    transform: translate(-50%, -50%);
    width: 67rpx;
    height: 67rpx;
    border: 1px solid #fff;
    border-radius: 50%;
  }
  &::after {
    content: '';
    display: block;
    position: absolute;
    left: 50%;
    top: 50%;
    transform: translate(-50%, -50%);
    width: 65rpx;
    height: 65rpx;
    border: 1px solid #ffb3a5;
    border-radius: 50%;
  }
  @include m(text) {
    display: inline-block;
    color: #ff847c;
    font-size: 12rpx;
    line-height: 65rpx;
    transform: rotate(-30deg) scale(0.9);
    /* scale(0.9) */
  }
}
</style>
