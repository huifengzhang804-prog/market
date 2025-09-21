import { ApiPurchaseOrder } from '@/views/order/purchase/components/split-table/order'

const useShowProof = () => {
    const showProof = ref(false)
    const currentProof = ref('')
    const goToShowProof = (currentRow: ApiPurchaseOrder) => {
        currentProof.value = currentRow?.extra?.pay?.proof || ''
        showProof.value = true
    }
    return {
        showProof,
        goToShowProof,
        currentProof,
    }
}

export default useShowProof
