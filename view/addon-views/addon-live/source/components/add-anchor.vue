<script lang="ts" setup>
import { type Ref, ref, reactive } from 'vue'
import { type FormInstance, type FormRules, ElMessage } from 'element-plus'
import { doGetAnchorPhoneList } from '../apis'

const anchorModel = reactive({
  userId: '',
  anchorSynopsis: '',
})
const phoneList = ref([
  {
    avatar: '',
    gender: '',
    mobile: '',
    nickname: '',
    userId: '',
  },
])
const anchorRules: FormRules = {
  userId: [{ required: true, message: '请选择手机号', trigger: 'change' }],
}
const formRef: Ref<FormInstance | null> = ref(null)
const name = ref('')

async function initAnchorList(keywords: string) {
  const { code, data } = await doGetAnchorPhoneList({ keywords })
  if (code !== 200) return ElMessage.error('获取主播电话失败')
  phoneList.value = data.records
}

const remoteMethod = (query: string) => {
  if (query) {
    initAnchorList(query)
  } else {
    phoneList.value = []
  }
}
const getAnchorModel = () => {
  return new Promise((resolve, reject) => {
    if (formRef.value) {
      formRef.value.validate((isValid) => {
        if (isValid) {
          resolve(anchorModel)
        } else {
          reject('valid error')
        }
      })
    } else {
      reject('form instance not found')
    }
  })
}
const selectchange = (val: string) => {
  const currentItem = phoneList.value.filter((item) => {
    return item.userId === val
  })
  name.value = currentItem[0].nickname
}
defineExpose({ getAnchorModel })
</script>

<template>
  <el-form ref="formRef" :model="anchorModel" :rules="anchorRules">
    <el-form-item label="手机号" prop="phone">
      <el-select v-model="anchorModel.userId" :remote-method="remoteMethod" filterable placeholder="请选择手机号" remote @change="selectchange">
        <el-option v-for="item in phoneList" :key="item.userId" :label="item.mobile" :value="item.userId" />
      </el-select>
    </el-form-item>
    <el-form-item label="昵称">{{ name }}</el-form-item>
    <el-form-item label="主播简介" prop="introduce">
      <el-input v-model="anchorModel.anchorSynopsis" placeholder="请输入主播简介" type="textarea" maxlength="30" />
    </el-form-item>
  </el-form>
</template>
