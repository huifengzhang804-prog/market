<script setup lang="ts">
import ToTopGoCar from '@/views/toTopGoCar/index.vue'
import { PropertiesList, usePropertiesListStore } from '@/store/modules/propertiesList'
import { storeToRefs } from 'pinia'

const { getterPropertiesList } = storeToRefs(usePropertiesListStore())

document.getElementById('toTop')?.scrollIntoView()

const propertiesList = ref<PropertiesList>()
propertiesList.value = getterPropertiesList.value

console.log('propertiesList', propertiesList.value)

const componentMap = {
  swiper: defineAsyncComponent(() => import('./components/carousel.vue')),
  seckill: defineAsyncComponent(() => import('./components/Seckill.vue')),
  recommend: defineAsyncComponent(() => import('./components/recommends.vue')),
  goods: defineAsyncComponent(() => import('./components/Recommend.vue')),
  shop: defineAsyncComponent(() => import('./components/Channel.vue')),
} as any
</script>
<template>
  <div v-if="propertiesList">
    <template v-for="(item, index) in propertiesList?.components" :key="index">
      <component :is="componentMap[item?.value]" :properties-lists="item" />
    </template>
    <!-- 悬浮导航 -->
    <ToTopGoCar />
  </div>
  <div v-else c-h-600 flex justify-center items-center>
    <div>
      <img src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul111/2024/8/1066b6c9f8aced3ee24c6839e8.png" c-w-200 c-pb-20 />
      <p c-fs-12 c-mt-20 c-c-000>请前往平台端装修！！！</p>
    </div>
  </div>
</template>

<style scoped lang="scss"></style>
