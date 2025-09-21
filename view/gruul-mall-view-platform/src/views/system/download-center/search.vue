<template>
    <div class="form">
        <SchemaForm v-model="searchFromData" :columns="columns" @searchHandle="handleSearch" @handleReset="hanndleReset" @collapse="collapse" />
    </div>
</template>

<script lang="ts" setup>
import SchemaForm from '@/components/SchemaForm.vue'
import { DATA_TYPE_ENUM, EXPORT_STATUS_ENUM } from './types'
import { cloneDeep } from 'lodash-es'

const $emit = defineEmits(['search', 'changeShow'])
const searchFromData = reactive({
    dataType: '' as keyof typeof DATA_TYPE_ENUM | '',
    status: '' as keyof typeof EXPORT_STATUS_ENUM | '',
    exportDate: '',
    userPhone: '',
})
const dataTypeList = ref<{ value: keyof typeof DATA_TYPE_ENUM; label: string }[]>([])
const statusOptions = ref<{ value: keyof typeof EXPORT_STATUS_ENUM; label: string }[]>([])
const initialDataTypeList = () => {
    dataTypeList.value = []
    for (const dataType in DATA_TYPE_ENUM) {
        const dataTypeStr = dataType as keyof typeof DATA_TYPE_ENUM
        dataTypeList.value.push({ value: dataTypeStr, label: DATA_TYPE_ENUM[dataTypeStr] })
    }
}
initialDataTypeList()

const initialStatusOptions = () => {
    statusOptions.value = []
    for (const status in EXPORT_STATUS_ENUM) {
        const statusStr = status as keyof typeof EXPORT_STATUS_ENUM
        statusOptions.value.push({ value: statusStr, label: EXPORT_STATUS_ENUM[statusStr] })
    }
}
const columns = [
    {
        label: '数据类型',
        labelWidth: 75,
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
        labelWidth: 45,
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
            type: 'datetimerange',
            startPlaceholder: '开始时间',
            endPlaceholder: '结束时间',
            format: 'YYYY/MM/DD HH:mm:ss',
            valueFormat: 'YYYY-MM-DD HH:mm:ss',
        },
    },
]
initialStatusOptions()

const handleSearch = () => {
    const cloneSearchForm: any = cloneDeep(searchFromData)
    if (Array.isArray(searchFromData.exportDate)) {
        cloneSearchForm.exportStartDate = searchFromData.exportDate?.[0]
        cloneSearchForm.exportEndDate = searchFromData.exportDate?.[1]
    }
    delete cloneSearchForm.exportDate
    $emit('search', cloneSearchForm)
}
const hanndleReset = () => {
    // @ts-ignore
    Object.keys(searchFromData).forEach((key) => (searchFromData[key] = ''))
    handleSearch()
}
const collapse = (e: boolean) => {
    $emit('changeShow', e)
}
</script>
