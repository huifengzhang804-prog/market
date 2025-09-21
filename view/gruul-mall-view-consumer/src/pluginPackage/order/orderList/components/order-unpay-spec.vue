<script lang="ts" setup>
import { computed, ref, type PropType } from 'vue'
import { getOrdercn, useCountdownTime } from '@/hooks'
import OrderGoodsSpec from '@/components/order/order-goods-spec.vue'
import OrderFooter from '@/pluginPackage/order/orderList/components/order-footer.vue'
import OrderCountdown from './order-countdown.vue'
import type { ApiOrder } from '../types'
import { queryConfigByModule } from '@/apis/order'

const $props = defineProps({
  order: {
    type: Object as PropType<ApiOrder>,
    default() {
      return {}
    },
  },
})
const emit = defineEmits(['cancel-order'])
const isCountdown = ref(true)
const platformName = ref(import.meta.env.VITE_PLATFORM_NAME)
const getConfigByModule = async () => {
  const { code, data } = await queryConfigByModule('PUBLIC_SETTING, PLATFORM_SETTING, SHOP_SETTING, SUPPLIER_SETTING')
  if (code === 200 && data) {
    platformName.value = data.PLATFORM_NAME
  }
}
getConfigByModule()
const info = computed(() => {
  return $props.order.shopOrders.map((item) => item.shopOrderItems).flat(1)
})
/**
 * useCountdownTime 订单待支付倒计时
 * @returns {*} 12:30:00
 */
const time = useCountdownTime($props.order.createTime, $props.order?.timeout.payTimeout)
/**
 * 导航去订单详情(未支付的订单没有拆分店铺传orderNo即可)
 * @param {*} orderNo
 * @param {*} shopNo
 */
const handleNavToDetail = () => {
  const url = `/pluginPackage/order/orderDetail/OrderDetail?orderNo=${$props.order.no}`
  uni.navigateTo({ url })
}
const handleNavToMerchant = (shopId: Long) => {
  uni.navigateTo({
    url: `/basePackage/pages/merchant/Index?shopId=${shopId}`,
  })
}
</script>

<template>
  <view class="unpay">
    <view class="unpay__title flex-space-between">
      <view class="unpay__title--name"
        ><text v-if="$props.order?.shopOrders?.length >= 2">{{ platformName }}</text>
        <view v-else class="unpay__title--img" @click.stop="handleNavToMerchant($props.order.shopOrders[0].shopId)">
          <u-image :width="40" :height="40" shape="circle" :src="$props.order.shopOrders[0].shopLogo" />
          <view class="shopNav__headTitle">
            <text class="f13 fontBold shopNav__headTitle--name" style="margin-left: 14rpx">{{ $props.order.shopOrders[0].shopName }}</text>
            <u-icon name="arrow-right" style="margin-left: 14rpx" color="#999999" class="f13"></u-icon>
          </view>
        </view>
      </view>
      <OrderCountdown v-if="isCountdown && $props.order.status === 'UNPAID'" />
      <view v-else>{{ getOrdercn($props.order.status) }}</view>
    </view>
    <view v-for="(item, index) in info" :key="index" @click.stop="handleNavToDetail"> <OrderGoodsSpec :info="item" /> </view>
    <OrderFooter :order="order" :shop-order-list="info" :time="time" @cancel-order="emit('cancel-order')" />
  </view>
</template>

<style lang="scss" scoped>
@include b(unpay) {
  box-sizing: border-box;
  border-radius: 10rpx;
  background: #fff;
  margin-bottom: 14rpx;
  @include e(title) {
    padding: 20rpx;
    height: 90rpx;
    text-align: right;
    color: #f54319;
    font-size: 24rpx;
    @include m(name) {
      font-size: 26rpx;
      font-weight: bold;
      color: #333333;
    }
    @include m(img) {
      @include flex();
    }
    @include e(headTitle) {
      width: 500rpx;
      display: flex;
      @include m(name) {
        display: block;
        @include utils-ellipsis(1);
      }
    }
  }
}
</style>
