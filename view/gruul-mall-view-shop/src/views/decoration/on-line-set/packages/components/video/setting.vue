<script lang="ts" setup>
import { PropType } from 'vue'
import { useVModel } from '@vueuse/core'
import { ElMessage } from 'element-plus'
import defaultVideoData from './video'
import { UploadProps } from 'element-plus'

// 选择素材 e
import selectMaterial from '@/views/material/selectMaterial.vue'

const dialogVisible = ref(false)
const selectMaterialFn = (val: boolean) => {
    dialogVisible.value = val
}
const parameterId = ref('')
const buttonlFn = (val?: string) => {
    dialogVisible.value = true
    if (val) parameterId.value = val
    else parameterId.value = ''
}
// @cropped-file-change="" 裁剪后返回的单个素材
// @checked-file-lists=""  选中素材返回的素材合集
const croppedFileChange = (val: string) => {
    if (parameterId.value === 'poster') {
        upLoadiVideo.value.poster = val
    } else {
        upLoadiVideo.value.video = val
        uploadLoad.value = false
    }
}
const checkedFileLists = (val: string[], name: string) => {
    if (parameterId.value === 'poster') {
        upLoadiVideo.value.poster = val?.shift() || ''
    } else {
        upLoadiVideo.value.video = val?.shift() || ''
        uploadLoad.value = false
        upLoadiVideo.value.videoName = name
    }
}
// 选择素材 d

const $props = defineProps({
    formData: {
        type: Object as PropType<typeof defaultVideoData>,
        default: defaultVideoData,
    },
})
const $emit = defineEmits(['update:formData'])
const upLoadiVideo = useVModel($props, 'formData', $emit)
const uploadLoad = ref(false)
const uploadUrl = 'gruul-mall-carrier-pigeon/oss/upload'

onMounted(() => {})

const handleGotoVideo = () => {
    window.open(upLoadiVideo.value.video, '_blank')
}
/**
 * 上传视频校验
 */
const beforeVideoUpload: UploadProps['beforeUpload'] = (rawfile) => {
    const whiteList = ['video/mp4', 'video/ogg', 'video/flv', 'video/avi', 'video/wmv', 'video/rmvb', 'video/mov']
    const fileSize = rawfile.size < 10 * 1024 * 1024
    if (!whiteList.includes(rawfile.type)) {
        ElMessage.error('上传视频只能是 mp4 格式!')
        return false
    }
    if (!fileSize) {
        ElMessage.error('上传视频大小不超过10M!')
        return false
    }
    uploadLoad.value = true
    return true
}
const uploadVideoSuccess: UploadProps['onSuccess'] = (response, uploadFile) => {
    upLoadiVideo.value.video = response.data
    uploadLoad.value = false
}
</script>

