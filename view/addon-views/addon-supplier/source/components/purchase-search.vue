<script lang="ts" setup>
import { reactive, ref, watch } from 'vue'
import SchemaForm from '@/components/SchemaForm.vue'
import { cloneDeep } from 'lodash-es'
import { getShopSearchList, getSupplierSearchList } from '../apis'
interface Supplier {
  id: string | number
  name: string
  label: string
  value: string | number
}

const isShow = ref(false)
const supplierList = ref<Supplier[]>([])
const shopList = ref<Supplier[]>([])

const emitFn = defineEmits(['changeShow', 'search', 'supplierExport'])

watch(
  () => isShow.value,
  (val) => emitFn('changeShow', val),
)
interface SearchType {
  supplierId: string
  shopId: string
  no: string
  purchasePhone: string
  date: string | [string, string] // 因为日期选择器可能返回日期数组
}

const searchType = reactive<SearchType>({
  supplierId: '',
  no: '',
  shopId: '',
  date: '',
  purchasePhone: '',
})
// 表单配置
const columns = [
  {
    label: '订单号',
    labelWidth: 60,
    prop: 'no',
    valueType: 'copy',
  },
  {
    label: '供应商',
    labelWidth: 60,
    prop: 'supplierId',
    valueType: 'select',
    options: supplierList,
    fieldProps: {
      props: {
        value: 'id',
        label: 'name',
        expandTrigger: 'hover',
      },
      filterable: true,
      remote: true,
      remoteMethod: (val: string) => {
        fetchSupplierList(val)
      },
    },
  },
  {
    label: '店铺',
    labelWidth: 60,
    prop: 'shopId',
    valueType: 'select',
    options: shopList,
    fieldProps: {
      props: {
        value: 'id',
        label: 'name',
        expandTrigger: 'hover',
      },
      filterable: true,
      remote: true,
      remoteMethod: (val: string) => {
        fetchShopList(val)
      },
    },
  },
  {
    label: '采购员手机',
    labelWidth: 85,
    prop: 'purchasePhone',
    valueType: 'copy',
  },
  {
    label: '下单时间',
    prop: 'date',
    valueType: 'date-picker',
    fieldProps: {
      type: 'daterange',
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

const handleReset = () => {
  ;(Object.keys(searchType) as Array<keyof SearchType>).forEach((key) => {
    searchType[key] = ''
  })
  handleSearch()
}

const fetchSupplierList = async (supplierName = '') => {
  const result = await getSupplierSearchList({ supplierName })
  if (result.data && Array.isArray(result.data)) {
    supplierList.value = result.data.map((v: any) => {
      return {
        ...v,
        label: v.name,
        value: v.id,
      }
    })
  }
}

const fetchShopList = async (shopName = '') => {
  const result = await getShopSearchList({ shopName })
  if (result.data && Array.isArray(result.data)) {
    shopList.value = result.data.map((v: any) => {
      return {
        ...v,
        label: v.name,
        value: v.id,
      }
    })
  }
}
const supplierExport = () => {
  emitFn('supplierExport', searchType)
}
</script>

<template>
  <div>
    <SchemaForm v-if="!isShow" v-model="searchType" :columns="columns" @searchHandle="handleSearch" @handleReset="handleReset">
      <template #otherOperations>
        <el-button class="from_btn" round type="primary" @click="supplierExport">导出</el-button>
      </template>
    </SchemaForm>
  </div>
</template>
