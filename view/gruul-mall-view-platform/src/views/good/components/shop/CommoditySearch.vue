<template>
    <div>
        <SchemaForm v-model="searchForm" :columns="columns" @searchHandle="searchHandle" @handleReset="handleReset" />
    </div>
</template>
<script lang="ts" setup>
import SchemaForm from '@/components/SchemaForm.vue'
import { doGetCategory, doGetShopList } from '@/apis/shops'
import type { searchFormType } from '../../types'

const $props = defineProps({
    tabsActive: { type: String, default: ' ' },
})
const $emit = defineEmits(['getSearchParams', 'showChange'])
const searchForm = reactive<searchFormType>({
    platformCategoryId: '',
    shopId: '',
    productType: '',
    name: '',
    createBeginTime: '',
    createEndTime: '',
    date: '',
    status: '',
    sellType: '',
})

const shopSearchList = ref<any[]>([])
const categoryList = ref([])
const shopSearchRemote = async (query: string) => {
    if (query) {
        const { data } = (await doGetShopList({ name: query, current: 1, size: 999 })) as any
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

watch(
    () => $props.tabsActive,
    (val) => {
        if (val !== ' ') {
            searchForm.status = ''
        }
    },
)
const pageConfig = reactive({
    pageSize: 20,
    pageNum: 1,
    total: 0,
})
getCategory()

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
            emptyValues: [null, undefined, ''],
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
        prop: 'platformCategoryId',
        valueType: 'cascader',
        options: categoryList,
        fieldProps: {
            placeholder: '请选择平台类目',
            props: {
                value: 'id',
                label: 'name',
                expandTrigger: 'hover',
            },
            showAllLevels: false,
            onChange: (e: any[]) => {
                if (e.length > 1) {
                    searchForm.platformCategoryId = e[e.length - 1]
                } else {
                    searchForm.platformCategoryId = ''
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
                label: '全部商品',
            },
            {
                value: 'VIRTUAL_PRODUCT',
                label: '虚拟商品',
            },
            {
                value: 'REAL_PRODUCT',
                label: '实物商品',
            },
        ],
        fieldProps: {
            placeholder: '请输入商品类型',
        },
    },
    {
        label: '商品来源',
        prop: 'sellType',
        valueType: 'select',
        options: [
            {
                value: '',
                label: '全部',
            },
            {
                value: 'OWN',
                label: '自有商品',
            },
            {
                value: 'PURCHASE',
                label: '采购商品',
            },
            {
                value: 'CONSIGNMENT',
                label: '代销商品',
            },
        ],
        fieldProps: {
            placeholder: '请选择商品来源',
        },
    },
]

const searchHandle = () => {
    if (searchForm?.date?.length > 0) {
        searchForm.createBeginTime = searchForm.date[0]
        searchForm.createEndTime = searchForm.date[1]
    } else {
        searchForm.createBeginTime = void 0
        searchForm.createEndTime = void 0
    }

    $emit('getSearchParams', searchForm)
}

const handleReset = () => {
    Object.keys(searchForm).forEach((key) => {
        searchForm[key as keyof searchFormType] = ''
    })
    searchHandle()
}
/**
 * 获取类目列表
 * @param {number} current
 * @param {number} size
 */
async function getCategory() {
    const { data } = (await doGetCategory({
        current: pageConfig.pageNum,
        size: 1000,
    })) as any
    pageConfig.total = data.total
    initList(data.records, 'secondCategoryVos')
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

// 重置搜索参数
const refreshSearchForm = () => {
    let key: keyof searchFormType
    for (key in searchForm) {
        searchForm[key] = ''
    }
}

const goodsType = ref<'店铺商品' | '供应商商品'>('店铺商品')

// 切换店铺商品 | 供应商商品
const changeGoods = (type: '店铺商品' | '供应商商品') => {
    goodsType.value = type
    // 刷新
    refreshSearchForm()
}
defineExpose({
    changeGoods,
})
</script>
