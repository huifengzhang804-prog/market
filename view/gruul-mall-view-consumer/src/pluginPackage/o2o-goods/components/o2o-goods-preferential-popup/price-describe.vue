<script setup lang="ts">
import { computed, inject, type PropType, type Ref } from 'vue'
import ForecastItem from '@/pluginPackage/o2o-goods/components/o2o-goods-preferential-popup/forecast-item.vue'

const props = defineProps({
  price: {
    type: String as PropType<Long>,
    required: true,
  },
})
const $comProvide = inject('comProvide') as {
  discountAmount: Ref<string>
  memberPriceC: Ref<string>
}
const { mulTenThousand } = useConvert()
const discountList = computed(() => {
  const list: any = []
  list.unshift({ price: props.price, text: `当前价` })
  if ($comProvide.discountAmount.value) {
    list.push({ price: $comProvide.discountAmount.value, text: `优惠券` })
  }
  if ($comProvide.memberPriceC.value) {
    list.push({ price: mulTenThousand($comProvide.memberPriceC.value), text: `黑卡折扣` })
  }
  return list
})
</script>

<template>
  <view class="arrow">
    <u-icon name="arrow-up" margin="0" class="arrow--icon" color="#F12F22" />
  </view>
  <view class="forecast-info">
    <view class="forecast-info__title">现在购买，享受以下优惠</view>
    <scroll-view scroll-x enhanced :show-scrollbar="false" style="white-space: nowrap; width: 100%; height: 120rpx">
      <view class="forecast-info__main">
        <forecast-item v-for="(i, idx) in discountList" :key="i.text" :idx="idx" :length="discountList.length - 1" :info="i" />
      </view>
    </scroll-view>
  </view>
</template>

<style scoped lang="scss">
@include b(arrow) {
  position: relative;
  height: 40rpx;
  @include m(icon) {
    position: absolute;
    left: 50%;
    transform: translateX(-50%);
    bottom: -9rpx;
    z-index: 9;
  }
}
@include b(forecast-info) {
  position: relative;
  padding: 40rpx 14rpx;
  border-radius: 10rpx;
  background-color: rgba(255, 255, 255, 1);
  text-align: center;
  border: 1px solid #f54319;
  &::before {
    content: '';
    position: absolute;
    left: 50%;
    transform: translateX(-50%);
    display: inline-block;
    width: 20rpx;
    top: -2rpx;
    height: 3px;
    background: #fff;
    z-index: 8;
  }
  @include e(title) {
    color: rgba(16, 16, 16, 1);
    font-size: 24rpx;
    font-family: PingFangSC-regular;
  }
  @include e(main) {
    display: flex;
    margin-top: 25rpx;
    align-items: center;
    justify-content: center;
  }
}
</style>
