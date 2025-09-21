<script setup lang="ts">
import type { PropType } from 'vue'
import { useUserStore } from '@/store/modules/user'

const $props = defineProps({
  // list 为列表会员价 goodDetail为商品详情
  display: {
    type: String,
    default: 'list',
  },
  // <!--priceType===LAST && display === goodDetail 时 price 为计算好的会员价格传入 -->
  price: {
    type: [String, Number],
    default: '0',
  },
  textColor: {
    type: String,
    default: '#f7e6c9',
  },
  priceType: {
    type: String as PropType<'DEFAULT' | 'LAST'>,
    default: '#f7e6c9',
  },
})
const { divTenThousand, fixedUp } = useConvert()
const { memberPrice, includeDiscount } = useMember()
const showMemberPrice = includeDiscount()
const memberInfo = useUserStore().getterMemberInfo
</script>

<template>
  <view v-if="showMemberPrice && $props.display === 'list'" class="memberPrice">
    <view class="memberPrice__title">会员</view>
    <view class="fontBold">
      <text>优惠价</text>
      <text>￥{{ memberPrice($props.price) }}</text>
    </view>
  </view>
  <view v-else-if="showMemberPrice && $props.display === 'goodDetail'" class="member-gooodDetail" :style="{ color: `${$props.textColor}` }">
    <text>{{ memberInfo?.memberName }}</text>
    <text v-if="priceType === 'DEFAULT'" class="fontBold">￥{{ memberPrice($props.price) }}&nbsp;起</text>
    <text v-if="priceType === 'LAST'" class="fontBold">￥{{ divTenThousand($props.price).toFixed(2) }}&nbsp;起</text>
  </view>
</template>

<style lang="scss" scoped>
@include b(memberPrice) {
  width: 100%;
  box-sizing: border-box;
  font-size: 20rpx;
  @include flex(flex-start);
  @include e(title) {
    width: 54rpx;
    height: 30rpx;
    text-align: center;
    line-height: 28rpx;
    background: #302d2d;
    color: #f7e6c9;
    border-radius: 4rpx;
    margin-right: 6rpx;
  }
}
@include b(member-gooodDetail) {
  @include flex;
  font-size: 24rpx;
  padding: 6rpx 18rpx;
  text-align: center;
  background: #ffedc7;
  border-radius: 60rpx;
}
</style>
