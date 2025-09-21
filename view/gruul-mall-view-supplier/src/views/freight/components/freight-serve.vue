<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
    doGetLogisticsCompany,
    doGetDeliveryAddress,
    doSaveLogisticsServe,
    doGetLogisticServeList,
    doUpdateLogisticServe,
    doDelLogisticsServe,
    doGetPrintList,
} from '@/apis/freight'
import PageManage from '@/components/PageManage/index.vue'
import type { ApiAddress, ApiLogisticCompany, ApiServeList, EditRowType, ApiPrint } from '@/views/freight/components/types'

const dialogVisible = ref(false)
const logisticsForm = reactive({
    freightId: '',
    addressId: '',
    customerCode: '',
    customerPassword: '',
    networkName: '',
    networkCode: '',
    logisticsPrintId: '',
})
const pageConfig = reactive({
    pageSize: 20,
    pageNum: 1,
    total: 0,
})
const FormRef = ref()
const rules = reactive({
    freightId: [{ required: true, message: '请选择快递公司', trigger: 'change' }],
    logisticsPrintId: [{ required: true, message: '请选择打印机', trigger: 'change' }],
    customerCode: [{ required: true, message: '未填客户号', trigger: 'blur' }],
    customerPassword: [{ required: true, message: '请输入密码', trigger: 'blur' }],
    networkName: [{ required: true, message: '请输入网点名称', trigger: 'blur' }],
})
//物流公司列表
const companySelectList = ref<ApiLogisticCompany[]>([])
//收货地址列表
const addressSelectList = ref<ApiAddress[]>([])
//物流列表
const serveList = ref<ApiServeList[]>([])
// tab当前行的物流 ID
const rowID = ref('')
const pageConfigPrint = reactive({
    pageSize: 20,
    pageNum: 1,
    total: 0,
})
// 打印机列表信息
const printList = ref<ApiPrint[]>([])

initLogisticsCompany()
initAddress()
initServeList()
initPrintList()

const handleSizeChange = (value) => {
    pageConfig.pageSize = value
    initServeList()
}
const handleCurrentChange = (value) => {
    pageConfig.pageNum = value
    initServeList()
}
/**
 * @description: 编辑回显
 * @returns {*}
 */
const handleEdit = (row: EditRowType) => {
    rowID.value = row.id
    for (const key in logisticsForm) {
        if (row[key]) {
            logisticsForm[key] = row[key]
        }
    }
    dialogVisible.value = true
}
/**
 * @description: 删除物流服务
 * @returns {*}
 */
const handleDelete = async (row: EditRowType) => {
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
        console.error(e)
    }
}
/**
 * @description: 新增 / 编辑
 * @returns {*}
 */
const HandleFormSubmit = async () => {
    // 请求编辑
    if (rowID.value) {
        const data = { ...logisticsForm, id: rowID.value }
        const { code, msg } = await doUpdateLogisticServe(data)
        if (code !== 200) return ElMessage.error(msg ? msg : '更新失败')
        ElMessage.success('更新成功')
        dialogVisible.value = false
        return
    }
    await FormRef.value.validate()
    const { code, msg } = await doSaveLogisticsServe(logisticsForm)
    if (code !== 200) return ElMessage.error(msg ? msg : '添加失败')
    ElMessage.success('添加成功')
    dialogVisible.value = false
}
/**
 * @description: 弹窗即将关闭
 * @returns {*}
 */
const handleCloseDialog = () => {
    rowID.value = ''
    FormRef.value.resetFields()
    for (const key in logisticsForm) {
        logisticsForm[key] = ''
    }
    initServeList()
}
async function initAddress() {
    const { code, data, success } = await doGetDeliveryAddress()
    if (code !== 200 && success) return ElMessage.error('获取收货地址失败')
    addressSelectList.value = data.records
}
/**
 * @description:获取物流公司
 * @returns {*}
 */
async function initLogisticsCompany() {
    const { code, data, success } = await doGetLogisticsCompany()
    if (code === 200 && success) {
        companySelectList.value = data.records
    } else {
        ElMessage.error('获取物流公司失败')
    }
}
/**
 * @description: 获取服务列表
 */
