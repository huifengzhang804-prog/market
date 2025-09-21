<script lang="ts" setup>
import { ref, defineAsyncComponent } from 'vue'
import ChromeTabs from '@/components/ChromeTab.vue'

const tabList = [
  {
    name: 'freightTemplate',
    label: '运费模板',
  },
  {
    name: 'freightAdd',
    label: '地址管理',
  },
  {
    name: 'freightSet',
    label: '快递100',
  },
  {
    name: 'freightPrint',
    label: '打印设置',
  },
  {
    name: 'freightServe',
    label: '物流服务',
  },
]
// 动态组件列表
const reactiveComponent = {
  freightTemplate: defineAsyncComponent(() => import('../components/freight-template.vue')),
  freightAdd: defineAsyncComponent(() => import('../components/freight-add.vue')),
  freightServe: defineAsyncComponent(() => import('../components/freight-serve.vue')),
  freightPrint: defineAsyncComponent(() => import('../components/freight-print.vue')),
  freightSet: defineAsyncComponent(() => import('../components/freight-set.vue')),
}

type TabType = 'freightTemplate' | 'freightAdd' | 'freightServe' | 'freightPrint' | 'freightSet'
// tabber状态
const activeName = ref<TabType>('freightTemplate')

const handleTabClick = (val: TabType) => {
  activeName.value = val
}
</script>

<template>
  <div class="q_plugin_container">
    <ChromeTabs :tab-list="tabList" :value="activeName" style="margin-bottom: 16px" @handle-tabs="handleTabClick" />
    <component :is="reactiveComponent[activeName]"></component>
  </div>
</template>

<style lang="scss" scoped></style>
