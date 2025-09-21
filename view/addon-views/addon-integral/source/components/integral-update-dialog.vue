<script lang="ts" setup>
import { reactive, ref, defineProps, defineEmits, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { doPostIntegralChange, doGetIntegralTotal } from '../apis'

const emit = defineEmits(['reset'])
const props = defineProps({
  showDialog: {
    type: Boolean,
    default: false,
  },
  userId: {
    type: [String, Number],
    default: '',
  },
})
const integralTotal = ref(0)
const ruleFormRef = ref()
const modelFormData = reactive({
  num: 0,
  changeType: 'INCREASE' as 'INCREASE' | 'REDUCE',
})
const rules = reactive({
  changeType: [{ required: true, message: '请选择正确的操作类型' }],
  num: [
    { required: true, message: '请输入正确的数额' },
    { type: 'number', message: '请输入一个正确的数字', min: 1 },
  ],
})
watch(
  () => props.showDialog,
  (value) => {
    value && initUserInfo()
  },
  { immediate: true },
)

async function initUserInfo() {
  const { code, data, msg } = await doGetIntegralTotal(props.userId)
  if (code !== 200) {
    return ElMessage.error(msg || '获取用户积分失败')
  }
  integralTotal.value = data
}

const handleSubmit = async () => {
  if (!ruleFormRef.value) return
  const isValidate = await ruleFormRef.value.validate()
  if (!isValidate) return
  const { code } = await doPostIntegralChange(props.userId, modelFormData.num, modelFormData.changeType)
  if (code !== 200) return ElMessage.error('操作失败')
  ElMessage.success('操作成功')
  integralTotal.value = 0
  modelFormData.num = 0
  modelFormData.changeType = 'INCREASE'
  emit('reset')
}
const handleClose = () => {
  modelFormData.num = 0
  modelFormData.changeType = 'INCREASE'
  emit('reset')
}
</script>

<template>
  <el-dialog :model-value="props.showDialog" append-to-body class="label-view-dialog" title="积分调整" width="40%" @close="handleClose">
    <el-form ref="ruleFormRef" :model="modelFormData" :rules="rules" label-width="90px">
      <el-form-item label="当前数值">
        <div>{{ integralTotal }}</div>
      </el-form-item>
      <el-form-item label="操作" prop="changeType">
        <el-radio-group v-model="modelFormData.changeType" class="ml-4">
          <el-radio value="INCREASE" size="large">充值</el-radio>
          <el-radio value="REDUCE" size="large">扣除</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="调整数值" prop="num">
        <el-input-number v-model="modelFormData.num" :controls="false" :min="0" />
        <span>请输入正整数</span>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit">确认</el-button>
    </template>
  </el-dialog>
</template>

<style lang="scss" scoped></style>
<style></style>
