import { WritableComputedRef, ref, computed, watch, reactive } from 'vue'
import type { ListInterface } from '../../types/list'
import Decimal from 'decimal.js'
import { getSupplierDistributionCost, doGetDeliveryAddress } from '../../../../../apis'
import { ElMessageBox } from 'element-plus'

const usePurchase = (tableData: WritableComputedRef<ListInterface>) => {
  const receiveList = ref<any[]>([])
  const freightTotal = ref(0)
  const maxUnlimitedNum = 10000
  const totalNum = computed(() => {
    return tableData.value.storageSkus?.reduce((pre, item) => {
      return (pre += item.purchaseNum || 0)
    }, 0)
  })
  const totalPrice = computed(() => {
    return (
      tableData.value.storageSkus?.reduce((pre, item) => {
        const plus = item.purchaseNum ? new Decimal(item.salePrice)?.div(10000).mul(new Decimal(item.purchaseNum)) : new Decimal(0)
        return new Decimal(pre).plus(plus)
      }, new Decimal(0)) || new Decimal(0)
    )
  })
  const maxBatchNum = computed(() => {
    const lists = tableData.value.storageSkus?.map((item) => (item?.stockType === 'UNLIMITED' ? maxUnlimitedNum : Number(item?.stock || 0))) || []
    return Math.min(...lists)
  })
  const totalOrderPrice = computed(() => {
    return totalPrice.value.plus(new Decimal(freightTotal.value))
  })
  const changeBatchPurchaseNum = (num?: number) => {
    tableData.value.storageSkus?.forEach((item) => (item.purchaseNum = num))
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
        shopFreights: [tableData.value].map((item) => {
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

  const purchaseFormModel = reactive({
    remark: '',
    receiveId: '',
  })

  const frightCompute = computed(() => ({
    totalNum: totalNum.value,
    receive: purchaseFormModel.receiveId,
  }))
  watch(
    () => frightCompute.value,
    () => getFreightCount(),
  )
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
  const handleRemove = (index: number) => {
    ElMessageBox.confirm('确认移除当前规格信息').then(() => {
      tableData.value.storageSkus?.splice(index, 1)
    })
  }
  return {
    receiveList,
    freightTotal,
    maxUnlimitedNum,
    totalNum,
    totalPrice,
    maxBatchNum,
    totalOrderPrice,
    changeBatchPurchaseNum,
    fetchReceiveAddress,
    purchaseFormModel,
    getOrderConfig,
    handleRemove,
  }
}

export default usePurchase

const buildSupplier = (tableData: WritableComputedRef<ListInterface>) => {
  const data = {
    supplierId: tableData.value.shopId,
    productSkus: {} as any,
  }
  tableData.value.storageSkus?.forEach((sku) => {
    if (sku.purchaseNum) {
      data.productSkus[tableData.value.id]
      if (!data.productSkus[tableData.value.id]) {
        data.productSkus[tableData.value.id] = []
      }
      data.productSkus[tableData.value.id].push({ skuId: sku.id, num: sku.purchaseNum })
    }
  })
  return [data]
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
