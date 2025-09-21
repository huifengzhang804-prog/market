<template>
    <el-form ref="formRef" :rules="rules" label-position="right" label-width="90px" :model="logisticsForm" style="max-width: 100%">
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
                        :options="regionData"
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
                    <el-input v-model="logisticsForm.zipCode" placeholder="请输入邮政编号" maxlength="6" onkeyup="value=value.replace(/[^\d]/g,'')" />
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="地址类型">
                    <el-checkbox v-model="logisticsForm.defSend">发货地址</el-checkbox>
                    <el-checkbox v-model="logisticsForm.defReceive">收货地址</el-checkbox>
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
import { FormInstance, FormRules } from 'element-plus'
import { logisticsFormType, newLogisticsFormType } from './types'
import type { PropType } from 'vue'
import { useGDRegionDataStore } from '@/store/modules/GDRegionData'

const regionData = useGDRegionDataStore().getterGDRegionData
const regionCascaderRef = ref()

const rules: FormRules = {
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
}
let logisticsForm = ref<logisticsFormType>({
    id: '',
    address: '',
    contactName: '',
    contactPhone: '',
    zipCode: '',
    Provinces: [],
    defSend: false,
    defReceive: false,
    defSelfShop: false,
    defSelfSupplier: false,
    area: [],
})
const $props = defineProps({
    logistics: {
        type: Object as PropType<newLogisticsFormType>,
        default: () => ({}),
    },
})
const formRef = ref<FormInstance | null>(null)

/**
 * 根据省市区的label[]获取省市区的value[]
 */
const getRegionCode = (regionList: string[]) => {
    const codeList = []
    const province = regionData.find((item) => item.label === regionList[0])
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

const initLogicForm = () => {
    const { area, defSend, defReceive, defSelfShop, defSelfSupplier } = $props.logistics
    Object.keys(logisticsForm.value).forEach((key) => {
        // @ts-ignore
        logisticsForm.value[key] = $props.logistics?.[key] || logisticsForm.value[key]
    })
    if (area && area.length) {
        logisticsForm.value.Provinces = getRegionCode(area)
    }
    logisticsForm.value.defSend = defSend === 'YES'
    logisticsForm.value.defSelfShop = defSelfShop === 'YES'
    logisticsForm.value.defReceive = defReceive === 'YES'
    logisticsForm.value.defSelfSupplier = defSelfSupplier === 'YES'
}

initLogicForm()

const getNewLogisticFormData = () => {
    return new Promise((resolve, reject) => {
        if (formRef.value) {
            formRef.value.validate((isValid, invalidFields) => {
                if (isValid) {
                    const newLogisticsForm: newLogisticsFormType = { ...logisticsForm.value }
                    newLogisticsForm.area = regionCascaderRef.value.getCheckedNodes()[0].pathLabels
                    Reflect.deleteProperty(newLogisticsForm, 'Provinces')
                    newLogisticsForm.defReceive = newLogisticsForm.defReceive ? 'YES' : 'NO'
                    newLogisticsForm.defSend = newLogisticsForm.defSend ? 'YES' : 'NO'
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
