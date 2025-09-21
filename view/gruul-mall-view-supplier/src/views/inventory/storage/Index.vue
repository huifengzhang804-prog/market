<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import Search from './Search.vue'
import { ElMessage, ElMessageBox, TabPaneName } from 'element-plus'
import PageManage from '@/components/PageManage/index.vue'
import { doGetStorageList, doPutStorageOrdercancel, doPutStorageOrdercomplete } from '@/apis/inventory'
import { SearchEmitType } from './types'

const $router = useRouter()
const tableList = reactive({
    page: { size: 10, current: 1 },
    list: [],
    total: 0,
})
const currentTab = ref<'' | 'WAIT_COMPLETION' | 'COMPLETION' | 'CANCEL'>('')
const searchParams = ref<any>({
    no: '',
    stockChangeType: '',
    operation: '',
    startTime: '',
    endTime: '',
    status: '' as TabPaneName,
    storageManagementOrderType: 'IN_OUT',
})
const statuslist = {
    WAIT_COMPLETION: '出入库中',
    COMPLETION: '已完成',
    CANCEL: '已取消',
}
const stockChangeTypelist = {
    ALLOCATION_INBOUND: '调拨入库',
    OTHER_INBOUND: '其它入库',
    ALLOCATION_OUTBOUND: '调拨出库',
    OTHER_OUTBOUND: '其它出库',
}

const initList = async () => {
    const { data } = await doGetStorageList({
        ...searchParams.value,
        ...tableList.page,
    })
    tableList.total = +data.total
    tableList.list = data.records
}
initList()

//tab点击事件
const handleTabClick = (status: TabPaneName) => {
    searchParams.value.status = status
    initList()
}
const getSearch = (e: SearchEmitType) => {
    searchParams.value.startTime = ''
    searchParams.value.endTime = ''
    tableList.page.current = 1
    searchParams.value = { ...searchParams.value, ...e }
    initList()
}
const handleClose = async (id: string) => {
    ElMessageBox.confirm('确定后，库存不做任何调整 ？？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
    }).then(async () => {
        const { code, data, msg } = await doPutStorageOrdercancel(id)
        if (code !== 200) {
            ElMessage.error(msg ? msg : '取消失败')
            return
        }
        ElMessage.success('已取消')
        initList()
    })
}
const handleConfirm = (id: string) => {
    ElMessageBox.confirm('确认后，将按出入库数变更库存 ？？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
    }).then(async () => {
        const { code, data, msg } = await doPutStorageOrdercomplete(id)
        if (code !== 200) {
            ElMessage.error(msg ? msg : '完成失败')
            return
        }
        ElMessage.success('已完成')
        initList()
    })
}
const handleContinue = (id: string) => {
    $router.push({
        path: '/inventory/storage/add',
        query: {
            id,
        },
    })
}
const handledetail = (id: string) => {
    $router.push({
        path: '/inventory/storage/detail',
        query: {
            id,
        },
    })
}

// 分页器
const handleSizeChange = (value: number) => {
    tableList.page.size = value
    initList()
}
const handleCurrentChange = (value: number) => {
    tableList.page.current = value
    initList()
}
</script>

<template>
    <div class="search_container">
        <Search @onSearchParams="getSearch" />
    </div>
    <div class="tab_container">
        <el-tabs v-model="currentTab" @tab-change="handleTabClick">
            <el-tab-pane label="全部" name=""></el-tab-pane>
            <el-tab-pane label="出入库中" name="WAIT_COMPLETION"></el-tab-pane>
            <el-tab-pane label="已完成" name="COMPLETION"></el-tab-pane>
            <el-tab-pane label="已取消" name="CANCEL"></el-tab-pane>
        </el-tabs>
    </div>
    <div class="table_container">
        <el-table
            empty-text="暂无数据~"
            :data="tableList.list"
            style="width: 100%"
            :header-cell-style="{ background: '#f6f8fa', height: '48px' }"
            :header-row-style="{ fontSize: '14', color: '#333' }"
            :cell-style="{ fontSize: '14', color: '#333333', height: '60px' }"
        >
            <template #empty>
                <ElTableEmpty />
            </template>
            <el-table-column label="订单号" width="210" fixed="left">
                <template #default="{ row }">
                    <div>{{ row.no }}</div>
                </template>
            </el-table-column>
            <el-table-column label="总出入库数" width="160px">
                <template #default="{ row }">
                    <div>{{ row.changeStockTotal }}</div>
                </template>
            </el-table-column>
            <el-table-column label="类型" width="120px">
                <template #default="{ row }">
                    <div>{{ stockChangeTypelist[row.stockChangeType as keyof typeof stockChangeTypelist] }}</div>
                </template>
            </el-table-column>
            <el-table-column label="出入库员" width="120px">
                <template #default="{ row }">
                    <div>{{ row.operationName }}</div>
                </template>
            </el-table-column>
            <el-table-column label="手机号" width="160px">
                <template #default="{ row }">
                    <div>{{ row.operationPhone }}</div>
                </template>
            </el-table-column>
            <el-table-column label="出入库时间" width="200px">
                <template #default="{ row }">
                    <div>{{ row.operationAccomplishTime }} <span v-if="!row.operationAccomplishTime">-</span></div>
                </template>
            </el-table-column>
            <el-table-column label="状态" width="140px">
                <template #default="{ row }">
                    <div :style="{ color: row.status === 'WAIT_COMPLETION' ? '#FD9224' : row.status === 'COMPLETION' ? '#00BB2C' : '#999999' }">
                        {{ statuslist[row.status as keyof typeof statuslist] }}
                    </div>
                </template>
            </el-table-column>
            <el-table-column label="操作" width="200px" fixed="right" align="right">
                <template #default="{ row }">
                    <div v-if="row.status === 'WAIT_COMPLETION'">
                        <el-link type="primary" @click="handleContinue(row.id)">继续</el-link>
                        <el-link type="primary" style="margin-left: 12px" @click="handleConfirm(row.id)">完成</el-link>
                        <el-link type="danger" style="margin-left: 12px" @click="handleClose(row.id)">取消</el-link>
                    </div>
                    <el-link v-else type="primary" @click="handledetail(row.id)">查看详情</el-link>
                </template>
            </el-table-column>
        </el-table>
    </div>

    <PageManage
        :page-size="tableList.page.size"
        :page-num="tableList.page.current"
        :total="tableList.total"
        @handle-size-change="handleSizeChange"
        @handle-current-change="handleCurrentChange"
    />
</template>

<style lang="scss" scoped></style>
