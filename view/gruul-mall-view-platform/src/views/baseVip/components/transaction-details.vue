<script setup lang="ts">
import PageManage from '@/components/PageManage.vue'
import { ElMessage } from 'element-plus'
import { doGetTradeList } from '@/apis/member'

enum PAYTYPE {
    BALANCE,
    WECHAT,
    ALIPAY,
}

const $props = defineProps({
    userId: {
        type: [String, Number],
        default: '',
    },
})
const { divTenThousand } = useConvert()
const pageConfig = reactive({
    current: 1,
    size: 10,
    total: 0,
})
const tradeList = ref([])
const staticPayType = {
    BALANCE: '余额支付',
    WECHAT: '微信支付',
    ALIPAY: '支付宝支付',
} as Record<keyof typeof PAYTYPE, string>

initTradeList()

const handleSizeChange = (e: number) => {
    pageConfig.size = e
    pageConfig.current = 1
    initTradeList()
}
const handleCurrentChange = (e: number) => {
    pageConfig.current = e
    initTradeList()
}

async function initTradeList() {
    const { code, data } = await doGetTradeList($props.userId as string, pageConfig)
    if (code === 200) {
        tradeList.value = data.records
        pageConfig.total = data.total
    } else {
        ElMessage.error('获取交易明细失败')
    }
}

function convertPayType(status: keyof typeof PAYTYPE) {
    return staticPayType[status]
}
</script>

<template>
    <div>
        <el-table :data="tradeList" style="width: 100%" :header-row-style="{ background: '#f6f8fa' }" :row-style="{ height: '60px' }">
            <template #empty> <ElTableEmpty /> </template>
            <el-table-column prop="createTime" label="交易时间" width="230px" />
            <el-table-column prop="orderNo" label="订单编号" width="248px" />
            <el-table-column prop="createTime" label="商品总价" align="center">
                <template #default="{ row }">
                    {{ divTenThousand(row.goodsAmount) }}
                </template>
            </el-table-column>
            <el-table-column label="优惠金额" align="center">
                <template #default="{ row }">
                    {{ divTenThousand(row.discountAmount) }}
                </template>
            </el-table-column>
            <!-- <el-table-column label="积分抵扣"> </el-table-column> -->
            <el-table-column label="实付金额" align="center" width="141px">
                <template #default="{ row }">
                    {{ divTenThousand(row.payAmount) }}
                    <!-- <span v-if="row.freightAmount">运费:({{ divTenThousand(row.freightAmount) }})</span> -->
                </template>
            </el-table-column>
            <el-table-column label="支付方式" align="center">
                <template #default="{ row }">
                    {{ convertPayType(row.payType) }}
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
    </div>
</template>

<style scoped></style>
