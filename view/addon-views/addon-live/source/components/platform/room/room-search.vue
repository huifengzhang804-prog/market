<script lang="ts" setup>
import { cloneDeep } from 'lodash-es'
import { reactive } from 'vue'
import SchemaForm from '@/components/SchemaForm.vue'

const searchType = reactive({
  liveTitle: '', // 直播主题
  anchorNickname: '', // 主播昵称
  createTime: '', // 直播时间范围
  liveId: '', // 直播ID
  phone: '', // 手机号
  shopName: '', //店铺名称
})
const columns = [
  {
    label: '直播主题',
    prop: 'liveTitle',
    valueType: 'copy',
    fieldProps: {
      placeholder: '请输入直播主题',
    },
  },
  {
    label: '主播昵称',
    prop: 'anchorNickname',
    valueType: 'copy',
    fieldProps: {
      placeholder: '请输入主播昵称',
    },
  },
  {
    label: '直播时间',
    prop: 'createTime',
    valueType: 'date-picker',
    fieldProps: {
      type: 'daterange',
      startPlaceholder: '开始时间',
      endPlaceholder: '结束时间',
      valueFormat: 'YYYY-MM-DD',
    },
  },
  {
    label: '直播ID',
    prop: 'liveId',
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
  {
    label: '店铺名称',
    prop: 'shopName',
    valueType: 'copy',
    fieldProps: {
      placeholder: '请输入店铺名称',
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
  <div>
    <SchemaForm v-model="searchType" :columns="columns" @searchHandle="search" @handleReset="handleReset"> </SchemaForm>
  </div>
</template>
