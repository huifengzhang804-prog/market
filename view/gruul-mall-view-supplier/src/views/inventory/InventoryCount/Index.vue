<script setup lang="ts">
import { reactive, Ref, ref } from 'vue'
import { useRouter } from 'vue-router'
import Search, { SearchType } from './Search.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import PageManage from '@/components/PageManage/index.vue'
import { doGetStorageList, doPutStorageOrdercancel, doPutStorageOrdercomplete } from '@/apis/inventory'

const $router = useRouter()
const tableList = reactive({
    page: { size: 10, current: 1 },
    list: [],
    total: 0,
})
const currentTab = ref<'' | 'WAIT_COMPLETION' | 'COMPLETION' | 'CANCEL'>('')
const searchParams = ref({
    no: '',
    operation: '',
    date: '',
    startTime: '',
    endTime: '',
    status: '',
})
const statuslist = {
    WAIT_COMPLETION: '盘点中',
    COMPLETION: '已完成',
    CANCEL: '已取消',
}

const initList = async () => {
    const { data } = await doGetStorageList({
        ...searchParams.value,
        ...tableList.page,
        storageManagementOrderType: 'INVENTORY',
    })
    tableList.total = +data.total
    tableList.list = data.records
}
initList()

//tab点击事件
const handleTabClick = (status: '' | 'WAIT_COMPLETION' | 'COMPLETION' | 'CANCEL' | any) => {
    searchParams.value.status = status
    initList()
}
const getSearch = (e: SearchType) => {
    tableList.page.current = 1
    searchParams.value.startTime = ''
    searchParams.value.endTime = ''
    if (e?.date?.length > 0) {
        searchParams.value.startTime = e.date[0]
        searchParams.value.endTime = e.date[1]
    }
    e.date = ''
    searchParams.value = { ...searchParams.value, ...e }
    initList()
}
const handleClose = async (id: string) => {
    ElMessageBox.confirm('确认后，本次盘点不变更任何库存数据？？', '提示', {
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
    ElMessageBox.confirm('确认后，将按盘点盈亏情况进行库存调整？？', '提示', {
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
        path: '/inventory/count/add',
        query: {
            id,
        },
    })
}
const handledetail = (id: string) => {
    $router.push({
        path: '/inventory/count/detail',
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
        <Search @on-search-params="getSearch" />
    </div>
    <div class="tab_container">
        <el-tabs v-model="currentTab" @tab-change="handleTabClick">
            <el-tab-pane label="全部" name=""></el-tab-pane>
            <el-tab-pane label="盘点中" name="WAIT_COMPLETION"></el-tab-pane>
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
            :cell-style="{ color: '#333', height: '60px' }"
        >
            <el-table-column label="订单号" width="230" fixed="left">
                <template #default="{ row }">
                    <div>{{ row.no }}</div>
                </template>
            </el-table-column>
            <el-table-column label="盘点区域" width="200px">
                <template #default="{ row }">
                    <div>{{ row.inventoryArea }}<span v-if="!row.inventoryArea">-</span>{{ row.inventoryAreaName }}</div>
                </template>
            </el-table-column>
            <el-table-column label="盘点员" width="120px">
                <template #default="{ row }">
                    <div>{{ row.operationName }}</div>
                </template>
            </el-table-column>
            <el-table-column label="手机号" width="180px">
                <template #default="{ row }">
                    <div>{{ row.operationPhone }}</div>
                </template>
            </el-table-column>
            <el-table-column label="盘点时间" width="200px">
                <template #default="{ row }">
                    <div>{{ row.operationAccomplishTime }}</div>
                </template>
            </el-table-column>
            <el-table-column label="状态" width="140px">
                <template #default="{ row }">
                    <div :style="{ color: row.status === 'WAIT_COMPLETION' ? '#FD9224' : row.status === 'COMPLETION' ? '#00BB2C' : '#999999' }">
                        {{ statuslist[row.status as keyof typeof statuslist] }}
                    </div>
                </template>
            </el-table-column>
            <el-table-column label="操作" fixed="right" align="right" width="130">
                <template #default="{ row }">
                    <div v-if="row.status === 'WAIT_COMPLETION'">
                        <el-link type="primary" @click="handleContinue(row.id)">继续</el-link>
                        <el-link type="primary" style="margin-left: 10px" @click="handleConfirm(row.id)">完成</el-link>
                        <el-link type="danger" style="margin-left: 10px" @click="handleClose(row.id)">取消</el-link>
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
