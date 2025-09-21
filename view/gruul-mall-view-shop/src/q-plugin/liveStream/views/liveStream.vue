<script setup lang="ts">
import ChromeTab from '@/components/ChromeTab.vue'

type ActiveNameType = 'STUDIO' | 'LIVE_GOODS' | 'LIVE_MEMBER'

const tabList = [
    {
        label: '直播间',
        name: 'STUDIO',
    },
    {
        label: '直播商品',
        name: 'LIVE_GOODS',
    },
    {
        label: '直播成员',
        name: 'LIVE_MEMBER',
    },
]
const activeName = ref<ActiveNameType>('STUDIO')
const defineAsyncComponentReactive = {
    STUDIO: defineAsyncComponent(() => import('@/q-plugin/liveStream/views/components/studio-list/studio-list.vue')),
    LIVE_GOODS: defineAsyncComponent(() => import('@/q-plugin/liveStream/views/components/live-goods/live-goods.vue')),
    LIVE_MEMBER: defineAsyncComponent(() => import('@/q-plugin/liveStream/views/components/live-member/live-member.vue')),
}
const changeTab = (value: any) => {
    if (value) activeName.value = value
}
</script>

<template>
    <ChromeTab :tab-list="tabList" :value="activeName" @handle-tabs="changeTab" />
    <component :is="defineAsyncComponentReactive[activeName]" />
</template>

<style scoped lang="scss"></style>
