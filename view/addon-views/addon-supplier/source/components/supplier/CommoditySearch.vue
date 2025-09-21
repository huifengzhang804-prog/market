<script lang="ts" setup>
import { doGetCategory } from '@/apis/shops'
import type { searchFormType } from '../../types/supplier'
import type { CascaderProps } from 'element-plus'
import { doGetSeachSupplierSearchList } from '@/apis/good'
import { reactive, ref, watch } from 'vue'
import SchemaForm from '@/components/SchemaForm.vue'

const $props = defineProps({
  tabsActive: { type: String, default: ' ' },
})
const $emit = defineEmits(['getSearchParams', 'showChange'])
const searchForm = reactive<searchFormType>({
  platformCategoryId: '',
  shopId: null,
  productType: '',
  sellType: '',
  supplierGoodsName: '',
  status: '',
  cascaderModel: '',
})

const goodsTypeOptions = [
  {
    value: '',
    label: '全部商品',
  },
  {
    value: 'VIRTUAL_PRODUCT',
    label: '虚拟商品',
  },
  {
    value: 'REAL_PRODUCT',
    label: '实物商品',
  },
]

// 来源选项
const soucerOptions = [
  {
    value: '',
    label: '全部',
  },
  {
    value: 'PURCHASE',
    label: '采购商品',
  },
  {
    value: 'CONSIGNMENT',
    label: '代销商品',
  },
]
const shopSearchList = ref<any[]>([])
const categoryList = ref([])
const columns = [
  {
    label: '供应商名称',
    labelWidth: 85,
    prop: 'shopId',
    valueType: 'select',
    options: shopSearchList,
    fieldProps: {
      placeholder: '请输入店铺名称',
      props: {
        value: 'id',
        label: 'name',
        expandTrigger: 'hover',
      },
      filterable: true,
      remote: true,
      reserveKeyword: true,
      emptyValues: [null, undefined, ''],
      remoteMethod: (val: string) => {
        shopSearchRemote(val)
      },
    },
  },
  {
    label: '商品名称',
    labelWidth: 75,
    prop: 'supplierGoodsName',
    valueType: 'copy',
    fieldProps: {
      placeholder: '请输入商品名称',
    },
  },
  {
    label: '平台类目',
    prop: 'cascaderModel',
    valueType: 'cascader',
    options: categoryList,
    fieldProps: {
      placeholder: '请选择平台类目',
      props: {
        value: 'id',
        label: 'name',
        expandTrigger: 'hover',
      },
      showAllLevels: false,
      onChange: (e: any[]) => {
        if (e.length > 1) {
          searchForm.platformCategoryId = e[e.length - 1]
        }
      },
    },
  },
  {
    label: '商品类型',
    labelWidth: 75,
    prop: 'productType',
    valueType: 'select',
    options: goodsTypeOptions,
    fieldProps: {
      placeholder: '请选择',
    },
  },
  {
    label: '销售方式',
    labelWidth: 75,
    prop: 'sellType',
    valueType: 'select',
    options: soucerOptions,
    fieldProps: {
      placeholder: '请选择',
    },
  },
]
const searchHandle = () => {
  searchForm.platformCategoryId = searchForm.platformCategoryId === '0' ? '' : searchForm.platformCategoryId
  $emit('getSearchParams', searchForm)
}
const handleReset = () => {
  // @ts-ignore
  Object.keys(searchForm).forEach((key) => (searchForm[key] = ''))
  searchHandle()
}

const shopSearchRemote = async (supplierName: string) => {
  const result = await doGetSeachSupplierSearchList({ supplierName })
  shopSearchList.value =
    result?.data.map((v: any) => {
      return {
        ...v,
        label: v.name,
        value: v.id,
      }
    }) || []
}

watch(
  () => $props.tabsActive,
  (val) => {
    if (val !== ' ') {
      searchForm.status = ''
    }
  },
)
const pageConfig = reactive({
  pageSize: 20,
  pageNum: 1,
  total: 0,
})
getCategory()

/**
 * 获取类目列表
 */
async function getCategory() {
  const { data } = await doGetCategory({
    current: pageConfig.pageNum,
    size: 1000,
  })
  pageConfig.total = data.total
  initList(data.records, 'secondCategoryVos')
  data.records.unshift({ categoryId: '0', id: '0', name: '全部类目', parentId: '0', sort: 1 })
  categoryList.value = data.records
}
function initList(list: any[], str: string) {
  list.forEach((item) => {
    if (item[str]) {
      item.children = item[str]
      delete item[str]
      if (item.children.length) {
        initList(item.children, 'categoryThirdlyVos')
      }
    }
  })
}
</script>

<template>
  <div style="background: #f9f9f9">
    <el-config-provider :empty-values="[undefined, null]">
      <SchemaForm v-model="searchForm" :columns="columns" @searchHandle="searchHandle" @handleReset="handleReset"> </SchemaForm>
    </el-config-provider>
  </div>
</template>
