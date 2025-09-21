<template>
    <div class="description">
        <div class="description__content">
            库存明细是以SKU为维度统计本店铺所有（自有、采购）商品库存变动的流水，记录了商品的发布、编辑、销售、退货、库存盘点、调拨 、采购等<br />
            操作，助力商家高效库存管理和结算。
        </div>
        <div class="description__title">
            <span class="description__title-icon"></span>
            <span>入库明细</span>
        </div>
        <el-table :data="inboundTableData" border :header-cell-style="{ background: '#BEBEBE', color: '#000', height: '36px' }">
            <template #empty> <ElTableEmpty /> </template>
            <el-table-column prop="type" label="出入库类型" width="120" />
            <el-table-column prop="source" label="商品来源" width="120" />
            <el-table-column prop="recordMechanism" label="记录时机" width="230" />
            <el-table-column prop="relatedOrderNo" label="关联订单号" width="180" />
            <el-table-column prop="description" label="说明" />
        </el-table>
        <div class="description__title">
            <span class="description__title-icon"></span>
            <span>出库明细</span>
        </div>
        <el-table :data="outboundTableData" border :header-cell-style="{ background: '#BEBEBE', color: '#000', height: '36px' }">
            <template #empty> <ElTableEmpty /> </template>
            <el-table-column prop="type" label="出入库类型" width="120" />
            <el-table-column prop="source" label="商品来源" width="120" />
            <el-table-column prop="recordMechanism" label="记录时机" width="230" />
            <el-table-column prop="relatedOrderNo" label="关联订单号" width="180" />
            <el-table-column prop="description" label="说明" />
        </el-table>
        <div class="description__title">
            <QIcon name="icon-tishi" size="20px" style="position: relative; top: 1px; margin-right: 3px" />
            <span>特别说明</span>
        </div>
        <div>
            1、含无限库存的商品不支持库存盘点和出入库操作<br />
            2、代销商品库存变动由供应商端记录，店铺端不记录<br />
            3、下架、违规下架、删除 、添加活动库存 均不影响库存
        </div>
    </div>
</template>

<script lang="ts" setup>
// 入库明细表格数据
const inboundTableData = [
    {
        type: '发布入库',
        source: '自有',
        recordMechanism: '发布成功',
        relatedOrderNo: '无',
        description: '自有商品发布成功时新增的库存',
    },
    {
        type: '编辑入库',
        source: '自有、采购',
        recordMechanism: '编辑提交成功',
        relatedOrderNo: '无',
        description: '将库存改大，而产生的入库',
    },
    {
        type: '采购入库',
        source: '采购',
        recordMechanism: '采购商品入库',
        relatedOrderNo: '采购订单号',
        description: '采购订单在入库时，对应的入库数',
    },
    {
        type: '盘盈入库',
        source: '自有、采购',
        recordMechanism: '库存盘点订单完成时',
        relatedOrderNo: '盘点订单号',
        description: '库存盘盈(增加库存)产生的入库',
    },
    {
        type: '退货入库',
        source: '自有、采购',
        recordMechanism: '退款成功时',
        relatedOrderNo: '退款订单号',
        description: '退货退款成功产生的退货入库',
    },
    {
        type: '订单取消',
        source: '自有、采购',
        recordMechanism: '取消订单或超时未支付订单关闭',
        relatedOrderNo: '用户订单号、采购订单号',
        description: '取消订单 或 订单超时关闭产生的入库',
    },
    {
        type: '调拨入库',
        source: '自有、采购',
        recordMechanism: '出入库完成时',
        relatedOrderNo: '出入库订单号',
        description: '从其它仓库调拨商品至本仓库产生的入库',
    },
    {
        type: '其它入库',
        source: '自有、采购',
        recordMechanism: '出入库完成时',
        relatedOrderNo: '出入库订单号',
        description: '其它场景产生的入库',
    },
]

// 出库明细表格数据
const outboundTableData = [
    {
        type: '销售出库',
        source: '自有、采购',
        recordMechanism: '订单提交成功',
        relatedOrderNo: '用户订单号',
        description: '提交订单成功产生的库存扣减',
    },
    {
        type: '编辑出库',
        source: '自有、采购',
        recordMechanism: '编辑提交成功',
        relatedOrderNo: '无',
        description: '将库存改小，而产生的出库',
    },
    {
        type: '盘亏出库',
        source: '自有、采购',
        recordMechanism: '库存盘点订单完成时',
        relatedOrderNo: '盘点订单号',
        description: '库存盘亏(减库存)产生的出库',
    },
    {
        type: '调拨出库',
        source: '自有、采购',
        recordMechanism: '出入库完成时',
        relatedOrderNo: '出入库订单号',
        description: '从本仓库调拨商品至其它仓库产生的出库',
    },
    {
        type: '其它出库',
        source: '自有、采购',
        recordMechanism: '出入库完成时',
        relatedOrderNo: '出入库订单号',
        description: '其它场景产生的出库',
    },
]
</script>

<style lang="scss">
.description {
    .el-table thead tr {
        .el-table__cell {
            background: #bebebe !important;
        }
    }
}
@include b(description) {
    overflow-y: scroll;
    height: 730px;
    @include e(content) {
        line-height: 22px;
        color: #333;
    }
    @include e(title) {
        padding: 15px 0;
        font-size: 16px;
        color: #2a2e3f;
        display: flex;
        align-items: center;
        @include e(title-icon) {
            width: 2px;
            height: 18px;
            background: #555cfd;
            margin-right: 10px;
            position: relative;
            top: 2px;
        }
    }
}
</style>
