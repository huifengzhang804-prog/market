<script setup lang="ts">
onMounted(() => {})

const tableData = [
    {
        date: '说的啥',
        name: '说的啥',
        address: '说的啥',
    },
]

const currentPage1 = ref(5)
const currentPage2 = ref(5)
const currentPage3 = ref(5)
const currentPage4 = ref(4)
const pageSize2 = ref(100)
const pageSize3 = ref(100)
const pageSize4 = ref(100)
const small = ref(false)
const background = ref(false)
const disabled = ref(false)

const handleSizeChange = (val: number) => {
    console.log(`${val} items per page`)
}
const handleCurrentChange = (val: number) => {
    console.log(`current page: ${val}`)
}
import type { TabsPaneContext } from 'element-plus'

const activeName = ref('first')

const handleClick = (tab: TabsPaneContext, event: Event) => {
    console.log(tab, event)
}
</script>

<template>
    <div class="box">
        <div class="topSearch">
            <el-row>
                <el-col :span="6"
                    ><div class="grid-content ep-bg-purple" />
                    <span style="margin: 0 13px">订单号</span> <input class="input" placeholder="单行输入" />
                </el-col>
                <el-col :span="6"
                    ><div class="grid-content ep-bg-purple-light" />
                    分账对象<input class="input" placeholder="单行输入"
                /></el-col>
                <el-col :span="6"
                    ><div class="grid-content ep-bg-purple" />
                    分账账号<input class="input" placeholder="单行输入"
                /></el-col>
                <el-col :span="6" style="line-height: 70px"
                    ><el-button class="but" style="background-color: #298ef6">搜索</el-button>
                    <el-button class="but" style="background-color: #ff9e00">重置</el-button></el-col
                >
            </el-row>
            <el-row>
                <el-col :span="6"
                    ><div class="grid-content ep-bg-purple" />
                    分账订单号<input class="input" placeholder="单行输入" />
                </el-col>
                <el-col :span="6"><div class="grid-content ep-bg-purple" /> </el-col>
                <el-col :span="6"><div class="grid-content ep-bg-purple" /> </el-col>
                <el-col :span="6"><div class="grid-content ep-bg-purple" /> </el-col>
            </el-row>
            <el-tabs v-model="activeName" class="demo-tabs" style="margin-left: 10px" @tab-click="handleClick">
                <el-tab-pane label="全部" name="first">
                    <div class="table">
                        <el-table :data="tableData" border style="width: 100%">
                            <template #empty> <ElTableEmpty /> </template>
                            <el-table-column prop="date" label="订单号" width="160" />
                            <el-table-column prop="name" label="订单金额" width="80" />
                            <el-table-column prop="date" label="分账服务方" width="90" />
                            <el-table-column prop="name" label="分账订单号" width="110" />
                            <el-table-column prop="date" label="分账对象" width="80" />
                            <el-table-column prop="name" label="分账账号" width="130" />
                            <el-table-column prop="date" label="分账金额" width="80" />
                            <el-table-column prop="name" label="分账状态" width="80" />
                            <el-table-column prop="name" label="交易类型" width="80" />
                            <el-table-column prop="date" label="分账摘要" width="80" />
                            <el-table-column prop="name" label="分账时间" width="130" />
                        </el-table>
                    </div>
                </el-tab-pane>
                <el-tab-pane label="待分布" name="second">
                    <el-table :data="tableData" border style="width: 100%">
                        <template #empty> <ElTableEmpty /> </template>
                        <el-table-column prop="date" label="订单号" width="160" />
                        <el-table-column prop="name" label="订单金额" width="80" />
                        <el-table-column prop="date" label="分账服务方" width="90" />
                        <el-table-column prop="name" label="分账订单号" width="110" />
                        <el-table-column prop="date" label="分账对象" width="80" />
                        <el-table-column prop="name" label="分账账号" width="130" />
                        <el-table-column prop="date" label="分账金额" width="80" />
                        <el-table-column prop="name" label="分账状态" width="80" />
                        <el-table-column prop="name" label="交易类型" width="80" />
                        <el-table-column prop="date" label="分账摘要" width="80" />
                        <el-table-column prop="name" label="分账时间" width="130" /> </el-table
                ></el-tab-pane>
                <el-tab-pane label="已分账" name="third">
                    <el-table :data="tableData" border style="width: 100%">
                        <template #empty> <ElTableEmpty /> </template>
                        <el-table-column prop="date" label="订单号" width="160" />
                        <el-table-column prop="name" label="订单金额" width="80" />
                        <el-table-column prop="date" label="分账服务方" width="90" />
                        <el-table-column prop="name" label="分账订单号" width="110" />
                        <el-table-column prop="date" label="分账对象" width="80" />
                        <el-table-column prop="name" label="分账账号" width="130" />
                        <el-table-column prop="date" label="分账金额" width="80" />
                        <el-table-column prop="name" label="分账状态" width="80" />
                        <el-table-column prop="name" label="交易类型" width="80" />
                        <el-table-column prop="date" label="分账摘要" width="80" />
                        <el-table-column prop="name" label="分账时间" width="130" /> </el-table
                ></el-tab-pane>
                <el-tab-pane label="分账失败" name="fourth">
                    <el-table :data="tableData" border style="width: 100%"
                        ><template #empty> <ElTableEmpty /> </template>
                        <el-table-column prop="date" label="订单号" width="160" />
                        <el-table-column prop="name" label="订单金额" width="80" />
                        <el-table-column prop="date" label="分账服务方" width="90" />
                        <el-table-column prop="name" label="分账订单号" width="110" />
                        <el-table-column prop="date" label="分账对象" width="80" />
                        <el-table-column prop="name" label="分账账号" width="130" />
                        <el-table-column prop="date" label="分账金额" width="80" />
                        <el-table-column prop="name" label="分账状态" width="80" />
                        <el-table-column prop="name" label="交易类型" width="80" />
                        <el-table-column prop="date" label="分账摘要" width="80" />
                        <el-table-column prop="name" label="分账时间" width="130" /> </el-table
                ></el-tab-pane>
            </el-tabs>
        </div>
        <div class="demo-pagination-block">
            <el-pagination
                v-model:current-page="currentPage2"
                v-model:page-size="pageSize2"
                :page-sizes="[100, 200, 300, 400]"
                :small="small"
                :disabled="disabled"
                :background="background"
                layout="total,prev, pager, next,sizes"
                :total="1000"
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
            />
        </div>
    </div>
