<script setup lang="ts">
import Payattention from '@/pages/modules/home/components/payattention/payattention.vue'
import useCollectionDispatcher from '@/store/dispatcher/useCollectionDispatcher'
import { onMounted, onUnmounted, ref } from 'vue'

const $collectionDispatcherStore = useCollectionDispatcher()
const payattentionRef = ref()
const init = () => {
  payattentionRef.value?.initialSwiperData()
}
onMounted(() => {
  init()
  $collectionDispatcherStore.addShopSubscriber(init)
})
onUnmounted(() => $collectionDispatcherStore.removeShopSubscriber(init))
</script>

<template>
  <Payattention ref="payattentionRef"></Payattention>
</template>

<style scoped lang="scss">
@include b(container) {
  display: flex;
  justify-content: space-between;
  width: 100%;
  border-bottom: 1px solid #bbbb;
  padding: 10rpx;
  @include e(image) {
    width: 200rpx;
    height: 200rpx;
    border-radius: 15rpx;
  }
  @include e(info) {
    flex: 1;
    height: 200rpx;
    padding: 20rpx 40rpx;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    @include m(name) {
      font-size: 32rpx;
      font-weight: 700;
      color: #101010;
      display: flex;
      justify-content: space-between;
    }
    @include m(name-text) {
      width: 250rpx;
      font-size: 28rpx;
      text-align: LEFT;
      color: #2f2f2f;
      overflow: hidden;
      white-space: nowrap; //内容超出不换行
      text-overflow: ellipsis;
    }
  }
  @include e(name-new) {
    margin-right: 34rpx;
    @include flex;
    @include m(round) {
      width: 28rpx;
      height: 28rpx;
      background: #ffffff;
      border: 2rpx solid #11a741;
      border-radius: 50%;
      margin-right: 10rpx;
      position: relative;
      &::before {
        content: ' ';
        display: block;
        width: 20rpx;
        height: 20rpx;
        background: #11a741;
        border-radius: 50%;
        position: absolute;
        left: 50%;
        top: 50%;
        transform: translate(-50%, -50%);
      }
    }
  }
  @include e(info-footer) {
    width: 100%;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  @include e(info-footer-btn) {
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-radius: 15rpx;
    border: 1px solid #bbbb;
    padding: 5rpx 15rpx;
  }
}
</style>
