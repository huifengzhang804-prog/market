<script setup lang="ts">
import type { PropType } from 'vue'
import type { CurrentProduct } from '@/basePackage/pages/customerService/types'

const $props = defineProps({
  info: { type: Object as PropType<CurrentProduct>, required: true },
})
const $emit = defineEmits(['sendClick'])
const { divTenThousand } = useConvert()
</script>

<template>
  <view class="product-popup">
    <view class="product-popup__box">
      <view class="product-popup__box-image"> <u-image width="100%" :height="160" :src="$props.info.pic" border-radius="15" /> </view>
      <!-- 订单详情提跳转时 展示 -->
      <view v-if="$props.info.no" class="product-popup__box-info">
        <view class="product-popup__box-info--no">{{ $props.info.no }}</view>
        <!--  订单号 采用定位 脱离文档流 占位-->
        <view class="f13" style="opacity: 0">1</view>
        <!-- 占位 -->
        <view>你可能想咨询该订单</view>
        <view>
          实付 <text>{{ info.amountRealPay }}</text>
        </view>
      </view>
      <!-- 订单详情提跳转时 展示 -->
      <!-- 商品详情提跳转时 展示 -->
      <view v-else class="product-popup__box-info">
        <text class="product-popup__box-info--name">{{ $props.info.name }}</text>
        <text>{{ $props.info.price.estimate && divTenThousand($props.info.price.estimate) }} 起</text>
      </view>
      <!-- 商品详情提跳转时 展示 -->
      <view class="product-popup__box-send">
        <u-button shape="circle" size="mini" type="warning" @click="$emit('sendClick')">发送{{ info.no ? '订单' : '商品' }}</u-button>
      </view>
    </view>
  </view>
</template>

<style scoped lang="scss">
// 商品弹窗
@include b(product-popup) {
  position: absolute;
  left: 0;
  right: 0;
  top: -200rpx;
  height: 200rpx;
  padding: 10rpx 30rpx;
  z-index: 9;
  @include e(box) {
    border-radius: 15rpx;
    background: #fff;
    height: 100%;
    @include flex(space-between);
  }

  @include e(box-image) {
    flex-shrink: 0;
    width: 160rpx;
    margin-left: 10rpx;
  }
  @include e(box-info) {
    width: 50%;
    flex: 1;
    padding: 0 20rpx;
    @include flex;
    height: 160rpx;
    flex-direction: column;
    justify-content: space-around;
    align-items: flex-start;
    @include m(no) {
      position: absolute;
      top: 25rpx;
    }
    & > text:nth-child(1) {
      width: 100%;
      font-weight: 700;
      @include utils-ellipsis(1);
    }
    & > text:nth-child(2) {
      color: red;
      &::before {
        content: '￥';
        color: red;
      }
    }
  }
  @include e(box-send) {
    margin: 0 20rpx;
  }
}
</style>
