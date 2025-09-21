<script lang="ts" setup>
import { doPostMaterialSuggest } from '@/apis/material'
import { ElMessage } from 'element-plus'
import SchemaForm from '@/components/SchemaForm.vue'
import { SearchType as AllSearchType } from '../hooks/useMaterialListHooks'

type SearchType = Omit<AllSearchType, 'categoryId'>
withDefaults(
    defineProps<{
        flex?: boolean
    }>(),
    {
        flex: true,
    },
)

const $emit = defineEmits(['onSearchParams'])
const searchType = reactive<SearchType>({
    format: '',
    name: '',
    imgSize: '',
})

const handleResetSearchType = () => {
    searchType.format = ''
    searchType.imgSize = ''
    searchType.name = ''
}
const suggestList = reactive<{ format: string[]; size: string[] }>({
    format: [],
    size: [],
})

const materialSuggest = async () => {
    const { code, data, msg } = await doPostMaterialSuggest()
    if (code === 200) {
        suggestList.format =
            data.format?.map((item: any) => {
                return {
                    label: item,
                    value: item,
                }
            }) || []
        suggestList.size =
            data.size?.map((item: any) => {
                return {
                    label: item,
                    value: item,
                }
            }) || []
    } else ElMessage.error(msg || '获取素材推荐检索建议失败')
}
materialSuggest()

const columns = [
    {
        label: '素材名称',
        prop: 'name',
        valueType: 'copy',
        fieldProps: {
            placeholder: '请输入名称',
        },
    },
    {
        label: '尺寸',
        prop: 'imgSize',
        valueType: 'select',
        options: toRef(suggestList, 'size'),
        fieldProps: {
            placeholder: '请选择尺寸',
        },
    },
    {
        label: '格式',
        prop: 'format',
        valueType: 'select',
        options: toRef(suggestList, 'format'),
        fieldProps: {
            placeholder: '请选择格式',
        },
    },
]

function search() {
    $emit('onSearchParams', toRaw(searchType))
}

const handleReset = () => {
    ;(Object.keys(searchType) as (keyof SearchType)[]).forEach((key) => (searchType[key] = ''))
    search()
}
defineExpose({ handleReset, handleResetSearchType, searchType })
</script>
<template>
    <SchemaForm
        v-model="searchType"
        :columns="columns"
        :searchCol="flex ? { xs: 1, sm: 2, md: 3, lg: 3, xl: 4 } : { xs: 4, sm: 4, md: 4, lg: 4, xl: 4 }"
        @searchHandle="search"
        @handleReset="handleReset"
    />
</template>
