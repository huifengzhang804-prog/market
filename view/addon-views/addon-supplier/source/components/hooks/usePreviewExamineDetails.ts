import { reactive, ref } from 'vue'

const usePreviewExamineDetails = () => {
  const previewVisible = ref(false)
  const commodityInfo = reactive({
    shopId: '',
    id: '',
  })
  const handlePreviewExamineDetails = (commodity: any) => {
    commodityInfo.id = commodity.id
    commodityInfo.shopId = commodity.shopId
    previewVisible.value = true
  }
  return {
    previewVisible,
    commodityInfo,
    handlePreviewExamineDetails,
  }
}

export default usePreviewExamineDetails
