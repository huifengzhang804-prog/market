<script setup lang="ts">
import { ref, reactive } from 'vue'
import PageManage from '@/components/PageManage/index.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { doGetLogisticsList, delLogisticsById, setLogisticsDddress, doLogisticsSetDef } from '@/apis/freight/freightAdd'
import type { ApiAddressList, logisticsFormType, newLogisticsFormType } from './types'
import { useGDRegionDataStore } from '@/store/modules/GDRegionData'

const regionData = useGDRegionDataStore().getterGDRegionData
//TODO:表格剩余高度

let tableData = ref<ApiAddressList>({
    current: 1,
    records: [],
    size: 10,
    total: 0,
})
// tabber状态
const addressDialog = ref(false)
const regionDatas = regionData // 定义地区数据
// addressDialog表单数据
let logisticsForm = ref<logisticsFormType>({
    address: '',
    contactName: '',
    contactPhone: '',
    zipCode: '',
    Provinces: [],
    defSend: false,
    defReceive: false,
})
const RowID = ref('')
const FormRef = ref()
const rules = reactive({
    contactName: [
        { required: true, message: '请输入联系人', trigger: 'blur' },
        { max: 10, message: '最多输入10个字符', trigger: 'blur' },
    ],
    contactPhone: [
        { required: true, message: '请输入联系电话', trigger: 'blur' },
        { pattern: /^1[3|5|6|7|8|9]\d{9}$/, message: '请输入正确的号码格式', trigger: 'blur' },
    ],
    Provinces: [{ type: 'array', required: true, message: '请选择类别', trigger: 'change' }],
    // 验证未写
    address: [{ required: true, message: '请填写详细地址', trigger: 'blur' }],
    // zipCode: [
    //     { required: true, message: '请输入邮政编号', trigger: 'blur' },
    //     { min: 6, max: 6, message: '请输入6位邮政编号', trigger: 'blur' },
    // ],
})

loadGetLogisticsList()

/**
 * @description: 获取物流地址列表
 * @returns {*}
 */
async function loadGetLogisticsList() {
    try {
        const { current, size, total } = tableData.value
        const { data, code } = await doGetLogisticsList({ current, size, total })
        if (code === 200) {
            tableData.value = data
        } else {
            ElMessage({ message: '请刷新重试...', type: 'warning' })
        }
    } catch (e) {
        console.log(e)
    }
}
const handleSizeChange = (value: number) => {
    tableData.value.size = value
    loadGetLogisticsList()
}
const handleCurrentChange = (value: number) => {
    tableData.value.current = value
    loadGetLogisticsList()
}
/**
 * @description: 地址管理编辑
 * @param {*} row
 * @returns {*}
 */
const handleEdit = (row: any) => {
    RowID.value = row.id
    for (const key in logisticsForm.value) {
        if (['defSend', 'defReceive'].includes(key)) {
            ;(logisticsForm.value as any)[key] = row[key] === 'YES'
            ;(logisticsForm.value as any)[key] = row[key] === 'YES'
        } else {
            ;(logisticsForm.value as any)[key] = row[key]
            ;(logisticsForm.value as any)[key] = row[key]
        }
    }
    logisticsForm.value.Provinces = getRegionCode(row.area)
    logisticsForm.value.Provinces = getRegionCode(row.area)
    addressDialog.value = true //打开弹窗
}
/**
 * 根据省市区的label[]获取省市区的value[]
 */
const getRegionCode = (regionList: string[]) => {
    const codeList = []
    const province = regionDatas.find((item) => item.label === regionList[0])
    if (province) {
        codeList.push(province.value)
        if (province.children) {
            const city = province.children.find((it) => it.label === regionList[1])
            if (city) {
                codeList.push(city.value)
                if (city.children) {
                    const region: any = city.children.find((i) => i.label === regionList[2])
                    if (region) {
                        codeList.push(region.value)
                    }
                }
            }
        }
    }
    return codeList
}
const handleDelete = async (id: string) => {
    try {
        await ElMessageBox.confirm('确定删除此项?删除后不会保留已删除地址！', {
            title: '提示',
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
        })
        await loadDelLogisticsById(id) // 调用删除接口
    } catch (e) {
        console.log(e)
    }
}
/**
 * @description: 发货地址处理
 * @param {*} id
 * @returns {*}
 */
