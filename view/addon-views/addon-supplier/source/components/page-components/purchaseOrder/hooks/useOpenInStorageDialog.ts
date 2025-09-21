import { ref } from 'vue'
import inStorage from '../components/in-storage.vue'

const useOpenInStorageDialog = () => {
  const inStorageOrderNo = ref('')
  const showInStorageDialog = ref(false)
  const inStorageRefs = ref<InstanceType<typeof inStorage> | null>(null)
  const openInStorageDialog = (orderNo: string) => {
    inStorageOrderNo.value = orderNo
    showInStorageDialog.value = true
  }
  return {
    inStorageOrderNo,
    showInStorageDialog,
    openInStorageDialog,
    inStorageRefs,
  }
}

export default useOpenInStorageDialog