async function initServeList() {
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
async function initPrintList() {
    const { code, data } = await doGetPrintList({
        size: pageConfigPrint.pageSize,
        current: pageConfigPrint.pageNum,
    })
    if (code !== 200) ElMessage.error('打印机列表获取失败')
    const { records, current, size, total, searchCount } = data
    printList.value = records
    pageConfigPrint.pageNum = current
    pageConfigPrint.pageSize = size
    pageConfigPrint.total = total
}
</script>

<template>
    <div class="handle_container">
        <el-button class="distributionServe_container__btn" type="primary" round @click="dialogVisible = true">添加服务</el-button>
    </div>
    <div class="table_container">
        <el-table
            :data="serveList"
            empty-text="暂无服务"
            style="width: 100%"
            :header-cell-style="{
                backgroundColor: '#F7F8FA',
                color: '#333',
                height: '48px',
            }"
            :cell-style="{ height: '60px' }"
        >
            <el-table-column label="快递公司" width="140">
                <template #default="{ row }">
                    {{ row.logisticsCompanyName }}
                </template>
            </el-table-column>
            <el-table-column label="客户号">
                <template #default="{ row }">
                    {{ row.customerCode }}
                </template>
            </el-table-column>
            <el-table-column label="网点名称" width="300">
                <template #default="{ row }">
                    {{ row.networkName }}
                </template>
            </el-table-column>
            <el-table-column label="网点编码" width="120">
                <template #default="{ row }">
                    {{ row.networkCode }}
                </template>
            </el-table-column>
            <el-table-column label="打印机">
                <template #default="{ row }">
                    {{ row.printName }}
                </template>
            </el-table-column>
            <el-table-column fixed="right" align="right" label="操作" width="120">
                <template #default="{ row }">
                    <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
                    <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
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
    <!-- 弹出层 -->
    <el-dialog v-model="dialogVisible" width="34%" center @close="handleCloseDialog">
        <template #header="{ titleId, titleClass }">
            <div class="my-header">
                <h4 :id="titleId" :class="titleClass">{{ rowID ? '编辑物流服务' : '新增物流服务' }}</h4>
            </div>
        </template>
        <el-form ref="FormRef" label-position="right" label-width="130px" :model="logisticsForm" :rules="rules">
            <el-form-item label="选择快递公司" prop="freightId">
                <el-select
                    v-model="logisticsForm.freightId"
                    :placeholder="companySelectList.length > 0 ? '请选择快递公司' : '等待平台端添加物流快递'"
                    style="width: 90%"
                >
                    <el-option v-for="item in companySelectList" :key="item.id" :label="item.logisticsCompanyName" :value="item.id" />
                </el-select>
            </el-form-item>
            <el-form-item label="选择打印机" prop="logisticsPrintId">
                <el-select v-model="logisticsForm.logisticsPrintId" placeholder="请选择打印机" style="width: 90%">
                    <el-option v-for="item in printList" :key="item.id" :label="item.printName" :value="item.id" />
                </el-select>
            </el-form-item>
            <el-form-item label="客户号" prop="customerCode">
                <el-input
                    v-model.trim="logisticsForm.customerCode"
                    maxlength="20"
                    placeholder="请输入客户号"
                    style="width: 90%; margin-right: 10px"
                />
                <el-tooltip
                    class="box-item"
                    effect="dark"
                    content="您与快递公司合作后，其提供的月结账号信息(客户号、密码、网点名称、网点编号)"
                    placement="bottom-end"
                >
                    <QIcon name="icon-wenhao" color="#999"></QIcon>
                </el-tooltip>
            </el-form-item>
            <el-form-item label="密码" prop="customerPassword">
                <el-input v-model.trim="logisticsForm.customerPassword" placeholder="请输入密码" style="width: 90%" />
            </el-form-item>
            <el-form-item label="网点名称" prop="networkName">
                <el-input v-model.trim="logisticsForm.networkName" maxlength="20" placeholder="请输入网点名称" style="width: 90%" />
            </el-form-item>
            <el-form-item label="网点编号" prop="networkCode">
                <el-input v-model.trim="logisticsForm.networkCode" maxlength="20" placeholder="请输入网点编号" style="width: 90%" />
            </el-form-item>
        </el-form>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button type="primary" @click="HandleFormSubmit">提交</el-button>
            </span>
        </template>
    </el-dialog>
</template>

<style lang="scss" scoped></style>
