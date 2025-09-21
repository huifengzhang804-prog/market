<script setup lang="ts">
import { ref, reactive, computed, type PropType } from 'vue'
import ElTableEmpty from '@/components/element-plus/el-table/ElTableEmpty/index.vue'
import PageManage from '@/components/PageManage.vue'
import { ElMessageBox, ElMessage, FormInstance } from 'element-plus'
import { getPrinter, doUpdatePrinter, doDelPrinter, doAddPrinter } from '@/apis/order'
import { PickPageParams } from '@/utils/types'
import { Printer, PrinterBrand, PrinterDTO, PrinterDTOMode, PrinterEditDTO, PrinterSize, PrinterStatus, PrinterType, makeOptions } from './types'

const props = defineProps({
    mode: {
        type: String as PropType<keyof typeof PrinterDTOMode>,
        default: 'STORE_PICKUP_SELF',
    },
})
const pageConfig = reactive<PickPageParams<'current' | 'size' | 'pages' | 'total'>>({
    current: 1, // 当前页码
    size: 10, // 每页条数
    pages: 0, // 总页数
    total: 0, // 总条数
})
const printerList = ref<Printer[]>([])
const initPrinterList = async () => {
    const { code, data, msg } = await getPrinter({
        ...pageConfig,
        mode: props.mode,
    })
    if (code === 200 && data) {
        pageConfig.pages = data.pages
        pageConfig.total = data.total
        printerList.value = data.records
    } else {
        ElMessage.error(msg || '获取打印机列表失败')
    }
}
initPrinterList()
const handleSizeChange = (value: number) => {
    pageConfig.size = value
    initPrinterList()
}
const handleCurrentChange = (value: number) => {
    pageConfig.current = value
    initPrinterList()
}

const dialogAddVisible = ref(false)
const dialogEditVisible = ref(false)
const printerAddForm = ref<PrinterDTO>({
    brand: 'FEIE',
    place: '',
    name: '',
    mode: props.mode,
    sn: '',
    key: '',
    flowCard: '',
})
const printerEditForm = ref<PrinterEditDTO>({
    id: '',
    place: '',
    name: '',
    mode: props.mode,
})
const printerFormRef = ref()
const printerEditFormRef = ref()
const confirm = (formRef: FormInstance) => {
    formRef.validate(async (valid: boolean) => {
        if (valid) {
            const { code, msg } = dialogEditVisible.value ? await doUpdatePrinter(printerEditForm.value) : await doAddPrinter(printerAddForm.value)
            if (code === 200) {
                ElMessage.success(`${editOrAdd.value}成功`)
                dialogAddVisible.value = false
                dialogEditVisible.value = false
                initPrinterList()
            } else {
                ElMessage.error(msg || `${editOrAdd.value}失败`)
            }
        }
    })
}
const editOrAdd = computed(() => {
    return dialogEditVisible.value ? '编辑' : '新增'
})
const rules = {
    name: [{ required: true, message: '请输入打印机名称', trigger: 'blur' }],
    brand: [{ required: true, message: '请选择打印机品牌', trigger: 'blur' }],
    sn: [{ required: true, message: '请输入打印机 sn 码', trigger: 'blur' }],
}

const printerBrandList = makeOptions(PrinterBrand)

const editPrinter = async (row: Printer) => {
    dialogEditVisible.value = true
    await nextTick()
    printerEditForm.value = { ...row, mode: props.mode }
}

const delPrinter = async (row: Printer) => {
    // 绑定任务中不可删除
    if (row.bound) {
        ElMessage.warning('打印机存在关联打印任务无法删除')
    } else {
        try {
            await ElMessageBox.confirm('打印机删除后无法恢复，确定要删除该打印机吗？', '删除打印机', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning',
            })
            const { code, msg } = await doDelPrinter({ printerId: row.id })
            if (code === 200) {
                initPrinterList()
                ElMessage.success('删除打印机成功')
            } else {
                ElMessage.error(msg || '删除打印机失败')
            }
        } catch (e) {
            console.log(' ', e)
        }
    }
}

const imgDialogVisible = ref(false)

/**
 * 计算在线状态
 */
const isOnline = (row: Printer) => {
    return row.status.includes('ONLINE') ? '在线' : '离线'
}
/**
 * 计算工作状态
 */
const isWorking = (row: Printer) => {
    return !row.status.includes('NOT_OK') ? '正常' : '异常'
}
</script>

