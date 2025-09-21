<template>
    <el-form ref="formRef" :model="formModel" :rules="formRules">
        <el-form-item prop="status" label="审核状态">
            <el-radio-group v-model="formModel.status" @change="handleChangeStatus">
                <el-radio value="SELL_ON">通过</el-radio>
                <el-radio value="REFUSE">拒绝</el-radio>
            </el-radio-group>
        </el-form-item>
        <el-form-item v-if="formModel.status === 'REFUSE'" label="原因拒绝" prop="explain">
            <el-input v-model="formModel.explain" placeholder="20字以内" :maxlength="20" show-word-limit />
        </el-form-item>
    </el-form>
</template>

<script lang="ts" setup>
import { FormInstance, FormRules } from 'element-plus'

const formModel = reactive({
    status: 'SELL_ON',
    explain: '',
})
const handleChangeStatus = (changedStatus: any) => {
    if (changedStatus === 'SELL_ON') {
        formModel.explain = ''
    }
}
const formRules: FormRules = {
    explain: {
        required: true,
        message: '请输入原因',
        trigger: 'blur',
    },
    status: {
        required: true,
        message: '请选择审核状态',
        trigger: 'change',
    },
}
const formRef = ref<FormInstance | null>(null)
const validateForm = () => {
    return new Promise((resolve, reject) => {
        if (formRef.value) {
            formRef.value.validate((isValid) => {
                if (isValid) {
                    resolve(formModel)
                } else {
                    reject('valid fail')
                }
            })
        } else {
            reject('none form inst')
        }
    })
}

defineExpose({ validateForm })
</script>
