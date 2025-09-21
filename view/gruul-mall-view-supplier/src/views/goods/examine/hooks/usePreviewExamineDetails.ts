const usePreviewExamineDetails = () => {
    const previewVisible = ref(false)
    const commodityInfo = reactive({
        id: '',
        shopId: '',
    })
    const handlePreviewExamineDetails = (row: any) => {
        commodityInfo.id = row?.id
        commodityInfo.shopId = row?.shopId
        previewVisible.value = true
    }
    return {
        previewVisible,
        commodityInfo,
        handlePreviewExamineDetails,
    }
}

export default usePreviewExamineDetails
