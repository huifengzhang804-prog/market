<script setup lang="ts">
import { useRouter } from 'vue-router'
import DateUtil from '@/utils/date'
import { doGetFinance } from '@/apis/finance/index'
import { ElMessage } from 'element-plus'
import DescriptionDialog from './description-dialog.vue'
import { doPostExportStatementData } from '@/apis/overview'
import type { QueryFrom, TableFromType } from '@/views/finance/types/index.ts'
import SchemaForm from '@/components/SchemaForm.vue'
import BetterPageManage from '@/components/BetterPageManage/BetterPageManage.vue'
import { doGetShopList, doGetAllStoresList } from '@/apis/shops'

/**
 * 交易类型
 * @params ORDER_PAID 订单支付(订单详情)
 * @params ORDER_AFS 售后退款(详情)
 * @params USER_BALANCE_RECHARGE 会员余额充值(会员详情)
 * @params PAID_MEMBER_OPEN 付费会员开通(会员详情)
 * @params PAID_MEMBER_RENEW 付费会员续费(会员详情)
 * @params DISTRIBUTE 分销佣金
 */
enum TRADESTATUS {
    ORDER_PAID,
    ORDER_AFS,
    USER_BALANCE_RECHARGE,
    PAID_MEMBER_OPEN,
    PAID_MEMBER_RENEW,
    DISTRIBUTE,
}

const $router = useRouter()
const { divTenThousand } = useConvert()
const activeTab = ref('')
const dateTool = new DateUtil()
const showDescriptionDialog = ref(false)
const shopSearchList = ref<any[]>([])

/**
 * 切换顶部状态
 */
const query = ref<QueryFrom>({
    transactionType: '',
    changeType: '', //   ""->全部;INCREASE->收入;REDUCE->支出
    createTime: [],
    startDate: '',
    endDate: '',
    keyword: '',
    orderNo: '',
    tradeNo: '',
    targetShopId: '',
    current: 1,
    size: 10,
})
const total = ref(0)

/** 对账单概况 */
const account = ref({
    income: 0,
    incomeCount: 0,
    payout: 0,
    payoutCount: 0,
})

const basicOptions = [
    { label: '全部', value: '' },
    { label: '用户充值', value: 'USER_BALANCE_RECHARGE', type: 'in' },
    { label: '购买付费会员', value: 'PAID_MEMBER_OPEN', type: 'in' },
    { label: '续费付费会员', value: 'PAID_MEMBER_RENEW', type: 'in' },
    { label: '平台服务费', value: 'SYSTEM_SERVICE', type: 'in' },
    { label: '平台服务费(采购)', value: 'PURCHASE_ORDER_SERVICE_CHARGE', type: 'in' },
    { label: '积分交易', value: 'INTEGRAL_GOODS_DEAL', type: 'in' },
    { label: '会员折扣', value: 'MEMBER_DISCOUNT', type: 'out' },
    { label: '会员包邮', value: 'MEMBER_LOGISTICS_DISCOUNT', type: 'out' },
    { label: '平台优惠券', value: 'PLATFORM_DISCOUNT_COUPON', type: 'out' },
    { label: '返利抵扣', value: 'REBATE_DEDUCTION', type: 'out' },
    { label: '充值赠送', value: 'RECHARGE_GIFT', type: 'out' },
]

const columns = [
    {
        label: '关联订单号',
        labelWidth: 85,
        prop: 'orderNo',
        valueType: 'copy',
    },
    {
        label: '交易类型',
        prop: 'transactionType',
        valueType: 'select',
        options: basicOptions,
    },
    {
        label: '所属商家',
        labelWidth: 75,
        prop: 'targetShopId',
        valueType: 'select',
        options: shopSearchList,
        fieldProps: {
            props: {
                value: 'id',
                label: 'name',
                expandTrigger: 'hover',
            },
            filterable: true,
            remote: true,
            reserveKeyword: true,
            emptyValues: [null, undefined, ''],
            remoteMethod: (val: string) => {
                shopSearchRemote(val)
            },
        },
    },
    {
        label: '交易流水号',
        labelWidth: 85,
        prop: 'tradeNo',
        valueType: 'copy',
    },
    {
        label: '交易时间',
        prop: 'createTime',
        valueType: 'date-picker',
        fieldProps: {
            type: 'daterange',
            format: 'YYYY/MM/DD',
            valueFormat: 'YYYY-MM-DD',
            onChange: (data: Array<Date>) => {
                query.value.startDate = data ? data[0] : ''
                query.value.endDate = data ? data[1] : ''
            },
        },
    },
]
/**
 * 当前页全选
 */
