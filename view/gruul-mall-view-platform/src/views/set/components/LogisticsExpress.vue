<script setup lang="ts">
import { ElMessage, ElMessageBox } from 'element-plus'
import PageManage from '@/components/PageManage.vue'
import Data from '@/assets/json/data.json'
import { doUpdateLogistics, doDelLogistics, doGetLogisticsList, doEditLogistics, doForbiddenLogistics } from '@/apis/set'
import type { LogisticsForm, LogisticsFormID, Row } from '@/views/set/type'
import type { FormInstance, FormRules } from 'element-plus'
import DeliveryOfficialImage from '@/assets/image/delivery_official.png'

/**
 * 表格渲染数据
 */
const initData = Data
const RefLogistics = ref<FormInstance>()
let pendingList: Row[] = [] //保存批量操作数据
let LogisticsId = ref('') // 编辑物流公司id
const tableDataLogistics = ref({
    current: 1,
    pages: 1,
    records: [],
    size: 10,
    total: 0,
})

const dialogLogistics = ref(false)
const form = ref<LogisticsForm>({
    logisticsCompanyCode: '',
    logisticsCompanyName: '',
    logisticsCompanyStatus: 'FORBIDDEN',
    printTempNo: '',
})
// 表单校验规则
const rules = reactive<FormRules>({
    logisticsCompanyCode: [{ required: true, message: '请选择物流', trigger: 'change' }],
    logisticsCompanyStatus: [{ required: true, message: '请设定状态', trigger: 'change' }],
    printTempNo: [{ required: true, message: '请输入电子面单编号', trigger: 'change' }],
})
/**
 * 初始化物流公司列表
 */
const initTableDataLogistics = async () => {
    const { current, size } = tableDataLogistics.value
    const { code, data } = await doGetLogisticsList({ current, size })
    if (code === 200) {
        tableDataLogistics.value = data
    }
}
initTableDataLogistics()
/**
 * 编辑事件 根据id获取详情
 */
const handleEditLogistics = async (row?: LogisticsFormID) => {
    if (row) {
        LogisticsId.value = row.id
        form.value = {
            logisticsCompanyCode: row.logisticsCompanyCode,
            logisticsCompanyName: row.logisticsCompanyName,
            logisticsCompanyStatus: row.logisticsCompanyStatus,
            printTempNo: row.printTempNo,
        }
    }
    dialogLogistics.value = true
}
/**
 * 根据id删除物流信息
 * @param {*} item tab 中的 row
 */
const handleDeleteLogistics = (item: Row) => {
    if (item.logisticsCompanyStatus === 'OPEN') {
        return ElMessage({
            message: `${item.logisticsCompanyName}处于开启状态`,
            type: 'error',
        })
    }
    ElMessageBox.confirm('确认删除当前项吗?', '', {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning',
    })
        .then(async () => {
            const { code } = await doDelLogistics([item.id])
            ElMessage({
                message: '删除成功',
                type: 'success',
            })
            await initTableDataLogistics()
            dialogLogistics.value = false
        })
        .catch(() => {})
}
/**
 * 处理提交物流接口
 */
const handleSubmitLogistics = async () => {
    const logisticsCompanyName = initData.find((item) => item.companyCode === form.value.logisticsCompanyCode)?.companyName
    const data: any = { ...form.value, logisticsCompanyName }
    if (LogisticsId.value) {
        data.id = LogisticsId.value
        const { code, msg } = await doEditLogistics(data)
        if (code !== 200) {
            ElMessage.error(msg)
            return
        }
        ElMessage({
            message: '编辑物流成功',
            type: 'success',
        })
        reset()
    } else {
        await RefLogistics.value?.validate()
        const { code, msg } = await doUpdateLogistics(data)
        if (code !== 200) {
            ElMessage.error(msg)
            return
        }
        ElMessage({
            message: '添加物流成功',
            type: 'success',
        })
        reset()
    }
}

