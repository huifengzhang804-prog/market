<script setup lang="ts">
import { ref, toRefs } from 'vue'
import ReleaseLive from './release-live.vue'
import CreateLiveList from './create-live-list.vue'
import { doGetUserMessage } from '@/pluginPackage/liveModule/apis/CreateLive'
import type { GetUserMessage } from '@/pluginPackage/liveModule/apis/CreateLive/model'

// 组件默认高度 rpx
const release_live_height = 330
const userInfo = ref<GetUserMessage>({
  id: '',
  shopId: '',
  anchorNickname: '',
  anchorSynopsis: '',
  anchorIcon: '',
  status: 'NORMAL',
  gender: '',
  phone: '',
  followCount: '',
  viewership: '',
  duration: '',
})

initUserMessage()

async function initUserMessage() {
  const { code, data, msg } = await doGetUserMessage()
  if (code !== 200) {
    uni.showToast({ title: `${msg || '获取主播信息失败'}`, icon: 'none' })
    return
  }
  if (data) {
    userInfo.value = data
  }
}
</script>

<template>
  <release-live
    :anchor-icon="userInfo.anchorIcon"
    :follow-count="userInfo.followCount"
    :viewership="userInfo.viewership"
    :anchor-nickname="userInfo.anchorNickname"
    :status="userInfo.status"
  />
  <create-live-list :used-height="release_live_height" :status="userInfo.status" />
</template>

<style scoped lang="scss"></style>
