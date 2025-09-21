<script lang="ts" setup>
import ChromeTabs from '@/components/ChromeTab.vue'
import { type Component, ref, defineAsyncComponent } from 'vue'

const currentTabName = ref('purchaseOrder')
const tabsList = [
  // { name: 'supply', label: '货源' },
  { name: 'purchaseOrder', label: '采购订单' },
  { name: 'waitingPublish', label: '待发布' },
  { name: 'release', label: '已发布' },
]
const changeTab = (value: any) => {
  if (value) currentTabName.value = value
}
const asyncComponents: {
  [key: string]: Component
} = {
  // supply: defineAsyncComponent(() => import('../components/page-components/supply/index.vue')),
  purchaseOrder: defineAsyncComponent(() => import('../components/page-components/purchaseOrder/index.vue')),
  waitingPublish: defineAsyncComponent(() => import('../components/page-components/waitingPublish/index.vue')),
  release: defineAsyncComponent(() => import('../components/page-components/release/index.vue')),
}
</script>

<template>
  <div class="q_plugin_container">
    <ChromeTabs :tab-list="tabsList" :value="currentTabName" @handle-tabs="changeTab" />
    <component :is="asyncComponents[currentTabName]" />
  </div>
</template>
