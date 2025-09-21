<template>
    <div class="service">
        <div class="handle_container">
            <el-button type="primary" round @click="dialogVisible = true">新增服务</el-button>
        </div>
        <!-- 表格部分 -->
        <div class="table_container">
            <el-table
                stripe
                :data="serveList"
                empty-text="暂无服务"
                style="width: 100%; max-height: calc(100vh - 273px)"
                :header-cell-style="{ fontSize: '14px', fontWeight: 'bold', color: '#333', background: '#f6f8fa', height: '48px' }"
            >
                <template #empty> <ElTableEmpty /> </template>
                <el-table-column label="快递公司" width="150">
                    <template #default="{ row }">
                        <span>{{ row.logisticsCompanyName }}</span>
                    </template>
                </el-table-column>
                <el-table-column label="客户号">
                    <template #default="{ row }">
                        <span>{{ row.customerCode }}</span>
                    </template>
                </el-table-column>
                <el-table-column label="网点名称" width="150">
                    <template #default="{ row }">
                        <span>{{ row.networkName }}</span>
                    </template>
                </el-table-column>
                <el-table-column label="网点编码">
                    <template #default="{ row }">
                        <span>{{ row.networkCode }}</span>
                    </template>
                </el-table-column>
                <el-table-column label="打印机">
                    <template #default="{ row }">
                        <span>{{ row.printName }}</span>
                    </template>
                </el-table-column>
                <el-table-column label="应用商家">
                    <template #default="{ row }">
                        <span v-if="row.defSelfShop === 'YES'" type="primary" size="small">自营商家</span>&nbsp;
                        <span v-if="row.defSelfSupplier === 'YES'" style="margin: 0" type="primary" size="small">自营供应商 </span>
                    </template>
                </el-table-column>
                <el-table-column fixed="right" label="操作" align="right" width="120">
                    <template #default="{ row }">
                        <div class="right_btn">
                            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
                            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
                        </div>
                    </template>
                </el-table-column>
            </el-table>
        </div>
        <page-manage
            :page-size="pageConfig.pageSize"
            :page-num="pageConfig.pageNum"
            :total="pageConfig.total"
            @handle-size-change="handleSizeChange"
            @handle-current-change="handleCurrentChange"
        />
    </div>
    <el-dialog
        v-model="dialogVisible"
        width="50%"
        :title="currentRow?.id ? '编辑物流服务' : '新增物流服务'"
        destroy-on-close
        @close="currentRow = {}"
    >
        <Service ref="serviceRef" :service="currentRow" />
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button type="primary" @click="handleFormSubmit">提交</el-button>
            </span>
        </template>
    </el-dialog>
</template>

<script lang="ts" setup>
import { ElMessage, ElMessageBox } from 'element-plus'
import Service from './service.vue'
import { doDelLogisticsServe, doGetLogisticServeList, doSaveLogisticsServe, doUpdateLogisticServe } from '@/apis/freight'

const dialogVisible = ref(false)
const serveList = ref<any[]>([])
const pageConfig = reactive({
    pageSize: 20,
    pageNum: 1,
    total: 0,
})

const serviceRef = ref<InstanceType<typeof Service> | null>(null)
const currentRow = ref<any>({})
const initServeList = async () => {
    const { code, data, success } = await doGetLogisticServeList(pageConfig.pageNum, pageConfig.pageSize)
    if (code === 200 && success) {
        serveList.value = data.records
        pageConfig.pageSize = data.size
        pageConfig.pageNum = data.current
        pageConfig.total = data.total
        return
    }
    ElMessage.error('获取物流服务列表失败')
}

const handleSizeChange = (value: number) => {
    pageConfig.pageNum = 1
    pageConfig.pageSize = value
    initServeList()
}
const handleCurrentChange = (value: number) => {
    pageConfig.pageNum = value
    initServeList()
}
const handleEdit = (row: any) => {
    currentRow.value = row
    dialogVisible.value = true
}

const handleDelete = async (row: any) => {
    try {
        await ElMessageBox.confirm('确定删除此项?', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
        })
        const { code } = await doDelLogisticsServe(row.id)
        if (code !== 200) return ElMessage.error('删除失败')
        initServeList()
        ElMessage.success('删除成功')
    } catch (e) {
        console.log(e)
    }
}

const handleFormSubmit = async () => {
    const data: any = await serviceRef.value?.getNewLogisticFormData()
    if (data?.id) {
        const { code, msg } = await doUpdateLogisticServe(data)
        if (code !== 200) return ElMessage.error(msg ? msg : '更新失败')
        ElMessage.success('更新成功')
        dialogVisible.value = false
        initServeList()
        return
    }
    const { code, msg } = await doSaveLogisticsServe(data)
    if (code !== 200) return ElMessage.error(msg ? msg : '添加失败')
    ElMessage.success('添加成功')
    dialogVisible.value = false
    initServeList()
}
initServeList()
</script>

<style lang="scss" scoped>
@include b(service) {
    @include e(btn) {
        width: 81px;
        height: 36px;
        font-size: 12px;
        margin-bottom: 10px;
    }
}
</style>
