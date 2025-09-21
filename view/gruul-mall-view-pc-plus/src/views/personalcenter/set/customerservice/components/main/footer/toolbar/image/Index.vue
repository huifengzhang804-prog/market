<template>
  <el-upload :action="uploadUrl" :http-request="elementUploadRequest" :multiple="false" :on-change="imageChange" :show-file-list="false" auto-upload>
    <QIcon name="icon-tupian2" class="sendOut" size="18px"></QIcon>
  </el-upload>
</template>

<script lang="ts" setup>
import { UploadFile } from 'element-plus'
import { elementUploadRequest } from '@/apis/upload'
import { R } from '@/apis/http.type'

const emits = defineEmits(['imageSelect'])
const uploadUrl = 'gruul-mall-carrier-pigeon/oss/upload'
const imageChange = (file: UploadFile) => {
  if (file.status !== 'success') return
  emits('imageSelect', (file.response as R<string>).data)
}
</script>

<style scoped>
.el-upload {
  height: 30px;
  width: 30px;
}
</style>
