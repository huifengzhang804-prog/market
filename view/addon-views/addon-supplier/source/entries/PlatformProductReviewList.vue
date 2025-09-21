<script lang="ts" setup>
import useExamineListHooks from '../components/hooks/useExamineListHooks'
import usePreviewExamineDetails from '../components/hooks/usePreviewExamineDetails'
import QTable from '@/components/qszr-core/packages/q-table/QTable'
import QTableColumn from '@/components/qszr-core/packages/q-table/q-table-column.vue'
import PageManage from '@/components/BetterPageManage/BetterPageManage.vue'
import CommodityDetails from '../components/supplier/commodityDetail.vue'
import AuditGoods from '../components/audit-goods.vue'
import SchemaForm from '@/components/SchemaForm.vue'
import ElTableEmpty from '@/components/element-plus/el-table/ElTableEmpty/index.vue'
import QIcon from '@/components/q-icon/q-icon.vue'

const {
  getSearch,
  handleReset,
  currentTab,
  goodsStatus,
  handleTabClick,
  handleBatchExamine,
  tableList,
  salePriceRange,
  ExamineGoodsEnum,
  initList,
  selectItems,
  handleAuditGoods,
  showAuditDialog,
  auditGoodsRefs,
  handleConfirmAudit,
  handleCloseAuditDialog,
  handleCurrentChange,
  handleSizeChange,
  sortTableList,
  searchParams,
  columns,
  formModel,
  visible,
  refuseVisible,
} = useExamineListHooks()

const { commodityInfo, previewVisible, handlePreviewExamineDetails } = usePreviewExamineDetails()
</script>

