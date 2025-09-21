<script setup lang="ts">
import { computed, ref, type PropType } from 'vue'
import ContentText from '@/basePackage/pages/customerService/cmponents/content-text.vue'
import ContentImage from '@/basePackage/pages/customerService/cmponents/content-image.vue'
import ContentProduct from '@/basePackage/pages/customerService/cmponents/content-product.vue'
import ContentOrder from '@/basePackage/pages/customerService/cmponents/content-order.vue'
import type { Message, ShopData } from '@/basePackage/pages/customerService/types'
import DateUtil from '@/utils/date'
import { queryConfigByModule } from '@/apis/order'

const $props = defineProps({
  message: { type: Array as PropType<Message[]>, required: true },
  shopInfo: { type: Object as PropType<ShopData>, required: true },
})

const isMine = (message: Message) => {
  return message.sender.senderType === 'SHOP_ADMIN' || message.sender.senderType === 'PLATFORM_ADMIN'
}
const currentMessages = computed(() => {
  const newArr: Message[] = []
  $props.message.forEach((item) => {
    newArr.unshift(item)
  })
  return newArr
})

const renderTime = (time: any) => {
  if (!time) return ''
  const lastTime = new Date(+time)
  const dateUtil = new DateUtil(lastTime)
  const isToday = new Date().getDay() === lastTime.getDay()
  return isToday ? dateUtil.getH() + ':' + dateUtil.getMin() : dateUtil.getYMD()
}
const shopLogo = ref($props.shopInfo.shopLogo)
const getConfigByModule = async () => {
  const { code, data } = await queryConfigByModule('PUBLIC_SETTING, PLATFORM_SETTING, SHOP_SETTING, SUPPLIER_SETTING')
  if (code === 200 && data) {
    shopLogo.value = data.PLATFORM_LOGO
  }
}
getConfigByModule()
</script>

<template>
  <view v-for="msg in currentMessages" :key="msg.sendTime" class="chat" :class="isMine(msg) ? '' : 'coustomer'">
    <view class="chat__time">
      <text>{{ renderTime(msg.sendTime) }}</text>
    </view>
    <view class="chat__body" :class="isMine(msg) ? '' : 'coustomer__body'">
      <view class="chat__body-avatar">
        <u-avatar v-if="msg.sender.senderUserInfo" :src="msg.sender.senderUserInfo.avatar" mode="circle" img-mode=""> </u-avatar>
        <u-avatar
          v-if="msg.sender.senderShopInfo"
          :src="msg.sender.senderShopInfo.shopLogo ? msg.sender.senderShopInfo.shopLogo : shopLogo"
          mode="circle"
          img-mode=""
        >
        </u-avatar>
      </view>
      <view class="chat__body--content">
        <ContentText v-if="msg.messageType === 'TEXT'" :is-mine="isMine(msg)" :msg="msg.message" />
        <ContentImage v-if="msg.messageType === 'IMAGE'" :msg="msg.message" :is-mine="isMine(msg)" />
        <ContentProduct v-if="msg.messageType === 'PRODUCT'" :msg="msg.message" :is-mine="isMine(msg)" :shop-id="$props.shopInfo.shopId" />
        <ContentOrder v-if="msg.messageType === 'ORDER'" :msg="msg.message" :is-mine="isMine(msg)" :shop-id="$props.shopInfo.shopId" />
      </view>
    </view>
  </view>
</template>

<style scoped lang="scss">
$content-bg: #606266;
@include b(chat) {
  @include e(time) {
    padding: 10rpx 0;
    text-align: center;
    & > text {
      font-size: 24rpx;
    }
  }
  @include e(body) {
    width: 85%;
    @include flex;
    align-items: flex-start;
    justify-content: flex-start;
  }
  @include e(body-avatar) {
  }
  @include e(body-content) {
    width: 100%;
    position: relative;
    margin-left: 20rpx;
    border-radius: 15rpx;
    background: $content-bg;
    padding: 20rpx;
    &::after {
      content: '';
      position: absolute;
      left: -10rpx;
      top: 35rpx;
      height: 0;
      width: 0;
      border-top: 10rpx solid transparent;
      border-right: 10rpx solid $content-bg;
      border-bottom: 10rpx solid transparent;
    }
  }
}
@include b(coustomer) {
  @include e(body) {
    width: 100%;
    flex-direction: row-reverse;
    justify-content: flex-start;
    padding-left: 100rpx;
  }
  @include e(body-content) {
    width: 100%;
    margin: 0 20rpx 0 0;
    &::after {
      display: none;
    }
    &::before {
      content: '';
      position: absolute;
      top: 35rpx;
      right: -10rpx;
      height: 0;
      width: 0;
      border-top: 10rpx solid transparent;
      border-left: 10rpx solid $content-bg;
      border-bottom: 10rpx solid transparent;
    }
  }
}
</style>
