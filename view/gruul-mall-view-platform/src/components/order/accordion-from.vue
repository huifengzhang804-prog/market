<script lang="ts" setup>
import { h } from 'vue'
import SchemaForm from '@/components/SchemaForm.vue'
import type { SearchFromData } from '@/views/order/types.ts'
import ShopSelect from './shop-select.vue'
import { SearchProps } from '../types'

const $emit = defineEmits(['search-data', 'exportData'])
const props = defineProps({
    order: {
        type: Boolean,
        default: false,
    },
})
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
// 下拉选择状态初始数据
const searchFromData = reactive<SearchFromData>({
    orderNo: '', // 订单号
    buyerNickname: '', // 买家名称
    clinchTime: '',
    productName: '', // 商品名称
    receiverName: '', // 收货人姓名
    distributionMode: '', // 配送方式
    shopType: '', // 店铺类型
    shopId: '',
    platform: '',
})
// 表单配置项
const columns: SearchProps[] = [
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
        prop: 'orderNo',
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
        label: '收货人',
        prop: 'receiverName',
        labelWidth: 85,
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
        label: '配送方式',
        prop: 'distributionMode',
        valueType: 'select',
        options: [
            {
                value: '',
                label: '全部',
            },
            {
                value: 'EXPRESS',
                label: '快递配送',
            },
            {
                value: 'INTRA_CITY_DISTRIBUTION',
                label: '同城配送',
            },
            {
                value: 'SHOP_STORE',
                label: '到店自提',
            },
            {
                value: 'VIRTUAL',
                label: '无需物流',
            },
        ],
        fieldProps: {
            placeholder: '请选择配送方式',
        },
    },
    {
        label: '店铺',
        prop: 'shopId',
        fieldProps: {},
        renderField(modelValue) {
            return h(ShopSelect, {
                modelValue,
                onChange: (e) => {
                    if (e) {
                        searchFromData.shopId = e
                    } else {
                        searchFromData.shopId = ''
                    }
                },
            })
        },
    },
    {
        label: '所属渠道',
        prop: 'platform',
        valueType: 'select',
        options: platformList,
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
                prop: 'orderNo',
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
                label: '收货人',
                prop: 'receiverName',
                labelWidth: 85,
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
            {
                label: '配送方式',
                prop: 'distributionMode',
                valueType: 'select',
                options: [
                    {
                        value: '',
                        label: '全部',
                    },
                    {
                        value: 'EXPRESS',
                        label: '快递配送',
                    },
                    {
                        value: 'INTRA_CITY_DISTRIBUTION',
                        label: '同城配送',
                    },
                    {
                        value: 'SHOP_STORE',
                        label: '到店自提',
                    },
                    {
                        value: 'VIRTUAL',
                        label: '无需物流',
                    },
                ],
                fieldProps: {
                    placeholder: '请选择配送方式',
                },
            },
            {
                label: '店铺',
                prop: 'shopId',
                fieldProps: {},
                renderField(modelValue: any) {
                    return h(ShopSelect, {
                        modelValue,
                        onChange: (e) => {
                            if (e) {
                                searchFromData.shopId = e
                            } else {
                                searchFromData.shopId = ''
                            }
                        },
                    })
                },
            },
        ]
    } else {
        return columns
    }
})

const HandleSearch = () => {
    const { orderNo, buyerNickname, productName, receiverName, distributionMode, shopType, shopId, platform } = searchFromData
    const params = {
        orderNo,
        buyerNickname,
        productName,
        receiverName,
        startTime: '',
        endTime: '',
        distributionMode,
        shopType,
        shopId,
        platform,
    }
    if (Array.isArray(searchFromData.clinchTime)) {
        params.startTime = searchFromData.clinchTime[0]
        params.endTime = searchFromData.clinchTime[1]
    }
    $emit('search-data', params)
}

const handleReset = () => {
    ;(Object.keys(searchFromData) as (keyof SearchFromData)[]).forEach((key) => (searchFromData[key] = ''))
    HandleSearch()
}
const exportData = () => {
    $emit('exportData', searchFromData)
}
</script>

<template>
    <!-- 搜索部分s -->
    <div class="form">
        <SchemaForm v-model="searchFromData" :columns="newColumns" label-width="90" @searchHandle="HandleSearch" @handleReset="handleReset">
            <template #otherOperations>
                <el-button round type="primary" @click="exportData">导出</el-button>
            </template>
        </SchemaForm>
    </div>
    <!-- 搜索部分e -->
</template>

<style lang="scss" scoped></style>
