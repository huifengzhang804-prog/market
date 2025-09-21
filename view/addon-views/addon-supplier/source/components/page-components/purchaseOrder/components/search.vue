<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { cloneDeep } from 'lodash-es'
import { getSupplierSearchList } from '../../../../apis'
import SchemaForms from '@/components/SchemaForm.vue'

const supplierList = ref<any[]>([])
const emitFn = defineEmits(['search', 'supplierExport'])
const searchType = reactive({
  supplierId: '',
  no: '',
  date: '',
})
const columns = [
  {
    label: '订单号',
    prop: 'no',
    valueType: 'copy',
    fieldProps: {
      placeholder: '请输入订单号',
    },
  },
  {
    label: '供应商',
    prop: 'supplierId',
    valueType: 'select',
    options: supplierList,
    fieldProps: {
      placeholder: '请输入店铺名称',
      props: {
        expandTrigger: 'hover' as 'click' | 'hover',
      },
      filterable: true,
      remote: true,
      reserveKeyword: true,
      remoteMethod: (val: string) => {
        fetchSupplierList(val)
      },
    },
  },
  {
    label: '采购员手机',
    labelWidth: '85px',
    prop: 'purchasePhone',
    valueType: 'copy',
    fieldProps: {
      placeholder: '请输入采购员手机',
    },
  },
  {
    label: '下单时间',
    prop: 'date',
    valueType: 'date-picker',
    fieldProps: {
      type: 'daterange',
      startPlaceholder: '开始时间',
      endPlaceholder: '结束时间',
      format: 'YYYY/MM/DD',
      valueFormat: 'YYYY-MM-DD',
    },
  },
]
const handleSearch = () => {
  const cloneSearchType: any = cloneDeep(searchType)
  cloneSearchType.startTime = Array.isArray(cloneSearchType.date) ? cloneSearchType.date?.[0] : void 0
  cloneSearchType.endTime = Array.isArray(cloneSearchType.date) ? cloneSearchType.date?.[1] : void 0
  delete cloneSearchType.date
  emitFn('search', cloneSearchType)
}

const fetchSupplierList = async (supplierName = '') => {
  const result = await getSupplierSearchList({ supplierName })
  if (result.data && Array.isArray(result.data) && result.data.length) {
    supplierList.value = result.data.map((v: { name: string; id: number | string }) => {
      return {
        label: v.name,
        value: v.id,
      }
    })
  }
}
const handleReset = () => {
  Object.keys(searchType).forEach((key) => ((searchType as any)[key] = ''))
  handleSearch()
}
const supplierExport = () => {
  emitFn('supplierExport', searchType)
}
</script>

<template>
  <SchemaForms v-model="searchType" :columns="columns" :show-number="3" @searchHandle="handleSearch" @handleReset="handleReset">
    <template #otherOperations>
      <el-button class="from_btn" round type="primary" @click="supplierExport">导出</el-button>
    </template>
  </SchemaForms>
</template>
