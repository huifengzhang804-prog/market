<template>
  <div style="border: 1px solid #f9f9f9; border-radius: 10px">
    <el-table
      ref="tableRef"
      :data="tableList"
      row-key="id"
      :header-cell-style="{ background: '#F7F8FA', height: '48px', color: '#333' }"
      :cell-style="{ color: '#333333', height: '90px' }"
      :style="{ height: props.height, width: '100%' }"
      @selection-change="handleSelectionChange"
    >
      <template #empty>
        <ElTableEmpty />
      </template>
      <el-table-column v-if="!isShowPurchase" type="selection" width="40" :selectable="checkLimited" fixed="left"> </el-table-column>
      <el-table-column label="商品" width="370">
        <template #default="{ row }">
          <div class="commodity-info">
            <img :src="row.albumPics?.split(',')?.shift()" />
            <div class="commodity-info__right">
              <el-tooltip v-if="row?.productName.length >= 35" class="box-item" effect="dark" :content="row?.productName" placement="top">
                <p><text v-if="row.productType === 'VIRTUAL_PRODUCT'" class="virtual_product">虚拟</text>{{ row?.productName }}</p>
              </el-tooltip>
              <p v-else><text v-if="row.productType === 'VIRTUAL_PRODUCT'" class="virtual_product">虚拟</text>{{ row.productName }}</p>
              <span style="color: #f54319"> ￥{{ computedSalePrice(row) }}</span>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="供应商" prop="supplierName">
        <template #default="{ row }">
          <div class="customer">
            <div>
              <span class="customer__name">{{ row.supplierName }}</span>
            </div>
            <span>{{ row.supplierContractNumber }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="供应商库存" width="120">
        <template #default="{ row }">{{ renderLimited(checkLimited(row), row) }}</template>
      </el-table-column>
      <el-table-column label="自有库存" prop="shopOwnProductStockNum" width="100">
        <template #default="{ row }">
          {{ row?.storageSkus?.reduce((prev: number, sku: any) => prev + Number(sku?.shopOwnProductStockNum || 0), 0) }}
        </template>
      </el-table-column>
      <el-table-column v-if="isShowPurchase" align="center" label="采购数" width="180">
        <template #default="{ row }">
          <el-input-number
            v-if="row.storageSkus.length === 1"
            v-model="row.purchaseNum"
            :max="row?.stockType === 'UNLIMITED' ? +maxUnlimitedNum : +row.stock"
            :min="+1"
            placeholder="请输入"
            :precision="0"
          />
          <span v-else>{{ row.storageSkus.reduce((prev: number, sku: any) => prev + Number(sku?.purchaseNum || 0), 0) }}</span>
        </template>
      </el-table-column>
      <el-table-column v-if="isShowPurchase" label="小计" width="120">
        <template #default="{ row }">
          <span style="color: #f54319"
            >￥{{
              row.storageSkus.length > 1
                ? row.storageSkus.reduce((prev: number, sku: any) => prev + sku?.purchaseNum * sku?.salePrice, 0) / 10000
                : (row.purchaseNum * Math.max(...row.salePrices)) / 10000
            }}</span
          >
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" align="right" :width="isShowPurchase ? 180 : 120">
        <template #default="{ row, $index }">
          <div style="display: flex; align-items: center; justify-content: end">
            <el-button v-if="row.storageSkus.length > 1 && isShowPurchase" type="primary" link @click="handleSetPurchaseNum(row, $index)"
              >添加采购</el-button
            >
            <el-button link type="primary" @click="handlePreviewDetails(row)">查看</el-button>
            <el-button v-if="isShowPurchase" type="danger" link @click="handleRemovePurchaseGoods($index)">移除</el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { computed, PropType, ref, defineEmits, onMounted, nextTick, watch } from 'vue'
import ElTableEmpty from '@/components/element-plus/el-table/ElTableEmpty/index.vue'
import { ListInterface } from '../types/list'

const props = defineProps({
  height: {
    type: String,
    default: 'auto',
  },
  tableList: {
    type: Array as PropType<ListInterface[]>,
    default: () => [],
  },
  isShowPurchase: {
    type: Boolean,
    default: false,
  },
  defaultSelected: {
    type: Array as PropType<ListInterface[]>,
    default: () => [],
  },
})

const emitFn = defineEmits(['preview-details', 'remove-purchase-goods', 'set-purchase-num', 'selection-change'])

// 最大无限库存
const maxUnlimitedNum = 10000

const tableRef = ref()
// 选中数据
const multiSelect = computed(() => {
  return tableRef.value?.getSelectionRows?.() || []
})

// 处理选择变化
const handleSelectionChange = (selection: ListInterface[]) => {
  emitFn('selection-change', selection)
}

// 计算供货价
const computedSalePrice = computed(() => (row: ListInterface) => {
  const minPrice = Math.min(...row.storageSkus.map((i) => Number(i.salePrice)))
  const maxPrice = Math.max(...row.storageSkus.map((i) => Number(i.salePrice)))
  return minPrice === maxPrice ? maxPrice / 10000 : `${minPrice / 10000}~￥${maxPrice / 10000}`
})

// 计算供应商库存
const computedSuplier = computed(() => (storageSkus?: any[]) => {
  if (storageSkus?.[0]?.stockType === 'UNLIMITED') return '无限库存'
  return storageSkus?.reduce((prev, item) => (prev += Number(item.stock)), 0)
})

/**
 * 渲染库存
 */
const renderLimited = (stock: string | boolean, row: any) => {
  if (!stock) return 0
  if (stock === 'UNLIMITED') return '无限库存'
  if (stock === 'STOCK') return computedSuplier.value(row?.storageSkus)
}

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

/**
 * 设置默认选中
 */
const setDefaultSelection = () => {
  // 先清空之前的选择
  tableRef.value?.clearSelection()
  if (props.defaultSelected.length === 0) return
  // 遍历表格数据，找到匹配的项进行选中
  props.tableList.forEach((row) => {
    const shouldSelect = props.defaultSelected.some((selected) => selected.id === row.id)
    if (shouldSelect) {
      nextTick(() => {
        tableRef.value!.toggleRowSelection(row, true)
      })
    }
  })
}

// 监听表格数据
watch(
  () => props.tableList,
  () => {
    setDefaultSelection()
  },
)

/**
 * 查看详情
 */
const handlePreviewDetails = (row: ListInterface) => {
  emitFn('preview-details', row)
}

/**
 * 移除采购商品
 */
const handleRemovePurchaseGoods = (index: number) => {
  emitFn('remove-purchase-goods', index)
}

/**
 * 设置采购数
 */
const handleSetPurchaseNum = (row: ListInterface, index: number) => {
  emitFn('set-purchase-num', row, index)
}

defineExpose({
  multiSelect,
  tableRef,
})
</script>

<style scoped lang="scss">
@include b(commodity-info) {
  display: flex;
  align-items: center;
  cursor: pointer;
  img {
    width: 65px;
    height: 65px;
  }
  @include e(right) {
    height: 65px;
    width: 244px;
    margin-left: 10px;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    p {
      overflow: hidden;
      text-overflow: ellipsis;
      display: box;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
    }
  }
}
</style>
