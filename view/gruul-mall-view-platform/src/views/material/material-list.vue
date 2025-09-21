<script lang="ts" setup>
import useMaterialListHooks from './hooks/useMaterialListHooks'
import translationMaterial from './components/translationMaterial.vue'
import useMaterialCategoryList from './hooks/useMaterialCategoryList'
import uploading from './components/uploading.vue'
import { uploadFileApi } from '../../apis/upload/index'
import { doPutMaterialTo } from '@/apis/material'
import { ElMessage } from 'element-plus'
import type { FormRules } from 'element-plus'
import Storage from '@/utils/Storage'

const {
    checkedSelect,
    tableList,
    tableHeight,
    handlePreview,
    paginationOptions,
    initialList,
    lookInfoVal,
    lookInfos,
    lookInfoFn,
    disable,
    uploadingVal,
    uploadingFn,
    lookInfoValFn,
    handleSizeChange,
    handleCurrentChange,
    delBatchFn,
    handleAddCategoryTran,
    showDia,
    checkedSelectIds,
    ruleFormRef,
    searchCondition,
    pImgs,
    checkItem,
    handleMouseEnter,
    handleMouseLeave,
} = useMaterialListHooks()
initialList()
const { currentFormModel, handleCloseDialog } = useMaterialCategoryList()
const upload = ref()
const loading = ref(false)
const uploadingConfirm = async () => {
    try {
        loading.value = true
        const formData = new FormData()
        // if (currentFormModel.parentId === ' ') return ElMessage.error('请选择上传分类')
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
                initialList()
                new Storage().setItem('platformParentId', upload.value.categoryFormModel.parentId)
                ElMessage.success('上传成功')
            } else ElMessage.error(res.data.msg || '上传失败')
        }
    } finally {
        loading.value = false
    }
}
const emit = defineEmits(['categoryListFn'])
const uploadFn = () => {
    uploadingVal.value = false
    // upload.value.categoryFormModel.parentId = ' '
    emit('categoryListFn')
}
const moveFn = async () => {
    // if (currentFormModel.parentId === '') return ElMessage.error('请选择上级分类')
    const { data, code, msg } = await doPutMaterialTo(currentFormModel.parentId, checkedSelectIds.value)
    if (code === 200) {
        initialList()
        showDia.value = false
        ElMessage.success('操作成功')
        checkedSelect.value = []
        return
    } else ElMessage.error(msg || '移动素材失败，请重试')
}
const rules: FormRules = {
    name: [
        { required: true, message: '请输入素材名称', trigger: 'blur' },
        { min: 0, max: 20, message: '素材名称 0 - 20 之间', trigger: 'blur' },
    ],
}
const props = defineProps({
    classificationId: {
        type: String,
        default: () => '',
    },
})
watch(
    () => props.classificationId,
    () => {
        searchCondition.categoryId = props.classificationId
        currentFormModel.parentId = props.classificationId
        initialList()
    },
)
function changeTableHeight(isShowCondition: boolean) {
    tableHeight.value = isShowCondition ? 'calc(100% - 452px)' : 'calc(100% - 347px)'
}
function handleSearch(searchType: typeof searchCondition) {
    ;(Object.keys(searchCondition) as (keyof typeof searchCondition)[]).forEach((key) => (searchCondition[key] = searchType[key]))
    paginationOptions.page.current = 1
    initialList()
}

