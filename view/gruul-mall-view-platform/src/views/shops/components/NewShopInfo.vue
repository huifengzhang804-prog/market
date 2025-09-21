<template>
    <div class="shopInfo">
        <el-form
            ref="currentFormRef"
            :model="submitForm"
            :rules="infoFormRules"
            :disabled="$route.name === 'previewShop' || $route.name === 'previewSupplier'"
        >
            <el-form-item label="营业执照" prop="license" style="position: relative">
                <q-upload v-model:src="submitForm.license" :isCropper="false">
                    <img v-if="submitForm.license" :src="submitForm.license" class="avatar" />
                    <el-icon v-else class="avatar-uploader-icon"><i-ep-plus /> </el-icon>
                </q-upload>
                <div class="shopInfo__exp ml20">
                    <el-image
                        :preview-src-list="[
                            'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/2025/7/31688b1e49e4b0dd235a3dd37b.png',
                        ]"
                        class="shopInfo__exp--img"
                        src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/2025/7/31688b1e49e4b0dd235a3dd37b.png"
                    >
                    </el-image>
                    <!-- <div class="shopInfo__exp--tip">示例</div> -->
                </div>
                <div v-if="uploadLicenseProgress" class="progress"><el-progress :percentage="uploadprogressPercentage" :color="colors" /></div>
            </el-form-item>
            <el-form-item label="法人身份" prop="legalPersonIdFront">
                <el-space :size="8">
                    <q-upload v-model:src="submitForm.legalPersonIdFront" :isCropper="false">
                        <img v-if="submitForm.legalPersonIdFront" :src="submitForm.legalPersonIdFront" class="avatar" />
                        <el-icon v-else class="avatar-uploader-icon"><i-ep-plus /></el-icon>
                    </q-upload>
                    <el-form-item prop="legalPersonIdBack">
                        <q-upload v-model:src="submitForm.legalPersonIdBack" :isCropper="false">
                            <img v-if="submitForm.legalPersonIdBack" :src="submitForm.legalPersonIdBack" class="avatar" />
                            <el-icon v-else class="avatar-uploader-icon"><i-ep-plus /></el-icon>
                        </q-upload>
                    </el-form-item>
                </el-space>
            </el-form-item>
            <el-form-item label="法人示例">
                <div class="shopInfo__exp">
                    <el-image
                        :preview-src-list="[
                            'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/2025/7/31688b1e78e4b0dd235a3dd37c.png',
                        ]"
                        src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/2025/7/31688b1e78e4b0dd235a3dd37c.png"
                        class="shopInfo__exp--img"
                    ></el-image>
                    <!-- <div class="shopInfo__exp--tip">示例</div> -->
                </div>
                <div class="shopInfo__exp ml20">
                    <el-image
                        :preview-src-list="[
                            'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/2025/7/31688b1eb4e4b0dd235a3dd37d.png',
                        ]"
                        src="https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/2025/7/31688b1eb4e4b0dd235a3dd37d.png"
                        class="shopInfo__exp--img"
                    ></el-image>
                    <!-- <div class="shopInfo__exp--tip">示例</div> -->
                </div>
            </el-form-item>
            <el-form-item>
                <div class="shopInfo__tips">请上传身份证正反面照片，仅限jpg、png，大小不超过2M，仅限上传2张</div>
            </el-form-item>
        </el-form>
    </div>
</template>

<script setup lang="ts">
import QUpload from '@/components/q-upload/q-upload.vue'
import { FormRules } from 'element-plus'
import type { FormInstance } from 'element-plus'
import type { Ref } from 'vue'
import { ShopFormType } from '../types'
const $route = useRoute()
//父组件
const $parent = inject('addShops')
const submitForm = ($parent as { submitForm: Ref<ShopFormType> }).submitForm
const currentFormRef = ref<FormInstance>()
// 裁切组件显示
const cropperShow = ref(false)
// 裁切组件Src
const cropperSrc = ref('')
// (许可证)上传组件实例
const licenseUploadRef = ref()
// 裁剪后的文件对象
const licenseUploadFile = ref('')
// file 当前预览的文件对象用于取消删除
defineExpose({
    currentFormRef,
    componentFlag: 'info',
})
const colors = [
    { color: '#f56c6c', percentage: 20 },
    { color: '#e6a23c', percentage: 40 },
    { color: '#5cb87a', percentage: 60 },
    { color: '#1989fa', percentage: 80 },
    { color: '#6f7ad3', percentage: 100 },
]
const uploadLicenseProgress = ref(false)
/**
 * 进度条
 */
const uploadprogressPercentage = ref(0)
const onUpdateIsShow = (e: boolean) => {
    cropperShow.value = e
    licenseUploadRef.value.clearFiles()
}
/**
 * 上传图片(许可证)
 * @param {*} uploadFile
 */
const uploadImg = (uploadFile: string) => {
    licenseUploadFile.value = uploadFile
    licenseUploadRef.value.submit()
}
const addUploadBigLegal = (res: any) => {
    submitForm.value.legalPersonIdFront = res.data
}
const addUploadBigLegelBack = (res: any) => {
    submitForm.value.legalPersonIdBack = res.data
}
const infoFormRules = reactive<FormRules>({
    license: [
        {
            required: true,
            message: '请上传营业执照',
            trigger: 'blur',
        },
    ],
    legalPersonIdFront: [
        {
            required: true,
            message: '请上传法人正面照',
            trigger: 'blur',
        },
    ],
    legalPersonIdBack: [
        {
            required: true,
            message: '请上传法人反面照',
            trigger: 'blur',
        },
    ],
})
</script>

<style lang="scss">
@include b(shopInfo) {
    font-size: 12px;
    padding: 20px 25px 70px 46px;
    @include e(exp) {
        display: inline-block;
        width: 120px;
        height: 120px;
        @include m(img) {
            display: block;
            width: inherit;
            height: inherit;
            border: 1px solid;
        }
        @include m(tip) {
            position: fixed;
            bottom: 0;
            width: 100%;
            height: 28px;
            line-height: 28px;
            text-align: center;
            background: rgba(0, 0, 0, 0.36);
            color: #fff;
        }
    }
    @include e(tips) {
        color: #999;
    }
}
@include b(progress) {
    width: 20%;
    bottom: -15px;
    position: absolute;
}
</style>
<style scoped>
.avatar-uploader .avatar {
    width: 178px;
    height: 178px;
    display: block;
}
</style>

<style>
.avatar-uploader .el-upload {
    border: 1px dashed #d9d9d9;
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
    width: 120px;
    height: 120px;
    text-align: center;
}
.ml20 {
    margin-left: 20px;
}
</style>
