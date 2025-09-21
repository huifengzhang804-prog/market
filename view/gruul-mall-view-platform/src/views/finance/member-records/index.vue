<script setup lang="ts">
import { useRoute } from 'vue-router'
import ChromeTab from '@/components/chrome-tab/index.vue'

onMounted(() => {
    activeName.value = useRoute().query.name ? (useRoute().query.name as TabListType) : 'OpenMembership'
})
type TabListType = 'OpenMembership' | 'VoucherRecord'

const membersTabList = [
    {
        label: '开通会员',
        name: 'OpenMembership',
    },
    {
        label: '用券记录',
        name: 'VoucherRecord',
    },
]
const activeName = ref<TabListType>('OpenMembership')
const defineAsyncComponentReactive = {
    OpenMembership: defineAsyncComponent(() => import('@/views/finance/member-records/openMembership.vue')),
    VoucherRecord: defineAsyncComponent(() => import('@/q-plugin/coupon/voucherRecord.vue')),
}
const changeTab = (value: any) => {
    if (value) activeName.value = value
}
</script>

<template>
    <ChromeTab :tab-list="membersTabList" :value="activeName" style="margin-bottom: 16px" @handle-tabs="changeTab" />
    <template v-if="defineAsyncComponentReactive[activeName]">
        <component :is="defineAsyncComponentReactive[activeName]" />
    </template>
</template>

<style scoped lang="scss"></style>
