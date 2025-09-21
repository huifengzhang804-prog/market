<script setup lang="ts">
import { type PropType, ref, onMounted } from 'vue'
import type { SeparatorFormData } from '../types'

const $props = defineProps({
  decorationProperties: {
    type: Object as PropType<SeparatorFormData>,
    default() {
      return {}
    },
  },
})
// 分隔符边框样式
const inlineBox = ref('')
// 分割符线条样式
const inlineStyle = ref('')

onMounted(() => {
  getinlineBox()
  getinlineStyle()
})

/**
 * 分隔符边框样式
 */
function getinlineBox() {
  inlineBox.value = `box-sizing: 'border-box'; padding: 10px ${$props.decorationProperties.hasMargin ? 15 : 0}px;`
}
/**
 * 分隔符线条样式
 */
function getinlineStyle() {
  inlineStyle.value = `border-bottom: 1px ${$props.decorationProperties.borderStyle} ${$props.decorationProperties.borderColor};`
}
</script>

<template>
  <view :style="inlineBox">
    <view :style="inlineStyle"></view>
  </view>
</template>

<style lang="scss" scoped></style>