const handleShipments = async (id: string) => {
    const { code } = await doLogisticsSetDef(id, 'DEF_SEND')
    if (code === 200) {
        ElMessage.success('设置成功')
        await loadGetLogisticsList() // 重新拉取数据
    } else {
        ElMessage.error({
            message: '设置失败',
        })
    }
}
/**
 * @description: 收货地址
 * @param {*} id
 * @returns {*}
 */
const handleRecede = async (id: string) => {
    const { code } = await doLogisticsSetDef(id, 'DEF_RECEIVE')
    if (code === 200) {
        await loadGetLogisticsList() // 重新拉取数据
        ElMessage.success('设置成功')
    } else {
        ElMessage.error({
            message: '设置失败',
        })
    }
}
/**
 * @description: 表单提交请求
 * @returns {*}
 */
const submitHandle = async () => {
    await FormRef.value.validate()
    await loadSetLogisticsDddress()
}

/**
 * @description: 通过id删除物流
 * @param {*} id
 * @returns {*}
 */
const loadDelLogisticsById = async (id: string) => {
    const { code, msg } = await delLogisticsById(id)
    if (code === 200) {
        ElMessage({
            type: 'success',
            message: '删除成功',
        })
        await loadGetLogisticsList() // 重新拉取数据
    } else {
        ElMessage({
            type: 'warning',
            message: msg,
        })
    }
}
/**
 * @description: 新增或者编辑地址列表
 * @returns {*}
 */
const loadSetLogisticsDddress = async () => {
    const newLogisticsForm: newLogisticsFormType = { ...logisticsForm.value }
    newLogisticsForm.area = regionCascaderRef.value.getCheckedNodes()[0].pathLabels
    Reflect.deleteProperty(newLogisticsForm, 'Provinces')
    newLogisticsForm.defReceive = newLogisticsForm.defReceive ? 'YES' : 'NO'
    newLogisticsForm.defSend = newLogisticsForm.defSend ? 'YES' : 'NO'
    if (RowID.value) {
        const { code } = await setLogisticsDddress({ ...newLogisticsForm, id: RowID.value })
        if (code === 200) {
            ElMessage.success('更新成功')
            await loadGetLogisticsList()
            addressDialog.value = false
        }
    } else {
        const { code } = await setLogisticsDddress({ ...newLogisticsForm })
        if (code === 200) {
            ElMessage.success('增加成功')
            await loadGetLogisticsList()
            addressDialog.value = false
        }
    }
}
//
/**
 * @description: 关闭窗口的回调 移除表单验证 初始化数据
 * @returns {*}
 */
const onClose = () => {
    addressDialog.value = false
    RowID.value = ''
    FormRef.value.resetFields()
    logisticsForm.value = {
        Provinces: [],
        address: '',
        contactName: '',
        contactPhone: '',
        zipCode: '',
        defReceive: false,
        defSend: false,
    }
}
const addressTypeComputed = computed(() => (row: any) => {
    const addressTypeStack = []
    if (row?.defSend === 'YES') addressTypeStack.push('发货地址')
    if (row?.defReceive === 'YES') addressTypeStack.push('收货地址')
    return addressTypeStack
})

const regionCascaderRef = ref()
</script>

