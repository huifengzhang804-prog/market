<script lang="ts" setup>
import Search from './components/search.vue'
import { useRouter } from 'vue-router'
import useWaitingPublish from './hooks/useWaitingPublish'
import PageManageTwo from '@/components/PageManage.vue'
import QTable from '@/components/qszr-core/packages/q-table/QTable'
import QTableColumn from '@/components/qszr-core/packages/q-table/q-table-column.vue'
import preview from './components/preview.vue'
import usePreview from './hooks/usePreview'
import { ToBeReleaseList } from './types'
import { postAddContact } from '../../../apis'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import QIcon from '@/components/q-icon/q-icon.vue'

const { handleSearch, waitingPublishData, pagination, computedSalePrice, handleSizeChange, handleCurrentChange, initData } = useWaitingPublish()
const { currentRow, showPreviewDialog, openPreviewDialog } = usePreview()

initData()
const router = useRouter()
const handleRelease = (row: ToBeReleaseList) => {
  router.push({ path: '/goods/purchase/release', query: { id: row?.productId, supplierId: row?.supplierId, type: 'ProcurementRelease' } })
}
const handleContact = (row: ToBeReleaseList) => {
  postAddContact(useShopInfoStore().shopInfo.id, row.shopId).then(({ code }) => {
    if (code === 200) {
      router.push({ path: '/message/customer/supplierService', query: { id: row.supplierId } })
    }
  })
}
</script>

<template>
  <Search @search="handleSearch" />
  <QTable :data="waitingPublishData" no-border :style="{ overflowY: 'auto' }">
    <QTableColumn label="商品名称" width="320">
      <template #default="{ row }">
        <div class="commodity-info">
          <img :src="row?.image" />
          <div class="commodity-info-text">
            <el-tooltip v-if="row?.productName.length >= 30" class="box-item" effect="dark" :content="row?.productName" placement="top">
              <p>
                <span v-if="row?.productType === 'VIRTUAL_PRODUCT'" class="virtual_product">虚拟</span>
                {{ row?.productName }}
              </p>
            </el-tooltip>
            <p v-else>
              <span v-if="row?.productType === 'VIRTUAL_PRODUCT'" class="virtual_product">虚拟</span>
              {{ row?.productName }}
            </p>
          </div>
        </div>
      </template>
    </QTableColumn>
    <QTableColumn label="供货价" width="200">
      <template #default="{ row }">
        <span style="color: #f54319"> ￥{{ computedSalePrice(row) }}</span>
      </template>
    </QTableColumn>
    <QTableColumn label="采购数" prop="num" width="120" />
    <QTableColumn label="待发货数" prop="waitForDeliveryCount" width="120" />
    <QTableColumn label="已发货数" prop="hasDeliveryCount" width="120" />
    <QTableColumn label="入库数" prop="stockInCount" width="120" />
    <QTableColumn label="所属供应商" prop="supplierName" width="220">
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
    <QTableColumn align="right" label="操作" width="120" fixed="right">
      <template #default="{ row }">
        <div>
          <el-button link size="small" type="primary" @click="openPreviewDialog(row)">查看</el-button>
          <el-button link size="small" type="primary" @click="handleRelease(row)">发布</el-button>
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
}
@include b(commodity-info-text) {
  height: 68px;
  width: 217px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  margin-left: 8px;
  p {
    overflow: hidden;
    text-overflow: ellipsis;
    display: box;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
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
