import { ref } from 'vue'

const useOpenStorageDetails = () => {
  const storageDetailsOrderNo = ref('')
  const showStorageDetailsDialog = ref(false)
  const openStorageDetailsDialog = (orderNo: string) => {
    storageDetailsOrderNo.value = orderNo
    showStorageDetailsDialog.value = true
  }
  return {
    storageDetailsOrderNo,
    showStorageDetailsDialog,
    openStorageDetailsDialog,
  }
}

export default useOpenStorageDetails
