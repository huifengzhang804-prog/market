<template>
    <el-config-provider :empty-values="[undefined, null]">
        <Search @search="handleSearch">
            <el-button round type="primary" @click="handleExport">导出</el-button>
        </Search>
    </el-config-provider>
    <div class="grey_bar"></div>
    <div class="operate-bar handle_container">
        <div>
            <el-button @click="handleOpenRemarkDialog">批量备注</el-button>
        </div>
        <QIcon name="icon-wenhao" size="18px" color="#333" style="cursor: pointer" @click="showDescriptionDialog = true" />
    </div>
    <div class="table_container">
        <el-table
            :data="memberRecordsData"
            :header-row-style="{ color: '#333' }"
            :header-cell-style="{ background: '#F7F8FA', height: '50px' }"
            :cell-style="{ color: '#333333', height: '60px' }"
            @selection-change="handleSelectionChange"
        >
            <template #empty> <ElTableEmpty /> </template>
            <el-table-column type="selection" width="35" fixed="left" />
            <el-table-column label="流水编号" prop="no" width="210" />
            <el-table-column label="用户昵称" prop="userNickName" width="130" />
            <el-table-column label="手机号" prop="userPhone" width="140" />
            <el-table-column label="操作类型" prop="operatorTypeStr" width="120" />
            <el-table-column label="变动金额(元)" prop="amountStr" width="160" />
            <el-table-column label="期后金额(元)" prop="afterAmountStr" width="160" />
            <el-table-column label="关联订单" prop="orderNo" width="230" />
            <el-table-column label="操作人" prop="operatorUserNickName" width="130" />
            <el-table-column label="操作时间" prop="createTime" width="180" />
            <el-table-column label="备注" fixed="right" align="right">
                <template #default="{ row }">
                    <el-tooltip v-if="row.remark" class="box-item" effect="dark" :content="row.remark" placement="bottom-end">
                        <template #content>
                            <div style="max-width: 440px">{{ row.remark }}</div>
                        </template>
                        <el-link type="primary" @click="handlePreviewRemark(row)">备注</el-link>
                    </el-tooltip>
                    <el-link v-else type="primary" @click="handlePreviewRemark(row)">备注</el-link>
                </template>
            </el-table-column>
        </el-table>
    </div>
    <page-manage
        :page-size="pagination.pageSize"
        :page-num="pagination.current"
        :total="pagination.total"
        @handle-size-change="handleSizeChange"
        @handle-current-change="handleCurrentChange"
    />
    <el-dialog v-model="showDescriptionDialog" title="储值流水说明" :width="800">
        <description-dialog />
    </el-dialog>

    <el-dialog v-model="remarkDialog.show" title="备注" :width="550">
        <el-input v-model="remarkDialog.remark" type="textarea" placeholder="请填写备注信息" :maxlength="50" :rows="4" />
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="closeRemarkDialog">取消</el-button>
                <el-button type="primary" @click="confirmRemarkDialog">确认</el-button>
            </span>
        </template>
    </el-dialog>
</template>

<script lang="ts" setup>
import Search from './search.vue'
import PageManage from '@/components/PageManage.vue'
import descriptionDialog from './description-dialog.vue'
import { doGetUserBalanceList, doPostBatchRemarkUserBalance, doPostExportUserBalanceList } from '@/apis/vip'
import { ElMessage } from 'element-plus'
const memberRecordsData = ref<any[]>([])
const selectedRecordsData = ref<any[]>([])
const showDescriptionDialog = ref(false)
const searchData = reactive({
    orderNo: '',
    userNickName: '',
    userPhone: '',
    operatorType: '',
    no: '',
    operatorStartTime: '',
    operatorEndTime: '',
})

const pagination = reactive({
    current: 1,
    pageSize: 10,
    total: 0,
})

const remarkDialog = reactive({
    show: false,
    remark: '',
})

// 处理选择变化
function handleSelectionChange(selection: any[]) {
    selectedRecordsData.value = selection
}
const handleCurrentChange = (current: number) => {
    pagination.current = current
    initialData()
}
const handleSizeChange = (pageSize: number) => {
    pagination.pageSize = pageSize
    pagination.current = 1
    initialData()
}

const handleSearch = (searchOptions: typeof searchData) => {
    Object.keys(searchData).forEach((key) => {
        // @ts-ignore
        searchData[key] = searchOptions[key] || ''
    })
    initialData()
}
const initialData = async () => {
    let datas: any[] = [],
        total = 0
    try {
        const { data, code, msg } = await doGetUserBalanceList({ ...searchData, current: pagination.current, size: pagination.pageSize })
        if (code === 200) {
            datas = data?.records || []
            total = +data?.total
        }
    } finally {
        memberRecordsData.value = datas
        pagination.total = total
    }
}

initialData()

const handleExport = async () => {
    let params: any = {}
    if (selectedRecordsData.value.length) {
        params.exportIds = selectedRecordsData.value.map((item) => item.id)
    } else {
        params = searchData
    }
    const { code, msg } = await doPostExportUserBalanceList(params)
    if (code === 200) {
        ElMessage.success({ message: msg || '导出成功' })
    } else {
        ElMessage.error({ message: msg || '导出失败' })
    }
}

const handleOpenRemarkDialog = () => {
    if (!selectedRecordsData.value.length) return ElMessage.error({ message: '请选择备注记录' })
    remarkDialog.show = true
}
const closeRemarkDialog = () => {
    remarkDialog.remark = ''
    remarkDialog.show = false
}
/**
 * 备注
 */
const remarksList = ref<string[]>([])
const confirmRemarkDialog = async () => {
    const ids = selectedRecordsData.value.length > 0 ? selectedRecordsData.value?.map((item) => item.id) : remarksList.value
    const remark = remarkDialog.remark
    const { code, msg } = await doPostBatchRemarkUserBalance({ ids, remark })
    if (code === 200) {
        ElMessage.success({ message: msg || '备注成功' })
        closeRemarkDialog()
        initialData()
    } else {
        ElMessage.success({ message: msg || '备注失败' })
    }
}
const handlePreviewRemark = (row: any) => {
    remarkDialog.show = true
    remarkDialog.remark = row.remark
    remarksList.value = [row.id]
}
</script>

<style lang="scss" scoped>
@include b(operate-bar) {
    padding-top: 16px;
    @include flex(space-between, center);
}
</style>
