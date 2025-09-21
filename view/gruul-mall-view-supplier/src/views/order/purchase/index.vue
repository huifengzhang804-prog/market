<template>
    <div class="search_container">
        <Search @search="handleSearch" @supplier-export="supplierExport" />
    </div>
    <div class="tab_container" style="position: relative">
        <el-tabs v-model="activeTabName" @tab-change="handleTabChange">
            <el-tab-pane name=" ">
                <template #label>
                    <span>{{ quickSearchTabName }}</span>
                    <el-dropdown placement="bottom-end" trigger="click" @command="handleQuickSearchCommand">
                        <span class="el-dropdown-link" @click.stop="() => {}">
                            <el-icon class="el-icon--right">
                                <i-ep-arrow-down />
                            </el-icon>
                        </span>
                        <template #dropdown>
                            <el-dropdown-menu>
                                <el-dropdown-item v-for="NameItem in quickSearchTabNames" :key="NameItem" :command="NameItem">{{
                                    NameItem
                                }}</el-dropdown-item>
                            </el-dropdown-menu>
                        </template>
                    </el-dropdown>
                </template>
            </el-tab-pane>
            <el-tab-pane v-for="item in statusSearchTabs" :key="item[0]" :label="item[1]" :name="item[0]" />
        </el-tabs>
        <el-tooltip class="box-item" effect="dark" placement="bottom-end">
            <template #content>
                1、线下支付的采购订单需供应商确认(审核)收款，审核通过后流程继续<br />
                2、采购流程:创建采购订单->支付货款->供应商审核->供应商发货->店铺入库商品一>发布商品->申请开票
            </template>
            <QIcon class="last" name="icon-wenhao" size="18px"></QIcon>
        </el-tooltip>
    </div>
    <div class="handle_container">
        <template v-if="['WAITING_FOR_DELIVER', ' '].includes(activeTabName)">
            <el-button type="primary" round @click="handleDatchDeliverys"> 批量发货 </el-button>
            <el-button type="primary" round @click="openRemark()"> 批量备注 </el-button>
        </template>
    </div>

    <div class="table_container overh">
        <el-table
            :data="orderDataList"
            :header-row-style="{ fontSize: '14', color: '#333' }"
            :header-cell-style="{ background: '#f6f8fa', height: '48px' }"
            :cell-style="{ fontSize: '14', color: '#333333', height: '80px' }"
            @selection-change="multiSelect = $event"
        >
            <template #empty>
                <ElTableEmpty image-index="9" />
            </template>
            <el-table-column type="selection" width="40" fixed="left" />
            <el-table-column label="订单号" prop="no" width="220"> </el-table-column>
            <el-table-column label="店铺" width="190">
                <template #default="{ row }">
                    <div>
                        <div style="display: flex; align-items: center">
                            <el-tooltip v-if="row.shopName.length > 10" class="box-item" effect="dark" :content="row.shopName" placement="top-start">
                                <span class="name">{{ row.shopName }}</span>
                            </el-tooltip>
                            <span v-else class="name">{{ row.shopName }}</span>
                            <QIcon
                                class="iconfont icon-xiaoxi2"
                                color="#1bad19"
                                style="font-size: 18px; cursor: pointer; margin-left: 3px"
                                @click="handleDispatchEvent('contact', row)"
                            />
                        </div>
                        <span>
                            {{ row.shopContractNumber }}
                        </span>
                    </div>
                </template>
            </el-table-column>
            <el-table-column label="采购数" prop="no" width="100">
                <template #default="{ row }">
                    <span>
                        {{ row?.orderItems && row?.orderItems.reduce((pre: number, item: OrderItems) => pre + item.num, 0) }}
                    </span>
                </template>
            </el-table-column>
            <el-table-column label="商品总价(元)" prop="payAmount" width="150">
                <template #default="{ row }">{{ divTenThousand(row.payAmount).toFixed(2) }}</template>
            </el-table-column>
            <el-table-column label="运费(元)" prop="orderItems" width="120">
                <template #default="{ row }">{{ computedCalculateFreight(row?.orderItems).toFixed(2) }}</template>
            </el-table-column>
            <el-table-column label="应收金额(元)" prop="payAmount" width="150">
                <template #default="{ row }">
                    <span style="color: #f54319">{{ divTenThousand(row.payAmount).toFixed(2) }}</span>
                    <i
                        v-if="row?.extra?.pay?.proof.length"
                        class="iconfont icon-pingzhengzhongxin"
                        style="font-size: 22px; color: #306bff; position: relative; top: 3px; left: 2px; cursor: pointer"
                        @click="goToShowProof(row)"
                    ></i>
                </template>
            </el-table-column>
            <el-table-column label="订单状态" width="180">
                <template #default="{ row }">
                    <span :class="{ 'text-red': getMainOrderStatusText(row) === '待支付' }" class="df">
                        {{ getMainOrderStatusText(row) }}
                        <template v-if="getMainOrderStatusText(row) === '待支付'">
                            <countdown :create-time="row?.createTime" :pay-timeout="row?.extra?.payTimeout" />
                        </template>
                    </span>
                </template>
            </el-table-column>
            <el-table-column v-if="[' ', 'WAITING_FOR_DELIVER', 'WAITING_FOR_PUTIN'].includes(activeTabName)" label="待发货数" width="120">
                <template #default="{ row }">{{ row.waitForDeliveryCount }} </template>
            </el-table-column>
            <el-table-column v-if="[' ', 'WAITING_FOR_DELIVER', 'WAITING_FOR_PUTIN'].includes(activeTabName)" label="已发货数" width="120">
                <template #default="{ row }">{{ row.hasDeliveryCount }} </template>
            </el-table-column>
            <el-table-column v-if="!['UNPAID', 'PAYMENT_AUDIT', 'CLOSED'].includes(activeTabName)" label="入库数" width="120">
                <template #default="{ row }">{{ row.stockInCount }} </template>
            </el-table-column>
            <el-table-column label="采购员" width="120">
                <template #default="{ row }">
                    <div>
                        <p>{{ row?.purchaseNickName }}</p>
                        <p>{{ row?.purchasePhone }}</p>
                    </div>
                </template>
            </el-table-column>
            <el-table-column label="支付方式" width="120">
                <template #default="{ row }">
                    {{ payTypeMap[row?.extra?.pay?.payType as keyof typeof payTypeMap] }} <span v-if="!row?.extra?.pay?.payType">-</span></template
                >
            </el-table-column>
            <el-table-column label="下单时间" prop="createTime" width="180"> </el-table-column>
            <el-table-column label="支付时间" width="180">
                <template #default="{ row }">{{ row?.timeNodes?.payTime }}<span v-if="!row?.timeNodes?.payTime">-</span> </template>
            </el-table-column>
            <el-table-column v-if="activeTabName === 'FINISHED' || activeTabName === ' '" label="开票状态" prop="createTime" width="140">
                <template #default="{ row }">
                    <div>{{ INVOICE_STATUS[row.invoiceStatus as keyof typeof INVOICE_STATUS] }}</div>
                </template>
            </el-table-column>
            <el-table-column v-if="activeTabName === 'FINISHED' || activeTabName === ' '" label="申请时间" prop="applyInvoiceTime" width="180" />
            <el-table-column v-if="activeTabName === 'FINISHED' || activeTabName === ' '" label="开票时间" prop="invoiceTime" width="180" />
            <el-table-column label="操作" fixed="right" align="right" width="200">
                <template #default="{ row }">
                    <div style="display: flex; justify-content: end">
                        <el-link
                            v-for="btn in computedBtnList(row)"
                            :key="btn.action"
                            style="margin-left: 10px"
                            :type="btn.type"
                            @click="handleDispatchEvent(btn.action, row)"
                        >
                            <el-tooltip
                                v-if="row.remark && btn.text === '备注'"
                                class="box-item"
                                effect="dark"
                                :content="row.remark"
                                placement="bottom-start"
                            >
                                {{ btn.text }}
                            </el-tooltip>
                            <text v-else>{{ btn.text }}</text>
                        </el-link>
                    </div>
                </template>
            </el-table-column>
        </el-table>
    </div>
    <PageManage
        :page-size="pagination.page.size"
        :page-num="pagination.page.current"
        :total="pagination.total"
        @handle-size-change="handleSizeChange"
        @handle-current-change="handleCurrentChange"
    />
    <el-dialog v-model="showProof" title="付款凭证" width="500px">
        <div style="max-height: 650px; overflow-y: auto">
            <img v-for="item in currentProof" :key="item" :src="item" class="proof-img" />
        </div>
    </el-dialog>
    <el-dialog v-model="showAuditDialog" title="审核" width="500px" destroy-on-close>
        <audit v-model:order-info="auditOrderInfo" />
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="showAuditDialog = false">取消</el-button>
                <el-button type="primary" @click="handleConfirmAuditOrder"> 确认 </el-button>
            </span>
        </template>
    </el-dialog>
    <el-dialog v-model="showDeliveryDialog" title="商品发货" width="700px" destroy-on-close :close-on-click-modal="false">
        <delivery
            ref="deliveryRef"
            :current-no="deliveryProps.currentNo"
            :list-order-items="deliveryProps.listOrderItems"
            :receiver="deliveryProps.receiver"
            :create-time="deliveryProps.createTime"
        />
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="showDeliveryDialog = false">取消</el-button>
                <el-button :loading="deliveryConfirmLoading" type="primary" @click="handleConfirmDelivery"> 确认 </el-button>
            </span>
        </template>
    </el-dialog>
    <el-dialog v-model="showRemarkDialog" title="备注" width="500px" destroy-on-close>
        <remark v-model:remark="remarkData.remark" :order-nos="remarkData.orderNos" />
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="showRemarkDialog = false">取消</el-button>
                <el-button type="primary" @click="handleConfirmRemark"> 确认 </el-button>
            </span>
        </template>
    </el-dialog>
