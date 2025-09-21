<script setup lang="ts">
import type { PropType } from 'vue'
import { LIVE_LIST_STATUS, type Joint_Live_List_Status, type GetUserMessage } from '@/pluginPackage/liveModule/apis/CreateLive/model'
import { doDelLiveRoom, doPutLiveRoomLower } from '@/pluginPackage/liveModule/apis/CreateLive'

const $props = defineProps({
  model: {
    type: String as PropType<Joint_Live_List_Status>,
    default: 'NOT_STARTED',
  },
  id: { type: String, default: '' },
  status: { type: String as PropType<GetUserMessage['status']>, default: 'NORMAL' },
})
const emit = defineEmits(['init-list', 'to-live'])

const handleDel = () => {
  uni.showModal({
    title: '提示',
    content: '是否删除该直播',
    success: async (res) => {
      if (res.confirm) {
        const { code, success } = await doDelLiveRoom($props.id)
        if (code === 200 && success) {
          uni.showToast({
            title: '删除成功',
            icon: 'none',
          })
          emit('init-list')
        }
      }
    },
  })
}
const handleContinueLive = () => {
  console.log('继续直播')
}
const handleToLive = (status: GetUserMessage['status']) => {
  if (status === 'NORMAL') return emit('to-live')
  uni.showToast({ title: '主播已被禁播', icon: 'none' })
}
const handleLiveRoomLower = async () => {
  uni.showModal({
    title: '提示',
    content: '是否下播',
    success: async (res) => {
      if (res.confirm) {
        const { code, data, msg } = await doPutLiveRoomLower($props.id)
        if (code !== 200) {
          uni.showToast({ title: `${msg || '下播失败'}`, icon: 'none' })
          return
        }
        emit('init-list')
      }
    },
  })
}
</script>

<template>
  <view v-if="model === LIVE_LIST_STATUS.PROCESSING" class="content">
    <!-- <view class="content__fill" @click="handleContinueLive">继续直播</view> -->
    <view class="content__unfill ml15" @click="handleLiveRoomLower">下播</view>
  </view>
  <view v-if="model === LIVE_LIST_STATUS.NOT_STARTED" class="content">
    <view class="content__fill" @click="handleToLive(status)">去直播</view>
    <view class="content__unfill ml15" @click="handleDel">删除</view>
  </view>
  <view v-if="model === LIVE_LIST_STATUS.OVER" class="content">
    <view class="content__unfill ml15" @click="handleDel">删除</view>
  </view>
  <view v-if="model === LIVE_LIST_STATUS.ILLEGAL_SELL_OFF" class="content">
    <view class="content__unfill ml15" @click="handleDel">删除</view>
  </view>
</template>

<style scoped lang="scss">
@include b(content) {
  @include flex;
  justify-content: flex-end;
  width: 100%;
  @include e(fill) {
    width: 148rpx;
    height: 52rpx;
    /* 主色 */
    background: $qszr-red;
    border-radius: 56rpx;
    text-align: center;
    line-height: 52rpx;
    color: #fff;
    &:active {
      opacity: 0.5;
    }
  }
  @include e(unfill) {
    width: 100rpx;
    height: 50rpx;
    border-radius: 56rpx;
    text-align: center;
    line-height: 50rpx;
    color: $qszr-red;
    border: 1px solid $qszr-red;
    &:active {
      opacity: 0.5;
    }
  }
}
.ml15 {
  margin-left: 15rpx;
}
</style>
