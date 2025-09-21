<template>
  <scroll-view scroll-y :style="{ height: `${height}px` }">
    <SignInHead @change-list="changeList" />
    <SignInBody :key="bodyKey" />
  </scroll-view>
  <Auth />
</template>

<script setup lang="ts">
import SignInHead from './components/sign-in-head.vue'
import SignInBody from './components/sign-in-body.vue'
import { computed, ref } from 'vue'
import Auth from '@/components/auth/auth.vue'
const bodyKey = ref(0)
const changeList = () => {
  bodyKey.value = Date.now()
}
const height = computed(() => {
  let contentHeight = useScreenHeight().value - useBottomSafe().value
  return contentHeight
})
</script>

<style lang="scss" scoped></style>
