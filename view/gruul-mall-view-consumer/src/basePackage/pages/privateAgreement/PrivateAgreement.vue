<script setup lang="ts">
import { ref } from 'vue'
import { doGetPrivateAgreement } from '@/apis/consumer/footprint'

const textnode = ref('')

initPrivateAgreement()

async function initPrivateAgreement() {
  const { data } = await doGetPrivateAgreement()
  if (data && data.platformPrivacyAgreementText) {
    textnode.value = data.platformPrivacyAgreementText
  } else {
    uni.showToast({
      icon: 'none',
      title: '获取协议失败',
    })
  }
}
</script>

<template>
  <view style="width: 100%; overflow: hidden">
    <rich-text :nodes="textnode"></rich-text>
  </view>
</template>

<style lang="scss" scoped></style>
