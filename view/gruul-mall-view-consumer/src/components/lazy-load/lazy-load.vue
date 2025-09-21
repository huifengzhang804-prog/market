<template>
  <slot v-if="load"></slot>
  <view v-else ref="box" :style="{ height: h, width: w }"></view>
</template>
<script setup lang="ts">
// #ifdef H5
import 'intersection-observer' // intersection-observer polyfill
// #endif
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { initLazyIntersectionObserver } from '../../utils/index'

const props = defineProps({
  w: {
    type: String,
    default: '100%',
  },
  h: {
    type: String,
    default: '100%',
  },
})

const load = ref(false)

const box = ref()

let observer: IntersectionObserver | null

onMounted(() => {
  observer = initLazyIntersectionObserver((entry: IntersectionObserverEntry) => {
    if (entry.isIntersecting) {
      // 当内容可见
      load.value = true
      observer?.unobserve(box.value.$el)
      observer = null
    }
  })
  observer?.observe(box.value.$el) // 观察
})

onBeforeUnmount(() => observer && observer.unobserve(box.value.$el)) // 不观察了
</script>
