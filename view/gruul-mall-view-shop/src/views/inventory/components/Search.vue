<template>
    <SchemaForm v-model="searchType" :columns="columns" @searchHandle="search" @handleReset="handleReset" />
</template>

<script lang="ts" setup>
import { doGetCategory } from '@/apis/good'
import SchemaForm from '@/components/SchemaForm.vue'

export type SearchType = Record<
    'productName' | 'productId' | 'shopCategoryId' | 'productType' | 'sellType' | 'shopCategoryIdList',
    string | undefined
>

const sellTypeList = reactive([
    {
        value: '',
        label: '全部',
    },
    {
        value: 'PURCHASE',
        label: '采购商品',
    },
    {
        value: 'OWN',
        label: '自有商品',
    },
])
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
    productName: '',
    productId: '',
    shopCategoryId: '',
    productType: '',
    sellType: '',
    shopCategoryIdList: '',
})
const categoryList = ref([])
const shopCascaderProps = {
    expandTrigger: 'hover' as 'click' | 'hover',
    label: 'name',
    value: 'id',
}
const columns = [
    {
        label: '商品名称',
        prop: 'productName',
        valueType: 'copy',
        fieldProps: {
            placeholder: '请输入商品名称',
        },
    },
    {
        label: 'SPUID',
        prop: 'productId',
        valueType: 'copy',
        fieldProps: {
            placeholder: '请输入SPUID',
            type: 'number',
            onChange: () => {
                searchType.productId = searchType.productId?.replace(/[^\d]/g, '') || ''
            },
        },
    },
    {
        label: '店铺类目',
        prop: 'shopCategoryIdList',
        valueType: 'cascader',
        options: categoryList,
        fieldProps: {
            placeholder: '请选择店铺类目',
            props: shopCascaderProps,
            showAllLevels: false,
            onChange: (e: any[]) => {
                if (e.length > 1) {
                    searchType.shopCategoryId = e?.[e.length - 1] || ''
                }
            },
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

    {
        label: '商品来源',
        prop: 'sellType',
        valueType: 'select',
        options: sellTypeList,
        fieldProps: {
            placeholder: '请选择商品来源',
        },
    },
]

const $emit = defineEmits(['onSearchParams'])

onMounted(() => {
    init()
})

async function init() {
    const { data, code } = await doGetCategory({ size: 1000 })
    categoryList.value = data.records
}

function search() {
    $emit('onSearchParams', toRaw(searchType))
}
const handleReset = () => {
    ;(Object.keys(searchType) as (keyof SearchType)[]).forEach((key) => (searchType[key] = ''))
    search()
}
</script>
