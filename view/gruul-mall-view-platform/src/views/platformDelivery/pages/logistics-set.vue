<script setup lang="ts">
import { ElMessage } from 'element-plus'
import { doGetCourierInfo, doCourierUpdateAndEdit } from '@/apis/set/platformDelivery'
import DeliveryOfficialKey from '@/assets/image/delivery_key.png'

interface PrintSetForm {
    id: string
    customer: string
    key: string
    secret: string
}
const rules = reactive({
    customer: [{ required: true, message: '请输入客户号', trigger: 'blur' }],
    key: [{ required: true, message: '请输入快递100 key', trigger: 'blur' }],
    secret: [{ required: true, message: '请输入快递100 secret', trigger: 'blur' }],
})
let printSetForm = reactive<PrintSetForm>({ key: '', customer: '', secret: '', id: '' })
// 快递设置信息获取
const courierInfo = async () => {
    const { code, data, msg } = await doGetCourierInfo()
    if (code === 200) {
        Object.keys(printSetForm).forEach(
            // @ts-ignore
            (key) => (printSetForm[key] = data?.[key]),
        )
    } else ElMessage.error(msg || '快递设置信息获取失败')
}
courierInfo()
const printSetFormRef = ref()
// 保存按钮
const handleSubmit = async () => {
    try {
        await printSetFormRef.value.validate()

        const { code, msg } = await doCourierUpdateAndEdit(printSetForm)
        if (code !== 200) return ElMessage.error(printSetForm.id ? `${msg || '快递设置更新失败'}` : `${msg || '快递设置新增失败'}`)
        ElMessage.success(printSetForm.id ? '快递设置更新成功' : '快递设置新增成功')
        courierInfo()
    } catch (error: any) {
        const errorArr = []
        for (const key in error) {
            errorArr.push(error[key][0].message)
        }
        ElMessage.error({
            message: errorArr.join(' '),
            onClose: () => {
                printSetFormRef.value.resetFields()
            },
        })
    }
}
</script>
<template>
    <div style="padding-top: 15px">
        <el-form ref="printSetFormRef" :model="printSetForm" label-width="70px" label-position="left" :rules="rules" style="padding: 0 0 0 1.5%">
            <el-form-item label="客户号" prop="customer">
                <el-input
                    v-model.trim="printSetForm.customer"
                    placeholder="请输入"
                    style="width: 350px; margin-right: 10px"
                    maxlength="50"
                ></el-input>
                <el-popover effect="dark" placement="bottom" :show-arrow="false" width="1150">
                    <el-image :src="DeliveryOfficialKey" />
                    <template #reference>
                        <QIcon name="icon-wenhao" color="#999"></QIcon>
                    </template>
                </el-popover>
            </el-form-item>
            <el-form-item label="Key" prop="key">
                <el-input v-model.trim="printSetForm.key" placeholder="请输入" style="width: 350px" maxlength="50"></el-input>
            </el-form-item>
            <el-form-item label="Secret" prop="secret">
                <el-input v-model.trim="printSetForm.secret" placeholder="请输入" style="width: 350px" maxlength="50"></el-input>
            </el-form-item>
        </el-form>
    </div>
    <div class="preservation">
        <el-button type="primary" @click="handleSubmit"> 保存 </el-button>
    </div>
</template>

<style scoped lang="scss">
@include b(title) {
    height: 50px;
    line-height: 50px;
    font-size: 14px;
    font-weight: bold;
    background-color: #f0f0f0;
    padding-left: 30px;
    margin-bottom: 23px;
}
@include b(shopTil) {
    font-size: 14px;
    font-weight: bold;
    padding-left: 262px;
    margin-bottom: 10px;
}
@include b(preservation) {
    position: absolute;
    bottom: 0;
    width: 100%;
    display: flex;
    justify-content: center;
    height: 52px;
    align-items: center;
    box-shadow: 0 -3px 8px rgba(0, 0, 0, 0.1);
}
</style>
