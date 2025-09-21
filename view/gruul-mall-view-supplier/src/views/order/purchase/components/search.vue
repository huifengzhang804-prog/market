<template>
    <div>
        <SchemaForm v-model="searchFormData" :columns="columns" @searchHandle="handleSearchOrder" @handleReset="handleReset">
            <template #otherOperations>
                <el-button type="primary" round @click="supplierExport">导出</el-button>
            </template>
        </SchemaForm>
    </div>
</template>

<script lang="ts" setup>
import SchemaForm from '@/components/SchemaForm.vue'
import { cloneDeep } from 'lodash-es'
import { useOptions } from '@/composables/useOptions'

const $route = useRoute()
type SearchFormData = {
    no: string
    shopId: string
    date: string
}
const searchFormData = reactive<SearchFormData>({
    no: ($route.query.orderNo as string) || '', // 订单号
    shopId: '', // 采购商
    date: '', // 采购时间
})
const emitFn = defineEmits(['search', 'cardVisibleChange', 'supplierExport'])
const handleSearchOrder = () => {
    const cloneSearchFormData: { [key: string]: any } = cloneDeep(searchFormData)
    if (cloneSearchFormData.date?.length) {
        cloneSearchFormData.startDate = cloneSearchFormData?.date?.[0]
        cloneSearchFormData.endDate = cloneSearchFormData?.date?.[1]
    }
    delete cloneSearchFormData.date
    emitFn('search', cloneSearchFormData)
}

const handleReset = () => {
    Object.keys(searchFormData).forEach((key) => (searchFormData[key] = ''))
    handleSearchOrder()
}

const { shopSearchOptions } = useOptions()

// 表单配置项
const columns = [
    {
        label: '订单号',
        prop: 'no',
        valueType: 'copy',
    },
    {
        label: '店铺',
        prop: 'shopId',
        ...shopSearchOptions,
    },
    {
        label: '采购员手机',
        prop: 'purchasePhone',
        labelWidth: '85',
        valueType: 'copy',
    },
    {
        label: '下单时间',
        prop: 'date',
        valueType: 'date-picker',
        fieldProps: {
            type: 'daterange',
            format: 'YYYY-MM-DD',
            valueFormat: 'YYYY-MM-DD',
        },
    },
]
const supplierExport = () => {
    emitFn('supplierExport', searchFormData)
}
</script>