</template>
<style lang="scss">
.el-message-box {
    height: 200px !important;
    p {
        line-height: 70px;
        text-align: center;
    }
}

.el-table tr {
    color: #000;
}
.el-table__header-wrapper tr th.el-table-fixed-column--right {
    text-align: center;
}
.el-button {
    margin-left: 20px;
}
.el-tabs__nav-wrap::after {
    background-color: transparent;
}
</style>
<style scoped lang="scss">
.box {
    .topSearch {
        height: 600px;
        background-color: rgba(255, 255, 255, 1);
        text-align: center;

        .el-row {
            height: 50px;
        }

        .el-col {
            border-radius: 4px;
        }

        .grid-content {
            margin-top: 20px;
        }
        .input {
            width: 140px;
            height: 30px;
            color: rgba(136, 136, 136, 1);
            font-size: 14px;
            text-align: left;
            font-family: Microsoft Yahei;
            border: 1px solid rgba(187, 187, 187, 1);
            border-radius: 0;
            padding: 0 10px;
            margin: 0 10px;
        }
        .but {
            width: 80px;
            height: 30px;
            line-height: 20px;
            border-radius: 8px;
            color: rgba(255, 255, 255, 1);
            font-size: 14px;
            text-align: center;
            font-family: Roboto;
        }
        .demo-tabs > .el-tabs__content {
            padding: 32px;
            color: #6b778c;
            font-size: 32px;
            font-weight: 600;
        }
    }
    .table {
        margin-top: 10px;
        margin-bottom: 40px;
        height: 440px;
        .el-table--border {
            font-size: 12px !important;
        }
    }
    .demo-pagination-block {
        float: right;
    }
}
</style>
