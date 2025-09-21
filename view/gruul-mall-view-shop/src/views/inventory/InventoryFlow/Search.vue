<template>
    <SchemaForm v-model="searchType" :columns="columns" @searchHandle="search" @handleReset="handleReset">
        <template #otherOperations>
            <el-button type="primary" @click="handleExport">导出</el-button>
        </template>
    </SchemaForm>
</template>

<script lang="ts" setup>
import SchemaForm from '@/components/SchemaForm.vue'

export type SearchType = Record<'productName' | 'productId' | 'Id' | 'stockChangeType' | 'sellType' | 'orderNo' | 'date', string>

const sellTypeList = reactive([
    {
        value: '',
        label: '全部',
    },
    {
        value: 'PURCHASE',
        label: '采购商品',
    },
    {
        value: 'OWN',
        label: '自有商品',
    },
])
const stockChangeTypeList = reactive([
    {
        value: '',
        label: '全部',
    },
    {
        value: 'PUBLISHED_INBOUND',
        label: '发布入库',
    },
    {
        value: 'EDITED_INBOUND',
        label: '编辑入库',
    },
    {
        value: 'OVERAGE_INBOUND',
        label: '盘盈入库',
    },
    {
        value: 'RETURNED_INBOUND',
        label: '退货入库',
    },
    {
        value: 'ORDER_CANCELLED_INBOUND',
        label: '订单取消入库',
    },
    {
        value: 'ALLOCATION_INBOUND',
        label: '调拨入库',
    },
    {
        value: 'PURCHASE_INBOUND',
        label: '采购入库',
    },
    {
        value: 'OTHER_INBOUND',
        label: '其它入库',
    },
    {
        value: 'SOLD_OUTBOUND',
        label: '销售出库',
    },
    {
        value: 'EDITED_OUTBOUND',
        label: '编辑出库',
    },
    {
        value: 'SHORTAGE_OUTBOUND',
        label: '盘亏出库',
    },
    {
        value: 'ALLOCATION_OUTBOUND',
        label: '调拨出库',
    },
    {
        value: 'OTHER_OUTBOUND',
        label: '其它出库',
    },
])
const searchType = reactive({
    productName: '',
    productId: '',
    Id: '',
    stockChangeType: '',
    sellType: '',
    orderNo: '',
    date: '',
})
const columns = [
    {
        label: '商品名称',
        prop: 'productName',
        valueType: 'copy',
        fieldProps: {
            placeholder: '请输入商品名称',
        },
    },
    {
        label: '出入库类型',
        prop: 'stockChangeType',
        labelWidth: '85px',
        valueType: 'select',
        options: stockChangeTypeList,
        fieldProps: {
            placeholder: '请选择库存类型',
        },
    },
    {
        label: '关联订单',
        prop: 'orderNo',
        valueType: 'copy',
        fieldProps: {
            placeholder: '请输入关联订单号',
        },
    },
    {
        label: '单据编号',
        prop: 'Id',
        valueType: 'copy',
        fieldProps: {
            placeholder: '请输入单据编号',
            onChange: () => {
                searchType.Id = searchType.Id?.replace(/[^\d]/g, '') || ''
            },
        },
    },
    {
        label: '商品来源',
        prop: 'sellType',
        valueType: 'select',
        options: sellTypeList,
        fieldProps: {
            placeholder: '请选择商品来源',
        },
    },
    {
        label: 'SPUID',
        labelWidth: '60px',
        prop: 'productId',
        valueType: 'copy',
        fieldProps: {
            placeholder: '请输入SPUID',
            type: 'number',
            onChange: () => {
                searchType.productId = searchType.productId?.replace(/[^\d]/g, '') || ''
            },
        },
    },
    {
        label: '出入库时间',
        prop: 'date',
        labelWidth: '85px',
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
const $emit = defineEmits(['onSearchParams', 'onExport'])

function search() {
    $emit('onSearchParams', toRaw(searchType))
}

const handleExport = () => {
    $emit('onExport', toRaw(searchType))
}

const handleReset = () => {
    ;(Object.keys(searchType) as (keyof SearchType)[]).forEach((key) => (searchType[key] = ''))
    search()
}
</script>
