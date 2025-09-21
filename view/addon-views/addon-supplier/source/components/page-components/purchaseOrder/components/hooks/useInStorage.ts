import { ref, computed } from 'vue'
import { getInStorageDetails } from '../../../../../apis'
import { ElMessageBox } from 'element-plus'
import type { InStorageInterface } from '../types/instorage'

const useBatchPurchase = ($props: any): any => {
  const remark = ref('')
  const tableData = ref<InStorageInterface['products']>([])
  const expandRowKeys = ref<any[]>([])
  const expandOpen = (row: any, expandedRows: any) => {
    // 如果当前行已经展开，则收起
    console.log(row, 'row.key', expandRowKeys.value.includes(row.productId))

    if (expandRowKeys.value.includes(row.productId)) {
      expandRowKeys.value = expandRowKeys.value.filter((key) => key !== row.productId)
    } else {
      // 否则，展开当前行并收起其他行
      expandRowKeys.value.push(row.productId)
    }
  }
  const maxRowBatchNum = computed(() => (row: any) => {
    let minLotStartingNum = row?.skus?.[0]?.num - row?.skus?.[0]?.used
    row?.skus?.forEach((item: any) => {
      const resetNum = item?.num - item?.used
      if (resetNum < minLotStartingNum) {
        minLotStartingNum = resetNum
      }
    })
    return minLotStartingNum || 1
  })
  const changeRowBatchNum = (rowId: string, batchNum?: number) => {
    if (typeof batchNum === 'number') {
      const currentRowIndex = tableData.value.findIndex((line) => line.productId === rowId)
      if (currentRowIndex > -1) {
        tableData.value[currentRowIndex]?.skus.forEach((item) => (item.inStorageNum = batchNum))
      }
    }
  }
  const computedActualNum = computed(() => (id: string) => {
    const currentRowIndex = tableData.value.findIndex((line) => line.productId === id)
    if (currentRowIndex > -1) {
      const totalNum = tableData.value[currentRowIndex].skus.reduce((pre, currentRow) => {
        return pre + Number(currentRow.inStorageNum || 0)
      }, 0)
      return totalNum
    }
    return 0
  })
  const handleRemoveCommodity = (index: number) => {
    ElMessageBox.confirm('确认移除当前商品信息').then(() => {
      tableData.value.splice(index, 1)
    })
  }
  const handleRemoveSku = (commodityIndex: number, skuIndex: number) => {
    ElMessageBox.confirm('确认移除当前规格信息').then(() => {
      tableData.value?.[commodityIndex]?.skus?.splice(skuIndex, 1)
    })
  }
  const initialInstorageData = async () => {
    const result = await getInStorageDetails($props.orderNo)
    if (result.data) {
      remark.value = result?.data?.remark
      tableData.value = result.data?.products
    }
  }
  return {
    remark,
    tableData,
    expandRowKeys,
    expandOpen,
    maxRowBatchNum,
    changeRowBatchNum,
    computedActualNum,
    handleRemoveCommodity,
    handleRemoveSku,
    initialInstorageData,
  }
}

export default useBatchPurchase
