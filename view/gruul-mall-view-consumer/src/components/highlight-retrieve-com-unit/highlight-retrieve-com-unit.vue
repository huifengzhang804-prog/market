<script lang="ts" setup>
import QPrice from '@/components/q-price/index.vue'
import { computed, type PropType } from 'vue'
import { Decimal } from 'decimal.js-light'

const $props = defineProps({
  goodName: {
    type: String,
    default: '',
  },
  price: {
    type: Array as PropType<Long[]>,
    default: () => [0],
  },
  saleVolume: {
    type: [String, Number],
    default: '0',
  },
})
const { salesVolumeToStr } = useConvert()
const content = computed(() => $props.goodName)

// 最低价格
const minPrice = computed(() => {
  return $props.price
    .map((amount) => new Decimal(amount))
    .reduce((prev, current) => (prev.lessThan(current) ? prev : current), new Decimal($props.price[0]))
})
</script>

<template>
  <view class="good good-right">
    <view class="good__content">
      <view class="good__content--title">
        <rich-text :nodes="content" />
      </view>
      <view class="good__content-price">
        <view v-if="$props.price" class="good__content-price--number">
          <q-price :price="minPrice" font-size="28rpx" unit="¥" />
        </view>
        <view v-if="+salesVolumeToStr($props.saleVolume) > 0" class="good__content-price--count">已售 {{ salesVolumeToStr($props.saleVolume) }}</view>
      </view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
@include b(good) {
  box-sizing: border-box;
  border-radius: 0 0 16rpx 16rpx;
  margin-bottom: 10rpx;
  background-color: #ffffff;
  padding: 20rpx;
  position: relative;
  @include e(content) {
    @include m(title) {
      font-size: 28rpx;
      color: #000000;
      font-weight: 700;
      width: 320rpx;
      :deep(uni-rich-text) {
        div {
          @include utils-ellipsis(1);
        }
      }
    }
  }
  @include e(content-price) {
    @include flex(space-between);
    @include m(number) {
      font-size: 36rpx;
      color: #f83f30;
      font-weight: 700;
    }
    @include m(count) {
      font-size: 24rpx;
      color: #9a9a9a;
    }
  }
}

@include b(good-left) {
  margin-left: 0;
}

@include b(good-right) {
  margin-right: 0;
}
</style>
