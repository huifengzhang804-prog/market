<script setup lang="ts">
interface QTimeLineItemProps {
  data: QTimeLineItemData
  curColor?: string
}

interface QTimeLineItemData {
  title: string
  content: string
  time: string
  // 是否是当前状态
  isCurrent?: boolean
  // 是否是最后一条
  isLast?: boolean
}
withDefaults(defineProps<QTimeLineItemProps>(), {
  data: () => {
    return {
      title: '',
      content: '',
      time: '',
      isCurrent: false,
      isLast: false,
    }
  },
  curColor: 'rgb(85, 92, 253)',
})
</script>

<template>
  <view class="q_time_line_item">
    <view class="q_time_line_item_line_container">
      <slot name="icon">
        <view
          class="q_time_line_item_line_icon"
          :class="{
            q_time_line_item_line_current: data.isCurrent,
          }"
        ></view>
      </slot>
      <view v-if="!data.isLast" class="q_time_line_item_line"></view>
    </view>
    <view class="q_time_line_item_right_container">
      <view
        class="q_time_line_item_title"
        :class="{
          q_time_line_item_title_current: data.isCurrent,
        }"
      >
        <slot name="title">
          {{ data.title }}
        </slot>
      </view>
      <view class="q_time_line_item_content_container">
        <view class="q_time_line_item_content">{{ data.content }}</view>
        <view class="q_time_line_item_time">{{ data.time }}</view>
      </view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.q_time_line_item {
  display: flex;
  width: 100%;
  min-width: 628rpx;
  .q_time_line_item_line_container {
    display: flex;
    flex-direction: column;
    align-items: center;
    flex-shrink: 0;
    margin-right: 16rpx;
    .q_time_line_item_line_icon {
      width: 32rpx;
      height: 32rpx;
      border-radius: 50%;
      border: 1px solid v-bind(curColor);
      position: relative;
      &.q_time_line_item_line_current {
        border: 1px solid v-bind(curColor);
        // 放大动画
        animation: enlargeAnimation 2s ease-in-out infinite;
        @keyframes enlargeAnimation {
          0% {
            transform: scale(0.8);
          }
          50% {
            transform: scale(1.2);
          }
          100% {
            transform: scale(0.8);
          }
        }
        &::before {
          background: v-bind(curColor);
        }
      }
      &::before {
        position: absolute;
        content: '';
        width: 16rpx;
        height: 16rpx;
        left: 50%;
        top: 50%;
        transform: translate(-50%, -50%);
        border-radius: 50%;
        background: v-bind(curColor);
      }
    }
    .q_time_line_item_line {
      flex: 1;
      border: 1px solid rgb(235, 235, 235);
    }
  }
  .q_time_line_item_right_container {
    display: flex;
    flex-direction: column;
    flex: 1;
    margin-bottom: 40rpx;
    transform: translateY(-4rpx);
    .q_time_line_item_title {
      color: #666666;
      font-size: 28rpx;
      font-weight: bold;
      line-height: 40rpx;
      margin-bottom: 8rpx;
      &.q_time_line_item_title_current {
        color: #333333;
      }
    }
    .q_time_line_item_content_container {
      display: flex;
      align-items: center;
      justify-content: space-between;
      color: rgb(153, 153, 153);
      font-size: 24rpx;
      line-height: 40rpx;
    }
  }
}
</style>
