<script setup lang="ts">
const $props = defineProps({
  status: { type: String, default: '' },
  priceShow: { type: Boolean, default: true },
  price: { type: [String, Number], default: '' },
})
const { divTenThousand } = useConvert()
</script>
<template>
  <view class="results">
    <view>{{ $props.status }}</view>
    <view v-if="priceShow" class="results__price">{{ $props.price && divTenThousand($props.price) }}</view>
  </view>
</template>

<style scoped lang="scss">
@include b(results) {
  background: #fff;
  padding: 0 20rpx;
  height: 90rpx;
  box-sizing: border-box;
  @include flex(space-between);
  border-bottom: 1px solid #e9e9e9;
  @include e(price) {
    &::before {
      content: '￥';
    }
  }
}
</style>
