<script setup lang="ts">
import { doUpdatePrint, doAddPrint, doGetPrintList, doDelPrintById } from '@/apis/freight'
import { ElMessage, ElMessageBox, FormRules } from 'element-plus'
import PageManage from '@/components/PageManage.vue'
import type { PrintList, PrintForm, PageConfig } from '@/views/platformDelivery/pages/types'
import InfoImg from '@/assets/image/delivery_info.png'

const dialogVisible = ref(false)
const printList = ref<PrintList[]>([])
const rowID = ref()
const FormRef = ref()
const showInfoDialog = ref(false)
const pageConfig = reactive<PageConfig>({
    size: 20,
    current: 1,
})
const printForm = reactive<PrintForm>({
    printName: '',
    printCode: '',
    defSelfShop: false,
    defSelfSupplier: false,
})

const rules: FormRules = {
    printCode: [
        { required: true, message: '请输入SIID', trigger: 'blur' },
        {
            validator(_, value, callback) {
                const reg = new RegExp('[\\u4E00-\\u9FFF]+', 'g')
                if (reg.test(value)) {
                    callback(new Error('SIID不能包含汉字'))
                } else {
                    callback()
                }
            },
        },
    ],
    printName: [{ required: true, message: '请输入打印机名称', trigger: 'blur' }],
}

initList()

const total = ref(0)
async function initList() {
    const { code, data, msg } = await doGetPrintList(pageConfig)
    if (code !== 200) ElMessage.error(msg || '列表获取失败')
    const { records, current, size, total: dataTotal, searchCount } = data
    printList.value = records
    pageConfig.current = current
    pageConfig.size = size
    total.value = +dataTotal
}
const handleSizeChange = (value: number) => {
    pageConfig.current = 1
    pageConfig.size = value
    initList()
}
const handleCurrentChange = (value: number) => {
    pageConfig.current = value
    initList()
}

const handleDelete = async (row: PrintList) => {
    await ElMessageBox.confirm('请确认是否删除？？？', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
    })
        .then(async () => {
            const { code, msg } = await doDelPrintById(row.id)
            if (code !== 200) return ElMessage.error(msg || '删除失败')
            ElMessage.success('删除成功')
        })
        .catch(() => {
            ElMessage.info('取消删除')
        })
    initList()
}
const handleCloseDialog = () => {
    dialogVisible.value = false
    rowID.value = ''
    // @ts-ignore
    Object.keys(printForm).forEach((item) => (printForm[item] = null))
    printForm.defSelfShop = false
    printForm.defSelfSupplier = false
    // FormRef.value.resetFields()
}
const HandleFormSubmit = async () => {
    let defSelf = reactive<{ defSelfShops: 'YES' | 'NO'; defSelfSuppliers: 'YES' | 'NO' }>({
        defSelfShops: 'NO',
        defSelfSuppliers: 'NO',
    })
    if (printForm.defSelfShop) defSelf.defSelfShops = 'YES'
    if (printForm.defSelfSupplier) defSelf.defSelfSuppliers = 'YES'
    const { defSelfShops, defSelfSuppliers } = defSelf
    if (!rowID.value) {
        await FormRef.value.validate()
        const { printName, printCode } = printForm
        const { code, msg } = await doAddPrint(printCode, printName, defSelfShops, defSelfSuppliers)
        if (code !== 200) return ElMessage.error(msg || '新增失败')
        ElMessage.success(msg || '新增成功')
        handleCloseDialog()
        initList()
        return
    }
    const { printCode, printName } = printForm
    const { code, msg } = await doUpdatePrint(printCode, printName, rowID.value, defSelfShops, defSelfSuppliers)
    if (code !== 200) return ElMessage.error(msg || '编辑失败')
    ElMessage.success(msg || '编辑成功')
    handleCloseDialog()
    initList()
}

const handleEdit = async (row: PrintList) => {
    if (row.defSelfShop === 'YES') printForm.defSelfShop = true
    if (row.defSelfSupplier === 'YES') printForm.defSelfSupplier = true
    rowID.value = row.id
    printForm.printCode = row.deviceNo
    printForm.printName = row.printName
    dialogVisible.value = true
}
</script>

<template>
    <div class="handle_container" style="display: flex; justify-content: space-between; align-items: center">
        <el-button class="distributionServe_container__btn" type="primary" round @click="dialogVisible = true">添加打印机</el-button>
        <el-link class="info" type="primary" @click="showInfoDialog = true">打印机推荐</el-link>
    </div>
    <!-- 表格部分 -->
    <div class="table_container">
        <el-table
            stripe
            empty-text="暂无打印机"
            :data="printList"
            style="width: 100%"
            :header-cell-style="{ fontSize: '14px', fontWeight: 'bold', color: '#333', background: '#f6f8fa', height: '48px' }"
        >
            <template #empty> <ElTableEmpty /> </template>
            <el-table-column label="打印机名称">
                <template #default="{ row }">
                    <span>{{ row.printName }}</span>
                </template>
            </el-table-column>
            <el-table-column label="SIID(设备码)">
                <template #default="{ row }">
                    <span>{{ row.deviceNo }}</span>
                </template>
            </el-table-column>
            <el-table-column label="应用商家">
                <template #default="{ row }">
                    <span v-if="row.defSelfShop === 'YES'">自营商家</span>
                    <span v-if="row.defSelfSupplier === 'YES'">自营供应商</span>
                </template>
            </el-table-column>
            <el-table-column fixed="right" align="right" label="操作" width="120">
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
        :page-size="pageConfig.size"
        :page-num="pageConfig.current"
        :total="total"
        @handle-size-change="handleSizeChange"
        @handle-current-change="handleCurrentChange"
    />
    <!-- 弹出层 -->
    <el-dialog v-model="dialogVisible" width="30%" @close="handleCloseDialog">
        <template #header="{ titleId, titleClass }">
            <div class="my-header">
                <h4 :id="titleId" :class="titleClass">{{ rowID ? '编辑打印机' : '新增打印机' }}</h4>
            </div>
        </template>
        <el-form ref="FormRef" label-position="right" label-width="130px" :model="printForm" style="max-width: 90%" :rules="rules">
            <el-form-item label="打印机名称" prop="printName">
                <el-input v-model.trim="printForm.printName" maxlength="20" />
            </el-form-item>
            <el-form-item label="SIID(设备码)" prop="printCode">
                <el-input v-model.trim="printForm.printCode" maxlength="32" />
            </el-form-item>
            <el-form-item label="应用商家">
                <el-checkbox v-model="printForm.defSelfShop">自营商家</el-checkbox>
                <el-checkbox v-model="printForm.defSelfSupplier">自营供应商</el-checkbox>
            </el-form-item>
        </el-form>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="handleCloseDialog">取消</el-button>
                <el-button type="primary" @click="HandleFormSubmit">提交</el-button>
            </span>
        </template>
    </el-dialog>
    <el-dialog v-model="showInfoDialog" title="推荐使用【 快递100 打印机 】" :width="550">
        <el-image :src="InfoImg" style="width: 500px" />
    </el-dialog>
</template>

<style scoped lang="scss">
@include b(info) {
    margin-left: 15px;
    cursor: pointer;
}
</style>
