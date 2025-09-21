<template>
    <el-form ref="formRef" label-position="right" label-width="130px" :model="logisticsForm" style="max-width: 100%" :rules="rules">
        <el-row :gutter="8">
            <el-col :span="12">
                <el-form-item label="选择快递公司" prop="freightId">
                    <el-select v-model="logisticsForm.freightId" placeholder="请选择快递公司" style="width: 90%">
                        <el-option v-for="item in companySelectList" :key="item.id" :label="item.logisticsCompanyName" :value="item.id" />
                    </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="选择打印机" prop="logisticsPrintId">
                    <el-select v-model="logisticsForm.logisticsPrintId" placeholder="请选择打印机" style="width: 100%">
                        <el-option v-for="item in printList" :key="item.id" :label="item.printName" :value="item.id" />
                    </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="12">
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
            </el-col>
            <el-col :span="12">
                <el-form-item label="密码" prop="customerPassword">
                    <el-input v-model.trim="logisticsForm.customerPassword" placeholder="请输入密码" />
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="网点名称" prop="networkName">
                    <el-input v-model.trim="logisticsForm.networkName" maxlength="20" placeholder="请输入网点名称" style="width: 90%" />
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="网点编号" prop="networkCode">
                    <el-input v-model.trim="logisticsForm.networkCode" maxlength="20" placeholder="请输入网点编号" />
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="应用商家">
                    <el-checkbox v-model="logisticsForm.defSelfShop">自营商家</el-checkbox>
                    <el-checkbox v-model="logisticsForm.defSelfSupplier">自营供应商</el-checkbox>
                </el-form-item>
            </el-col>
        </el-row>
    </el-form>
</template>

<script lang="ts" setup>
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import { ApiLogisticCompany, ApiPrint } from './types'
import { doGetPrintList } from '@/apis/freight/freight-print'
import { doGetLogisticsCompany } from '@/apis/freight'

const $props = defineProps({
    service: {
        type: Object,
        default: () => ({}),
    },
})
const formRef = ref<FormInstance | null>(null)
const logisticsForm = reactive({
    id: '',
    freightId: '',
    addressId: '',
    customerCode: '',
    customerPassword: '',
    networkName: '',
    networkCode: '',
    logisticsPrintId: '',
    defSelfShop: false,
    defSelfSupplier: false,
})

const rules: FormRules = {
    freightId: [{ required: true, message: '请选择快递公司', trigger: 'change' }],
    logisticsPrintId: [{ required: true, message: '请选择打印机', trigger: 'change' }],
    customerCode: [{ required: true, message: '未填客户号', trigger: 'blur' }],
    customerPassword: [{ required: true, message: '请输入密码', trigger: 'blur' }],
    networkName: [{ required: true, message: '请输入网点名称', trigger: 'blur' }],
}
const pageConfigPrint = reactive({
    pageSize: 20,
    pageNum: 1,
    total: 0,
})
//物流公司列表
const companySelectList = ref<ApiLogisticCompany[]>([])
// 打印机列表信息
const printList = ref<ApiPrint[]>([])

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
/**
 * 获取物流公司
 */
async function initLogisticsCompany() {
    const { code, data, success } = await doGetLogisticsCompany()
    if (code === 200 && success) {
        companySelectList.value = data.records
    } else {
        ElMessage.error('获取物流公司失败')
    }
}

const initLogicForm = () => {
    const { defSelfShop, defSelfSupplier } = $props.service
    Object.keys(logisticsForm).forEach((key) => {
        // @ts-ignore
        logisticsForm[key] = $props.service?.[key] || logisticsForm[key]
    })
    logisticsForm.defSelfShop = defSelfShop === 'YES'
    logisticsForm.defSelfSupplier = defSelfSupplier === 'YES'
}
initLogicForm()
initPrintList()
initLogisticsCompany()

const getNewLogisticFormData = () => {
    return new Promise((resolve, reject) => {
        if (formRef.value) {
            formRef.value.validate((isValid, invalidFields) => {
                if (isValid) {
                    const newLogisticsForm: any = { ...logisticsForm }
                    newLogisticsForm.defSelfShop = newLogisticsForm.defSelfShop ? 'YES' : 'NO'
                    newLogisticsForm.defSelfSupplier = newLogisticsForm.defSelfSupplier ? 'YES' : 'NO'
                    resolve(newLogisticsForm)
                } else {
                    reject(invalidFields)
                }
            })
        } else {
            reject('none form inst')
        }
    })
}

defineExpose({ getNewLogisticFormData })
</script>
