<script lang="ts" setup>
import type { PropType } from 'vue'
import cropper from './cropper.vue'
import { ElMessage } from 'element-plus'
import { useVModel } from '@vueuse/core'
import { verifyFormat, httpRequest } from '@/components/q-upload/types/Upload'
import type { UploadConfig, CusUploadRawFile, IMAGETYPE } from '@/components/q-upload/types/Upload'
import uuid from '@/utils/uuid'

const $props = defineProps({
    uploadUrl: {
        type: String,
        default: 'gruul-mall-carrier-pigeon/oss/upload',
    },
    src: {
        type: String,
        default: '',
    },
    format: {
        type: Object as PropType<UploadConfig>,
        default() {
            return {
                width: 10000,
                height: 10000,
                types: ['image/png', 'image/jpg', 'image/gif', 'image/jpeg', 'image/webp', 'image/bmp'],
                size: 10,
            }
        },
    },
    width: {
        type: Number,
        default: 60,
    },
    height: {
        type: Number,
        default: 60,
    },
    isAutoUpload: {
        type: Boolean,
        default: true,
    },
    // 是否需要裁剪
    isCropper: {
        type: Boolean,
        default: true,
    },
    disabled: {
        type: Boolean,
        default: false,
    },
    tip: {
        type: String,
        default: '',
    },
    categoryAdvertising: {
        type: Boolean,
        default: false,
    },
})
const $emit = defineEmits(['update:src', 'change', 'beforeUpdate'])
const imgSrc = useVModel($props, 'src', $emit)
const uploadRef = ref()
const cropperSrc = ref('')
const isShowCropper = ref(false)
// 暂存cropper的file
const tempCropperFile = ref()

const fileType = ref<keyof typeof IMAGETYPE>('image/png')

const handleChangeUpload = async (file: CusUploadRawFile, fileList: CusUploadRawFile[]) => {
    if (file.status !== 'ready') return
    const { src, success, type } = await verifyFormat(file, $props.format)
    if (success && src) {
        if ($props.isCropper) {
            cropperSrc.value = src
            isShowCropper.value = true
            if (fileList.length > 1) {
                fileList.splice(0, fileList.length - 1)
            }
        } else {
            // @ts-ignore
            const base64: string = await fileToBase64(file.raw)
            const files = dataURLtoFile(base64, uuid(10))
            // @ts-ignore
            handleCropperLoad(files)
        }
        fileType.value = type
        // if (fileList.length > 5) {
        //     ElMessage.error('图片最多只能上传5张')
        // }
    } else {
        uploadRef.value.handleRemove(file)
    }
}
/**
 * cropper组件裁剪完成回调
 * @param {string} uploadStr
 */
const handleCropperLoad = (uploadStr: string) => {
    tempCropperFile.value = uploadStr
    uploadRef.value.submit()
}
const handleUploadRequest = async (request: any) => {
    try {
        $emit('beforeUpdate', request.file)
        if ($props.isAutoUpload) {
            let imgStr = await httpRequest(request, tempCropperFile.value)
            imgSrc.value = imgStr
            $emit('change', imgStr)
        } else {
            $emit('change', tempCropperFile.value)
        }
    } catch (e) {
        ElMessage.error('图片上传失败')
    }
}

function fileToBase64(file: any) {
    return new Promise((resolve) => {
        //file转bse64
        let reader = new FileReader()
        reader.readAsDataURL(file)
        reader.onload = function (e) {
            resolve(e.target?.result)
        }
    })
}

function dataURLtoFile(dataurl: string, filename: string) {
    let arr = dataurl.split(',')
    let mime = arr[0].match(/:(.*?);/)![1]
    let suffix = mime.split('/')[1]
    let bstr = window.atob(arr[1])
    let n = bstr.length
    let u8arr = new Uint8Array(n)
    while (n--) {
        u8arr[n] = bstr.charCodeAt(n)
    }
    return new File([u8arr], `${filename}.${suffix}`, {
        type: mime,
    })
    //将base64转换为文件
}
const handleError = (err: any, file: any, fileList: any) => {
    let errorMessage = err.message || '文件上传失败，请稍后重试'
    if (err.response && err.response.data && err.response.data.message) {
        errorMessage = err.response.data.message
    }
    ElMessage.error(errorMessage)
}
// defineExpose({ handleChangeUpload })
</script>
<template>
    <div class="qupload">
        <el-upload
            ref="uploadRef"
            :disabled="$props.disabled"
            :action="$props.uploadUrl"
            :auto-upload="false"
            :http-request="handleUploadRequest"
            :on-change="(uploadFile, uploadFiles) => handleChangeUpload(uploadFile as CusUploadRawFile, uploadFiles as CusUploadRawFile[])"
            :on-error="handleError"
            :show-file-list="false"
            class="avatar-uploader"
        >
            <slot>
                <van-image
                    v-if="categoryAdvertising"
                    :src="imgSrc"
                    :style="{ width: `${$props.width}px`, height: `${$props.height}px`, objectFit: 'contain' }"
                />
                <img
                    v-else-if="imgSrc && !categoryAdvertising"
                    :src="imgSrc"
                    :style="{ width: `${$props.width}px`, height: `${$props.height}px`, objectFit: 'contain' }"
                />
                <el-icon v-else :style="{ width: `${$props.width}px`, height: `${$props.height}px` }" class="avatar-uploader-icon"
                    ><i-ep-plus
                /></el-icon>
            </slot>
            <slot name="mask"></slot>
            <template #tip>
                <div v-if="$props.tip" class="el-upload__tip" style="margin-top: 0; min-width: 183px">{{ $props.tip }}</div>
            </template>
        </el-upload>
        <cropper
            v-if="isShowCropper"
            v-model:cropper-show="isShowCropper"
            :cropperSrc="cropperSrc"
            :type="fileType"
            @upload-img="handleCropperLoad"
        />
    </div>
</template>

<style lang="scss" scoped>
.qupload {
    :deep(.avatar-uploader) {
        .el-upload {
            border: 1px dashed var(--el-border-color);
            border-radius: 6px;
            cursor: pointer;
            position: relative;
            overflow: hidden;
            transition: var(--el-transition-duration-fast);
        }
        .el-upload:hover {
            border-color: var(--el-color-primary);
        }
    }
    :deep(.el-icon.avatar-uploader-icon) {
        font-size: 28px;
        color: #8c939d;
        text-align: center;
    }
}
</style>
