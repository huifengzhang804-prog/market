<template>
    <SchemaForm v-model="searchFromData" :columns="columns" @searchHandle="search" @handleReset="handleReset" />
</template>

<script lang="ts" setup>
import { DATA_TYPE_ENUM, EXPORT_STATUS_ENUM } from './types'

const $emit = defineEmits(['search'])

export interface SearchType {
    dataType: keyof typeof DATA_TYPE_ENUM | string
    status: keyof typeof EXPORT_STATUS_ENUM | string
    exportDate: string
    userPhone: string
}
const searchFromData = reactive<SearchType>({
    dataType: '',
    status: '',
    exportDate: '',
    userPhone: '',
})

function search() {
    $emit('search', toRaw(searchFromData))
}
const handleReset = () => {
    Object.keys(searchFromData).forEach((key) => {
        searchFromData[key as keyof SearchType] = ''
    })
    search()
}
const dataTypeList = ref<{ value: keyof typeof DATA_TYPE_ENUM; label: string }[]>([])
const initialDataTypeList = () => {
    dataTypeList.value = []
    for (const dataType in DATA_TYPE_ENUM) {
        const dataTypeStr = dataType as keyof typeof DATA_TYPE_ENUM
        dataTypeList.value.push({ value: dataTypeStr, label: DATA_TYPE_ENUM[dataTypeStr] })
    }
}
initialDataTypeList()
const statusOptions = ref<{ value: keyof typeof EXPORT_STATUS_ENUM; label: string }[]>([])
const initialStatusOptions = () => {
    statusOptions.value = []
    for (const status in EXPORT_STATUS_ENUM) {
        const statusStr = status as keyof typeof EXPORT_STATUS_ENUM
        statusOptions.value.push({ value: statusStr, label: EXPORT_STATUS_ENUM[statusStr] })
    }
}
initialStatusOptions()
const columns = [
    {
        label: '数据类型',
        prop: 'dataType',
        valueType: 'select',
        options: dataTypeList,
        fieldProps: {
            placeholder: '请选择数据类型',
        },
    },
    {
        label: '状态',
        prop: 'status',
        valueType: 'select',
        options: statusOptions,
        fieldProps: {
            placeholder: '请选择状态',
        },
    },
    {
        label: '导出用户',
        prop: 'userPhone',
        valueType: 'copy',
        fieldProps: {
            placeholder: '请输入导出用户',
        },
    },
    {
        label: '导出时间',
        prop: 'exportDate',
        valueType: 'date-picker',
        fieldProps: {
            type: 'daterange',
            startPlaceholder: '开始时间',
            endPlaceholder: '结束时间',
            valueFormat: 'YYYY-MM-DD',
            format: 'YYYY-MM-DD',
        },
    },
]
</script>
