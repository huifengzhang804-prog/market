<script setup lang="ts">
import { useRouter } from 'vue-router'
import Search, { SearchType } from './Search.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import PageManage from '@/components/PageManage.vue'
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
    storageManagementOrderType: 'INVENTORY',
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
    })
    tableList.total = +data.total
    tableList.list = data.records
}
initList()
/**
 * 表格排序
 */
let sortEnum = ref('')
const sortTableList = () => {
    // sortEnum.value = sortEnum.value === 'AMOUNT_ASC' ? 'AMOUNT_DESC' : 'AMOUNT_ASC'
    initList()
}
//tab点击事件
const handleTabClick = (status: '' | 'WAIT_COMPLETION' | 'COMPLETION' | 'CANCEL') => {
    searchParams.value.status = status
    initList()
}
const getSearch = (e: SearchType) => {
    tableList.page.current = 1
    if (e.date.length > 0) {
        searchParams.value.startTime = e.date[0]
        searchParams.value.endTime = e.date[1]
    }
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
// 分页方法
const handleSizeChange = (val: number) => {
    tableList.page.current = 1
    tableList.page.size = val
    initList()
}
const handleCurrentChange = (val: number) => {
    tableList.page.current = val
    initList()
}
</script>

<template>
    <Search @on-search-params="getSearch" />
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
            :header-cell-style="{
                'background-color': '#F7F8FA',
                color: '#333',
                height: '48px',
            }"
            :cell-style="{
                height: '60px',
                color: '#333',
            }"
        >
            <template #empty> <ElTableEmpty /> </template>
            <el-table-column label="订单号" width="280">
                <template #default="{ row }">
                    <div>{{ row.no }}</div>
                </template>
            </el-table-column>
            <el-table-column label="盘点区域">
                <template #default="{ row }">
                    <div>{{ row.inventoryArea }}</div>
                </template>
            </el-table-column>
            <el-table-column label="盘点员">
                <template #default="{ row }">
                    <div>{{ row.operationName }}</div>
                </template>
            </el-table-column>
            <el-table-column label="手机号">
                <template #default="{ row }">
                    <div>{{ row.operationPhone }}</div>
                </template>
            </el-table-column>
            <el-table-column label="盘点时间" width="200px">
                <template #header>
                    <span style="margin-right: 5px">盘点时间</span>
                    <!-- <i style="font-size: 12px" class="iconfont icon-path" @click="sortTableList"></i> -->
                </template>
                <template #default="{ row }">
                    <div>{{ row.operationAccomplishTime }}</div>
                </template>
            </el-table-column>
            <el-table-column label="盘点状态" width="120px">
                <template #default="{ row }">
                    <div :style="{ color: row.status === 'COMPLETION' ? '#00BB2C' : row.status === 'WAIT_COMPLETION' ? '#FD9224' : '#666' }">
                        {{ statuslist[row.status as keyof typeof statuslist] }}
                    </div>
                </template>
            </el-table-column>
            <el-table-column label="操作" fixed="right" align="right" width="160">
                <template #default="{ row }">
                    <div v-if="row.status === 'WAIT_COMPLETION'">
                        <el-link type="primary" @click="handleContinue(row.id)">继续</el-link>
                        <el-link type="primary" style="margin-left: 10px" @click="handleConfirm(row.id)">完成</el-link>
                        <el-link type="danger" style="margin-left: 10px" @click="handleClose(row.id)">取消</el-link>
                    </div>
                    <el-link v-else type="primary" @click="handledetail(row.id)">详情</el-link>
                </template>
            </el-table-column>
        </el-table>
    </div>
    <PageManage
        v-if="tableList.total"
        :page-size="tableList.page.size"
        :page-num="tableList.page.current"
        :total="tableList.total"
        @handle-size-change="handleSizeChange"
        @handle-current-change="handleCurrentChange"
    />
</template>

<style lang="scss" scoped>
.last {
    position: absolute;
    right: 10px;
    top: 4px;
    z-index: 6;
}
</style>
