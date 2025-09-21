<script setup lang="ts">
import type { PropType } from 'vue'
import { couponIndexType } from '@plugin/coupon/utils'
import type { CouponJointType } from '@/apis/plugin/coupon/model'

const { divTenThousand } = useConvert()
const $props = defineProps({
  backgroundC: {
    type: String,
    default: '#fceced',
  },
  price: {
    type: [String, Number],
    default: '0',
  },
  unit: {
    type: String,
    default: '元',
  },
  type: {
    type: String as PropType<CouponJointType>,
    default: 'PRICE_REDUCE',
  },
  requiredAmount: { type: String, default: '0' },
})

//  * @param REQUIRED_PRICE_REDUCE 满减券
//  * @param REQUIRED_PRICE_DISCOUNT 满折券
function initCouponType() {
  let couponType: string
  if (['无门槛折扣券', '无门槛现金券'].includes(couponIndexType[$props.type])) {
    couponType = couponIndexType[$props.type]
  } else {
    couponType = `满${divTenThousand($props.requiredAmount)}可使用`
  }
  return couponType
}
</script>

<template>
  <view class="container" :style="{ background: $props.backgroundC }">
    <view class="container__body">
      <text class="container__body--price">{{ $props.price }}</text>
      <text class="container__body--unit">{{ $props.unit }}</text>
    </view>
    <text class="container--describe">{{ initCouponType() }}</text>
  </view>
</template>

<style scoped lang="scss">
@include b(container) {
  border-radius: 16rpx;
  min-width: 228rpx;
  border-top-right-radius: 0;
  border-bottom-right-radius: 0;
  height: 100%;
  @include flex;
  flex-direction: column;
  @include e(body) {
    @include m(price) {
      font-size: 64rpx;
      font-weight: 700;
    }
    @include m(unit) {
      margin-left: 10rpx;
      font-size: 32rpx;
    }
  }
  @include m(describe) {
    margin-top: 10rpx;
    font-size: 22rpx;
  }
}
</style>
