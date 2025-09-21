<script setup lang="ts">
const $props = defineProps({
  text: {
    type: String,
    default: '返回首页',
  },
  bgColor: {
    type: String,
    default: '#F54319',
  },
  color: { type: String, default: '' },
  isBold: { type: Boolean, default: false },
  disable: {
    type: Boolean,
    default: false,
  },
  height: {
    type: Number,
    default: 84,
  },
})
const $emit = defineEmits(['click'])

const handleClick = () => {
  if (!$props.disable) {
    $emit('click')
  }
}
</script>

<template>
  <view
    class="footer"
    :class="[!disable ? 'footerActive' : 'notAllowed']"
    :style="{ background: $props.bgColor, height: height + 'rpx', lineHeight: height + 'rpx' }"
    @click="handleClick"
  >
    <text :style="{ color: $props.color, fontWeight: isBold ? '700' : '400' }">{{ $props.text }}</text>
  </view>
</template>

<style scoped lang="scss">
@include b(footer) {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  font-size: 28rpx;
  text-align: CENTER;
  color: #ffffff;
}

@include b(notAllowed) {
  &:hover {
    cursor: not-allowed;
  }
}
@include b(footerActive) {
  &:active {
    opacity: 0.5;
  }
}
</style>
