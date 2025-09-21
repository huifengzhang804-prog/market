import { ref } from 'vue'

const useBatch = () => {
  const showBatchDialog = ref(false)
  const batchPurchaseLines = ref<any[]>([])
  const handleSkus = (item: any) => item.storageSkus?.map((ite) => ({ ...ite, purchaseNum: 1 }))

  const setBatchPurchaseLines = (checkedLines: any[] = []) => {
    batchPurchaseLines.value = checkedLines.map((item) => ({
      ...item,
      batchNum: 1,
      totalPurchase: item.storageSkus.length,
      storageSkus: handleSkus(item),
    }))
  }

  const openBatchDialog = (checkedLines: any[] = []) => {
    batchPurchaseLines.value = checkedLines.map((item) => ({
      ...item,
      batchNum: 1,
      totalPurchase: item.storageSkus.length,
      storageSkus: handleSkus(item),
    }))
    showBatchDialog.value = true
  }
  const batchDialogConfirmLoading = ref(false)
  return {
    batchPurchaseLines,
    showBatchDialog,
    openBatchDialog,
    setBatchPurchaseLines,
    batchDialogConfirmLoading,
  }
}

export default useBatch
