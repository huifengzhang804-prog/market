<script setup lang="ts">
const { divTenThousand } = useConvert()
const props = defineProps({
    invoiceDetail: {
        type: Object,
        default: () => ({}),
    },
})
const invoiceHeaderType = {
    PERSONAL: '个人',
    ENTERPRISE: '企业',
}
const invoiceStatus = {
    REQUEST_IN_PROCESS: '开票中',
    SUCCESSFULLY_INVOICED: '开票成功',
    FAILED_INVOICE_REQUEST: '开票失败',
    CLIENT_CANCEL_REQUEST: '用户撤销',
}

/**
 * @description: 下载附件
 * @returns
 */
const download = () => {
    props.invoiceDetail.attachments.forEach((item) => {
        window.open(item)
    })
}
</script>

<template>
    <el-row>
        <el-col :span="8">开票状态：{{ invoiceStatus[props.invoiceDetail.invoiceStatus] }}</el-col>
        <el-col v-if="props.invoiceDetail.invoiceStatus === 'SUCCESSFULLY_INVOICED'" :span="8"
            >发票：<span style="cursor: pointer; color: #0892ee" @click="download">下载</span>
        </el-col>
        <el-col v-if="props.invoiceDetail.invoiceStatus === 'FAILED_INVOICE_REQUEST'" :span="8"
            >拒绝原因：{{ props.invoiceDetail.denialReason }}
        </el-col>
    </el-row>
    <el-row>
        <el-col :span="8">申请单号：{{ props.invoiceDetail.id }}</el-col>
        <el-col :span="8">申请时间：{{ props.invoiceDetail.createTime }}</el-col>
        <el-col :span="8">更新时间：{{ props.invoiceDetail.updateTime }} </el-col>
    </el-row>
    <el-row>
        <el-col :span="8">发票类型：{{ props.invoiceDetail.invoiceType === 'VAT_GENERAL' ? '增值税电子普通发票' : '增值税电子专用发票' }}</el-col>
        <el-col :span="8"
            >开票金额：<span style="color: #fd0505">￥ {{ divTenThousand(props.invoiceDetail.invoiceAmount) }}</span>
        </el-col>
        <el-col :span="8">订单号：{{ props.invoiceDetail.orderNo }}</el-col>
    </el-row>
    <el-row>
        <el-col :span="8">抬头类型：{{ invoiceHeaderType[props.invoiceDetail.invoiceHeaderType] }}</el-col>
        <el-col :span="8">发票抬头：{{ props.invoiceDetail.header }}</el-col>
        <el-col :span="8">税号：{{ props.invoiceDetail.taxIdentNo }}</el-col>
    </el-row>
    <el-row>
        <el-col :span="8">开户行：{{ props.invoiceDetail.openingBank }} </el-col>
        <el-col :span="8">银行账号：{{ props.invoiceDetail.bankAccountNo }} </el-col>
        <el-col :span="8">企业电话：{{ props.invoiceDetail.enterprisePhone }}</el-col>
    </el-row>
    <el-row>
        <el-col :span="8">邮箱地址：{{ props.invoiceDetail.email }} </el-col>
        <el-col :span="16">企业地址：{{ props.invoiceDetail.enterpriseAddress }} </el-col>
    </el-row>
    <div style="width: 100%; word-wrap: break-word">开票备注： {{ props.invoiceDetail.billingRemarks }}</div>
</template>

<style lang="scss" scoped>
.el-row {
    margin-bottom: 20px;
}
</style>
