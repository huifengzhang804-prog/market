<template>
    <div class="description">
        <div class="description__content">统计整个平台购买(续费)付费会员(支付成功)的记录，为付费会员等级变更提供依据。</div>
        <el-table :data="configData" border :header-cell-style="{ background: '#BEBEBE', color: '#000', height: '36px' }">
            <template #empty> <ElTableEmpty /> </template>
            <el-table-column prop="field" label="字段" width="140" />
            <el-table-column prop="description" label="字段说明" />
        </el-table>
    </div>
</template>

<script lang="ts" setup>
const configData = [
    {
        field: '会员等级',
        description: '该笔订单中所购买的付费会员等级',
    },
    {
        field: '有效期',
        description: '付费会员的有效期',
    },
    {
        field: '购买时间',
        description: '支付成功的时间',
    },
    {
        field: '到期时间',
        description: '到期时间 = 支付成功时间 + 有效期',
    },
    {
        field: '购买付费会员',
        description: '是指首次购买或过期后购买付费会员',
    },
    {
        field: '续费付费会员',
        description: '是指原会员等级还未过期再次购买相同等级的付费会员',
    },
]
</script>

<style lang="scss" scoped>
@include b(description) {
    @include e(content) {
        margin-bottom: 15px;
    }
}
</style>
