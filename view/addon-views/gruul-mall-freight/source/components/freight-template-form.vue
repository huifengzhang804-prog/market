<script lang="ts" setup>
import { ref, reactive, inject, defineProps, watch } from 'vue'
import UnFreeForm from './un-free-form.vue'
import FreeForm from './free-form.vue'
import type { FormInstance, FormRules } from 'element-plus'
import type { FreightTemplateFormSubmitData } from '../index'

const $props = defineProps({ isPKGS: { type: Boolean, required: true }, isEdit: { type: Boolean, required: true } })
/**
 * 是否按件数
 */
const fromRef = ref<FormInstance>()
const freightTemplateForm = inject('parentFreightTemplateForm') as FreightTemplateFormSubmitData
const rules = reactive<FormRules>({
  templateName: [{ required: true, message: '请输入模板名称', trigger: 'blur' }],
  valuationModel: [
    {
      required: true,
      message: '请选择计费方式',
      trigger: 'change',
    },
  ],
})

defineExpose({
  freightTemplateForm,
})
</script>

<template>
  <el-form ref="fromRef" :model="freightTemplateForm" :rules="rules">
    <el-form-item label="模板名称" label-width="80px" prop="templateName">
      <el-row style="width: 100%">
        <el-col :span="12">
          <el-input v-model="freightTemplateForm.templateName" maxlength="25" placeholder="请输入模板名称" style="width: 80%" />
        </el-col>
      </el-row>
    </el-form-item>
    <el-form-item label="计费方式" label-width="80px" prop="valuationModel">
      <el-radio-group v-model="freightTemplateForm.valuationModel">
        <el-radio value="PKGS" size="large">按件数</el-radio>
        <el-radio value="WEIGHT" size="large">按重量</el-radio>
      </el-radio-group>
    </el-form-item>
    <el-form-item>
      <UnFreeForm v-model:tableData="freightTemplateForm.logisticsBaseModelDTO" :isPKGS="$props.isPKGS" />
    </el-form-item>
    <el-form-item label-width="10px">
      <el-checkbox v-model="freightTemplateForm.choiceConditionPostage" :false-value="0" :true-value="1">指定包邮条件</el-checkbox>
    </el-form-item>
    <!-- 包邮区域 s -->
    <el-form-item v-show="freightTemplateForm.choiceConditionPostage">
      <FreeForm
        v-model:tableData="freightTemplateForm.logisticsIncludePostageDTO"
        :canChooseArea="freightTemplateForm.logisticsBaseModelDTO"
        :isEdit="$props.isEdit"
        :isPKGS="$props.isPKGS"
      />
    </el-form-item>
  </el-form>
</template>
<style lang="scss" scoped>
@include b(templateNames) {
  color: #ccc;
}
</style>
