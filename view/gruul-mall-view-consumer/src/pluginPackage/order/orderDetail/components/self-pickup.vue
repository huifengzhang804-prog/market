<template>
  <view class="pickup">
    <view class="pickup__header">
      <text class="pickup__header--title">提货人信息</text>
      <text class="pickup__header--notice">请按时到店自提</text>
      <text class="pickup__header--status" :class="{ checked: shouldWriteOff }">{{ shouldWriteOff ? '待核销' : '已核销' }}</text>
    </view>
    <view class="pickup__content">
      <view class="pickup__content--info">
        <view class="line">
          <text class="line__label">核销码</text>
          <text class="line__value">{{ $props.info.code }}</text>
          <text class="line__copy" @click="copyCode">复制</text>
        </view>
        <!-- <view class="line">
                    <text class="line__label">提货人</text>
                    <text class="line__value">{{ $props.info.personName }}</text>
                    <text>{{ $props.info.personPhone }}</text>
                </view> -->
        <view class="line">
          <text class="line__label">提货时间</text>
          <text class="line__value">{{ $props.info.pickupTime }}</text>
        </view>
      </view>
      <u-qrcode v-if="!amplify" ref="qrcodeRef" canvas-id="qrcode" size="64" :value="$props.info.code" @click="handleTap" />
    </view>
  </view>
  <u-popup v-model="amplify" mode="center" :closeable="true" :mask="true" :safeAreaInsetBottom="true" :maskCloseAble="false" width="80%">
    <view class="cont">
      <view class="imgbox">
        <u-qrcode v-if="amplify" ref="qrcodeRef" canvas-id="qrcode" size="230" :value="$props.info.code" />
      </view>
    </view>
  </u-popup>
</template>

<script lang="ts" setup>
import { type PropType, computed, ref } from 'vue'
import UQrcode from '@/asyncPackages/uqrcode/components/u-qrcode/u-qrcode.vue'
import type { OrderStatusPlus } from '@/hooks'

const $props = defineProps({
  info: {
    type: Object as PropType<{ code?: string; personName: string; personPhone: string; pickupTime?: string; orderStatus: OrderStatusPlus }>,
    required: true,
  },
})

const shouldWriteOff = computed(() => {
  if ($props.info.orderStatus.isClosed === false && ['待发货', '待收货'].includes($props.info.orderStatus.desc)) {
    return true
  }
  return false
})
const copyCode = () => {
  uni.setClipboardData({
    data: $props.info.code || '',
  })
}
const amplify = ref(false)
const handleTap = () => {
  amplify.value = true
}
</script>

<style lang="scss" scoped>
@include b(pickup) {
  background-color: #fff;
  width: 710rpx;
  margin: 20rpx auto 0;
  border-radius: 20rpx;
  padding: 0 20rpx;
  @include e(header) {
    @include flex(space-between);
    border-bottom: 1rpx dashed #bdbdbd;
    padding-left: 10rpx;
    padding-right: 22rpx;
    height: 70rpx;
    line-height: 70rpx;
    @include m(title) {
      font-size: 28rpx;
      color: #333;
    }
    @include m(notice) {
      font-size: 24rpx;
      color: #999;
    }
    @include m(status) {
      font-size: 28rpx;
      color: #999;
      &.checked {
        color: #fa3534;
      }
    }
  }
  @include e(content) {
    @include flex(space-between);
    padding: 20rpx 22rpx 20rpx 10rpx;
    @include m(code) {
      width: 128rpx;
      height: 128rpx;
      flex-shrink: 0;
    }
    @include m(info) {
      @include b(line) {
        line-height: 36rpx;
        font-size: 26rpx;
        @include flex(space-between);
        @include e(label) {
          width: 104rpx;
          text-align: right;
          display: inline-block;
          margin-right: 10rpx;
        }
        @include e(value) {
          flex: 1;
        }
        @include e(copy) {
          color: #005cf4;
        }
      }
      .line + .line {
        margin-top: 10rpx;
      }
    }
  }
}
.cont {
  display: flex;
  align-items: center;
  .imgbox {
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 50rpx 0;
  }
  .imgClose {
    width: 28px;
    height: 28px;
    border-radius: 50%;
    border: 1px solid #fff;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
    margin-top: 40px;
  }
}
</style>
