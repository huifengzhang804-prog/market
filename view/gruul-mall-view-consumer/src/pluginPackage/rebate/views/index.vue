<script setup lang="ts">
import { onMounted, onUnmounted, ref } from 'vue'
import { useRebatePrices } from '@/pluginPackage/rebate/hooks'
import type { RebateTransactions } from '@/pluginPackage/rebate/apis/model'
import Auth from '@/components/auth/auth.vue'
import useRebateDispatcher from '@/store/dispatcher/useRebateDispatcher'
const $rebateDispatcherStore = useRebateDispatcher()

const navHandler = {
  Rules: {
    url: '/pluginPackage/rebate/views/Rules',
  },
  WithdrawDetail: {
    url: '/pluginPackage/rebate/views/WithdrawDetail',
  },
  RebateDetails: {
    url: '/pluginPackage/rebate/views/RebateDetails',
  },
  Order: {
    url: '/pluginPackage/rebate/views/Order',
  },
}
const { init, formatPrice } = useRebatePrices()
const rebateRef = ref<RebateTransactions>({
  accumulatedRebate: '0',
  expiredRebate: '0',
  rebateBalance: '0',
  unsettledRebate: '0',
})

initRebateTransactions()

async function initRebateTransactions() {
  rebateRef.value = await init()
}
const handleNavToPage = (key: keyof typeof navHandler) => {
  let url = navHandler[key].url
  // if (key === 'RebateDetails') {
  //     url = `${url}?rebateBalance=${rebateRef.value.rebateBalance}`
  // }
  uni.navigateTo({ url })
}

const rebateDispatcher = () => initRebateTransactions()
onMounted(() => $rebateDispatcherStore.addSubscriber(rebateDispatcher))
onUnmounted(() => $rebateDispatcherStore.removeSubscriber(rebateDispatcher))
</script>

<template>
  <view>
    <view class="head">
      <text class="head__label">余额(元)</text>
      <view class="head__price"> {{ formatPrice(rebateRef.rebateBalance) }}</view>
    </view>
    <view class="price-card">
      <view class="price-card__left">
        累计返利 <text class="price-card__price">{{ formatPrice(rebateRef.accumulatedRebate) }}</text>
      </view>
      <text style="opacity: 0.5">|</text>
      <view class="price-card__right">
        待结算返利 <text class="price-card__price">{{ formatPrice(rebateRef.unsettledRebate) }}</text>
      </view>
    </view>
    <view class="card">
      <u-grid :col="2" :border="false">
        <u-grid-item :custom-style="{ padding: '10rpx 0' }" bg-color="transparent">
          <view class="card__item" @click="handleNavToPage('Order')">
            <view class="card__item_left">
              <view class="card__item_left--title">返利订单</view>
              <view class="card__item_left--desc">返利来自哪里</view>
            </view>
            <view class="card__item_right"></view>
            <view class="card__item-right"><q-icon color="#a7c9b8" name="icon-Vector" size="100rpx" /> </view>
          </view>
        </u-grid-item>
        <u-grid-item :custom-style="{ padding: '10rpx 0' }" bg-color="transparent">
          <view class="card__item" style="background: #e07a5f" @click="handleNavToPage('RebateDetails')">
            <view class="card__item_left">
              <view class="card__item_left--title">返利明细</view>
              <view class="card__item_left--desc">清晰账目</view>
            </view>
            <view class="card__item_right"></view>
            <view class="card__item-right"><q-icon color="#e9a28f" name="icon-path" size="100rpx" /> </view>
          </view>
        </u-grid-item>
        <u-grid-item :custom-style="{ padding: '10rpx 0' }" bg-color="transparent">
          <view class="card__item" style="background: #f4a261" @click="handleNavToPage('WithdrawDetail')">
            <view class="card__item_left">
              <view class="card__item_left--title">提现明细</view>
              <view class="card__item_left--desc">清晰账目</view>
            </view>
            <view class="card__item_right"></view>
            <view class="card__item-right"><q-icon color="#f7bf92" name="icon-Fdingdan" size="100rpx" /> </view>
          </view>
        </u-grid-item>
        <u-grid-item :custom-style="{ padding: '10rpx 0' }" bg-color="transparent">
          <view class="card__item" style="background: #159a9c" @click="handleNavToPage('Rules')">
            <view class="card__item_left">
              <view class="card__item_left--title">返利规则</view>
              <view class="card__item_left--desc">如何获得返利</view>
            </view>
            <view class="card__item_right"></view>
            <view class="card__item-right"><q-icon color="#5cb8ba" name="icon-a-Vector1" size="100rpx" /> </view>
          </view>
        </u-grid-item>
      </u-grid>
    </view>
  </view>
  <Auth />
</template>
<style scoped lang="scss">
@include b(head) {
  width: 100vw;
  height: 167rpx;
  background: #fd5e37;
  color: #fff;
  font-family: Arial;
  font-weight: 700;
  text-align: center;
  @include e(label) {
    top: 35rpx;
    left: 20rpx;
    position: absolute;
    font-size: 30rpx;
    font-weight: 500;
  }
  @include e(price) {
    padding-top: 30rpx;
    font-size: 53rpx;
    text-align: center;
  }
}
@include b(price-card) {
  position: absolute;
  left: 0;
  right: 0;
  margin: 0 20rpx;
  height: 88rpx;
  top: 120rpx;
  background: #fff;
  border-radius: 20rpx;
  box-shadow: 0rpx 0rpx 15rpx rgba(0, 0, 0, 0.25);
  @include flex;
  justify-content: space-between;
  padding: 0 60rpx;
  @include e(price) {
    color: #ff794d;
    font-weight: 700;
  }
}
@include b(card) {
  margin: 60rpx 20rpx;
  @include e(item) {
    width: 340rpx;
    height: 180rpx;
    background: rgb(129, 178, 154);
    border-radius: 20rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    &:active {
      opacity: 0.8;
    }
  }
  @include e(item_left) {
    width: 185rpx;
    @include flex;
    flex-direction: column;
    height: 90rpx;
    color: #fff;
    justify-content: space-between;
    @include m(title) {
      font-size: 36rpx;
      font-weight: bold;
    }
    @include m(desc) {
      font-size: 26rpx;
    }
  }
  @include e(item-right) {
    width: 100rpx;
    height: 100rpx;
    margin-left: 20rpx;
  }
}
</style>
