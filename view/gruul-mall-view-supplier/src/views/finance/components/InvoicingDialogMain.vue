<script lang="ts" setup>
import { elementUploadRequest } from '@/apis/upload'
import { ElMessage, UploadProps } from 'element-plus'

const { divTenThousand } = useConvert()
const props = defineProps({
    invoiceDetail: {
        type: Object,
        default: () => ({}),
    },
})

const uploadUrl = import.meta.env.VITE_BASE_URL + 'gruul-mall-carrier-pigeon/oss/upload'
const pdfList = ref<string[]>([])
const $emit = defineEmits(['handlePdfListChange'])

const beforePdfUpload: UploadProps['beforeUpload'] = (rawfile) => {
    const whiteList = ['application/pdf']
    const isLt = rawfile.size < 3 * 1024 * 1024
    if (!whiteList.includes(rawfile.type)) {
        ElMessage.error('只能上传pdf文件！')
        return false
    }
    if (!isLt) {
        ElMessage.error('上传视频大小不超过3M!')
        return false
    }
    return true
}

const uploadPdfSuccess: UploadProps['onSuccess'] = (response, uploadFile) => {
    pdfList.value.push(response.data)
    $emit('handlePdfListChange', pdfList.value)
}
const openPDF = (item: string) => {
    window.open(item)
}
const delImgHandle = (index: number) => {
    pdfList.value.splice(index, 1)
    $emit('handlePdfListChange', pdfList.value)
}
</script>

<template>
    <div class="invoice-dialog" style="padding: 0 30px">
        <div class="invoice-dialog-main">
            <div class="invoice-dialog-main--name">
                发票抬头:<span>{{ props.invoiceDetail.header }}</span>
            </div>
            <div>
                开票金额:
                <span class="invoice-dialog-main--price">{{ divTenThousand(props.invoiceDetail.invoiceAmount).toFixed(2) }}</span>
            </div>
            <div class="invoice-dialog-main--number">
                税号：
                <span>{{ props.invoiceDetail.taxIdentNo }}</span>
            </div>
        </div>
        <div style="display: flex; margin-top: 20px">
            发票：
            <div style="margin-left: 20px">
                <div>
                    仅支持pdf格式的发票文件最多上传 <span style="color: #fd0505">5</span> 个(单个文件3MB以内)，确定后系统自动将发票发送至客户邮箱
                </div>
                <div style="display: flex; margin-top: 10px">
                    <div
                        v-for="(item, index) in pdfList"
                        :key="item"
                        style="position: relative; margin-right: 20px; width: 100px"
                        @click="openPDF(item)"
                    >
                        <q-icon color="#5b6982" name="icon-ziyuan1" size="100px"></q-icon>
                        <el-icon
                            v-if="item"
                            color="#7f7f7f"
                            size="20px"
                            style="position: absolute; right: 5px; top: -5px; background: #fff; border-radius: 50%"
                            @click.stop="delImgHandle(index)"
                            ><i-ep-circle-close
                        /></el-icon>
                    </div>
                    <el-upload
                        v-if="pdfList.length < 5"
                        :action="uploadUrl"
                        :before-upload="beforePdfUpload"
                        :http-request="elementUploadRequest"
                        :on-success="uploadPdfSuccess"
                        :show-file-list="false"
                        class="avatar-uploader"
                    >
                        <el-icon class="avatar-uploader-icon"><i-ep-plus /></el-icon>
                    </el-upload>
                </div>
            </div>
        </div>
    </div>
</template>

<style lang="scss" scoped>
.invoice-dialog {
    color: #000;
}
@include b(invoice-dialog-main) {
    @include flex;
    justify-content: space-between;
    @include m(price) {
        color: rgba(253, 5, 5, 1);
        font-size: 14px;
        font-weight: 700;
        &::before {
            content: '￥';
            font-size: 10px;
            font-weight: normal;
        }
    }
}
.avatar-uploader {
    width: 100px;
    height: 100px;
    border-radius: 10px;
    border: 1px solid #bbbbbb;
    cursor: pointer;
}
.avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 100px;
    height: 100px;
    text-align: center;
}
</style>
