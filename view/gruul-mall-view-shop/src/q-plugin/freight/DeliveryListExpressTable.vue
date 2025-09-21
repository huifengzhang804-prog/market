<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import UseConvert from '@/AutoImportCustomUse/useConvert'
import SplitTable from '@/views/order/components/order-split-table/SplitTable'
import SplitTableColumn from '@/views/order/components/order-split-table/split-table-column.vue'

const $props = defineProps({
    tableData: { type: Array, default: () => [] },
    companySelectList: { type: Array, default: () => [] },
    logisticsCompanyCode: { type: String, default: '' },
    filterOrderList: { type: Function, default: () => {} },
    changeExpressCompanyName: { type: Function, default: () => {} },
})
</script>
<template>
    <q-plugin
        :context="{
            SplitTable,
            SplitTableColumn,
            UseConvert,
        }"
        :properties="$props"
        name="ShopDeliveryListExpressTable"
        service="gruul-mall-freight"
    />
</template>
