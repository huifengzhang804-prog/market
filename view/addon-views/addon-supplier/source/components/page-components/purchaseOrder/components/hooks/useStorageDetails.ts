import { ref, reactive } from 'vue'
import { getInStorageDetails } from '../../../../../apis'
import { InStorageInterface } from '../types/instorage'

const useStorageDetails = ($props: any) => {
  const extraData = reactive({
    orderNo: '',
    supplierId: '',
    remark: '',
  })
  const tableData = ref<InStorageInterface['products']>([])
  const initialInstorageData = async () => {
    const result = await getInStorageDetails($props.orderNo)
    if (result.data) {
      tableData.value = result.data?.products
      Object.keys(extraData).forEach((key) => {
        const extraDataKey = key as keyof typeof extraData
        extraData[extraDataKey] = result?.data?.[key]
      })
    }
  }
  return {
    tableData,
    extraData,
    initialInstorageData,
  }
}

export default useStorageDetails
