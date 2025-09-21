<script setup lang="ts">
import { doHandleIcOrderError } from '@/apis/order'
import { type HandleErrorForm, HandleErrorStatus } from '@/apis/order/types'

const props = withDefaults(defineProps<{ title?: string; orderNo: string }>(), {
    title: '配送异常处理',
})

const emits = defineEmits(['handled'])
const dialogRef = ref()

const form = reactive<HandleErrorForm>({
    orderNo: props.orderNo,
    status: '',
    desc: '',
})
watch(
    () => props.orderNo,
    (val) => {
        if (val) {
            form.orderNo = val
        } else {
            console.log('请传入订单编号 orderNo')
        }
    },
    {
        immediate: true,
    },
)
const rules = {
    status: [{ required: true, message: '请选择订单状态', trigger: 'change' }],
    desc: [{ required: true, message: '请输入处理说明', trigger: 'blur' }],
}
const statusOptions = ref<{ label: string; value: keyof typeof HandleErrorStatus }[]>([])
statusOptions.value = (Object.keys(HandleErrorStatus) as (keyof typeof HandleErrorStatus)[]).map((key) => ({
    label: HandleErrorStatus[key],
    value: key,
}))
const formRef = ref()
const confirm = () => {
    formRef.value.validate(async (valid: boolean) => {
        if (valid) {
            await doHandleIcOrderError(form)
            cancel()
            emits('handled')
            ElMessage.success('处理成功')
        }
    })
}
const dialogVisible = ref(false)
const cancel = () => {
    dialogVisible.value = false
}
</script>

<template>
    <span>
        <el-button type="danger" link style="margin-left: 10px" @click="dialogVisible = true">异常处理</el-button>
        <el-dialog ref="dialogRef" v-bind="{ ...$attrs, title }" v-model="dialogVisible" append-to-body @close="formRef?.resetFields()">
            <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
                <el-form-item label="订单状态" prop="status">
                    <el-select v-model="form.status" placeholder="请选择订单状态">
                        <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="处理说明" prop="desc">
                    <el-input v-model="form.desc" placeholder="请输入处理说明" maxlength="30" show-word-limit></el-input>
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="cancel">取 消</el-button>
                <el-button type="primary" @click="confirm">确 定</el-button>
            </template>
        </el-dialog>
    </span>
</template>

<style lang="scss" scoped></style>
