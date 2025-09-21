<script setup lang="ts">
import type { Component } from 'vue'
import ChromeTab from '@/components/chrome-tab/index.vue'

interface AsyncComponent {
    BasicSet: Component
    FileSet: Component
    MessageSet: Component
    WechatPay: Component
    PayTreasurePay: Component
    LogisticsExpress: Component
    WebsiteSettings: Component
    ThirdPartyDelivery: Component
    PrintSet: Component
    SetAmapKey: Component
}

const tabPaneSet = [
    { label: '基础设置', name: 'BasicSet' },
    { label: '微信支付', name: 'WechatPay' },
    { label: '支付宝支付', name: 'PayTreasurePay' },
    { label: '网站设置', name: 'WebsiteSettings' },
    { label: '短信设置', name: 'MessageSet' },
    // { label: '物流设置', name: 'PrintSet' },
    { label: 'OSS设置', name: 'FileSet' },
    { label: '物流设置', name: 'LogisticsExpress' },
    { label: '第三方配送', name: 'ThirdPartyDelivery' },
    { label: '高德地图', name: 'SetAmapKey' },
    { label: '打印设置', name: 'PrintSet' },
]
const activeName = ref<keyof AsyncComponent>('BasicSet')
// 动态组件列表
const reactiveComponent: AsyncComponent = {
    BasicSet: defineAsyncComponent(() => import('./components/BasicSet.vue')),
    WechatPay: defineAsyncComponent(() => import('./components/WechatPay.vue')),
    PayTreasurePay: defineAsyncComponent(() => import('./components/PayTreasurePay.vue')),
    MessageSet: defineAsyncComponent(() => import('./components/MessageSet.vue')),
    LogisticsExpress: defineAsyncComponent(() => import('./components/LogisticsExpress.vue')),
    FileSet: defineAsyncComponent(() => import('./components/FileSet.vue')),
    WebsiteSettings: defineAsyncComponent(() => import('./components/WebsiteSettings.vue')),
    ThirdPartyDelivery: defineAsyncComponent(() => import('@/q-plugin/cityDistribution/ThirdPartyDelivery.vue')),
    PrintSet: defineAsyncComponent(() => import('./components/PrintSet.vue')),
    SetAmapKey: defineAsyncComponent(() => import('./components/SetAmapKey.vue')),
}

const changeTab = (value: any) => {
    if (value) activeName.value = value
}
</script>

<template>
    <ChromeTab :tab-list="tabPaneSet" :value="activeName" style="margin-bottom: 16px" @handle-tabs="changeTab" />
    <component :is="reactiveComponent[activeName]" />
</template>
<style lang="scss" scoped></style>