async function reset() {
    await initTableDataLogistics()
    dialogLogistics.value = false
    RefLogistics.value?.resetFields()
    LogisticsId.value = ''
    form.value.logisticsCompanyCode = ''
    form.value.logisticsCompanyName = ''
    form.value.logisticsCompanyStatus = ''
    form.value.printTempNo = ''
}
/**
 * 处理Switch状态
 */
const handleSwitchLogistics = async (row: Row) => {
    const { id, logisticsCompanyStatus, logisticsCompanyName, logisticsCompanyCode } = row
    const data = { id, logisticsCompanyStatus, logisticsCompanyName, logisticsCompanyCode }
    const { code } = await doEditLogistics(data)
    if (code === 200) {
        ElMessage({
            message: `${logisticsCompanyStatus === 'OPEN' ? '启用' : '禁用'}成功`,
            type: 'success',
        }) // '批量禁用'
        await initTableDataLogistics()
    }
}
/**
 * 批量操作[删除、启用、禁用]
 * @param {*} Event 触发事件的条件
 */
const handleBatch = async (Event: string) => {
    if (!pendingList.length) return ElMessage.error('请选择需要批量操作的物流公司')
    const isOPEN = pendingList.some((item) => item.logisticsCompanyStatus === 'OPEN')
    switch (Event) {
        case 'disable':
            if (!isOPEN) {
                initTableDataLogistics()
                return ElMessage({
                    message: '存在处于禁用状态的物流公司,无法重复禁用',
                    type: 'error',
                })
            } else {
                const ids = pendingList.map((item) => item.id)
                const { code } = await doForbiddenLogistics('FORBIDDEN', ids)
                if (code === 200) {
                    ElMessage({
                        message: '批量禁用成功',
                        type: 'success',
                    }) // '批量禁用'
                    await initTableDataLogistics()
                }
            }
            break
        case 'del':
            if (isOPEN) {
                initTableDataLogistics()
                return ElMessage({
                    message: '存在处于禁用开启的物流公司,无法删除',
                    type: 'error',
                })
            } else {
                const ids = pendingList.map((item) => item.id)
                const { code } = await doDelLogistics(ids)
                if (code === 200) {
                    ElMessage({
                        message: '批量删除成功',
                        type: 'success',
                    })
                    await initTableDataLogistics()
                }
            }
            break
        case 'open':
            if (isOPEN) {
                ElMessage({
                    message: '存在处于启用状态的物流公司,无法重复启用',
                    type: 'error',
                })
                return initTableDataLogistics()
            } else {
                const ids = pendingList.map((item) => item.id)
                const { code } = await doForbiddenLogistics('OPEN', ids)
                if (code === 200) {
                    ElMessage({
                        message: '批量开启成功',
                        type: 'success',
                    }) // '批量启用'
                    await initTableDataLogistics()
                }
            }
            break
        default:
            break
    }
}
/**
 * 保存选中数据
 */
const handleSelectionChange = (list: Row[]) => {
    pendingList = list
}

const TabCellStyle = () => {
    return { border: 'unset' }
}
/**
 * 分页器
 */
const handleSizeChange = (value: number) => {
    tableDataLogistics.value.current = 1
    tableDataLogistics.value.size = value
    initTableDataLogistics()
}
const handleCurrentChange = (value: number) => {
    tableDataLogistics.value.current = value
    initTableDataLogistics()
}
</script>

