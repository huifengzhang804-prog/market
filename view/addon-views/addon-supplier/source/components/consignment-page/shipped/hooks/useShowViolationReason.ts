import { ref } from 'vue'
import { ShippedGoodsList } from '../types'

/**
 * 查看违规原因
 */
const useShowViolationReason = () => {
  // 是否展示违规下架的弹窗
  const showViolationReason = ref(false)
  // 违规原因数据
  const violationReason = ref<ShippedGoodsList['extra']['productViolation']>()
  // 设置违规原因并且展示弹窗
  const openViolationReasonDialog = (reason: ShippedGoodsList['extra']['productViolation']) => {
    violationReason.value = reason
    showViolationReason.value = true
  }
  return {
    violationReason,
    showViolationReason,
    openViolationReasonDialog,
  }
}

export default useShowViolationReason
