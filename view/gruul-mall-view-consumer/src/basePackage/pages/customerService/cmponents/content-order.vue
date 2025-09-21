<script setup lang="ts">
import { computed, type Ref } from 'vue'
import type { CurrentProduct } from '@/basePackage/pages/customerService/types'
import { jumpGoods } from '@/utils/navigateToShopInfo'

const $props = defineProps({
  msg: { type: String, required: true },
  isMine: { type: Boolean, required: true },
  shopId: { type: [String, Number], required: true },
})
const { divTenThousand } = useConvert()
const product: Ref<CurrentProduct> = computed(() => {
  const productMsg = $props.msg
  if (!productMsg) {
    return { id: '', name: '未正确获取商品信息', salePrices: [], pic: '' }
  }
  return JSON.parse(productMsg)
})

const navToProductInfo = () => {
  const shopId = product.value.shopId ? product.value.shopId : $props.shopId
  if (!product.value.no || !shopId) return
  const url = `/pluginPackage/order/orderDetail/OrderDetail?orderNo=${product.value.no}&shopId=${shopId}`
  uni.navigateTo({ url })
}
</script>

<template>
  <view class="product" :class="$props.isMine ? '' : 'product-consumer'" @click="navToProductInfo">
    <view class="product__image">
      <u-image width="100%" :height="300" border-radius="14" :src="product.pic" mode="widthFix" />
    </view>
    <view class="product__info">
      <text class="product__info--name" style="width: 100%">订单号:{{ product.no }}</text>
    </view>
    <view class="product__info">
      <text class="product__info--name">{{ product.name ? product.name : '未正确获取商品信息' }}</text>
      <text class="product__info--price"> 实付￥{{ product.amountRealPay }} </text>
    </view>
  </view>
</template>

<style scoped lang="scss">
@include b(product) {
  margin: 20rpx;
  padding: 20rpx;
  border: 5rpx solid $content-bg;
  background: $content-bg;
  border-radius: 15rpx;
  @include e(image) {
    width: 450rpx;
    margin: 0 auto;
  }
  @include e(info) {
    margin-top: 20rpx;
    @include flex;
    @include m(name) {
      width: 60%;
      font-size: 24rpx;
      font-weight: 700;
      @include utils-ellipsis(2);
    }
    @include m(price) {
      flex: 1;
      font-size: 26rpx;
      font-weight: 700;
      text-align: right;
      color: red;
      @include utils-ellipsis(2);
    }
  }
}
@include b(product-consumer) {
  border: 5rpx solid $content-bg;
  background: $content-bg;
}
</style>
