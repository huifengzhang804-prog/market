<script lang="ts" setup>
import useSearch from '../hooks/useSearch'
import SchemaForms from '@/components/SchemaForm.vue'
import { cloneDeep } from 'lodash-es'

const emitFn = defineEmits(['search'])
const { searchType, columns } = useSearch()

const handleSearch = () => {
  const cloneSearchType = cloneDeep(searchType)
  cloneSearchType.shopCategoryId = Array.isArray(cloneSearchType.shopCategoryId) ? cloneSearchType.shopCategoryId.pop() : ''
  emitFn('search', cloneSearchType)
}
const handleReset = () => {
  Object.keys(searchType).forEach((key) => (searchType[key] = ''))
  handleSearch()
}
</script>

<template>
  <SchemaForms v-model="searchType" :columns="columns" :show-number="3" @searchHandle="handleSearch" @handleReset="handleReset" />
</template>
