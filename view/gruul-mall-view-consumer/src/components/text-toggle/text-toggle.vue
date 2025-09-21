<!-- $props
max:超出最大字数限制
 -->
<script setup lang="ts">
import { ref } from 'vue'

const $props = defineProps({
  text: {
    type: String,
    default: '',
  },
  max: { type: Number, default: 20 },
})
const showText = ref(true)
const isTextShow = () => $props.text.length > $props.max
</script>

<template>
  <view v-if="$props.text" class="toggle">
    <slot></slot>
    <text :class="{ text: isTextShow() && showText }" style="flex: 1; word-break: break-all">{{ $props.text }}</text>
    <text v-if="isTextShow()" class="text__hide" @click="showText = !showText">{{ showText ? '展开' : '收起' }}</text>
  </view>
</template>

<style scoped lang="scss">
@include b(toggle) {
  display: flex;
  justify-content: space-between;
}
@include b(text) {
  @include utils-ellipsis(1);
  @include e(hide) {
    color: #000;
    font-weight: 700;
    clear: both;
    margin-right: 15rpx;
    flex-shrink: 0;
  }
  @include e(show) {
    color: #000;
    font-weight: 700;
  }
}
</style>
