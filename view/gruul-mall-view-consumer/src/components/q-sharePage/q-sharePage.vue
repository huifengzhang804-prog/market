<template>
  <view class="generate" @click="generateCode">{{ text }}</view>
</template>

<script setup lang="ts">
const $props = defineProps({
  text: {
    type: String,
    default: '口令码',
  },
  url: {
    type: String,
    default: '',
  },
  customStyle: {
    type: Object,
    default: () => ({
      width: '350rpx',
      backgroundColor: 'rgba(222, 50, 36, 1)',
      color: '#fff',
      lineHeight: '76rpx',
      textAlign: 'center',
    }),
  },
})
const emit = defineEmits(['error', 'success'])
const generateCode = () => {
  if ($props.url.length) {
    uni.setClipboardData({
      data: $props.url,
      showToast: false,
      success: function () {
        uni.showToast({ title: '已复制快去粘贴吧~', icon: 'none' })
        emit('success')
      },
    })
  } else {
    emit('error')
  }
}
</script>
<style lang="scss">
@include b(generate) {
  width: 350rpx;
  height: 76rpx;
  border-radius: 6rpx;
  background-color: rgba(222, 50, 36, 1);
  color: #fff;
  line-height: 76rpx;
  text-align: center;
}
</style>
