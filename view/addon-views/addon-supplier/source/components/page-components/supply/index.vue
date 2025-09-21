<script lang="ts" setup>
import { ref, reactive, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import Search from './components/search.vue'
import PageManageTwo from '@/components/PageManage.vue'
import Purchase from './components/purchase.vue'
import BatchPurchase from './components/batch-purchase.vue'
import SuppplyDetails from './components/details.vue'
import { getPurchaseOrderCreationResult, getSupplierProductList, postAddContact, purchaseCreateOrder } from '../../../apis'
import type { ListInterface } from './types/list'
import useBatch from './hooks/useBatch'
import { ElMessage } from 'element-plus'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import ElTableEmpty from '@/components/element-plus/el-table/ElTableEmpty/index.vue'
import QIcon from '@/components/q-icon/q-icon.vue'

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
 * 渲染库存
 */
const renderLimited = (stock: string | boolean, row: any) => {
  if (!stock) return 0
  if (stock === 'UNLIMITED') return '无限库存'
  if (stock === 'STOCK') return computedSuplier.value(row?.storageSkus)
}

const detailsCurrentRow = ref<ListInterface>()
const showDetailsDialog = ref(false)
const tableRef = ref()
const multiSelect = computed(() => {
  return tableRef.value?.getSelectionRows?.() || []
})
const searchCondition = ref<any>({})
const tableList = ref<ListInterface[]>([])
const showPurchase = ref(false)
const $router = useRouter()
const $route = useRoute()
const pagination = reactive({
  page: { current: 1, size: 10 },
  total: 0,
})
if ($route.query.supplierGoodsName) {
  searchCondition.value.supplierGoodsName = $route.query.supplierGoodsName
}
const handleSearch = (condition: any = {}) => {
  searchCondition.value = condition
  pagination.page.current = 1
  initList()
}
const selectCommodityOptions = ref<ListInterface>()
const initList = async () => {
  const result = await getSupplierProductList<ListInterface>({ ...pagination.page, sellType: 'PURCHASE', ...searchCondition.value })
  if (result.data) {
    pagination.total = Number(result.data.total)
    tableList.value = result.data.records.map((item) => {
      return { ...item, disabled: !checkLimited(item) }
    })
  }
}
initList()

const handlePurchase = (row?: ListInterface) => {
  if (row) {
    selectCommodityOptions.value = {
      ...row,
      purchaseNum: 1,
      storageSkus:
        row.storageSkus
          ?.map((item) => ({ ...item, purchaseNum: 1 }))
          ?.filter((item) => {
            return (item.stockType === 'LIMITED' && Number(item.stock || 0) >= item.minimumPurchase) || item.stockType === 'UNLIMITED'
          }) || [],
    }
    showPurchase.value = true
  } else {
    if (multiSelect.value.length === 0) {
      ElMessage.error({ message: '请选择需要采购的商品' })
      return
    }
    openBatchDialog(
      multiSelect.value?.map((row) => ({
        ...row,
        storageSkus: row.storageSkus?.filter((item) => {
          return (item.stockType === 'LIMITED' && Number(item.stock || 0) > item.minimumPurchase) || item.stockType === 'UNLIMITED'
        }),
      })),
    )
  }
}
const handleContact = (row: ListInterface) => {
  postAddContact(useShopInfoStore().shopInfo.id, row.shopId).then(({ code }) => {
    if (code === 200) {
      $router.push({ path: '/message/customer/supplierService', query: { id: row.shopId } })
    }
  })
}
const computedSalePrice = computed(() => (row: ListInterface) => {
  const minPrice = Math.min(...row.storageSkus.map((i) => Number(i.salePrice)))
  const maxPrice = Math.max(...row.storageSkus.map((i) => Number(i.salePrice)))
  return minPrice === maxPrice ? maxPrice / 10000 : `${minPrice / 10000}~￥${maxPrice / 10000}`
})
const computedSuplier = computed(() => (storageSkus?: any[]) => {
  if (storageSkus?.[0]?.stockType === 'UNLIMITED') return '无限库存'
  return storageSkus?.reduce((prev, item) => (prev += Number(item.stock)), 0)
})
const purchaseRef = ref<InstanceType<typeof Purchase> | null>(null)
const purchaseConfirmLoading = ref(false)
const batchPurchaseRef = ref<InstanceType<typeof BatchPurchase> | null>(null)
const { showBatchDialog, openBatchDialog, batchPurchaseLines, batchDialogConfirmLoading } = useBatch()
let createCycle = 0
const handleConfirmPurchase = async () => {
  purchaseConfirmLoading.value = true
  createCycle = 0
  const data = purchaseRef.value?.getOrderConfig()
  if (!data?.receiver.mobile) {
    purchaseConfirmLoading.value = false
    return ElMessage.error('请选择收货人信息')
  }
  const result = await purchaseCreateOrder(data)
  if (result.code === 200) {
    if (result.data?.mainNo) {
      loopToCreationResult(result.data?.mainNo)
    }
  } else if (result.code === 210004) {
    // 商品库存不足
    ElMessage.error(result.msg)
    // 请求列表
    initList()
    // 关弹窗
    closeDialog()
  } else {
    ElMessage.error(result.msg || '采购失败')
  }
}

/**
 * 关弹窗
 */
const closeDialog = () => {
  showPurchase.value = false
  showBatchDialog.value = false
  purchaseConfirmLoading.value = false
  batchDialogConfirmLoading.value = false
  initList()
}

const loopToCreationResult = async (mainNo: string) => {
  createCycle++
  if (createCycle === 20) {
    purchaseConfirmLoading.value = false
    batchDialogConfirmLoading.value = false
    ElMessage.error('创建失败，请稍后重试')
    return
  }
  const creationResult = await getPurchaseOrderCreationResult(mainNo)
  console.log(creationResult)
  if (creationResult.data) {
    ElMessage.success('订单提交成功，请在采购订单中支付货款')
    closeDialog()
  } else {
    setTimeout(() => {
      loopToCreationResult(mainNo)
    }, 1000)
  }
}
const handleBatchPurchase = async () => {
  batchDialogConfirmLoading.value = true
  const data = batchPurchaseRef.value?.getOrderConfig()
  if (!data?.receiver.mobile) {
    batchDialogConfirmLoading.value = false
    return ElMessage.error('请选择收货人信息')
  }
  const result = await purchaseCreateOrder(data)
  if (result.code === 200) {
    if (result.data?.mainNo) {
      loopToCreationResult(result.data?.mainNo)
    }
  } else if (result.code === 210004) {
    // 商品库存不足
    ElMessage.error(result.msg)
    // 请求列表
    initList()
    // 关弹窗
    closeDialog()
  } else {
    ElMessage.error(result.msg || '采购失败')
  }
}
const handlePreviewDetails = (row: ListInterface) => {
  detailsCurrentRow.value = row
  showDetailsDialog.value = true
}
const handleSizeChange = (value: number) => {
  pagination.page.size = value
  initList()
}
const handleCurrentChange = (value: number) => {
  pagination.page.current = value
  initList()
}
</script>

<template>
  <Search @search="handleSearch" />
  <div style="display: flex; align-items: center; padding: 16px 16px 0 16px">
    <el-button type="primary" @click="handlePurchase()">一键采购</el-button>
    <span v-if="multiSelect.length" style="color: #999; margin-left: 8px">已选{{ multiSelect.length }}条&emsp;</span>
  </div>
  <div class="table_container">
    <el-table
      ref="tableRef"
      :data="tableList"
      :header-cell-style="{ background: '#F7F8FA', height: '48px', color: '#333' }"
      :cell-style="{ color: '#333333' }"
    >
      <template #empty>
        <ElTableEmpty />
      </template>
      <el-table-column type="selection" width="40" :selectable="checkLimited" fixed="left"> </el-table-column>
      <el-table-column label="商品" width="350">
        <template #default="{ row }">
          <div class="commodity-info">
            <img :src="row.albumPics?.split(',')?.shift()" />
            <div class="name fdc">
              <el-tooltip v-if="row?.productName.length >= 35" class="box-item" effect="dark" :content="row?.productName" placement="top">
                <p>{{ row?.productName }}</p>
              </el-tooltip>
              <p v-else>{{ row.productName }}</p>
              <div class="type_tag">{{ row.productType === 'REAL_PRODUCT' ? '实物商品' : '虚拟商品' }}</div>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="供货价" width="180">
        <template #default="{ row }">
          <span style="color: #f54319"> ￥{{ computedSalePrice(row) }}</span>
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
      <el-table-column label="所属供应商" prop="supplierName">
        <template #default="{ row }">
          <div class="customer">
            <div>
              <span class="customer__name">{{ row.supplierName }}</span>
              <QIcon name="icon-xiaoxi-copy" size="18px" style="cursor: pointer" svg @click="handleContact(row)" />
            </div>
            <span>{{ row.supplierContractNumber }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="210" fixed="right" align="right">
        <template #default="{ row }">
          <div>
            <el-button link type="primary" size="small" @click="handlePreviewDetails(row)">查看</el-button>
            <el-button link size="small" type="primary" :disabled="!checkLimited(row)" @click="handlePurchase(row)">一键采购</el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>
  </div>

  <PageManageTwo
    :page-size="pagination.page.size"
    :page-num="pagination.page.current"
    :total="pagination.total"
    @handle-size-change="handleSizeChange"
    @handle-current-change="handleCurrentChange"
  />
  <el-dialog v-model="showPurchase" :close-on-click-modal="false" destroy-on-close title="一键采购" width="1000px">
    <Purchase ref="purchaseRef" v-model:lines="selectCommodityOptions" />
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="showPurchase = false">取消</el-button>
        <el-button :loading="purchaseConfirmLoading" type="primary" @click="handleConfirmPurchase"> 确认 </el-button>
      </span>
    </template>
  </el-dialog>
  <!-- 批量一键采购 -->
  <el-dialog v-model="showBatchDialog" :close-on-click-modal="false" destroy-on-close title="一键采购" top="8vh" width="1000px">
    <BatchPurchase ref="batchPurchaseRef" v-model:lines="batchPurchaseLines" />
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="showBatchDialog = false">取消</el-button>
        <el-button :loading="batchDialogConfirmLoading" type="primary" @click="handleBatchPurchase"> 确认 </el-button>
      </span>
    </template>
  </el-dialog>
  <el-dialog v-model="showDetailsDialog" :close-on-click-modal="false" destroy-on-close title="商品详情" width="1000px">
    <SuppplyDetails :details-info="detailsCurrentRow" />
  </el-dialog>
</template>

<style lang="scss" scoped>
* {
  font-size: 14px;
}
@include b(tools) {
  padding: 16px;
}
@include b(table) {
  overflow: auto;
}
@include b(commodity-info) {
  display: flex;
  align-items: center;
  cursor: pointer;
  img {
    width: 60px;
    height: 60px;
  }
  p {
    width: 217px;
    overflow: hidden;
    text-overflow: ellipsis;
    display: box;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    margin-left: 10px;
  }
  .type_tag {
    color: rgb(153, 153, 153);
    font-size: 14px;
    font-weight: 400;
    margin-left: 10px;
  }
}
@include b(customer) {
  width: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  font-size: 14px;
  color: #333;
  div {
    display: flex;
    align-items: center;
  }
  @include e(name) {
    display: inline-block;
    max-width: 156px;
    margin-right: 5px;
    @include utils-ellipsis(1);
  }
  img {
    width: 16.25px;
    height: 15.32px;
    margin-left: 5px;
    cursor: pointer;
  }
}
</style>
