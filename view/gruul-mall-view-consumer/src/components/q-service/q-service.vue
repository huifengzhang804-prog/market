<template>
  <view class="customer-service" @click="handleNavToConsumerSever">
    <q-icon name="icon-xiaoxi" :size="$props.size + 'rpx'" color="#999" />
    <view class="customer-service__badge">
      <u-badge type="error" :count="consumerSeverCount" is-center />
    </view>
  </view>
</template>

<script setup lang="ts">
import QIcon from '@/components/q-icon/q-icon.vue'

import { ref } from 'vue'
import { useUserStore } from '@/store/modules/user'
import { useMsgCountStore } from '@/store/modules/message'
import { SHOW_LOGIN_MODAL } from '@/utils/tokenConfig'
const msgCountStore = useMsgCountStore()
// TODO:消息未读
const consumerSeverCount = ref('0')
const $props = defineProps({
  size: {
    type: Number,
    default: 60,
  },
})

// 获取客服条数
function initGetConsumerSever() {
  consumerSeverCount.value = msgCountStore.getCount
  msgCountStore.$subscribe((mutation, state) => {
    consumerSeverCount.value = state.count
  })
}

initGetConsumerSever()

// 用户信息
const $userStore = useUserStore()
/**
 * 跳转去客服页面
 */
const handleNavToConsumerSever = () => {
  if ($userStore.userInfo.token) {
    uni.navigateTo({ url: '/basePackage/pages/message/Message' })
  } else {
    uni.$emit(SHOW_LOGIN_MODAL, true)
  }
}
</script>

<style scoped lang="scss">
@include b(customer-service) {
  height: 100%;
  width: 100%;
  position: relative;
  @include e(count) {
    position: absolute;
    right: -20rpx;
    top: -27rpx;
    padding: 0 11rpx;
    text-align: center;
    line-height: 35rpx;
    font-size: 24rpx;
    color: #fff;
    background-color: red;
    border-radius: 25rpx;
  }
  @include e(badge) {
    position: absolute;
    top: 10rpx;
    right: 5rpx;
  }
}
</style>
