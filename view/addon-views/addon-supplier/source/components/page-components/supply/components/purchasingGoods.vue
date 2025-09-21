<template>
  <div>
    <Search @search="handleSearch" />
    <PurchasingGoodsTable
      ref="purchasingGoodsTableRef"
      :table-list="tableList"
      :default-selected="defaultSelected"
      :height="'550px'"
      @preview-details="handlePreviewDetails"
      @selection-change="handleSelectionChange"
    />
    <PageManageTwo
      :page-size="pagination.page.size"
      :page-num="pagination.page.current"
      :total="pagination.total"
      @handle-size-change="handleSizeChange"
      @handle-current-change="handleCurrentChange"
    />
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, defineEmits, PropType, nextTick, onMounted } from 'vue'
import Search, { SearchInterface } from './search.vue'
import PurchasingGoodsTable from './purchasingGoodsTable.vue'
import { getSupplierProductList } from '../../../../apis'
import { ListInterface } from '../types/list'
import PageManageTwo from '@/components/PageManage.vue'

const $props = defineProps({
  defaultSelected: {
    type: Array as PropType<ListInterface[]>,
    default: () => [],
  },
})

const emit = defineEmits(['preview-details'])
const pagination = reactive({
  page: { current: 1, size: 10 },
  total: 0,
})
const tableList = ref<ListInterface[]>([])
const selectedItems = ref<ListInterface[]>([])
const isInitializing = ref(true)

// 采购商品表
const purchasingGoodsTableRef = ref<InstanceType<typeof PurchasingGoodsTable>>()

// 处理选择变化
const handleSelectionChange = (selection: ListInterface[]) => {
  if (!isInitializing.value) {
    selectedItems.value = selection
  }
}

// 采购商品表
const handleSearch = (condition: SearchInterface) => {
  pagination.page.current = 1
  initList(condition)
}

const initList = async (condition?: SearchInterface) => {
  const result = await getSupplierProductList<ListInterface>({ ...pagination.page, sellType: 'PURCHASE', ...condition })
  if (result.data) {
    pagination.total = Number(result.data.total)
    tableList.value = result.data.records.map((item) => {
      return { ...item, disabled: !checkLimited(item), purchaseNum: item.storageSkus?.length || 1 }
    })

    // 在下一个 tick 中设置选中状态
    nextTick(() => {
      if (purchasingGoodsTableRef.value?.tableRef) {
        tableList.value.forEach((row) => {
          const isSelected = selectedItems.value.some((selected) => selected.id === row.id)
          if (isSelected) {
            purchasingGoodsTableRef.value?.tableRef?.toggleRowSelection(row, true)
          }
        })
        isInitializing.value = false
      }
    })
  }
}

// 初始化时设置标志
onMounted(() => {
  isInitializing.value = true
  initList()
})

/**
 * 检查库存
 */
const checkLimited = (row: ListInterface, index?: number): boolean => {
  if (!row.storageSkus) {
    return false
  }
  const unLimited = row.storageSkus.find((item) => item.stockType === 'UNLIMITED')
  if (unLimited) return 'UNLIMITED' as unknown as true
  const stock = row.storageSkus.some((item) => Number(item.stock) > 0)
  if (stock) return 'STOCK' as unknown as true
  return false
}

const handleSizeChange = (value: number) => {
  pagination.page.size = value
  isInitializing.value = true
  initList()
}

const handleCurrentChange = (value: number) => {
  pagination.page.current = value
  isInitializing.value = true
  initList()
}

const handlePreviewDetails = (row: ListInterface) => {
  emit('preview-details', row)
}

defineExpose({
  purchasingGoodsTableRef,
})
</script>

<style scoped></style>
