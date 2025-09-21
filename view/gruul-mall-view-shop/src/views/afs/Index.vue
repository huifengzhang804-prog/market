<script setup lang="ts">
import { useRouter } from 'vue-router'
import QTable from '@/components/qszr-core/packages/q-table/QTable'
import QTableColumn from '@/components/qszr-core/packages/q-table/q-table-column.vue'
import { ElMessage } from 'element-plus'
import PageManage from '@/components/PageManage.vue'
import RemarkPopup from '@/components/remark/remark-popup.vue'
import RemarkFlag from '@/components/remark/remark-flag.vue'
import { TabItem, getAfsNumStatusCn, packageStatus } from '@/composables/useAfsStatus'
import { doGetAfsList } from '@/apis/afs'
import type { ApiOrderAfsItem } from '@/views/afs/types'
import type { OrderListSearchData } from '@/views/order/types/order'
import type { TabPaneName } from 'element-plus'
import { doPostAfsExport } from '@/apis/exportData'
import AccordionFrom from '@/views/order/components/accordionFrom.vue'
import { postAddContact } from '@/apis'

const { divTenThousand } = useConvert()
enum SellTypeEnum {
    CONSIGNMENT = '代销',
    PURCHASE = '采购',
    OWN = '自有',
}
const activeTab = ref(' ')
// 批量备注弹窗
const noteDialog = ref(false)
const isPackUp = ref(false)
// 备注消息文本
const TabData = ref<ApiOrderAfsItem[]>([])
const multiSelect = ref<ApiOrderAfsItem[]>([])
const ids = ref<string[]>([])
const currentRemark = ref('')
const $router = useRouter()
const pageConfig = reactive({
    size: 10,
    current: 1,
    total: 0,
})
const afsListparams = reactive({
    status: '',
    params: {
        afsNo: '',
        buyerNickname: '',
        productName: '',
        receiverName: '',
        startTime: '',
        endTime: '',
    },
})

async function initAfsList() {
    const params = { ...afsListparams.params, status: afsListparams.status, ...pageConfig }
    const { code, data } = (await doGetAfsList(params)) as any
    if (code !== 200) return ElMessage.error('订单列表获取失败')
    TabData.value = data.records
    pageConfig.current = data.current
    pageConfig.size = data.size
    pageConfig.total = data.total
}
initAfsList()
/**
 * 处理审核
 */
const handleAudit = (row: ApiOrderAfsItem, afsNo: string) => {
    const { no, shopOrderItemId, packageId, orderNo, afsOrderItem } = row
    $router.push({
        name: 'orderSaleDetailIndex',
        query: { orderNo, no, shopOrderItemId, packageId: packageId || '', afsNo, audit: 'afterSalesInfo', productId: afsOrderItem.productId },
    })
}
/**
 * 下拉选择（查看详情）
 * @params salesShow 显示售后页面
 */
const handleSelected = (row: ApiOrderAfsItem, e: string, afsNo: string) => {
    const { no, shopOrderItemId, packageId, orderNo, afsOrderItem } = row
    if (e !== '查看详情') return
    $router.push({
        name: 'orderSaleDetailIndex',
        query: {
            orderNo,
            no,
            shopOrderItemId,
            packageId: packageId || '',
            afsNo,
            productId: afsOrderItem.productId,
            statusContent: row.statusContent,
        },
    })
}
/**
 * 分页器
 * @param {*} value
 */
const handleSizeChange = (value: number) => {
    pageConfig.size = value
    initAfsList()
}
const handleCurrentChange = (value: number) => {
    pageConfig.current = value
    initAfsList()
}

const totalPrice = (price: string, num: number) => {
    return divTenThousand(price).mul(num)
}
/**
 * 手风琴表单收起
 * @param {*} value
 */
const handleSearchChange = (value: boolean) => {
    isPackUp.value = value
}
const handleTabChange = (name: TabPaneName) => {
    afsListparams.status = name as string
    initAfsList()
}
const handleSearchData = (params: OrderListSearchData) => {
    afsListparams.params = {
        afsNo: params.no,
        buyerNickname: params.buyerNickname,
        productName: params.productName,
        receiverName: params.receiverName,
        startTime: params.startTime,
        endTime: params.endTime,
    }
    initAfsList()
}
/**
 * 批量备注
 */
