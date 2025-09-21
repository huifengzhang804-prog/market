<script lang="ts" setup>
import SchemaForms from '@/components/SchemaForm.vue'
import useSearch from '../hooks/useSearch'
import { cloneDeep } from 'lodash-es'

const emitFn = defineEmits(['search'])
const { searchType, columns } = useSearch()

const handleSearch = () => {
  const cloneSearchType = cloneDeep(searchType)
  cloneSearchType.categoryId = Array.isArray(cloneSearchType.categoryId) ? cloneSearchType.categoryId.pop() : ''
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
