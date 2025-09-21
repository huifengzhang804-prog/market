<script setup lang="ts">
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'

onLoad(() => {
  uni.$emit('updateTitle')
})
const url = ref('')

onLoad((params = { url: '' }) => {
  if (params.url) {
    url.value = params.url
  }
})
</script>

<template>
  <web-view :src="url"></web-view>
</template>
