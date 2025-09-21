<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import MessageItem from './components/message-item.vue'
import { doGetPigeonMessageShop } from '@/apis/consumerSever'
import { onLoad, onShow } from '@dcloudio/uni-app'
import Auth from '@/components/auth/auth.vue'
import { useCustomerServiceStore } from '@/store/modules/message'
import type { ApiMessageShop } from '@/basePackage/pages/customerService/types'
import { ConnectType } from '@/hooks/stomp/typs'
import { useUserStore } from '@/store/modules/user'
import { playSound } from '@/libs/MediaPlayer'
const $useUserStore = useUserStore().getterUserInfo

const pageConfig = reactive({
  pages: 1,
  current: 1,
  size: 999,
})
const messageList = ref<ApiMessageShop[]>([])

onLoad(() => {
  useCustomerServiceStore().$subscribe((mutation, state) => {
    const connectType = state.value.connectType
    switch (connectType) {
      case ConnectType.SUCCESS:
        initAssessList()
        return
      case ConnectType.FAIL:
        return
      default:
        break
    }
    playSound().then(() => {})
    initAssessList()
  })
})
onShow(() => {
  initAssessList()
})

/**
 * 消息列表
 */
async function initAssessList() {
  // 刷新
  pageConfig.current = 1
  messageList.value = await getCommodityList()
}
async function getCommodityList() {
  const { code, data, msg } = await doGetPigeonMessageShop({ userId: $useUserStore.info.userId, chatWithType: 'CONSUMER', ...pageConfig })
  if (code !== 200) {
    uni.showToast({ title: `${msg ? msg : '获取消息列表失败'}`, icon: 'none' })
    return []
  }
  if (data.current >= data.pages) {
    // pageConfig.status = 'nomore'
  }
  pageConfig.pages = data.pages
  return data.records
}
const height = computed(() => {
  return useScreenHeight().value - useBottomSafe().value
})
</script>

<template>
  <MessageItem v-for="item in messageList" :key="item.lastMessage?.sendTime" :message="item" />
  <view v-if="!messageList.length" class="message-empty">
    <u-empty mode="message" />
  </view>
  <Auth />
</template>

<style scoped lang="scss">
@include b(message-empty) {
  height: 50vh;
}
</style>
