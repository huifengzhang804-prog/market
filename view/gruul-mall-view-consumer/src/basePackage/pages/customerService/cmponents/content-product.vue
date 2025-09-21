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
  // 字符串截取成数组
  // 截取第一个元素
  const id = product.value.id.split(':')[1] ? product.value.id.split(':')[1] : product.value.id
  const shopId = product.value.id.split(':').length > 1 && product.value.id.split(':')[0] ? product.value.id.split(':')[0] : $props.shopId
  if (!id || !shopId) return
  jumpGoods(shopId, id)
}
</script>

<template>
  <view class="product" :class="$props.isMine ? '' : 'product-consumer'" @click="navToProductInfo">
    <view class="product__image">
      <u-image width="100%" :height="300" border-radius="14" :src="product.pic" mode="widthFix" />
    </view>
    <view class="product__info">
      <text class="product__info--name">{{ product.name ? product.name : '未正确获取商品信息' }}</text>
      <text v-if="product.h5" class="product__info--price">{{
        product.price?.estimate ? `¥${divTenThousand(product.price.estimate).toFixed(2)}起` : ''
      }}</text>

      <text v-else class="product__info--price">￥{{ String(product.price?.estimate).split('~')[0] }}起</text>
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
    flex-wrap: wrap;
    @include m(name) {
      width: 270rpx;
      font-size: 24rpx;
      font-weight: 700;
      @include utils-ellipsis(2);
    }
    @include m(price) {
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
