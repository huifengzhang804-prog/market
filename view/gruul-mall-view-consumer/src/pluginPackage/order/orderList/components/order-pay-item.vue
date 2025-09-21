<script lang="ts" setup>
import type { PropType } from 'vue'
import ShopNav from '@/components/order/shop-nav.vue'
import OrderGoodsSpec from '@/components/order/order-goods-spec.vue'
import OrderFooter from '@/pluginPackage/order/orderList/components/order-footer.vue'
import { orderMapComputed } from '@/pluginPackage/order/orderList/hooks/orderHooks'
import type { ApiOrder } from '../types'

defineProps({
  order: { type: Object as PropType<ApiOrder>, default: () => ({}) },
  currentTabIndex: {
    type: Number,
    default: 0,
  },
})
const emit = defineEmits(['update:isShow', 'update-order'])

/**
 * 导航去订单详情(未支付的订单没有拆分店铺传orderNo即可)
 * @param  orderNo 订单号
 * @param  shopId 店铺 id
 * @param  packageId 包裹 id
 */
const handleNavToDetail = (orderNo: string, shopId?: Long, packageId?: string) => {
  const baseUrl = `/pluginPackage/order/orderDetail/OrderDetail?orderNo=${orderNo}&packageId=${packageId ? packageId : ''}`
  if (!shopId) return uni.navigateTo({ url: baseUrl })
  const url = `${baseUrl}&shopId=${shopId}`
  uni.navigateTo({ url })
}
</script>
<template>
  <view
    v-for="(shop, index) in order.shopOrders"
    :key="index"
    class="container"
    @click.stop="handleNavToDetail(order.no, shop.shopId, shop.shopOrderItems[0].packageId)"
  >
    <ShopNav :showIc="[0, 3].includes(currentTabIndex)" :info="shop" :order="order" />
    <view v-for="(item, idx) in Array.from(orderMapComputed(shop.shopOrderItems).values())" :key="idx">
      <OrderGoodsSpec :info="item.merged" />
    </view>
    <OrderFooter :order="order" :shop="shop" />
  </view>
</template>

<style lang="scss" scoped>
@include b(container) {
  box-sizing: border-box;
  border-radius: 10rpx;
  background: #fff;
  margin-bottom: 14rpx;
}
</style>
