<script lang="ts" setup>
import { reactive, ref, watch, onMounted } from 'vue'
import { doGetPlatformCategory, getSupplierSearchList } from '../../../../apis'
import { cloneDeep } from 'lodash-es'
import SchemaForms from '@/components/SchemaForm.vue'

const supplierList = ref<any[]>([])
const platformCategoryList = ref<any[]>([])

type SearchType = {
  supplierId: string
  platformCategoryParentId: string
  supplierGoodsName: string
  deliveryStartTime: Date | undefined
  deliveryEndTime: Date | undefined
  undefined?: undefined
  data?: undefined
}

const searchType = reactive<SearchType>({
  supplierId: '',
  platformCategoryParentId: '',
  supplierGoodsName: '',
  deliveryStartTime: undefined,
  deliveryEndTime: undefined,
})
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
    label: '供应商',
    labelWidth: 85,
    prop: 'supplierId',
    valueType: 'select',
    options: supplierList,
    fieldProps: {
      placeholder: '请输入供应商名称',
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
    label: '铺货员手机',
    labelWidth: 85,
    prop: 'deliveryUserPhone',
    valueType: 'input', // 改为input类型
    fieldProps: {
      placeholder: '请填写申请人手机',
      maxlength: 11,
      type: 'text', // 使用text类型配合v-model.number
      'v-model.number': '', // 只允许输入数字
      oninput: "value=value.replace(/[^\\d]/g,'')", // 只允许输入数字
      pattern: '^1[3-9]\\d{9}$', // 手机号码格式验证
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
  {
    label: '铺货时间',
    prop: 'data',
    valueType: 'date-picker',
    fieldProps: {
      type: 'daterange',
      startPlaceholder: '开始时间',
      endPlaceholder: '结束时间',
      format: 'YYYY/MM/DD',
      valueFormat: 'YYYY-MM-DD',
      onChange: (data: [Date, Date]) => {
        searchType.deliveryStartTime = data ? data[0] : undefined
        searchType.deliveryEndTime = data ? data[1] : undefined
      },
    },
  },
]
const emitFn = defineEmits(['search'])

const initialData = async () => {
  const { data, code } = await doGetPlatformCategory()
  platformCategoryList.value = data
}
onMounted(() => initialData())

const handleSearch = () => {
  const cloneSearchType = cloneDeep(searchType)
  delete cloneSearchType.undefined
  delete cloneSearchType.data
  emitFn('search', cloneSearchType)
}
const fetchSupplierList = async (supplierName = '') => {
  const result = await getSupplierSearchList({ supplierName })
  if (result.data && result.data?.length) {
    supplierList.value = result.data.map((item: { id: any; name: any }) => {
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
