<script setup lang="ts">
import { type Component, ref, computed, defineAsyncComponent } from 'vue'
import ChromeTab from '@/components/chrome-tab/index.vue'

enum TabName {
  distributionCom = 'distributionCom',
  distributionOrder = 'distributionOrder',
  wholeSaler = 'wholeSaler',
}
type AsyncComponent = {
  [K in TabName]: Component
}
const asyncComponents: AsyncComponent = {
  distributionCom: defineAsyncComponent(() => import('../components/DistributionCom.vue')),
  distributionOrder: defineAsyncComponent(() => import('../components/DistributionOrder.vue')),
  wholeSaler: defineAsyncComponent(() => import('../components/WholeSaler.vue')),
}

const list = [
  {
    label: '分销商品',
    name: 'distributionCom',
  },
  {
    label: '分销订单',
    name: 'distributionOrder',
  },
  {
    label: '分销商',
    name: 'wholeSaler',
  },
]

const activeName = ref<keyof typeof TabName>('distributionCom')
const currentComponent = computed(() => {
  return asyncComponents[activeName.value]
})
const changeTab = (value: any) => {
  if (value) activeName.value = value
}
</script>

<template>
  <div class="q_plugin_container">
    <ChromeTab :tabList="list" :value="activeName" @handle-tabs="changeTab" />
    <!-- <keep-alive> -->
    <component :is="currentComponent" ref="componentRef"></component>
    <!-- </keep-alive> -->
  </div>
</template>

<style lang="scss" scoped></style>
