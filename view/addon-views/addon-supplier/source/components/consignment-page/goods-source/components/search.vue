<script lang="ts" setup>
import { reactive, ref, watch, onMounted, computed } from 'vue'
import { cloneDeep } from 'lodash-es'
import { doGetPlatformCategory, getSupplierSearchList } from '../../../../apis'
import SchemaForms from '@/components/SchemaForm.vue'

const supplierList = ref<any[]>([])
const platformCategoryList = ref<any[]>([])
const searchType = reactive({
  supplierId: '',
  platformCategoryParentId: '',
  supplierGoodsName: '',
})
const columns = computed(() => [
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
        if (e.length > 1) {
          searchType.platformCategoryParentId = e[e.length - 1]
        }
      },
    },
  },
])
const emitFn = defineEmits(['search'])

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
        value: item.id,
        label: item.name,
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
