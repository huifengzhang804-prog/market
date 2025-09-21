<template>
    <SchemaForm v-model="searchForm" :columns="columns" :show-number="3" @searchHandle="searchHandle" @handleReset="handleReset" />
</template>
<script lang="ts" setup>
import SchemaForm from '@/components/SchemaForm.vue'
import type { SearchFormType } from '../types'

const searchForm = reactive<SearchFormType>({
    name: '',
    mobile: '',
    supplierSn: '',
})
const columns = [
    {
        label: '供应商名称',
        prop: 'name',
        valueType: 'copy',
        labelWidth: '90',
        fieldProps: {
            placeholder: '请填写供应商名称',
        },
    },
    {
        label: '手机号',
        prop: 'mobile',
        labelWidth: '65',
        valueType: 'copy',
        fieldProps: {
            placeholder: '请填写手机号',
        },
    },
    {
        label: '供应商ID',
        prop: 'supplierSn',
        valueType: 'copy',
        fieldProps: {
            placeholder: '请填写供应商ID',
        },
    },
]

const $emit = defineEmits(['onSearchParam'])
const searchHandle = () => {
    $emit('onSearchParam', searchForm)
}
const handleReset = () => {
    ;(Object.keys(searchForm) as (keyof SearchFormType)[]).forEach((key) => (searchForm[key] = ''))
    searchHandle()
}
</script>
