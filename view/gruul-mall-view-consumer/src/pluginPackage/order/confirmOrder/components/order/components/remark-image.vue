<script setup lang="ts">
import { ref } from 'vue'
import { UPLOAD_URL } from '@/hooks'
import { getheader } from '@/libs/request/returnHeader'
import { useUserStore } from '@/store/modules/user'

const $props = defineProps({
  placeholder: { type: String, default: '' },
  modelValue: { type: String, default: '' },
})
const $emit = defineEmits(['update:modelValue'])

const handleUpload = () => {
  uni.chooseImage({
    sourceType: ['album'],
    success: (res) => {
      console.log('res', res.tempFilePaths)
      uni.uploadFile({
        url: UPLOAD_URL,
        filePath: res.tempFilePaths[0],
        name: 'file',
        header: getheader(),
        success: (uploadFileRes) => {
          const { data } = JSON.parse(uploadFileRes.data)
          $emit('update:modelValue', data)
          // sendMessagesImage({ messageType: 'IMAGE', message: data })
        },
      })
    },
  })
}
</script>

<template>
  <view class="image-choose">
    <view class="image-choose__upload" @click="handleUpload">
      <text v-if="!$props.modelValue" style="color: #c0c4cc">{{ placeholder }}</text>
      <u-image v-show="$props.modelValue" :width="100" height="100%" :src="$props.modelValue"></u-image>
    </view>
    <u-icon v-show="$props.modelValue" class="image-choose__icon" name="trash" color="#2979ff" size="28" @click="$emit('update:modelValue', '')" />
  </view>
</template>

<style scoped lang="scss">
@include b(image-choose) {
  width: 100%;
  display: flex;
  justify-content: space-between;
  @include e(upload) {
    flex: 1;
    display: flex;
    justify-content: space-between;
    padding-right: 30rpx;
  }
}
</style>
