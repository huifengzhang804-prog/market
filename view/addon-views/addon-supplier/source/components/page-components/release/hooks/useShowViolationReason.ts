import { ref } from 'vue'
import { ReleaseList } from '../types'

/**
 * 查看违规原因
 */
const useShowViolationReason = () => {
  // 是否展示违规下架的弹窗
  const showViolationReason = ref(false)
  // 违规原因数据
  const violationReason = ref<ReleaseList['extra']['productViolation']>()
  // 设置违规原因并且展示弹窗
  const openViolationReasonDialog = (reason: ReleaseList['extra']['productViolation']) => {
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
