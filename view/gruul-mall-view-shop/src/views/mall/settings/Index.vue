<template>
    <ChromeTab :tab-list="tabList" :value="currentTab" style="margin-bottom: 16px" @handle-tabs="handleTabClick" />
    <component :is="dynamicComponent[currentTab]"></component>
</template>

<script setup lang="ts">
import type { Component } from 'vue'
import ChromeTab from '@/components/ChromeTab.vue'

enum TabName {
    ShopSet = 'ShopSet',
    TransactionSet = 'TransactionSet',
    CategorySet = 'CategorySet',
    InvoiceHeader = 'InvoiceHeader',
    // WechatSpecialMerchantSet = 'WechatSpecialMerchantSet',
}
const tabList = [
    {
        name: 'ShopSet',
        label: '店铺设置',
    },
    {
        name: 'CategorySet',
        label: '类目设置',
    },
    {
        name: 'TransactionSet',
        label: '交易设置',
    },
    {
        name: 'InvoiceHeader',
        label: '发票抬头',
    },
]
const dynamicComponent: Record<keyof typeof TabName, Component> = {
    ShopSet: defineAsyncComponent(() => import('./components/ShopSet.vue')),
    CategorySet: defineAsyncComponent(() => import('./components/CategorySet.vue')),
    TransactionSet: defineAsyncComponent(() => import('./components/TransactionSet.vue')),
    InvoiceHeader: defineAsyncComponent(() => import('@/q-plugin/supplier/InvoiceHeader.vue')),
    // WechatSpecialMerchantSet: defineAsyncComponent(() => import('./components/WechatSpecialMerchantSet.vue')),
}
const currentTab = ref<keyof typeof TabName>(TabName.ShopSet)
// 切换tab
const handleTabClick = (status: keyof typeof TabName) => {
    currentTab.value = status
}
</script>

<style scoped lang="scss">
.settings-tab {
    width: 100%;
    padding-left: 20px;
    padding-right: 20px;
    position: relative;
}
</style>