<template>
    <div class="handle_container">
        <el-row>
            <el-button type="primary" round @click="handleEditLogistics()">新增物流</el-button>
            <el-button @click="handleBatch('open')">批量启用</el-button>
            <el-button @click="handleBatch('disable')">批量禁用</el-button>
            <el-button @click="handleBatch('del')">批量删除</el-button>
        </el-row>

        <el-popover effect="dark" placement="bottom-end" :show-arrow="false" width="auto">
            <el-image :src="DeliveryOfficialImage" />
            <template #reference>
                <QIcon name="icon-wenhao" size="18px"></QIcon>
            </template>
        </el-popover>
    </div>
    <!-- tab部分s -->
    <div class="table_container">
        <el-table
            :header-row-style="{ fontSize: '14px', fontWeight: 'Bold', color: '#000', height: '48px' }"
            :row-style="{ height: '68px' }"
            :header-cell-style="{ background: '#f6f8fa' }"
            :cell-style="TabCellStyle"
            :data="tableDataLogistics.records"
            style="max-height: calc(100vh - 270px)"
            @selection-change="handleSelectionChange"
        >
            <template #empty><ElTableEmpty /></template>
            <el-table-column type="selection" width="55" />
            <el-table-column prop="date" label="账号名称">
                <template #default="{ row }">
                    <span style="margin-left: 10px">{{ row.logisticsCompanyName }}</span>
                </template>
            </el-table-column>
            <el-table-column prop="name" label="物流公司编码">
                <template #default="{ row }">
                    <span style="margin-left: 10px">{{ row.logisticsCompanyCode }}</span>
                </template>
            </el-table-column>
            <el-table-column prop="address" label="创建时间">
                <template #default="{ row }">
                    <span style="margin-left: 10px">{{ row.createTime }}</span>
                </template>
            </el-table-column>
            <el-table-column prop="name" label="状态">
                <template #default="{ row }">
                    <el-switch
                        v-model="row.logisticsCompanyStatus"
                        size="large"
                        active-value="OPEN"
                        inactive-value="FORBIDDEN"
                        @change="handleSwitchLogistics(row)"
                    />
                </template>
            </el-table-column>
            <el-table-column prop="address" label="操作" align="right">
                <template #default="{ row }">
                    <el-link type="primary" @click="handleEditLogistics(row)">编辑</el-link>
                    <el-link
                        v-if="row.logisticsCompanyStatus === 'FORBIDDEN'"
                        style="margin-left: 20px"
                        type="danger"
                        @click="handleDeleteLogistics(row)"
                        >删除</el-link
                    >
                </template>
            </el-table-column>
        </el-table>
    </div>

    <PageManage
        class="pagination"
        :load-init="true"
        :page-size="tableDataLogistics.size"
        :page-num="tableDataLogistics.current"
        :total="tableDataLogistics.total"
        @reload="initTableDataLogistics"
        @handle-size-change="handleSizeChange"
        @handle-current-change="handleCurrentChange"
    />
    <!-- tab部分e -->
    <!-- 弹窗s -->
    <el-dialog v-model="dialogLogistics" :title="LogisticsId ? '编辑物流' : '新增物流'" width="30%" destroy-on-close>
        <el-form ref="RefLogistics" :model="form" :rules="rules">
            <el-form-item label="物流名称" label-width="120px" prop="logisticsCompanyCode">
                <el-select v-model="form.logisticsCompanyCode" filterable placeholder="请选择物流公司">
                    <el-option v-for="item in initData" :key="item.companyCode" :label="item.companyName" :value="item.companyCode" />
                </el-select>
            </el-form-item>
            <el-form-item label="电子面单编号" label-width="120px" prop="printTempNo">
                <el-input v-model="form.printTempNo" maxlength="40" placeholder="请输入电子面单编号" />
            </el-form-item>
            <el-form-item label="状态" label-width="120px" prop="logisticsCompanyStatus">
                <el-switch v-model="form.logisticsCompanyStatus" active-value="OPEN" inactive-value="FORBIDDEN" />
            </el-form-item>
        </el-form>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="dialogLogistics = false">取消</el-button>
                <el-button type="primary" @click="handleSubmitLogistics">确认</el-button>
            </span>
        </template>
    </el-dialog>
    <!-- 弹窗e -->
</template>

<style lang="scss" scoped>
@include b(table) {
    overflow: auto;
}
@include b(LogisticsLogistics) {
    :deep(.el-table__body) {
        border-collapse: separate;
        border-spacing: 0 6px;
    }
}
@include b(handle_container) {
    display: flex;
    justify-content: space-between;
    align-items: center;
}
</style>
