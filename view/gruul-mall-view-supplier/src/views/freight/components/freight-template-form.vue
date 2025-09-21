<script setup lang="ts">
import { ref, reactive, inject } from 'vue'
import UnFreeForm from '@/views/freight/components/un-free-form.vue'
import FreeForm from '@/views/freight/components/free-form.vue'
import type { FormInstance, FormRules } from 'element-plus'
import type { FreightTemplateFormSubmitData } from '@/views/freight/components/types'

const $props = defineProps({ isPKGS: { type: Boolean, required: true }, isEdit: { type: Boolean, required: true } })
/**
 * @description: 是否按件数
 */
const fromRef = ref<FormInstance>()
const freightTemplateForm = inject('parentFreightTemplateForm') as FreightTemplateFormSubmitData
const rules = reactive<FormRules>({
    templateName: [
        { required: true, message: '请输入模板名称', trigger: 'blur' },
        { min: 2, max: 25, message: '请输入2~25个字', trigger: 'blur' },
    ],
    valuationModel: [
        {
            required: true,
            message: '请选择计费方式',
            trigger: 'change',
        },
    ],
})
</script>

<template>
    <el-form ref="fromRef" :model="freightTemplateForm" :rules="rules">
        <el-form-item label="模板名称" label-width="80px" prop="templateName">
            <el-row style="width: 100%">
                <el-col :span="12">
                    <el-input v-model="freightTemplateForm.templateName" maxlength="25" placeholder="请输入模板名称" style="width: 80%" />
                </el-col>
                <el-col :span="6"> <span class="templateNames">模板名称最多25个字</span></el-col>
            </el-row>
        </el-form-item>
        <el-form-item label="计费方式" label-width="80px" prop="valuationModel">
            <el-radio-group v-model="freightTemplateForm.valuationModel">
                <el-radio label="PKGS" size="large">按件数</el-radio>
                <el-radio label="WEIGHT" size="large">按重量</el-radio>
            </el-radio-group>
        </el-form-item>
        <el-form-item>
            <UnFreeForm v-model:table-data="freightTemplateForm.logisticsBaseModelDTO" :is-p-k-g-s="$props.isPKGS" />
        </el-form-item>
        <el-form-item label-width="10px">
            <el-checkbox v-model="freightTemplateForm.choiceConditionPostage" label="指定包邮条件" :true-label="1" :false-label="0"></el-checkbox>
        </el-form-item>
        <!-- 包邮区域 s -->
        <el-form-item v-if="freightTemplateForm.choiceConditionPostage">
            <FreeForm
                v-model:tableData="freightTemplateForm.logisticsIncludePostageDTO"
                :canChooseArea="freightTemplateForm.logisticsBaseModelDTO"
                :isPKGS="$props.isPKGS"
                :isEdit="$props.isEdit"
            />
        </el-form-item>
    </el-form>
</template>
<style scoped lang="scss">
@include b(templateNames) {
    color: #ccc;
}
</style>
