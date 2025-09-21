<script setup lang="ts">
import { ref } from 'vue'
import { doGetDistributeConfig } from '@pluginPackage/distribute/apis'

const protocol = ref()

initDisConfig()

async function initDisConfig() {
  const { code, data } = await doGetDistributeConfig()
  if (code === 200) {
    protocol.value = data.protocol
  } else {
    uni.showToast({
      icon: 'none',
      title: '获取分销协议失败',
    })
  }
}
</script>

<template>
  <view style="padding: 40rpx; background: #fff; line-height: 1.5">
    <rich-text :nodes="protocol"></rich-text>
  </view>
</template>

<style lang="scss" scoped></style>
