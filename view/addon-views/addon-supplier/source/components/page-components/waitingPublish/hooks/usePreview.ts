import { ref } from 'vue'
import { ToBeReleaseList } from '../types'

const usePreview = () => {
  const currentRow = ref<ToBeReleaseList>()
  const showPreviewDialog = ref(false)
  const openPreviewDialog = (row: ToBeReleaseList) => {
    currentRow.value = row
    showPreviewDialog.value = true
  }
  return {
    currentRow,
    showPreviewDialog,
    openPreviewDialog,
  }
}

export default usePreview
