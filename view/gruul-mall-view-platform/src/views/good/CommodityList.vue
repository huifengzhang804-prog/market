<template>
    <ChromeTab :tab-list="list" :value="tabRadio" @handle-tabs="changeTab" />
    <component :is="tabPageComponentMap[tabRadio]" />
</template>

<script lang="ts" setup>
import ChromeTab from '@/components/chrome-tab/index.vue'
const tabRadio = ref<'shop' | 'supplier'>('shop')
const list = [
    { label: '店铺商品', name: 'shop' },
    { label: '供应商商品', name: 'supplier' },
]
const changeTab = (value: any) => {
    if (value) tabRadio.value = value
}
const tabPageComponentMap = {
    shop: defineAsyncComponent(() => import('./tab-pages/shopGoods.vue')),
    supplier: defineAsyncComponent(() => import('@/q-plugin/supplier/CommodityList.vue')),
}
</script>
