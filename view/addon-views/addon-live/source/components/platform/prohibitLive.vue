<script lang="ts" setup>
import { ref, reactive } from 'vue'
import { type FormInstance, type FormRules, UploadProps } from 'element-plus'
import QUpload from '@/components/q-upload/q-upload.vue'
import { CircleClose } from '@element-plus/icons-vue'

const prohibitModel = reactive({
  categoryTypes: [],
  reason: '',
  relevantEvidence: '',
})
const ImgList = ref<string[]>([])
const prohibitRules: FormRules = {
  categoryTypes: [{ required: true, message: '请选择类型', trigger: 'change' }],
  reason: [{ required: true, message: '请输入原因', trigger: 'change' }],
  relevantEvidence: [{ required: true, message: '请选择证据', trigger: 'change' }],
}
const formRef = ref<FormInstance | null>(null)

const getprohibitModel = () => {
  return new Promise((resolve, reject) => {
    if (formRef.value) {
      formRef.value.validate((isValid) => {
        if (isValid) {
          resolve(prohibitModel)
        } else {
          reject('valid error')
        }
      })
    } else {
      reject('form instance not found')
    }
  })
}
/**
 * 删除图片
 */
const delImgHandle = (index: number) => {
  ImgList.value.splice(index, 1)
  prohibitModel.relevantEvidence = ImgList.value.join(',')
}
const addNewMainSuccess: UploadProps['onSuccess'] = (response, uploadFile) => {
  ImgList.value.push(response)
  prohibitModel.relevantEvidence = ImgList.value.join(',')
}
defineExpose({ getprohibitModel })
</script>

<template>
  <el-form ref="formRef" :model="prohibitModel" :rules="prohibitRules">
    <el-form-item label="类型(多选)" prop="categoryTypes">
      <el-checkbox-group v-model="prohibitModel.categoryTypes">
        <el-checkbox label="YELLOW_INVOLVEMENT">涉黄</el-checkbox>
        <el-checkbox label="DRUG_RELATED">涉毒</el-checkbox>
        <el-checkbox label="SENSITIVE_TOPIC">敏感话题</el-checkbox>
        <el-checkbox label="OTHER">其它</el-checkbox>
      </el-checkbox-group>
    </el-form-item>
    <el-form-item label="原因" prop="reason">
      <el-input v-model="prohibitModel.reason" placeholder="20字以内" />
    </el-form-item>
    <el-form-item label="相关证据（最多5张图片）" label-width="120px" prop="relevantEvidence">
      <div v-for="(item, index) in ImgList" :key="item" style="position: relative; margin-right: 20px">
        <q-upload v-model:src="ImgList[index]" :format="{ size: 1 }" :height="100" :width="100" />
        <el-icon
          v-if="item"
          color="#7f7f7f"
          size="20px"
          style="position: absolute; right: -5px; top: -5px; background: #fff; border-radius: 50%"
          @click="delImgHandle(index)"
        >
          <circle-close />
        </el-icon>
      </div>
      <q-upload v-show="ImgList.length <= 4" :format="{ size: 1 }" :height="100" :width="100" @change="addNewMainSuccess" />
    </el-form-item>
  </el-form>
</template>
