<script setup lang="ts">
import VipIntegralSubsidiary from '@/q-plugin/integral/VipIntegralSubsidiary.vue'
import VipDistributionInformation from '@/q-plugin/distribution/VipDistributionInformation.vue'

type TabActive = 'transactionDetails' | 'theBalanceOfSubsidiary'
const tabActive = ref<TabActive>('transactionDetails')
const tabsArr = ref([
    { label: '交易明细', name: 'transactionDetails' },
    { label: '储值明细', name: 'theBalanceOfSubsidiary' },
])
const $props = defineProps({
    userId: {
        type: [String, Number],
        default: '',
    },
})
const asyncComponent = shallowRef({
    transactionDetails: defineAsyncComponent(() => import('./transaction-details.vue')),
    theBalanceOfSubsidiary: defineAsyncComponent(() => import('./balance-subsidiary.vue')),
})
</script>

<template>
    <el-tabs v-model="tabActive" class="demo-tabs">
        <el-tab-pane v-for="item in tabsArr" :key="item.label" :name="item.name" :label="item.label" />
        <vip-integral-subsidiary :user-id="$props.userId" />
        <vip-distribution-information :user-id="$props.userId" />
    </el-tabs>
    <template v-if="$props.userId && asyncComponent[tabActive]">
        <component :is="asyncComponent[tabActive]" :user-id="$props.userId"></component>
    </template>
</template>

<style scoped lang="scss"></style>