const checkedData = ref<TableFromType[]>([]) // 选中数据
const tableData = ref<TableFromType[]>([]) // 表格数据

// 处理选择变化
function handleSelectionChange(selection: TableFromType[]) {
    checkedData.value = selection
}

const shopSearchRemote = async (query: string) => {
    if (query) {
        const { data } = (await doGetAllStoresList({ shopName: query, includeSupplier: true })) as any
        shopSearchList.value =
            data?.map((v: any) => {
                return {
                    ...v,
                    label: v.name,
                    value: v.id,
                }
            }) || []
    } else {
        shopSearchList.value = []
    }
}
/**
 * 表格排序
 */
// let sortEnum = ref('')
// const sortTableList = (label: string) => {
//     switch (label) {
//         case '收支金额(元)':
//             sortEnum.value = sortEnum.value === 'AMOUNT_ASC' ? 'AMOUNT_DESC' : 'AMOUNT_ASC'
//             initReconciliation()
//             break
//         case '交易时间':
//             sortEnum.value = sortEnum.value === 'TIME_ASC' ? 'TIME_DESC' : 'TIME_ASC'
//             initReconciliation()
//             break
//         default:
//             break
//     }
// }

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
 * tab页切换
 */
const clickTab = () => {
    query.value.changeType = activeTab.value || ''
    query.value.current = 1

    initReconciliation()
}

/**
 * @method handleSizeChange
 * @description 每页 条
 */
const handleSizeChange = (val: number) => {
    query.value.current = 1
    query.value.size = val
    initReconciliation()
}

/**
 * @method handleCurrentChange
 * @description 当前页
 */
const handleCurrentChange = (val: number) => {
    query.value.current = val
    initReconciliation()
}

const handleReset = () => {
    query.value.keyword = ''
    query.value.transactionType = ''
    query.value.createTime = []
    query.value.startDate = ''
    query.value.endDate = ''
    query.value.orderNo = ''
    query.value.tradeNo = ''
    query.value.targetShopId = ''
    initReconciliation()
}
/**
 * 不同类型跳转详情
 */
const handleJump = (row: any) => {
    const navMap = {
        ORDER_PAID: {
            path: 'order/details',
            params: {
                orderNo: row.orderNo,
                shopId: row.shopId,
            },
        },
        ORDER_AFS: {
            path: 'order/afs/details',
            params: {
                afsNo: row.tradeDetail.afsNo || '',
                packageId: row.tradeDetail.packageId || '',
                orderNo: row.orderNo,
            },
        },
        USER_BALANCE_RECHARGE: {
            path: 'vip/base/vip/details',
            params: {
                userId: row.userId,
            },
        },
        PAID_MEMBER_OPEN: {
            path: 'vip/base/vip/details',
            params: {
                userId: row.userId,
            },
        },
        PAID_MEMBER_RENEW: {
            path: 'vip/base/vip/details',
            params: {
                userId: row.userId,
            },
        },
        DISTRIBUTE: {
            path: 'order/details',
            params: {
                orderNo: row.orderNo,
                shopId: row.shopId,
            },
        },
    } as { [x: string]: { path: string; params: any } }
    const currentNavLink = navMap[row.tradeType]
    console.log('currentNavLink', currentNavLink)
    $router.push({
        path: currentNavLink.path,
        query: currentNavLink.params,
    })
}
/**
 * 状态转换
 */