<template>
    <div>
        <div v-loading="uploadLoad" class="homeSwiperForm-form">
            <el-form :model="upLoadiVideo" label-position="left" label-width="70px">
                <el-form-item label="视频来源">
                    <el-radio-group v-model="upLoadiVideo.radio">
                        <el-radio :value="1">本地视频</el-radio>
                        <el-radio :value="2">外部视频</el-radio>
                    </el-radio-group>
                    <!-- <el-upload
                        v-if="upLoadiVideo.radio === 1 && !upLoadiVideo.video"
                        :action="uploadUrl"
                        :before-upload="beforeVideoUpload"
                        :http-request="elementUploadRequest"
                        :on-success="uploadVideoSuccess"
                        :show-file-list="false"
                    >
                        <div class="uploadBox">
                            <el-icon><i-ep-plus /></el-icon>
                        </div>
                    </el-upload> -->

                    <div
                        v-if="upLoadiVideo.radio === 1 && !upLoadiVideo.video"
                        class="selectMaterialStyle"
                        style="margin-top: 30px"
                        @click="buttonlFn('')"
                    >
                        <span class="selectMaterialStyle__span">+</span>
                    </div>
                    <!-- <img alt="" class="selectMaterialStyle" src="" @click="buttonlFn" /> -->
                    <div v-else-if="upLoadiVideo.radio === 1 && upLoadiVideo.video" style="display: flex">
                        <div class="uploadLink" @click="handleGotoVideo">{{ upLoadiVideo.videoName }}</div>
                        <div class="modify" @click="buttonlFn('')">修改</div>
                    </div>

                    <!-- <div v-else-if="upLoadiVideo.radio === 1 && upLoadiVideo.video" style="display: flex">
                        <div class="uploadLink" @click="handleGotoVideo">{{ linkString }}</div>
                        <el-upload
                            :action="uploadUrl"
                            :before-upload="beforeVideoUpload"
                            :http-request="elementUploadRequest"
                            :on-success="uploadVideoSuccess"
                            :show-file-list="false"
                        >
                            <div class="modify">修改</div>
                        </el-upload>
                    </div> -->

                    <div v-else-if="upLoadiVideo.radio === 2">
                        <el-input v-model="upLoadiVideo.videoLink" class="uploadInput"></el-input>
                    </div>
                </el-form-item>
            </el-form>
            <el-form v-if="upLoadiVideo.video || upLoadiVideo.radio === 2" :model="upLoadiVideo" label-position="left" label-width="70px">
                <el-form-item label="封面图">
                    <!-- <q-upload v-model:src="upLoadiVideo.poster"> -->
                    <!-- <div v-if="!upLoadiVideo.poster" class="imgContent">
                            <el-icon><Plus /></el-icon>
                        </div>
                        <div v-if="upLoadiVideo.poster" class="imgContent">
                            <img :src="upLoadiVideo.poster" style="width: 200px; height: 120px" />
                        </div> -->
                    <!-- </q-upload> -->

                    <div v-if="!upLoadiVideo.poster" class="selectMaterialStyle" style="width: 60px; height: 60px" @click="buttonlFn('poster')">
                        <span class="selectMaterialStyle__span">+</span>
                    </div>
                    <img
                        v-else
                        alt=""
                        class="selectMaterialStyle"
                        style="width: 60px; height: 60px"
                        :src="upLoadiVideo.poster"
                        @click="buttonlFn('poster')"
                    />
                </el-form-item>
            </el-form>

            <el-form v-if="upLoadiVideo.video || upLoadiVideo.radio === 2" :model="upLoadiVideo" label-position="left" label-width="70px">
                <el-form-item label="播放比例">
                    <el-radio-group v-model="upLoadiVideo.radioBL">
                        <el-radio :value="1">16:9</el-radio>
                        <el-radio :value="2">1:1</el-radio>
                        <el-radio :value="3">自适应高度</el-radio>
                    </el-radio-group>
                </el-form-item>
            </el-form>

            <el-form v-if="upLoadiVideo.video || upLoadiVideo.radio === 2" :model="upLoadiVideo" label-position="left" label-width="70px">
                <el-form-item label="视频填充 ">
                    <el-radio-group v-model="upLoadiVideo.radioTC">
                        <el-radio :value="1">填充</el-radio>
                        <el-radio :value="2">周边留白</el-radio>
                    </el-radio-group>
                </el-form-item>
            </el-form>
        </div>
        <!-- 选择素材 e -->
        <selectMaterial
            :dialog-visible="dialogVisible"
            :upload-files="1"
            :video-limit="parameterId === '' ? true : false"
            @select-material-fn="selectMaterialFn"
            @cropped-file-change="croppedFileChange"
            @checked-file-lists="checkedFileLists"
        />
        <!-- 选择素材 d -->
    </div>
</template>

<style lang="scss" scoped>
.uploadBox {
    width: 200px;
    height: 80px;
    display: flex;
    justify-content: center;
    align-items: center;
    border: 1px dashed #e4e4e4;
    margin-top: 20px;
}

@include b(homeSwiperForm-form) {
    border: 1px solid #e4e4e4;
    padding: 10px;
}

.uploadLink {
    color: #409eff;
    font-size: 13px;
    display: inline-block;
    flex-wrap: nowrap;
    width: 220px;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
    cursor: pointer;
}

.uploadInput {
    width: 250px;
    height: 60px;
    margin-top: 20px;
}

.modify {
    cursor: pointer;
    color: #409eff;
    font-size: 13px;
}

.imgContent {
    width: 200px;
    height: 120px;
    display: flex;
    justify-content: center;
    align-items: center;
    line-height: 120px;
}
@include b(selectMaterialStyle) {
    width: 200px;
    height: 80px;
    border-radius: 5px;
    overflow: hidden;
    border: 1px dashed #ccc;
    cursor: pointer;
    display: flex;
    justify-content: center;
    align-items: center;
    @include e(span) {
        color: #999;
        font-size: 25px;
    }
}
</style>
