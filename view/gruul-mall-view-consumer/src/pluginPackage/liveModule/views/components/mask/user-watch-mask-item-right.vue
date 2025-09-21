<script setup lang="ts">
import { type PropType } from 'vue'
import LiveHeadBar from '../headBar/live-head-bar.nvue'
// #ifdef APP-PLUS
import ChatRoom from '@/pluginPackage/liveModule/views/components/chatRoom/chat-room.vue'
// #endif
import type { LiveRoomDetail } from '../../../apis/CreateLive/model'

defineProps({
  width: {
    type: Number,
    default: 0,
  },
  height: {
    type: Number,
    default: 0,
  },
  isEnd: {
    type: Boolean,
    default: false,
  },
  liveRoomDetail: { type: Object as PropType<LiveRoomDetail>, default: () => ({}) },
})

const pausePusher = () => {
  uni.navigateBack({ delta: 1 })
}
const stopPusher = () => {
  uni.navigateBack({ delta: 1 })
}
const handleShare = () => {}
</script>

<template>
  <view :class="['user-item-right', { 'blur-15': isEnd }]">
    <LiveHeadBar
      mode="user"
      :user-info="{ ...liveRoomDetail.anchor, shopId: liveRoomDetail.shopId, viewership: liveRoomDetail.viewership }"
      :anchor-id="liveRoomDetail.anchorId"
      @pause="pausePusher"
      @stop="stopPusher"
      @share="handleShare"
    ></LiveHeadBar>
    <!-- #ifdef APP-PLUS -->
    <view class="anchor-mask__footer">
      <chat-room :disable-input="isEnd"></chat-room>
    </view>
    <!-- #endif -->
  </view>
  <!-- <share-index :user-avatar="userInfo.anchorIcon" headerImg v-if="isShare" @close="closeshare" /> -->
</template>

<style scoped>
.blur-15 {
  filter: blur(15rpx);
}
.user-item-right {
  position: absolute;
  top: 0;
  bottom: 0;
}
.anchor-mask__footer {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
}
</style>
