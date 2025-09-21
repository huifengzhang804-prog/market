<script setup lang="ts">
import { computed } from 'vue'

const $props = defineProps({
  loading: { type: Boolean, default: false },
  text: { type: String, default: 'default' },
  boxHeight: { type: Number, default: 134 },
})
const $emit = defineEmits(['click'])
const boxHeight = uni.upx2px($props.boxHeight)
const safeHeight = useBottomSafe()
// TODO: 兼容底部button高度
const buttonBoxHeight = computed(() => {
  console.log('safeHeight.value', safeHeight.value)
  return boxHeight + uni.upx2px(safeHeight.value) + 'px'
})
const customStyle = {
  width: '85%',
  height: '78rpx',
  fontSize: '28rpx',
  textAlign: 'CENTER',
  color: '#ffffff',
  borderRadius: '50rpx',
  lineHeight: '78rpx',
}
</script>

<template>
  <view class="balance" :style="{ height: buttonBoxHeight, lineHeight: buttonBoxHeight }">
    <slot>
      <u-button type="error" :loading="$props.loading" :hair-line="false" :custom-style="customStyle" class="balance--text" @click="$emit('click')">
        {{ $props.text }}
      </u-button>
    </slot>
  </view>
</template>

<style scoped lang="scss">
@include b(balance) {
  width: 100%;
  background: #ffffff;
  border-radius: 20rpx 20rpx 0 0;
  position: fixed;
  display: flex;
  justify-content: center;
  bottom: 0;
  z-index: 999;
  @include m(text) {
    width: 85%;
  }
}
</style>
