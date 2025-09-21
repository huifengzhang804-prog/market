<script setup lang="ts">
import { ElMessage } from 'element-plus'
import AccordionFrom from '@/components/order/accordion-from.vue'
import { packageStatus } from '@/composables/useOrderStatus'
import QTable from '@/components/qszr-core/packages/q-table/QTable'
import QTableColumn from '@/components/qszr-core/packages/q-table/q-table-column.vue'
import PageManage from '@/components/PageManage.vue'
import { TabItem, getAfsListStatusCn, getAfsNumStatusCn, getAfsListPagesStatusCn } from '@/composables/useAfsStatus'
import { doGetAfsList } from '@/apis/afs'
import RemarkFlag from '@/components/remark/remark-flag.vue'
import RemarkPopup from '@/components/remark/remark-popup.vue'
import type { ApiOrderAfsItem } from '@views/afs/types'
import { doPostAfsExport } from '@/apis/exportData'
import { postAddContact } from '@/apis/set/platformDelivery'

const { divTenThousand } = useConvert()
const activeTab = ref('')
const remarkDialog = ref(false) // 批量备注弹窗
const remarkMessageText = ref('') // 备注消息文本
const tabData = ref<ApiOrderAfsItem[]>([])
const multiSelect = ref<ApiOrderAfsItem[]>([])
const ids = ref<string[]>([])
const $router = useRouter()
const pageConfig = reactive({
    size: 10,
    current: 1,
    total: 0,
    params: { afsNo: '', receiverName: '', endTime: '', buyerNickname: '', orderNo: '', productName: '', startTime: '' },
})
const tableStyleUp = ref(false)
enum SellTypeEnum {
    CONSIGNMENT = '代销',
    PURCHASE = '采购',
    OWN = '自有',
}

initAfsList()

async function initAfsList() {
    const { params, current, size } = pageConfig
    const { code, data } = (await doGetAfsList({ ...params, status: activeTab.value, current, size, afsNo: params.orderNo })) as any
    if (code !== 200) return ElMessage.error('订单列表获取失败')
    tabData.value = data.records
    pageConfig.current = data.current
    pageConfig.size = data.size
    pageConfig.total = data.total
}
//tab点击切换
const handleTabChange = () => {
    initAfsList()
}
/**
 * 获取搜索表单数据
 */
const GetSearchData = (data: any) => {
    pageConfig.params = data
    initAfsList()
}
/**
 * 查看详情(平台端传shopid，商家端传itemid)
 * @params salesShow 显示售后页面
 */
const handleSelected = (row: ApiOrderAfsItem, e: string, afsNo: string) => {
    const { no, shopOrderItemId, packageId, orderNo } = row
    $router.push({
        name: 'afsDetailsIndex',
        query: { orderNo, no, shopOrderItemId, packageId: packageId || '', afsNo, statusContent: row.statusContent },
    })
}
/**
 * 分页器
 * @param {*} value
 */
