const usePreviewExamineDetails = () => {
    const previewVisible = ref(false)
    const commodityId = ref('')
    const handlePreviewExamineDetails = (id = '') => {
        commodityId.value = id
        previewVisible.value = true
    }
    return {
        previewVisible,
        commodityId,
        handlePreviewExamineDetails,
    }
}

export default usePreviewExamineDetails
