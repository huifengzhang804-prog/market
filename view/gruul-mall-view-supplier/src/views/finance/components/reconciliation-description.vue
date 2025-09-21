<template>
    <div class="description">
        <div class="description__content">
            商品实际交易金额 = (代销/采购货款 + 会员包邮 ) - ( 运费+退款金额) ；<br />
            运费计算：每次发货生成一笔运费记录；若快递配送拆单，将产生多条记录；<br />
            供应商对账是指本供应商 与 各交易对象之间的资金(不含提现业务)往来，订单【已完成】后生成对应的对账数据；
        </div>
        <el-table :data="configData" border :header-cell-style="{ background: '#BEBEBE', color: '#000', height: '36px' }">
            <el-table-column prop="transactionType" label="交易类型" width="140" />
            <el-table-column prop="flowType" label="收入/支出" width="90" />
            <el-table-column prop="transactionObject" label="交易对象" width="90" />
            <el-table-column prop="fieldDescription" label="字段说明" />
        </el-table>
    </div>
</template>

<script lang="ts" setup>
const configData = [
    {
        transactionType: '代销交易',
        flowType: '收入',
        transactionObject: '店铺',
        fieldDescription: '店铺支付供应商的代销货款需剔除退款成功的金额；若属性设置不合理，该数值可能为0',
    },
    {
        transactionType: '代销运费',
        flowType: '收入',
        transactionObject: '用户',
        fieldDescription: '客户购买代销商品支付的运费',
    },
    {
        transactionType: '采购交易',
        flowType: '收入',
        transactionObject: '店铺',
        fieldDescription: '店铺购买采购商品支付的货款，仅店铺余额支付会产生',
    },
    {
        transactionType: '采购运费',
        flowType: '收入',
        transactionObject: '店铺',
        fieldDescription: '店铺购买采购商品支付的运费，仅店铺余额支付会产生',
    },
    {
        transactionType: '会员包邮',
        flowType: '收入',
        transactionObject: '平台',
        fieldDescription: '因会员包邮权益而包邮，平台需返还运费给供应商',
    },
    {
        transactionType: '平台服务费(代销)',
        flowType: '支出',
        transactionObject: '平台',
        fieldDescription: '因商家为客户购买代销商品产生的代销交易收入，该笔收入需按类目扣率或订单金额收取服务费。',
    },
    {
        transactionType: '平台服务费(采购)',
        flowType: '支出',
        transactionObject: '平台',
        fieldDescription: '仅针对店铺余额支付的采购订单，平台将根据类目扣率或订单金额收取服务费。',
    },
]
</script>

<style lang="scss" scoped>
@include b(description) {
    @include e(content) {
        color: #000;
        margin-bottom: 15px;
    }
}
</style>
