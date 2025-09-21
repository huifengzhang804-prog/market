<script setup lang="ts">
defineProps({
  msg: {
    type: String,
    default: '您还没有关注主播，可能错过更多精彩~~~',
  },
  btnText: {
    type: String,
    default: '去看直播',
  },
  src: {
    type: String,
    default: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20230616/84a424f7c0ce4187a018d9b35af88b4e.png',
  },
  usedHeight: {
    type: Number,
    // 单位 rpx
    default: 0,
  },
})

const $emit = defineEmits(['click'])
</script>

<template>
  <view class="list-empty" :style="{ height: `calc(100vh - ${usedHeight}rpx - 40rpx)` }">
    <image :src="src" style="width: 294rpx; height: 227rpx"></image>
    <view style="color: rgba(0, 0, 0, 0.6); margin: 65rpx 0">{{ msg }}</view>
    <view class="list-empty__btn">
      <text @click="$emit('click')">{{ btnText }}</text>
    </view>
  </view>
</template>

<style scoped lang="scss">
@include b(list-empty) {
  background: #fff;
  border-radius: 20rpx;
  @include flex;
  flex-direction: column;
  margin: 10rpx;
  @include e(btn) {
    display: flex;
    flex-direction: row;
    justify-content: center;
    align-items: center;
    padding: 24rpx 40rpx;
    width: 208rpx;
    height: 60rpx;
    /* 主色 */
    background: #fa3534;
    border-radius: 12rpx;
    color: #fff;
    &:active {
      opacity: 0.8;
    }
  }
}
</style>
