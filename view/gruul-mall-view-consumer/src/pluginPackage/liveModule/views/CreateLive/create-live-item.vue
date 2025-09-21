<script setup lang="ts">
import type { PropType } from 'vue'
import { type GetLiveRoomAnchorList, LIVE_LIST_STATUS, type GetUserMessage } from '@/pluginPackage/liveModule/apis/CreateLive/model'
import StatusTag from './status-tag.vue'
import ItemBtn from './item-btn.vue'
import { toLive } from '@/pluginPackage/liveModule/views/components/liveList'

defineProps({
  roomAnchorItem: {
    type: Object as PropType<GetLiveRoomAnchorList>,
    default: () => ({}),
  },
  status: { type: String as PropType<GetUserMessage['status']>, default: 'NORMAL' },
})
const emit = defineEmits(['init-list'])
</script>
<template>
  <view class="live_item">
    <view class="live_item__left">
      <u-image width="100%" height="100%" :src="roomAnchorItem.pic" shape="square" mode="aspectFit" border-radius="20rpx 0px 0px 20rpx" />
    </view>
    <view class="live_item__right">
      <view class="live_item__right_top">
        <status-tag :model="roomAnchorItem.status" />
        <text class="u-line-1" style="margin-left: 10rpx; font-weight: 700">{{ roomAnchorItem.liveTitle }}</text>
      </view>
      <view class="u-line-1">{{ roomAnchorItem.liveSynopsis }}</view>
      <view class="live_item__right--time">
        <text v-if="[LIVE_LIST_STATUS.NOT_STARTED, LIVE_LIST_STATUS.PROCESSING].includes(roomAnchorItem.status)">
          直播时间:{{ roomAnchorItem.beginTime }}
        </text>
        <template v-else>
          <view>开始时间:{{ roomAnchorItem.beginTime }}</view>
          <view>结束时间:{{ roomAnchorItem.endTime }}</view>
        </template>
      </view>
      <item-btn
        :id="roomAnchorItem.id"
        style="width: 100%"
        :model="roomAnchorItem.status"
        :status="status"
        @init-list="emit('init-list')"
        @to-live="toLive({ id: roomAnchorItem.id, pushAddress: roomAnchorItem.pushAddress })"
      />
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
      font-size: 24rpx;
      color: #333333;
    }
  }
  @include e(right_top) {
    @include flex;
    justify-content: flex-start;
  }
}
</style>