const handleSizeChange = (value: number) => {
    pageConfig.current = 1
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
const handleChangeShow = (val: boolean) => {
    tableStyleUp.value = val
}
const handleNoteItem = (row: ApiOrderAfsItem) => {
    if (row.no) {
        ids.value = [row.no]
        if (row.remark) {
            remarkMessageText.value = row.remark
        }
        remarkDialog.value = true
    }
}
const handleNote = () => {
    if (multiSelect.value.length) {
        ids.value = multiSelect.value.map((item) => item.no)
        remarkDialog.value = true
        return
    }
    ElMessage.error('请先选择订单')
}
const handleRemarkSuccess = () => {
    multiSelect.value = []
    initAfsList()
}
// 导出数据
// "afsNo":null//"工单号"
// "buyerNickname":null,//买家昵称
// "productName":null,//商品名称
// "receiverName":null,//收货人姓名
// "startTime":null,//下单开始时间
// "endTime":null,//下单结束时间
// "status":null,//工单状态  待审核 处理中 已完成 已关闭
// "exportAfsOrderNos":[]//需要导出的工单号列表
const exportData = async (SearchFromData: any) => {
    if (multiSelect.value.length) {
        let exportAfsOrderNos = ['']
        exportAfsOrderNos = multiSelect.value.map((item) => item.no)
        const { code, data, msg } = await doPostAfsExport({ exportAfsOrderNos })
        if (code !== 200) return ElMessage.error(msg || '导出失败')
        else return ElMessage.success('导出成功')
    } else {
        let param = {
            afsNo: SearchFromData.orderNo,
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
    postAddContact(0, row.buyerId).then(({ code }) => {
        if (code === 200) {
            $router.push({ path: '/customerService', query: { id: row.buyerId, types: '订单', orderId: row.id, orderNo: row.no } })
        }
    })
}
</script>

<template>
    <!-- 搜索部分s -->
    <el-config-provider :empty-values="[undefined, null]">
        <AccordionFrom :order="true" @search-data="GetSearchData" @change-show="handleChangeShow" @export-data="exportData" />
    </el-config-provider>
    <!-- 搜索部分e -->
    <div class="grey_bar"></div>
    <!-- tab部分s -->
    <div class="tab_container">
        <el-tabs v-model="activeTab" style="margin-top: 13px" @tab-change="handleTabChange">
            <el-tab-pane v-for="item in TabItem" :key="item.id" :label="item.title" :name="item.name" />
        </el-tabs>
    </div>
    <!-- <el-button type="primary" class="caozuo_btn" plain round bg style="width: 82px; height: 36px; background: #ecf5fd" @click="handleNote">
        批量备注
    </el-button> -->
    <!-- tab部分e -->
    <!-- tab表格s -->
    <div class="handle_container">
        <el-button @click="handleNote">批量备注</el-button>
    </div>
    <q-table v-model:checkedItem="multiSelect" :data="tabData" header-selection class="tab" :class="{ tableup: !tableStyleUp }" no-border>
        <template #header="{ row }">
            <div class="tab__header">
                <div style="display: flex">
                    <div style="flex-shrink: 0">
                        <span :class="['tab__header--status', row.type === 'REFUND' ? 'refund' : 'returns-refunds']">
                            {{ getAfsNumStatusCn(row.type) }}单
                        </span>
                    </div>
                    <div class="tab__header--information">
                        <QIcon v-if="row.quicknessAfs" name="icon-a-zuhe1285" style="position: absolute; left: 0; top: 0" size="18px" svg />
                        <span>工单号:{{ row.no }}</span>
                        <span>申请时间：{{ row.createTime }}</span>
                        <span>店铺名称：{{ row.shopName || '未知店铺' }}</span>
                        <span v-if="row.quicknessAfs" style="color: #f54319">极速售后</span>
                    </div>
                </div>
                <remark-flag :content="row.remark" @see-remark="handleNoteItem(row)" />
            </div>
        </template>

        <q-table-column label="退款商品" align="left" width="450">
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
                        <span style="margin-top: 8px">x{{ row.afsOrderItem.num }}</span>
                    </div>
                </div>
            </template>
        </q-table-column>
        <q-table-column label="退款金额(元)" class="rate_size" align="left" width="130">
            <template #default="{ row }">
                <div class="avatar_text money_text" style="color: #ff860b; font-size: 14px">
                    {{ totalPrice(row.afsOrderItem.dealPrice, row.afsOrderItem.num).toFixed(2) }}
                </div>
            </template>
        </q-table-column>
        <q-table-column label="退款用户" width="130" align="left">
            <template #default="{ row }">
                <div class="customer">
                    <div>
                        <span class="customer__name">{{ row.buyerNickname }}</span>
                        <i
                            style="font-size: 18px; color: #1bad19; position: relative; top: -1px; cursor: pointer"
                            class="iconfont icon-xiaoxi2"
                            @click="contactBuyer(row)"
                        ></i>
                    </div>
                    <span>{{ row.buyerPhone }}</span>
                </div>
            </template>
        </q-table-column>
        <q-table-column label="售后状态" width="170" align="center">
            <template #default="{ row }">
                <div class="sale__status">
                    <!-- <div class="order-status_text">{{ getAfsListPagesStatusCn(row.packageStatus) }}</div> -->
                    <!-- <div class="money_text">{{ getAfsListStatusCn(row) }}</div> -->
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
        </q-table-column>
        <q-table-column prop="sex" label="操作" width="130" align="right" fixed="right">
            <template #default="{ row }">
                <span style="cursor: pointer; color: #555cfd" @click="handleSelected(row, '查看详情', row.no)"> 查看详情 </span>
            </template>
        </q-table-column>
    </q-table>
    <!-- tab表格e -->
    <el-row justify="space-between" align="bottom" style="padding-top: 20px">
        <page-manage
            :load-init="true"
            :page-size="pageConfig.size"
            :page-num="pageConfig.current"
            :total="pageConfig.total"
            @reload="initAfsList"
            @handle-size-change="handleSizeChange"
            @handle-current-change="handleCurrentChange"
        />
    </el-row>
    <!-- 备注弹窗s -->
    <remark-popup
        v-model:isShow="remarkDialog"
        v-model:ids="ids"
        v-model:remark="remarkMessageText"
        remark-type="AFS"
        @success="handleRemarkSuccess"
    />
    <!-- 备注弹窗e -->
</template>

<style lang="scss" scoped>
@include b(note) {
    &:hover {
        color: #fff;
        background: #309af3 !important;
    }
}
@include b(tab) {
    height: calc(100vh - 492px);
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
            > span {
                flex-shrink: 0;
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
        max-width: 50%;
        color: #555cfd;
    }
}
.caozuo_btn:hover {
    color: #fff;
    background: #309af3 !important;
}
@include b(sale) {
    @include e(status) {
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
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
        margin: 0 8px;
        word-break: break-all;
        line-height: 1.5;
        overflow: hidden;
        width: 310px;

        @include e(name) {
            @include utils-ellipsis(2);
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
            @include utils-ellipsis(1);
            color: #999;
        }
    }
}

@include b(tableup) {
    // height: calc(100vh - 340px);
    height: calc(100vh - 285px);
}
:deep(.m__table .m__table--head th:nth-child(4) > div) {
    text-align: center;
}
</style>
