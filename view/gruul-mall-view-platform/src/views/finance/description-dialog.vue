<template>
    <div class="description">
        <div class="description__content">
            运费计算:每次发货生成一笔运费记录，若快递配送拆单，将产生多条记录;<br />
            平台对账是指平台端与各交易对象之间的资金(不含提现业务)往来，订单完成后生成对应的对账数据;<br />
            实际交易金额=(商品交易 + 会员折扣+会员包邮 + 平台优惠券+返利抵扣)-(店铺优惠券+满减优惠+运费+退款金额+分销佣金+代销货款)
        </div>
        <el-table
            :data="configData"
            border
            :header-cell-style="{
                'background-color': '#BEBEBE',
                'font-weight': 'normal',
                color: '#000',
                fontWeight: 'bold',
            }"
            :cell-style="{ color: '#666' }"
        >
            <template #empty> <ElTableEmpty /> </template>
            <el-table-column prop="goodsType" label="商品类型" width="180" />
            <el-table-column prop="description" label="平台服务费说明" />
        </el-table>
        <el-table
            :data="tableData"
            border
            :header-cell-style="{
                'background-color': '#BEBEBE',
                'font-weight': 'normal',
                color: '#000',
                fontWeight: 'bold',
            }"
            :cell-style="{ color: '#666' }"
            :style="{ marginTop: '15px' }"
        >
            <template #empty> <ElTableEmpty /> </template>
            <el-table-column prop="transType" label="交易类型" width="180" />
            <el-table-column prop="inOut" label="收入/支出" width="120" />
            <el-table-column prop="target" label="交易对象" width="120" />
            <el-table-column prop="description" label="交易类型说明" />
        </el-table>
    </div>
</template>

<script lang="ts" setup>
const configData = [
    {
        goodsType: '自有商品',
        description: '店铺向消费者销售自有商品时收取平台服务费',
    },
    {
        goodsType: '代销商品',
        description: '店铺向消费者销售代销商品时收取平台服务费，同时，店铺向供应商购买代销商品时也需支付平台服务费',
    },
    {
        goodsType: '采购商品',
        description: '店铺向供应商采购商品（店铺余额支付）时需支付平台服务费，同时，店铺向消费者销售商品时也需支付平台服务费',
    },
]
const tableData = [
    {
        transType: '平台服务费',
        inOut: '收入',
        target: '店铺/供应商',
        description: '商家实际交易金额乘以，类目扣率或订单扣率',
    },
    {
        transType: '平台服务费(采购)',
        inOut: '收入',
        target: '供应商',
        description: '店铺余额支付的采购订单，根据平台类目和采购订单金额进行提佣',
    },
    {
        transType: '用户充值',
        inOut: '收入',
        target: '用户',
        description: '用户自己储值充值而产生的平台收入，不含系统充值',
    },
    {
        transType: '购买付费会员',
        inOut: '收入',
        target: '用户',
        description: '用户购买付费会员产生的平台收入',
    },
    {
        transType: '续费付费会员',
        inOut: '收入',
        target: '用户',
        description: '用户续费(继续购买)付费会员产生的平台收入',
    },
    {
        transType: '积分交易',
        inOut: '收入',
        target: '用户',
        description: '用户购买需要现金的积分商品而产生的(含运费)收入',
    },
    {
        transType: '会员折扣',
        inOut: '支出',
        target: '店铺',
        description: '因会员享有该权益导致的费用支出，该笔支出将作为店铺的收入',
    },
    {
        transType: '会员包邮',
        inOut: '支出',
        target: '店铺/供应商',
        description: '因会员享有该权益导致的费用支出，该笔支出将作为店铺/供应商的收入',
    },
    {
        transType: '平台优惠券',
        inOut: '支出',
        target: '店铺',
        description: '平台给用户发放优惠券而产生的支出，该笔支出将作为店铺的收入',
    },
    {
        transType: '返利抵扣',
        inOut: '支出',
        target: '店铺',
        description: '用户使用消费返利的余额购买商品，该笔支出将作为店铺的收入',
    },
    {
        transType: '充值赠送',
        inOut: '支出',
        target: '用户',
        description: '用户充值时因满足储值规则赠送的金额',
    },
]
</script>

<style lang="scss" scoped>
@include b(description) {
    @include e(content) {
        margin-bottom: 15px;
        line-height: 22px;
        color: #000;
    }
}
</style>
