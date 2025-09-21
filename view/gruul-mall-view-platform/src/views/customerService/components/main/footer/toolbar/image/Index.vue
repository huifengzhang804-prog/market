<template>
    <el-upload
        :action="uploadUrl"
        :http-request="elementUploadRequest"
        :multiple="false"
        :on-change="imageChange"
        :on-error="handleError"
        :show-file-list="false"
        auto-upload
    >
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
const handleError = (err: any, file: any, fileList: any) => {
    let errorMessage = err.message || '文件上传失败，请稍后重试'
    if (err.response && err.response.data && err.response.data.message) {
        errorMessage = err.response.data.message
    }
    ElMessage.error(errorMessage)
}
</script>

<style scoped>
.el-upload {
    height: 30px;
    width: 30px;
}
</style>
