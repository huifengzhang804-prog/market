<script lang="ts" setup>
import { Decimal } from 'decimal.js-light'
import { ref, watch, type PropType } from 'vue'
import type { OrderDiscountDetail } from '@/pluginPackage/order/orderDetail/types'
import { ORDERSTATUS } from '@/pluginPackage/order/orderList/types'

import QPrice from '@/components/q-price/index.vue'

const props = defineProps({
  freightPrice: {
    type: Object as PropType<Decimal>,
    default: () => new Decimal(0),
  },
  discounts: {
    type: Array as PropType<Array<OrderDiscountDetail>>,
    default: () => [],
  },
  totalNum: {
    type: Number,
    default: 0,
  },
  payAmountComputed: { type: [Number, Decimal], default: 0 },
  amountRealPay: {
    type: [Number, Decimal],
    default: 0,
  },
  ruleStr: {
    type: String as PropType<keyof typeof ORDERSTATUS>,
    default: 'UNPAID',
  },
})

//优惠统计
const statistics = ref({
  //总优惠
  totalDiscount: new Decimal(0),
  // 店铺优惠
  shopDiscount: new Decimal(0),
  // 其它优惠项
  otherDiscount: [] as OrderDiscountDetail[],
})

watch(
  () => props.discounts,
  (discounts) => {
    let totalDiscount = new Decimal(0)
    let shopDiscount = new Decimal(0)
    const otherDiscount = [] as OrderDiscountDetail[]
    discounts.forEach((item) => {
      totalDiscount = totalDiscount.add(item.price)
      if (item.isShopDiscount) {
        shopDiscount = shopDiscount.add(item.price)
      } else {
        otherDiscount.push(item)
      }
    })
    statistics.value = {
      totalDiscount,
      shopDiscount,
      otherDiscount: otherDiscount.sort((a, b) => a.sort - b.sort),
    }
  },
  {
    immediate: true,
  },
)
</script>

<template>
  <view class="pay-info__title f13"> 价格明细</view>
  <view class="pay-info__main f13">
    <view class="pay-info__item flex-space-between">
      <view class="pay-info__label">
        商品总价（共
        <text class="pay-info__label--num">{{ totalNum }}</text>
        件）
      </view>
      <q-price :price="payAmountComputed" class="pay-info__item--price pay-info__item--price-info" font-size="30rpx" />
    </view>
    <!--   运费   -->
    <view v-if="freightPrice?.cmp(0) > 0" class="pay-info__item flex-space-between">
      <view class="pay-info__label"> 运费</view>
      <q-price :price="freightPrice" class="pay-info__item--price pay-info__item--price-info" font-size="26rpx" symbol="+" />
    </view>
    <!--   优惠折扣项    -->
    <!--   店铺优惠    -->
    <view v-show="statistics.shopDiscount.cmp(0) > 0" class="pay-info__item flex-space-between">
      <view class="pay-info__label">店铺优惠</view>
      <q-price :price="statistics.shopDiscount" class="pay-info__item--price pay-info__item--price-info" font-size="26rpx" symbol="-" />
    </view>
    <!--   其它折扣项    -->
    <view v-for="(item, idx) in statistics.otherDiscount" :key="idx" class="pay-info__item flex-space-between">
      <view class="pay-info__label">{{ item.name }}</view>
      <q-price :price="item.price" class="pay-info__item--price pay-info__item--price-info" font-size="26rpx" symbol="-" />
    </view>
    <view v-show="statistics.totalDiscount.cmp(0) > 0" class="pay-info__item flex-space-between">
      <view class="pay-info__label">总优惠</view>
      <q-price :price="statistics.totalDiscount" class="pay-info__item--price" font-size="28rpx" symbol="-" />
    </view>
  </view>
  <view class="flex-space-between" style="padding: 0 40rpx 20rpx 20rpx">
    <view class="pay-info__label fontBold">{{ ruleStr === 'PAID' ? '实付款' : '应付款' }}</view>
    <q-price :price="amountRealPay" class="pay-info__item--price fontBold" font-size="30rpx" unit="￥" />
  </view>
</template>

<style lang="scss" scoped>
@include b(pay-info) {
  @include e(title) {
    padding: 20rpx;
    font-weight: 700;
  }
  @include e(label) {
    @include m(num) {
      color: $u-type-warning-disabled;
    }
  }
  @include e(main) {
    padding: 0rpx 40rpx 0 40rpx;
  }
  @include e(item) {
    margin-bottom: 25rpx;
    @include m(price) {
      color: $qszr-red;
    }
    @include m(price-info) {
      color: #666666;
    }
  }
}
</style>