<template>
  <div class="q_plugin_container f1 overh">
    <el-config-provider :empty-values="[undefined, null]">
      <SchemaForm v-model="searchParams" :columns="columns" @searchHandle="getSearch" @handleReset="handleReset"> </SchemaForm>
    </el-config-provider>

    <el-tabs v-model="currentTab" style="padding: 0 16px 0 16px" @tab-change="handleTabClick">
      <el-tab-pane v-for="(item, key) in goodsStatus" :key="item" :label="key" :name="item"></el-tab-pane>
    </el-tabs>
    <div class="handle_container">
      <el-button v-if="currentTab === 'UNDER_REVIEW'" type="primary" @click="handleBatchExamine">批量审核</el-button>
    </div>

    <q-table
      v-model:checked-item="selectItems"
      :data="tableList.goods"
      no-border
      class="table"
      :selection="currentTab === 'UNDER_REVIEW' ? true : false"
      @change-sort="sortTableList"
    >
      <template #noData>
        <ElTableEmpty imageIndex="9" text="暂无商品" />
      </template>
      <q-table-column label="商品" align="left" width="300">
        <template #default="{ row }">
          <div class="commodity-info">
            <el-image :src="row?.pic" :alt="row?.name" style="width: 68px; height: 68px; flex-shrink: 0" />
            <div class="commodity-info__main">
              <el-tooltip v-if="row.name.length >= 30" effect="dark" :content="row.name" placement="top-start">
                <span class="commodity-info__main--name"
                  ><span v-if="row.productType === 'VIRTUAL_PRODUCT'" class="virtual_product">虚拟</span>{{ row?.name }}</span
                >
              </el-tooltip>
              <span v-else><span v-if="row.productType === 'VIRTUAL_PRODUCT'" class="virtual_product">虚拟</span>{{ row?.name }}</span>
              <span class="commodity-info__main--price">￥{{ salePriceRange(row?.salePrices) }}</span>
            </div>
          </div>
        </template>
      </q-table-column>
      <q-table-column key="shopName" label="所属供应商" align="left" width="220" prop="shopName">
        <template #default="{ row }">
          <el-tooltip v-if="row.shopName.length >= 20" effect="dark" :content="row.shopName" placement="top-start" class="transparent-tooltip"
            ><span :key="row.id" class="shop-name">{{ row.shopName }}</span></el-tooltip
          >
          <span v-if="row.shopName.length < 20" :key="row.id" class="shop-name">{{ row.shopName }}</span>
        </template>
      </q-table-column>
      <q-table-column label="状态" align="left" width="100">
        <template #default="{ row }">
          <span :style="{ color: row.auditStatus === 'UNDER_REVIEW' ? '#FD9224' : row.auditStatus === 'ALREADY_PASSED' ? '#00BB2C' : '#999' }">
            {{ ExamineGoodsEnum[row?.auditStatus as keyof typeof ExamineGoodsEnum] }}
          </span>
        </template>
      </q-table-column>
      <q-table-column label="提交时间" align="left" prop="submitTime" width="130" />
      <q-table-column v-if="currentTab !== 'UNDER_REVIEW'" label="审核时间" align="left" prop="auditTime" width="130" />
      <q-table-column label="操作" align="right" width="140" fixed="right">
        <template #default="{ row }">
          <el-link type="primary" @click="handlePreviewExamineDetails(row)">查看</el-link>
          <!-- <el-link v-if="row?.auditStatus === 'UNDER_REVIEW'" type="primary" @click="handleAuditGoods([row])">审核</el-link> -->
          <el-link v-if="row?.auditStatus === 'UNDER_REVIEW'" type="primary" @click="handleAuditGoods([row], true)">通过</el-link>
          <el-link v-if="row?.auditStatus === 'UNDER_REVIEW'" type="danger" @click="handleAuditGoods([row], false)">拒绝</el-link>
          <el-tooltip v-if="row?.auditStatus === 'REFUSE'" class="box-item" effect="dark" :content="row.explain" placement="bottom-end">
            <el-link type="primary">拒绝原因</el-link>
          </el-tooltip>
        </template>
      </q-table-column>
    </q-table>
    <PageManage
      :page-num="tableList.page.current"
      :page-size="tableList.page.size"
      load-init
      :total="tableList.total"
      @handle-current-change="handleCurrentChange"
      @handle-size-change="handleSizeChange"
      @reload="initList"
    />
    <el-dialog v-model="previewVisible" title="商品详情" center width="900px" destroy-on-close top="3vh">
      <CommodityDetails :commodity-id="commodityInfo.id" :shop-id="commodityInfo.shopId" />
    </el-dialog>
    <el-dialog v-model="showAuditDialog" title="商品审核" destroy-on-close @close="handleCloseAuditDialog">
      <AuditGoods ref="auditGoodsRefs" />
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showAuditDialog = false">取消</el-button>
          <el-button type="primary" @click="handleConfirmAudit">确认</el-button>
        </span>
      </template>
    </el-dialog>
    <el-dialog v-model="visible" :show-close="false" width="500">
      <template #header="{ close, titleId, titleClass }">
        <div class="my-header">
          <h4 :id="titleId" :class="titleClass" style="display: flex; align-items: center">
            <QIcon name="icon-tishi1-copy" svg size="24" /><span style="margin-left: 5px; font-size: 16px">商品审核</span>
          </h4>
          <QIcon size="18" name="icon-guanbi1" @click="close" />
        </div>
      </template>
      <div style="width: 100%; display: flex; justify-content: center; padding: 10px 0">
        <span style="width: 80%; color: #4e5969">商品审核通过后商品将处于上架状态，确定通过审核吗？</span>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="visible = false">取消</el-button>
          <el-button type="primary" @click="handleConfirmAudit('SELL_ON')"> 确认 </el-button>
        </div>
      </template>
    </el-dialog>
    <el-dialog v-model="refuseVisible" title="商品审核" width="690">
      <el-form ref="formRef" :model="formModel">
        <el-form-item label="拒绝原因" prop="explain">
          <el-input v-model="formModel.explain" placeholder="请输入拒绝原因" :maxlength="20" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="refuseVisible = false">取消</el-button>
          <el-button type="primary" @click="handleConfirmAudit('REFUSE', formModel.explain)"> 确认 </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style lang="scss" scoped>
* {
  font-size: 14px;
}
.my-header {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  gap: 16px;
}
@include b(table) {
  overflow-y: auto;
  transition: height 0.5s;
}
@include b(commodity-info) {
  display: flex;
  @include e(main) {
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    margin-left: 8px;
    width: 225px;
    height: 68px;
    @include m(name) {
      word-break: break-all;
      overflow: hidden;
      text-overflow: ellipsis;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
    }
    @include m(price) {
      color: #ff7417;
    }
  }
}
@include b(el-link) {
  margin-right: 8px;
}
@include b(btns) {
  padding-bottom: 8px;
}
@include b(shop-name) {
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}
.no_data {
  margin-top: 150px;
  img {
    width: 246px;
    height: 154px;
  }
  .cont {
    color: #737b80;
    text-align: center;
    margin-top: 40px;
  }
}
</style>
