import { reactive, ref, computed, watch } from 'vue'
import { getSupplierProductList, postAddContact } from '../../../../apis'
import { ListInterface } from '../../types/list'
import { useRouter, useRoute } from 'vue-router'
import { useShopInfoStore } from '@/store/modules/shopInfo'

const useGoodsSourceList = () => {
  const $router = useRouter()
  const $route = useRoute()
  const searchCondition = ref<any>({})
  const pagination = reactive({
    page: { current: 1, size: 10 },
    total: 0,
  })
  if ($route.query.supplierGoodsName) {
    searchCondition.value.supplierGoodsName = $route.query.supplierGoodsName
  }
  const tableList = ref<ListInterface[]>([])
  const multiSelect = ref<ListInterface[]>([])
  const computedSalePrice = computed(() => (row: ListInterface) => {
    const salePricesGroup = (row?.storageSkus || []).map((item: any) => Number(item.salePrice))
    const minPrice = Math.min(...salePricesGroup)
    const maxPrice = Math.max(...salePricesGroup)
    return minPrice === maxPrice ? maxPrice / 10000 : `${minPrice / 10000}~￥${maxPrice / 10000}`
  })
  const computedSuplier = computed(() => (storageSkus?: any[]) => {
    return storageSkus?.reduce((pre, item) => pre + Number(item.stock), 0)
  })
  const handleSearch = (condition: any = {}) => {
    searchCondition.value = condition
    pagination.page.current = 1
    initList()
  }

  /**
   * 检查库存
   */
  const checkLimited = (row: ListInterface) => {
    const unLimited = row.storageSkus.find((item) => item.stockType === 'UNLIMITED')
    if (unLimited) return 'UNLIMITED'
    const stock = row.storageSkus.some((item) => Number(item.stock) > 0)
    if (stock) return 'STOCK'
    return false
  }

  const initList = async () => {
    const result = await getSupplierProductList({ ...pagination.page, sellType: 'CONSIGNMENT', ...searchCondition.value })
    if (result.data) {
      pagination.total = Number(result.data.total)
    }
    tableList.value = result.data.records.map((item) => ({ ...item, disabled: !checkLimited(item) }))
  }
  const handleDistribution = (row: ListInterface) => {
    $router.push({ path: '/goods/consignment/distribution', query: { shopId: row.shopId, id: row.id } })
  }
  const handleSizeChange = (value: number) => {
    pagination.page.size = value
    initList()
  }
  const handleCurrentChange = (value: number) => {
    pagination.page.current = value
    initList()
  }
  const handleContact = (row: ListInterface) => {
    postAddContact(useShopInfoStore().shopInfo.id, row.shopId).then(({ code }) => {
      if (code === 200) {
        $router.push({ path: '/message/customer/supplierService', query: { id: row.shopId } })
      }
    })
  }
  return {
    pagination,
    tableList,
    multiSelect,
    computedSalePrice,
    computedSuplier,
    handleSearch,
    initList,
    checkLimited,
    handleDistribution,
    handleSizeChange,
    handleCurrentChange,
    handleContact,
  }
}

export default useGoodsSourceList
