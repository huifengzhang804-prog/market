<script setup lang="ts">
import { type PropType, computed } from 'vue'
import { transformTextToImg } from '@/utils'
import { useRichText } from '@/hooks/useRichText'

const $props = defineProps({
  decorationProperties: {
    type: Object as PropType<{ text: string }>,
    default() {
      return {}
    },
  },
})
const textnode = computed(() => transformTextToImg($props.decorationProperties.text))
const { handleTextClick, handleLinkClick } = useRichText()
</script>

<template>
  <view style="width: 100%; overflow: hidden">
    <!-- #ifdef APP-PLUS -->
    <rich-text class="rich-txt" :nodes="textnode" @itemclick="handleTextClick"></rich-text>
    <!-- #endif -->

    <!-- #ifndef APP-PLUS -->
    <u-parse class="rich-txt" :html="textnode" :preview="false" @linkpress="handleLinkClick"></u-parse>
    <!-- #endif -->
  </view>
</template>

<style lang="scss" scoped>
.rich-txt {
  word-break: break-all;
  white-space: pre-line;
}
</style>
