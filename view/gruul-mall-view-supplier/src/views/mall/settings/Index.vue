<template>
    <ChromeTab :tab-list="tabList" :value="currentTab" style="margin-bottom: 16px" @handle-tabs="handleTabClick" />
    <div class="overh fdc1">
        <component :is="dynamicComponent[currentTab]"></component>
    </div>
</template>

<script setup lang="ts">
import { defineAsyncComponent, reactive, ref } from 'vue'
import ChromeTab from '@/components/ChromeTab.vue'

const tabList = [
    {
        name: 'ShopSet',
        label: '店铺设置',
    },
    {
        name: 'InvoiceSet',
        label: '发票设置',
    },
    {
        name: 'CategorySet',
        label: '类目设置',
    },
]
const dynamicComponent = reactive({
    ShopSet: defineAsyncComponent(() => import('./components/ShopSet.vue')),
    InvoiceSet: defineAsyncComponent(() => import('./components/InvoiceSet.vue')),
    CategorySet: defineAsyncComponent(() => import('./components/CategorySet.vue')),
    // WechatSpecialMerchantSet: defineAsyncComponent(() => import('./components/WechatSpecialMerchantSet.vue')),
})
const currentTab = ref('ShopSet')

const handleTabClick = (val: string) => {
    currentTab.value = val
}
</script>

<style scoped lang="scss">
.settings-tab {
    width: 100%;
}
</style>
