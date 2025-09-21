<script lang="ts" setup>
import { cloneDeep } from 'lodash-es'
import { reactive } from 'vue'
import SchemaForm from '@/components/SchemaForm.vue'

const searchType = reactive({
  anchorNickname: '', // 主播昵称
  id: '', // 直播ID
  phone: '', // 手机号
})
const columns = [
  {
    label: '主播昵称',
    prop: 'anchorNickname',
    valueType: 'copy',
    fieldProps: {
      placeholder: '请输入主播昵称',
    },
  },
  {
    label: '直播ID',
    prop: 'id',
    valueType: 'copy',
    fieldProps: {
      placeholder: '请输入直播ID',
    },
  },
  {
    label: '手机号',
    prop: 'phone',
    valueType: 'copy',
    fieldProps: {
      placeholder: '请输入手机号',
    },
  },
]
const emitFns = defineEmits(['onSearchParams'])

const search = () => {
  const cloneSearchParams = cloneDeep(searchType)
  emitFns('onSearchParams', cloneSearchParams)
}
const handleReset = () => {
  Object.keys(searchType).forEach((key) => (searchType[key] = ''))
  search()
}
</script>

<template>
  <SchemaForm v-model="searchType" :columns="columns" @searchHandle="search" @handleReset="handleReset"> </SchemaForm>
</template>
