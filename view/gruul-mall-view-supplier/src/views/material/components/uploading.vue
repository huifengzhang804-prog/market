<script lang="ts" setup>
import { FormInstance, FormRules, UploadProps, UploadUserFile, UploadRequestOptions } from 'element-plus'
import useMaterialCategoryList from '../hooks/useMaterialCategoryList'
import QUpload from '@/components/q-upload/q-upload.vue'
import { Plus, Delete, Download, ZoomIn } from '@element-plus/icons-vue'
import { reactive } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'

import handleCategory from './handle-category.vue'

const { materialCategoryList, getMaterialCategoryList, handleAddCategory, showDialog, handleCloseDialog, currentFormModel, handleConfirm } =
    useMaterialCategoryList()
getMaterialCategoryList()
interface IProps {
    formModel: {
        parentId: string
        name: string
        id?: string
    }
}
const formRef = ref<FormInstance | null>(null)
const $props = withDefaults(defineProps<IProps>(), {
    formModel: () => ({
        parentId: '',
        name: '',
        id: ' ',
    }),
})
const $emit = defineEmits(['update:formModel'])
const categoryFormModel = computed({
    get() {
        return $props.formModel
    },
    set(newVal) {
        $emit('update:formModel', newVal)
    },
})
const formFules: FormRules = {
    name: {
        required: true,
        message: '请输入分类名称',
        trigger: 'blur',
    },
    parentId: {
        required: true,
        message: '请输入分类名称',
        trigger: 'blur',
    },
}
const validateFormRules = () => {
    return new Promise((resolve, reject) => {
        if (formRef.value) {
            formRef.value.validate((isValid) => {
                if (isValid) {
                    resolve('validate success')
                } else {
                    reject('validate error')
                }
            })
        } else {
            reject('none form inst')
        }
    })
}
const uploadUrl = import.meta.env.VITE_BASE_URL + 'gruul-mall-carrier-pigeon/oss/upload'
let isCloseElMessage = true
let isCloseElMessages = true
const beforeAvatarUpload: UploadProps['beforeUpload'] = (rawFile) => {
    const rawFileType = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp', 'image/bmp', 'video/mp4']
    if (!rawFileType.includes(rawFile.type)) {
        if (isCloseElMessage) {
            isCloseElMessage = false
            ElMessage.error({ message: '上传素材类型必须是 jpeg、jpg、png、gif、webp、bmp、mp4 其中之一', onClose: () => (isCloseElMessage = true) })
        }
        return false
    } else if (rawFile.size / 1024 / 1024 > 10) {
        if (isCloseElMessages) {
            isCloseElMessages = false
            ElMessage.error({ message: '上传素材大小不能超过 10MB!', onClose: () => (isCloseElMessages = true) })
        }
        return false
    }
    return true
}
const onExceed = () => {
    return ElMessage.error('素材一次性最多上传 10 个')
}
// 裁剪上传
const tailorFn = () => {
    if (fileLists.value.length >= 10 || fileList.value.length >= 10) return ElMessage.error('素材一次性最多上传 10 个')
}
const fileList = ref<UploadUserFile[]>([])
const fileLists = ref<File[]>([])

// 监听文件列表变化
watch(fileList, (newVal) => {
    if (newVal.length >= 10) {
        const uploadElement = document.querySelector('.el-upload.el-upload--picture-card')
        if (uploadElement) {
            uploadElement.classList.add('isNone')
        }
    } else {
        const uploadElement = document.querySelector('.el-upload.el-upload--picture-card')
        if (uploadElement) {
            uploadElement.classList.remove('isNone')
        }
    }
})

const elementUploadRequest = (options: UploadRequestOptions) => {
    const { file } = options
    fileLists.value.push(file)
}
const imageUrl = ref('')

