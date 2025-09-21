<script setup lang="ts">
import { type PropType, computed } from 'vue'
import { useCountdownTime } from '@/hooks'
import { useCountdown } from '@/hooks/useCountdown/useCountdown'
import { type IOrderList, orderStatus } from '@/pluginPackage/integral/api/types'
import Price from '@/pluginPackage/integral/mall/components/price.vue'
import GoodsItem from './goodsItem.vue'
import OrderOperate from './orderOperate.vue'

const props = defineProps({
  order: {
    type: Object as PropType<IOrderList>,
    default() {
      return {}
    },
  },
})

const emit = defineEmits(['init-list'])
// 倒计时处理
const time = useCountdownTime(props.order.createTime, props.order.timeout?.payTimeout)
// 倒计时处理
const { timeSet } = useCountdown(time, { immediate: true, immediateCallback: true })

/**
 * useCountdownTime 订单待支付倒计时
 * @returns {*} 12:30:00
 */

const handleNavToDetail = () => {
  const url = `/pluginPackage/integral/mall/view/integralOrderDetail/integralOrderDetail?orderNo=${props.order.no}`
  uni.navigateTo({ url })
}

const salePrice = computed(() => String(+props.order.freightPrice + +props.order.salePrice))

const setClipboard = (data: string) => {
  uni.setClipboardData({
    data,
    showToast: true,
  })
}
</script>

<template>
  <view class="orderItem" @click="handleNavToDetail">
    <view class="orderItem__title flex-space-between">
      <view class="orderItem__title--name">
        订单号：{{ order.no }} <text style="color: #005cf4; margin-left: 20rpx" @click="setClipboard(order.no)">复制</text>
      </view>
      <view :style="{ color: ['ACCOMPLISH', 'SYSTEM_CLOSED'].includes(order.status) ? '#999999' : '#FA3534' }">
        {{ orderStatus[order.status] }}
      </view>
    </view>
    <GoodsItem :info="order" />
    <view class="orderItem__info">
      <view>
        <view v-if="order.status === 'UNPAID'">
          还剩
          <text class="red">{{ timeSet.hours }}</text
          >:<text class="red">{{ timeSet.minutes }}</text
          >:<text class="red">{{ timeSet.seconds }}</text>
        </view>
      </view>
      <view style="font-size: 30rpx">
        {{ order.status === 'UNPAID' ? '需付款' : '实付' }}:
        <Price :integral="order?.price" :sale-price="salePrice" />
      </view>
    </view>
    <OrderOperate :info="order" @init-order="emit('init-list')" />
  </view>
</template>

<style lang="scss" scoped>
@include b(orderItem) {
  box-sizing: border-box;
  border-radius: 10rpx;
  background: #fff;
  margin-bottom: 14rpx;
  padding: 20rpx;
  @include e(title) {
    text-align: right;
    color: #f54319;
    font-size: 24rpx;
    @include m(name) {
      font-size: 28rpx;
      color: #333333;
    }
  }
  @include e(info) {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }
}
.red {
  color: #fa101d;
}
.integral {
  color: #ff794d;
}
</style>
