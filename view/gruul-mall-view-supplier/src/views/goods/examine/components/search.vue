<template>
    <div>
        <SchemaForm v-model="searchType" :columns="columns" @searchHandle="search" @handleReset="handleReset" />
    </div>
</template>

<script lang="ts" setup>
import { doGetPlatformCategory } from '@/apis/good'
import SchemaForm from '@/components/SchemaForm.vue'

type searchFormType = {
    name: string
    platformCategoryId: string
    sellType: string
}

const categoryList = ref<any[]>([])
const shopCascaderProps = {
    expandTrigger: 'hover' as 'click' | 'hover',
    label: 'name',
    value: 'id',
}
const searchType = reactive<searchFormType>({
    name: '',
    platformCategoryId: '',
    sellType: '',
})
const typeList = [
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
        prop: 'platformCategoryId',
        label: '平台类目',
        valueType: 'cascader',
        options: categoryList,
        fieldProps: {
            placeholder: '请选择平台类目',
            props: shopCascaderProps,
            showAllLevels: false,
            onChange: (e: any[]) => {
                if (e.length > 1) {
                    searchType.platformCategoryId = e?.[e.length - 1] || ''
                } else {
                    searchType.platformCategoryId = ''
                }
            },
        },
    },
    {
        label: '销售方式',
        prop: 'sellType',
        valueType: 'select',
        options: typeList,
        fieldProps: {
            placeholder: '请输入商品类型',
        },
    },
]
const $emit = defineEmits(['onSearchParams'])

async function init() {
    const { data, code } = await doGetPlatformCategory({ size: 1000 })
    categoryList.value = checkCategoryEnable(1, data)
}
function checkCategoryEnable(currentLevel: number, records: any[]) {
    const isLastLevel = currentLevel === 3
    if (currentLevel > 1) {
        return records
    }
    for (let index = 0; index < records.length; ) {
        const record = records[index]
        if (isLastLevel) {
            record.disabled = false
            index++
            continue
        }
        const children = (record.children || record.secondCategoryVos || record.categoryThirdlyVos) as any[]
        delete record.secondCategoryVos
        delete record.categoryThirdlyVos
        const disable = !children || children.length === 0
        record.disabled = disable
        if (disable) {
            records.splice(index, 1)
            continue
        }
        checkCategoryEnable(currentLevel + 1, children)
        if (children.length === 0) {
            records.splice(index, 1)
            continue
        }
        record.children = children
        index++
    }

    return records
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
