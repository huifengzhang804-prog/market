<template>
    <SchemaForm v-model="searchType" :columns="columns" @searchHandle="search" @handleReset="handleReset">
        <template #otherOperations>
            <router-link to="/inventory/count/add">
                <el-button type="primary" round style="margin-left: 12px">新增盘点</el-button>
            </router-link>
        </template>
    </SchemaForm>
</template>

<script lang="ts" setup>
import SchemaForm from '@/components/SchemaForm.vue'
export type SearchType = Record<'no' | 'date' | 'inventoryArea' | 'operationPhone', string>

const searchType = reactive({
    no: '',
    date: '',
    inventoryArea: '',
    operationPhone: '',
})
const columns = [
    {
        label: '订单号',
        prop: 'no',
        valueType: 'copy',
        fieldProps: {
            placeholder: '请输入订单号',
        },
    },
    {
        label: '盘点区域',
        prop: 'inventoryArea',
        valueType: 'copy',
        fieldProps: {
            placeholder: '请输入盘点区域',
        },
    },
    {
        label: '手机号',
        prop: 'operationPhone',
        valueType: 'input', // 改为input类型
        fieldProps: {
            placeholder: '请填写盘点员手机号',
            maxlength: 11,
            type: 'text', // 使用text类型配合v-model.number
            'v-model.number': '', // 只允许输入数字
            oninput: "value=value.replace(/[^\\d]/g,'')", // 只允许输入数字
            pattern: '^1[3-9]\\d{9}$', // 手机号码格式验证
        },
    },
    {
        label: '盘点时间',
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
