<script lang="ts" setup>
import SchemaForm from '@/components/SchemaForm.vue'
import { type PropType } from 'vue'
import { useVModel } from '@vueuse/core'

const props = defineProps({
  searchForm: {
    type: Object as PropType<{
      orderNo: string
      productName: string
      shopName: string
      buyerNickname: string
      orderCreateTime: string
      orderEndTime: string
      clinchTime: string[] | '' | null
    }>,
    default: () => ({
      orderNo: '',
      productName: '',
      shopName: '',
      buyerNickname: '',
      clinchTime: '',
      orderCreateTime: '',
      orderEndTime: '',
    }),
  },
})
const $emit = defineEmits(['getSearchParams', 'update:searchForm', 'handleExport'])
const searchForm = useVModel(props, 'searchForm', $emit)
const columns = [
  {
    label: '订单号',
    prop: 'orderNo',
    valueType: 'copy',
    fieldProps: {
      placeholder: '请填写订单号',
      maxlength: 21,
    },
  },
  {
    label: '商品名称',
    prop: 'productName',
    valueType: 'copy',
    fieldProps: {
      placeholder: '请填写商品名称',
      maxlength: 20,
    },
  },
  {
    label: '店铺名称',
    prop: 'shopName',
    valueType: 'copy',
    fieldProps: {
      placeholder: '请填写店铺名称',
      maxlength: 20,
    },
  },
  {
    label: '买家昵称',
    prop: 'buyerNickname',
    valueType: 'copy',
    fieldProps: {
      placeholder: '请填写买家昵称',
      maxlength: 20,
    },
  },
  {
    label: '下单时间',
    prop: 'clinchTime',
    valueType: 'date-picker',
    fieldProps: {
      type: 'daterange',
      startPlaceholder: '开始时间',
      endPlaceholder: '结束时间',
      format: 'YYYY/MM/DD',
      valueFormat: 'YYYY-MM-DD',
      onChange: (e: any) => {
        if (e && Array.isArray(e)) {
          searchForm.value.orderCreateTime = e[0]
          searchForm.value.orderEndTime = e[1]
        } else {
          searchForm.value.orderCreateTime = ''
          searchForm.value.orderEndTime = ''
        }
      },
    },
  },
]

const searchHandle = () => {
  $emit('getSearchParams', searchForm)
}

const handleReset = () => {
  searchForm.value = {
    orderNo: '',
    productName: '',
    shopName: '',
    buyerNickname: '',
    clinchTime: '',
    orderCreateTime: '',
    orderEndTime: '',
  }
  searchHandle()
}
</script>

<template>
  <div>
    <SchemaForm v-model="searchForm" :columns="columns" @searchHandle="searchHandle" @handleReset="handleReset">
      <template #otherOperations>
        <el-button type="primary" @click="$emit('handleExport')">导出</el-button>
      </template>
    </SchemaForm>
  </div>
</template>
