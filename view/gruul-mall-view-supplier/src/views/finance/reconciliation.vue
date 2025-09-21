<script setup lang="ts">
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { doGetFinance } from '@/apis/finance/index'
import PageManage from '@/components/PageManage/index.vue'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import type { ApiFinanceItem, QueryFrom } from '@/views/finance/types'
import ReconciliationDescription from './components/reconciliation-description.vue'
import { doPostExportStatementData } from '@/apis/overview'
import SchemaForm from '@/components/SchemaForm.vue'
import { SearchType } from '@/components/types'
/**
 * 交易类型
 * @params ORDER_PAID 订单支付(订单详情)
 * @params ORDER_AFS 售后退款(详情)
 * @params DISTRIBUTE 分销佣金
 */
enum TRADESTATUS {
    ORDER_PAID,
    ORDER_AFS,
    DISTRIBUTE,
}

const showDescriptionDialog = ref(false)
const $router = useRouter()
const $shopStore = useShopInfoStore()
const { divTenThousand } = useConvert()
const activeTab = ref('')

/** 搜索条件 */
const query = ref<QueryFrom>({
    transactionType: '',
    changeType: '', //   ""->全部;INCREASE->收入;REDUCE->支出
    createTime: [],
    startDate: '',
    endDate: '',
    orderNo: '',
    tradeNo: '',
    current: 1,
    size: 10,
    shopId: $shopStore.getterShopInfo.id,
})
const basicOptions = [
    { label: '全部', value: '' },
    { label: '代销交易', value: 'ORDER_PAID', type: 'in' },
    // { label: '平台优惠券', value: 'PLATFORM_DISCOUNT_COUPON', type: 'in' },
    { label: '会员包邮', value: 'MEMBER_LOGISTICS_DISCOUNT', type: 'in' },
    // { label: '会员折扣', value: 'MEMBER_DISCOUNT', type: 'in' },
    { label: '代销运费', value: 'ORDER_FREIGHT', type: 'in' },
    // { label: '返利抵扣', value: 'REBATE_DEDUCTION', type: 'in' },
    // { label: '分销佣金', value: 'DISTRIBUTE', type: 'out' },
    { label: '采购运费', value: 'PURCHASE_ORDER_FREIGHT', type: 'out' },
    { label: '采购交易', value: 'PURCHASE_ORDER', type: 'out' },
    // { label: '代销支出', value: 'CONSIGNMENT_DISBURSE', type: 'out' },
    // { label: '退款', value: 'ORDER_AFS' },
    { label: '平台服务费(采购)', value: 'PURCHASE_ORDER_SERVICE_CHARGE', type: 'in' },
    { label: '平台服务费(代销)', value: 'SYSTEM_SERVICE', type: 'out' },
]

// 表格渲染数据
const basicOption = [
    { label: '全部', value: '' },
    { label: '代销交易', value: 'ORDER_PAID', type: 'in' },
    { label: '平台优惠券', value: 'PLATFORM_DISCOUNT_COUPON', type: 'in' },
    { label: '会员包邮', value: 'MEMBER_LOGISTICS_DISCOUNT', type: 'in' },
    { label: '会员折扣', value: 'MEMBER_DISCOUNT', type: 'in' },
    { label: '代销运费', value: 'ORDER_FREIGHT', type: 'in' },
    { label: '返利抵扣', value: 'REBATE_DEDUCTION', type: 'in' },
    { label: '平台服务费(代销)', value: 'SYSTEM_SERVICE', type: 'out' },
    { label: '分销佣金', value: 'DISTRIBUTE', type: 'out' },
    { label: '采购运费', value: 'PURCHASE_ORDER_FREIGHT', type: 'out' },
    { label: '采购交易', value: 'PURCHASE_ORDER', type: 'out' },
    { label: '代销支出', value: 'CONSIGNMENT_DISBURSE', type: 'out' },
    { label: '退款', value: 'ORDER_AFS' },
    { label: '平台服务费(采购)', value: 'PURCHASE_ORDER_SERVICE_CHARGE', type: 'in' },
]

const total = ref(0)

/** 对账单概况 */
const account = ref({
    income: 0,
    incomeCount: 0,
    payout: 0,
    payoutCount: 0,
})

const checkedData = ref<ApiFinanceItem[]>([]) // 选中数据
const tableData = ref<ApiFinanceItem[]>([]) // 表格数据

const columns = [
    {
        label: '关联订单号',
        labelWidth: 85,
        prop: 'orderNo',
        valueType: 'copy',
        fieldProps: {
            placeholder: '请输入关联订单号',
        },
    },
    {
        label: '交易类型',
        labelWidth: 75,
        prop: 'transactionType',
        valueType: 'select' as SearchType,
        options: basicOptions,
        fieldProps: {
            placeholder: '请选择',
        },
    },
    {
        label: '交易流水号',
        labelWidth: 85,
        prop: 'tradeNo',
        valueType: 'copy',
        fieldProps: {
            placeholder: '请输入交易流水号',
        },
    },
    {
        label: '交易时间',
        prop: 'createTime',
        valueType: 'date-picker' as SearchType,
        fieldProps: {
            type: 'daterange',
            startPlaceholder: '开始时间',
            endPlaceholder: '结束时间',
            format: 'YYYY/MM/DD',
            valueFormat: 'YYYY-MM-DD',
            onChange: (data: [Date, Date]) => {
                query.value.startDate = data ? data[0] : ''
                query.value.endDate = data ? data[1] : ''
            },
        },
    },
]

// 处理选择变化
function handleSelectionChange(selection: ApiFinanceItem[]) {
    checkedData.value = selection
}

onMounted(() => {
    init()
})

/**
 * 初始化
 */
