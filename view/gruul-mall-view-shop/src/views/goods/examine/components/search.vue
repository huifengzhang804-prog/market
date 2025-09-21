<template>
    <div>
        <SchemaForm v-model="searchType" :columns="columns" @searchHandle="search" @handleReset="handleReset" />
    </div>
</template>

<script lang="ts" setup>
import { doGetCategory } from '@/apis/good'
import SchemaForm from '@/components/SchemaForm.vue'

type searchFormType = {
    name: string
    categoryId: string
    productType: string
}

const categoryList = ref([])
const shopCascaderProps = {
    expandTrigger: 'hover' as 'click' | 'hover',
    label: 'name',
    value: 'id',
}
const searchType = reactive<searchFormType>({
    name: '',
    categoryId: '',
    productType: '',
})
const typeList = [
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
        label: '店铺类目',
        valueType: 'cascader',
        options: categoryList,
        fieldProps: {
            placeholder: '请选择店铺类目',
            props: shopCascaderProps,
            showAllLevels: false,
            onChange: (e: any[]) => {
                if (e.length > 1) {
                    searchType.categoryId = e?.[e.length - 1] || ''
                }
            },
        },
    },
    {
        label: '商品类型',
        prop: 'productType',
        valueType: 'select',
        options: typeList,
        fieldProps: {
            placeholder: '请输入商品类型',
        },
    },
]
const $emit = defineEmits(['onSearchParams'])

async function init() {
    const { data, code } = await doGetCategory({ size: 1000 })
    categoryList.value = data.records
}

function search() {
    $emit('onSearchParams', toRaw(searchType))
}
const handleReset = () => {
    ;(Object.keys(searchType) as (keyof searchFormType)[]).forEach((key) => (searchType[key] = ''))
    search()
}
init()
</script>

<style lang="scss" scoped></style>
