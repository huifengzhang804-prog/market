const useRemark = () => {
    const remarkData = reactive({
        remark: '',
        orderNos: [] as string[],
    })
    const showRemarkDialog = ref(false)
    const setRemarkData = (orderNos: string[], remark?: string) => {
        remarkData.orderNos = orderNos
        remarkData.remark = remark || ''
        showRemarkDialog.value = true
    }
    return {
        remarkData,
        showRemarkDialog,
        setRemarkData,
    }
}

export default useRemark
