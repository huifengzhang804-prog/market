<template>
  <div class="addPurchase">
    <el-form v-model="addPurchase.purchaseFormModel" style="width: 65%" label-width="100px">
      <el-form-item label="收货人信息:" :rules="{ required: true, message: '请选择收货人信息' }">
        <el-select v-model="addPurchase.purchaseFormModel.receiveId" style="width: 100%">
          <el-option
            v-for="receiver in receiveList"
            :key="receiver.id"
            :label="receiver.contactName + '  ' + receiver.contactPhone + '   ' + receiver.area.join('') + '   ' + receiver.address"
            :value="receiver.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="采购备注:">
        <el-input
          v-model="addPurchase.purchaseFormModel.remark"
          placeholder="请输入采购备注(100字以内)"
          :rows="3"
          maxlength="100"
          show-word-limit
          type="textarea"
        />
      </el-form-item>
      <el-form-item label="采购商品:">
        <el-link type="primary" :underline="false" @click="handleAddPurchaseGoods">添加商品</el-link>
      </el-form-item>
    </el-form>
    <div style="padding-left: 30px">
      <PurchasingGoodsTable
        :table-list="addPurchase.tableList"
        :is-show-purchase="true"
        @preview-details="handlePreviewDetails"
        @remove-purchase-goods="handleRemovePurchaseGoods"
        @set-purchase-num="handleSetPurchaseNum"
      />
    </div>

    <el-dialog v-model="showPurchase" :close-on-click-modal="false" destroy-on-close title="添加商品" width="1100px" top="6vh">
      <PurchasingGoods ref="purchasingGoodsRef" :default-selected="addPurchase.tableList" @preview-details="handlePreviewDetails" />
      <template #footer>
        <div style="display: flex; justify-content: center">
          <div>
            <el-button @click="showPurchase = false">取消</el-button>
            <el-button type="primary" @click="confirmAddPurchaseGoods">确定</el-button>
          </div>
        </div>
      </template>
    </el-dialog>

    <el-dialog v-model="showDetailsDialog" :close-on-click-modal="false" destroy-on-close title="商品详情" width="1000px">
      <SuppplyDetails :details-info="detailsCurrentRow" />
    </el-dialog>

    <el-dialog v-model="showSetPurchaseNum" :close-on-click-modal="false" destroy-on-close title="设置采购数" width="1100px">
      <div v-if="setPurchaseNumRow?.storageSkus && setPurchaseNumRow?.storageSkus?.length > 1" class="purchase__batch">
        <el-form :show-message="false" inline>
          <el-form-item label="批量设置：">
            <div style="display: flex; align-items: center">
              <el-input-number v-model="batchPurchaseNum" :controls="false" :max="+maxBatchNum" :min="1" style="width: 80px" placeholder="采购数" />
              <el-link style="width: 50px" type="primary" :underline="false" @click="changeBatchPurchaseNum(batchPurchaseNum)">填入</el-link>
            </div>
          </el-form-item>
        </el-form>
      </div>
      <el-table :data="setPurchaseNumRow?.storageSkus" :max-height="350" style="border: 1px solid #f9f9f9; border-radius: 10px 10px 0 0">
        <el-table-column label="商品规格" min-width="230">
          <template #default="{ row }">
            <div class="purchaseSku_specs">
              {{ row?.specs?.join(',') || '单规格' }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="供货价">
          <template #default="{ row }">
            <span style="color: #f54319">￥{{ (row?.salePrice / 10000).toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="供应商库存">
          <template #default="{ row }">
            {{ row?.stockType === 'UNLIMITED' ? '无限库存' : row.stock }}
          </template>
        </el-table-column>
        <el-table-column label="自有库存" prop="shopOwnProductStockNum" />
        <el-table-column label="起批数" prop="minimumPurchase" />
        <el-table-column label="采购数" width="140">
          <template #default="{ row }">
            <el-input-number
              v-model="row.purchaseNum"
              :max="row?.stockType === 'UNLIMITED' ? +maxUnlimitedNum : +row.stock"
              :min="+row.minimumPurchase"
              :controls="false"
              :precision="0"
              style="width: 80px"
              placeholder="请输入"
            />
          </template>
        </el-table-column>
        <el-table-column label="小计">
          <template #default="{ row }">
            <span style="color: #f54319">￥{{ (row?.purchaseNum * row?.salePrice) / 10000 }}</span>
          </template>
        </el-table-column>
        <el-table-column fixed="right" label="操作" align="center" width="80">
          <template #default="{ $index }">
            <el-button link type="danger" :disabled="setPurchaseNumRow?.storageSkus?.length === 1" @click="handleRemove($index)">移除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <div style="display: flex; justify-content: center">
          <el-button @click="showSetPurchaseNum = false">取消</el-button>
          <el-button type="primary" @click="confirmSetPurchaseNum">确定</el-button>
        </div>
      </template>
    </el-dialog>
    <div v-if="addPurchase.tableList.length > 0" style="float: right; margin-top: 10px">
      <div>
        <div class="purchase__total">
          <span class="purchase__total--title">采购数量：</span>
          <span class="purchase__total--value">{{ totalNum }}</span>
        </div>
        <div class="purchase__total">
          <span class="purchase__total--title">商品总价：</span>
          <span class="purchase__total--value">￥{{ totalPrice.toFixed(2) }}</span>
        </div>
        <div class="purchase__total">
          <span class="purchase__total--title">运费：</span>
          <span class="purchase__total--value">￥{{ freightTotal }}</span>
        </div>
        <div class="purchase__total">
          <span class="purchase__total--title">实收金额：</span>
          <span class="purchase__total--value" style="color: #f54319; font-size: 24px"
            ><text style="font-size: 14px">￥</text>{{ totalOrderPrice.toFixed(2) }}</span
          >
        </div>
      </div>
    </div>
  </div>
  <div class="purchase__footer">
    <el-button @click="handleCancelPurchase">取消</el-button>
    <el-button :loading="purchaseConfirmLoading" type="primary" @click="handleCreatePurchase">提交订单</el-button>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, reactive, ref, watch, WritableComputedRef } from 'vue'
import { doGetDeliveryAddress, getPurchaseOrderCreationResult, getSupplierDistributionCost, purchaseCreateOrder } from '../apis'
import PurchasingGoodsTable from '../components/page-components/supply/components/purchasingGoodsTable.vue'
import { ListInterface } from '../components/page-components/supply/types/list'
import PurchasingGoods from '../components/page-components/supply/components/purchasingGoods.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import SuppplyDetails from '../components/page-components/supply/components/details.vue'
import { useRouter } from 'vue-router'
import { AddressInfo } from '../types/supplier'

const router = useRouter()
interface AddPurchase {
  purchaseFormModel: {
    receiveId: string
    remark: string
  }
  tableList: ListInterface[]
}

// 添加采购单
const addPurchase = reactive<AddPurchase>({
  purchaseFormModel: {
    receiveId: '',
    remark: '',
  },
  tableList: [],
})
// 添加商品
const showPurchase = ref(false)
// 商品详情
const detailsCurrentRow = ref<ListInterface>()
// 商品详情弹窗
const showDetailsDialog = ref(false)

// 收货人信息
const receiveList = ref<AddressInfo[]>([])

// 采购商品
const purchasingGoodsRef = ref<InstanceType<typeof PurchasingGoods>>()

// 设置采购数
const showSetPurchaseNum = ref(false)

// 设置采购数行
const setPurchaseNumRow = ref<ListInterface>()

// 设置采购数行索引
const setPurchaseNumIndex = ref(0)

// 移除采购商品索引
const removePurchaseSkuIndex = ref(-1)

// 最大无限库存
const maxUnlimitedNum = 100000

// 批量采购数
const batchPurchaseNum = ref(1)

// 运费
const freightTotal = ref(0)

// 确认创建采购单loading
const purchaseConfirmLoading = ref(false)

// 总数量
const totalNum = computed(() => {
  return addPurchase.tableList.reduce((pre, item) => {
    if (item.storageSkus.length === 1) {
      return (pre += item.purchaseNum)
    } else {
      return (pre += item.storageSkus?.reduce((pre, item) => (pre += item.purchaseNum || 0), 0))
    }
  }, 0)
})

// 总价
const totalPrice = computed(() => {
  return addPurchase.tableList.reduce((pre, item) => {
    if (item.storageSkus.length === 1) {
      return (pre += item.storageSkus.length === 1 ? (Math.max(...item.salePrices) / 10000) * item.purchaseNum : 0)
    } else {
      return (pre += item.storageSkus?.reduce((pre, item) => (pre += item.purchaseNum ? (+item.salePrice / 10000) * item.purchaseNum : 0), 0))
    }
  }, 0)
})

// 总价
const totalOrderPrice = computed(() => {
  return totalPrice.value + freightTotal.value
})

// 监听运费计算
watch(
  () => totalNum.value,
  () => getFreightCount(),
)

// 获取收货人信息
const fetchReceiveAddress = async () => {
  const { data, code } = await doGetDeliveryAddress()
  if (code === 200 && data && data.records) {
    receiveList.value = data.records
    addPurchase.purchaseFormModel.receiveId = data.records.find((item: AddressInfo) => item?.defReceive === 'YES')?.id || ''
  }
}
fetchReceiveAddress()

// 查看详情
const handlePreviewDetails = (row: ListInterface) => {
  detailsCurrentRow.value = row
  showDetailsDialog.value = true
}

// 移除采购商品
const handleRemovePurchaseGoods = (index: string) => {
  addPurchase.tableList.splice(Number(index), 1)
}

// 添加采购
const handleSetPurchaseNum = (row: any, index: number) => {
  showSetPurchaseNum.value = true
  setPurchaseNumRow.value = {
    ...row,
    storageSkus: row.storageSkus.map((item: any) => ({ ...item, purchaseNum: 1 })),
  }
  setPurchaseNumIndex.value = index
}

// 移除采购商品
const handleRemove = (index: number) => {
  //防止连续点击
  setPurchaseNumRow.value?.storageSkus?.splice(index, 1)
  removePurchaseSkuIndex.value = index
}

// 确认设置采购数
const confirmSetPurchaseNum = () => {
  if (removePurchaseSkuIndex.value !== -1) {
    if (setPurchaseNumRow.value?.storageSkus) {
      addPurchase.tableList[setPurchaseNumIndex.value].storageSkus = [...setPurchaseNumRow.value.storageSkus]
    }
  }
  addPurchase.tableList[setPurchaseNumIndex.value]?.storageSkus?.forEach((item: any, index: number) => {
    if (setPurchaseNumRow.value?.storageSkus?.[index]) {
      item.purchaseNum = setPurchaseNumRow.value.storageSkus[index].purchaseNum
    }
  })

  showSetPurchaseNum.value = false
}

// 添加采购商品
const handleAddPurchaseGoods = () => {
  if (!addPurchase.purchaseFormModel.receiveId) {
    ElMessage.warning('请选择收货人信息')
    return
  }
  showPurchase.value = true
}

// 确认添加采购商品
const confirmAddPurchaseGoods = () => {
  const tableList = purchasingGoodsRef.value?.purchasingGoodsTableRef?.multiSelect
  addPurchase.tableList = tableList.map((item: any) => ({
    ...item,
    purchaseNum: item.storageSkus.length === 1 ? item.storageSkus[0].minimumPurchase || 1 : 1,
    storageSkus: item.storageSkus.length > 1 ? item.storageSkus.map((sku: any) => ({ ...sku, purchaseNum: 1 })) : item.storageSkus,
  }))
  batchPurchaseNum.value = 1
  showPurchase.value = false
}

// 最大批量采购数
const maxBatchNum = computed(() => {
  const lists =
    setPurchaseNumRow.value?.storageSkus?.map((item: any) => (item?.stockType === 'UNLIMITED' ? maxUnlimitedNum : Number(item?.stock || 0))) || []
  return Math.min(...lists)
})

// 批量采购数
const changeBatchPurchaseNum = (num: number) => {
  setPurchaseNumRow.value?.storageSkus?.forEach((item) => (item.purchaseNum = +num))
}

/**
 * 获取运费
 */
const getFreightCount = async () => {
  const receiveInfo = receiveList.value.find((item) => item.id === addPurchase.purchaseFormModel.receiveId)
  console.log(addPurchase.tableList, 'receiveInfo')
  if (!receiveInfo && addPurchase.tableList.length === 0) return
  let freightCount = 0
  try {
    const result = await getSupplierDistributionCost({
      area: receiveInfo?.area,
      freeRight: false,
      distributionMode: ['EXPRESS'],
      address: receiveInfo?.address,
      shopFreights: addPurchase.tableList.map((item) => {
        return {
          shopId: item.shopId,
          freights: [
            {
              templateId: item.freightTemplateId,
              skuInfos: item.storageSkus?.map((sku: any) => ({
                price: sku.salePrice,
                skuId: sku.id,
                num: sku.purchaseNum || 0,
                weight: sku.weight,
              })),
            },
          ],
        }
      }),
    })
    const costTotal: number[] = Object.values(result?.data?.EXPRESS || {})
    freightCount = costTotal.reduce((pre, item) => (pre += item), 0)
  } finally {
    freightTotal.value = freightCount
  }
}

// 构建收货人
const buildReceiver = (receiverList: any[], checkedReceiverId = '') => {
  const checkedReceiver = receiverList.find((item) => item.id === checkedReceiverId)
  if (checkedReceiver) {
    return {
      name: checkedReceiver.contactName,
      mobile: checkedReceiver.contactPhone,
      area: checkedReceiver.area,
      address: checkedReceiver?.address,
    }
  } else {
    return {
      name: '',
      mobile: '',
      area: [],
      address: '',
    }
  }
}

const getOrderConfig = () => {
  const receiver = buildReceiver(receiveList.value, addPurchase.purchaseFormModel.receiveId)
  const sellType = 'PURCHASE'
  const remark = addPurchase.purchaseFormModel.remark
  const suppliers = buildSupplier(addPurchase.tableList)

  return {
    receiver,
    sellType,
    remark,
    suppliers,
  }
}

// 构建供应商
const buildSupplier = (tableList: ListInterface[]) => {
  const supplierList: any[] = []
  tableList.forEach((list) => {
    let isExistSupplierIndex = supplierList.findIndex((supplier) => supplier.supplierId === list.shopId)
    if (isExistSupplierIndex === -1) {
      isExistSupplierIndex = supplierList.push({ supplierId: list.shopId, productSkus: {} as any }) - 1
    }
    list.storageSkus?.forEach((sku) => {
      supplierList[isExistSupplierIndex].productSkus[list.id]
      if (!supplierList[isExistSupplierIndex].productSkus[list.id]) {
        supplierList[isExistSupplierIndex].productSkus[list.id] = []
      }
      supplierList[isExistSupplierIndex].productSkus[list.id].push({ skuId: sku.id, num: sku.purchaseNum || list.purchaseNum || 1 })
    })
  })
  return supplierList.filter((item) => Object.keys(item?.productSkus).length > 0)
}

let createCycle = 0
// 确认创建采购单
const handleCreatePurchase = async () => {
  if (totalNum.value === 0) {
    ElMessage.warning('请选择采购商品')
    return
  }
  purchaseConfirmLoading.value = true
  createCycle = 0
  const result = await purchaseCreateOrder(getOrderConfig())
  if (result.code === 200) {
    if (result.data?.mainNo) {
      loopToCreationResult(result.data?.mainNo)
    }
  } else if (result.code === 210004) {
    // 商品库存不足
    ElMessage.error(result.msg)
  } else {
    purchaseConfirmLoading.value = false
    ElMessage.error(result.msg || '采购失败')
  }
}

// 取消采购
const handleCancelPurchase = () => {
  ElMessageBox.confirm('取消采购数据不可恢复，确定取消吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    router.push('/goods/purchase')
  })
}

// 循环获取创建结果
const loopToCreationResult = async (mainNo: string) => {
  createCycle++
  if (createCycle === 20) {
    purchaseConfirmLoading.value = false
    ElMessage.error('创建失败，请稍后重试')
    return
  }
  const creationResult = await getPurchaseOrderCreationResult(mainNo)
  if (creationResult.data) {
    purchaseConfirmLoading.value = false
    ElMessage.success('订单提交成功，请在采购订单中支付货款')
  } else {
    setTimeout(() => {
      loopToCreationResult(mainNo)
    }, 1000)
  }
  router.push('/goods/purchase')
}
</script>

<style lang="scss" scoped>
.addPurchase {
  padding: 30px 16px 65px 16px;
  overflow-y: auto;

  .purchase__batch {
    height: 63px;
    display: flex;
    align-items: center;
    background-color: #f4f4f4;
    margin-bottom: 10px;
    padding: 0 12px;
    :deep(.el-form--inline .el-form-item) {
      margin-bottom: 0;
    }
  }

  .purchase__total {
    width: 200px;
    color: #666;
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 16px;
    line-height: 32px;

    &--title {
      width: 80px;
      text-align: right;
    }

    &--value {
      width: 120px;
      text-align: right;
    }
  }
}

.purchaseSku_specs {
  width: 260px;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.purchase__footer {
  display: flex;
  justify-content: center;
  align-items: center;
  position: absolute;
  bottom: 0;
  width: 100%;
  height: 52px;
  background-color: #fff;
  box-shadow: 0 0 10px 0 rgba(0, 0, 0, 0.1);
  z-index: 100;
}
</style>
