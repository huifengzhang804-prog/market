const useAudit = () => {
    const auditOrderInfo = reactive({
        orderNo: '',
        payof: '',
        success: true,
        totalPrice: '',
    })
    const showAuditDialog = ref(false)
    const setOrderInfo = (data: { orderNo: string; payof: string; totalPrice: string }) => {
        auditOrderInfo.orderNo = data.orderNo
        auditOrderInfo.payof = data.payof
        auditOrderInfo.totalPrice = data.totalPrice
    }
    return {
        showAuditDialog,
        auditOrderInfo,
        setOrderInfo,
    }
}

export default useAudit
