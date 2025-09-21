<script setup lang="ts">
import { reactive } from 'vue'
import PageManage from '@/components/PageManage/index.vue'
import { doGetShopBalance, doGetWithdrawList, doPostWithdraw } from '@/apis/finance'
import { ElMessage } from 'element-plus'
import { QuestionFilled } from '@element-plus/icons-vue'
import { doPostExportSupplierWithdrawData } from '@/apis/overview'

interface SearchParams {
    status: string
    startDate: string
    endDate: string
    no: string
    supplier: string
}

const showDescriptionDialog = ref(false)

const { divTenThousand } = useConvert()
const pageConfig = reactive({
    size: 10,
    current: 1,
    total: 0,
})
const searchParams = ref({
    status: 'APPLYING',
    startDate: '',
    endDate: '',
    no: '',
    supplier: '',
    applyUserPhone: '',
    date: [],
})
const balanceInfo = reactive({
    withdrawing: '0',
    withdrawalTotal: '0',
    undrawn: '0',
})
const dialogTableVisible = ref(false)
const paymentVoucherImg = ref([])
const withdrawList = ref([])
const showWithdrawDialog = ref(false)
const withdrawValue = ref(0.01)

initBalance()
initWithdrawList()

const handleChangeCurrent = (e: number) => {
    pageConfig.current = e
    initWithdrawList()
}
const handleChangeSize = (e: number) => {
    pageConfig.current = 1
    pageConfig.size = e
    initWithdrawList()
}
const handleChangeTab = () => {
    pageConfig.current = 1
    initWithdrawList()
    initBalance()
}
const columns = [
    {
        label: '申请单号',
        prop: 'no',
        valueType: 'copy',
        fieldProps: {
            placeholder: '请输入',
        },
    },
    {
        label: '手机号',
        prop: 'applyUserPhone',
        valueType: 'input', // 改为input类型
        fieldProps: {
            placeholder: '请填写申请人手机',
            maxlength: 11,
            type: 'text', // 使用text类型配合v-model.number
            'v-model.number': '', // 只允许输入数字
            oninput: "value=value.replace(/[^\\d]/g,'')", // 只允许输入数字
            pattern: '^1[3-9]\\d{9}$', // 手机号码格式验证
        },
    },
    {
        label: '申请时间',
        prop: 'date',
        valueType: 'date-picker',
        fieldProps: {
            type: 'daterange',
            startPlaceholder: '开始时间',
            endPlaceholder: '结束时间',
            format: 'YYYY/MM/DD',
            valueFormat: 'YYYY-MM-DD',
        },
    },
]

const gridData = ref([
    {
        field: '供应商余额',
        describe: '供应商销售采购或代销商品，订单已完成(剔除售后成功/平台服务费)后获得的营业额',
    },
    {
        field: '累计提现',
        describe: '已提现到账的累计金额',
    },
    {
        field: '提现中',
        describe: '提现申请已提交成功，待平台审核、打款',
    },
    {
        field: '提现',
        describe: '提现需向平台端发起提现申请，待平台端审核通过后打款',
    },
    {
        field: '待审核',
        describe: '提现申请已提交成功，待平台审核',
    },
    {
        field: '已到账',
        describe: '审核已通过并打款成功，线下打款可查看付款凭证',
    },
    {
        field: '已拒绝',
        describe: '该笔提现申请未获得通过',
    },
])

// 商家端 'SHOP'，供应商 'SUPPLIER'
const type = 'SUPPLIER'
const handleSubmit = async () => {
    try {
        if (withdrawValue.value < 0.01) {
            return ElMessage.error('输入值最小为0.01')
        }
        if (withdrawValue.value > Number(divTenThousand(balanceInfo.undrawn))) {
            return ElMessage.error('提现金额不可大于供应商余额')
        }
        const { code, msg } = await doPostWithdraw(withdrawValue.value * 10000, type)
        if (code === 200) {
            ElMessage.success('申请成功')
            showWithdrawDialog.value = false
            initBalance()
            initWithdrawList()
        } else {
            ElMessage.error(msg ? msg : '申请失败')
        }
    } catch (error) {
        ElMessage.error('申请失败')
    }
}

/**
 * 重置表单
 */