<template>
    <div class="q_plugin_container overf">
        <div class="handle_container fcenter">
            <el-button type="primary" @click="dialogAddVisible = true">新增打印机</el-button>
            <div class="tips">异常一般是无纸，离线是指打印机与服务器失去联系超过2分钟。</div>
            <el-button style="margin-left: auto" type="primary" link @click="imgDialogVisible = true">示意图</el-button>
        </div>
        <div class="table_container">
            <el-table :data="printerList" style="width: 100%">
                <template #empty>
                    <ElTableEmpty />
                </template>
                <el-table-column prop="name" label="打印机名称" width="130" fixed="left"> </el-table-column>
                <el-table-column prop="brand" label="打印机品牌" width="130">
                    <template #default="{ row }">
                        <span>{{ PrinterBrand[(row as Printer).brand] }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="size" label="打印纸宽">
                    <template #default="{ row }">
                        <span>{{ PrinterSize[(row as Printer).size] }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="type" label="机型" width="130">
                    <template #default="{ row }">
                        <span>{{ PrinterType[(row as Printer).type] }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="status" label="在线状态">
                    <template #default="{ row }">
                        <span>{{ isOnline(row) }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="status" label="工作状态">
                    <template #default="{ row }">
                        <span>{{ isWorking(row) }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="place" label="使用位置"> </el-table-column>
                <el-table-column prop="createDate" label="添加时间" width="100"> </el-table-column>
                <el-table-column label="操作" fixed="right" width="150">
                    <template #default="{ row }">
                        <el-button type="primary" link @click="editPrinter(row)">编辑</el-button>
                        <el-button type="danger" link @click="delPrinter(row)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </div>
        <PageManage
            :pageSize="pageConfig.size"
            :total="pageConfig.total"
            :pageNum="pageConfig.current"
            @handleSizeChange="handleSizeChange"
            @handleCurrentChange="handleCurrentChange"
        />
        <el-dialog v-model="dialogAddVisible" :title="`${editOrAdd}打印机`" width="600" @close="printerFormRef?.resetFields()">
            <el-form ref="printerFormRef" :model="printerAddForm" :rules="rules" label-width="120px">
                <el-form-item label="打印机名称" prop="name">
                    <el-input v-model="printerAddForm.name" maxlength="20" show-word-limit placeholder="请填写打印机名称"></el-input>
                </el-form-item>
                <el-form-item v-if="!dialogEditVisible" label="打印机品牌" prop="brand">
                    <el-select v-model="printerAddForm.brand" placeholder="请选择打印机品牌">
                        <el-option v-for="item in printerBrandList" :key="item.value" :label="item.label" :value="item.value"> </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item v-if="!dialogEditVisible" label="编号(SN/SIID)" prop="sn">
                    <el-input v-model="printerAddForm.sn" maxlength="10" show-word-limit></el-input>
                </el-form-item>
                <el-form-item v-if="!dialogEditVisible" label="密钥(KEY)" prop="key">
                    <el-input v-model="printerAddForm.key" maxlength="20" show-word-limit placeholder="设备上有则必填"></el-input>
                </el-form-item>
                <el-form-item v-if="!dialogEditVisible" label="流量卡密码" prop="flowCard">
                    <el-input v-model="printerAddForm.flowCard" maxlength="20" show-word-limit placeholder="支持流量卡的打印机请填写"></el-input>
                </el-form-item>
                <el-form-item label="使用位置" prop="place">
                    <el-input v-model="printerAddForm.place" maxlength="20" show-word-limit placeholder="请输入使用位置"></el-input>
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="dialogAddVisible = false">取 消</el-button>
                <el-button type="primary" @click="confirm(printerFormRef)">确 定</el-button>
            </template>
        </el-dialog>
        <el-dialog v-model="dialogEditVisible" :title="`${editOrAdd}打印机`" width="600" @close="printerEditFormRef?.resetFields()">
            <el-form ref="printerEditFormRef" :model="printerEditForm" :rules="rules" label-width="120px">
                <el-form-item label="打印机名称" prop="name">
                    <el-input v-model="printerEditForm.name" maxlength="20" show-word-limit placeholder="请填写打印机名称"></el-input>
                </el-form-item>
                <el-form-item label="使用位置" prop="place">
                    <el-input v-model="printerEditForm.place" maxlength="20" show-word-limit placeholder="请输入使用位置"></el-input>
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="dialogEditVisible = false">取 消</el-button>
                <el-button type="primary" @click="confirm(printerEditFormRef)">确 定</el-button>
            </template>
        </el-dialog>
        <el-dialog v-model="imgDialogVisible" title="示意图" width="500">
            <div>
                <el-image
                    src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/s2b2c_2.0.8/2024/10/2267170459e4b0dd23b7a8b456.png"
                ></el-image>
            </div>
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
