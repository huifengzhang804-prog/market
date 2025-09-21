<template>
    <SchemaForm v-model="searchType" :columns="columns" @searchHandle="search" @handleReset="handleReset" />
</template>

<script lang="ts" setup>
import SchemaForm from '@/components/SchemaForm.vue'

export type SearchType = Record<'supplierGoodsName' | 'supplierProductId' | 'productType', string>

const productTypeList = reactive([
    {
        value: '',
        label: '全部',
    },
    {
        value: 'REAL_PRODUCT',
        label: '实物商品',
    },
    {
        value: 'VIRTUAL_PRODUCT',
        label: '虚拟商品',
    },
])
const searchType = reactive<SearchType>({
    supplierGoodsName: '',
    supplierProductId: '',
    productType: '',
})

const columns = [
    {
        label: '商品名称',
        prop: 'supplierGoodsName',
        valueType: 'copy',
        fieldProps: {
            placeholder: '请输入商品名称',
        },
    },
    {
        label: 'SPUID',
        prop: 'supplierProductId',
        valueType: 'copy',
        fieldProps: {
            placeholder: '请输入SPUID',
        },
    },

    {
        label: '商品类型',
        prop: 'productType',
        valueType: 'select',
        options: productTypeList,
        fieldProps: {
            placeholder: '请选择商品类型',
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
