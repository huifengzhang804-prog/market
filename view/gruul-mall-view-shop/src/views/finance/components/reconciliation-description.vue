<template>
    <div class="description">
        <div class="description__content">
            运费计算: 每次发货生成一笔运费记录，若快递配送拆单，将产生多条记录;<br />
            店铺对账是指本店铺与各交易对象之间，不含提现、第三方配送费用 的资金往来，在订单完成后生成对账数据。<br />
            实际交易金额 = (商品交易 + 会员折扣 + 会员包邮 + 平台优惠券 + 返利抵扣) - (店铺优惠券 + 满减优惠 + 运费 + 退款金额 + 分销佣金 + 代销货款)
        </div>
        <el-table :data="configData" border :header-cell-style="{ background: '#BEBEBE', color: '#000', height: '36px' }">
            <template #empty> <ElTableEmpty /> </template>
            <el-table-column prop="transactionType" label="交易类型" width="120" />
            <el-table-column prop="flowType" label="收入/支出" width="100" />
            <el-table-column prop="transactionObject" label="交易对象" width="100" />
            <el-table-column prop="fieldDescription" label="交易类型说明" />
        </el-table>
    </div>
</template>

<script lang="ts" setup>
const configData = [
    {
        transactionType: '商品交易',
        flowType: '收入',
        transactionObject: '用户',
        fieldDescription: '该笔订单用户实际支付的金额',
    },
    {
        transactionType: '平台优惠券',
        flowType: '收入',
        transactionObject: '平台',
        fieldDescription: '因平台发给用户优惠券而优惠的金额，平台需返还给店铺',
    },
    {
        transactionType: '会员包邮',
        flowType: '收入',
        transactionObject: '平台',
        fieldDescription: '因会员包邮权益而包邮，平台需返还运费给店铺',
    },
    {
        transactionType: '会员折扣',
        flowType: '收入',
        transactionObject: '平台',
        fieldDescription: '因会员折扣权益而打折的优惠金额，平台需返还给店铺',
    },
    {
        transactionType: '返利抵扣',
        flowType: '收入',
        transactionObject: '平台',
        fieldDescription: '用户使用返利余额购买商品，平台需返还店铺对应的返利支付金额',
    },
    {
        transactionType: '运费',
        flowType: '收入',
        transactionObject: '用户',
        fieldDescription: '用户购买商品时支付的运费',
    },
    {
        transactionType: '平台服务费',
        flowType: '支出',
        transactionObject: '平台',
        fieldDescription: '店铺收到的实际交易金额乘以，类目扣率或订单扣率',
    },
    {
        transactionType: '分销佣金',
        flowType: '支出',
        transactionObject: '分销商',
        fieldDescription: '分销(订单完成)成功，店铺支付给各层级分销商的佣金',
    },
    {
        transactionType: '采购运费',
        flowType: '支出',
        transactionObject: '供应商',
        fieldDescription: '店铺购买采购商品支付的运费，仅店铺余额支付会产生',
    },
    {
        transactionType: '采购交易',
        flowType: '支出',
        transactionObject: '供应商',
        fieldDescription: '店铺购买采购商品支付的货款，仅店铺余额支付会产生',
    },
    {
        transactionType: '代销支出',
        flowType: '支出',
        transactionObject: '供应商',
        fieldDescription: '店铺支付供应商的代销(代销商品)货款',
    },
]
</script>

<style lang="scss" scoped>
@include b(description) {
    @include e(content) {
        margin-bottom: 15px;
        line-height: 22px;
        color: #333;
    }
}
</style>