defineExpose({ handleSearch, changeTableHeight })
</script>
<template>
    <div style="display: flex; flex-direction: column; flex: 1">
        <div class="btn">
            <el-button type="primary" @click="uploadingFn">上传素材</el-button>
            <el-button :disabled="checkedSelect.length === 0" @click="handleAddCategoryTran">移动至</el-button>
            <el-button :disabled="checkedSelect.length === 0" @click="delBatchFn">批量删除</el-button>
        </div>
        <div v-if="paginationOptions.total !== 0" class="card-list">
            <div
                v-for="(item, index) in tableList"
                :key="index"
                class="card-content"
                @click="checkItem(item)"
                @mouseenter="(e) => handleMouseEnter(index)"
                @mouseleave="(e) => handleMouseLeave(index)"
            >
                <div>
                    <video
                        v-if="item.format === 'mp4' || item.format === 'MP4'"
                        style="width: 140px; height: 140px"
                        autoplay
                        controls
                        loop
                        muted
                        :src="item?.url"
                    ></video>
                    <el-image v-else :src="item.url" fit="cover"></el-image>
                    <div class="card-list__foot">
                        <el-tooltip v-if="item?.name.length >= 12" class="box-item" effect="dark" :content="item?.name" placement="bottom">
                            <span class="card-list__foot--name">{{ item?.name }}</span>
                        </el-tooltip>
                        <span v-else class="card-list__foot--name">{{ item?.name }}</span>
                        <el-tooltip class="box-item" effect="dark" :content="item?.size + '（' + item?.format + ')'" placement="bottom-start">
                            <span class="card-list__foot--format">{{ item?.size }}（{{ item?.format }}）</span>
                        </el-tooltip>
                    </div>
                    <div v-if="item?.check" class="check-item">
                        <i class="iconfont icon-dui-copy" style="font-size: 20px; color: #555cfd"></i>
                    </div>
                </div>
                <div v-if="item.show" class="operation">
                    <span class="margin" @click.stop="handlePreview(item, 'look')">查看</span>
                    <span class="margin" @click.stop="handlePreview(item, '')">重命名</span>
                    <span class="margin" style="color: red" @click.stop="handlePreview(item, 'del')">删除</span>
                </div>
            </div>
        </div>
        <div v-else class="no-data-available">
            <div>
                <img src="@/assets/image/currently_unavailable.png" alt="" />
                <p style="padding: 30px 0; text-align: center; color: #737b80">暂无数据</p>
            </div>
        </div>

        <!-- 移动素材 -->
        <el-dialog v-model="showDia" title="移动素材" :width="650" :destroy-on-close="true" @close="handleCloseDialog">
            <translationMaterial v-model:form-model="currentFormModel" />
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="showDia = false">取消</el-button>
                    <el-button type="primary" @click="moveFn"> 确定 </el-button>
                </span>
            </template>
        </el-dialog>
        <!-- 上传素材 -->
        <el-dialog v-model="uploadingVal" title="上传素材" :width="900" :destroy-on-close="true" @close="uploadFn">
            <uploading ref="upload" v-model:form-model="currentFormModel" />
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="uploadFn">取消</el-button>
                    <el-button type="primary" :loading="loading" @click="uploadingConfirm"> 确定 </el-button>
                </span>
            </template>
        </el-dialog>
        <!-- 查看 && 重命名 -->
        <el-dialog
            v-model="lookInfoVal"
            :close-on-click-modal="!disable ? true : false"
            center
            :width="800"
            :destroy-on-close="true"
            style="background-color: #f8f8f8"
            @closed="
                () => {
                    lookInfoFn(false)
                }
            "
        >
            <div :style="!disable ? 'height: 400px; display: flex; justify-content: center' : 'height: 100px'">
                <div v-if="!disable" style="width: 750px; height: 400px; overflow: hidden; text-align: center">
                    <video
                        v-if="(lookInfos.format === 'mp4' || lookInfos.format === 'MP4') && !disable"
                        :src="lookInfos?.url"
                        loop
                        autoplay
                        muted
                        controls
                        style="width: auto; height: 400px"
                    />
                    <el-image
                        v-else-if="(lookInfos.format !== 'mp4' || lookInfos.format !== 'MP4') && !disable"
                        class="pImg"
                        :src="pImgs"
                        fit="contain"
                    ></el-image>
                </div>
                <el-form v-if="disable" ref="ruleFormRef" :model="lookInfos" :rules="rules" label-position="left" label-width="100px">
                    <el-form-item label="素材名称：" :prop="disable ? 'name' : ''">
                        <el-input v-model="lookInfos.name" :disabled="disable ? false : true" style="width: 320px" />
                    </el-form-item>
                </el-form>
            </div>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="lookInfoFn(false)">取消</el-button>
                    <el-button type="primary" @click="lookInfoValFn(lookInfos)"> 确定 </el-button>
                </span>
            </template>
        </el-dialog>
        <!-- <PageManage v-model="paginationOptions.page" load-init :total="paginationOptions.total" @reload="initialList" /> -->
        <page-manage
            v-if="paginationOptions.total !== 0"
            :page-size="paginationOptions.page.size"
            :page-num="paginationOptions.page.current"
            :total="paginationOptions.total"
            @handle-size-change="handleSizeChange"
            @handle-current-change="handleCurrentChange"
        />
    </div>
</template>

<style lang="scss" scoped>
@include b(btn) {
    text-align: left;
    padding: 16px 16px 16px 0;
    .el-button {
        border-radius: 2px;
    }
}
@include b(card-list) {
    display: flex;
    flex-wrap: wrap;
    overflow-y: scroll;
    .card-content {
        width: 140px;
        height: 190px;
        margin-right: 24px;
        margin-bottom: 36px;
        position: relative;
        cursor: pointer;
        .el-image {
            width: 140px;
            height: 140px;
        }

        .operation {
            width: 100%;
            position: absolute;
            left: 0;
            bottom: 50px;
            height: 28px;
            display: flex;
            justify-content: space-around;
            align-items: center;
            background: linear-gradient(to right, #e0f3fc, #a4daee);
            font-size: 12px;
            cursor: pointer;
        }
    }
    @include e(foot) {
        width: 140px;
        display: flex;
        flex-direction: column;
        padding: 0 11px;
        @include m(name) {
            @include utils-ellipsis;
            padding: 8px 0 2px 0;
        }
        @include m(format) {
            color: #999999;
            @include utils-ellipsis;
        }
    }
    @include b(check-item) {
        position: absolute;
        width: 140px;
        height: 140px;
        border: 1px solid #555cfd;
        top: 0;
        left: 0;
        display: flex;
        justify-content: flex-end;
        i {
            padding: 2px;
        }
    }
}
@include b(no-data-available) {
    width: 100%;
    height: 100%;
    @include flex(center, center);
    img {
        width: 246px;
        height: 144px;
    }
}
@include b(margin) {
    color: #555cfd;
}
@include b(selected) {
    margin-right: 0;
}
@include b(name) {
    word-break: break-all;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 4;
    -webkit-box-orient: vertical;
}

@include b(pImg) {
    width: 750px;
    height: 400px;
    background-size: contain !important ;
}
@include b(rowImg) {
    width: 140px;
    height: 70px;
    background-size: contain !important ;
}
:deep(.el-card__body) {
    padding: 0;
}
:deep(.el-card__footer) {
    padding: 0;
}
</style>
