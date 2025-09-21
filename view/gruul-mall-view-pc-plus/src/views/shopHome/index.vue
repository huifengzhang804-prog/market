<script setup lang="ts">
defineProps<{
  components: any[]
}>()

const componentMap = {
  swiper: defineAsyncComponent(() => import('@/views/home/components/carousel.vue')),
  recommend: defineAsyncComponent(() => import('@/views/home/components/recommends.vue')),
  goods: defineAsyncComponent(() => import('@/views/home/components/Recommend.vue')),
} as any
</script>

<template>
  <template v-for="(item, index) in components" :key="index">
    <component :is="componentMap[item?.value]" :properties-lists="item" :shop-val="true" />
  </template>
</template>

<style scoped></style>
