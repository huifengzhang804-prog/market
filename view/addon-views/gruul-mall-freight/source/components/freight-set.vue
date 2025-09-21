<script lang="ts" setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { doCourierUpdateAndEdit, doGetCourierInfo } from '../apis'
import DeliveryOfficialKey from '@/assets/images/delivery_key.png'
import QIcon from '@/components/q-icon/q-icon.vue'

const printSetFormRef = ref()
const id = ref()
const rules = reactive({
  key: [{ required: true, message: '请输入快递100 key', trigger: 'blur' }],
  customer: [{ required: true, message: '请输入客户号', trigger: 'blur' }],
  secret: [{ required: true, message: '请输入快递100 secret', trigger: 'blur' }],
})
const printSetForm = reactive({ key: '', customer: '', secret: '' })

initCourierInfo()

async function initCourierInfo() {
  const { code, data } = await doGetCourierInfo()
  if (code !== 200) return
  if (data && data.id) {
    id.value = data.id
    printSetForm.customer = data.customer
    printSetForm.key = data.key
    printSetForm.secret = data.secret
  }
}

const handleSubmit = async () => {
  try {
    await printSetFormRef.value.validate()
    const { key, customer, secret } = printSetForm
    const { code } = id.value ? await doCourierUpdateAndEdit(customer, key, secret, id.value) : await doCourierUpdateAndEdit(customer, key, secret)
    if (code !== 200) return ElMessage.error(id.value ? '快递设置更新失败' : '快递设置新增失败')
    ElMessage.success(id.value ? '快递设置更新成功' : '快递设置新增成功')
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
  <div class="handle_container">
    <el-form ref="printSetFormRef" :model="printSetForm" :rules="rules" :show-message="false" label-position="left" label-width="70px">
      <el-form-item label="客户号" prop="customer">
        <el-input v-model.trim="printSetForm.customer" maxlength="50" style="width: 350px; margin-right: 10px"></el-input>
        <el-popover effect="dark" placement="bottom" :show-arrow="false" width="1150">
          <el-image :src="DeliveryOfficialKey" />
          <template #reference>
            <QIcon name="icon-wenhao" color="#999"></QIcon>
          </template>
        </el-popover>
      </el-form-item>
      <el-form-item label="Key" prop="key">
        <el-input v-model.trim="printSetForm.key" maxlength="50" style="width: 350px"></el-input>
      </el-form-item>
      <el-form-item label="Secret" prop="secret">
        <el-input v-model.trim="printSetForm.secret" maxlength="50" style="width: 350px"></el-input>
      </el-form-item>
    </el-form>
  </div>
  <div class="btn_container">
    <el-button type="primary" @click="handleSubmit">保存</el-button>
  </div>
</template>

<style lang="scss" scoped>
.btn_container {
  width: 100%;
  text-align: center;
  padding: 15px;
  margin-top: auto;
  box-shadow: 0 -3px 8px rgba(0, 0, 0, 0.1);
}
</style>
