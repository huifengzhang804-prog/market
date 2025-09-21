<script setup lang="ts">
import PageManage from '@/components/PageManage.vue'
import { ElMessage } from 'element-plus'
import { doGetBalanceHistory } from '@/apis/member'
import type { PAYTYPE } from '../types'

const $props = defineProps({
    userId: {
        type: String,
        default: '',
    },
})
const { divTenThousand } = useConvert()
const pageConfig = reactive({
    current: 1,
    size: 10,
    total: 0,
    dealAggregationType: 'ALL',
})
const balanceList = ref([])
const handleSizeChange = (e: number) => {
    pageConfig.size = e
    pageConfig.current = 1
    initBalanceList()
}
const handleCurrentChange = (e: number) => {
    pageConfig.current = e
    initBalanceList()
}
const staticPayType = {
    SYSTEM_GIVE: '系统赠送',
    PERSONAL_CHARGING: '个人充值',
    SYSTEM_CHARGING: '系统充值',
    SHOPPING_PURCHASE: '购物消费',
    PURCHASE_MEMBER: '购买会员',
    REFUND_SUCCEED: '退款成功',
    WITHDRAW: '提现',
    GIVE: '赠送',
} as Record<keyof typeof PAYTYPE, string>

initBalanceList()

async function initBalanceList() {
    const { code, data } = await doGetBalanceHistory($props.userId, pageConfig)
    if (code === 200) {
        balanceList.value = data.records
        pageConfig.total = data.total
    } else {
        ElMessage.error('获取交易明细失败')
    }
}
function convertDealTip(type: keyof typeof PAYTYPE) {
    return staticPayType[type]
}
</script>

<template>
    <el-table :data="balanceList" style="width: 100%" :header-row-style="{ background: '#f6f8fa' }" :row-style="{ height: '60px' }">
        <template #empty> <ElTableEmpty /> </template>
        <el-table-column prop="createTime" label="交易时间" />
        <el-table-column label="交易说明">
            <template #default="{ row }">
                <div>{{ convertDealTip(row.dealType) }}</div>
            </template>
        </el-table-column>
        <el-table-column label="金额">
            <template #default="{ row }">
                <div>{{ row.changeType === 'REDUCE' ? '-' : '+' }}{{ divTenThousand(row.money) }}</div>
            </template>
        </el-table-column>
        <el-table-column label="类型">
            <template #default="{ row }">
                <div>{{ row.changeType === 'REDUCE' ? '支出' : '收入' }}</div>
            </template>
        </el-table-column>
    </el-table>
    <page-manage
        :page-size="pageConfig.size"
        :page-num="pageConfig.current"
        :total="pageConfig.total"
        @handle-size-change="handleSizeChange"
        @handle-current-change="handleCurrentChange"
    />
</template>

<style scoped></style>
