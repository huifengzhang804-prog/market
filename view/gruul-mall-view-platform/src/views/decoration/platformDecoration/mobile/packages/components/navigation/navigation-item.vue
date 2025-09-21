<script setup lang="ts">
import LinkSelect from '@/components/link-select/link-select.vue'
import QUpload from '@/components/q-upload/q-upload.vue'
import { useVModel } from '@vueuse/core'
import { defaultNavItem, NavItem } from './navigation'
import type { PropType } from 'vue'

// 选择素材 e
import selectMaterial from '@/views/material/selectMaterial.vue'
const dialogVisible = ref(false)
const selectMaterialFn = (val: boolean) => {
    dialogVisible.value = val
}
const buttonlFn = () => {
    dialogVisible.value = true
}
// @cropped-file-change="" 裁剪后返回的单个素材
// @checked-file-lists=""  选中素材返回的素材合集
const croppedFileChange = (val: string) => {
    formData.value.navIcon = val
}
const checkedFileLists = (val: string[]) => {
    formData.value.navIcon = val?.shift() || ''
}
// 选择素材 d

const $props = defineProps({
    formData: {
        type: Object as PropType<NavItem>,
        default: defaultNavItem,
    },
    itemIndex: {
        type: Number,
        default: 0,
    },
})
const $emit = defineEmits(['update:formData', 'onDelect'])
const formData = useVModel($props, 'formData', $emit)
/**
 * 删除店铺导航tab
 */
const hanldeDelect = () => {
    $emit('onDelect', $props.itemIndex)
}
</script>

<template>
    <div class="storeNavigation-item-form">
        <el-form v-model="formData" label-width="85px" style="margintop: 15px">
            <el-form-item label="导航名称">
                <el-input v-model="formData.navName" :maxlength="4" placeholder="请输入导航名称"></el-input>
            </el-form-item>
            <el-form-item label="字体颜色">
                <el-color-picker v-model="formData.fontColor"></el-color-picker>
            </el-form-item>
            <el-form-item label="图标">
                <!-- <q-upload v-model:src="formData.navIcon" /> -->
                <img alt="" class="selectMaterialStyle" :src="formData.navIcon" @click="buttonlFn" />
            </el-form-item>
            <el-form-item label="跳转路径">
                <link-select :link="formData.link"></link-select>
                <!-- <div v-if="formData.link.name">
                    <span style="color: #9797a1">{{ formData.link.name }}</span>
                </div> -->
            </el-form-item>
        </el-form>
        <img
            style="width: 35px; height: 35px; cursor: pointer; position: absolute; top: -5px; right: -5px"
            class="bar_item_del_icon"
            src="@/assets/image/decoration/del.png"
            @click="hanldeDelect"
        />
        <!-- 选择素材 e -->
        <selectMaterial
            :upload-files="1"
            :dialog-visible="dialogVisible"
            @select-material-fn="selectMaterialFn"
            @cropped-file-change="croppedFileChange"
            @checked-file-lists="checkedFileLists"
        />
        <!-- 选择素材 d -->
    </div>
</template>

<style lang="scss" scoped>
@import '@/assets/css/decoration/navigation.scss';
@include b(selectMaterialStyle) {
    width: 60px;
    height: 60px;
    border-radius: 5px;
    overflow: hidden;
    border: 1px dashed #ccc;
    cursor: pointer;
    display: flex;
    justify-content: center;
    align-items: center;
    @include e(span) {
        color: #999;
        font-size: 20px;
    }
}
</style>