</template>

<script lang="ts" setup>
import Search from './components/search.vue'
import PageManage from '@/components/PageManage/index.vue'
import { queryOrderStatus } from './helper/constant'
import countdown from '@/views/order/components/count-down/index.vue'
import useShowProof from './hooks/useShowProof'
import audit from './components/audit.vue'
import usePurchaseOrderList from './hooks/usePurchaseOrderList'
import delivery from './components/delivery.vue'
import remark from './components/remark.vue'
import { ElMessage } from 'element-plus'
import { doPostSupplierExport } from '@/apis/exportData'
import { OrderItems } from './components/split-table/order'
/**
 * hooks
 */
const {
    handleTabChange,
    pagination,
    initOrderList,
    orderDataList,
    handleQuickSearchCommand,
    handleSearch,
    quickSearchTabName,
    quickSearchTabNames,
    activeTabName,
    getMainOrderStatusText,
    computedBtnList,
    handleDispatchEvent,
    computedCalculateFreight,
    payTypeMap,
    handleConfirmAudit,
    auditOrderInfo,
    showAuditDialog,
    showDeliveryDialog,
    deliveryProps,
    deliveryRef,
    handleConfirmDelivery,
    multiSelect,
    handleDatchDeliverys,
    remarkData,
    showRemarkDialog,
    openRemark,
    handleConfirmRemark,
    deliveryConfirmLoading,
    handleSizeChange,
    handleCurrentChange,
    INVOICE_STATUS,
} = usePurchaseOrderList()
const { divTenThousand } = useConvert()
const { showProof, goToShowProof, currentProof } = useShowProof()

