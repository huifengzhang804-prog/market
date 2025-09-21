<template>
    <SchemaForm v-model="searchType" :columns="columns" :showNumber="3" @searchHandle="search" @handleReset="handleReset">
        <template #otherOperations>
            <router-link to="/inventory/storage/add">
                <el-button type="primary" round style="margin-left: 12px">新增出入库</el-button>
            </router-link>
        </template>
    </SchemaForm>
</template>

<script lang="ts" setup>
import SchemaForm from '@/components/SchemaForm.vue'
import { cloneDeep } from 'lodash-es'
import type { SearchEmitType } from './types'

export type SearchType = Record<'no' | 'stockChangeType' | 'date', string>

const typeList = reactive([
    {
        value: '',
        label: '全部',
    },
    {
        value: 'ALLOCATION_INBOUND',
        label: '调拨入库',
    },
    {
        value: 'OTHER_INBOUND',
        label: '其它入库',
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
    no: '',
    stockChangeType: '',
    date: '',
})
const columns = [
    {
        label: '订单号',
        labelWidth: '55px',
        prop: 'no',
        valueType: 'copy',
        fieldProps: {
            placeholder: '请输入订单号',
        },
    },
    {
        label: '类型',
        labelWidth: '40px',
        prop: 'stockChangeType',
        valueType: 'select',
        options: typeList,
        fieldProps: {
            placeholder: '请选择类型',
        },
    },
    {
        label: '手机号',
        prop: 'operationPhone',
        valueType: 'input', // 改为input类型
        fieldProps: {
            placeholder: '请填写出入库员手机',
            maxlength: 11,
            type: 'text', // 使用text类型配合v-model.number
            'v-model.number': '', // 只允许输入数字
            oninput: "value=value.replace(/[^\\d]/g,'')", // 只允许输入数字
            pattern: '^1[3-9]\\d{9}$', // 手机号码格式验证
        },
    },
    {
        label: '出入库时间',
        prop: 'date',
        valueType: 'date-picker',
        labelWidth: '85px',
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
    const cloneSearchType: SearchEmitType & { date: any } = cloneDeep(searchType) as unknown as SearchEmitType & { date: any }
    if (Array.isArray(cloneSearchType.date) && cloneSearchType.date?.length > 0) {
        cloneSearchType.startTime = cloneSearchType.date?.[0]
        cloneSearchType.endTime = cloneSearchType.date?.[1]
    }
    delete cloneSearchType.date
    $emit('onSearchParams', cloneSearchType)
}
const handleReset = () => {
    ;(Object.keys(searchType) as (keyof SearchType)[]).forEach((key) => (searchType[key] = ''))
    search()
}
</script>
