import { ref, reactive, onMounted } from 'vue'
import { getSupplierSearchList, doGetPlatformCategory } from '../../../../apis'

const useSearch = () => {
  const isShow = ref(false)
  const supplierList = ref<any[]>([])
  const platformCategoryList = ref<any[]>([])
  const searchType = reactive({
    supplierId: '',
    productName: '',
    shopCategoryId: '',
  })
  const columns = [
    {
      label: '商品名称',
      prop: 'productName',
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
            searchType.shopCategoryId = e[e.length - 1]
          }
        },
      },
    },
  ]

  async function getPlatformCategory() {
    const { data, code } = await doGetPlatformCategory()
    platformCategoryList.value = data
  }

  const fetchSupplierList = async (supplierName = '') => {
    const result = await getSupplierSearchList({ supplierName })
    if (result.data && result.data?.length) {
      supplierList.value = result.data.map((v) => {
        return {
          label: v.name,
          value: v.id,
        }
      })
    }
  }

  onMounted(() => {
    getPlatformCategory()
  })

  return {
    isShow,
    searchType,
    columns,
    supplierList,
    platformCategoryList,
    fetchSupplierList,
  }
}

function checkCategoryEnable(currentLevel: number, records: any[]) {
  const isLastLevel = currentLevel === 3
  for (let index = 0; index < records.length; ) {
    const record = records[index]
    if (isLastLevel) {
      record.disabled = false
      index++
      continue
    }
    const children = (record.children || record.secondCategoryVos || record.categoryThirdlyVos) as any[]
    delete record.secondCategoryVos
    delete record.categoryThirdlyVos
    const disable = !children || children.length === 0
    record.disabled = disable
    if (disable) {
      records.splice(index, 1)
      continue
    }
    checkCategoryEnable(currentLevel + 1, children)
    if (children.length === 0) {
      records.splice(index, 1)
      continue
    }
    record.children = children
    index++
  }

  return records
}

export default useSearch
