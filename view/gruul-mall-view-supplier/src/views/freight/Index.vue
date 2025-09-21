<script setup lang="ts">
import { ref, defineAsyncComponent } from 'vue'
import ChromeTab from '@/components/ChromeTab.vue'

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
const reactiveComponent = shallowRef({
    freightTemplate: defineAsyncComponent(() => import('./components/freight-template.vue')),
    freightAdd: defineAsyncComponent(() => import('./components/freight-add.vue')),
    freightSet: defineAsyncComponent(() => import('./components/freight-set.vue')),
    freightPrint: defineAsyncComponent(() => import('./components/freight-print.vue')),
    freightServe: defineAsyncComponent(() => import('./components/freight-serve.vue')),
})
// tabber状态
const activeName = ref<'freightTemplate' | 'freightAdd' | 'freightServe' | 'freightPrint' | 'freightSet'>('freightTemplate')
const handleTabClick = (val: 'freightTemplate' | 'freightAdd' | 'freightServe' | 'freightPrint' | 'freightSet') => {
    activeName.value = val
}
</script>

<template>
    <!-- 导航栏 -->
    <ChromeTab :tab-list="tabList" :value="activeName" style="margin-bottom: 16px" @handle-tabs="handleTabClick" />
    <component :is="reactiveComponent[activeName]"></component>
</template>

<style lang="scss" scoped></style>
