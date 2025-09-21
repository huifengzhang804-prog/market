<template>
</template>
<script setup lang="ts">
import {onMounted} from 'vue'
const emit = defineEmits<{ (e: 'loaded'): void }>()
onMounted(() => {
  emit('loaded')
})
</script>