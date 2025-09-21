<template>
    <div class="dialog__top">
        <div>
            商品金额：<span style="color: #f70707"> {{ divTenThousand(currentrow?.merged.salePrice).mul(currentrow.merged.num)?.toFixed(2) }}</span>
        </div>
        <div>
            优惠金额：<span style="color: #f70707"> {{ divTenThousand(currentrow?.discountAmount)?.toFixed(2) }}</span>
        </div>
        <div>
            小计： <span style="color: #f70707"> {{ divTenThousand(currentrow?.subtotal)?.toFixed(2) }}</span>
        </div>
    </div>
    <el-table :data="currentrow?.discounts" style="width: 100%" border>
        <template #empty> <ElTableEmpty /> </template>
        <el-table-column label="层级" align="center">
            <template #default="{ row }">{{ row.level }}</template>
        </el-table-column>
        <el-table-column label="优惠项" width="300px">
            <template #default="{ row }">{{ row.name }}</template>
        </el-table-column>
        <el-table-column label="金额" align="center">
            <template #default="{ row }">{{ divTenThousand(row.amount)?.toFixed(2) }}</template>
        </el-table-column>
    </el-table>
</template>

<script lang="ts" setup>
defineProps({
    currentrow: {
        type: Object,
        default: () => ({}),
    },
})
const { divTenThousand } = useConvert()
</script>

<style lang="scss" scoped>
@include b(dialog) {
    @include e(top) {
        display: flex;
        align-items: center;
        justify-content: space-between;
        margin-bottom: 20px;
    }
}
</style>
