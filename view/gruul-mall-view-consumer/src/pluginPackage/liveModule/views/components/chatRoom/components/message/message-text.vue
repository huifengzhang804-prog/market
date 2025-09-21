<template>
  <view>
    <view class="text-line">
      <rich-text :nodes="parseHtmlComputed"></rich-text>
    </view>
  </view>
</template>

<script lang="ts" setup>
import { computed } from 'vue'
import parseHtml from '@/pluginPackage/liveModule/views/components/chatRoom/hooks/html-parser'
// @ts-ignore

const $props = defineProps({
  data: {
    type: Object,
    default: () => {
      return {}
    },
  },
  messageData: {
    type: Object,
    default: () => {
      return {}
    },
  },
})
const parseHtmlComputed = computed(() => {
  let name = JSON.parse($props.messageData.cloudCustomData || '{"name":"游客"}').name || '游客'
  // @ts-ignore

  if (name === getApp().globalData.userInfo.nickname) {
    name += '(我)'
  }
  let html = `<span style="color: #4dd4ff" > ${name || '游客'}：</span>`
  if ($props.data.text) {
    for (let i = 0; i < $props.data.text.length; i++) {
      const item = $props.data.text[i]
      if (item.name === 'text') {
        html += `<span style="color: #fff">${item.text}</span>`
      }
      if (item.name === 'img') {
        html += `<img style="width: 40rpx; height: 40rpx;margin-top: 20rpx" src="${item.src}" alt="" />`
      }
    }
    return parseHtml(html)
  }
  return ''
})
</script>
<style scoped>
.text-line {
  padding: 19rpx 30rpx;
  border-radius: 20rpx;
  margin-bottom: 20rpx;
  display: flex;
  justify-content: center;
  background: rgba(0, 0, 0, 0.2);
}
</style>
