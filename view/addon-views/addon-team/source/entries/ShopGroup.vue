<script setup lang="ts">
import { ref, defineAsyncComponent } from 'vue'
import 'uno.css'
import ChromeTabs from '@/components/ChromeTab.vue'

type AsyncComponents = 'GroupActivity' | 'GroupOrder'

const asyncComponents = {
  GroupActivity: defineAsyncComponent(() => import('../components/group-activity.vue')),
  GroupOrder: defineAsyncComponent(() => import('../components/group-order.vue')),
} as const

const currentComponent = ref<AsyncComponents>('GroupActivity')
const tabsList = [
  { name: 'GroupActivity', label: '拼团活动' },
  { name: 'GroupOrder', label: '拼团订单' },
]
const changeTab = (value: any) => {
  if (value) currentComponent.value = value
}
</script>

<template>
  <div class="q_plugin_container">
    <ChromeTabs :tab-list="tabsList" :value="currentComponent" @handle-tabs="changeTab" />
    <div class="fdc1 overh">
      <component :is="asyncComponents[currentComponent]"></component>
    </div>
  </div>
</template>

<style lang="scss" scoped></style>
