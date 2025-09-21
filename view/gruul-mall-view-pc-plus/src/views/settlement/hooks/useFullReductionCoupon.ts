import { OrderType } from '../types'

const useFullReductionCoupon = () => {
  const showFullReductionCouponList = ref(false)
  const fullReductionItem = ref<OrderType>()
  const setFullReductionCouponItem = (item: OrderType) => {
    fullReductionItem.value = item
    showFullReductionCouponList.value = true
  }
  return {
    showFullReductionCouponList,
    fullReductionItem,
    setFullReductionCouponItem,
  }
}

export default useFullReductionCoupon
