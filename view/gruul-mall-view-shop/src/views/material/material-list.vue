<script lang="ts" setup>
import useMaterialListHooks from './hooks/useMaterialListHooks'
import TranslationMaterial from './components/translationMaterial.vue'
import useMaterialCategoryList from './hooks/useMaterialCategoryList'
import uploading from './components/uploading.vue'
import { uploadFileApi } from '../../apis/upload/index'
import { doPutMaterialTo } from '@/apis/material'
import { ElMessage } from 'element-plus'
import type { FormRules } from 'element-plus'
import Storage from '@/utils/Storage'
import { ImageDetail } from '@/apis/material/types'

const {
    checkedSelect,
    tableList,
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
} = useMaterialListHooks()

initialList()
const { currentFormModel, handleCloseDialog } = useMaterialCategoryList()
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
                initialList()
                new Storage().setItem('shopParentId', upload.value.categoryFormModel.parentId)
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

const handleMouseEnter = (index: number) => {
    tableList.value[index].show = true
}
const handleMouseLeave = (index: number) => {
    tableList.value[index].show = false
}

const handleSelect = (row: ImageDetail) => {
    if (checkedSelect.value.find((item: ImageDetail) => item.id === row.id)) {
        checkedSelect.value = checkedSelect.value.filter((item: ImageDetail) => item.id !== row.id)
    } else {
        checkedSelect.value.push(row)
    }
}
defineExpose({
    searchCondition,
    initialList,
    currentFormModel,
})
</script>
<template>
    <div class="fdc1 right_container">
        <div class="handle_container" style="padding-left: 0">
            <el-button type="primary" @click="uploadingFn">上传素材</el-button>
            <el-button :disabled="checkedSelect.length === 0" @click="handleAddCategoryTran">移动至</el-button>
            <el-button :disabled="checkedSelect.length === 0" @click="delBatchFn">批量删除</el-button>
        </div>
        <div class="sourceMaterial_container">
            <div
                v-for="(row, index) in tableList"
                :key="index"
                class="sourceMaterial_item ccenter fdc"
                style="margin-right: 24px"
                @mouseenter="(e) => handleMouseEnter(index)"
                @mouseleave="(e) => handleMouseLeave(index)"
                @click="handleSelect(row)"
            >
                <video
                    v-if="row.format === 'mp4' || row.format === 'MP4'"
                    style="width: 140px; height: 140px"
                    :class="{
                        isChecked: checkedSelect.find((item: ImageDetail) => item.id === row.id),
                    }"
                    autoplay
                    controls
                    loop
                    muted
                    :src="row?.url"
                ></video>
                <el-image
                    v-else
                    :class="{
                        isChecked: checkedSelect.find((item: ImageDetail) => item.id === row.id),
                    }"
                    style="width: 140px; height: 140px"
                    :src="row?.url"
                    fit="cover"
                >
                </el-image>
                <q-icon v-show="checkedSelect.find((item:ImageDetail) => item.id === row.id)" name="icon-dui" class="checked_icon"></q-icon>
                <div v-show="row.show" class="hover_handle fcenter">
                    <el-button type="primary" link @click.stop="handlePreview(row, 'look')">查看</el-button>
                    <el-button type="primary" link @click.stop="handlePreview(row, '')">重命名</el-button>
                    <el-button type="danger" link @click.stop="handlePreview(row, 'del')">删除</el-button>
                </div>
                <div class="info fdc">
                    <div class="title">
                        <el-tooltip v-if="row?.name.length >= 12" class="box-item" effect="dark" :content="row?.name" placement="bottom">
                            <span>{{ row?.name }}</span>
                        </el-tooltip>
                        <span v-else>{{ row?.name }}</span>
                    </div>
                    <div class="format">
                        <el-tooltip
                            v-if="`${row.size || '未知尺寸'}（${row.format}）`.length >= 15"
                            class="box-item"
                            effect="dark"
                            :content="`${row.size || '未知尺寸'}（${row.format}）`"
                            placement="bottom"
                        >
                            <span>
                                {{ `${row.size || '未知尺寸'}（${row.format}）` }}
                            </span>
                        </el-tooltip>
                        <span v-else>
                            {{ `${row.size || '未知尺寸'}（${row.format}）` }}
                        </span>
                    </div>
                </div>
            </div>
        </div>
        <ElTableEmpty v-if="tableList.length === 0" />
        <!-- 移动素材 -->
        <el-dialog v-model="showDia" title="移动素材" :width="650" :destroy-on-close="true" @close="handleCloseDialog">
            <TranslationMaterial v-model:form-model="currentFormModel" />
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
            @closed="lookInfoFn"
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
                <span v-if="disable" class="dialog-footer">
                    <el-button @click="lookInfoFn">取消</el-button>
                    <el-button type="primary" @click="lookInfoValFn(lookInfos)"> 确定 </el-button>
                </span>
            </template>
        </el-dialog>
        <page-manage
            :page-size="paginationOptions.page.size"
            :page-num="paginationOptions.page.current"
            :total="paginationOptions.total"
            @handle-size-change="handleSizeChange"
            @handle-current-change="handleCurrentChange"
        />
    </div>
</template>

<style lang="scss" scoped>
.right_container {
    padding-top: 16px;
    .sourceMaterial_container {
        display: flex;
        flex-wrap: wrap;
        overflow-y: scroll;
        .sourceMaterial_item {
            width: 140px;
            flex-shrink: 0;
            color: #333;
            margin-right: 24px;
            margin-bottom: 36px;
            font-size: 14px;
            position: relative;
            cursor: pointer;
            .el-image {
                background: #f3f3f3;
            }
            .checked_icon {
                position: absolute;
                right: 3px;
                top: 8px;
                color: rgb(85, 92, 253);
            }
            .isChecked {
                position: relative;
                border: 1px solid rgb(85, 92, 253);
                // 蒙版
                &::after {
                    content: '';
                    position: absolute;
                    width: 100%;
                    height: 100%;
                    left: 0;
                    top: 0;
                    background: rgba(255, 255, 255, 0.5);
                }
            }
            .hover_handle {
                position: absolute;
                width: 100%;
                height: 28px;
                left: 0;
                top: 115px;
                // 从#e0f3fc到#a5d4e4左右渐变
                background: linear-gradient(to right, #e0f3fc, #a4daee);
                display: flex;
                justify-content: center;
                align-items: center;
                .el-button {
                    font-size: 12px;
                }
                .el-button + .el-button {
                    margin-left: 5px;
                }
            }
            .title {
                @include utils-ellipsis;
                padding: 8px 0 2px 0;
            }
        }
    }
}
@include b(btn) {
    text-align: right;
    margin: 5px 0;
}
@include b(margin) {
    margin: 0;
    padding: 0 7px;
}
@include b(info) {
    padding: 0 11px;
    width: 140px;
}
.format {
    color: #999;
    @include utils-ellipsis;
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
</style>
