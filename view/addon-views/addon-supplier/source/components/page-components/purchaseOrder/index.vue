<script lang="ts" setup>
import Search from './components/search.vue'
import PageManageTwo from '@/components/PageManage.vue'
import { QUERYORDERSTATUS, queryOrderStatus } from './helper/constant'
import countdown from '../../count-down/index.vue'
import useShowProof from './hooks/useShowProof'
import payOrder from './components/pay-order.vue'
import invoiceDialogMain from './components/invoice-dialog-main.vue'
import InStorage from './components/in-storage.vue'
import StorageDetails from './components/storage-details.vue'
import usePurchaseOrderList from './hooks/usePurchaseOrderList'
import useConvert from '@/composables/useConvert'
import { doPostSupplierExport } from '../../../apis'
import { ElMessage } from 'element-plus'
import { ArrowDown } from '@element-plus/icons-vue'
// @ts-ignore-next-line
// @ts-ignore-next-line
import paymentVoucher from '@/assets/images/icon/paymentVoucher.png'
import ElTableEmpty from '@/components/element-plus/el-table/ElTableEmpty/index.vue'
import { ApiPurchaseOrder } from '../../..'
import QIcon from '@/components/q-icon/q-icon.vue'
import { useRouter } from 'vue-router'

const {
  handleTabChange,
  pagination,
  orderDataList,
  chooseList,
  multipleTableRef,
  handleQuickSearchCommand,
  handleSearch,
  quickSearchTabName,
  quickSearchTabNames,
  activeTabName,
  getMainOrderStatusText,
  computedBtnList,
  handleDispatchEvent,
  computedCalculateFreight,
  showPayDialog,
  showInvoiceDialog,
  handlePayOrder,
  payOrderRef,
  payTypeMap,
  currentRow,
  showInStorageDialog,
  inStorageOrderNo,
  storageDetailsOrderNo,
  showStorageDetailsDialog,
  handleConfirmInStorage,
  inStorageRefs,
  InvoiceStatusHander,
  invoiceDetail,
  handleInvoiceConfig,
  handleCloseInvoiceDialog,
  invoiceSetType,
  multiSelect,
  handleSizeChange,
  handleCurrentChange,
  handleSelectionChange,
  initOrderList,
} = usePurchaseOrderList()

enum INVOICE_STATUS {
  // 待开票
  INVOICE_NOT_START = '待开票',
  // 申请开票处理中
  REQUEST_IN_PROCESS = '开票中',
  //发票申请失败
  FAILED_INVOICE_REQUEST = '开票失败',
  //  已经成功开票
  SUCCESSFULLY_INVOICED = '开票成功',
  // 客户取消开票
  CLIENT_CANCEL_REQUEST = '撤销开票',
}

const { divTenThousand } = useConvert()
const { showProof, goToShowProof, currentProof } = useShowProof()
const router = useRouter()
const statusSearchTabs: any[][] = []

const initialTabs = () => {
  Object.keys(queryOrderStatus).forEach((key: string) => {
    statusSearchTabs.push([key, queryOrderStatus[key as keyof typeof QUERYORDERSTATUS]])
  })
}
initialTabs()
initOrderList()
const supplierExport = async (val: any) => {
  if (chooseList.value.length) {
    let exportOrderIds = []
    exportOrderIds = chooseList.value.map((item) => item.mainNo)
    const { data, code, msg } = await doPostSupplierExport({ exportOrderIds })
    if (code !== 200) return ElMessage.error(msg || '导出数据失败')
    return ElMessage.success('导出数据成功')
  } else {
    let param = {
      no: val.no,
      supplierId: val.supplierId,
      startTime: val.date?.[0],
      endTime: val.date?.[1],
      status: activeTabName.value,
      exportOrderIds: '',
    }
    param.status = param.status.trim()
    const { data, code, msg } = await doPostSupplierExport(param)
    if (code !== 200) return ElMessage.error(msg || '导出数据失败')
    return ElMessage.success('导出数据成功')
  }
}
</script>

