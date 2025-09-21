<script setup lang="ts">
import { ref } from 'vue'
import { useRebatePrices } from '@/pluginPackage/rebate/hooks'
import RebatesThatPopup from '@/pluginPackage/rebate/views/components/rebates-that-popup.vue'
import type { RebateTransactions } from '@/pluginPackage/rebate/apis/model'

const rebateRef = ref<RebateTransactions>({
  accumulatedRebate: '0',
  expiredRebate: '0',
  rebateBalance: '0',
  unsettledRebate: '0',
})
const rebatesThatPopupRef = ref<InstanceType<typeof RebatesThatPopup> | null>(null)
const { init, formatPrice } = useRebatePrices()

initRebatePrices()

async function initRebatePrices() {
  rebateRef.value = await init()
}
const handleShowPopup = () => {
  rebatesThatPopupRef.value?.switchPopup(true)
}
</script>

<template>
  <view class="head-content">
    <view class="head-content__card">
      <view class="head-content__item">
        <text class="head-content__item--price">{{ formatPrice(rebateRef.accumulatedRebate) }}</text>
        <text class="head-content__item--msg">累计返利</text>
      </view>
      <view class="head-content__item">
        <text class="head-content__item--price">{{ formatPrice(rebateRef.unsettledRebate) }}</text>
        <text class="head-content__item--msg">待结算返利</text>
      </view>
      <view class="head-content__item">
        <text class="head-content__item--price" style="color: #999999">{{ formatPrice(rebateRef.expiredRebate) }}</text>
        <text class="head-content__item--msg">已失效返利</text>
      </view>
    </view>
    <view class="head-content__icon" @click="handleShowPopup">
      <u-icon name="question-circle"></u-icon>
    </view>
  </view>
  <rebates-that-popup ref="rebatesThatPopupRef"></rebates-that-popup>
</template>

<style scoped lang="scss">
@include b(head-content) {
  position: relative;
  width: 100vw;
  height: 206rpx;
  padding: 0 20rpx;
  @include flex;
  @include e(card) {
    /* 矩形 9 */
    /* 卡片投影 */
    width: 100%;
    box-shadow: 0px 0px 15rpx rgba(0, 0, 0, 0.25);
    border-radius: 20rpx;
    height: 166rpx;
    background: #fff;
    @include flex;
    justify-content: space-between;
    padding: 0 75rpx;
  }
  @include e(item) {
    height: 166rpx;
    @include flex;
    flex-direction: column;
    @include m(price) {
      /* 80.90 */
      font-size: 40rpx;
      font-weight: 400;
      color: #fa3534;
      margin-bottom: 10rpx;
    }
    @include m(msg) {
      color: #6666;
    }
  }
  @include e(icon) {
    position: absolute;
    right: 60rpx;
    top: 40rpx;
    color: #575b66;
  }
}
</style>
