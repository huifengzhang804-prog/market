import { getPurchaseIssueList, putIssureListOnline, doUpdateSellStatus } from '../../../../apis'
import { ElMessage, ElMessageBox, TabPaneName } from 'element-plus'
import { ReleaseList } from '../types'
import { reactive, ref, computed } from 'vue'
import { postAddContact } from '../../../../apis'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import { useRouter } from 'vue-router'

const useRelease = () => {
  const $router = useRouter()
  // 分页控制
  const pagination = reactive({
    page: { current: 1, size: 10 },
    total: 0,
  })
  // 搜索条件控制字段
  const searchType = reactive({
    supplierId: '',
    productName: '',
    shopCategoryId: '',
    status: '',
  })
  // 选中表格列表
  const multiSelect = ref<ReleaseList[]>([])
  // 已发布数据列表
  const releaseData = ref<ReleaseList[]>([])
  // 初始化数据/刷新数据
  const initData = async () => {
    let data: ReleaseList[] = [],
      total = 0
    try {
      // 拼接分页和搜索条件数据
      const result = await getPurchaseIssueList({ ...pagination.page, ...searchType })
      if (result.code === 200 && result.data) {
        data = result.data.records
        total = Number(result.data.total)
      }
    } finally {
      releaseData.value = data
      pagination.total = total
    }
  }
  /**
   * 点击调用搜索，重置页码，刷新列表
   * @param searchCondition 搜索条件
   */
  const handleSearch = (searchCondition: typeof searchType) => {
    Object.keys(searchCondition).forEach((key) => {
      const searchKey = key as keyof typeof searchType
      searchType[searchKey] = searchCondition[searchKey] || ''
    })
    pagination.page.current = 1
    initData()
  }
  /**
   * 商家被下架的商品
   * @param id 商品编号ID
   */
  const handleSaleOn = async (isMulti: boolean, status: string, row?: any) => {
    let ids = isMulti
      ? multiSelect.value.map((item) => ({ shopId: useShopInfoStore().shopInfo.shopId, productId: item.id }))
      : [{ shopId: useShopInfoStore().shopInfo.shopId, productId: row.id as string }]
    const { code, success } = await doUpdateSellStatus(ids, status)
    if (code === 200 && success) {
      multiSelect.value = []
      ElMessage.success(`${status.includes('OFF') ? '下' : '上'}架成功`)
    }
    initData()
  }
  /**
   * 价格字段处理
   */
  const computedSalePrice = computed(() => (row: any) => {
    const salePricesGroup = (row?.salePrices || []).map((item: any) => Number(item))
    const minPrice = Math.min(...salePricesGroup)
    const maxPrice = Math.max(...salePricesGroup)
    return minPrice === maxPrice ? maxPrice / 10000 : `${minPrice / 10000}~￥${maxPrice / 10000}`
  })
  const changeStatus = (status: TabPaneName) => {
    searchType.status = status as string
    pagination.page.current = 1
    multiSelect.value = []
    initData()
  }
  const handleSizeChange = (value: number) => {
    pagination.page.size = value
    initData()
  }
  const handleCurrentChange = (value: number) => {
    pagination.page.current = value
    initData()
  }
  const handleContact = (row: any) => {
    postAddContact(useShopInfoStore().shopInfo.id, row.shopId).then(({ code }) => {
      if (code === 200) {
        $router.push({ path: '/message/customer/supplierService', query: { id: row.supplierId } })
      }
    })
  }
  return {
    handleSearch,
    releaseData,
    multiSelect,
    pagination,
    initData,
    handleSaleOn,
    searchType,
    changeStatus,
    handleSizeChange,
    handleCurrentChange,
    computedSalePrice,
    handleContact,
  }
}

export default useRelease
