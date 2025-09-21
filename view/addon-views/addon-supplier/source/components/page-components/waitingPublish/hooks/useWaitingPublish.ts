import { ref } from 'vue'
import { getToBeReleaseList } from '../../../../apis'
import { ToBeReleaseList } from '../types'
import { reactive, computed } from 'vue'

const useWaitingPublish = () => {
  const searchCondition = reactive({
    supplierId: '',
    categoryId: '',
    productName: '',
  })
  const pagination = reactive({
    page: { current: 1, size: 10 },
    total: 0,
  })
  const waitingPublishData = ref<ToBeReleaseList[]>([])
  const initData = async () => {
    const { code, data } = await getToBeReleaseList({ ...pagination.page, ...searchCondition })
    if (code === 200 && data) {
      waitingPublishData.value = data?.records || []
      pagination.total = Number(data?.total)
    }
  }
  const handleSearch = (searchType: typeof searchCondition) => {
    Object.keys(searchType).forEach((key) => {
      const searchKey = key as keyof typeof searchCondition
      searchCondition[searchKey] = searchType[key]
    })
    pagination.page.current = 1
    initData()
  }

  const computedSalePrice = computed(() => (row: any) => {
    const salePricesGroup = (row?.prices || []).map((item: any) => Number(item))
    const minPrice = Math.min(...salePricesGroup)
    const maxPrice = Math.max(...salePricesGroup)
    return minPrice === maxPrice ? maxPrice / 10000 : `${minPrice / 10000}~￥${maxPrice / 10000}`
  })
  const handleSizeChange = (value: number) => {
    pagination.page.size = value
    initData()
  }
  const handleCurrentChange = (value: number) => {
    pagination.page.current = value
    initData()
  }
  return {
    handleSearch,
    waitingPublishData,
    pagination,
    computedSalePrice,
    handleSizeChange,
    handleCurrentChange,
    initData,
  }
}

export default useWaitingPublish
