<script setup lang="ts">
import SchemaForm from '@/components/SchemaForm.vue'

export interface SearchFromDataType {
    no: string // 订单号
    buyerNickname: string // 买家名称
    clinchTime: string
    productName: string // 商品名称
    receiverName: string // 收货人姓名
    platform: string
}

const $route = useRoute()
const $emit = defineEmits(['search-data', 'exportData'])

// 下拉选择状态初始数据
const SearchFromData = reactive<SearchFromDataType>({
    no: $route.query.orderNo as string, // 订单号
    buyerNickname: '', // 买家昵称
    clinchTime: '', // 时间段
    productName: '', // 商品名称
    receiverName: '', // 收货人姓名
    platform: '', //所属渠道
})
const props = defineProps({
    show: {
        type: Boolean,
        default: true,
    },
    order: {
        type: Boolean,
        default: false,
    },
})

const HandleSearch = () => {
    const { no, buyerNickname, productName, receiverName, platform } = SearchFromData
    const params = {
        no,
        buyerNickname,
        productName,
        receiverName,
        startTime: '',
        endTime: '',
        platform,
    }
    if (Array.isArray(SearchFromData.clinchTime)) {
        params.startTime = SearchFromData.clinchTime[0]
        params.endTime = SearchFromData.clinchTime[1]
    }
    $emit('search-data', params)
}

const platformList = [
    {
        label: '全部',
        value: '',
    },
    {
        label: '小程序',
        value: 'WECHAT_MINI_APP',
    },
    // {
    //     label: '公众号',
    //     value: 'WECHAT_MP',
    // },
    {
        label: 'H5商城',
        value: 'H5',
    },
    {
        label: 'IOS端',
        value: 'IOS',
    },
    {
        label: '安卓端',
        value: 'ANDROID',
    },
    {
        label: '鸿蒙端',
        value: 'HARMONY',
    },
    {
        label: 'PC商城',
        value: 'PC',
    },
]

// 表单配置项
const columns = [
    {
        label: '商品名称',
        labelWidth: 75,
        prop: 'productName',
        valueType: 'copy',
        fieldProps: {
            placeholder: '请输入商品名称',
        },
    },
    {
        label: props.order ? '工单号' : '订单号',
        prop: 'no',
        valueType: 'copy',
        fieldProps: {
            placeholder: props.order ? '请输入工单号' : '请输入订单号',
        },
    },
    {
        label: '买家昵称',
        prop: 'buyerNickname',
        valueType: 'copy',
        fieldProps: {
            placeholder: '请输入买家昵称',
        },
    },
    {
        label: '收货人姓名',
        labelWidth: '82',
        prop: 'receiverName',
        valueType: 'copy',
        fieldProps: {
            placeholder: '请输入收货人姓名',
        },
    },
    {
        label: '下单时间',
        prop: 'clinchTime',
        valueType: 'date-picker',
        fieldProps: {
            type: 'daterange',
            startPlaceholder: '开始时间',
            endPlaceholder: '结束时间',
            format: 'YYYY/MM/DD',
            valueFormat: 'YYYY-MM-DD',
        },
    },

    {
        label: '所属渠道',
        prop: 'platform',
        valueType: 'select',
        options: platformList,
        hideInSearch: props.show,
        fieldProps: {
            placeholder: '全部渠道',
        },
    },
]

const newColumns = computed(() => {
    if (props.order) {
        return [
            {
                label: '商品名称',
                labelWidth: 75,
                prop: 'productName',
                valueType: 'copy',
                fieldProps: {
                    placeholder: '请输入商品名称',
                },
            },
            {
                label: props.order ? '工单号' : '订单号',
                prop: 'no',
                valueType: 'copy',
                fieldProps: {
                    placeholder: props.order ? '请输入工单号' : '请输入订单号',
                },
            },
            {
                label: '买家昵称',
                prop: 'buyerNickname',
                valueType: 'copy',
                fieldProps: {
                    placeholder: '请输入买家昵称',
                },
            },
            {
                label: '收货人姓名',
                labelWidth: '82',
                prop: 'receiverName',
                valueType: 'copy',
                fieldProps: {
                    placeholder: '请输入收货人姓名',
                },
            },
            {
                label: '申请时间',
                prop: 'clinchTime',
                valueType: 'date-picker',
                fieldProps: {
                    type: 'daterange',
                    startPlaceholder: '开始时间',
                    endPlaceholder: '结束时间',
                    format: 'YYYY/MM/DD',
                    valueFormat: 'YYYY-MM-DD',
                },
            },
        ]
    } else {
        return columns
    }
})

const handleReset = () => {
    ;(Object.keys(SearchFromData) as (keyof SearchFromDataType)[]).forEach((key) => (SearchFromData[key] = ''))
    HandleSearch()
}
const exportData = () => {
    $emit('exportData', SearchFromData)
}
</script>

<template>
    <SchemaForm v-model="SearchFromData" :columns="newColumns" @searchHandle="HandleSearch" @handleReset="handleReset">
        <template #otherOperations>
            <el-button type="primary" round @click="exportData">导出</el-button>
        </template>
    </SchemaForm>
</template>

<style lang="scss" scoped></style>
