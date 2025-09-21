<script lang="ts" setup>
import { type PropType, reactive, ref } from 'vue'
import ElTableEmpty from '@/components/element-plus/el-table/ElTableEmpty/index.vue'
import PageManage from '@/components/PageManage.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { doDelPrintTask, doUpdatePrintTask, getPrinter, getPrintTask, getPrintTemplate } from '@/apis/order'
import {
    makeOptions,
    Printer,
    PrinterDTOMode,
    PrintTask,
    PrintTaskDTO,
    PrintTaskLink,
    PrintTaskScene,
    ToStorePrintTaskScene,
    PrintTemplate,
} from './types'
import { PickPageParams } from '@/utils/types'

const props = defineProps({
    mode: {
        type: String as PropType<keyof typeof PrinterDTOMode>,
        default: 'STORE_PICKUP_SELF',
    },
})
console.log(props.mode, 'props.mode')
const printTaskFormRef = ref()
const printTaskForm = ref<PrintTaskDTO>({
    id: '',
    name: '',
    mode: props.mode,
    printerId: '',
    templateId: '',
    templateName: '',
    scene: 'MANUAL',
    times: 1,
    link: '',
})
const rules = {
    name: [{ required: true, message: '请输入打印任务名称', trigger: 'change' }],
    printerId: [{ required: true, message: '请选择打印机', trigger: 'change' }],
    templateId: [{ required: true, message: '请选择打印模板', trigger: 'change' }],
    link: [{ required: true, message: '请选择打印类型', trigger: 'change' }],
}

/**
 * 打印机列表
 */
const printerList = ref<Printer[]>([])
const getPrinterList = async () => {
    const { code, data, msg } = await getPrinter({
        pages: 0,
        size: 1000,
        mode: props.mode,
    })
    if (code === 200 && data) {
        printerList.value = data.records
    } else {
        ElMessage.error(msg || '获取打印机列表失败')
    }
}
getPrinterList()
/**
 * 打印模板列表
 */
const printerTempList = ref<PrintTemplate[]>([])
const getPrinterTempList = async (link?: '' | 'CUSTOMER' | 'MERCHANT' | 'KITCHEN' | 'REMIND') => {
    printerTempList.value = []
    const size = printerList.value.find((item) => item.id === printTaskForm.value.printerId)?.size || ''
    const { code, data, msg } = await getPrintTemplate({
        pages: 0,
        size: 1000,
        mode: props.mode,
        link: (link as keyof typeof PrintTaskLink) || printTaskForm.value.link,
        ticketSize: size,
    })
    if (code === 200 && data) {
        if (!data.records.find((item) => item.id === printTaskForm.value.templateId)) {
            printTaskForm.value.templateId = ''
        }
        printerTempList.value = data.records
    } else {
        printerTempList.value = []
        ElMessage.error(msg || '获取打印机列表失败')
    }
}

/**
 * 打印任务列表
 */
const pageConfig = reactive<PickPageParams<'current' | 'size' | 'pages' | 'total'>>({
    current: 1, // 当前页码
    size: 10, // 每页条数
    pages: 0, // 总页数
    total: 0, // 总条数
})
const printerTaskList = ref<PrintTask[]>([])
const initPrinterTaskList = async () => {
    const { code, data, msg } = await getPrintTask({
        ...pageConfig,
        mode: props.mode,
    })
    if (code === 200 && data) {
        pageConfig.pages = data.pages
        pageConfig.total = data.total
        printerTaskList.value = data.records
    } else {
        ElMessage.error(msg || '获取打印任务列表失败')
    }
}
initPrinterTaskList()

const handleSizeChange = (value: number) => {
    pageConfig.size = value
    initPrinterTaskList()
}
const handleCurrentChange = (value: number) => {
    pageConfig.current = value
    initPrinterTaskList()
}

const dialogVisible = ref(false)
const confirm = () => {
    printTaskFormRef.value.validate(async (valid: boolean) => {
        if (valid) {
            const { code, msg } = await doUpdatePrintTask(printTaskForm.value)
            if (code === 200) {
                ElMessage.success(`${printTaskForm.value.id ? '编辑' : '添加'}成功`)
                dialogVisible.value = false
                initPrinterTaskList()
            } else {
                ElMessage.error(msg || `${printTaskForm.value.id ? '编辑' : '添加'}失败`)
            }
        }
    })
}

const editPrintTask = async (row: PrintTask) => {
    dialogVisible.value = true
    await nextTick()
    await getPrinterTempList(row.link)
    printTaskForm.value = { ...row, mode: props.mode, templateId: -1 }
}
const addPrintTask = async () => {
    printTaskForm.value.id = ''
    dialogVisible.value = true
    await nextTick()
    getPrinterTempList()
}
const delPrinterTask = async (row: PrintTask) => {
    try {
        await ElMessageBox.confirm('打印任务删除后无法恢复，确定要删除该打印任务吗？', '删除打印任务', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
        })
        const { code, msg } = await doDelPrintTask({ id: row.id })
        if (code === 200) {
            initPrinterTaskList()
            ElMessage.success('删除打印机成功')
        } else {
            ElMessage.error(msg || '删除打印机失败')
        }
    } catch (e) {
        console.log(' ', e)
    }
}

