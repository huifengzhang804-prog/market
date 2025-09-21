import { getSupplierDistributionCost, doGetDeliveryAddress } from '../../../../../apis'
import { WritableComputedRef, computed, nextTick, reactive, ref, watch } from 'vue'
import { ListInterface } from '../../types/list'
import Decimal from 'decimal.js'
import { ElMessageBox } from 'element-plus'

const useBatchPurchase = (commodityList: WritableComputedRef<ListInterface[]>) => {
  const paginationOptions = reactive({
    page: 1,
    pageSize: 5,
    total: 0,
  })
  const purchaseFormModel = reactive({
    remark: '',
    receiveId: '',
  })
  const refreshKey = ref(0)
  const tableData = commodityList
  const freightTotal = ref(0)
  const expandRowKeys = ref<any[]>([])
  const receiveList = ref<any[]>([])
  const expandOpen = (_: any, expand: any) => {
    expandRowKeys.value = expand.map((item: any) => item.id)
  }
  const handleChangePurchaseNum = (rowId: string) => {
    nextTick(() => {
      const currentRowIndex = tableData.value.findIndex((line) => line.id === rowId)
      const totalNum = tableData.value[currentRowIndex]?.storageSkus?.reduce((pre: number, currentRow: any) => {
        return pre + Number(currentRow.purchaseNum || 0)
      }, 0)
      tableData.value[currentRowIndex].totalPurchase = totalNum
      // getFreightCount()
    })
  }
  const minRowBatchNum = computed(() => (row: any) => {
    let maxLotStartingNum = 0
    row?.skus?.forEach((item: any) => {
      if (item.lotStartingNum > maxLotStartingNum) {
        maxLotStartingNum = item.lotStartingNum
      }
    })
    return +maxLotStartingNum
  })
  const maxRowBatchNum = computed(() => (row: any) => {
    if (row?.storageSkus?.[0]?.stockType === 'UNLIMITED') {
      return 100000
    }
    let minLotStartingNum = row?.skus?.[0]?.supplierInventory
    row?.skus?.forEach((item: any) => {
      if (item.supplierInventory < minLotStartingNum) {
        minLotStartingNum = item.supplierInventory
      }
    })
    return +minLotStartingNum
  })

  const computedSalePrice = computed(() => (row: any) => {
    const salePricesGroup = (row?.salePrices || []).map((item: any) => Number(item))
    const minPrice = Math.min(...salePricesGroup)
    const maxPrice = Math.max(...salePricesGroup)
    return minPrice === maxPrice ? maxPrice / 10000 : `${minPrice / 10000}~${maxPrice / 10000}`
  })

  const computedShopOwnProductStockNum = (row: any) => {
    return row?.storageSkus?.reduce((pre: number, currentRow: any) => {
      return pre + Number(currentRow?.shopOwnProductStockNum || 0)
    }, 0)
  }
  const totalNum = computed(() => {
    return tableData.value.reduce((pre, item) => {
      return pre + (item.storageSkus?.reduce((prev, sku) => (prev += sku.purchaseNum || 0), 0) || 0)
    }, 0)
  })

  const totalPrice = computed(() => {
    return tableData.value.reduce((pre, item) => {
      return pre.add(
        item.storageSkus?.reduce((prev, sku) => {
          const plus = sku.purchaseNum ? new Decimal(sku.salePrice)?.div(10000).mul(new Decimal(sku.purchaseNum || 0)) : new Decimal(0)
          return new Decimal(prev).plus(plus)
        }, new Decimal(0)) || new Decimal(0),
      )
    }, new Decimal(0))
  })
  const totalOrderPrice = computed(() => totalPrice.value.add(new Decimal(freightTotal.value)))
  const computedSuplier = computed(() => (storageSkus?: any[]) => {
    if (storageSkus?.[0]?.stockType === 'UNLIMITED') return '无限库存'
    return storageSkus?.reduce((prev, item) => (prev += Number(item.stock)), 0)
  })
  const changeRowBatchNum = (rowId: string, batchNum?: number) => {
    // debugger
    if (typeof batchNum === 'number') {
      const currentRowIndex = tableData.value.findIndex((line) => line.id === rowId)
      if (currentRowIndex > -1) {
        tableData.value[currentRowIndex]?.storageSkus?.forEach((item: any) => {
          item.purchaseNum = batchNum
        })
        handleChangePurchaseNum(rowId)
      }
    }
  }
  /**
   * 获取运费
   */
  const getFreightCount = async () => {
    const receiveInfo = receiveList.value.find((item) => item.id === purchaseFormModel.receiveId)
    if (!receiveInfo) return
    let freightCount = 0
    try {
      const result = await getSupplierDistributionCost({
        area: receiveInfo.area,
        freeRight: false,
        distributionMode: ['EXPRESS'],
        address: receiveInfo?.address,
        shopFreights: tableData.value.map((item) => {
          return {
            shopId: item.shopId,
            freights: [
              {
                templateId: item.freightTemplateId,
                skuInfos: item.storageSkus?.map((sku) => ({
                  price: sku.salePrice,
                  skuId: sku.id,
                  num: sku.purchaseNum || 0,
                  weight: sku.weight,
                })),
              },
            ],
          }
        }),
      })
      const costTotal: number[] = Object.values(result?.data?.EXPRESS || {})
      freightCount = costTotal.reduce((pre, item) => (pre += item), 0)
    } finally {
      freightTotal.value = freightCount
    }
  }
  const fetchReceiveAddress = async () => {
    let receiveData = []
    try {
      const result = await doGetDeliveryAddress()
      receiveData = result?.data?.records
    } finally {
      receiveList.value = receiveData
    }
  }

  const getOrderConfig = () => {
    const receiver = buildReceiver(receiveList.value, purchaseFormModel.receiveId)
    const sellType = 'PURCHASE'
    const remark = purchaseFormModel.remark
    const suppliers = buildSupplier(tableData)
    return {
      receiver,
      sellType,
      remark,
      suppliers,
    }
  }
  const handleRemoveBatch = (index: number) => {
    ElMessageBox.confirm('确认移除当前商品信息').then(() => {
      commodityList.value.splice(index, 1)
      paginationOptions.total = commodityList.value.length
      refreshKey.value = Date.now()
    })
  }
  const handleRemoveSku = (skuIndex: number, index: number, rowId: string) => {
    ElMessageBox.confirm('确认移除当前规格信息').then(() => {
      commodityList.value?.[index]?.storageSkus?.splice(skuIndex, 1)
      commodityList.value = commodityList.value.filter((item) => item.storageSkus && item.storageSkus.length > 0)
      nextTick(() => {
        paginationOptions.total = commodityList.value.length
      })
      handleChangePurchaseNum(rowId)
      refreshKey.value = Date.now()
    })
  }
  const frightCompute = computed(() => ({
    totalNum: totalNum.value,
    receive: purchaseFormModel.receiveId,
  }))
  watch(
    () => frightCompute.value,
    () => getFreightCount(),
  )
  return {
    totalPrice,
    totalNum,
    totalOrderPrice,
    freightTotal,
    purchaseFormModel,
    tableData,
    expandRowKeys,
    expandOpen,
    handleChangePurchaseNum,
    minRowBatchNum,
    maxRowBatchNum,
    computedSalePrice,
    computedShopOwnProductStockNum,
    computedSuplier,
    changeRowBatchNum,
    getFreightCount,
    receiveList,
    fetchReceiveAddress,
    getOrderConfig,
    handleRemoveBatch,
    handleRemoveSku,
    refreshKey,
    paginationOptions,
  }
}