const convertStatus = (status: keyof typeof TRADESTATUS) => {
    if (!status) return '狗啊，后端不给数据'
    return basicOptions.find((item) => item.value === status)?.label
}
async function initReconciliation() {
    const { current, size, changeType, startDate, endDate, transactionType, keyword, orderNo, tradeNo, targetShopId } = query.value
    let tempObj = {
        // sortEnum: sortEnum.value,
        page: {
            current,
            size,
        },
        changeType,
        startDate,
        endDate,
        keyword,
        transactionType,
        orderNo,
        tradeNo,
        targetShopId,
    }
    const { code, data } = (await doGetFinance(tempObj)) as any
    if (code === 200) {
        tableData.value = data.records
        total.value = data.total
        if (data.statistics) {
            account.value = data.statistics
        }
    } else {
        ElMessage.error('获取数据失败')
    }
}

const handleExport = async () => {
    let params: any = {}
    if (checkedData.value?.length) {
        params.exportIds = checkedData.value?.map((item) => item.id) || []
    } else {
        const { changeType, startDate, endDate, keyword, transactionType } = query.value
        params = { changeType, startDate, endDate, keyword, transactionType }
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
    <el-config-provider :empty-values="[undefined, null]">
        <SchemaForm v-model="query" :columns="columns" is-empty @searchHandle="initReconciliation" @handleReset="handleReset">
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
            <el-table-column label="交易时间" width="200px">
                <template #header>
                    <span style="margin-right: 5px">交易时间</span>
                </template>
                <template #default="{ row }">
                    <div>{{ row.tradeTime }}</div>
                </template>
            </el-table-column>
            <el-table-column label="交易流水号" width="210px">
                <template #default="{ row }">
                    <div>{{ row.tradeNo || '-' }}</div>
                </template>
            </el-table-column>
            <el-table-column label="关联订单号" width="210px">
                <template #default="{ row }">
                    <div>{{ row.orderNo || '-' }}</div>
                </template>
            </el-table-column>
            <el-table-column label="所属商家">
                <template #default="{ row }">
                    <div>{{ row.shopName || '-' }}</div>
                </template>
            </el-table-column>
            <el-table-column label="交易类型" width="120px">
                <template #default="{ row }">
                    <template v-if="row">
                        <div>{{ convertStatus(row.tradeType) }}</div>
                    </template>
                </template>
            </el-table-column>
            <el-table-column width="140px">
                <template #header>
                    <span style="margin-right: 5px">收支金额(元)</span>
                </template>
                <template #default="{ row }">
                    <div>{{ row.changeType === 'INCREASE' ? '+' : '-' }}{{ row.amount && divTenThousand(row.amount) }}</div>
                </template>
            </el-table-column>

            <!-- 操作按钮暂定隐藏  操作逻辑后面有变动 -->
            <!-- <el-table-column label="操作" width="60px" fixed="right">
                <template #default="{ row }">
                    <div style="color: #2e99f3; cursor: pointer" @click="handleJump(row)">详情</div>
                </template>
            </el-table-column> -->
        </el-table>
    </div>
    <BetterPageManage
        :page-size="query.size"
        :page-num="query.current"
        :total="total"
        @handle-size-change="handleSizeChange"
        @handle-current-change="handleCurrentChange"
    />
    <el-dialog v-model="showDescriptionDialog" title="平台对账说明" :width="1000" top="4vh" center>
        <description-dialog />
    </el-dialog>
</template>

<style lang="scss" scoped>
@include b(finance__header) {
    display: flex;
    align-items: center;
    justify-content: space-between;
    height: 128px;
    margin: auto;
    font-size: 14px;
    margin-bottom: 20px;
    font-size: 20px;

    @include m(income) {
        width: 48%;
        background-image: url('@/assets/image/income.jpg');
        height: 100%;
        border-radius: 4px;
        background-size: 100%; /* 设置背景图片的宽度与盒子宽度相同 */
        background-position: center; /* 背景图片居中显示 */
        background-repeat: no-repeat; /* 背景图片不重复 */
    }

    @include m(expenses) {
        height: 100%;
        width: 48%;
        background-image: url('@/assets/image/expenditure.jpg');
        border-radius: 4px;
        background-size: 100%; /* 设置背景图片的宽度与盒子宽度相同 */
        background-position: center; /* 背景图片居中显示 */
        background-repeat: no-repeat; /* 背景图片不重复 */
    }
}

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
.fs30 {
    font-size: 30px !important;
}
:deep(.el-empty) {
    padding: 0;
}
</style>
