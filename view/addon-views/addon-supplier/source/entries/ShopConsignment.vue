<script lang="ts" setup>
import ChromeTabs from '@/components/ChromeTab.vue'
import { type Component, defineAsyncComponent, ref } from 'vue'

const currentTabName = ref('goodSource')
const asyncComponents: {
  [key: string]: Component
} = {
  goodSource: defineAsyncComponent(() => import('../components/consignment-page/goods-source/index.vue')),
  shipped: defineAsyncComponent(() => import('../components/consignment-page/shipped/index.vue')),
  settings: defineAsyncComponent(() => import('../components/consignment-page/consignment-settings/index.vue')),
}
const tabsList = [
  { name: 'goodSource', label: '货源' },
  { name: 'shipped', label: '已铺货' },
  { name: 'settings', label: '代销设置' },
]
const changeTab = (value: any) => {
  if (value) currentTabName.value = value
}
</script>

<template>
  <div class="q_plugin_container">
    <ChromeTabs :tabList="tabsList" :value="currentTabName" @handle-tabs="changeTab" />
    <component :is="asyncComponents[currentTabName]" />
  </div>
</template>
