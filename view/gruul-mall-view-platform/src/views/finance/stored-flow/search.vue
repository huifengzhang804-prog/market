<template>
    <div>
        <SchemaForm v-model="searchForm" :columns="columns" @searchHandle="handleSearch" @handleReset="handleReset" @collapse="handleCollapse">
            <template #otherOperations>
                <slot></slot>
            </template>
        </SchemaForm>
    </div>
</template>

<script lang="ts" setup>
import { cloneDeep } from 'lodash-es'
import { columns } from './config'
import SchemaForm from '@/components/SchemaForm.vue'

type SearchFormType = typeof searchForm
const searchForm = reactive({
    orderNo: '',
    userNickName: '',
    userPhone: '',
    operatorType: '',
    no: '',
    createTime: '',
})
const $emit = defineEmits(['changeShow', 'search'])

const handleSearch = () => {
    const cloneSearchForm: any = cloneDeep(searchForm)
    if (Array.isArray(searchForm.createTime)) {
        cloneSearchForm.operatorStartTime = searchForm.createTime?.[0]
        cloneSearchForm.operatorEndTime = searchForm.createTime?.[1]
    }
    $emit('search', cloneSearchForm)
}
const handleReset = () => {
    ;(Object.keys(searchForm) as (keyof SearchFormType)[]).forEach((key) => (searchForm[key] = ''))
    handleSearch()
}
const handleCollapse = (e: boolean) => {
    $emit('changeShow', e)
}
</script>

<style lang="scss" scoped>
@include b(export-icon) {
    font-size: 28px;
    margin-left: 8px;
}
</style>
