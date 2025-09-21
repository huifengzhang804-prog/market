<template>
    <ChromeTab :tab-list="list" :value="tabRadio" @handle-tabs="changeTab" />
    <component :is="tabPageComponentMap[tabRadio]" :tabRadio="tabRadio" />
</template>

<script lang="ts" setup>
import ChromeTab from '@/components/chrome-tab/index.vue'
import { useRoute } from 'vue-router'
const $route = useRoute()

// const tabRadio = ref<'storeList' | 'storeReview'>('storeList')
// 经营概况直接进入待审核
const tabRadio = ref<'storeList' | 'storeReview'>($route.query.name ? 'storeReview' : 'storeList')

const list = [
    { label: '店铺列表', name: 'storeList' },
    { label: '店铺审核', name: 'storeReview' },
]
const changeTab = (value: any) => {
    if (value) tabRadio.value = value
}
const tabPageComponentMap = {
    storeList: defineAsyncComponent(() => import('@/views/shops/storeList.vue')),
    storeReview: defineAsyncComponent(() => import('@/views/shops/storeReview.vue')),
}
</script>

<style lang="scss" scoped></style>
