<script setup lang="ts">
import type { PropType } from 'vue'
import { useVModel } from '@vueuse/core'
import type { EvaluateSearchData } from '@/views/order/appraise/types'
import SchemaForm from '@/components/SchemaForm.vue'

const $props = defineProps({
    searchData: {
        type: Object as PropType<EvaluateSearchData>,
        default: () => ({}),
    },
})
const $emit = defineEmits(['update:searchData', 'click', 'changeShow'])
const _searchData = useVModel($props, 'searchData', $emit)
const rateData = [
    { label: '全部', value: 0 },
    { label: '一颗星', value: 1 },
    { label: '二颗星', value: 2 },
    { label: '三颗星', value: 3 },
    { label: '四颗星', value: 4 },
    { label: '五颗星', value: 5 },
]
const columns = [
    {
        label: '商品名称',
        prop: 'name',
        valueType: 'copy',
        fieldProps: {
            placeholder: '请输入商品名称',
        },
    },
    {
        label: '买家昵称',
        prop: 'nickname',
        valueType: 'copy',
        fieldProps: {
            placeholder: '请输入买家昵称',
        },
    },
    {
        label: '评论时间',
        valueType: 'date-picker',
        prop: 'clinchTime',
        fieldProps: {
            type: 'daterange',
            startPlaceholder: '开始日期',
            endPlaceholder: '结束日期',
            format: 'YYYY/MM/DD',
            valueFormat: 'YYYY-MM-DD',
        },
    },
    {
        label: '评价星级',
        prop: 'rate',
        valueType: 'select',
        options: rateData,
        fieldProps: {
            placeholder: '请选择评价星级',
        },
    },
]

const handleReset = () => {
    ;(Object.keys(_searchData.value) as (keyof EvaluateSearchData)[]).forEach((key) => {
        if (key === 'rate') {
            _searchData.value[key] = 0
        } else {
            _searchData.value[key] = ''
        }
    })
    $emit('click')
}

const handleCollapse = (e: boolean) => {
    $emit('changeShow', e)
}
</script>

<template>
    <SchemaForm v-model="_searchData" :columns="columns" @searchHandle="$emit('click')" @handleReset="handleReset" @collapse="handleCollapse" />
</template>

<style scoped lang="scss"></style>