const handleNote = () => {
    if (multiSelect.value.length) {
        ids.value = multiSelect.value.map((item) => item.no)
        noteDialog.value = true
        return
    }
    ElMessage.error('请先选择订单')
}
const handleNoteItem = (row: ApiOrderAfsItem) => {
    if (row.no) {
        ids.value = [row.no]
        if (row.remark) {
            currentRemark.value = row.remark
        }
        noteDialog.value = true
    }
}
const updateList = () => {
    multiSelect.value = []
    ids.value = []
    initAfsList()
}
// 导出数据
const exportData = async (SearchFromData: any) => {
    if (multiSelect.value.length) {
        let exportAfsOrderNos = ['']
        exportAfsOrderNos = multiSelect.value.map((item) => item.no)
        const { code, data, msg } = await doPostAfsExport({ exportAfsOrderNos })
        if (code !== 200) return ElMessage.error(msg || '导出失败')
        else return ElMessage.success('导出成功')
    } else {
        let param = {
            afsNo: SearchFromData.no,
            buyerNickname: SearchFromData.buyerNickname,
            productName: SearchFromData.productName,
            receiverName: SearchFromData.receiverName,
            startTime: SearchFromData.clinchTime?.[0],
            endTime: SearchFromData.clinchTime?.[1],
            status: activeTab.value,
            exportAfsOrderNos: '',
        }
        param.status = param.status.trim()
        const { code, data, msg } = await doPostAfsExport(param)
        if (code !== 200) return ElMessage.error(msg || '导出失败')
        else return ElMessage.success('导出成功')
    }
}

// 联系买家
const contactBuyer = (row: ApiOrderAfsItem) => {
    postAddContact(row.shopId, row.buyerId).then(({ code }) => {
        if (code === 200) {
            $router.push({ path: '/message/customer/service', query: { id: row.buyerId, types: '订单', orderId: row.id, orderNo: row.no } })
        }
    })
}
</script>

