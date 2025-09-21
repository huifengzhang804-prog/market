const useExamineRejectReason = () => {
    const showReasonDialog = ref(false)
    const rejectReson = ref('')
    const handleShowRejectReason = (reason: string) => {
        rejectReson.value = reason
        showReasonDialog.value = true
    }
    return {
        showReasonDialog,
        rejectReson,
        handleShowRejectReason,
    }
}

export default useExamineRejectReason
