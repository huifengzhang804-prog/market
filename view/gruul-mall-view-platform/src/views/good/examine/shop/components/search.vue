<template>
    <div class="search">
        <SchemaForm v-model="searchType" :columns="columns" @searchHandle="search" @handleReset="handleReset" />
    </div>
</template>

<script lang="ts" setup>
import { doGetCategory, doGetShopList } from '@/apis/shops'
import SchemaForm from '@/components/SchemaForm.vue'
import type { searchAuditingFormType } from '../../../types'

const categoryList = ref([])
const $emit = defineEmits(['onSearchParams', 'changeShow'])
const shopSearchList = ref<any[]>([])

const shopSearchRemote = async (query: string) => {
    if (query) {
        const { data } = await doGetShopList({ name: query, current: 1, size: 999 })
        shopSearchList.value =
            data?.records?.map((v: any) => {
                return {
                    ...v,
                    label: v.name,
                    value: v.id,
                }
            }) || []
    } else {
        shopSearchList.value = []
    }
}
const searchType = reactive<searchAuditingFormType>({
    shopId: '',
    name: '',
    platformCategoryId: '',
    productType: '',
    cascaderModel: '',
})

const columns = [
    {
        label: '店铺名称',
        labelWidth: 75,
        prop: 'shopId',
        valueType: 'select',
        options: shopSearchList,
        fieldProps: {
            placeholder: '请输入店铺名称',
            props: {
                value: 'id',
                label: 'name',
                expandTrigger: 'hover',
            },
            filterable: true,
            remote: true,
            reserveKeyword: true,
            remoteMethod: (val: string) => {
                shopSearchRemote(val)
            },
        },
    },
    {
        label: '商品名称',
        prop: 'name',
        valueType: 'copy',
        fieldProps: {
            placeholder: '请输入商品名称',
        },
    },
    {
        label: '平台类目',
        prop: 'cascaderModel',
        valueType: 'cascader',
        options: categoryList,
        fieldProps: {
            placeholder: '请选择平台类目',
            props: {
                expandTrigger: 'hover' as 'click' | 'hover',
                label: 'name',
                value: 'id',
            },
            showAllLevels: false,
            onChange: (e: any[]) => {
                if (e.length > 1) {
                    handleChangeCascader(e)
                    searchType.cascaderModel = ''
                }
            },
        },
    },
    {
        label: '商品类型',
        prop: 'productType',
        valueType: 'select',
        options: [
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
        ],
        fieldProps: {
            placeholder: '请输入商品类型',
        },
    },
]

const handleChangeCascader = (e: any) => {
    searchType.platformCategoryId = e?.pop()
}
async function init() {
    const { data } = await doGetCategory({
        current: 1,
        size: 1000,
    })
    initList(data.records, 'secondCategoryVos')
    data.records.unshift({ categoryId: '0', id: '0', name: '全部类目', parentId: '0', sort: 1 })
    categoryList.value = data.records
}
function initList(list: any[], str: string) {
    list.forEach((item) => {
        if (item[str]) {
            item.children = item[str]
            delete item[str]
            if (item.children.length) {
                initList(item.children, 'categoryThirdlyVos')
            }
        }
    })
}

function search() {
    $emit('onSearchParams', toRaw(searchType))
}
const handleReset = () => {
    // @ts-ignore
    Object.keys(searchType).forEach((key) => (searchType[key] = ''))
    search()
}
init()
</script>

<style lang="scss" scoped></style>
