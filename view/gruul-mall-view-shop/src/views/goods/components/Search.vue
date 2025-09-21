<template>
    <div>
        <SchemaForm v-model="searchType" :columns="columns" @searchHandle="search" @handleReset="handleReset" />
    </div>
</template>

<script lang="ts" setup>
import { doGetCategory } from '@/apis/good'
import { doGetCurrentShopRelationCategory } from '@/apis/mall/settings/index'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import DateUtil from '@/utils/date'
import SchemaForm from '@/components/SchemaForm.vue'

export interface SearchType {
    name: string
    categoryId: string
    productType: string
    sellType: string
    createBeginTime: string
    createEndTime: string
    secondPlatformCategoryId: string
}

const platformCategoryList = ref([])
const categoryList = ref([])
const typeList = reactive([
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
        value: 'CONSIGNMENT',
        label: '代销商品',
    },
    {
        value: 'OWN',
        label: '自有商品',
    },
])
const searchType = reactive<SearchType>({
    name: '',
    categoryId: '',
    productType: '',
    sellType: '',
    createBeginTime: '',
    createEndTime: '',
    secondPlatformCategoryId: '',
})

const shopCascaderProps = {
    expandTrigger: 'hover' as 'click' | 'hover',
    label: 'name',
    value: 'id',
}
const platformCascaderProps = {
    expandTrigger: 'hover' as 'click' | 'hover',
    label: 'currentName',
    value: 'currentId',
}
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
        prop: 'searchType.secondPlatformCategoryId',
        label: '平台类目',
        valueType: 'cascader',
        options: platformCategoryList,
        fieldProps: {
            placeholder: '请选择平台类目',
            props: platformCascaderProps,
            showAllLevels: false,
            onChange: (e: any[]) => {
                if (e?.length > 1) {
                    searchType.secondPlatformCategoryId = e?.[e?.length - 1] || ''
                } else {
                    searchType.secondPlatformCategoryId = ''
                }
            },
        },
    },
    {
        prop: 'searchType.categoryId',
        label: '店铺类目',
        valueType: 'cascader',
        options: categoryList,
        fieldProps: {
            placeholder: '请选择商品类目',
            props: shopCascaderProps,
            showAllLevels: false,
            onChange: (e: any[]) => {
                if (e?.length > 1) {
                    searchType.categoryId = e?.[e?.length - 1] || ''
                } else {
                    searchType.categoryId = ''
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
    {
        label: '商品来源',
        prop: 'sellType',
        valueType: 'select',
        options: sellTypeList,
        fieldProps: {
            placeholder: '请选择商品来源',
        },
    },
    {
        label: '创建时间',
        valueType: 'date-picker',
        fieldProps: {
            type: 'daterange',
            startPlaceholder: '开始日期',
            endPlaceholder: '结束日期',
            format: 'YYYY/MM/DD',
            valueFormat: 'YYYY-MM-DD',
            onChange: (data: Array<Date>) => {
                searchType.createBeginTime = data ? dateConversion(data[0]) : ''
                searchType.createEndTime = data ? dateConversion(data[1]) : ''
            },
        },
    },
]
const $emit = defineEmits(['onSearchParams'])
const shopStore = useShopInfoStore()

onMounted(() => {
    init()
})

async function init() {
    const { data, code } = await doGetCategory({ size: 1000 })
    categoryList.value = data.records
    getPlatformCategory()
}
async function getPlatformCategory() {
    const res = await doGetCurrentShopRelationCategory({ shopId: shopStore.shopInfo.id })
    for (let i = 0; i < res.data?.length; i++) {
        const element = res.data[i]
        element.currentName = element.parentName
        element.currentId = element.parentId
        element.children = []
        element.children.push({
            currentName: element.currentCategoryName,
            currentId: element.currentCategoryId,
        })
    }
    platformCategoryList.value = mergedCategories(res.data) || []
}
function mergedCategories(list: any) {
    const map = new Map()
    list.forEach((category: any) => {
        if (!map.has(category.parentName)) {
            map.set(category.parentName, {
                ...category,
                currentId: category.parentId,
                currentName: category.parentName,
                children: [],
            })
        }
        map.get(category.parentName).children.push({
            currentId: category.currentCategoryId,
            currentName: category.currentCategoryName,
        })
    })
    return Array.from(map.values()) as any
}

function dateConversion(value: Date) {
    const date = new DateUtil().getYMDs(value)
    return date
}
function search() {
    $emit('onSearchParams', toRaw(searchType))
}
const handleReset = () => {
    ;(Object.keys(searchType) as (keyof SearchType)[]).forEach((key) => (searchType[key] = ''))
    searchType.createBeginTime = ''
    searchType.createEndTime = ''
    search()
}
</script>
