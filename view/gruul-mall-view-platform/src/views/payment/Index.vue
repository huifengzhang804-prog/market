<script setup lang="ts">
import BetterPageManage from '@/components/BetterPageManage/BetterPageManage.vue'
import { doGetPaymentHistory } from '@/apis/payment'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import RemarkFlag from '@/components/remark/remark-flag.vue'
import RemarkPopup from '@/components/remark/remark-popup.vue'
import QTable from '@/components/qszr-core/packages/q-table/QTable'
import QTableColumn from '@components/qszr-core/packages/q-table/q-table-column.vue'

import type { PaymentHistoryItem } from '@/views/payment/types'

const time = ref('')
const params = reactive({ startTime: '', endTime: '', userNickname: '' })
const pageConfig = reactive({
    size: 10,
    current: 1,
    total: 0,
})
const paymentHistoryList = ref<PaymentHistoryItem[]>([])
const remark = reactive({
    dialog: false,
    ids: [] as string[],
    remarkMessageText: '',
    multiSelect: [] as PaymentHistoryItem[],
})
const payTypeCn = {
    ALIPAY: '支付宝',
    WECHAT: '微信',
    BALANCE: '余额',
}
const dealTypeCn = {
    SYSTEM_GIVE: '系统赠送',
    PERSONAL_CHARGING: '个人充值',
    SYSTEM_CHARGING: '系统充值',
    SHOPPING_PURCHASE: '购物消费',
    PURCHASE_MEMBER: '购买会员',
    REFUND_SUCCEED: '退款成功',
    WITHDRAW: '提现',
}
const convert = useConvert()

initGetPaymentHistory()

async function initGetPaymentHistory() {
    if (!time.value) {
        params.endTime = ''
        params.startTime = ''
    }
    if (Array.isArray(time.value)) {
        params.startTime = time.value[0]
        params.endTime = time.value[1]
        console.log('params.startTime ', params.startTime)
    }
    const { data, msg, code } = await doGetPaymentHistory({ ...pageConfig, ...params })
    if (code !== 200) {
        ElMessage.error(msg || '获取储值订单列表失败')
        return
    }
    pageConfig.size = data.size
    pageConfig.current = data.current
    pageConfig.total = data.total
    paymentHistoryList.value = data.records
}
/**
 * 分页器
 * @param {*} value
 */
const handleSizeChange = (value: number) => {
    pageConfig.current = 1
    pageConfig.size = value
    initGetPaymentHistory()
}
const handleCurrentChange = (value: number) => {
    pageConfig.current = value
    initGetPaymentHistory()
}
const handleRemarkSuccess = () => {
    remark.multiSelect = []
    initGetPaymentHistory()
}
// 批量备注
const handleRemarks = () => {
    if (!remark.multiSelect.length) return ElMessage.error('请先选择订单')
    remark.ids = remark.multiSelect.map((item) => item.id)
    remark.dialog = true
}
// 单个备注
const handleSeeRemark = (row: PaymentHistoryItem) => {
    remark.ids = [row.id]
    if (row.remark) {
        remark.remarkMessageText = row.remark
    }
    remark.dialog = true
}
</script>

<template>
    <el-row justify="space-between" align="middle">
        <el-col :span="12">
            <span>付款时间</span>
            <el-date-picker
                v-model="time"
                style="width: 80%; margin-left: 10px"
                type="datetimerange"
                range-separator="-"
                format="YYYY/MM/DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                @change="initGetPaymentHistory"
            />
        </el-col>
        <el-col :span="8">
            <el-input
                v-model="params.userNickname"
                clearable
                placeholder="订单编号或会员昵称"
                class="input-with-select"
                @change="initGetPaymentHistory"
            >
                <template #append>
                    <el-button :icon="Search" @click="initGetPaymentHistory" />
                </template>
            </el-input>
        </el-col>
    </el-row>
    <q-table v-model:checkedItem="remark.multiSelect" :data="paymentHistoryList" style="margin-top: 20px" class="tab">
        <template #header="{ row }: { row: PaymentHistoryItem }">
            <div class="tab__header">
                <div>
                    <span>订单编号:{{ row.id }}</span>
                    <span style="margin-left: 50px">付款时间:{{ row.createTime }}</span>
                </div>
                <remark-flag :content="row.remark" @see-remark="handleSeeRemark(row)" />
            </div>
        </template>
        <q-table-column label="会员信息">
            <template #default="{ row }: { row: PaymentHistoryItem }">
                <div class="member">
                    <el-avatar style="width: 68px; height: 68px" shape="square" size="large" :src="row.userHeadPortrait" fit="cover" />
                    <div style="margin-left: 10px; text-align: left">
                        <span>{{ row.userNikeName }} </span>
                    </div>
                    <!-- <div class="member info">
                        <span style="margin-top: 10px">会员昵称</span> 
                        <span style="margin-top: 10px">（1231231231）</span>
                    </div> -->
                </div>
            </template>
        </q-table-column>
        <q-table-column label="充值金额">
            <template #default="{ row }: { row: PaymentHistoryItem }">
                <div class="tab__client_box">{{ row.money && convert.divTenThousand(row.money) }}</div>
            </template>
        </q-table-column>
        <q-table-column label="充值类型" class="rate_size">
            <template #default="{ row }: { row: PaymentHistoryItem }">
                <div>{{ dealTypeCn[row.dealType] }}</div>
            </template>
        </q-table-column>
        <q-table-column label="支付方式" class="rate_size">
            <template #default="{ row }: { row: PaymentHistoryItem }">
                <div class="avatar_text money_text" style="width: 68px; padding-left: 10px">{{ row.payType ? payTypeCn[row.payType] : '—' }}</div>
            </template>
        </q-table-column>
    </q-table>
    <!-- tab表格e -->
    <el-row justify="space-between" align="bottom">
        <el-button type="primary" class="caozuo_btn" plain round bg style="width: 82px; height: 36px; background: #ecf5fd" @click="handleRemarks">
            批量备注
        </el-button>
        <BetterPageManage
            :load-init="true"
            :page-size="pageConfig.size"
            :page-num="pageConfig.current"
            :total="pageConfig.total"
            @reload="() => {}"
            @handle-size-change="handleSizeChange"
            @handle-current-change="handleCurrentChange"
        />
    </el-row>
    <remark-popup
        v-model:isShow="remark.dialog"
        v-model:ids="remark.ids"
        v-model:remark="remark.remarkMessageText"
        remark-type="PAYMENT"
        @success="handleRemarkSuccess"
    />
</template>

<style scoped lang="scss">
@include b(tab) {
    // height: calc(100vh - 260px);
    height: calc(100vh - 220px);
    overflow-y: scroll;
    transition: height 0.5s;
    @include e(header) {
        @include flex(space-between);
        width: 100%;
        @include m(icon) {
            width: 20%;
            text-align: right;
        }
    }
}
@include b(member) {
    width: 150px;
    @include flex();
    justify-content: flex-start;
}
@include b(info) {
    flex-direction: column;
    height: 50px;
    justify-content: flex-start;
    & > span:nth-child(1) {
        color: #2e99f3;
        font-size: 12px;
        text-align: LEFT;
    }
}
</style>
