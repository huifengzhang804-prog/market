<template>
  <ChromeTab :tab-list="tabList" :value="activeType" @handle-tabs="changeTab" />
  <div style="height: calc(100vh - 133px)">
    <component :is="tabPageComponentMap[activeType]" :tabRadio="activeType" />
  </div>
</template>

<script lang="ts" setup>
import ChromeTab from '@/components/chrome-tab/index.vue'
import { defineAsyncComponent, ref } from 'vue'

const activeType = ref<'supplierList' | 'supplierAudit'>('supplierList')
const tabList = [
  { label: '供应商列表', name: 'supplierList' },
  { label: '供应商审核', name: 'supplierAudit' },
]
const changeTab = (value: any) => {
  if (value) activeType.value = value
}
const tabPageComponentMap = {
  supplierList: defineAsyncComponent(() => import('../components/supplier-management/supplierList.vue')),
  supplierAudit: defineAsyncComponent(() => import('../components/supplier-management/supplierAudit.vue')),
}
</script>

<style lang="scss" scoped></style>
