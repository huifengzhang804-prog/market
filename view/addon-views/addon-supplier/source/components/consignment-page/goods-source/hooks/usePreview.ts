import { ListInterface } from '../../types/list'
import { ref } from 'vue'

const usePreview = () => {
  const previewData = ref<ListInterface>()
  const showPreviewDialog = ref(false)
  const handlePreviewData = (row: ListInterface) => {
    previewData.value = row
    showPreviewDialog.value = true
  }
  return {
    previewData,
    handlePreviewData,
    showPreviewDialog,
  }
}

export default usePreview
