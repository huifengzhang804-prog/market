<script lang="ts" setup>
import { reactive, ref, watch, onMounted } from 'vue'
import { cloneDeep } from 'lodash-es'
import { getSupplierSearchList, doGetPlatformCategory } from '../../../../apis'
import SchemaForms from '@/components/SchemaForm.vue'

export interface SearchInterface {
  supplierId: string
  platformCategoryParentId: string
  supplierGoodsName: string
}
const supplierList = ref<any[]>([])
const platformCategoryList = ref<any[]>([])
const searchType = reactive<SearchInterface>({
  supplierId: '',
  platformCategoryParentId: '',
  supplierGoodsName: '',
})
const emitFn = defineEmits(['search'])
const columns = [
  {
    label: '商品名称',
    prop: 'supplierGoodsName',
    valueType: 'copy',
    fieldProps: {
      placeholder: '请输入商品名称',
    },
  },
  {
    label: '供应商名称',
    labelWidth: 85,
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
    label: '平台类目',
    valueType: 'cascader',
    options: platformCategoryList,
    fieldProps: {
      placeholder: '请选择平台类目',
      props: {
        expandTrigger: 'hover' as 'click' | 'hover',
        label: 'name',
        value: 'id',
        children: 'secondCategoryVos',
      },
      onChange: (e: any[]) => {
        if (e?.length > 1) {
          searchType.platformCategoryParentId = e[e.length - 1]
        } else {
          searchType.platformCategoryParentId = ''
        }
      },
    },
  },
]

const initialData = async () => {
  const { data, code } = await doGetPlatformCategory()
  platformCategoryList.value = data
}
onMounted(() => initialData())

const handleSearch = () => {
  const cloneSearchType = cloneDeep(searchType)
  delete cloneSearchType.undefined
  emitFn('search', cloneSearchType)
}
const fetchSupplierList = async (supplierName = '') => {
  const result = await getSupplierSearchList({ supplierName })
  if (result.data && result.data?.length) {
    supplierList.value = result.data.map((item) => {
      return {
        label: item.name,
        value: item.id,
      }
    })
  }
}
const handleReset = () => {
  Object.keys(searchType).forEach((key) => (searchType[key] = ''))
  handleSearch()
}
</script>

<template>
  <SchemaForms v-model="searchType" :columns="columns" :show-number="3" @searchHandle="handleSearch" @handleReset="handleReset" />
</template>
