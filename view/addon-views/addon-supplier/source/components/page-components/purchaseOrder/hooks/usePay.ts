import { ref } from 'vue'

const usePay = () => {
  const showPayDialog = ref(false)
  const payPrice = ref('')
  const goToPayPurchaseOrder = (currentRow: { price: '' }) => {
    payPrice.value = currentRow.price
    showPayDialog.value = true
  }
  return {
    showPayDialog,
    goToPayPurchaseOrder,
    payPrice,
  }
}

export default usePay