export default useBatchPurchase

const buildSupplier = (tableData: WritableComputedRef<ListInterface[]>) => {
  const supplierList: any[] = []
  tableData.value.forEach((list) => {
    let isExistSupplierIndex = supplierList.findIndex((supplier) => supplier.supplierId === list.shopId)
    if (isExistSupplierIndex === -1) {
      isExistSupplierIndex = supplierList.push({ supplierId: list.shopId, productSkus: {} as any }) - 1
    }
    list.storageSkus?.forEach((sku) => {
      if (sku.purchaseNum) {
        supplierList[isExistSupplierIndex].productSkus[list.id]
        if (!supplierList[isExistSupplierIndex].productSkus[list.id]) {
          supplierList[isExistSupplierIndex].productSkus[list.id] = []
        }
        supplierList[isExistSupplierIndex].productSkus[list.id].push({ skuId: sku.id, num: sku.purchaseNum })
      }
    })
  })
  return supplierList.filter((item) => Object.keys(item?.productSkus).length > 0)
}

const buildReceiver = (receiverList: any[], checkedReceiverId = '') => {
  const checkedReceiver = receiverList.find((item) => item.id === checkedReceiverId)
  if (checkedReceiver) {
    return {
      name: checkedReceiver.contactName,
      mobile: checkedReceiver.contactPhone,
      area: checkedReceiver.area,
      address: checkedReceiver?.address,
    }
  } else {
    return {
      name: '',
      mobile: '',
      area: [],
      address: '',
    }
  }
}
