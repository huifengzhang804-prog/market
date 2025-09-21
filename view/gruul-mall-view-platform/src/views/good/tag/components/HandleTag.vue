<template>
    <el-form ref="formRef" :model="formModel" :rules="formRules">
        <el-form-item label="标签名称" prop="name">
            <el-input v-model="formModel.name" placeholder="请输入标签名称" :maxlength="5" />
        </el-form-item>
        <el-row :gutter="8">
            <el-col :span="12">
                <el-form-item label="字体颜色" prop="fontColor">
                    <el-color-picker v-model="formModel.fontColor" />
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="标签背景" prop="backgroundColor">
                    <el-color-picker v-model="formModel.backgroundColor" />
                </el-form-item>
            </el-col>
        </el-row>
        <el-form-item label="商家类型" prop="shopType">
            <el-checkbox-group v-model="formModel.shopType">
                <el-checkbox label="SELF_OWNED">自营</el-checkbox>
                <el-checkbox label="PREFERRED">优选</el-checkbox>
                <el-checkbox label="ORDINARY">普通</el-checkbox>
            </el-checkbox-group>
        </el-form-item>
    </el-form>
</template>

<script lang="ts" setup>
import { FormInstance, FormRules } from 'element-plus'

const $props = defineProps({
    tagInfo: {
        type: Object,
        default: () => ({}),
    },
})
const formModel = reactive({
    id: '',
    name: '',
    fontColor: '',
    backgroundColor: '',
    shopType: [],
})
const formRef = ref<FormInstance | null>(null)
onMounted(() => {
    Object.keys(formModel).forEach((key) => {
        if ($props.tagInfo?.[key]) {
            // @ts-ignore
            formModel[key] = $props.tagInfo[key]
        }
    })
})
const formRules: FormRules = {
    name: { required: true, message: '请输入标签名称', trigger: 'blur' },
    fontColor: { required: true, message: '请选择字体颜色', trigger: 'change' },
    backgroundColor: { required: true, message: '请选择标签背景', trigger: 'change' },
    shopType: { required: true, message: '请选择商家类型', type: 'array', trigger: 'change' },
}
const getFormModel = () => {
    return new Promise((resolve, reject) => {
        if (formRef.value) {
            formRef.value.validate((isValid, invalidFields) => {
                if (isValid) {
                    resolve(formModel)
                } else {
                    reject(invalidFields)
                }
            })
        } else {
            reject('non form inst')
        }
    })
}
defineExpose({ getFormModel })
</script>
