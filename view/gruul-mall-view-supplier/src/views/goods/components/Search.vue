<template>
    <div>
        <SchemaForm v-model="searchType" :columns="columns" @searchHandle="search" @handleReset="handleReset" />
    </div>
</template>

<script lang="ts" setup>
import { doGetCategory } from '@/apis/good'
import { doGetCurrentShopRelationCategory } from '@/apis/store/index'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import { CascaderInstance } from 'element-plus'
import SchemaForm from '@/components/SchemaForm.vue'

export interface SearchType {
    supplierGoodsName: string
    sellType: string
    secondPlatformCategoryId: string
}

const platformCategoryList = ref([])
const categoryList = ref([])

const searchType = reactive<SearchType>({
    supplierGoodsName: '',
    sellType: '',
    secondPlatformCategoryId: '',
})

const platformCascaderProps = {
    expandTrigger: 'hover' as 'click' | 'hover',
    label: 'currentName',
    value: 'currentId',
}
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
])
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
        label: '销售方式',
        prop: 'sellType',
        valueType: 'select',
        options: sellTypeList,
        fieldProps: {
            placeholder: '请选择商品来源',
        },
    },
    {
        prop: 'secondPlatformCategoryId',
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
    list?.forEach((category: any) => {
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

function search() {
    $emit('onSearchParams', toRaw(searchType))
}
const handleReset = () => {
    ;(Object.keys(searchType) as (keyof SearchType)[]).forEach((key) => (searchType[key] = ''))
    search()
}
</script>
