<script lang="ts" setup>
import { reactive, watch, type PropType } from 'vue'
import GoodSku from '@/pluginPackage/order/confirmOrder/components/order/good-sku.vue'
import CusForm from '@/pluginPackage/order/confirmOrder/components/order/cus-form.vue'
import QPrice from '@/components/q-price/index.vue'
import type { ApishopDealSetting, OrderProductType, OrderShopPackage } from '@/pluginPackage/order/confirmOrder/types'
import { formattingCouponRules } from '@plugin/coupon/utils'
import type { ApiOrderCouponVO } from '@/apis/plugin/coupon/model'
import type { Nullable } from '@/constant/global'

const $props = defineProps({
  info: {
    type: Object as PropType<OrderProductType>,
    default() {
      return {}
    },
  },
  coupon: {
    type: Object as PropType<ApiOrderCouponVO>,
    default() {
      return null
    },
  },
  fullDiscount: {
    type: [String, Number] as PropType<Nullable<Long>>,
    default: null,
  },
  recordErrGoods: { type: String as PropType<Long>, default: '' },
  submitForm: { type: Object as PropType<OrderShopPackage>, default: () => ({}) },
  idx: { type: Number, default: 0 },
  shopDealSetting: { type: Object as PropType<ApishopDealSetting>, required: true },
  freight: { type: [String, Number] as PropType<Nullable<Long>>, default: 0 },
})
const $emit = defineEmits(['end', 'chooseShopCoupon'])

interface FormType {
  //满减折扣金额
  full: Nullable<Long>
  //运费
  freight: Nullable<Long>
}

const form = reactive<FormType>({
  full: null,
  freight: null,
})

watch(
  () => $props.fullDiscount,
  (fullDiscount) => (form.full = convertPrice(fullDiscount)),
  {
    immediate: true,
  },
)
watch(
  () => $props.freight,
  (freight) => (form.freight = convertPrice(freight)),
  {
    immediate: true,
  },
)

function convertPrice(price: Nullable<Long>) {
  if (price === null || price === 0 || price === '0') {
    return null
  }
  return price
}
</script>

<template>
  <view class="order">
    <view class="order__shopInfo">
      <u-image :border-radius="20" :height="36" :src="$props.info.shopLogo" :width="36" class="order__shopInfo--img" mode="aspectFill" />
      <view>{{ $props.info.shopName }}</view>
    </view>
    <view class="order__row"></view>
    <view class="order__list">
      <GoodSku
        v-for="item in $props.info.products"
        :key="item.id"
        :class="{ 'err-goods': $props.recordErrGoods === item.productId || $props.recordErrGoods === item.skuId }"
        :info="item"
        @animationend="$emit('end', '')"
      />
      <view class="order__discount" @click="$emit('chooseShopCoupon')">
        <text>优惠券</text>
        <view>
          <text style="font-weight: 700; margin-right: 5rpx">{{ formattingCouponRules($props.coupon) }}</text>
          <u-icon color="#909399" name="arrow-right" />
        </view>
      </view>
      <view class="order__discount">
        <text>运费</text>
        <view>
          <view style="font-weight: 700; margin-right: 5rpx">
            <QPrice v-if="form.freight" :price="form.freight" font-size="28rpx" />
            <text v-else>包邮</text>
          </view>
        </view>
      </view>
      <view v-if="form.full" class="order__discount">
        <text>满减优惠</text>
        <view>
          <view style="font-weight: 700; margin-right: 5rpx">
            <QPrice :price="form.full" font-size="28rpx" symbol="-" />
          </view>
        </view>
      </view>
      <CusForm :idx="$props.idx" :shop-deal-setting="$props.shopDealSetting" :shop-id="$props.info.shopId" />
    </view>
  </view>
</template>

<style lang="scss" scoped>
@import '@/assets/css/animate.scss';

@include b(order) {
  background: #fff;
  border-radius: 20rpx;
  margin-bottom: 22rpx;
  @include e(shopInfo) {
    @include flex(flex-start);
    font-size: 28rpx;
    color: #333333;
    font-weight: bold;
    height: 110rpx;
    @include m(img) {
      margin-right: 16rpx;
      margin-left: 32rpx;
    }
  }
  @include e(row) {
    height: 4rpx;
    background: #ededed;
  }
  @include e(list) {
    padding: 36rpx 36rpx 10rpx 16rpx;
  }
  @include e(discount) {
    @include flex;
    justify-content: space-between;
    margin: 40rpx 0;
  }
}
</style>