<template>
  <Search @search="handleSearch" @supplier-export="supplierExport" />
  <div class="tab_container" style="position: relative">
    <el-tabs v-model="activeTabName" @tab-change="handleTabChange">
      <el-tab-pane name=" ">
        <template #label>
          <span>{{ quickSearchTabName }}</span>
          <el-dropdown placement="bottom-end" trigger="click" @command="handleQuickSearchCommand">
            <span class="el-dropdown-link" style="line-height: 40px" @click.stop="() => {}">
              <el-icon class="el-icon--right">
                <ArrowDown />
              </el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item v-for="NameItem in quickSearchTabNames" :key="NameItem" :command="NameItem">{{ NameItem }}</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
      </el-tab-pane>
      <el-tab-pane v-for="item in statusSearchTabs" :key="item[0]" :label="item[1]" :name="item[0]" />
    </el-tabs>
    <div class="last">
      <el-button type="primary" @click="router.push('/goods/purchase/add')">新增采购</el-button>
      <el-tooltip class="box-item" effect="dark" placement="bottom-end">
        <template #content>
          1、线下支付的采购订单需供应商确认(审核)收款，审核通过后流程继续<br />
          2、商品首次采购时需要先发布商品，发布完成后，后续采购无需再次发布商品<br />
          3、采购流程:创建采购订单->支付货款->供应商审核->供应商发货->店铺入库商品->发布商品->申请开票
        </template>
        <QIcon name="icon-wenhao" size="18px" style="margin-left: 8px; position: relative; top: 3px"></QIcon>
      </el-tooltip>
    </div>
  </div>
  <div class="table_container overh">
    <el-table
      ref="multipleTableRef"
      :cell-style="{ fontSize: '14px', color: '#333333' }"
      :data="orderDataList"
      :header-cell-style="{ background: '#f6f8fa', height: '50px' }"
      :header-row-style="{ fontSize: '14px', color: '#000' }"
      @selection-change="handleSelectionChange"
    >
      <template #empty> <ElTableEmpty /> </template>
      <el-table-column type="selection" width="40" fixed="left" />
      <el-table-column label="订单号" prop="no" width="200" />
      <el-table-column label="供应商" prop="extraInfo" width="160">
        <template #default="{ row }">
          <div class="customer">
            <div>
              <span class="customer__name">{{ row?.extraInfo?.supplierName }}</span>
              <QIcon name="icon-xiaoxi-copy" size="18px" style="cursor: pointer" svg @click="handleDispatchEvent('contact', row)" />
            </div>
            <span>{{ row?.extraInfo?.supplierPhone }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="采购数" prop="orderItems" width="90">
        <template #default="{ row }">
          {{ row.orderItems.reduce((pre: number, item: any) => pre + item.num, 0) }}
        </template>
      </el-table-column>
      <el-table-column label="商品总价(元)" prop="payAmount" width="110">
        <template #default="{ row }">
          {{ divTenThousand(row.payAmount).toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column label="运费(元)" prop="orderItems" width="90">
        <template #default="{ row }">
          {{ computedCalculateFreight(row?.orderItems).toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column label="应付金额(元)" prop="payAmount" width="120">
        <template #default="{ row }">
          <div style="display: flex; align-items: center">
            <span style="color: #f54319">￥{{ divTenThousand(row.payAmount) }}</span>
            <img
              v-if="row?.extra?.pay?.proof.length"
              :src="paymentVoucher"
              alt=""
              style="height: 16px; width: 21px; margin-left: 4px"
              @click="goToShowProof(row)"
            />
          </div>
        </template>
      </el-table-column>
      <el-table-column label="订单状态" width="140">
        <template #default="{ row }">
          <div
            :class="{
              'text-red': getMainOrderStatusText(row) === '待支付',
              'text-grey': getMainOrderStatusText(row) === '待支付',
            }"
            style="display: flex; align-items: center"
          >
            {{ getMainOrderStatusText(row) }}
            <countdown v-if="getMainOrderStatusText(row) === '待支付'" :create-time="row?.createTime" :pay-timeout="row?.extra?.payTimeout" />
          </div>
        </template>
      </el-table-column>
      <el-table-column v-if="!['UNPAID', 'PAYMENT_AUDIT', 'CLOSED', 'FINISHED'].includes(activeTabName)" label="待发货数" width="120">
        <template #default="{ row }">{{ row.waitForDeliveryCount }} </template>
      </el-table-column>
      <el-table-column v-if="!['UNPAID', 'PAYMENT_AUDIT', 'CLOSED', 'FINISHED'].includes(activeTabName)" label="已发货数" width="120">
        <template #default="{ row }">{{ row.hasDeliveryCount }} </template>
      </el-table-column>
      <el-table-column v-if="!['UNPAID', 'PAYMENT_AUDIT', 'CLOSED'].includes(activeTabName)" label="入库数" width="120">
        <template #default="{ row }">{{ row.stockInCount }} </template>
      </el-table-column>
      <el-table-column label="采购员" width="150">
        <template #default="{ row }">
          <div>
            <p>{{ row.purchaseNickName }}</p>
            <p>{{ row.purchasePhone }}</p>
          </div>
        </template>
      </el-table-column>
      <el-table-column v-if="activeTabName !== 'UNPAID'" label="支付方式" width="120">
        <template #default="{ row }">
          {{ payTypeMap[(row as ApiPurchaseOrder)?.extra?.pay?.payType] }}
        </template>
      </el-table-column>
      <el-table-column label="下单时间" prop="createTime" width="180" />
      <el-table-column v-if="activeTabName !== 'UNPAID'" label="支付时间" prop="payTime" width="180">
        <template #default="{ row }"> {{ row?.timeNodes?.payTime }} </template>
      </el-table-column>
      <el-table-column v-if="activeTabName === 'FINISHED' || activeTabName === ' '" label="开票状态" prop="createTime" width="140">
        <template #default="{ row }">
          <div>{{ INVOICE_STATUS[row.invoiceStatus as keyof typeof INVOICE_STATUS] }}</div>
        </template>
      </el-table-column>
      <el-table-column v-if="activeTabName === 'FINISHED' || activeTabName === ' '" label="申请时间" prop="applyInvoiceTime" width="180" />
      <el-table-column v-if="activeTabName === 'FINISHED' || activeTabName === ' '" label="开票时间" prop="invoiceTime" width="180" />
      <el-table-column label="操作" fixed="right" width="200" align="right">
        <template #default="{ row }">
          <div style="display: flex; justify-content: end">
            <el-button
              v-for="btn in computedBtnList(row)"
              :key="btn.action"
              link
              :type="btn.type"
              style="margin-left: 5px"
              @click="handleDispatchEvent(btn.action, row)"
              >{{ btn.text }}</el-button
            >
          </div>
        </template>
      </el-table-column>
    </el-table>
  </div>
  <PageManageTwo
    class="pagination"
    :page-size="pagination.page.size"
    :page-num="pagination.page.current"
    :total="pagination.total"
    @handle-size-change="handleSizeChange"
    @handle-current-change="handleCurrentChange"
  />
  <el-dialog v-model="showPayDialog" title="支付订单" width="500px" center destroy-on-close>
    <payOrder ref="payOrderRef" :price="divTenThousand(currentRow?.payAmount)" />
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="showPayDialog = false">取消</el-button>
        <el-button type="primary" @click="handlePayOrder"> 确认 </el-button>
      </span>
    </template>
  </el-dialog>
  <el-image-viewer v-if="showProof" :url-list="currentProof" @close="showProof = false" />
  <el-dialog v-model="showInStorageDialog" title="入库" width="1150px">
    <InStorage v-if="showInStorageDialog" ref="inStorageRefs" :order-no="inStorageOrderNo" />
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="showInStorageDialog = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmInStorage"> 确认 </el-button>
      </span>
    </template>
  </el-dialog>
  <el-dialog v-model="showStorageDetailsDialog" title="入库详情" width="1150px">
    <StorageDetails :order-no="storageDetailsOrderNo" />
  </el-dialog>
  <el-dialog v-model="showInvoiceDialog" width="1200px" center @close="handleCloseInvoiceDialog">
    <template #header="{ titleId, titleClass }">
      <div class="invoice-title">
        <h5 :id="titleId" :class="titleClass">{{ InvoiceStatusHander[invoiceDetail.invoiceStatus].title }}</h5>
      </div>
      <div class="invoice-desc">{{ InvoiceStatusHander[invoiceDetail.invoiceStatus].describe }}</div>
    </template>
    <invoice-dialog-main :invoice-detail="invoiceDetail" :invoice-set-type="invoiceSetType"></invoice-dialog-main>
    <template #footer>
      <span class="dialog-footer">
        <template v-if="invoiceDetail.invoiceStatus === 'START'">
          <el-button @click="showInvoiceDialog = false">取消</el-button>
          <el-button type="primary" @click="handleInvoiceConfig"> 确认 </el-button>
        </template>
        <el-button v-else :type="InvoiceStatusHander[invoiceDetail.invoiceStatus].btnConfig.type" @click="handleInvoiceConfig">{{
          InvoiceStatusHander[invoiceDetail.invoiceStatus].btnConfig.text
        }}</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<style lang="scss" scoped>
* {
  font-size: 14px;
}
@include b(invoice-title) {
  @include flex;
}
@include b(invoice-desc) {
  @include flex;
  color: #fd0505;
  margin-top: 7px;
}
.proof-img {
  width: 100%;
  height: 350px;
  object-fit: contain;
}
@include b(copy) {
  color: #1890ff;
  margin-left: 8px;
  cursor: pointer;
}
@include b(text-red) {
  color: #f00;
}
@include b(text-grey) {
  color: #999;
}
@include b(customer) {
  width: 100%;
  height: 42px;
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
    max-width: 88px;
    @include utils-ellipsis(1);
  }
  img {
    width: 16.25px;
    height: 15.32px;
    margin-left: 5px;
    cursor: pointer;
  }
}
.last {
  position: absolute;
  right: 20px;
  top: 0;
  z-index: 6;
  cursor: pointer;
  color: #303133;
}
</style>
