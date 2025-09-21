<script lang="ts" setup>
import { onMounted } from 'vue'
import Search from './components/search.vue'
import useShippedList from './hooks/useShippedList'
import usePreview from './hooks/usePreview'
import PageManageTwo from '@/components/PageManage.vue'
import QTable from '@/components/qszr-core/packages/q-table/QTable'
import QTableColumn from '@/components/qszr-core/packages/q-table/q-table-column.vue'
import { ShippedGoodsList } from './types'
import useShowViolationReason from './hooks/useShowViolationReason'
import ViolationReason from './components/violation-reason.vue'
import useConvert from '@/composables/useConvert'
import Preview from './components/preview.vue'
import QIcon from '@/components/q-icon/q-icon.vue'

const {
  tableList,
  multiSelect,
  searchCondition,
  computedSalePrice,
  computedSalePriceRange,
  dialogTableVisible,
  gridData,
  handleSearch,
  pagination,
  initList,
  computedSuplierStock,
  changeStatus,
  StatusMap,
  handleSizeChange,
  handleCurrentChange,
  handleContact,
  removeFromShelves,
  changeCommodityStatus,
  editHandle,
} = useShippedList()

onMounted(() => {
  initList()
})
const { divTenThousand } = useConvert()

// 引入违规原因查看的对应的展示逻辑
const { violationReason, showViolationReason, openViolationReasonDialog } = useShowViolationReason()
// 引入查看对应的数据和方法
const { currentRow, showPreviewDialog, openPreviewDialog } = usePreview()
</script>

<template>
  <Search @search="handleSearch" />
  <div class="tab_container" style="position: relative">
    <el-tabs :model-value="searchCondition.status" @update:model-value="changeStatus">
      <el-tab-pane name="" label="全部" />
      <el-tab-pane name="SELL_ON" label="已上架" />
      <el-tab-pane name="SELL_OFF" label="已下架" />
      <el-tab-pane name="PLATFORM_SELL_OFF" label="违规下架" />
    </el-tabs>
    <QIcon
      name="icon-wenhao"
      size="18px"
      color="#333"
      style="cursor: pointer; position: absolute; right: 20px; top: 25%"
      @click="dialogTableVisible = true"
    />
  </div>
  <div class="handle_container">
    <el-button
      v-show="['', 'SELL_OFF'].includes(searchCondition.status)"
      :disabled="multiSelect.length === 0"
      style="margin-right: 10px"
      round
      type="primary"
      @click="changeCommodityStatus(true, 'SELL_ON')"
      >批量上架</el-button
    >
    <el-button
      v-show="['', 'SELL_ON'].includes(searchCondition.status)"
      :disabled="multiSelect.length === 0"
      style="margin-right: 10px"
      round
      type="primary"
      @click="changeCommodityStatus(true, 'SELL_OFF')"
      >批量下架</el-button
    >
  </div>
  <QTable v-model:checkedItem="multiSelect" :data="tableList" selection no-border :style="{ overflowY: 'auto' }" class="table">
    <QTableColumn label="商品" width="350">
      <template #default="{ row }: { row: ShippedGoodsList }">
        <div class="commodity-info">
          <img :src="row.pic?.split(',')?.shift()" />
          <div class="commodity-info__main">
            <el-tooltip v-if="row.name.length > 30" effect="dark" :content="row.name" placement="top">
              <span>{{ row.name }}</span>
            </el-tooltip>
            <span v-else>{{ row.name }}</span>
          </div>
        </div>
      </template>
    </QTableColumn>
    <QTableColumn label="供货价" width="170">
      <template #default="{ row }">
        <span>￥{{ computedSalePrice(row) }}</span>
      </template>
    </QTableColumn>
    <QTableColumn label="供应商库存" width="100">
      <template #default="{ row }">{{ computedSuplierStock(row?.storageSkus) }}</template>
    </QTableColumn>
    <QTableColumn label="销售价" width="170">
      <template #default="{ row }">
        <span style="color: #f54319">￥{{ computedSalePriceRange(row) }}</span></template
      >
    </QTableColumn>
    <QTableColumn label="状态" width="120">
      <template #default="{ row }: { row: ShippedGoodsList }">
        <span
          v-if="['PLATFORM_SELL_OFF'].includes(row.status)"
          :style="{
            color: ['PLATFORM_SELL_OFF'].includes(row.status) ? '#F54319' : '#333',
          }"
          >{{ StatusMap[row?.status] }}</span
        >
        <el-switch
          v-else
          v-model="row.status"
          inline-prompt
          active-text="上架"
          :inactive-text="row.status === 'SUPPLIER_SELL_OFF' ? '供应商下架' : '下架'"
          active-value="SELL_ON"
          inactive-value="SELL_OFF"
          @click="removeFromShelves(false, row.status, row)"
        />
      </template>
    </QTableColumn>
    <QTableColumn label="供应商" width="140" prop="supplierName">
      <template #default="{ row }">
        <div class="customer">
          <div>
            <span class="customer__name">{{ row.supplierName }}</span>
            <QIcon name="icon-xiaoxi-copy" size="18px" style="cursor: pointer" svg @click="handleContact(row, 'supplierId')" />
          </div>
          <span>{{ row.supplierContractNumber }}</span>
        </div>
      </template>
    </QTableColumn>
    <QTableColumn label="铺货员" width="140">
      <template #default="{ row }">
        <div>
          <p>{{ row.extra.deliveryUserName }}</p>
          <p>{{ row.extra.deliveryUserPhone }}</p>
        </div>
      </template>
    </QTableColumn>
    <QTableColumn label="铺货时间" width="180" prop="createTime" />
    <QTableColumn label="操作" fixed="right" align="right" width="160">
      <template #default="{ row }: { row: ShippedGoodsList }">
        <div>
          <el-button v-if="row?.status !== 'PLATFORM_SELL_OFF'" link type="primary" @click="editHandle(row)">编辑</el-button>
          <el-button
            v-if="row?.status === 'PLATFORM_SELL_OFF'"
            link
            type="primary"
            size="small"
            @click="openViolationReasonDialog(row?.extra?.productViolation)"
            >违规原因</el-button
          >
          <el-button link type="primary" size="small" @click="openPreviewDialog(row)">查看 </el-button>
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
  <el-dialog v-model="showPreviewDialog" title="查看" width="1000px" destroy-on-close :close-on-click-modal="false">
    <Preview :currentProduct="currentRow" />
  </el-dialog>
  <el-dialog v-model="showViolationReason" title="违规原因" width="600px" destroy-on-close :close-on-click-modal="false">
    <ViolationReason :product-violation="violationReason" />
  </el-dialog>
  <el-dialog v-model="dialogTableVisible" title="代销商品各状态说明" center width="960px">
    <el-table :data="gridData" border :header-cell-style="{ background: '#F5F7FA', fontSize: '16px' }">
      <el-table-column prop="supplier" label="供应商端" width="200" />
      <el-table-column prop="shop" label="店铺端" width="140" />
      <el-table-column prop="address" label="说明">
        <template #default="{ row }">
          <!-- eslint-disable-next-line vue/no-v-html -->
          <div v-html="row.address"></div>
        </template>
      </el-table-column>
    </el-table>
  </el-dialog>
</template>

<style lang="scss" scoped>
* {
  font-size: 14px;
}
@include b(commodity-info) {
  display: flex;
  align-items: center;
  img {
    width: 68px;
    height: 68px;
  }
  span {
    display: inline-block;
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
    align-content: center;
  }
  @include e(name) {
    display: inline-block;
    max-width: 70px;
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
