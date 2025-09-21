<template>
    <div class="description">
        <el-table
            :data="configData"
            border
            :header-cell-style="{
                'background-color': '#BEBEBE',
                'font-weight': 'normal',
                color: '#515151',
            }"
        >
            <template #empty> <ElTableEmpty /> </template>
            <el-table-column prop="operateType" label="操作类型" width="140" />
            <el-table-column prop="description" label="字段说明" />
        </el-table>
    </div>
</template>

<script lang="ts" setup>
const configData = [
    {
        operateType: '系统充值',
        description: '平台端系统用户对会员进行储值充值',
    },
    {
        operateType: '系统扣除',
        description: '平台端系统用户对会员进行储值扣除',
    },
    {
        operateType: '用户充值',
        description: '用户自己储值充值',
    },
    {
        operateType: '充值赠送',
        description: '用户充值时因满足储值规则赠送的金额',
    },
    {
        operateType: '购物消费',
        description: '用户使用储值购买商品',
    },
    {
        operateType: '退款成功',
        description: '储值购买的商品退款成功',
    },
    {
        operateType: '购买会员',
        description: '使用储值购买付费会员',
    },
    {
        operateType: '续费会员',
        description: '使用储值续费付费会员',
    },
    // {
    //     operateType: '升级会员',
    //     description: '使用储值升级付费会员',
    // },
]
</script>
