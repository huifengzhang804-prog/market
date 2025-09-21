<script setup lang="ts">
import type { PropType } from 'vue'
import type { ApiMessageShop, ILastMessage } from '@/basePackage/pages/customerService/types'
import { MessageType } from '@/hooks/stomp/typs'
import DateUtil from '@/utils/date'

const $props = defineProps({
  message: {
    type: Object as PropType<ApiMessageShop>,
    required: true,
  },
})

const renderMessage = (message: ILastMessage) => {
  if (!message) return ''
  switch (message.messageType) {
    case MessageType.UN_HANDLE:
      return '[未处理消息]'
    case MessageType.PRODUCT:
      return '[商品]'
    case MessageType.IMAGE:
      return '[图片]'
    case MessageType.ORDER:
      return '[订单]'
    default:
      return message.message
  }
}

const renderTime = (time: any) => {
  if (!time) return ''
  const lastTime = new Date(+time)
  const dateUtil = new DateUtil(lastTime)
  const isToday = new Date().getDay() === lastTime.getDay()
  return isToday ? dateUtil.getH() + ':' + dateUtil.getMin() : dateUtil.getYMD()
}
const handleNavToCustomerService = () => {
  const { shopId, shopLogo, shopName } = $props.message.chatWithShopInfo
  uni.navigateTo({ url: `/basePackage/pages/customerService/CustomerService?shopId=${shopId}&shopLogo=${shopLogo}&shopName=${shopName}` })
}
</script>

<template>
  <view class="main" @click="handleNavToCustomerService">
    <view class="main__left">
      <image class="main__left--image" :src="$props.message.chatWithShopInfo.shopLogo"> </image>
      <view v-if="!$props.message.lastMessage?.read" class="main__left--badge"></view>
    </view>
    <view class="main__right">
      <view class="main__right-title">
        <text class="main__right-title--name">{{ $props.message.chatWithShopInfo.shopName }}</text>
        <text class="main__right-title--time">{{ renderTime($props.message.lastMessage?.sendTime) }}</text>
      </view>
      <view v-if="$props.message.lastMessage" class="main__right--msg">
        {{ renderMessage($props.message.lastMessage) }}
      </view>
    </view>
  </view>
  <view class="line" />
</template>

<style scoped lang="scss">
@include b(main) {
  margin: 30rpx 28rpx;
  height: 94rpx;
  @include flex;
  @include e(left) {
    position: relative;
    width: 94rpx;
    margin-right: 22rpx;
    @include m(image) {
      width: 94rpx;
      height: 94rpx;
      border-radius: 50%;
      border: 5rpx solid salmon;
    }
    @include m(badge) {
      position: absolute;
      top: 0rpx;
      right: 0rpx;
      width: 24rpx;
      height: 24rpx;
      border: 2rpx solid #ffffff;
      border-radius: 50rpx;
      font-size: 18rpx;
      color: #ffffff;
      line-height: 26rpx;
      background: #fe2b32;
      text-align: center;
    }
  }
  @include e(right) {
    flex: 1;
    height: 94rpx;
    @include flex;
    flex-direction: column;
    justify-content: space-between;
    align-items: flex-start;
    @include m(msg) {
      width: 566rpx;
      @include utils-ellipsis;
      font-size: 26rpx;
      color: #9c9c9c;
    }
  }
  @include e(right-title) {
    width: 100%;
    @include flex(space-between);
    @include m(name) {
      width: 340rpx;
      @include utils-ellipsis;
      font-size: 28rpx;
      color: #713a2f;
    }
    @include m(time) {
      font-size: 22rpx;
      color: #9c9c9c;
    }
  }
}
@include b(line) {
  margin: 0 60rpx;
  height: 0rpx;
  border: 2rpx solid #ebebeb;
}
</style>
