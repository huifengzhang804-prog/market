<template>
    <div>
        <el-table
            :data="tableData1"
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
            <el-table-column prop="withdrawType" label="提现类型" width="140" />
            <el-table-column prop="withdrawLevel" label="提现对象" width="120" />
            <el-table-column prop="withdrawMethod" label="提现方式" width="160" />
            <el-table-column prop="description" label="说明" />
        </el-table>
        <el-table
            :data="tableData2"
            border
            :header-cell-style="{
                'background-color': '#BEBEBE',
                'font-weight': 'normal',
                color: '#000',
                fontWeight: 'bold',
            }"
            :cell-style="{ color: '#666' }"
            style="margin-top: 10px"
        >
            <template #empty> <ElTableEmpty /> </template>
            <el-table-column prop="withdrawMethod" label="提现方式" width="120" />
            <el-table-column prop="paymentMethod" label="支付方式" width="120" />
            <el-table-column prop="description" label="说明" />
        </el-table>
        <el-table
            :data="tableData3"
            border
            :header-cell-style="{
                'background-color': '#BEBEBE',
                'font-weight': 'normal',
                color: '#000',
                fontWeight: 'bold',
            }"
            :cell-style="{ color: '#666' }"
            style="margin-top: 10px"
        >
            <template #empty> <ElTableEmpty /> </template>
            <el-table-column prop="withdrawStatus" label="提现状态" width="120" />
            <el-table-column prop="description" label="说明" />
        </el-table>
    </div>
</template>

<script lang="ts" setup>
const tableData1 = [
    {
        withdrawType: '交易提现(店铺)',
        withdrawLevel: '店铺',
        withdrawMethod: '银行卡',
        description: '店铺向平台发起【店铺余额】提现',
    },
    {
        withdrawType: '交易提现(供应商)',
        withdrawLevel: '供应商',
        withdrawMethod: '银行卡',
        description: '供应商向平台发起【供应商余额】提现',
    },
    {
        withdrawType: '佣金提现(分销商)',
        withdrawLevel: '分销商',
        withdrawMethod: '支付宝/微信/银行卡',
        description: '分销商向平台发起【分销佣金】提现',
    },
    {
        withdrawType: '返利提现(用户)',
        withdrawLevel: '返利用户',
        withdrawMethod: '支付宝/微信/银行卡',
        description: '返利用户向平台发起【返利余额】提现',
    },
]

// 第二个表格数据
const tableData2 = [
    {
        withdrawMethod: '银行卡',
        paymentMethod: '线下打款',
        description: '线下打款：财务人员直接打款给用户并上传打款凭证',
    },
    {
        withdrawMethod: '支付宝/微信',
        paymentMethod: '线上/线下',
        description:
            '线上打款：\n1. 提现中、提现失败属于线上打款的特有提现状态\n2. 财务人员需在支付宝/微信平台审核，审核通过后资金自动打到客户支付宝/微信账上',
    },
]

// 第三个表格数据
const tableData3 = [
    {
        withdrawStatus: '待审核',
        description: '提现对象已将提现申请提交成功，待平台管理人员审核',
    },
    {
        withdrawStatus: '提现中',
        description: '提现申请已通过，待财务在【支付宝/微信】平台上审核通过后打款',
    },
    {
        withdrawStatus: '已打款',
        description: '平台财务已将提现金额打到客户账上',
    },
    {
        withdrawStatus: '提现失败',
        description: '提现申请已通过，财务在【支付宝/微信】平台上审核拒绝或账上余额不足等原因都将导致提现失败',
    },
    {
        withdrawStatus: '已拒绝',
        description: '提现申请被平台管理人员拒绝',
    },
]
</script>
