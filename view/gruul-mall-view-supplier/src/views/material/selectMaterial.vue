<script setup lang="ts">
import Search from './components/search.vue'
import selectMaterialList from './components/selectMaterialList.vue'
import { ElMessage } from 'element-plus'
import { uploadFileApi } from '../../apis/upload/index'
import { QuestionFilled } from '@element-plus/icons-vue'
import { doPostMaterialList } from '@/apis/material'
import uploading from './components/uploading.vue'
import useMaterialListHooks from './hooks/useMaterialListHooks'
import useMaterialCategoryList from './hooks/useMaterialCategoryList'
import { cloneDeep, isArray } from 'lodash-es'
const { uploadingFn, uploadingVal } = useMaterialListHooks()
const { currentFormModel } = useMaterialCategoryList()

interface SelectLists {
    categoryId: string
    format: string
    id: string
    name: string
    shopId: string
    size: string
    url: string
}
const handleClose = () => {
    emit('selectMaterialFn', false)
    selectLists.value = []
}
const props = defineProps({
    dialogVisible: {
        type: Boolean,
        default: false,
    },
    uploadFiles: {
        type: Number,
        default: 0,
    },
    videoLimit: {
        type: Boolean,
        default: false,
    },
})
/*
 *uploadFiles ==> 上传素材限制几张
 *selectMaterialFn==> 控制弹窗
 *croppedFileChange==> 裁剪图片后返回的文件
 *checkedFileLists==> 选择素材后返回的素材合集
 */
const emit = defineEmits(['selectMaterialFn', 'croppedFileChange', 'checkedFileLists'])
const searchCondition = reactive({
    format: '',
    name: '',
    imgSize: '',
    categoryId: '',
})
const paginationOptions = reactive({
    page: { size: 50, current: 1 },
    total: 0,
})
const categoryBolFn = () => initialList()
const tableList = ref()
let tableData = ref<{ categoryId: string; format: string; id: string; name: string; shopId: string; size: string; url: string }[]>()
const initialList = async () => {
    let total = 0
    try {
        const result = await doPostMaterialList({ ...searchCondition, ...paginationOptions.page })
        tableData.value = result?.data?.records
        total = result?.data?.total || 0
    } finally {
        tableList.value = tableData
        paginationOptions.total = total
    }
}
initialList()
const searchModule = ref()
watch(
    () => props.dialogVisible,
    (val) => {
        if (!val) {
            croppedFile.value = []
            CheckedMaterialUrlLists.value = []
            searchModule.value?.handleResetSearchType()
        }
    },
)

const handleSearch = (searchType: typeof searchCondition) => {
    const paramsValue = cloneDeep(searchType)
    if (isArray(paramsValue.categoryId)) paramsValue.categoryId = paramsValue.categoryId?.shift()
    Object.keys(searchCondition).forEach((key) => (searchCondition[key] = paramsValue[key]))
    paginationOptions.page.current = 1
    initialList()
}
const handleSizeChange = (val: number) => {
    paginationOptions.page.current = 1
    paginationOptions.page.size = val
    selectLists.value = []
    initialList()
}
const handleCurrentChange = (val: number) => {
    paginationOptions.page.current = val
    selectLists.value = []
    initialList()
}

const selectLists = ref<SelectLists[]>([])
const listLiFn = (li: SelectLists) => {
    if (!selectLists.value.includes(li)) selectLists.value.push(li)
    else selectLists.value = selectLists.value.filter((item) => item.id !== li.id)
}
const isShowCropper = ref(false)
const appendToBody = ref(false)
const cropperSrc = ref('')
// 使用裁剪
const useTailorFn = () => {
    if (selectLists.value.length === 0) return ElMessage.error('至少选中一个素材进行裁剪')
    else if (selectLists.value.length > 1) return ElMessage.error('只能选择一个素材进行裁剪')
    if (selectLists.value?.[0].format === 'mp4' || selectLists.value?.[0].format === 'MP4') return ElMessage.error('暂不支持视频裁剪喔~')
    if (props.videoLimit) return ElMessage.error('只能选择视频素材')
    cropperSrc.value = selectLists.value?.[0]?.url
    isShowCropper.value = true
}

// 裁剪后的文件返回
const croppedFile = ref<string[]>([])
const handleCropperLoad = (uploadStr: any) => {
    const formData = new FormData()
    formData.append('file', uploadStr)
    uploadFileApi(import.meta.env.VITE_BASE_URL + 'gruul-mall-carrier-pigeon/oss/upload', formData).then((res) => {
        if (res.data.code === 200) {
            croppedFile.value.push(res.data.data)
            emit('croppedFileChange', croppedFile.value)
            croppedFile.value = []
            handleClose()
        }
    })
}

const handleCloseFn = () => {
    isShowCropper.value = false
}