const handleConfirmAuditOrder = () => handleConfirmAudit({ orderNo: auditOrderInfo.orderNo, success: auditOrderInfo.success })
const statusSearchTabs: any[][] = []

const initialTabs = () => {
    Object.keys(queryOrderStatus).forEach((key: string) => {
        statusSearchTabs.push([key, queryOrderStatus[key as keyof typeof queryOrderStatus]])
    })
}
initialTabs()
initOrderList()
const supplierExport = async (val: any) => {
    if (multiSelect.value.length) {
        let exportOrderIds = ['']
        exportOrderIds = multiSelect.value.map((item) => item.mainNo)
        const { data, code, msg } = await doPostSupplierExport({ exportOrderIds })
        if (code === 200) {
            ElMessage.success('导出成功')
        } else {
            ElMessage.error(msg || '导出数据失败')
        }
    } else {
        let param = {
            no: val.no,
            supplierId: val.purchaser,
            startTime: val.date?.[0],
            endTime: val.date?.[1],
            status: activeTabName.value,
            exportOrderIds: '',
        }
        param.status = param.status.trim()
        const { data, code, msg } = await doPostSupplierExport(param)
        if (code === 200) {
            ElMessage.success('导出成功')
        } else {
            ElMessage.error(msg || '导出数据失败')
        }
    }
}
</script>

<style lang="scss" scoped>
@include b(name) {
    max-width: 130px;
    @include utils-ellipsis;
}
@include b(order-table) {
    background-color: #fff;
    padding: 0 16px;
    overflow-x: auto;
    transition: height 0.5s;
    :deep(.m__table--head) {
        position: sticky;
        top: 0;
        z-index: 999;
    }
    :deep(.body--header) {
        background-color: #f7f8fa;
        border-radius: 0;
    }
    :deep(.m__table--head th) {
        border: none;
        background-color: #f7f8fa;
    }
    :deep(.m__table--center) {
        border: none;
    }
    :deep(.body--header) {
        border: none;
    }
    @include e(commodity) {
        width: 280px;
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        @include m(content) {
            margin-left: 10px;
            @include flex(space-between, flex-start);
            flex-direction: column;
            height: 63px;
        }
        @include m(name) {
            overflow: hidden;
            text-overflow: ellipsis;
            display: box;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            margin-right: 10px;
            font-weight: 600;
        }
        @include m(spec) {
            overflow: hidden;
            text-overflow: ellipsis;
            display: box;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            margin-right: 10px;
        }
        @include m(info) {
            flex-shrink: 0;
            display: flex;
            flex-direction: column;
            align-items: flex-end;
        }
    }
    @include e(supplier) {
        display: flex;
        justify-content: flex-start;
        align-items: center;
        flex-direction: column;
        line-height: 1.5;
    }
    @include e(actions) {
        display: flex;
        flex-wrap: wrap;
        .el-link + .el-link {
            margin-left: 8px;
        }
    }
    @include e(header) {
        font-size: 11px;
        display: flex;
        justify-content: space-around;
        align-items: center;
        width: 100%;
    }
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
    color: #f54319;
}
.no_data {
    margin-top: 100px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    img {
        width: 200px;
        height: 210px;
    }
    .cont {
        color: #737b80;
        text-align: center;
    }
}
:deep(.el-empty__image > img) {
    width: 230px;
}
:deep(.el-empty__image) {
    width: auto;
}
.last {
    position: absolute;
    right: 20px;
    top: 30%;
    z-index: 6;
    cursor: pointer;
    color: #303133;
}
</style>