const dialogImageInfo = reactive({
    url: '',
    isMedia: false,
})
const dialogVisible = ref(false)
const handleRemove: UploadProps['onRemove'] = (uploadFile, uploadFiles) => {
    fileList.value = fileList.value.filter((item) => item.uid !== uploadFile.uid)
    if (uploadFile.raw) fileLists.value = fileLists.value.filter((item) => item.uid !== uploadFile.uid)
    else fileLists.value = uploadFiles
}
const handlePictureCardPreview: UploadProps['onPreview'] = (uploadFile) => {
    dialogImageInfo.url = uploadFile.url!
    dialogImageInfo.isMedia = uploadFile?.raw?.type?.startsWith('video')
    // dialogImageUrl.value = uploadFile.url!
    dialogVisible.value = true
}
const changeFn = (file: any) => {
    fileLists.value.push(file)
    fileList.value.push({ name: '', url: URL.createObjectURL(file) })
}
const handleRemoveFile = (file: any) => {
    fileLists.value = fileLists.value.filter((item) => item.uid !== file.uid)
    fileList.value = fileList.value.filter((item) => item.uid !== file.uid)
}
const defaultProps = {
    expandTrigger: 'hover',
    checkStrictly: true,
    emitPath: false,
    children: 'children',
    label: 'name',
    value: 'id',
}
defineExpose({ validateFormRules, categoryFormModel, imageUrl, fileLists })
</script>
<template>
    <el-form ref="formRef" :model="categoryFormModel" :rules="formFules" label-width="95px" label-position="left">
        <el-form-item label="上传至分类" prop="parentId">
            <el-cascader v-model="categoryFormModel.parentId" :options="materialCategoryList" :show-all-levels="false" :props="defaultProps" />
            <el-button type="primary" link style="margin-left: 20px" @click="handleAddCategory">创建分类</el-button>
        </el-form-item>
        <el-form-item label="上传素材" prop="name" style="width: 500px">
            <div class="uploading">
                <!-- <span class="uploading__text">单个不超过 10 MB；支持各格式图片、视频仅支持MP4 格式</span> -->
                <span class="uploading__text">支持素材格式（jpg、gif、jpeg、png、webp、bmp、MP4），每次限10个文件，单个文件 10MB 以内</span>
                <!-- <span>
                    <QUpload v-if="fileLists.length < 10 || fileList.length < 10" :is-auto-upload="false" style="border: 0" @change="changeFn">
                        <el-button type="primary">裁剪上传</el-button>
                    </QUpload>
                </span> -->
            </div>
            <el-upload
                v-model:file-list="fileList"
                :action="uploadUrl"
                :http-request="elementUploadRequest"
                list-type="picture-card"
                :multiple="true"
                :limit="10"
                :on-preview="handlePictureCardPreview"
                :on-remove="handleRemove"
                :before-upload="beforeAvatarUpload"
                :on-exceed="onExceed"
            >
                <el-icon><Plus /></el-icon>
                <template #file="{ file }">
                    <!-- {{ file?.raw?.type?.startsWith('video') }} -->
                    <div v-if="file?.raw?.type?.startsWith('video')">
                        <video :src="file.url || ''" :alt="file.name" muted style="width: 148px; height: 148px" />
                        <span class="el-upload-list__item-actions">
                            <span class="el-upload-list__item-preview" @click="handlePictureCardPreview(file)">
                                <el-icon><zoom-in /></el-icon>
                            </span>
                            <span class="el-upload-list__item-delete" @click="handleRemoveFile(file)">
                                <el-icon><Delete /></el-icon>
                            </span>
                        </span>
                    </div>
                </template>
            </el-upload>
            <el-dialog v-model="dialogVisible">
                <video v-if="dialogImageInfo.isMedia" :src="dialogImageInfo?.url" loop autoplay muted style="width: 600px; height: 600px" />
                <img v-else w-full :src="dialogImageInfo?.url" alt="预览..." style="width: 600px; height: 600px" />
            </el-dialog>
            <el-dialog v-model="showDialog" title="添加分类" :width="650" destroy-on-close @close="handleCloseDialog">
                <handle-category ref="handleCategoryRef" v-model:form-model="currentFormModel" />
                <template #footer>
                    <span class="dialog-footer">
                        <el-button @click="showDialog = false">取消</el-button>
                        <el-button type="primary" @click="handleConfirm"> 确定 </el-button>
                    </span>
                </template>
            </el-dialog>
        </el-form-item>
    </el-form>
</template>

<style lang="scss" scoped>
@include b(uploading) {
    display: flex;
    @include e(text) {
        width: 670px;
        padding-left: 10px;
    }
}
.avatar-uploader {
    width: 150px;
    height: 150px;
    display: block;
    border: 1px dashed var(--el-border-color);
    border-radius: 6px;
}
.avatar-uploader .el-upload {
    border: 1px dashed var(--el-border-color);
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
    border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 150px;
    height: 150px;
    text-align: center;
}
@include b(QUpload1) {
    display: block !important;
    border: 0px !important;
    float: right;
    // margin-right: 80px;
}
:deep(.el-upload-list--picture-card) {
    margin-top: 10px;
    width: 650px;
}
:deep(.isNone) {
    visibility: hidden !important;
}
</style>