const CheckedMaterialUrlLists = ref<string[]>([])
// 使用选中素材
const useCheckedMaterialFn = () => {
    if (selectLists.value.length === 0) return ElMessage.error('至少选中一个素材')
    if (props.uploadFiles > 0) if (selectLists.value.length > props.uploadFiles) return ElMessage.error(`只能选择${props.uploadFiles}个素材喔`)

    for (let i = 0; i < selectLists.value.length; i++) {
        if (!props.videoLimit && (selectLists.value?.[i]?.format === 'mp4' || selectLists.value?.[i]?.format === 'MP4')) {
            ElMessage.error('暂不支持视频素材，请重新选择素材上传')
            CheckedMaterialUrlLists.value = []
            return
        }
        //     CheckedMaterialUrlLists.value.push(selectLists.value?.[i]?.url)
        // }
        if (props.videoLimit && selectLists.value?.[i]?.format !== 'mp4') {
            if (selectLists.value?.[i]?.format !== 'mp4' || selectLists.value?.[i]?.format !== 'MP4') {
                ElMessage.error('只能选择视频素材')
                CheckedMaterialUrlLists.value = []
                return
            }
        } else if (!props.videoLimit && (selectLists.value?.[i]?.format === 'mp4' || selectLists.value?.[i]?.format === 'MP4')) {
            CheckedMaterialUrlLists.value = []
            ElMessage.error('暂不支持视频素材，请选择其他素材上传')
            return
        }
        CheckedMaterialUrlLists.value.push(selectLists.value?.[i]?.url)
    }
    emit('checkedFileLists', CheckedMaterialUrlLists.value)
    CheckedMaterialUrlLists.value = []
    handleClose()
}
const uploadFn = () => {
    uploadingVal.value = false
    upload.value.categoryFormModel.parentId = ' '
}
const upload = ref()
const loading = ref(false)
const uploadingConfirm = async () => {
    try {
        loading.value = true
        const formData = new FormData()
        if (upload.value) {
            formData.append('categoryId', upload.value.categoryFormModel.parentId)
            upload.value.fileLists.forEach((item: any) => {
                formData.append('files[]', item)
            })
            // if (!upload.value.categoryFormModel.parentId) return ElMessage.error('请选择上级分类')
            // else
            if (upload.value.fileLists.length === 0) return
            const res = await uploadFileApi('/gruul-mall-search/search/material/upload', formData)
            if (res.data.code === 200) {
                upload.value.fileLists = []
                uploadingVal.value = false
                ElMessage.success('上传成功')
            } else ElMessage.error(res.data.msg || '上传失败')
        }
        initialList()
    } finally {
        loading.value = false
    }
}
</script>
<template>
    <el-dialog
        :model-value="props.dialogVisible"
        center
        title="选择素材"
        width="1100"
        top="6vh"
        :before-close="handleClose"
        :append-to-body="true"
        :destroy-on-close="true"
    >
        <Search v-model:categoryId="searchCondition.categoryId" :flex="false" @onSearchParams="handleSearch" />
        <div class="uploadingBox">
            <el-button type="primary" class="uploadingBox__btn" @click="uploadingFn">上传素材</el-button>
            <el-tooltip content="支持素材格式（jpg、gif、jpeg、png、webp、bmp、MP4），每次限10个文件，单个文件 10MB 以内" placement="top">
                <el-icon color="#333" :size="20"><QuestionFilled /></el-icon>
            </el-tooltip>
        </div>
        <selectMaterialList :select-lists="selectLists" :table-data="tableData" @list-li-fn="listLiFn" />
        <page-manage
            style="margin-top: 12px"
            :page-size="paginationOptions.page.size"
            :page-num="paginationOptions.page.current"
            :total="paginationOptions.total"
            :page-sizes="[50, 100, 150, 200]"
            @handle-size-change="handleSizeChange"
            @handle-current-change="handleCurrentChange"
        />
        <template #footer>
            <div class="dialog-footer">
                <cropper
                    v-if="isShowCropper"
                    v-model:cropper-show="isShowCropper"
                    :append-to-body="appendToBody"
                    :cropper-src="cropperSrc"
                    @close="handleCloseFn"
                    @upload-img="handleCropperLoad"
                ></cropper>
                <!-- <QUpload :is-auto-upload="false" style="border: 0" @change="changeFn">
                    <el-button class="use" @click="useTailorFn">使用裁剪</el-button>
                </QUpload> -->
                <el-button class="use" @click="useTailorFn">裁剪后使用</el-button>
                <el-button type="primary" @click="useCheckedMaterialFn"> 确定 </el-button>
            </div>
        </template>
        <!-- 上传素材 -->
        <el-dialog v-model="uploadingVal" title="上传素材" :width="950" destroy-on-close @close="uploadFn">
            <uploading ref="upload" v-model:form-model="currentFormModel" />
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="uploadFn">取消</el-button>
                    <el-button type="primary" :loading="loading" @click="uploadingConfirm"> 确定 </el-button>
                </span>
            </template>
        </el-dialog>
    </el-dialog>
</template>

<style lang="scss" scoped>
:deep(.el-dialog__body, .el-dialog--center) {
    padding-top: 0 !important;
}
@include b(use) {
    margin: 0 50px 0 0px;
}
@include b(dialog-footer) {
    display: flex;
    justify-content: end;
    padding: 0 10px 0 0;
}
@include b(uploadingBox) {
    display: flex;
    align-items: center;
    float: right;
    margin-bottom: 16px;
    padding-right: 20px;
    @include e(btn) {
        margin-right: 10px;
    }
}
</style>
