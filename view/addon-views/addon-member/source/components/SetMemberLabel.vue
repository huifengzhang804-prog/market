<script lang="ts" setup>
import { PropType } from 'vue'
import { defineProps, ref, reactive } from 'vue'

const formRef = ref<any>(null)
const setLabelFormModel = reactive({
  id: '',
  name: '',
  fontColor: '',
  labelColor: '',
  priceLabelName: '',
  priceFontColor: '',
  priceLabelColor: '',
})
const $props = defineProps({
  labelInfo: {
    type: Object as PropType<typeof setLabelFormModel>,
    default: () => ({}),
  },
})
const setLabelFormRules = {
  fontColor: { required: true, message: '请选择会员名称字体颜色', trigger: 'change' },
  labelColor: { required: true, message: '请选择标签颜色', trigger: 'change' },
  priceLabelName: { required: true, message: '请输入会员价标签', trigger: 'blur' },
  priceFontColor: { required: true, message: '请选择会员价字体颜色', trigger: 'change' },
  priceLabelColor: { required: true, message: '请选择会员价标签颜色', trigger: 'change' },
}

const initSetLabelFormModel = () => {
  Object.keys(setLabelFormModel).forEach((key) => {
    // @ts-ignore
    setLabelFormModel[key] = $props?.labelInfo?.[key] || setLabelFormModel[key]
  })
}
initSetLabelFormModel()
const getFormModel = () => {
  return new Promise((resolve, reject) => {
    if (formRef.value) {
      formRef.value.validate((isValid, errors) => {
        if (isValid) {
          resolve(setLabelFormModel)
        } else {
          reject(errors)
        }
      })
    } else {
      reject('non form inst')
    }
  })
}

defineExpose({ getFormModel })
</script>

<template>
  <el-form ref="formRef" :model="setLabelFormModel" :rules="setLabelFormRules">
    <div class="title">会员名称标签</div>
    <el-row :gutter="8">
      <el-col :span="24">会员名称：{{ setLabelFormModel.name }}</el-col>
      <el-col :span="12">
        <el-form-item label="会员名称字体" prop="fontColor">
          <el-color-picker v-model="setLabelFormModel.fontColor" />
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="标签颜色" prop="labelColor">
          <el-color-picker v-model="setLabelFormModel.labelColor" />
        </el-form-item>
      </el-col>
    </el-row>
    <div class="title">会员价标签</div>
    <el-row :gutter="8">
      <el-col :span="24">
        <el-form-item label="会员价标签" prop="priceLabelName">
          <el-input v-model="setLabelFormModel.priceLabelName" placeholder="请输入会员价标签" :maxlength="5" />
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="会员价字体" prop="priceFontColor">
          <el-color-picker v-model="setLabelFormModel.priceFontColor" />
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="标签颜色" prop="priceLabelColor">
          <el-color-picker v-model="setLabelFormModel.priceLabelColor" />
        </el-form-item>
      </el-col>
    </el-row>
  </el-form>
</template>

<style scoped>
.title {
  font-weight: bold;
  font-size: 1.2em;
  line-height: 1.8;
}
.el-row {
  padding-left: 15px;
  line-height: 1.6;
}
</style>
