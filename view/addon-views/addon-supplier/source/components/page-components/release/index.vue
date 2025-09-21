<script lang="ts" setup>
import Search from './components/search.vue'
import usePreview from './hooks/usePreview'
import useRelease from './hooks/useRelease'
import PageManageTwo from '@/components/PageManage.vue'
import QTable from '@/components/qszr-core/packages/q-table/QTable'
import QTableColumn from '@/components/qszr-core/packages/q-table/q-table-column.vue'
import preview from './components/preview.vue'
import useShowViolationReason from './hooks/useShowViolationReason'
import ViolationReason from './components/violation-reason.vue'
// @ts-ignore
import QIcon from '@/components/q-icon/q-icon.vue'

// 已发布列表的数据管理和方法管理
const {
  handleSearch,
  releaseData,
  multiSelect,
  pagination,
  initData,
  handleSaleOn,
  searchType,
  changeStatus,
  handleSizeChange,
  handleCurrentChange,
  computedSalePrice,
  handleContact,
} = useRelease()
// 引入查看对应的数据和方法
const { currentRow, showPreviewDialog, openPreviewDialog } = usePreview()
// 引入违规原因查看的对应的展示逻辑
const { violationReason, showViolationReason, openViolationReasonDialog } = useShowViolationReason()

initData()
</script>

<template>
  <Search @search="handleSearch" />
  <div class="tab_container">
    <el-tabs :model-value="searchType.status" @update:model-value="changeStatus">
      <el-tab-pane label="全部" name="" />
      <el-tab-pane label="已上架" name="SELL_ON" />
      <el-tab-pane label="已下架" name="SELL_OFF" />
      <el-tab-pane label="违规下架" name="PLATFORM_SELL_OFF" />
    </el-tabs>
  </div>
  <div class="handle_container">
    <el-button
      v-show="['SELL_OFF', ''].includes(searchType.status)"
      :disabled="multiSelect.length === 0"
      style="margin-right: 10px"
      round
      @click="handleSaleOn(true, 'SELL_ON')"
      >批量上架</el-button
    >
    <el-button
      v-show="['SELL_ON', ''].includes(searchType.status)"
      :disabled="multiSelect.length === 0"
      style="margin-right: 10px"
      round
      @click="handleSaleOn(true, 'SELL_OFF')"
      >批量下架</el-button
    >
  </div>
  <QTable v-model:checkedItem="multiSelect" :data="releaseData" :style="{ overflowY: 'auto' }" :selection="true" no-border>
    <QTableColumn label="商品名称" width="360">
      <template #default="{ row }">
        <div class="commodity-info">
          <img :src="row?.pic" />
          <div class="commodity-info-text">
            <el-tooltip v-if="row?.name.length >= 30" class="box-item" effect="dark" :content="row?.name" placement="top">
              <p>
                <span v-if="row.productType === 'VIRTUAL_PRODUCT'" class="virtual_product">虚拟</span>
                {{ row?.name }}
              </p>
            </el-tooltip>
            <p v-else>
              <span v-if="row.productType === 'VIRTUAL_PRODUCT'" class="virtual_product">虚拟</span>
              {{ row?.name }}
            </p>
          </div>
        </div>
      </template>
    </QTableColumn>
    <QTableColumn label="价格" width="200">
      <template #default="{ row }">
        <span style="color: #f54319"> ￥{{ computedSalePrice(row) }}</span>
      </template>
    </QTableColumn>
    <QTableColumn label="库存" width="140">
      <template #default="{ row }">
        {{ row?.storageSkus?.reduce((pre: number, item: any) => pre + Number(item.stock), 0) }}
      </template>
    </QTableColumn>
    <QTableColumn label="状态" width="120">
      <template #default="{ row }">
        <span v-if="row?.delete">下架</span>
        <span v-else-if="row.status === 'PLATFORM_SELL_OFF'" style="color: #f54319">违规下架</span>
        <el-switch
          v-else
          v-model="row.status"
          style="--el-switch-on-color: #13ce66"
          active-value="SELL_ON"
          inactive-value="SELL_OFF"
          @click="handleSaleOn(false, row.status, row)"
        />
      </template>
    </QTableColumn>
    <QTableColumn label="所属供应商" prop="supplierName" width="140">
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
    <QTableColumn fixed="right" label="操作" align="right" width="140">
      <template #default="{ row }">
        <div>
          <el-button link size="small" type="primary" @click="openPreviewDialog(row)">查看</el-button>
          <el-button
            v-if="row?.status === 'PLATFORM_SELL_OFF'"
            link
            size="small"
            type="primary"
            @click="openViolationReasonDialog(row?.extra?.productViolation)"
            >违规原因
          </el-button>
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
  <el-dialog v-model="showPreviewDialog" :close-on-click-modal="false" destroy-on-close title="查看" width="1000px">
    <preview :current-product="currentRow" />
  </el-dialog>
  <el-dialog v-model="showViolationReason" :close-on-click-modal="false" destroy-on-close title="违规原因" width="600px">
    <ViolationReason :product-violation="violationReason" />
  </el-dialog>
</template>

<style lang="scss" scoped>
* {
  font-size: 14px;
}
@include b(commodity-info) {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  width: 100%;
  img {
    width: 68px;
    height: 68px;
  }
  .commodity-info-text {
    width: 217px;
    height: 68px;
    margin-left: 8px;
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
    max-width: 95px;
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
