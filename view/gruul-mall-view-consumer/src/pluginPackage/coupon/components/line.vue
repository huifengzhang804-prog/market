<script setup lang="ts">
const $props = defineProps({
  height: {
    type: [Number, String],
    default: 3,
  },
  backgroundColor: {
    type: String,
    default: '#f1f2f6',
  },
})
</script>

<template>
  <view :style="{ height: $props.height + 'px', backgroundColor: $props.backgroundColor }" />
</template>