function init() {
    query.value.current = 1
    initReconciliation()
}

/**
 * 切换顶部状态
 */
const clickTab = () => {
    query.value.current = 1
    query.value.changeType = activeTab.value || null
    initReconciliation()
}

/**
 * 每页 条
 */
const handleSizeChange = (val: number) => {
    query.value.current = 1
    query.value.size = val
    initReconciliation()
}

/**
 * 状态转换
 */
const convertStatus = (status: keyof typeof TRADESTATUS) => {
    const currentOptions = basicOption.filter((item) => item.value === status)
    if (currentOptions.length) {
        return currentOptions[0].label
    }
    return null
}

/**
 * 当前页
 */
const handleCurrentChange = (val: number) => {
    query.value.current = val
    initReconciliation()
}

/**
 * 重置表单
 */
const handleReset = () => {
    query.value.transactionType = ''
    query.value.createTime = []
    query.value.startDate = ''
    query.value.endDate = ''
    query.value.orderNo = ''
    query.value.tradeNo = ''
    initReconciliation()
}

async function initReconciliation() {
    const { current, size, changeType, startDate, endDate, transactionType, orderNo, tradeNo } = query.value
    let tempObj = {
        page: {
            current,
            size,
        },
        changeType,
        startDate,
        endDate,
        transactionType,
        orderNo,
        tradeNo,
    }
    const { code, data } = await doGetFinance(tempObj)
    if (code === 200) {
        account.value = data.statistics
        tableData.value = data.records
        total.value = data.total
    } else {
        ElMessage.error('获取数据失败')
    }
}

const handleExport = async () => {
    let params: any = {}
    if (checkedData.value?.length) {
        params.exportIds = checkedData.value?.map((item) => item.id) || []
    } else {
        const { changeType, startDate, endDate } = query.value
        params = { changeType, startDate, endDate }
    }
    const { code, msg } = await doPostExportStatementData(params)
    if (code === 200) {
        ElMessage.success({ message: msg || '导出成功' })
    } else {
        ElMessage.error({ message: msg || '导出失败' })
    }
}
</script>

<template>
    <el-config-provider value-on-clear="" :empty-values="[undefined, null]">
        <SchemaForm v-model="query" :columns="columns" @searchHandle="initReconciliation" @handleReset="handleReset">
            <template #otherOperations>
                <el-button type="primary" @click="handleExport">导出</el-button>
            </template>
        </SchemaForm>
    </el-config-provider>
    <div class="grey_bar"></div>
    <div class="tab_container">
        <div class="fiance-tabs">
            <el-tabs v-model="activeTab" @tab-change="clickTab">
                <el-tab-pane label="全部" name=""></el-tab-pane>
                <el-tab-pane label="收入" name="INCREASE"></el-tab-pane>
                <el-tab-pane label="支出" name="REDUCE"></el-tab-pane>
            </el-tabs>
            <div class="fiance-tabs__export">
                <QIcon name="icon-wenhao" color="#333" size="18px" @click="showDescriptionDialog = true" />
            </div>
        </div>
    </div>
    <div class="handle_container">
        <span v-if="activeTab !== 'REDUCE'" style="margin-right: 20px">收入：{{ account.income && divTenThousand(account.income) }}元</span>
        <span v-if="activeTab !== 'INCREASE'">支出：{{ account.payout && divTenThousand(account.payout) }}元</span>
    </div>
    <div class="table_container">
        <el-table
            empty-text="暂无数据~"
            :data="tableData"
            style="width: 100%"
            :header-cell-style="{
                backgroundColor: '#F6F8FA',
                color: '#515151',
                height: '48px',
            }"
            :cell-style="{ color: '#333', height: '60px' }"
            @selection-change="handleSelectionChange"
        >
            <template #empty> <ElTableEmpty /> </template>
            <el-table-column type="selection" width="30" fixed="left" />
            <el-table-column label="交易时间">
                <template #default="{ row }">
                    <div>{{ row.tradeTime }}</div>
                </template>
            </el-table-column>
            <el-table-column label="交易流水号">
                <template #default="{ row }">
                    <div>{{ row.tradeNo }}</div>
                </template>
            </el-table-column>
            <el-table-column label="关联订单号">
                <template #default="{ row }">
                    <div>{{ row.orderNo }}</div>
                </template>
            </el-table-column>
            <el-table-column label="交易类型" width="200">
                <template #default="{ row }">
                    <template v-if="row">
                        <div>{{ convertStatus(row.tradeType) }}</div>
                    </template>
                </template>
            </el-table-column>
            <el-table-column label="交易金额(元)" width="120">
                <template #default="{ row }">
                    <div>{{ row.changeType === 'INCREASE' ? '+' : '-' }}{{ row.amount && divTenThousand(row.amount) }}</div>
                </template>
            </el-table-column>
        </el-table>
    </div>
    <pageManage
        :page-size="query.size"
        :page-num="query.current"
        :total="total"
        @handle-size-change="handleSizeChange"
        @handle-current-change="handleCurrentChange"
    />
    <el-dialog v-model="showDescriptionDialog" center width="1000px">
        <template #header>
            <div style="font-size: 20px; font-weight: 600">供应商对账说明</div>
        </template>
        <ReconciliationDescription />
    </el-dialog>
</template>

<style lang="scss" scoped>
@include e(form) {
    margin-bottom: 15px;
    display: flex;
    justify-content: space-between;
    align-content: center;
}
@include b(fiance-tabs) {
    position: relative;
    @include e(export) {
        position: absolute;
        top: 10px;
        right: 0;
        cursor: pointer;
        @include flex();
        @include m(icon) {
            font-size: 22px;
            margin-left: 10px;
        }
    }
}
</style>
