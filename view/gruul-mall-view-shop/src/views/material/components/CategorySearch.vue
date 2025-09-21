<script lang="ts" setup>
import SchemaForm from '@/components/SchemaForm.vue'
import { doPostMaterialSuggest } from '@/apis/material'
import { ElMessage } from 'element-plus'
import useMaterialCategoryList from '@/views/material/hooks/useMaterialCategoryList'

const { getMaterialCategoryList } = useMaterialCategoryList()

defineProps({
    flex: {
        type: Boolean,
        default: true,
    },
})
const $emit = defineEmits(['onSearchParams'])

enum FormKey {
    format = 'format',
    name = 'name',
    imgSize = 'imgSize',
}

export type SearchType = Record<keyof typeof FormKey, string>

const searchType = reactive<SearchType>({
    format: '',
    name: '',
    imgSize: '',
})

onMounted(() => {
    getMaterialCategoryList()
    getData()
})

const formatList = ref([])
const sizeList = ref([])
const getData = async () => {
    const { code, data, msg } = await doPostMaterialSuggest()
    if (code === 200) {
        formatList.value =
            data.format?.map((item: any) => {
                return {
                    label: item,
                    value: item,
                }
            }) || []
        sizeList.value =
            data.size?.map((item: any) => {
                return {
                    label: item,
                    value: item,
                }
            }) || []
    } else {
        ElMessage.error(msg || '获取素材推荐检索建议失败')
    }
}

const columns = computed(() => {
    return [
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
            labelWidth: 40,
            valueType: 'select',
            options: [{ label: '全部尺寸', value: '' }, ...sizeList.value],
            fieldProps: {
                placeholder: '请选择尺寸',
            },
        },
        {
            label: '格式',
            prop: 'format',
            labelWidth: 40,
            valueType: 'select',
            options: [{ label: '全部格式', value: '' }, ...formatList.value],
            fieldProps: {
                placeholder: '请选择格式',
            },
        },
    ]
})

function search() {
    $emit('onSearchParams', toRaw(searchType))
}
const handleReset = () => {
    ;(Object.keys(searchType) as (keyof SearchType)[]).forEach((key) => (searchType[key] = ''))
    search()
}
</script>

<template>
    <el-config-provider :empty-values="[undefined, null]">
        <SchemaForm
            v-model="searchType"
            :showNumber="3"
            :columns="columns"
            :searchCol="flex ? { xs: 1, sm: 2, md: 3, lg: 3, xl: 4 } : { xs: 4, sm: 4, md: 4, lg: 4, xl: 4 }"
            @searchHandle="search"
            @handleReset="handleReset"
        >
            <template #extraButtons>
                <slot name="button"></slot>
            </template>
        </SchemaForm>
    </el-config-provider>
</template>
