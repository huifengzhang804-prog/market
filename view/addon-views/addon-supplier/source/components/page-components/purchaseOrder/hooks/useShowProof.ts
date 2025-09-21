import { ref } from 'vue'

const useShowProof = () => {
  const showProof = ref(false)
  const currentProof = ref('')
  const goToShowProof = (currentRow: { extra: { pay: { proof: string } } }) => {
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
