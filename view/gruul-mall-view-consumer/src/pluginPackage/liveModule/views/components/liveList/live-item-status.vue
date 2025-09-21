<script setup lang="ts">
import type { PropType } from 'vue'
import type { RoomStatusJointType } from '@pluginPackage/live/components/types'

const $props = defineProps({
  active: {
    type: String as PropType<RoomStatusJointType>,
    required: true,
  },
})

const statusConfig = {
  PROCESSING: {
    bgC: '#FF1E32',
    text: '直播中',
    animation: '',
  },
  LIVE_BROADCAST: {
    bgC: '#FF1E32',
    text: '直播中',
    animation: '',
  },
  NOT_STARTED: {
    bgC: '#1EFFB8',
    text: '未开始',
    animation: 'none',
  },
  OVER: {
    bgC: '#CCCCCC',
    text: '已结束',
    animation: 'none',
  },
  CLOSED: {
    bgC: '#CCCCCC',
    text: '已结束',
    animation: 'none',
  },
  ILLEGAL_SELL_OFF: {
    bgC: '#CCCCCC',
    text: '已下架',
    animation: 'none',
  },
}
</script>

<template>
  <view class="room-status">
    <view
      class="room-status--icon"
      :class="{ choosed: $props.active === 'LIVE_BROADCAST' }"
      :style="{ backgroundColor: statusConfig[$props.active].bgC }"
    >
      <text class="room-status--icon-center left"></text>
      <text class="room-status--icon-center"></text>
      <text class="room-status--icon-center right"></text>
    </view>
    <text class="room-status--text">{{ statusConfig[$props.active].text }}</text>
  </view>
</template>

<style scoped lang="scss">
@include b(room-status) {
  display: flex;
  align-items: center;
  padding: 5rpx 10rpx 5rpx 5rpx;
  position: absolute;
  color: #fff;
  font-weight: 700;
  min-width: 200rpx;
  border-radius: 24rpx;
  background: rgba(0, 0, 0, 0.1);
  top: 10rpx;
  font-size: 24rpx;
  left: 10rpx;
  @include m(icon) {
    display: inline-flex;
    justify-content: space-evenly;
    align-items: flex-end;
    padding: 0 10rpx 10rpx 10rpx;
    width: 54rpx;
    height: 40rpx;
    border-radius: 24rpx;
    background-color: rgba(30, 255, 184, 1);
    & > text:nth-child(1) {
      display: inline-block;
      width: 5rpx;
      background: #fff;
      border-radius: 10rpx;
      height: 66%;
    }
    & > text:nth-child(2) {
      display: inline-block;
      width: 5rpx;
      background: #fff;
      border-radius: 10rpx;
      height: 50%;
    }
    & > text:nth-child(3) {
      display: inline-block;
      width: 5rpx;
      background: #fff;
      border-radius: 10rpx;
      height: 66%;
    }
  }
  @include m(text) {
    margin-left: 5rpx;
  }
}
@include b(choosed) {
  & > text:nth-child(1) {
    display: inline-block;
    width: 5rpx;
    background: #fff;
    border-radius: 10rpx;
    height: 0;
    animation: left alternate 1s ease infinite;
  }
  & > text:nth-child(2) {
    display: inline-block;
    width: 5rpx;
    background: #fff;
    border-radius: 10rpx;
    height: 0;
    animation: room alternate 1.2s ease infinite;
  }
  & > text:nth-child(3) {
    display: inline-block;
    width: 5rpx;
    background: #fff;
    border-radius: 10rpx;
    height: 0;
    animation: left alternate 0.8s ease infinite;
  }
}
@keyframes room {
  10% {
    height: 10%;
  }
  30% {
    height: 25%;
  }
  60% {
    height: 40%;
  }
  100% {
    height: 50%;
  }
}
@keyframes left {
  10% {
    height: 10%;
  }
  30% {
    height: 30%;
  }
  60% {
    height: 50%;
  }
  100% {
    height: 66%;
  }
}
</style>
