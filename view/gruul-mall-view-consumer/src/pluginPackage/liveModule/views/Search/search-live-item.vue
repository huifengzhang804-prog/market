<script setup lang="ts">
import { ref, type PropType } from 'vue'
import { toLive } from '@/pluginPackage/liveModule/views/components/liveList'
import type { FollowLiveRoomList } from '@/pluginPackage/liveModule/apis/UserLive/model'

defineProps({
  item: {
    type: Object as PropType<FollowLiveRoomList>,
    default: () => ({}),
  },
})
const emit = defineEmits(['to-live'])
</script>
<template>
  <view class="live_item" @click="emit('to-live')">
    <view class="live_item__left">
      <u-image width="100%" height="100%" :src="item.pic" shape="square" mode="aspectFit" border-radius="20rpx 0px 0px 20rpx" />
    </view>
    <view class="live_item__right">
      <view class="live_item__right_top">
        <text class="u-line-1" style="margin-left: 10rpx; font-weight: 700">{{ item.shopName }}</text>
      </view>
      <view class="u-line-1" style="font-size: 24rpx">{{ item.liveTitle }}</view>
      <view class="live_item__right--time">
        <text>{{ item.viewership }} 人观看</text>
        <view class="live_item__right--btn">
          <text>进入直播间</text>
        </view>
      </view>
    </view>
  </view>
</template>

<style scoped lang="scss">
@include b(live_item) {
  height: 254rpx;
  margin: 20rpx;
  background: #ffffff;
  box-shadow: 0px 0px 15rpx -3rpx rgba(0, 0, 0, 0.25);
  border-radius: 20rpx;
  @include flex;
  justify-content: flex-start;
  @include e(left) {
    width: 200rpx;
    height: 100%;
    border-radius: 20rpx 0px 0px 20rpx;
  }
  @include e(right) {
    height: 100%;
    flex: 1;
    padding: 20rpx;
    @include flex;
    flex-direction: column;
    justify-content: space-between;
    align-items: flex-start;
    @include m(time) {
      width: 100%;
      font-size: 24rpx;
      color: #333333;
      @include flex;
      justify-content: space-between;
    }
    @include m(btn) {
      height: 52rpx;
      padding: 10rpx 20rpx;
      color: #fff;
      /* 主色 */
      background: #fa3534;
      border-radius: 56rpx;
      @include flex;
      &:active {
        opacity: 0.5;
      }
    }
  }
  @include e(right_top) {
    @include flex;
    justify-content: flex-start;
  }
}
</style>
