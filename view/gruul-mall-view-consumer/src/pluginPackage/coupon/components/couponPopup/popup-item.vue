<script setup lang="ts">
import type { PropType } from 'vue'
import { formattingCouponRules } from '@plugin/coupon/utils'
import type { ApiOrderCouponVO } from '@/apis/plugin/coupon/model'

const $props = defineProps({
  radioName: { type: String, default: '' },
  tag: { type: Boolean, default: false },
  radioMsg: { type: Boolean, default: true },
  groupValue: { type: String, required: true },
  coupon: { type: Object as PropType<ApiOrderCouponVO>, default: null },
})
const { divTenThousand } = useConvert()
</script>

<template>
  <view class="reduction">
    <view class="reduction--describe">
      <slot>
        <text>{{ formattingCouponRules($props.coupon) }}</text>
        <u-tag v-if="$props.tag" :text="formattingCouponRules($props.coupon)" type="error" mode="plain" size="mini" class="reduction__tag" />
      </slot>
    </view>
    <view class="radio-box">
      <template v-if="$props.radioMsg">
        <text class="reduction--price">减</text>
        <text style="font-size: 34rpx; font-weight: Bold; color: #f83f30; margin-right: 15rpx">{{
          $props.coupon.discountAmount && divTenThousand($props.coupon.discountAmount)
        }}</text>
      </template>
      <slot name="radio"></slot>
    </view>
  </view>
</template>

<style scoped lang="scss">
@include b(reduction) {
  padding: 20rpx;
  padding-right: 0;
  width: 100vw;
  @include flex;
  justify-content: space-between;
  @include e(tag) {
    transform: scale(0.7);
  }
  @include m(describe) {
    font-size: 30rpx;
    font-weight: Bold;
    color: #000000;
    font-family:
      Microsoft YaHei,
      Microsoft YaHei-Bold;
  }
  @include m(price) {
    font-size: 28rpx;
    font-weight: Bold;
    color: #f83f30;
    & ::after {
      content: '￥';
      font-size: 24rpx;
      font-weight: Bold;
      color: #f83f30;
    }
  }
}
@include b(radio-box) {
  @include flex;
  margin-right: 20rpx;
}
</style>
