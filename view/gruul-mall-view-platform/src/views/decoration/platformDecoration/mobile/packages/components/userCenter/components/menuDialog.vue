<script setup lang="ts">
import QUpload from '@/components/q-upload/q-upload.vue'
import LinkSelect from '@/components/link-select/link-select.vue'
import { useVModel } from '@vueuse/core'
import type { PropType } from 'vue'
import type { UserCenterMenuItem } from '../user-center'
import { ElMessage, ElMessageBox } from 'element-plus'

// 选择素材 e
import selectMaterial from '@/views/material/selectMaterial.vue'
const dialogVisibles = ref(false)
const selectMaterialFn = (val: boolean) => {
    dialogVisibles.value = val
}
const parameterId = ref('')
const buttonlFn = () => {
    dialogVisibles.value = true
}
// @cropped-file-change="" 裁剪后返回的单个素材
// @checked-file-lists=""  选中素材返回的素材合集
const croppedFileChange = (val: string) => {
    menuItem.value.menuIconUrl = val
}
const checkedFileLists = (val: string[]) => {
    menuItem.value.menuIconUrl = val?.shift() || ''
}
// 选择素材 d

const $props = defineProps({
    menuVisible: {
        type: Boolean,
        default: false,
    },
    menuProp: {
        type: Object as PropType<UserCenterMenuItem>,
        default() {
            return {}
        },
    },
    // 1为修改 2位新增
    type: {
        type: Number,
        default: 1,
    },
})
const $emit = defineEmits(['update:menuVisble', 'updateMenu'])
const dialogVisible = useVModel($props, 'menuVisible', $emit)
const menuItem = ref<UserCenterMenuItem>({
    id: null,
    allowUse: 1,
    defaultIcon: '',
    showMenu: false,
    linkSelectItem: {
        id: '',
        name: '',
        type: 0,
        url: '',
        append: '',
    },
    menuIconUrl: '',
    menuName: '',
    sortIndex: 0,
    splitFlag: false,
})
watch(dialogVisible, (newVal) => {
    if (newVal) {
        if ($props.type === 2) {
            menuItem.value = {
                id: null,
                allowUse: 1,
                defaultIcon: '',
                showMenu: false,
                linkSelectItem: {
                    id: '',
                    name: '',
                    type: 0,
                    url: '',
                    append: '',
                },
                menuIconUrl: '',
                menuName: '',
                sortIndex: 0,
                splitFlag: false,
            }
        } else {
            menuItem.value = JSON.parse(JSON.stringify($props.menuProp))
        }
    }
})

const confirmClickHandle = () => {
    if (!menuItem.value.menuName) {
        ElMessage.warning('请填写菜单名称')
        return
    }
    if (!menuItem.value.menuIconUrl) {
        ElMessage.warning('请选择图标')
        return
    }
    $emit('updateMenu', toRaw(menuItem.value))
    dialogVisible.value = false
}
const handleClose = () => {
    dialogVisible.value = false
}
</script>

<template>
    <el-dialog v-model="dialogVisible" width="450px" :append-to-body="true" :modal-append-to-body="true" title="编辑" :before-close="handleClose">
        <el-form ref="form" v-model="menuItem" label-width="120px" label-position="left">
            <el-form-item label="菜单名称">
                <el-input v-model="menuItem.menuName" placeholder="请输入菜单名称" maxlength="10" show-word-limit></el-input>
            </el-form-item>
            <el-form-item label="菜单图标">
                <div class="form-item__content">
                    <!-- <q-upload v-model:src="menuItem.menuIconUrl" :width="120" :height="120" /> -->

                    <div v-if="!menuItem.menuIconUrl" class="selectMaterialStyle" @click="buttonlFn">
                        <span class="selectMaterialStyle__span">+</span>
                    </div>
                    <div v-else class="selectMaterialStyle" @click="buttonlFn">
                        <img :src="menuItem.menuIconUrl" alt="" style="height: 120px" />
                    </div>
                </div>
            </el-form-item>
            <el-form-item label="跳转链接">
                <link-select v-model:link="menuItem.linkSelectItem" />
            </el-form-item>
        </el-form>
        <template #footer>
            <div class="dialog--footer">
                <el-button @click="handleClose">取 消</el-button>
                <el-button type="primary" @click="confirmClickHandle">确 定</el-button>
            </div>
        </template>
        <selectMaterial
            :dialog-visible="dialogVisibles"
            :upload-files="1"
            @select-material-fn="selectMaterialFn"
            @cropped-file-change="croppedFileChange"
            @checked-file-lists="checkedFileLists"
        />
        <!-- 选择素材 d -->
    </el-dialog>
    <!-- 选择素材 e -->
</template>

<style scoped lang="scss">
.form--item {
    display: flex;
    justify-content: flex-start;
    align-content: flex-end;
    padding: 10px;

    .form-item__label {
        width: 120px;
        text-align: left;
        vertical-align: middle;
        float: left;
        font-size: 14px;
        color: #606266;
        line-height: 14px;
        box-sizing: border-box;
    }
}

.el-upload__tip {
    color: #9797a1;
}

@include b(selectMaterialStyle) {
    width: 120px;
    height: 120px;
    border-radius: 5px;
    overflow: hidden;
    border: 1px dashed #ccc;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    @include e(span) {
        color: #a7a7a7;
        font-size: 20px;
    }
}
</style>
