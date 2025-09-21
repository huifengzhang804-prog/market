<script lang="ts" setup>
import PageManageTwo from '@/components/PageManage.vue'
import QTable from '@/components/qszr-core/packages/q-table/QTable'
import QTableColumn from '@/components/qszr-core/packages/q-table/q-table-column.vue'
import Search from './components/search.vue'
import preview from './components/preview.vue'
import batchDistrubution from './components/batch-distrubution.vue'
import usePreview from './hooks/usePreview'
import useGoodsSourceList from './hooks/useGoodsSourceList'
import useBatchDistribution from './hooks/useBatchDistribution'
import { doPostBatchDistribution } from '../../../apis'
import { ElMessage } from 'element-plus'
import { onMounted } from 'vue'
import QIcon from '@/components/q-icon/q-icon.vue'
import { debounce } from 'lodash-es'

const { previewData, showPreviewDialog, handlePreviewData } = usePreview()

/**
 * 渲染库存
 */
const renderLimited = (stock: string | boolean, row: any) => {
  if (!stock) return 0
  if (stock === 'UNLIMITED') return '无限库存'
  if (stock === 'STOCK') return computedSuplier.value(row?.storageSkus)
}

const {
  pagination,
  tableList,
  multiSelect,
  computedSalePrice,
  computedSuplier,
  handleSearch,
  checkLimited,
  initList,
  handleDistribution,
  handleSizeChange,
  handleCurrentChange,
  handleContact,
} = useGoodsSourceList()

onMounted(() => {
  initList()
})
const { showBatchDistributionDialog, openBatchDistributionDialog, batchDistrubutionRef, checkedDistributuion } = useBatchDistribution()
const handleConfirmBatchDistrubution = debounce(
  async () => {
    const resultData = await batchDistrubutionRef.value?.buildData()
    const { code, success, msg } = await doPostBatchDistribution(resultData)
    if (code === 200 && success) {
      ElMessage.success({ message: msg || '批量铺货成功' })
      initList()
      showBatchDistributionDialog.value = false
    } else {
      ElMessage.error({ message: msg || '批量铺货失败' })
    }
  },
  1500,
  {
    leading: true,
    trailing: false,
  },
)
</script>

<template>
  <Search @search="handleSearch" />
  <div class="handle_container" style="padding-top: 16px; display: flex; align-items: center; justify-content: space-between">
    <div>
      <el-button type="primary" @click="openBatchDistributionDialog(multiSelect)">一键铺货</el-button>
      <span v-if="multiSelect.length >= 1" style="color: #999; margin-left: 8px">已选{{ multiSelect.length }}条&emsp;</span>
    </div>
    <el-tooltip class="box-item" effect="dark" placement="right-start">
      <template #content>
        1、代销也叫一件代发，无需备货<br />
        2、代销商品发货及售后由供应商自行处理
      </template>
      <QIcon name="icon-wenhao" size="18px" style="cursor: pointer" />
    </el-tooltip>
  </div>
  <QTable v-model:checkedItem="multiSelect" :data="tableList" no-border :selection="true" class="table">
    <QTableColumn label="商品" width="400">
      <template #default="{ row }">
        <div class="commodity-info">
          <img :src="row.albumPics?.split(',')?.shift()" />
          <el-tooltip v-if="row.productName.length > 30" :content="row.productName" effect="dark" placement="top">
            <div class="cup productName">
              <span v-if="row?.productType === 'VIRTUAL_PRODUCT'" class="virtual_product">虚拟</span>
              {{ row.productName }}
            </div>
          </el-tooltip>
          <div v-else class="productName">
            <span v-if="row?.productType === 'VIRTUAL_PRODUCT'" class="virtual_product">虚拟</span>
            {{ row.productName }}
          </div>
        </div>
      </template>
    </QTableColumn>
    <QTableColumn label="供货价" width="210">
      <template #default="{ row }">
        <span style="color: #f54319">￥{{ computedSalePrice(row) }}</span></template
      >
    </QTableColumn>
    <QTableColumn label="供应商库存" width="160">
      <template #default="{ row }">{{ renderLimited(checkLimited(row), row) }}</template>
    </QTableColumn>
    <QTableColumn label="所属供应商" width="240" prop="supplierName">
      <template #default="{ row }">
        <div class="customer">
          <div>
            <span class="customer__name">{{ row.supplierName }}</span>
            <QIcon name="icon-xiaoxi-copy" size="18px" style="cursor: pointer" svg @click="handleContact(row)" />
          </div>
          <span>{{ row.supplierContractNumber }}</span>
        </div>
      </template>
    </QTableColumn>
    <QTableColumn label="操作" align="right" fixed="right" width="150">
      <template #default="{ row }">
        <div>
          <el-button link type="primary" size="small" @click="handlePreviewData(row)">查看</el-button>
          <el-button link type="primary" size="small" :disabled="!checkLimited(row)" @click="handleDistribution(row)">一键铺货 </el-button>
        </div>
      </template>
    </QTableColumn>
  </QTable>
  <PageManageTwo
    :page-size="pagination.page.size"
    :page-num="pagination.page.current"
    :total="pagination.total"
    @handle-size-change="handleSizeChange"
    @handle-current-change="handleCurrentChange"
  />
  <el-dialog v-model="showPreviewDialog" title="商品详情" width="650px" top="6vh">
    <preview :commodity-item="previewData" />
  </el-dialog>
  <el-dialog v-model="showBatchDistributionDialog" title="一键铺货" width="750px" destroy-on-close :close-on-click-modal="false">
    <batch-distrubution ref="batchDistrubutionRef" :batch-items="checkedDistributuion" />
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="showBatchDistributionDialog = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmBatchDistrubution">确认</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<style lang="scss" scoped>
* {
  font-size: 14px;
}
@include b(table) {
  overflow-y: auto;
}
@include b(commodity-info) {
  display: flex;
  align-items: center;
  img {
    width: 68px;
    height: 68px;
  }
  .productName {
    width: 217px;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    margin-left: 10px;
  }
}
@include b(customer) {
  width: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-around;
  font-size: 14px;
  color: #333;
  div {
    display: flex;
    align-items: center;
  }
  @include e(name) {
    display: inline-block;
    max-width: 156px;
    @include utils-ellipsis(1);
    margin-right: 3px;
  }
  img {
    width: 16.25px;
    height: 15.32px;
    margin-left: 5px;
    cursor: pointer;
  }
}
</style>
