<script lang="ts" setup>
import request from '@/apis/request'
import { ElMessage, type UploadInstance, type UploadRequestOptions, genFileId } from 'element-plus'
import type { Ref } from 'vue'

const $emit = defineEmits(['importSuccess'])
const uploadFileApi = (url: string, formData: FormData) => {
    return request.post(url, formData, {
        headers: {
            'Content-Type': 'multipart/form-data',
        },
        timeout: Number(import.meta.env.VITE_REQUEST_TIME_OUT),
    })
}
const uploadExcelRef: Ref<UploadInstance | null> = ref(null)
const handleUploadRequest = async (options: UploadRequestOptions) => {
    const formData = new FormData()
    formData.append('file', options.file)
    const result = await uploadFileApi('gruul-mall-uaa/uaa/user/data/import', formData)
    if (result?.data.code === 200) {
        ElMessage.success({ message: '导入成功' })
        $emit('importSuccess')
    } else {
        ElMessage.error({ message: result?.data.msg || '导入失败' })
        return Promise.reject('导入失败')
    }
}
const handleDownloadDownloadTemplate = async () => {
    // request.get('gruul-mall-uaa/uaa/user/data/downloadExcelTemplate', { responseType: 'arraybuffer' }).then((res) => {
    //     const blob = new Blob([res.data], { type: 'application/vnd.ms-excel;charset=utf-8' })
    //     const elink = document.createElement('a')
    //     elink.href = URL.createObjectURL(blob)
    //     elink.download = '模版-批量导入客户.xls'
    //     elink.click()
    // })
    const eLink = document.createElement('a')
    eLink.href = `${import.meta.env.VITE_BASE_URL}gruul-mall-uaa/public/batch-import-user-template.xls`
    eLink.download = '模版-批量导入客户.xls'
    eLink.click()
}
const onExceed = (files: File[]) => {
    uploadExcelRef.value?.clearFiles()
    const file: any = files[0]
    file.uid = genFileId()
    uploadExcelRef.value?.handleStart(file)
}
defineExpose({ uploadExcelRef })
</script>
<template>
    <el-form>
        <el-form-item label="上传导入文件">
            <el-upload ref="uploadExcelRef" :on-exceed="onExceed" :limit="1" :auto-upload="false" :http-request="handleUploadRequest">
                <el-button type="primary">上传</el-button>
            </el-upload>
        </el-form-item>
        <el-form-item label="下载">
            <el-link type="primary" @click="handleDownloadDownloadTemplate">导入模板</el-link>
        </el-form-item>
        <el-form-item>
            <span class="notice">如导入付费会员请确保已收到购买付费会员的收入，否则影响分账数据与实际金额出现偏差</span>
        </el-form-item>
    </el-form>
</template>

<style scoped>
.notice {
    color: #f00;
}
</style>
