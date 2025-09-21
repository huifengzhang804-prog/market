<script lang="ts" setup>
import SchemaForm from '@/components/SchemaForm.vue'
import { doGetMaterialCategoryList, doPostMaterialSuggest } from '@/apis/material'
import { CascaderProps, ElMessage } from 'element-plus'
import { cloneDeep, isArray } from 'lodash-es'
import { Category } from '@/apis/material/types'
const props = defineProps({
    // eslint-disable-next-line vue/require-default-prop
    classificationId: {
        type: String || undefined,
        defalut: () => '',
    },
    showCategory: {
        type: Boolean,
        default: false,
    },
    flex: {
        type: Boolean,
        default: true,
    },
})
const searchType = reactive({
    categoryId: ' ',
    format: '',
    name: '',
    imgSize: '',
})
// searchType.format = suggestList.value?.format || ''
const $emit = defineEmits(['search', 'changeShow', 'reset', 'categoryBolFn'])

function handleSearch() {
    if (!props.showCategory) searchType.categoryId = props.classificationId || ''
    else if (isArray(searchType.categoryId)) searchType.categoryId = searchType.categoryId?.pop()
    $emit('search', toRaw(searchType))
}
const handleReset = () => {
    // 使用类型断言确保TypeScript理解key是searchType的有效键
    Object.keys(searchType).forEach((key) => {
        ;(searchType as Record<string, string>)[key] = ''
    })
    searchType.categoryId = ' '
    handleSearch()
    $emit('reset')
}

const handleResetSearchType = () => {
    searchType.format = ''
    searchType.imgSize = ''
    searchType.name = ''
}
const category = ref<Category[]>([])
const format = ref<{ label: string; value: string }[]>([])
const size = ref<{ label: string; value: string }[]>([])
const doPostMaterialSuggestInt = async (boolVal = true) => {
    const allClassifications = [{ hasChildren: false, name: '全部', id: ' ', parentId: ' ' }]
    let itemVal: any = []
    let iteVal: any = []
    let itVal: any = []
    const result = await doGetMaterialCategoryList('')
    if (result.code === 200) category.value = [...allClassifications, ...result.data]
    else ElMessage.error(result.msg || '获取素材推荐检索建议分类失败')
    if (boolVal) $emit('categoryBolFn')
    cloneDeep(category.value).filter((item: any) => {
        if (item.selectHistory) {
            itemVal = item
            return itemVal
        } else if (item.hasChildren) {
            item.children.filter((ite: { selectHistory: any; hasChildren: any; children: any[] }) => {
                if (ite.selectHistory) {
                    iteVal = ite
                    return iteVal
                } else if (ite.hasChildren) {
                    ite.children.filter((it: { selectHistory: any }) => {
                        if (it.selectHistory) {
                            itVal = it
                            return itVal
                        }
                        return false
                    })
                }
                return false
            })
        }
        return false
    })
    if (itVal && itVal.id) {
        searchType.categoryId = searchType.categoryId || itVal.id
    } else if (iteVal && iteVal.id) searchType.categoryId = searchType.categoryId || iteVal.id
    else if (itemVal && itemVal.id) searchType.categoryId = searchType.categoryId || itemVal.id
    else searchType.categoryId = ' '
}
const materialSuggest = async (categoryId?: string) => {
    console.log('props.classificationId', categoryId)
    const { code, data, msg } = await doPostMaterialSuggest(categoryId)
    if (code === 200) {
        format.value =
            data.format?.map((item: any) => {
                return {
                    label: item,
                    value: item,
                }
            }) || []
        size.value =
            data.size?.map((item: any) => {
                return {
                    label: item,
                    value: item,
                }
            }) || []
    } else ElMessage.error(msg || '获取素材推荐检索建议失败')
}
if (props.showCategory) {
    doPostMaterialSuggestInt()
}

watch(
    () => props.classificationId,
    () => {
        materialSuggest(props.classificationId)
    },
    { deep: true, immediate: true },
)
watch(
    () => searchType.categoryId,
    (value) => {
        materialSuggest(value)
    },
    { deep: true, immediate: true },
)
const defaultProps: CascaderProps = {
    expandTrigger: 'hover',
    checkStrictly: true,
    emitPath: false,
    children: 'children',
    label: 'name',
    value: 'id',
}

const columns = computed(() => {
    let items = [
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
            labelWidth: 40,
            prop: 'imgSize',
            valueType: 'select',
            options: size,
            fieldProps: {
                placeholder: '请选择',
            },
        },
        {
            label: '格式',
            labelWidth: 40,
            prop: 'format',
            valueType: 'select',
            options: format,
            fieldProps: {
                placeholder: '请选择',
            },
        },
    ]
    if (props.showCategory) {
        items.unshift({
            label: '分类',
            labelWidth: 40,
            prop: 'categoryId',
            valueType: 'cascader',
            options: category as any,
            fieldProps: {
                placeholder: '请选择素材分类',
                props: defaultProps,
                showAllLevels: false,
            } as any,
        })
    }
    return items
})
defineExpose({ handleReset, handleResetSearchType, searchType, doPostMaterialSuggestInt, handleSearch })
</script>
<template>
    <div class="search">
        <SchemaForm
            v-model="searchType"
            :columns="columns"
            :searchCol="flex ? { xs: 1, sm: 2, md: 3, lg: 3, xl: 4 } : { xs: 4, sm: 4, md: 4, lg: 4, xl: 4 }"
            @searchHandle="handleSearch"
            @handleReset="handleReset"
        />
    </div>
</template>
