<script lang="ts" setup>
import { computed, type PropType } from 'vue'
import { type SeckillRoundProductVO, SeckillStatus, SeckillQueryStatus } from '@/apis/plugin/secKill/model'
import { Decimal } from 'decimal.js-light'

const { divTenThousand } = useConvert()
const $emit = defineEmits(['click'])
const $props = defineProps({
  product: {
    type: Object as PropType<SeckillRoundProductVO>,
    required: true,
  },
  status: {
    type: String as PropType<SeckillQueryStatus>,
    required: true,
  },
})
const isSoldOut = computed(() => new Decimal($props.product.activityStock).lessThan(1))
</script>

<template>
  <view class="seconds-kill-goods" @click="$emit('click')">
    <view style="position: relative">
      <u-image :height="180" :src="product.productImage" :width="180" />
      <view v-if="isSoldOut" class="gone">
        <text>已售馨</text>
        <text>Sold out</text>
      </view>
    </view>
    <view class="seconds-kill-goods__info">
      <view class="seconds-kill-goods__info--title">{{ product.productName }}</view>
      <view class="seconds-kill-goods__info--msg"> 秒杀中！限时直降抢购！</view>
      <view class="seconds-kill-goods__info--price">
        <text class="seconds-kill-goods__info--price-num">{{ product.minPrice && divTenThousand(product.minPrice) }} </text>
        <text v-if="isSoldOut" class="seconds-kill-goods__info--botton"> 已抢光</text>
        <text v-else :class="SeckillStatus[status].buyButtonClass" class="seconds-kill-goods__info--botton">
          {{ SeckillStatus[status].buyButtonText }}
        </text>
      </view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
@include b(seconds-kill-goods) {
  display: flex;
  margin: 20rpx 30rpx 0 30rpx;
  background: #ffffff;
  border-radius: 16rpx;
  padding: 30rpx;
  @include e(info) {
    flex: 1;
    padding: 10rpx 10rpx 0 15rpx;
    overflow: hidden;
    @include m(title) {
      font-size: 28rpx;
      font-weight: Bold;
      color: #323232;
      @include utils-ellipsis(1);
    }
    @include m(msg) {
      font-size: 24rpx;
      color: #525252;
      margin: 20rpx 0 20rpx 0;
      @include utils-ellipsis(1);
    }
    @include m(botton) {
      display: inline-block;
      padding: 15rpx 30rpx;
      background: #a7a7a7;
      border-radius: 34rpx;
      font-size: 24rpx;
      text-align: CENTER;
      color: #ffffff;
    }
    @include m(price) {
      justify-content: space-between;
      display: flex;
    }
    @include m(price-num) {
      font-size: 35rpx;
      font-weight: Bold;
      color: #dd3324;
      &::before {
        content: '￥';
        font-size: 24rpx;
        color: #dd3324;
      }
    }
  }
}

// 抢光遮罩
@include b(gone) {
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  width: 170rpx;
  height: 170rpx;
  background: rgba(0, 0, 0, 0.51);
  border: 1px solid #ffffff;
  border-radius: 50%;
  font-size: 24rpx;
  text-align: center;
  color: #ffffff;
  display: flex;
  flex-direction: column;
  justify-content: space-evenly;
  padding: 30rpx;
}

// 马上抢
@include b(snapped-up-immediately) {
  background: linear-gradient(90deg, #e94927, #dd3324);
  color: #ffffff;
}

// 即将抢购
@include b(snap-up) {
  background: linear-gradient(90deg, #56e373, #4cc17e);
  color: #ffffff;
}
</style>
