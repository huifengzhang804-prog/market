<script setup lang="ts">
import { useRoute } from 'vue-router'
import ChromeTab from '@/components/chrome-tab/index.vue'

onMounted(() => {
    activeName.value = useRoute().query.name ? (useRoute().query.name as TabListType) : 'FreeMember'
})
type TabListType = 'FreeMember' | 'RightsMember' | 'GrowthValueSetting'

const membersTabList = [
    {
        label: '免费会员',
        name: 'FreeMember',
    },
    {
        label: '付费会员',
        name: 'PayingMember',
    },
    {
        label: '会员权益',
        name: 'RightsMember',
    },
    {
        label: '成长值设置',
        name: 'GrowthValueSetting',
    },
]
const activeName = ref<TabListType>('FreeMember')
const defineAsyncComponentReactive = {
    FreeMember: defineAsyncComponent(() => import('@/views/vipSetting/components/FreeMember.vue')),
    PayingMember: defineAsyncComponent(() => import('@/q-plugin/member/PaidMember.vue')),
    RightsMember: defineAsyncComponent(() => import('@/views/vipSetting/components/RightsMember.vue')),
    GrowthValueSetting: defineAsyncComponent(() => import('./components/GrowthValueSetting.vue')),
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