const handleReset = () => {
    ;(Object.keys(searchParams.value) as (keyof SearchParams)[]).forEach((key) => (searchParams.value[key] = ''))
    searchParams.value.status = 'APPLYING'
    initWithdrawList()
}
const handleCloseDialog = () => {
    withdrawValue.value = 0.01
}
const handleShowDialog = () => {
    showWithdrawDialog.value = true
}
async function initBalance() {
    const { code, data, msg } = await doGetShopBalance()
    if (code === 200) {
        balanceInfo.undrawn = data.undrawn
        balanceInfo.withdrawalTotal = data.withdrawalTotal
        balanceInfo.withdrawing = data.withdrawing
    } else {
        ElMessage.error(msg ? msg : '获取余额信息失败')
    }
}
async function initWithdrawList() {
    if (Array.isArray(searchParams.value.date)) {
        searchParams.value.startDate = searchParams.value.date[0]
        searchParams.value.endDate = searchParams.value.date[1]
    }
    const { code, data, msg } = await doGetWithdrawList({ ...pageConfig, ...searchParams.value, type })
    if (code === 200) {
        withdrawList.value = data.records
        pageConfig.total = data.total
    } else {
        ElMessage.error(msg ? msg : '获取提现列表失败')
    }
}
function convertStatus(val: string) {
    const statusType: { [x: string]: string } = {
        APPLYING: '待审核',
        SUCCESS: '已到账',
        CLOSED: '已拒绝',
        FORBIDDEN: '已拒绝',
    }
    return statusType[val]
}

const checkedData = ref<any[]>([])
const handleExport = async () => {
    const params: any = {}
    if (checkedData.value.length) {
        params.exportIds = checkedData.value.map((item) => item.id)
    }
    const { code, msg } = await doPostExportSupplierWithdrawData(params)
    if (code === 200) {
        ElMessage.success({ message: msg || '导出成功' })
    } else {
        ElMessage.error({ message: msg || '导出失败' })
    }
}

/**
 * 显示支付凭证
 */
const handleShowPaymentVoucher = (row: any) => {
    dialogTableVisible.value = true
    paymentVoucherImg.value = row.paymentVoucher.split(',')
}

/**
 * 隐藏支付凭证
 */
const handleClosePaymentVoucher = () => {
    dialogTableVisible.value = false
}
</script>

