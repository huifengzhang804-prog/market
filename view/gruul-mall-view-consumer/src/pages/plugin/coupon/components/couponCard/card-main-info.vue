<script setup lang="ts">
import type { PropType } from 'vue'
import { couponQueryStatusCn, claimedStatus } from '@plugin/coupon/utils'
import type { CouponQueryStatusJointType } from '@/apis/plugin/coupon/model'
import { CLAIMED_STATUS } from '@/apis/plugin/coupon/model'

const $props = defineProps({
  name: {
    type: String,
    default: '#fceced',
  },
  permissions: {
    type: String,
    default: '',
  },
  time: {
    type: String,
    default: '',
  },
  // 有用状态
  notUsedStatus: {
    type: String as PropType<keyof typeof CLAIMED_STATUS>,
    default: 'UNCLAIMED',
  },
  // 没用状态
  usedStatus: {
    type: String as PropType<CouponQueryStatusJointType>,
    default: 'UNCLAIMED',
  },
  status: {
    type: String as PropType<CouponQueryStatusJointType>,
    default: 'UNCLAIMED',
  },
  watermark: { type: Boolean, default: false },
  isCar: { type: Boolean, default: false },
  // 是否领券中心
  couponCenter: { type: Boolean, default: false },
})
const emit = defineEmits(['receiveClick'])
</script>

<template>
  <view class="container">
    <view class="container__title">
      <view style="margin-bottom: 10px">{{ $props.name }}</view>
      <slot name="default"> </slot>
    </view>
    <view v-if="$props.isCar" class="container__title">{{ $props.permissions }}</view>
    <view class="container__footer">
      <text dominant-baseline="baseline" y="7.14" style="line-height: 1; vertical-align: middle" class="container__footer--time">
        {{ $props.time }}
      </text>
      <template v-if="$props.status === 'UNCLAIMED'">
        <view v-if="$props.watermark" class="container__footer--button container__footer--disabled"> 已领取 </view>
        <view
          v-else-if="$props.couponCenter"
          class="container__footer--button"
          :custom-style="{ width: '120rpx', fontSize: '12rpx', marginRight: '15rpx' }"
          @click.stop="emit('receiveClick', couponQueryStatusCn[$props.usedStatus])"
        >
          {{ claimedStatus[$props.notUsedStatus].text }}
        </view>
        <view
          v-else-if="$props.isCar"
          class="container__footer--button"
          :custom-style="{ width: '120rpx', fontSize: '12rpx', marginRight: '15rpx' }"
          @click.stop="emit('receiveClick', couponQueryStatusCn[$props.usedStatus])"
        >
          {{ couponQueryStatusCn[$props.usedStatus] }}
        </view>
        <view
          v-else
          class="container__footer--button"
          :custom-style="{ width: '120rpx', fontSize: '12rpx', marginRight: '15rpx' }"
          @click.stop="emit('receiveClick', claimedStatus[$props.notUsedStatus])"
        >
          {{ claimedStatus[$props.notUsedStatus].text }}
        </view>
      </template>
    </view>
  </view>
</template>

<style scoped lang="scss">
@include b(container) {
  flex: 1;
  height: 100%;
  padding: 15rpx 0rpx 15rpx 14rpx;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  justify-content: space-between;
  @include e(title) {
    position: relative;
    width: auto;
    @include utils-ellipsis;
    font-size: 26rpx;
    font-weight: 700;
    color: #333333;
  }
  @include e(footer) {
    width: 100%;
    display: flex;
    justify-content: space-between;
    align-items: center;
    @include m(time) {
      flex: 1;
      font-size: 24rpx;
      color: #838383;
    }

    @include m(button) {
      position: absolute;
      border-radius: 20rpx;
      line-height: 170rpx;
      font-size: 24rpx;
      background-color: #f54319;
      color: rgba(255, 255, 255, 1);
      text-align: center;
      right: 0;
      top: 0;
      bottom: 0;
      width: 152rpx;
    }
    @include m(disabled) {
      background-color: #fff;
      color: rgba(255, 132, 124, 1);
      border: 1px solid rgba(255, 132, 124, 1);
    }
  }
}
</style>
