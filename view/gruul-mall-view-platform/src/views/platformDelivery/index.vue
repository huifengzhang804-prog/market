<template>
    <ChromeTab :tab-list="tabPaneSet" style="margin-bottom: 16px" :value="activeName" @handle-tabs="changeTab" />
    <component :is="reactiveComponent[activeName as keyof typeof reactiveComponent]" />
</template>

<script lang="ts" setup>
import ChromeTab from '@/components/chrome-tab/index.vue'

const activeName = ref('shopDelivery')
const tabPaneSet = [
    { label: '自营商家发货', name: 'shopDelivery' },
    { label: '地址管理', name: 'addressManage' },
    { label: '物流服务', name: 'logisticsService' },
    { label: '快递100', name: 'logisticsSet' },
    { label: '打印设置', name: 'printSet' },
]

const changeTab = (value: any) => {
    if (value) activeName.value = value
}
const reactiveComponent = {
    shopDelivery: defineAsyncComponent(() => import('./pages/shop-delivery.vue')),
    addressManage: defineAsyncComponent(() => import('./pages/address-manage/index.vue')),
    logisticsService: defineAsyncComponent(() => import('./pages/logistics-service/index.vue')),
    logisticsSet: defineAsyncComponent(() => import('./pages/logistics-set.vue')),
    printSet: defineAsyncComponent(() => import('./pages/print-set.vue')),
}
</script>