<template>
    <el-config-provider :empty-values="[undefined, null]">
        <!-- 搜索部分s -->
        <AccordionFrom :order="true" @export-data="exportData" @search-data="handleSearchData" @search-change="handleSearchChange" />
        <!-- tab部分s -->
    </el-config-provider>
    <el-tabs v-model="activeTab" class="tab_container" style="margin-top: 13px" @tab-change="handleTabChange">
        <el-tab-pane v-for="item in TabItem" :key="item.id" :label="item.title" :name="item.name">
            <!-- <dropDown title="批量备注" /> -->
        </el-tab-pane>
    </el-tabs>
    <div class="handle_container">
        <el-button @click="handleNote">批量备注 </el-button>
    </div>
    <!-- tab表格s -->
    <QTable v-model:checkedItem="multiSelect" :data="TabData" header-selection class="tab" no-border>
        <template #header="{ row }">
            <div class="tab__header">
                <div style="display: flex">
                    <i v-if="row.quicknessAfs" class="iconfont icon-a-zuhe1285"></i>
                    <div>
                        <span :class="['tab__header--status', row.type === 'REFUND' ? 'refund' : 'returns-refunds']">
                            {{ getAfsNumStatusCn(row.type) }}单
                        </span>
                    </div>
                    <div class="tab__header--information">
                        <span>工单号:{{ row.no }}</span>
                        <span>申请时间：{{ row.createTime }}</span>
                        <span v-if="row.quicknessAfs" style="color: #f54319">极速售后</span>
                    </div>
                </div>
                <remark-flag :content="row.remark" @see-remark="handleNoteItem(row)" />
            </div>
        </template>

        <QTableColumn label="退款商品" align="left" width="400">
            <template #default="{ row }">
                <div class="table-commodity">
                    <div class="table-commodity__img-box">
                        <el-avatar style="width: 68px; height: 68px; flex-shrink: 0" shape="square" size="large" :src="row.afsOrderItem.image" />
                        <div class="order-info">
                            <el-tooltip
                                v-if="row.afsOrderItem.productName.length >= 44"
                                effect="dark"
                                :content="row.afsOrderItem.productName"
                                placement="top-start"
                            >
                                <p class="order-info__name">
                                    <span v-if="row.afsOrderItem.sellType === 'CONSIGNMENT'" class="order-info__consignment_sales">{{
                                        SellTypeEnum[row.afsOrderItem.sellType as keyof typeof SellTypeEnum]
                                    }}</span>
                                    <span v-if="row.afsOrderItem.productType === 'VIRTUAL_PRODUCT'" class="virtual_product">虚拟</span>
                                    {{ row.afsOrderItem.productName }}
                                </p>
                            </el-tooltip>
                            <p v-else class="order-info__name">
                                <span v-if="row.afsOrderItem.sellType === 'CONSIGNMENT'" class="order-info__consignment_sales">{{
                                    SellTypeEnum[row.afsOrderItem.sellType as keyof typeof SellTypeEnum]
                                }}</span>
                                <span v-if="row.afsOrderItem.productType === 'VIRTUAL_PRODUCT'" class="virtual_product">虚拟</span>
                                {{ row.afsOrderItem.productName }}
                            </p>
                            <el-tooltip
                                v-if="row.afsOrderItem.specs?.join('、 ').length >= 20"
                                effect="dark"
                                :content="row.afsOrderItem.specs?.join('、 ')"
                                placement="top-start"
                            >
                                <p class="order-info__spec">{{ row.afsOrderItem.specs?.join('、') }}</p></el-tooltip
                            >
                            <p v-else class="order-info__spec">{{ row.afsOrderItem.specs?.join('、') }}</p>
                        </div>
                    </div>
                    <div class="table-commodity__img-mask">
                        <span>￥{{ divTenThousand(row.afsOrderItem.dealPrice)?.toFixed(2) }}</span>
                        <span style="color: #666; margin-top: 8px">x{{ row.afsOrderItem.num }}</span>
                    </div>
                </div>
            </template>
        </QTableColumn>
        <QTableColumn label="退款金额(元)" class="rate_size" align="left" width="130">
            <template #default="{ row }">
                <div class="avatar_text money_text" style="color: #ff860b; font-size: 14px">
                    {{ totalPrice(row.afsOrderItem.dealPrice, row.afsOrderItem.num).toFixed(2) }}
                </div>
            </template>
        </QTableColumn>
        <QTableColumn label="退款用户" width="160" align="left">
            <template #default="{ row }">
                <div class="customer">
                    <div>
                        <span class="customer__name">{{ row.buyerNickname }}</span>
                        <i style="font-size: 18px; color: #1bad19; margin-top: -2px" class="iconfont icon-xiaoxi2 cup" @click="contactBuyer(row)"></i>
                    </div>
                    <span>{{ row.buyerPhone }}</span>
                </div>
            </template>
        </QTableColumn>
        <QTableColumn label="售后状态" width="120" align="center">
            <template #default="{ row }">
                <div class="sale__status">
                    <div
                        :style="{
                            color:
                                row.statusContent.split(' ')[0] === '待处理'
                                    ? '#F54319'
                                    : row.statusContent.split(' ')[0] === '退款失败'
                                    ? '#999'
                                    : row.statusContent.split(' ')[0] === '退款成功'
                                    ? '#00BB2C'
                                    : row.statusContent.split(' ')[0] === '待退货'
                                    ? '#FD9224'
                                    : '',
                        }"
                        class="order-status_text"
                    >
                        {{ row.statusContent.split(' ')[0] }}
                    </div>
                    <div
                        :style="{
                            color:
                                row.statusContent.split(' ')[0] === '待处理'
                                    ? '#F54319'
                                    : row.statusContent.split(' ')[0] === '退款失败'
                                    ? '#999'
                                    : row.statusContent.split(' ')[0] === '退款成功'
                                    ? '#666'
                                    : row.statusContent.split(' ')[0] === '待退货'
                                    ? '#666'
                                    : '',
                        }"
                        class="money_text"
                    >
                        {{ row.statusContent.split(' ')[1] }}
                    </div>
                </div>
            </template>
        </QTableColumn>
        <QTableColumn prop="sex" label="操作" width="130" align="right" fixed="right">
            <template #default="{ row }">
                <span style="cursor: pointer; color: #555cfd" @click="handleSelected(row, '查看详情', row.no)"> 查看详情 </span>
            </template>
        </QTableColumn>
    </QTable>
    <!-- tab表格e -->
    <PageManage
        v-if="pageConfig.total"
        :page-size="pageConfig.size"
        :page-num="pageConfig.current"
        :total="pageConfig.total"
        @handle-size-change="handleSizeChange"
        @handle-current-change="handleCurrentChange"
    />
    <!-- 售后备注弹窗s -->
    <remark-popup v-model:isShow="noteDialog" v-model:ids="ids" v-model:remark="currentRemark" remark-type="AFS" @success="updateList" />
    <!-- 售后备注弹窗s -->
