<script setup lang="ts">
import type { PropType } from 'vue'
import type { OnlineTopUpItem } from '@/basePackage/pages/onlineTopUp/types'

const $props = defineProps({
  bgColor: { type: String, default: '#ff5851' },
  lineTopUpItem: { type: Object as PropType<OnlineTopUpItem>, default: () => ({}) },
  discountsState: { type: Boolean, default: true },
})
const { divTenThousand } = useConvert()
</script>

<template>
  <view class="Topup__card" :style="{ background: $props.bgColor }">
    <view class="Topup__card--title">充值金额(元)</view>
    <view class="Topup__card--pirce">{{ divTenThousand($props.lineTopUpItem.ladderMoney).toFixed(2) }}</view>
    <view v-if="$props.discountsState" class="Topup__card--params">
      充值优惠：赠送{{ divTenThousand($props.lineTopUpItem.presentedMoney) }}元；赠送{{ $props.lineTopUpItem.presentedGrowthValue }}成长值
    </view>
  </view>
</template>

<style scoped lang="scss">
@include b(Topup) {
  @include e(card) {
    padding: 40rpx;
    border-radius: 15rpx;
    color: #fff;
    @include m(title) {
      font-size: 22rpx;
      margin-bottom: 14rpx;
    }
    @include m(pirce) {
      font-size: 50rpx;
      font-weight: 700;
      margin-bottom: 30rpx;
    }
    @include m(params) {
      font-size: 22rpx;
    }
  }
}
</style>
