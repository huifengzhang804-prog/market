import { ref } from 'vue'

const useInStorage = () => {
  const showInStorageDialog = ref(false)
  const handleInStorage = () => {
    showInStorageDialog.value = true
  }
  return {
    showInStorageDialog,
    handleInStorage,
  }
}

export default useInStorage
