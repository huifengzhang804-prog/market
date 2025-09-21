import { ref } from 'vue'
import { ShippedGoodsList } from '../types'

/**
 * 查看已发布商品
 */
const usePreview = () => {
  // 当前选中的行数据结构
  const currentRow = ref<ShippedGoodsList>()
  // 是否展示查看弹窗
  const showPreviewDialog = ref(false)
  /**
   * 打开查看弹窗并且赋值查看的行数据
   */
  const openPreviewDialog = (row: ShippedGoodsList) => {
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
