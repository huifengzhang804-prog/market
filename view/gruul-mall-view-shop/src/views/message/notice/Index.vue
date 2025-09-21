<script lang="ts" setup>
import { messagePage } from '@/apis/message'
import { useRouter } from 'vue-router'
import PageManage from '@/components/PageManage.vue'

interface MsgPageRecords {
    channel: string
    content: string
    createBy: string
    createTime: string
    id: string
    msgType: string
    read: boolean
    sendType: string
    title: string
    type: string
    updateBy: string
    updateTime: string
    url: string
}
const msgPage = reactive({
    records: [] as MsgPageRecords[],
    total: 0,
    page: { current: 1, size: 10 },
})
const $router = useRouter()

const getMessagePage = () => {
    messagePage({ ...msgPage.page }).then((response) => {
        const page = response.data as any
        msgPage.total = page.total
        msgPage.records = page.records
    })
}
getMessagePage()

const handleClick = (row: MsgPageRecords) => {
    $router.push({
        name: 'messageNoticeDetail',
        query: {
            id: row.id,
        },
    })
}
const handleSizeChange = (value: number) => {
    msgPage.page.size = value
    getMessagePage()
}
const handleCurrentChange = (value: number) => {
    msgPage.page.current = value
    getMessagePage()
}
</script>

<template>
    <div class="table_container" style="padding-top: 16px">
        <el-table
            :data="msgPage.records"
            style="width: 100%"
            :header-row-style="{ color: '#333' }"
            :header-cell-style="{ background: '#f6f8fa', height: '48px' }"
            :row-style="{ height: '60px' }"
            class="f1"
        >
            <template #empty> <ElTableEmpty /> </template>
            <el-table-column type="index" label="序号" :index="1" align="center" width="70" />
            <el-table-column prop="title" label="公告标题">
                <template #default="{ row }">
                    <el-link style="height: 32px" :underline="false" class="notice-table-row" @click="handleClick(row)">
                        <el-badge is-dot :offset="[-10, 7]" :hidden="row.read">
                            {{ row.title }}
                        </el-badge>
                    </el-link>
                </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" />
        </el-table>
    </div>
    <PageManage
        :page-size="msgPage.page.size"
        :total="msgPage.total"
        :page-num="msgPage.page.current"
        @handle-size-change="handleSizeChange"
        @handle-current-change="handleCurrentChange"
    />
</template>

<style lang="scss" scoped>
.notice-table-row {
    padding: 20px 20px;
}
.column-margin {
    margin-left: 20px;
    margin-right: 20px;
}
:deep(.el-badge__content, .is-fixed) {
    transform: translate(40px, -9px);
    border: transparent;
}
</style>
<style>
.notice-table-row .el-badge__content,
.is-fixed {
    top: 6px;
}
</style>
