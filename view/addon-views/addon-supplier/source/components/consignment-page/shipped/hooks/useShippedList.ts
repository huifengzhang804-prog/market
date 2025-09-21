import { reactive, ref, computed } from 'vue'
import { doGetShippedGoodsList, doUpdateSellStatus, postAddContact } from '../../../../apis'
import { GoodsStatusEnum, ShippedGoodsList } from '../types'
import { TabPaneName, ElMessage } from 'element-plus'
import useConvert from '@/composables/useConvert'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import { useRouter } from 'vue-router'

export enum StatusMap {
  SELL_ON = '已上架',
  SELL_OFF = '已下架',
  SUPPLIER_SELL_OFF = '供应商下架',
  PLATFORM_SELL_OFF = '违规下架',
}
const { divTenThousand } = useConvert()
const useShippedList = () => {
  const router = useRouter()
  const tableList = ref<ShippedGoodsList[]>([])
  const multiSelect = ref<ShippedGoodsList[]>([])
  const currentRow = ref<ShippedGoodsList>()
  const gridData = ref([
    {
      supplier: '上架',
      shop: '上架',
      address: '1、处于【上架,库存>=1】的代销商品，店铺才能【铺货/上架】',
    },
    {
      supplier: '下架',
      shop: '供应商下架',
      address: `1、店铺端下架代销商品不会影响供应商端代销商品的状态</br>
                      2、供应商下架商品时，店铺端自动下架状态变更为【供应商下架】。供应商重新上架后，店铺端才能再次上架代销商品。`,
    },
    {
      supplier: '违规下架',
      shop: '违规下架',
      address: '1、平台端可【恢复销售】，供应商再重新【上架】后，店铺才能重新【上架】该代销商品',
    },
    {
      supplier: '删除',
      shop: '同步删除',
      address: '1、已删除商品无法恢复，但不影响已生成的订单;店铺端无法删除代销商品',
    },
    {
      supplier: '供应商被【禁用】',
      shop: '自动剔除',
      address: `1、供应商被禁用后，它的所有代销商品将从店铺端全部剔除，但不影响已生成的订单</br>
                      2、供应商再次【启用】后，店铺需重新铺货代销商品`,
    },
  ])
  const pagination = reactive({
    page: { current: 1, size: 10 },
    total: 0,
  })
  const dialogTableVisible = ref(false)
  const searchCondition = ref<any>({ status: '' })
  const handleSearch = (condition: any = {}) => {
    searchCondition.value = { ...condition, status: searchCondition.value.status }
    pagination.page.current = 1
    initList()
  }
  const computedSuplierStock = computed(() => (storageSkus: ShippedGoodsList['storageSkus']) => {
    let totalNum = 0,
      isLimited = false
    storageSkus?.forEach((item) => {
      totalNum += Number(item.stock)
      if (item.stockType === 'LIMITED') isLimited = true
    })
    return isLimited ? totalNum : '无限库存'
  })
  const initList = async () => {
    const result = await doGetShippedGoodsList({ ...pagination.page, ...searchCondition.value })
    if (result.data) {
      pagination.total = Number(result.data.total)
      tableList.value = result.data.records
    }
  }
  /**
   * 计算供货价区间
   */
  const computedSalePrice = computed(() => (row: ShippedGoodsList) => {
    const salePrices = (row?.salePrices || [])?.map((salePrice) => parseInt(salePrice))
    const minSalePrice = Math.min(...salePrices)
    const maxSalePrice = Math.max(...salePrices)
    if (minSalePrice === maxSalePrice) {
      return divTenThousand(minSalePrice)
    } else {
      return `${divTenThousand(minSalePrice)}~￥${divTenThousand(maxSalePrice)}`
    }
  })
  /**
   * 计算销售价区间
   */
  const computedSalePriceRange = computed(() => (row: ShippedGoodsList) => {
    const salePrices = (row?.storageSkus || [])?.map((sku) => parseInt(sku.salePrice))
    const minSalePrice = Math.min(...salePrices)
    const maxSalePrice = Math.max(...salePrices)
    if (minSalePrice === maxSalePrice) {
      return divTenThousand(minSalePrice)
    } else {
      return `${divTenThousand(minSalePrice)}~￥${divTenThousand(maxSalePrice)}`
    }
  })
  const changeStatus = (val: TabPaneName) => {
    searchCondition.value.status = val as string
    pagination.page.current = 1
    initList()
  }
  const handleSizeChange = (value: number) => {
    pagination.page.size = value
    initList()
  }
  const handleCurrentChange = (value: number) => {
    pagination.page.current = value
    initList()
  }
  const handleContact = (row: ShippedGoodsList, type: 'shopId' | 'supplierId' = 'shopId') => {
    postAddContact(useShopInfoStore().shopInfo.id, row[type]).then(({ code }) => {
      if (code === 200) {
        router.push({ path: '/message/customer/supplierService', query: { id: row[type] } })
      }
    })
  }

  /**
   * 商品上下架
   * @param {boolean} isMulti
   */
  const changeCommodityStatus = (isMulti: boolean, status: string) => {
    if (multiSelect.value.length === 0 && isMulti) {
      ElMessage.error('请选择商品')
      return
    }
    removeFromShelves(isMulti, status)
  }
  const removeFromShelves = async (isMulti: boolean, status: string, row?: ShippedGoodsList) => {
    if (row) {
      currentRow.value = row
    }
    if (!currentRow.value && !isMulti) {
      ElMessage.warning('请选择商品')
      return
    }
    let ids = isMulti
      ? multiSelect.value.map((item) => ({ shopId: item.shopId, productId: item.id }))
      : [{ shopId: currentRow.value!.shopId, productId: currentRow.value!.id }]
    const { code, success } = await doUpdateSellStatus(ids, status)
    if (code === 200 && success) {
      multiSelect.value = []
      ElMessage.success(`${status.includes('OFF') ? '下' : '上'}架成功`)
      initList()
    }
  }

  const editHandle = (row: any) => {
    let path = ''
    if (row?.sellType === 'CONSIGNMENT') {
      path = '/goods/list/edit-consignment'
    } else {
      path = '/goods/list/edit'
    }
    router.push({
      path,
      query: {
        id: row.id,
        current: row?.sellType !== 'CONSIGNMENT' && (pagination.page.current as any),
      },
    })
  }

  return {
    searchCondition,
    tableList,
    multiSelect,
    dialogTableVisible,
    gridData,
    handleSearch,
    initList,
    pagination,
    computedSalePrice,
    computedSalePriceRange,
    computedSuplierStock,
    changeStatus,
    StatusMap: GoodsStatusEnum,
    handleSizeChange,
    handleCurrentChange,
    handleContact,
    removeFromShelves,
    changeCommodityStatus,
    editHandle,
  }
}

export default useShippedList
