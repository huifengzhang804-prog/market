import { doGetSupplierExamineGoods, doUpdateSupplierSellStatus } from '../../apis'
import auditGoodsVue from '../components/audit-goods.vue'
import { ElMessage } from 'element-plus'
import { reactive, ref, computed, watch } from 'vue'
import { doGetSeachSupplierSearchList } from '@/apis/good'
import { doGetCategory } from '@/apis/shops'

type searchFormType = {
  name: string
  shopId: string | null
  platformCategoryId: string
  productType: 'REAL_PRODUCT' | 'VIRTUAL_PRODUCT' | ''
}

export enum ExamineGoodsEnum {
  UNDER_REVIEW = '待审核',
  ALREADY_PASSED = '已通过',
  REFUSE = '已拒绝',
}
const useExamineListHooks = () => {
  let cacheCurrentCheckRows: any[] = [] // 用于暂存当前选定的行信息
  const searchParams = ref<searchFormType>({
    name: '',
    platformCategoryId: '',
    productType: '',
    shopId: null,
  })
  const currentTab = ref<'' | 'ALREADY_PASSED' | 'UNDER_REVIEW' | 'REFUSE'>('')
  const selectItems = ref<any[]>([])
  const showAuditDialog = ref(false)
  const auditGoodsRefs = ref<InstanceType<typeof auditGoodsVue> | null>(null)
  const sortState = ref('')
  const sortLabel = ref('')
  const shopSearchList = ref([])
  const categoryList = ref([])
  // 表单配置项
  const columns = computed(() => {
    return [
      {
        label: '商品名称',
        labelWidth: 75,
        prop: 'name',
        valueType: 'copy',
        fieldProps: {
          placeholder: '请输入商品名称',
        },
      },
      {
        label: '供应商名称',
        labelWidth: 85,
        prop: 'shopId',
        valueType: 'select',
        options: shopSearchList,
        fieldProps: {
          placeholder: '请输入供应商名称',
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
        label: '平台类目',
        prop: 'platformCategoryId',
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
              searchParams.value.platformCategoryId = e[e.length - 1]
            } else {
              searchParams.value.platformCategoryId = ''
            }
          },
        },
      },
      {
        label: '商品类型',
        labelWidth: 75,
        prop: 'productType',
        valueType: 'select',
        options: [
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
        ],
        fieldProps: {
          placeholder: '请选择',
        },
      },
    ]
  })
  const visible = ref(false)
  const refuseVisible = ref(false)
  const formModel = reactive({
    explain: '',
  })
  watch(
    () => sortLabel.value,
    (val) => {
      sortState.value = ''
    },
  )
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
  /**
   * 获取类目列表
   */
  const pageConfig = reactive({
    pageSize: 20,
    pageNum: 1,
    total: 0,
  })
  getCategory()
  async function getCategory() {
    const { data } = await doGetCategory({
      current: pageConfig.pageNum,
      size: 1000,
    })
    pageConfig.total = data.total
    initLists(data.records, 'secondCategoryVos')
    categoryList.value = data.records
  }
  function initLists(list: any[], str: string) {
    list.forEach((item) => {
      if (item[str]) {
        item.children = item[str]
        delete item[str]
        if (item.children.length) {
          initLists(item.children, 'categoryThirdlyVos')
        }
      }
    })
  }
  const initList = async () => {
    let goodsList = [],
      total = 0
    try {
      console.log(searchParams.value)

      const result = await doGetSupplierExamineGoods({
        ...searchParams.value,
        ...tableList.page,
        productAuditStatus: currentTab.value,
      })
      if (result.code === 200) {
        goodsList = result.data.records
        total = result.data.total
      }
    } finally {
      tableList.goods = goodsList
      tableList.total = total
    }
  }

  const salePriceRange = computed(() => (salePrices: string[] = []) => {
    const min = Math.min(...salePrices.map((item) => Number(item))) / 10000
    const max = Math.max(...salePrices.map((item) => Number(item))) / 10000
    if (max === min) {
      return max.toFixed(2)
    } else {
      return `${min.toFixed(2)} ~ ￥${max.toFixed(2)}`
    }
  })
  const getSearch = () => {
    initList()
  }
  const goodsStatus = {
    全部: '',
    待审核: 'UNDER_REVIEW',
    已通过: 'ALREADY_PASSED',
    已拒绝: 'REFUSE',
  }

  const handleTabClick = () => {
    tableList.page.current = 1
    initList()
  }

  /**
   * 批量审核
   */
  const handleBatchExamine = () => {
    if (selectItems.value.length) {
      cacheCurrentCheckRows = selectItems.value
      showAuditDialog.value = true
      return
    }
    return ElMessage.error({ message: '请选择需要审核的商品信息' })
  }

  const handleReset = () => {
    Object.keys(searchParams.value).forEach((key) => ((searchParams.value as any)[key] = ''))
    getSearch()
  }

  const tableList = reactive({
    page: { size: 10, current: 1 },
    goods: [],
    total: 0,
  })

  const handleAuditGoods = (goodsList: any[], flag: boolean) => {
    if (goodsList.length === 0) {
      return ElMessage.error({ message: '请选择需要审核的商品信息' })
    }
    cacheCurrentCheckRows = goodsList
    if (flag) {
      visible.value = true
    } else {
      refuseVisible.value = true
    }
  }
  const handleConfirmAudit = async (status = '', explain = '') => {
    let rs = {} as any
    if (showAuditDialog.value) {
      rs = await auditGoodsRefs.value?.validateForm()
    }
    if (status === 'REFUSE' && !explain) {
      return
    }
    const statu = rs.status ? rs.status : status
    const { code, msg } = await doUpdateSupplierSellStatus(
      {
        explain: explain ? explain : rs.explain,
        productIds: cacheCurrentCheckRows?.map((item: any) => item.id),
      },
      statu,
    )
    if (code === 200) {
      ElMessage.success({ message: msg || '更新状态成功' })
      showAuditDialog.value = false
      if (status && explain) {
        refuseVisible.value = false
      } else if (rs.status || rs.explain) {
        showAuditDialog.value = false
      } else if (status && !explain) {
        visible.value = false
      }
      formModel.explain = ''
      initList()
    } else {
      ElMessage.error({ message: msg || '更新状态失败' })
    }
  }
  const handleCloseAuditDialog = () => {
    cacheCurrentCheckRows = []
  }
  const handleCurrentChange = (currentPage: number) => {
    tableList.page.current = currentPage
    initList()
  }
  const handleSizeChange = (size: number) => {
    tableList.page.size = size
    tableList.page.current = 1
    initList()
  }
  // 时间排序
  const sortList = (label: string) => {
    if (label === '提交时间') {
      return tableList.goods.sort((a: any, b: any) => {
        return sortState.value === 'just'
          ? Number(new Date(a.submitTime)) - Number(new Date(b.submitTime))
          : Number(new Date(b.submitTime)) - Number(new Date(a.submitTime))
      })
    } else {
      return tableList.goods.sort((a: any, b: any) => {
        return sortState.value === 'just'
          ? Number(new Date(a.auditTime)) - Number(new Date(b.auditTime))
          : Number(new Date(b.auditTime)) - Number(new Date(a.auditTime))
      })
    }
  }
  const sortTableList = (label: string) => {
    sortLabel.value = label
    sortState.value = sortState.value === 'just' ? 'inverted' : 'just'
    tableList.goods = sortList(label)
  }
  initList()
  return {
    getSearch,
    handleReset,
    currentTab,
    goodsStatus,
    handleTabClick,
    tableList,
    salePriceRange,
    ExamineGoodsEnum,
    initList,
    selectItems,
    handleAuditGoods,
    showAuditDialog,
    auditGoodsRefs,
    handleCloseAuditDialog,
    handleConfirmAudit,
    handleCurrentChange,
    handleSizeChange,
    sortTableList,
    searchParams,
    columns,
    handleBatchExamine,
    formModel,
    visible,
    refuseVisible,
  }
}

export default useExamineListHooks