</template>

<style lang="scss" scoped>
* {
    font-size: 14px;
}
@include b(tab) {
    overflow-y: scroll;
    transition: height 0.5s;
    @include e(header) {
        @include flex(space-between);
        width: 100%;
        font-size: 14px;
        color: #333;
        i {
            position: absolute;
            left: 0;
            top: 0;
            color: #f54319;
        }
        @include m(icon) {
            width: 20%;
            text-align: right;
        }
        @include m(status) {
            margin-right: 10px;
            font-size: 12px;
            padding: 3px 6px;
        }
        @include b(refund) {
            color: #e90000;
            background-color: #ff00000d;
        }
        @include b(returns-refunds) {
            color: #fd9224;
            background-color: #fd92241a;
        }
        @include m(information) {
            display: flex;
            span::after {
                content: '|';
                margin: 0 10px;
                position: relative;
                top: -1px;
                color: #999999;
            }
            span:last-child::after {
                content: '';
            }
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
    @include e(name) {
        display: inline-block;
        @include utils-ellipsis(1);
        width: 66px;
        color: #555cfd;
    }
}
.caozuo_btn:hover {
    color: #fff;
    background: #309af3 !important;
}
@include b(sale) {
    @include e(status) {
        @include flex;
        flex-direction: column;
    }
}
@include b(money_text) {
    font-size: 12px;
    color: #000000;
    overflow: hidden;
    width: 100%;
}
@include b(shop_specs) {
    @include utils-ellipsis(1);
}
@include b(order-status_text) {
    margin-bottom: 5px;
    font-size: 24rpx;
    color: #ff7417;
}
@include b(table-commodity) {
    width: 100%;
    display: flex;
    align-items: start;
    @include e(img-box) {
        display: flex;
        font-size: 14px;
        justify-content: space-between;
    }

    @include e(img-mask) {
        display: flex;
        flex-direction: column;
        align-items: flex-end;
        font-size: 14px;
        color: #666666;
        margin-left: 5px;
        height: 100%;
    }
    @include b(order-info) {
        flex: 1;
        display: flex;
        flex-direction: column;
        justify-content: space-between;
        padding-top: 2px;
        margin: 0 8px;
        word-break: break-all;
        line-height: 1.5;
        overflow: hidden;
        width: 250px;

        @include e(name) {
            @include utils-ellipsis(2);
            line-height: 18px;
            color: #333;
            @include e(consignment_sales) {
                display: inline-block;
                width: 30px;
                line-height: 18px;
                font-size: 12px;
                border-radius: 4px;
                background-color: rgba(255, 129, 0, 0.1);
                color: #ff8100;
                text-align: center;
                margin-right: 5px;
            }
        }
        @include e(spec) {
            text-overflow: ellipsis;
            white-space: nowrap;
            overflow: hidden;
            color: #999;
        }
    }
}
:deep(.m__table--head > .m__tr > th:nth-child(4)) {
    .m__table--shrink {
        text-align: center !important;
    }
}
</style>
