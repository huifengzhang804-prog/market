<script setup lang="ts">
import QNav from '@/components/q-nav/q-nav.vue'
import SearchConcernShop from '@/basePackage/pages/concern/components/search-concern-shop.vue'
import { onLoad } from '@dcloudio/uni-app'

onLoad(() => {
  uni.$emit('updateTitle')
})
</script>

<template>
  <q-nav title="搜索" />
  <search-concern-shop />
</template>

<style scoped lang="scss">
page {
  background: #fff;
}
</style>
