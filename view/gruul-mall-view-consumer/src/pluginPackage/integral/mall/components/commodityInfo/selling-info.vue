<script setup lang="ts">
import type { PropType } from 'vue'
import type { IntegralGoodsInfoType } from '@/apis/plugin/integral/model'
import price from '../price.vue'
const $props = defineProps({
  info: {
    type: Object as PropType<IntegralGoodsInfoType>,
    default() {
      return {}
    },
  },
})
const { divTenThousand, salesVolumeToStr } = useConvert()
</script>

<template>
  <view class="selling">
    <view class="selling__info">
      <view>
        <price :integral="info.integralPrice" :salePrice="info.salePrice" font-size="40rpx" />
        <view class="selling__info--price">
          ￥<text style="text-decoration: line-through"> {{ $props.info.price }}</text>
        </view>
      </view>
      <view> </view>
    </view>
    <view class="selling__name">{{ $props.info.name }}</view>
  </view>
</template>

<style scoped lang="scss">
@include b(selling) {
  padding-top: 18rpx;
  box-sizing: border-box;
  @include e(name) {
    margin-top: 25rpx;
    font-size: 30rpx;
    font-weight: 700;
    color: #333333;
  }
  @include e(info) {
    @include flex(space-between);
    @include m(price) {
      display: inline-block;
      color: rgb(153, 153, 153);
      font-size: 26rpx;
      margin-left: 20rpx;
    }
  }
}
</style>