<template>
    <div class="freight-add_container overh fdc1">
        <div class="handle_container">
            <el-button type="primary" round @click="addressDialog = true">新增地址</el-button>
        </div>

        <!-- 表格部分 -->
        <div class="freight-add_container__table_border table_container" style="width: 100%">
            <el-table
                stripe
                class="freight-add_container__table"
                :data="tableData.records"
                style="width: 100%"
                :cell-style="{ height: '84px' }"
                :header-cell-style="{
                    backgroundColor: '#F6F8FA',
                    color: '#333',
                    height: '48px',
                }"
            >
                <el-table-column prop="contactName" label="联系人" />
                <el-table-column prop="contactPhone" label="联系电话" />
                <el-table-column prop="address" label="地址" width="380">
                    <template #default="{ row }">
                        <span>{{ row.area.join('') }}{{ row.address }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="zipCode" label="邮政编码" width="120" />
                <el-table-column prop="address" label="地址类型(默认)">
                    <template #default="{ row }">
                        <div v-for="(item, index) in addressTypeComputed(row)" :key="index">
                            {{ item }}
                        </div>
                    </template>
                </el-table-column>
                <el-table-column fixed="right" label="操作" width="120" align="right">
                    <template #default="{ row }">
                        <!-- <el-button v-if="row.defSend !== 'YES'" type="primary" size="small" link @click="handleShipments(row.id)"
                                >默认发货地址</el-button
                            >
                            <el-button v-if="row.defReceive !== 'YES'" type="primary" size="small" link @click="handleRecede(row.id)"
                                >默认收货地址
                            </el-button> -->
                        <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
                        <el-button type="danger" link @click="handleDelete(row.id)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </div>
        <PageManage
            :page-size="tableData.size"
            :page-num="tableData.current"
            :total="tableData.total"
            @handle-size-change="handleSizeChange"
            @handle-current-change="handleCurrentChange"
        ></PageManage>
        <!-- 弹出层 -->
        <el-dialog v-model="addressDialog" width="650px" @close="onClose">
            <template #header="{ titleId, titleClass }">
                <div class="my-header">
                    <h4 :id="titleId" :class="titleClass">{{ RowID ? '编辑地址' : '添加地址' }}</h4>
                </div>
            </template>
            <el-form ref="FormRef" :rules="rules" label-position="right" label-width="90px" :model="logisticsForm" style="max-width: 100%">
                <el-row :gutter="8">
                    <el-col :span="12">
                        <el-form-item label="联系人" prop="contactName">
                            <el-input v-model="logisticsForm.contactName" maxlength="10" placeholder="请输入联系人" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="联系电话" prop="contactPhone">
                            <el-input
                                v-model="logisticsForm.contactPhone"
                                maxlength="11"
                                onkeyup="value=value.replace(/[^\d]/g,'')"
                                placeholder="请输入手机号"
                            />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="地区选择" prop="Provinces">
                            <el-cascader
                                ref="regionCascaderRef"
                                v-model="logisticsForm.Provinces"
                                :options="regionDatas"
                                :style="{ width: '100%' }"
                                filterable
                                placeholder="请选择省/市/区"
                            />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="详细地址" prop="address">
                            <el-input v-model="logisticsForm.address" maxlength="50" placeholder="请输入详细地址" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="邮政编号" prop="zipCode">
                            <el-input
                                v-model="logisticsForm.zipCode"
                                placeholder="请输入邮政编号"
                                maxlength="6"
                                onkeyup="value=value.replace(/[^\d]/g,'')"
                            />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="地址类型">
                            <el-checkbox v-model="logisticsForm.defSend">发货地址</el-checkbox>
                            <el-checkbox v-model="logisticsForm.defReceive">收货地址</el-checkbox>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="addressDialog = false">取消</el-button>
                    <el-button type="primary" @click="submitHandle">提交</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<style lang="scss" scoped>
@include b(freight-add_container) {
    @include e(table_border) {
        border-left: 1px solid #f2f2f2;
        border-right: 1px solid #f2f2f2;
    }
    @include e(table) {
        @include m(loctionbtn) {
            height: 60px;
            @include flex(space-between);
            flex-direction: column;
        }
    }
}
</style>
