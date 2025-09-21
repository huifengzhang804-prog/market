<script lang="ts" setup>
import { doGetMemberTransactionList } from '@/apis/member/index'
import { OrderDetail } from '@/apis/member/types'

const route = useRoute()
const tableData = ref<OrderDetail[]>([])
const pagination = reactive({
    current: 1,
    size: 10,
    total: 0,
})
const getMemberTransactionList = async () => {
    const memberId = route.query.id
    const { success, data } = await doGetMemberTransactionList(memberId as string, pagination)
    if (success) {
        pagination.total = data?.total || 0
        tableData.value = data?.records || []
    }
}

const handleCurrentChange = (currentPage: number) => {
    pagination.current = currentPage
    getMemberTransactionList()
}

const handleSizeChange = (pageSize: number) => {
    pagination.current = 1
    pagination.size = pageSize
    getMemberTransactionList()
}
onMounted(() => getMemberTransactionList())
</script>
<template>
    <el-table :data="tableData">
        <template #empty> <ElTableEmpty /> </template>
        <el-table-column prop="orderNo" label="订单号" :width="200" />
        <el-table-column prop="goodsAmount" label="商品总价">
            <template #default="{ row }">
                {{ row.goodsAmount / 10000 }}
            </template>
        </el-table-column>
        <el-table-column prop="discountAmount" label="优惠金额">
            <template #default="{ row }">
                {{ row.discountAmount / 10000 }}
            </template>
        </el-table-column>
        <el-table-column prop="payAmount" label="实付金额">
            <template #default="{ row }">
                {{ row.payAmount / 10000 }}
            </template>
        </el-table-column>
        <el-table-column label="支付方式">
            <template #default="{ row }">
                {{ row.payType === 'BALANCE' ? '余额支付' : row.payType === 'WECHAT' ? '微信支付' : '支付宝支付' }}
            </template>
        </el-table-column>
        <el-table-column prop="createTime" label="交易时间" :width="160" />
    </el-table>
    <div class="pagination">
        <el-pagination
            v-model:current-page="pagination.current"
            v-model:page-size="pagination.size"
            :total="+pagination.total"
            :page-sizes="[10, 20, 30, 40]"
            layout="prev, pager, next, sizes"
            @current-change="handleCurrentChange"
            @size-change="handleSizeChange"
        />
    </div>
</template>

<style lang="scss" scoped>
.pagination {
    display: flex;
    justify-content: flex-end;
    padding: 15px 0;
}
</style>
