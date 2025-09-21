<script setup lang="ts">
import QIcon from '@/components/q-icon/q-icon.vue'
import { computed, type PropType } from 'vue'

interface BtnOptions {
  color: string
  backgroundColor: string
  text: string
}

const $props = defineProps({
  btnOptions: {
    type: Array as PropType<BtnOptions[]>,
    default: () => [
      {
        color: '#fff',
        backgroundColor: '#333333',
        text: '加入购物车',
      },
      {
        color: '#fff',
        backgroundColor: '#F54319',
        text: '立即购买',
      },
    ],
  },
  loading: {
    type: Boolean,
    default: false,
  },
  disabled: {
    type: Boolean,
    default: false,
  },
  noShipService: {
    type: Boolean,
    default: false,
  },
})
const $emit = defineEmits(['btnClick', 'chooseShop', 'consumerSever', 'goodsCar'])
const safeHeight = useBottomSafe()
const barHeight = uni.upx2px(140)
const computedBarHeight = computed(() => {
  return safeHeight.value + barHeight + 'px'
})
const btnClick = (option: BtnOptions) => {
  if ($props.disabled) {
    return
  }
  $emit('btnClick', option)
}
</script>

<template>
  <view class="footer" :style="{ height: computedBarHeight }">
    <view class="bar">
      <view class="bar__tool">
        <view class="bar__tool-item" @click="$emit('chooseShop')">
          <q-icon name="icon-dianpu4" color="#999" size="44rpx" />
          <text>店铺</text>
        </view>
        <view class="bar__tool-item" @click="$emit('consumerSever')">
          <q-icon name="icon-svg-2" color="#999" size="44rpx" />
          <text>客服</text>
        </view>
        <view class="bar__tool-item" @click="$emit('goodsCar')">
          <q-icon name="icon-svg" color="#999" size="44rpx" />
          <text>购物车</text>
        </view>
      </view>
      <view class="bar__right">
        <view v-if="disabled" class="disabled_btn disabled">已售罄</view>
        <view v-else-if="noShipService" class="disabled_btn disabled">无物流服务</view>
        <view
          v-for="(option, index) in btnOptions"
          v-else
          :key="index"
          :style="{ backgroundColor: option.backgroundColor, color: option.color }"
          class="bar__item"
          :class="{ 'bar__buy-now': btnOptions.length === 1, bar__black: index === 0, loading, disabled }"
          @click="btnClick(option)"
        >
          {{ option.text }}
        </view>
      </view>
    </view>
    <view :style="{ height: safeHeight + 'px' }"></view>
  </view>
</template>

<style lang="scss" scoped>
@import '@/assets/css/mixins/mixins.scss';
@include b(footer) {
  background-color: #fff;
  position: fixed;
  bottom: 0;
  z-index: 9;
  width: 100vw;
}
@include b(bar) {
  height: 140rpx;
  font-size: 24rpx;
  @include flex(space-between);
  @include e(tool) {
    height: 140rpx;
    background: #fff;
    display: flex;
    align-items: center;
  }
  @include e(tool-item) {
    width: 100rpx;
    display: flex;
    flex-direction: column;
    align-items: center;
    font-size: 24rpx;
    letter-spacing: 2px;
  }
  @include e(right) {
    display: flex;
    height: 80rpx;
    border-radius: 16rpx;
    margin-right: 16rpx;
  }
  @include e(item) {
    width: 212rpx;
    text-align: center;
    font-size: 28rpx;
    background: #f54319;
    line-height: 80rpx;
    color: #fff;
    border-radius: 0 16rpx 16rpx 0;
  }
  .disabled_btn {
    width: 424rpx;
    text-align: center;
    font-size: 28rpx;
    background: #f54319;
    line-height: 80rpx;
    color: #fff;
    border-radius: 16rpx;
  }
  .disabled {
    background: #ccc !important;
  }
  @include e(black) {
    background: #595754;
    border-radius: 16rpx 0 0 16rpx;
  }
  @include e(buy-now) {
    // width: 100%;
    background: #fa3534;
    border-radius: 16rpx;
  }
}
</style>
