<script setup lang="ts">
import { shopManualPrintOrder } from '@/apis/order'
import { PrintTaskLink } from '@/apis/order/types'

const props = withDefaults(
    defineProps<{
        title?: string
        orderNo: string
        linkOptions: (keyof typeof PrintTaskLink)[]
    }>(),
    {
        title: '打印小票',
        linkOptions: () => [],
    },
)

const emits = defineEmits(['handled'])
const dialogRef = ref()

const form = reactive<{
    orderNo: string
    link: keyof typeof PrintTaskLink
}>({
    orderNo: props.orderNo,
    link: '' as keyof typeof PrintTaskLink,
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
    link: [{ required: true, message: '请选择打印类型', trigger: 'change' }],
}
const linkOptions = computed(() => {
    return props.linkOptions.map((key) => {
        return {
            label: PrintTaskLink[key],
            value: key,
        }
    })
})

const formRef = ref()
const confirm = () => {
    formRef.value.validate(async (valid: boolean) => {
        if (valid) {
            const { code } = await shopManualPrintOrder(form)
            if (code === 200) {
                cancel()
                emits('handled')
                ElMessage.success('打印成功')
            }
        }
    })
}
const dialogVisible = ref(false)
const cancel = () => {
    dialogVisible.value = false
}
</script>

<template>
    <el-button type="danger" link style="margin-left: 10px" @click="dialogVisible = true">打印小票</el-button>
    <el-dialog ref="dialogRef" v-bind="{ ...$attrs, title }" v-model="dialogVisible" append-to-body width="600px" @close="formRef?.resetFields()">
        <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
            <el-form-item label="打印类型" prop="link">
                <el-select v-model="form.link" placeholder="请选择打印类型">
                    <el-option v-for="item in linkOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
                </el-select>
            </el-form-item>
        </el-form>
        <template #footer>
            <el-button @click="cancel">取 消</el-button>
            <el-button type="primary" @click="confirm">确 定</el-button>
        </template>
    </el-dialog>
</template>

<style lang="scss" scoped></style>
