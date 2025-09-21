<script setup lang="ts">
import QIcon from '@/components/q-icon/q-icon.vue'

defineProps({
  buttonStyle: {
    type: Number,
    default: 1,
  },
})
</script>

<template>
  <div v-if="buttonStyle === 1" :class="['cart', `cart1`]">
    {{ '加入购物车' }}
  </div>
  <div v-if="buttonStyle === 2" :class="['cart', `cart2`]">
    {{ '加入购物车' }}
  </div>
  <div v-if="buttonStyle === 3" :class="['cart', `cart1`]">
    {{ '立即购买' }}
  </div>
  <div v-if="buttonStyle === 4" :class="['cart', `cart2`]">
    {{ '立即购买' }}
  </div>
  <div v-if="buttonStyle === 5" :class="['cart', 'icon']">
    <q-icon name="icon-gouwuche5" size="40rpx"></q-icon>
  </div>
</template>

<style scoped lang="scss">
$red: #fa3534;
$fontsize: 25rpx;
$height: 44rpx;
$color: #f3f3f3;
.cart {
  float: right;
  height: 50rpx;
  width: 50rpx;
  box-sizing: border-box;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
}
@include b(cart2) {
  border: 1px solid rgba(252, 98, 63, 1);
  width: auto;
  padding: $fontsize 20rpx;
  color: $red;
  font-size: $fontsize;
  border-radius: $fontsize;
  height: $height;
  line-height: $height;
}
@include b(cart1) {
  border: 1px solid rgba(252, 98, 63, 1);
  background-color: $red;
  width: auto;
  padding: $fontsize 20rpx;
  color: $color;
  font-size: $fontsize;
  border-radius: $fontsize;
  height: $height;
  line-height: $height;
}
.icon {
  color: $red;
  font-weight: 700;
}
</style>
