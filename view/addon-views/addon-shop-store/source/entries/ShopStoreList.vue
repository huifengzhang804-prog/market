<script lang="ts" setup>
import { ref, defineAsyncComponent, type Component } from 'vue'
import ChromeTabs from '@/components/ChromeTab.vue'
import SetPinter from '@/q-plugin/components/printer.vue'
import SetPrintTemplate from '@/q-plugin/components/printTemplate.vue'
import SetPrintingTask from '@/q-plugin/components/printingTask.vue'

const enum TabName {
  // 门店列表
  storeList = 'storeList',
  // 店员列表
  salesclerkList = 'salesclerkList',
  // 打印机
  printer = 'printer',
  // 打印模板
  printTemplate = 'printTemplate',
  // 打印任务
  printingTask = 'printingTask',
}
const tabList = [
  {
    name: 'storeList',
    label: '门店列表',
  },
  {
    name: 'salesclerkList',
    label: '店员列表',
  },
  {
    name: 'printer',
    label: '打印机',
  },
  {
    name: 'printTemplate',
    label: '打印模板',
  },
  {
    name: 'printingTask',
    label: '打印任务',
  },
]
const dynamicComponent: Record<keyof typeof TabName, Component> = {
  storeList: defineAsyncComponent(() => import('../components/storeList.vue')),
  salesclerkList: defineAsyncComponent(() => import('../components/salesclerkList.vue')),
  printer: SetPinter,
  printTemplate: SetPrintTemplate,
  printingTask: SetPrintingTask,
}
const activeTab = ref<keyof typeof TabName>('storeList')

const changeTab = (value: keyof typeof TabName) => {
  activeTab.value = value
}
</script>

<template>
  <div class="q_plugin_container">
    <ChromeTabs :tab-list="tabList" :value="activeTab" style="margin-bottom: 16px" @handle-tabs="changeTab" />
    <component :is="dynamicComponent[activeTab]" mode="STORE_PICKUP_SELF"></component>
  </div>
</template>

<style lang="scss" scoped></style>
