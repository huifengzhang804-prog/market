<script setup lang="ts">
import QIcon from '@/components/q-icon/q-icon.vue'
</script>

<template>
  <view class="empty">
    <view class="empty__content">
      <view class="empty__content--icon">
        <q-icon name="icon-dianpu1" size="80px" color="#fff" />
      </view>
    </view>
    <view class="empty__title">商品不可用</view>
  </view>
</template>

<style scoped lang="scss">
@include b(empty) {
  text-align: center;
  padding: 40rpx 0;
  background: rgba(237, 237, 237);
  @include e(content) {
    text-align: center;
    display: flex;
    justify-content: center;
    align-items: center;
    @include m(icon) {
      width: 200rpx;
      height: 200rpx;
      line-height: 200rpx;
      border-radius: 50%;
      background: rgba(217, 217, 217);
    }
  }
  @include e(title) {
    font-size: 24rpx;
    color: #b7b7b7;
    margin-top: 40rpx;
  }
}
</style>
