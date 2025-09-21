<script lang="ts" setup>
import type { PropType } from 'vue'
import Cropper from './cropper.vue'
import { ElMessage } from 'element-plus'
import { useVModel } from '@vueuse/core'
import { verifyFormat, httpRequest } from './upload'
import type { UploadConfig, CusUploadRawFile, IMAGETYPE } from '@/components/q-upload/upload'
import type { UploadFile, UploadRawFile } from 'element-plus'

// TODO： 类型优化
const $props = defineProps({
    uploadUrl: {
        type: String,
        default: 'gruul-mall-carrier-pigeon/oss/upload',
    },
    disabled: { type: Boolean, default: false },
    src: {
        type: String,
        default: '',
    },
    format: {
        type: Object as PropType<UploadConfig | { width: number } | { height: number } | { type: Array<string> } | { size: number }>,
        default() {
            return {
                width: 10000,
                height: 10000,
                types: ['image/png', 'image/jpg', 'image/gif', 'image/jpeg', 'image/avif'],
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
    appendToBody: {
        type: Boolean,
        default: false,
    },
    isAutoUpload: {
        type: Boolean,
        default: true,
    },
    cropper: {
        type: Boolean,
        default: true,
    },
})
const $emit = defineEmits(['update:src', 'change', 'success', 'beforeUpdate'])
const imgSrc = useVModel($props, 'src', $emit)
const uploadRef = ref()
const cropperSrc = ref('')
const isShowCropper = ref(false)
// 暂存cropper的file
const tempCropperFile = ref()

const fileType = ref<keyof typeof IMAGETYPE>('image/png')

const handleChangeUpload = async (file: UploadFile) => {
    if (file.status !== 'ready') return
    const result = await verifyFormat(file as CusUploadRawFile, $props.format as Partial<UploadConfig>, $props.cropper, uploadRef)
    if (result && result.success && result.src) {
        if (typeof result.src === 'string') {
            cropperSrc.value = result.src
            isShowCropper.value = true
        } else {
            handleCropperLoad(result.src as unknown as string)
        }
        fileType.value = result.type
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
const handleUploadRequest = async (request: { action: string; data: any; file: File; filename: string; headers: any; method: string }) => {
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
const handleClose = () => {
    uploadRef.value.handleRemove((file: UploadFile | UploadRawFile, rawFile?: UploadRawFile) => {})
}
const handleError = (err: any, file: any, fileList: any) => {
    let errorMessage = err.message || '文件上传失败，请稍后重试'
    if (err.response && err.response.data && err.response.data.message) {
        errorMessage = err.response.data.message
    }
    ElMessage.error(errorMessage)
}
</script>
<template>
    <div class="qupload">
        <el-upload
            ref="uploadRef"
            :action="$props.uploadUrl"
            :auto-upload="false"
            :disabled="$props.disabled"
            :http-request="handleUploadRequest"
            :on-change="handleChangeUpload"
            :on-error="handleError"
            :show-file-list="false"
            class="avatar-uploader"
            @success="$emit('success', { tempCropperFile })"
        >
            <slot>
                <img v-if="imgSrc" :src="imgSrc" :style="{ width: `${$props.width}px`, height: `${$props.height}px` }" />
                <el-icon v-else :style="{ width: `${$props.width}px`, height: `${$props.height}px` }"><i-ep-plus /></el-icon>
            </slot>
        </el-upload>
        <Cropper
            v-if="isShowCropper"
            v-model:cropper-show="isShowCropper"
            :append-to-body="appendToBody"
            :cropper-src="cropperSrc"
            :type="fileType"
            @close="handleClose"
            @upload-img="handleCropperLoad"
        />
    </div>
</template>

<style lang="scss" scoped>
@include b(qupload) {
    border-radius: 6px;
    border: 1px dashed #ccc;
    & img {
        border-radius: 6px;
    }
}
.avatar-uploader {
    @include flex();
}
</style>
