<script setup lang="ts">
let debounceTimer: NodeJS.Timeout
let imitate = true

const $emit = defineEmits(['bounceTap'])

function debounce(fn: () => void, delay: number) {
  if (imitate) {
    fn()
    return
  }
  if (debounceTimer && !imitate) {
    clearTimeout(debounceTimer)
  }
  debounceTimer = setTimeout(() => {
    fn()
  }, delay)
}

function handleClick() {
  debounce(function () {
    $emit('bounceTap')
    imitate = false
  }, 500)
}
</script>

<template>
  <view @click.stop="handleClick">
    <slot></slot>
  </view>
</template>

<style lang="scss" scoped></style>
