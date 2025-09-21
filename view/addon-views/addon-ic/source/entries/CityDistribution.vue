<script lang="ts" setup>
import { type Component, defineAsyncComponent, ref, reactive } from 'vue'
import ChromeTabs from '@/components/ChromeTab.vue'
import SetPinter from '@/q-plugin/components/printer.vue'
import SetPrintTemplate from '@/q-plugin/components/printTemplate.vue'
import SetPrintingTask from '@/q-plugin/components/printingTask.vue'

const enum TabName {
  // 基础设置
  basicSettings = 'basicSettings',
  // 第三方配送
  thirdPartyDelivery = 'thirdPartyDelivery',
  // 打印机
  printer = 'printer',
  // 打印模板
  printTemplate = 'printTemplate',
  // 打印任务
  printingTask = 'printingTask',
}
const currentTab = ref<keyof typeof TabName>(TabName.basicSettings)
const dynamicComponent: Record<keyof typeof TabName, Component> = {
  basicSettings: defineAsyncComponent(() => import('../components/basicSettings.vue')),
  thirdPartyDelivery: defineAsyncComponent(() => import('../components/ThirdPartyDelivery.vue')),
  printer: SetPinter,
  printTemplate: SetPrintTemplate,
  printingTask: SetPrintingTask,
}
const tabsList = [
  { name: 'basicSettings', label: '基础设置' },
  { name: 'thirdPartyDelivery', label: '第三方配送' },
  { name: 'printer', label: '打印机' },
  { name: 'printTemplate', label: '打印模板' },
  { name: 'printingTask', label: '打印任务' },
]
const changeTab = (value: any) => {
  if (value) currentTab.value = value
}
</script>

<template>
  <div class="q_plugin_container">
    <ChromeTabs style="margin-bottom: 20px" :tabList="tabsList" :value="currentTab" @handle-tabs="changeTab"></ChromeTabs>
    <component :is="dynamicComponent[currentTab]" mode="INTRA_CITY"></component>
  </div>
</template>

<style lang="scss" scoped></style>
