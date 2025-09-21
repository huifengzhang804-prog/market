<script setup lang="ts">
import QIcon from '@/components/q-icon/q-icon.vue'

const $emit = defineEmits(['keywordChange'])

const handleChangeWords = (e: any) => {
  $emit('keywordChange', e.detail.value)
}
</script>

<template>
  <view class="search" @click="handleChangeWords">
    <view class="search__main">
      <q-icon name="icon-sousuo_o" size="40rpx" color="#d5d5d5" />
      <view class="search__main--flag"></view>
      <input :disabled="true" class="search__main--name" placeholder="搜索店铺名称" />
    </view>
  </view>
</template>

<style scoped lang="scss">
@include b(search) {
  height: 124rpx;
  background: #ebebeb;
  @include e(main) {
    height: 64rpx;
    margin: 0 48rpx;
    padding: 0 20rpx;
    background: #f6f7f9;
    border-radius: 32rpx;
    @include flex(flex-start);
    @include m(flag) {
      width: 1rpx;
      height: 30rpx;
      background: #d5d5d5;
      margin: 0 16rpx;
    }
    @include m(name) {
      flex: 1;
      font-size: 24rpx;
    }
  }
}
</style>
