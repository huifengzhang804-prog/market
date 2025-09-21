<template>
  <view class="order-item">
    <view class="order-item__header">
      <view class="order-item__header--shop">
        <q-icon name="icon-Fdianpu" size="25" color="#FA3534" />
        <text class="shop-name">{{ $props.orderItem.shopName }}</text>
        <!-- <q-icon name="icon-youjiantou_huaban1" size="25" color="#999" /> -->
      </view>
      <view class="order-item__header--status">{{ PaidStatus[$props.orderItem.status] }}</view>
    </view>
    <view v-for="rebateOrderItem in $props.orderItem?.rebateOrderItems" :key="rebateOrderItem.skuId" class="order-item__container">
      <view class="order-item__info">
        <image :src="rebateOrderItem.image" class="order-item__info--cover" />
        <view class="order-item__info--desc commodity">
          <view class="commodity__title">{{ rebateOrderItem.productName }}</view>
          <view class="commodity__sku">
            <text class="commodity__sku--sku">{{ rebateOrderItem.specs?.join(';') }}</text>
            <text class="commodity__sku--num">X {{ rebateOrderItem.num }}</text>
          </view>
          <view class="commodity__price">
            <text class="commodity__price--currency">￥</text>
            <text class="commodity__price--main">{{ divTenThousand(rebateOrderItem.salePrice) }}</text>
          </view>
        </view>
      </view>
      <view class="order-item__summary">
        <text class="order-item__summary--back">
          <text class="decription">预计返 </text>
          <text class="price"> {{ divTenThousand(rebateOrderItem?.estimatedRebate)?.toFixed(2) }}</text>
        </text>
        <text class="order-item__summary--status">{{ SettlementStatus[rebateOrderItem.settlementStatus] }}</text>
      </view>
    </view>
    <view class="order-item__total">
      <text v-if="hasItems($props.orderItem?.rebateOrderItems, 'PENDING_SETTLEMENT')" class="order-item__total--group">
        <text class="description">待结算:</text>
        <text class="price">{{ divTenThousand($props.orderItem?.pendingSettlement) }}</text>
      </text>
      <text v-if="hasItems($props.orderItem?.rebateOrderItems, 'SETTLED')" class="order-item__total--group">
        <text class="description">已返:</text>
        <text class="price">{{ divTenThousand($props.orderItem?.settled) }}</text>
      </text>
      <text v-if="hasItems($props.orderItem?.rebateOrderItems, 'EXPIRED')" class="order-item__total--group">
        <text class="description">已失效:</text>
        <text class="price">{{ divTenThousand($props.orderItem?.expired) }}</text>
      </text>
      <text class="order-item__total--group">
        <text class="description">返利合计:</text>
        <text class="price">{{ divTenThousand($props.orderItem?.shopTotalRebate) }}</text>
      </text>
    </view>
  </view>
</template>

<script lang="ts" setup>
import type { PropType } from 'vue'
import type { RebateOrder } from '../types'
import QIcon from '@/components/q-icon/q-icon.vue'
import { PaidStatus, SettlementStatus } from '../types/index'

const { divTenThousand } = useConvert()
const $props = defineProps({
  orderItem: {
    type: Object as PropType<RebateOrder>,
    default: () => ({}),
  },
})
const hasItems = (shopItems: RebateOrder['rebateOrderItems'], targetStatus: keyof typeof SettlementStatus) => {
  const item = shopItems?.find((shopItem) => shopItem.settlementStatus === targetStatus)
  if (item) return true
  return false
}
</script>

<style lang="scss" scoped>
@include b(order-item) {
  padding: 20rpx;
  @include e(header) {
    @include flex(space-between, flex-end);
    line-height: 30rpx;
    @include m(shop) {
      @include flex(center, center);
      @include b(shop-name) {
        margin: 0 10rpx;
        font-size: 26rpx;
      }
    }
    @include m(status) {
      color: #fa3534;
      font-size: 22rpx;
    }
  }
  &__container {
    border-bottom: 1rpx dashed #ccc;
    padding: 20rpx 0;
  }
  &__info {
    display: flex;
    @include flex();
    @include m(cover) {
      width: 128rpx;
      height: 128rpx;
      border-radius: 12rpx;
      flex-shrink: 0;
    }
    @include m(desc) {
      flex: 1;
      margin-left: 16rpx;
    }
    @include b(commodity) {
      overflow: hidden;
      @include e(title) {
        overflow: hidden;
        white-space: nowrap;
        text-overflow: ellipsis;
        font-size: 24rpx;
        line-height: 40rpx;
      }
      @include e(sku) {
        @include flex(space-between);
        line-height: 40rpx;
        font-size: 24rpx;
        color: #666;
      }
      @include e(price) {
        color: #fa3534;
        margin-top: 10rpx;
        @include m(currency) {
          font-size: 22rpx;
        }
        @include m(main) {
          font-size: 28rpx;
          font-weight: 700;
        }
      }
    }
  }
  &__summary {
    margin-left: 144rpx;
    line-height: 40rpx;
    font-size: 24rpx;
    color: #333;
    @include flex(space-between);
    @include m(back) {
      @include b(description) {
        color: #666;
      }
      @include b(price) {
        color: #fa3534;
      }
    }
  }
  &__total {
    margin-top: 20rpx;
    font-size: 24rpx;
    &--group + &--group {
      margin-left: 20rpx;
    }
    @include b(description) {
      color: #333;
    }
    @include b(price) {
      color: #fa3534;
    }
  }
}
</style>
