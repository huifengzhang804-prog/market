<template>
    <SchemaForm v-model="searchType" :columns="columns" @searchHandle="search" @handleReset="handleReset" />
</template>

<script lang="ts" setup>
import SchemaForm from '@/components/SchemaForm.vue'

export type SearchType = {
    id: string
    header: string
    taxIdentNo: string
    orderNo: string
    date: string
}

const TypeList = reactive([
    {
        value: '',
        label: '全部',
    },
    {
        value: 'VAT_SPECIAL',
        label: '增值税专用发票',
    },
    {
        value: 'VAT_GENERAL',
        label: '增值税普通发票',
    },
])

const searchType = reactive({
    id: '',
    header: '',
    taxIdentNo: '',
    orderNo: '',
    date: '',
})
const columns = [
    {
        label: '申请单号',
        prop: 'id',
        valueType: 'copy',
        fieldProps: {
            placeholder: '请输入申请单号',
        },
    },
    {
        label: '发票抬头',
        prop: 'header',
        valueType: 'copy',
        fieldProps: {
            placeholder: '请输入抬头',
        },
    },
    {
        label: '关联订单',
        labelWidth: 85,
        prop: 'orderNo',
        valueType: 'copy',
        fieldProps: {
            placeholder: '请输入关联订单号',
        },
    },
    {
        label: '税号',
        prop: 'taxIdentNo',
        valueType: 'copy',
        fieldProps: {
            placeholder: '请输入税号',
        },
    },
    {
        label: '发票类型',
        prop: 'invoiceType',
        valueType: 'select',
        options: TypeList,
        fieldProps: {
            placeholder: '请选择',
        },
    },
    {
        label: '申请时间',
        prop: 'date',
        valueType: 'date-picker',
        fieldProps: {
            type: 'datetimerange',
            startPlaceholder: '开始日期',
            endPlaceholder: '结束日期',
            format: 'YYYY/MM/DD HH:mm:ss',
            valueFormat: 'YYYY-MM-DD HH:mm:ss',
        },
    },
    {
        label: '开票时间',
        prop: '',
        valueType: 'date-picker',
        fieldProps: {
            type: 'datetimerange',
            startPlaceholder: '开始日期',
            endPlaceholder: '结束日期',
            format: 'YYYY/MM/DD HH:mm:ss',
            valueFormat: 'YYYY-MM-DD HH:mm:ss',
        },
    },
]
const $emit = defineEmits(['onSearchParams'])

function search() {
    $emit('onSearchParams', toRaw(searchType))
}
const handleReset = () => {
    ;(Object.keys(searchType) as (keyof SearchType)[]).forEach((key) => (searchType[key] = ''))
    search()
}
</script>
