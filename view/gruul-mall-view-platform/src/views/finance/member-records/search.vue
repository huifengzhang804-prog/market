<template>
    <div class="search_container">
        <el-config-provider :empty-values="[undefined, null]">
            <SchemaForm v-model="searchForm" :columns="columns" label-width="90" @searchHandle="handleSearch" @handleReset="handleReset">
                <template #otherOperations>
                    <el-button round type="primary" @click="handleExport">导出</el-button>
                </template>
            </SchemaForm>
        </el-config-provider>
    </div>
    <div class="handle_container df">
        <div class="export cup">
            <QIcon name="icon-wenhao" size="18px" @click="showMemberRecordsDialog = true" />
        </div>
    </div>
    <el-dialog v-model="showMemberRecordsDialog" title="会员记录说明" :width="800">
        <description-dialog />
    </el-dialog>
</template>

<script lang="ts" setup>
import SchemaForm from '@/components/SchemaForm.vue'
import descriptionDialog from './description-dialog.vue'
import { cloneDeep } from 'lodash-es'
const showMemberRecordsDialog = ref(false)
const searchForm = reactive({
    no: '',
    nickName: '',
    userPhone: '',
    level: '',
    buyTime: '',
    expireTime: '',
})
const $emit = defineEmits(['changeShow', 'search', 'export'])
const columns = [
    {
        label: '订单号',
        labelWidth: 60,
        prop: 'no',
        valueType: 'copy',
        fieldProps: {
            placeholder: '请输入订单号',
        },
    },
    {
        label: '用户昵称',
        prop: 'nickName',
        valueType: 'copy',
        fieldProps: {
            placeholder: '请输入用户昵称',
        },
    },
    {
        label: '购买时间',
        prop: 'buyTime',
        valueType: 'date-picker',
        fieldProps: {
            type: 'daterange',
            startPlaceholder: '开始时间',
            endPlaceholder: '结束时间',
            valueFormat: 'YYYY-MM-DD',
        },
    },
    {
        label: '手机号',
        prop: 'userPhone',
        valueType: 'copy',
        fieldProps: {
            placeholder: '请输入手机号',
        },
    },
    {
        label: '会员等级',
        prop: 'level',
        valueType: 'select',
        fieldProps: {
            placeholder: '请选择会员等级',
        },
        options: [
            { label: '全部', value: '' },
            ...new Array(10).fill(0).map((_, index) => {
                return {
                    label: `等级${index + 1}`,
                    value: index + 1,
                }
            }),
        ],
    },
    {
        label: '到期时间',
        prop: 'expireTime',
        valueType: 'date-picker',
        fieldProps: {
            type: 'daterange',
            startPlaceholder: '开始时间',
            endPlaceholder: '结束时间',
            valueFormat: 'YYYY-MM-DD',
        },
    },
]

const handleSearch = () => {
    const cloneSearchForm: any = cloneDeep(searchForm)
    if (Array.isArray(searchForm.buyTime)) {
        cloneSearchForm.buyStartTime = searchForm.buyTime?.[0]
        cloneSearchForm.buyEndTime = searchForm.buyTime?.[1]
    }
    if (Array.isArray(searchForm.expireTime)) {
        cloneSearchForm.expireStartTime = searchForm.expireTime?.[0]
        cloneSearchForm.expireEndTime = searchForm.expireTime?.[1]
    }
    $emit('search', cloneSearchForm)
}
const handleReset = () => {
    // @ts-ignore
    Object.keys(searchForm).forEach((key) => (searchForm[key] = ''))
    handleSearch()
}
const handleExport = () => {
    $emit('export')
}
</script>

<style lang="scss" scoped>
@include b(export-icon) {
    font-size: 18px;
}
@include b(actions) {
    @include flex(space-between);
}
@include b(export) {
    margin-left: auto;
}
</style>