<template>
    <el-config-provider value-on-clear="" :empty-values="[undefined, null]">
        <SchemaForm v-model="searchParams" :columns="columns" is-empty @searchHandle="initWithdrawList" @handleReset="handleReset">
            <template #otherOperations>
                <el-button type="primary" @click="handleExport">导出</el-button>
            </template>
        </SchemaForm>
    </el-config-provider>
    <div class="grey_bar"></div>
    <div class="settle">
        <div class="settle__item">
            <div>
                <div class="settle__item--title" style="display: flex; align-items: center">供应商余额</div>
                <div class="settle__item--price">{{ divTenThousand(balanceInfo.undrawn).toFixed(2) }}</div>
                <div>
                    <el-button type="primary" @click="handleShowDialog">提现</el-button>
                </div>
            </div>
            <img src="@/assets/images/storeBalance.png" />
        </div>
        <div class="settle__item">
            <div>
                <div class="settle__item--title">累计提现</div>
                <div class="settle__item-bottom">
                    <div class="settle__item--price">{{ divTenThousand(balanceInfo.withdrawalTotal).toFixed(2) }}</div>
                </div>
            </div>
            <img src="@/assets/images/accumulatedWithdrawal.png" />
        </div>
        <div class="settle__item">
            <div>
                <div class="settle__item--title">提现中</div>
                <div class="settle__item-bottom">
                    <div class="settle__item--price">{{ divTenThousand(balanceInfo.withdrawing).toFixed(2) }}</div>
                </div>
            </div>
            <img src="@/assets/images/withdrawalIn.png" />
        </div>
    </div>
    <div class="tab_container" style="position: relative">
        <el-tabs v-model="searchParams.status" @tab-change="handleChangeTab">
            <el-tab-pane label="待审核" name="APPLYING" />
            <el-tab-pane label="已到账" name="SUCCESS" />
            <el-tab-pane label="已拒绝" name="FORBIDDEN" />
        </el-tabs>
        <QIcon
            name="icon-wenhao"
            size="18px"
            color="#333"
            style="position: absolute; right: 15px; top: 25%; cursor: pointer"
            @click="showDescriptionDialog = true"
        />
    </div>

    <div class="table_container">
        <el-table
            :data="withdrawList"
            style="width: 100%"
            :header-cell-style="{
                backgroundColor: '#F6F8FA',
                color: '#333',
                height: '48px',
            }"
            :cell-style="{ color: '#333', height: '50px' }"
            @selection-change="(data) => (checkedData = data)"
        >
            <template #empty> <ElTableEmpty /> </template>
            <el-table-column width="35" fixed="left" type="selection" min-width="170" />
            <el-table-column label="申请单号" prop="no" min-width="180" />
            <el-table-column label="申请时间" prop="createTime" min-width="180"></el-table-column>
            <el-table-column
                v-if="searchParams.status === 'SUCCESS' || searchParams.status === 'FORBIDDEN'"
                label="审批时间"
                prop="tradeTime"
                width="250"
            ></el-table-column>
            <el-table-column label="状态" width="140">
                <template #default="{ row }">
                    <div :style="{ color: row.status === 'APPLYING' ? '#FD9224' : row.status === 'SUCCESS' ? '#00BB2C' : '#F54319' }">
                        {{ convertStatus(row.status) }}
                    </div>
                </template>
            </el-table-column>
            <el-table-column label="提现金额(元)" width="140">
                <template #default="{ row }">
                    <div>
                        {{ divTenThousand(row.drawType.amount) }}
                        <QIcon
                            v-if="row.offline && row.paymentVoucher"
                            name="icon-pingzhengzhongxin"
                            size="20"
                            svg
                            style="position: relative; top: 2px; cursor: pointer"
                            @click="() => handleShowPaymentVoucher(row)"
                        />
                    </div>
                </template>
            </el-table-column>
            <el-table-column label="申请人" prop="applyUserNickName" width="150" />
            <el-table-column label="手机号" prop="applyUserPhone" width="150" />
            <el-table-column v-if="searchParams.status === 'FORBIDDEN'" label="拒绝说明" prop="reason" width="140" align="right">
                <template #default="{ row }">
                    <el-tooltip effect="dark" :content="row.reason" placement="bottom-end">
                        <el-link type="primary" :underline="false">拒绝说明</el-link>
                    </el-tooltip>
                </template>
            </el-table-column>
        </el-table>
    </div>

    <page-manage
        :page-num="pageConfig.current"
        :page-size="pageConfig.size"
        :total="pageConfig.total"
        @handle-current-change="handleChangeCurrent"
        @handle-size-change="handleChangeSize"
    />
    <el-dialog v-model="showWithdrawDialog" title="提现申请" center width="320px" top="25vh" @close="handleCloseDialog">
        <el-form>
            <el-form-item label="提现金额" required>
                <el-input-number v-model="withdrawValue" :min="0.01" :precision="2" :controls="false" />
            </el-form-item>
        </el-form>
        <template #footer>
            <el-button @click="showWithdrawDialog = false">取消</el-button>
            <el-button type="primary" @click="handleSubmit"> 确定 </el-button>
        </template>
    </el-dialog>
    <el-dialog v-model="showDescriptionDialog" width="740px" center>
        <template #header>
            <div style="font-size: 18px; font-weight: 600; color: #000">供应商结算说明</div>
        </template>
        <el-table :data="gridData" border :header-cell-style="{ background: '#BEBEBE', color: '#000', height: '36px' }">
            <el-table-column property="field" label="字段" width="120" />
            <el-table-column property="describe" label="说明" />
        </el-table>
    </el-dialog>
    <!--体现凭证-->
    <el-image-viewer v-if="dialogTableVisible" :url-list="paymentVoucherImg" @close="handleClosePaymentVoucher" />
</template>

<style lang="scss" scoped>
@include b(settle) {
    color: #000;
    @include flex(space-between);
    padding: 16px;
    @include e(item) {
        width: 390px;
        height: 132px;
        box-sizing: border-box;
        display: flex;
        justify-content: space-between;
        padding: 20px 30px 15px 30px;
        border-radius: 6px;
        border: 2px solid #e9ecf0;
        img {
            width: 65px;
            height: 55px;
            position: relative;
            top: 22px;
        }
        @include m(title) {
            font-size: 14px;
            color: #999;
        }
        @include m(price) {
            font-size: 24px;
            font-weight: 400;
            margin: 8px 0;
            &::before {
                content: '￥';
                display: inline-block;
                font-size: 13px;
                vertical-align: baseline;
            }
        }
        &:nth-child(n) {
            background-size: 100% 100%;
            background-repeat: no-repeat;
        }
    }
    @include e(item-bottom) {
        @include flex(space-between, flex-end);
    }
}
@include b(dialog-line) {
    line-height: 2;
}
</style>
