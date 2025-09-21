<script lang="ts" setup>
import { computed, onMounted, defineProps, ref } from 'vue'
import useBatchPurchase from './hooks/useBatchPurchase'

const $props = withDefaults(defineProps<{ lines: any[] }>(), {
  lines: () => [],
})
const commodityList = computed({
  get() {
    return $props.lines
  },
  set(newVal) {
    $emits('update:lines', newVal)
  },
})
const currentTableData = computed(() =>
  commodityList.value.filter(
    (_, index) => index >= (paginationOptions.page - 1) * paginationOptions.pageSize && index < paginationOptions.page * paginationOptions.pageSize,
  ),
)

/**
 * 检查库存
 */
const checkLimited = (row: any) => {
  const unLimited = row.storageSkus.find((item) => item.stockType === 'UNLIMITED')
  if (unLimited) return 'UNLIMITED'
  const stock = row.storageSkus.some((item) => item.stock > 0)
  if (stock) return 'STOCK'
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

const {
  receiveList,
  fetchReceiveAddress,
  purchaseFormModel,
  expandRowKeys,
  expandOpen,
  handleChangePurchaseNum,
  minRowBatchNum,
  maxRowBatchNum,
  computedSalePrice,
  computedShopOwnProductStockNum,
  computedSuplier,
  changeRowBatchNum,
  freightTotal,
  totalNum,
  totalPrice,
  totalOrderPrice,
  getOrderConfig,
  handleRemoveBatch,
  handleRemoveSku,
  paginationOptions,
} = useBatchPurchase(commodityList)
const maxUnlimitedNum = 100000
const $emits = defineEmits(['update:lines'])
const sizeChange = (pageSize: number) => {
  paginationOptions.pageSize = pageSize
}
const currentChange = (page: number) => {
  paginationOptions.page = page
}
onMounted(() => {
  paginationOptions.total = commodityList.value.length
  fetchReceiveAddress()
})
const expandableTable = ref()
const handleRowClick = (row: any) => {
  expandableTable.value.toggleRowExpansion(row)
}
defineExpose({ getOrderConfig })
</script>

<template>
  <div class="oneClickProcurement">
    <el-form :show-message="false" class="oneClickProcurement__remark">
      <el-form-item label="收货人信息">
        <el-select v-model="purchaseFormModel.receiveId" style="width: 100%">
          <el-option
            v-for="receiver in receiveList"
            :key="receiver.id"
            :label="receiver.contactName + '  ' + receiver.contactPhone + '   ' + receiver.area.join('') + '   ' + receiver.address"
            :value="receiver.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="采购备注">
        <el-input v-model="purchaseFormModel.remark" placeholder="请输入采购备注(100字以内)" maxlength="100" type="textarea" />
      </el-form-item>
    </el-form>
    <el-table
      ref="expandableTable"
      :data="currentTableData"
      :expand-row-keys="expandRowKeys"
      row-key="id"
      max-height="450"
      @expand-change="expandOpen"
      @row-click="handleRowClick"
    >
      <el-table-column type="expand">
        <template #default="{ row, $index }">
          <div class="oneClickProcurement__expand">
            <div class="oneClickProcurement__form">
              <el-form>
                <el-form-item label="采购数(批量)">
                  <el-input-number
                    v-model="row.batchNum"
                    :max="maxRowBatchNum(row)"
                    :min="minRowBatchNum(row)"
                    :precision="0"
                    @change="(batchNum) => changeRowBatchNum(row.id, batchNum)"
                  />
                </el-form-item>
              </el-form>
            </div>
            <el-table :data="row.storageSkus" row-key="id">
              <el-table-column label="商品规格">
                <template #default="{ row: childRow }">{{ childRow?.specs?.join(',') || '单规格' }}</template>
              </el-table-column>
              <el-table-column label="供货价" width="130">
                <template #default="{ row: childRow }"> ￥{{ childRow?.salePrice / 10000 }}</template>
              </el-table-column>
              <el-table-column label="起批数" prop="minimumPurchase" width="80" />
              <el-table-column label="供应商库存" prop="stock" width="100">
                <template #default="{ row: childRow }">
                  {{ childRow?.stockType === 'UNLIMITED' ? '无限库存' : childRow.stock }}
                </template>
              </el-table-column>
              <el-table-column label="自有库存" prop="shopOwnProductStockNum" width="80">
                <template #default="{ row: childRow }">
                  {{ childRow?.shopOwnProductStockNum }}
                </template>
              </el-table-column>
              <el-table-column label="采购数" prop="purchaseNum">
                <template #default="{ row: childRow }">
                  <el-input-number
                    v-model="childRow.purchaseNum"
                    :max="childRow.stockType === 'UNLIMITED' ? +maxUnlimitedNum : +childRow.stock"
                    :min="+childRow.minimumPurchase"
                    :precision="0"
                    @change="handleChangePurchaseNum(row.id)"
                  />
                </template>
              </el-table-column>
              <el-table-column fixed="right" label="操作" width="80">
                <template #default="{ $index: skuIndex }">
                  <el-link type="danger" @click="handleRemoveSku(skuIndex, $index, row.id)">移出</el-link>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="供应商" prop="supplierName" />
      <el-table-column label="商品名称" width="250">
        <template #default="{ row }">
          <div class="oneClickProcurement__commodity">
            <img :src="row.albumPics?.split(',').shift()" />
            <span class="oneClickProcurement__commodity--name">{{ row.productName }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="供货价">
        <template #default="{ row }">￥{{ computedSalePrice(row) }}</template>
      </el-table-column>
      <el-table-column label="供应商库存">
        <template #default="{ row }">{{ renderLimited(checkLimited(row), row) }}</template>
      </el-table-column>
      <el-table-column label="自有库存" prop="shopOwnProductStockNum">
        <template #default="{ row }">{{ computedShopOwnProductStockNum(row) }}</template>
      </el-table-column>
      <el-table-column label="采购数" prop="totalPurchase" />
      <el-table-column label="操作">
        <template #default="{ $index }">
          <el-link type="danger" @click="handleRemoveBatch($index)">移出</el-link>
        </template>
      </el-table-column>
    </el-table>
    <div class="oneClickProcurement__pagination">
      <el-pagination
        :current-page="paginationOptions.page"
        :page-size="paginationOptions.pageSize"
        :page-sizes="[5, 10, 15, 20]"
        :total="paginationOptions.total"
        layout="total, prev, pager, next, sizes"
        @size-change="sizeChange"
        @current-change="currentChange"
      />
    </div>
    <div class="oneClickProcurement__total">
      <div class="oneClickProcurement__total--title">订单合计</div>
      <div class="oneClickProcurement__total--line">采购数量：{{ totalNum }}</div>
      <div class="oneClickProcurement__total--line">商品总价：{{ totalPrice }}</div>
      <div class="oneClickProcurement__total--line">运费：{{ freightTotal.toFixed(2) }}</div>
      <div class="oneClickProcurement__total--line">采购金额(应付款)：￥{{ totalOrderPrice }}</div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@include b(oneClickProcurement) {
  @include e(commodity) {
    display: flex;
    align-items: center;
    img {
      width: 60px;
      height: 60px;
      flex-shrink: 0;
    }
    span {
      overflow: hidden;
      text-overflow: ellipsis;
      display: box;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      margin-left: 10px;
    }
  }
  @include e(expand) {
    margin-left: 20px;
    margin-right: 20px;
    padding: 10px;
    border: 1px solid #efefef;
  }
  @include e(form) {
    display: flex;
    justify-content: flex-end;
  }
  @include e(pagination) {
    padding: 15px 0;
    @include flex(flex-end);
  }
  @include e(total) {
    display: flex;
    flex-direction: column;
    align-items: flex-end;
    line-height: 1.5;
    @include m(title) {
      font-size: 1.2em;
      font-weight: 600;
    }
  }
}

:deep(.el-table thead .el-table__cell) {
  background-color: var(--el-fill-color-light);
}
</style>
