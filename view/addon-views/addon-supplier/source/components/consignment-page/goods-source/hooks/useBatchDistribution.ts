import { ElMessage } from 'element-plus'
import { ListInterface } from '../../types/list'
import { cloneDeep } from 'lodash-es'
import batchDistrubution from '../components/batch-distrubution.vue'
import { ref } from 'vue'

const useBatchDistribution = () => {
  const batchDistrubutionRef = ref<InstanceType<typeof batchDistrubution> | null>(null)
  const showBatchDistributionDialog = ref(false)
  const checkedDistributuion = ref<ListInterface[]>([])
  const openBatchDistributionDialog = (lists: ListInterface[]) => {
    const filterList = lists.filter((item) => {
      const storageSkus = item?.storageSkus
      let totalNum = 0,
        isLimited = false
      storageSkus?.forEach((item) => {
        totalNum += Number(item.stock)
        if (item.stockType === 'LIMITED') isLimited = true
      })
      const stock = isLimited ? totalNum : '无限库存'
      return stock !== 0
    })
    if (filterList.length === 0) {
      return ElMessage.error({ message: '请选择铺货商品' })
    }
    checkedDistributuion.value = cloneDeep(filterList)
    showBatchDistributionDialog.value = true
  }
  return {
    batchDistrubutionRef,
    showBatchDistributionDialog,
    checkedDistributuion,
    openBatchDistributionDialog,
  }
}

export default useBatchDistribution
