<script lang="ts" setup>
import { computed, inject, type PropType } from 'vue'
import type { comDispatcherType, ProductResponse, StorageSku } from '@/pluginPackage/goods/commodityInfo/types'
import { Decimal } from 'decimal.js-light'

const $props = defineProps({
  name: {
    type: String,
    default: '',
  },
  sku: {
    type: Object as PropType<StorageSku>,
    default() {
      return {}
    },
  },
  goodInfo: {
    type: Object as PropType<ProductResponse>,
    default() {
      return {}
    },
  },
  evaluateInfo: {
    type: Object,
    default() {
      return {}
    },
  },
  // 是否隐藏商品价格
  showPrice: {
    type: Boolean,
    default: true,
  },
})
const { divTenThousand, salesVolumeToStr } = useConvert()
const $comProvide = inject('comProvide') as comDispatcherType

const isEquation = computed(() => $comProvide.forecastPrice.value !== Number($props.sku.salePrice).toFixed(2))
const computedPrice = computed(() => {
  if ($comProvide.forecastPrice) {
    let actualPrice = divTenThousand($comProvide.forecastPrice.value).toString()
    const priceArray = actualPrice?.split('.')
    const integer = priceArray?.[0] || ''
    const decimal = (priceArray?.[1] || '').slice(0, 2)
    return {
      integer,
      decimal,
    }
  }
  return {
    integer: 0,
    decimal: 0,
  }
})
//sku销量
const skuSale = computed(() => {
  const salesVolume = $props.sku.salesVolume
  const initSalesVolume = $props.sku.initSalesVolume
  const totalSale = new Decimal(salesVolume || 0).add(initSalesVolume || 0)
  return totalSale.gte(10000) ? totalSale.div(10000).toFixed(2) + 'w' : totalSale.toString()
})
</script>

<template>
  <view class="selling">
    <view v-if="$props.showPrice" class="selling__info">
      <view class="salling">
        <view class="selling__info--main">
          <view style="background: rgba(245, 67, 25, 0.1); padding: 8rpx 20rpx 8rpx 20rpx; border-radius: 318rpx; display: flex; align-items: center">
            <span style="font-size: 28rpx; font-weight: 400; color: rgb(245, 67, 25); margin-right: 8rpx">预估到手</span>
            <text class="selling__info--price">
              <text style="font-size: 40rpx">{{ computedPrice.integer || 0 }}</text>
              <text v-if="computedPrice.decimal" style="font-size: 32rpx">.</text>
              <text v-if="computedPrice.decimal" style="font-size: 32rpx">{{ computedPrice.decimal }}</text>
              <!-- {{ $comProvide.forecastPrice && divTenThousand($comProvide.forecastPrice.value).toFixed(2) }} -->
            </text>
          </view>
          <text v-if="isEquation" style="color: rgb(153, 153, 153); margin-left: 20rpx; font-size: 22rpx">
            ￥<text class="selling__info--original-price">
              {{ $props.sku.price && Number(divTenThousand($props.sku.price)).toFixed(2) }}
            </text>
          </text>
        </view>
      </view>
      <view>
        <slot name="card" />
      </view>
    </view>

    <view class="selling__line">
      <view class="selling__line-shop">
        <view class="selling__line-shop--name">
          {{ $props.name }}
        </view>
      </view>
    </view>
    <div v-if="$props.showPrice" class="selling__line-shop--total">
      <view
        class="selling__line-shop--count"
        :style="{ width: '100%', justifyContent: $props.sku.limitType === 'UNLIMITED' ? 'space-between' : 'flex-start' }"
      >
        <view style="margin-right: 40rpx"> 销量:{{ skuSale }}</view>
        <view>好评率: {{ evaluateInfo.praiseRatio ? evaluateInfo.praiseRatio : 0 }}%</view>
        <view v-if="$props.sku.limitType !== 'UNLIMITED'" style="margin-left: 40rpx">限购: {{ $props.sku.limitNum }} </view>
      </view>
    </div>
  </view>
</template>

<style lang="scss" scoped>
@include b(selling) {
  box-sizing: border-box;
  @include e(line) {
    font-size: 32rpx;
    line-height: 52rpx;
    display: flex;
    align-items: center;
    margin-top: 20rpx;
    @include m(collect) {
      text-align: center;
      font-size: 24rpx;
      color: #999999;
      width: 100rpx;
      height: 80rpx;
      padding-left: 10rpx;
      border-left: 1px solid #ccc;
    }
  }
  @include e(line-shop) {
    flex: 1;
    @include m(total) {
      margin-top: 20rpx;
      @include flex(space-between);
    }
    @include m(count) {
      font-size: 24rpx;
      color: rgb(156, 155, 155);
      display: flex;
      align-items: center;
    }
    @include m(name) {
      color: #1e1c1c;
      font-weight: 700;
      width: 100%;
      @include utils-ellipsis(2);
    }
  }
  @include e(info) {
    display: flex;
    justify-content: space-between;
    align-items: center;
    @include m(integral_price) {
      margin-left: 10rpx;
      font-weight: 700;
      font-size: 44rpx;
      color: #e31436;
    }
    @include m(original-price) {
      text-decoration: line-through;
      display: inline-block;
      font-size: 24rpx;
    }
    @include b(salling) {
      width: 100%;
    }
    @include m(main) {
      display: flex;
      // justify-content: space-between;
      align-items: center;
      width: 100%;
    }
    @include m(price) {
      display: flex;
      align-items: center;
      color: rgb(245, 67, 25);
      font-weight: 400;
      &::before {
        font-weight: 400;
        font-size: 26rpx;
        content: '￥';
      }
    }
    @include m(num) {
      color: #999999;
      font-size: 20rpx;
      @include flex(space-between);
      & > text {
        padding-left: 22rpx;
      }
    }
    @include m(rebate) {
      font-size: 18rpx;
      color: #fff;
      text-align: center;
      height: 50rpx;
      line-height: 50rpx;
      background-color: #fa3534;
      border-radius: 4rpx;
      padding: 0 20rpx;
    }
  }
  @include e(preferential) {
    padding: 10rpx 0;
    color: #333;
    font-size: 24rpx;
    & > text:nth-child(2) {
      padding-left: 10rpx;
    }
  }
}
</style>
