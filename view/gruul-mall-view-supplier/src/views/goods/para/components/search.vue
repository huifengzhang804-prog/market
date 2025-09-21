<template>
    <SchemaForm v-model="searchType" :columns="columns" @searchHandle="search" @handleReset="handleReset" />
</template>

<script lang="ts" setup>
import { doGetCategory } from '@/apis/good'
import SchemaForm from '@/components/SchemaForm.vue'

export type SearchType = Record<'createBeginTime' | 'createEndTime' | 'name' | 'categoryId' | 'status', string>

const $props = defineProps({
    // 搜索表单绑定值
    titleStatus: {
        type: String,
        default() {
            return ''
        },
    },
})

const searchType = reactive({
    name: '',
})
const columns = computed(() => {
    if ($props.titleStatus === 'ATTRIBUTE') {
        return [
            {
                label: '属性名称',
                prop: 'name',
                valueType: 'copy',
                fieldProps: {
                    placeholder: '请输入名称',
                },
            },
        ]
    } else {
        return [
            {
                label: '参数名称',
                prop: 'name',
                valueType: 'copy',
                fieldProps: {
                    placeholder: '请输入名称',
                },
            },
        ]
    }
})

const categoryList = ref([])
const $emit = defineEmits(['onSearchParams'])

onMounted(() => {
    init()
})

async function init() {
    const { data, code } = await doGetCategory({ size: 1000 })
    categoryList.value = data.records
}
function changeItem(val: SearchType) {
    if (val) {
        ;(Object.keys(val) as (keyof SearchType)[]).forEach((key) => (searchType[key as keyof typeof searchType] = val[key]))
    }
}
function search() {
    $emit('onSearchParams', toRaw(searchType))
}
const handleReset = () => {
    searchType.name = ''
    search()
}
</script>
