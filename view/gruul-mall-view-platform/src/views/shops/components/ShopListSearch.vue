<template>
    <div>
        <SchemaForm
            v-model="_modelValue"
            :columns="isAddShop ? shopListcolumns : shopExaminecolumns"
            @searchHandle="searchHandle"
            @handleReset="resetHandle"
        >
            <template v-if="isAddShop" #otherOperations>
                <el-button type="primary" round style="margin-left: 12px" @click="handleAddShop">添加店铺</el-button>
            </template>
        </SchemaForm>
    </div>
</template>

<script lang="ts" setup>
import SchemaForm from '@/components/SchemaForm.vue'
import { SearchProps } from '@/components/types'
import { searchParamType } from '../types'
import { useRouter } from 'vue-router'
import { useVModel } from '@vueuse/core'

const props = defineProps({
    isAddShop: {
        type: Boolean,
        default: false,
    },
    modelValue: {
        type: Object,
        default: () => ({}),
    },
})
const $emit = defineEmits(['searchParams'])

const _modelValue = useVModel(props, 'modelValue', $emit)

const router = useRouter()

// 店铺列表搜索
const shopListcolumns: SearchProps[] = [
    {
        label: '店铺ID',
        prop: 'no',
        labelWidth: 60,
        valueType: 'copy',
        fieldProps: {
            placeholder: '请输入店铺ID',
            maxlength: 20,
        },
    },
    {
        label: '店铺名称',
        prop: 'name',
        valueType: 'copy',
        fieldProps: {
            placeholder: '请输入店铺名称',
            maxlength: 20,
        },
    },
    {
        label: '状态',
        prop: 'status',
        valueType: 'select',
        options: [
            {
                value: '',
                label: '全部',
            },
            {
                value: 'NORMAL',
                label: '启用',
            },
            {
                value: 'FORBIDDEN',
                label: '禁用',
            },
        ],
        fieldProps: {
            placeholder: '请选择',
        },
    },
    {
        label: '提佣类型',
        prop: 'extractionType',
        valueType: 'select',
        options: [
            {
                value: '',
                label: '全部',
            },
            {
                value: 'CATEGORY_EXTRACTION',
                label: '类目提佣',
            },
            {
                value: 'ORDER_SALES_EXTRACTION',
                label: '订单金额提佣',
            },
        ],
        fieldProps: {
            placeholder: '请选择',
        },
    },
    {
        label: '店铺类型',
        prop: 'shopType',
        valueType: 'select',
        options: [
            {
                value: '',
                label: '全部',
            },
            {
                value: 'SELF_OWNED',
                label: '自营',
            },
            {
                value: 'PREFERRED',
                label: '优选',
            },
            {
                value: 'ORDINARY',
                label: '普通',
            },
        ],
        fieldProps: {
            placeholder: '请选择',
        },
    },
    {
        label: '经营模式',
        prop: 'shopModes',
        valueType: 'select',
        options: [
            {
                value: '',
                label: '全部',
            },
            {
                value: 'COMMON',
                label: '线上模式',
            },
            {
                value: 'O2O',
                label: 'O2O模式',
            },
        ],
        fieldProps: {
            placeholder: '请选择',
        },
    },
    {
        label: '入驻时间',
        prop: 'data',
        valueType: 'date-picker',
        fieldProps: {
            type: 'daterange',
            startPlaceholder: '开始时间',
            endPlaceholder: '结束时间',
            format: 'YYYY/MM/DD',
            valueFormat: 'YYYY-MM-DD',
            onChange: (data: Array<Date>) => {
                _modelValue.value.settledStartTime = data ? data[0] : undefined
                _modelValue.value.settledEndTime = data ? data[1] : undefined
            },
        },
    },
]

// 店铺审核搜索
const shopExaminecolumns: SearchProps[] = [
    {
        label: '店铺ID',
        prop: 'no',
        labelWidth: 60,
        valueType: 'copy',
        fieldProps: {
            placeholder: '请输入店铺ID',
            maxlength: 20,
        },
    },
    {
        label: '店铺名称',
        prop: 'name',
        valueType: 'copy',
        fieldProps: {
            placeholder: '请输入店铺名称',
            maxlength: 20,
        },
    },
    {
        label: '经营模式',
        prop: 'shopModes',
        valueType: 'select',
        options: [
            {
                value: '',
                label: '全部',
            },
            {
                value: 'COMMON',
                label: '线上模式',
            },
            {
                value: 'O2O',
                label: 'O2O模式',
            },
        ],
        fieldProps: {
            placeholder: '请选择',
        },
    },
    {
        label: '申请人手机',
        labelWidth: 85,
        prop: 'applyUserPhone',
        valueType: 'input', // 改为input类型
        fieldProps: {
            placeholder: '请填写申请人手机',
            maxlength: 11,
            type: 'text', // 使用text类型配合v-model.number
            'v-model.number': '', // 只允许输入数字
            oninput: "value=value.replace(/[^\\d]/g,'')", // 只允许输入数字
            pattern: '^1[3-9]\\d{9}$', // 手机号码格式验证
        },
    },
    {
        label: '审核员手机',
        labelWidth: 85,
        prop: 'auditUserPhone',
        valueType: 'input', // 改为input类型
        fieldProps: {
            placeholder: '请填写审核员手机',
            maxlength: 11,
            type: 'text', // 使用text类型配合v-model.number
            'v-model.number': '', // 只允许输入数字
            oninput: "value=value.replace(/[^\\d]/g,'')", // 只允许输入数字
            pattern: '^1[3-9]\\d{9}$', // 手机号码格式验证
        },
    },
    {
        label: '申请时间',
        prop: 'applyOfData',
        valueType: 'date-picker',
        fieldProps: {
            type: 'daterange',
            startPlaceholder: '开始时间',
            endPlaceholder: '结束时间',
            format: 'YYYY/MM/DD',
            valueFormat: 'YYYY-MM-DD',
            onChange: (data: Array<Date>) => {
                _modelValue.value.applyStartTime = data ? data[0] : undefined
                _modelValue.value.applyEndTime = data ? data[1] : undefined
            },
        },
    },
    {
        label: '审批时间',
        prop: 'data',
        valueType: 'date-picker',
        fieldProps: {
            type: 'daterange',
            startPlaceholder: '开始时间',
            endPlaceholder: '结束时间',
            format: 'YYYY/MM/DD',
            valueFormat: 'YYYY-MM-DD',
            onChange: (data: Array<Date>) => {
                _modelValue.value.settledStartTime = data ? data[0] : undefined
                _modelValue.value.settledEndTime = data ? data[1] : undefined
            },
        },
    },
]

const handleAddShop = () => {
    router.push('/shopList/addShop')
}

const searchHandle = () => {
    $emit('searchParams', _modelValue.value)
}
const resetHandle = () => {
    Object.keys(_modelValue.value).forEach((key) => {
        // @ts-ignore
        _modelValue.value[key] = ''
    })
    searchHandle()
}
</script>
<style scoped lang="scss">
.shop__search-visible {
    padding: 20px 20px 0 20px;
}
</style>
