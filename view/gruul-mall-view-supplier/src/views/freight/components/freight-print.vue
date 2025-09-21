<script setup lang="ts">
import { ref, reactive } from 'vue'
import { doUpdatePrint, doAddPrint, doGetPrintList, doDelPrintById } from '@/apis/freight'
import { ElMessage, ElMessageBox, FormRules } from 'element-plus'
import PageManage from '@/components/PageManage/index.vue'
import { QuestionFilled } from '@element-plus/icons-vue'
import type { ApiPrint } from '@/views/freight/components/types'
import InfoImg from '@/assets/images/delivery_info.png'

const dialogVisible = ref(false)
const printList = ref<ApiPrint[]>([])
const rowID = ref()
const FormRef = ref()
const showInfoDialog = ref(false)
const pageConfig = reactive({
    size: 20,
    current: 1,
    total: 0,
})
const printForm = reactive({
    printName: '',
    printCode: '',
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

async function initList() {
    const { code, data } = await doGetPrintList(pageConfig)
    if (code !== 200) ElMessage.error('列表获取失败')
    const { records, current, size, total, searchCount } = data
    printList.value = records
    pageConfig.current = current
    pageConfig.size = size
    pageConfig.total = total
}
const handleSizeChange = (value: number) => {
    pageConfig.size = value
    initList()
}
const handleCurrentChange = (value: number) => {
    pageConfig.current = value
    initList()
}

const handleDelete = async (row: ApiPrint) => {
    await ElMessageBox.confirm('请确认是否删除？？？', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
    })
    const { code, msg } = await doDelPrintById(row.id)
    if (code !== 200) return ElMessage.error(msg || '删除失败')
    ElMessage.success('删除成功')
    initList()
}
const handleCloseDialog = () => {
    rowID.value = ''
    printForm.printCode = ''
    printForm.printName = ''
    FormRef.value.resetFields()
    initList()
}
const HandleFormSubmit = async () => {
    if (!rowID.value) {
        await FormRef.value.validate()
        const { printName, printCode } = printForm
        const { code } = await doAddPrint(printCode, printName)
        if (code !== 200) return ElMessage.error('新增失败')
        ElMessage.success('新增成功')
        dialogVisible.value = false
        return
    }
    const { printCode, printName } = printForm
    const { code } = await doUpdatePrint(printCode, printName, rowID.value)
    if (code !== 200) return ElMessage.error('编辑失败')
    ElMessage.success('编辑成功')
    dialogVisible.value = false
}
const handleEdit = async (row: ApiPrint) => {
    rowID.value = row.id
    printForm.printCode = row.deviceNo
    printForm.printName = row.printName
    dialogVisible.value = true
}
</script>

<template>
    <div class="distributionServe_container overh fdc1">
        <el-button type="primary" link class="info" @click="showInfoDialog = true">打印机使用说明</el-button>
        <div class="handle_container">
            <el-button class="distributionServe_container__btn" type="primary" round @click="dialogVisible = true">添加打印机</el-button>
        </div>
        <div class="table_container">
            <el-table
                stripe
                empty-text="暂无打印机"
                :data="printList"
                style="width: 100%"
                :header-cell-style="{ color: '#333', background: '#F7F8FA', height: '48px' }"
                :cell-style="{ height: '60px' }"
            >
                <el-table-column label="打印机名称">
                    <template #default="{ row }">
                        {{ row.printName }}
                    </template>
                </el-table-column>
                <el-table-column label="SIID(设备码)">
                    <template #default="{ row }">
                        {{ row.deviceNo }}
                    </template>
                </el-table-column>
                <el-table-column fixed="right" label="操作" width="120" align="right">
                    <template #default="{ row }">
                        <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
                        <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </div>

        <page-manage
            :page-size="pageConfig.size"
            :page-num="pageConfig.current"
            :total="pageConfig.total"
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
            <el-form ref="FormRef" label-position="right" label-width="110px" :model="printForm" style="max-width: 90%" :rules="rules">
                <el-form-item label="打印机名称" prop="printName">
                    <el-input v-model.trim="printForm.printName" maxlength="20" />
                </el-form-item>
                <el-form-item label="SIID(设备码)" prop="printCode">
                    <el-input v-model.trim="printForm.printCode" maxlength="32" />
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="dialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="HandleFormSubmit">提交</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
    <el-dialog v-model="showInfoDialog" title="推荐使用【 快递100 打印机 】" :width="550">
        <el-image :src="InfoImg" style="width: 500px" />
    </el-dialog>
</template>

<style scoped lang="scss">
@include b(distributionServe_container) {
    position: relative;
    @include b(info) {
        position: absolute;
        right: 24px;
        top: 0;
        margin-left: 15px;
        cursor: pointer;
    }
}
</style>