const PrintTaskLinkOptions = makeOptions(PrintTaskLink)
const PrintTaskSceneOptions = makeOptions(props.mode === 'INTRA_CITY' ? ToStorePrintTaskScene : PrintTaskScene)
</script>

<template>
    <div class="q_plugin_container overf">
        <div class="handle_container fcenter">
            <el-button type="primary" @click="addPrintTask">新增打印任务</el-button>
            <div class="tips">已关联模板的打印任务不受模板后续修改影响；编辑或新建打印任务将采用编辑后的模板设置。</div>
        </div>
        <div class="table_container">
            <el-table :data="printerTaskList" style="width: 100%">
                <template #empty>
                    <ElTableEmpty />
                </template>
                <el-table-column fixed="left" label="任务名称" prop="name" width="180"></el-table-column>
                <el-table-column label="打印机名称" prop="printerName" width="130"></el-table-column>
                <el-table-column label="打印类型" prop="link">
                    <template #default="{ row }">
                        <span>{{ PrintTaskLink[(row as PrintTask).link] }}</span>
                    </template>
                </el-table-column>
                <el-table-column label="打印模板" prop="templateName" width="135"></el-table-column>
                <el-table-column label="打印场景" prop="scene" width="135">
                    <template #default="{ row }">
                        <span>{{
                            props.mode === 'INTRA_CITY' ? ToStorePrintTaskScene[(row as PrintTask).scene] : PrintTaskScene[(row as PrintTask).scene]
                        }}</span>
                    </template>
                </el-table-column>
                <el-table-column label="打印联数" prop="times">
                    <template #default="{ row }">
                        <span>{{ row.times }}联</span>
                    </template>
                </el-table-column>
                <el-table-column label="添加时间" prop="createDate" width="135"></el-table-column>
                <el-table-column fixed="right" label="操作" width="150">
                    <template #default="{ row }">
                        <el-button link type="primary" @click="editPrintTask(row)">编辑</el-button>
                        <el-button link type="danger" @click="delPrinterTask(row)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </div>
        <PageManage
            :page-size="pageConfig.size"
            :total="pageConfig.total"
            :page-num="pageConfig.current"
            @handle-size-change="handleSizeChange"
            @handle-current-change="handleCurrentChange"
        />
        <el-dialog v-model="dialogVisible" :title="printTaskForm.id ? '编辑' : '新增'" width="600" @close="printTaskFormRef?.resetFields()">
            <template #header>
                <span style="font-weight: bold; font-size: 16px; color: #333">{{ printTaskForm.id ? '编辑' : '新增' }}</span>
            </template>
            <div>
                <el-form ref="printTaskFormRef" :model="printTaskForm" :rules="rules" label-width="90px">
                    <el-form-item label="任务名称" prop="name">
                        <el-input v-model="printTaskForm.name" maxlength="20" placeholder="请填写任务名称" show-word-limit></el-input>
                    </el-form-item>
                    <el-form-item label="打印机" prop="printerId">
                        <el-select v-model="printTaskForm.printerId" placeholder="请选择打印机" @change="() => getPrinterTempList()">
                            <el-option v-for="item in printerList" :key="item.id" :label="item.name" :value="item.id"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="打印类型" prop="link">
                        <el-select v-model="printTaskForm.link" placeholder="请选择打印类型" @change="getPrinterTempList">
                            <el-option v-for="item in PrintTaskLinkOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="打印模板" prop="templateId">
                        <el-select v-model="printTaskForm.templateId" placeholder="请选择打印模板">
                            <el-option v-if="printTaskForm.id && printTaskForm.templateId === -1" :label="printTaskForm.templateName" :value="-1">
                                <span>{{ printTaskForm.templateName }}</span>
                                <el-tag size="small" type="warning">快照</el-tag>
                            </el-option>
                            <el-option v-for="item in printerTempList" :key="item.id" :label="item.name" :value="item.id"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="打印场景" prop="scene">
                        <el-select v-model="printTaskForm.scene" placeholder="请选择打印场景">
                            <el-option v-for="item in PrintTaskSceneOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="打印联数" prop="times">
                        <el-select v-model="printTaskForm.times" placeholder="请选择打印场景">
                            <el-option v-for="item in 4" :key="item" :label="item + '联'" :value="item"></el-option>
                        </el-select>
                    </el-form-item>
                </el-form>
            </div>
            <template #footer>
                <el-button @click="dialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="confirm">确 定</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<style lang="scss" scoped>
.tips {
    color: rgb(153, 153, 153);
    font-size: 13px;
    font-weight: 400;
    margin-left: 10px;
}
</style>
