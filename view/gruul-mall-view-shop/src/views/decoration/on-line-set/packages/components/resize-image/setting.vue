<script setup lang="ts">
import type { PropType } from 'vue'
import LinkSelect from '@/components/link-select/link-select.vue'
import { useVModel } from '@vueuse/core'
import defaultResizeImage from './resize-image'
import selectMaterial from '@/views/material/selectMaterial.vue'

const dialogVisible = ref(false)
const selectMaterialFn = (val: boolean) => {
    dialogVisible.value = val
}
const parameterId = ref('')
const buttonlFn = () => {
    dialogVisible.value = true
}
// @cropped-file-change="" 裁剪后返回的单个素材
// @checked-file-lists=""  选中素材返回的素材合集
const croppedFileChange = (val: string) => {
    upLoadiImg.value.img = val
}
const checkedFileLists = (val: string[]) => {
    upLoadiImg.value.img = val?.shift() || ''
}
// 选择素材 d

const $props = defineProps({
    formData: {
        type: Object as PropType<typeof defaultResizeImage>,
        default() {
            return defaultResizeImage
        },
    },
})
const $emit = defineEmits(['update:formData', 'change'])
const upLoadiImg = useVModel($props, 'formData', $emit)

const handleSelect = (item: any) => {
    upLoadiImg.value.link = item
    $emit('change', item)
}
</script>

<template>
    <div>
        <!-- <q-upload v-model:src="upLoadiImg.img" :height="100" class="homeSwiperForm-add">
            <div v-if="upLoadiImg.img" class="imgContent">
                <img :src="upLoadiImg.img" style="width: 200px; height: 120px" />
            </div>
            <div v-else>
                <span>请上传图片</span>
                <p style="margin-top: 5px">可在左侧自定义图片大小</p>
            </div>
        </q-upload> -->

        <div v-if="!upLoadiImg.img" style="width: 365px; height: 180px; margin-bottom: 10px" class="selectMaterialStyle" @click="buttonlFn">
            <span class="selectMaterialStyle__span"
                ><span style="color: #3088f0">请上传图片</span> <br />
                <span style="font-size: 12px">可在左侧自定义图片大小</span>
            </span>
        </div>
        <div
            v-else
            style="
                width: 365px;
                height: 180px;
                margin-bottom: 10px;
                border: 1px dashed #ccc;
                display: flex;
                justify-content: center;
                align-items: center;
                cursor: pointer;
            "
            @click="buttonlFn"
        >
            <img alt="" style="width: 200px; height: 120px; margin-bottom: 10px" :src="upLoadiImg.img" />
        </div>

        <div class="homeSwiperForm-form">
            <el-form label-position="left" :model="upLoadiImg" label-width="70px">
                <el-form-item label="图片上传">
                    <!-- <q-upload v-model:src="upLoadiImg.img" /> -->

                    <div v-if="!upLoadiImg.img" class="selectMaterialStyle" @click="buttonlFn">
                        <span class="selectMaterialStyle__span">+</span>
                    </div>
                    <img v-else alt="" class="selectMaterialStyle" :src="upLoadiImg.img" @click="buttonlFn" />
                </el-form-item>
                <el-form-item label="导航链接">
                    <link-select :link="upLoadiImg.link" @select="handleSelect" />
                </el-form-item>
            </el-form>
        </div>
        <!-- 选择素材 e -->
        <selectMaterial
            :dialog-visible="dialogVisible"
            :upload-files="1"
            @select-material-fn="selectMaterialFn"
            @cropped-file-change="croppedFileChange"
            @checked-file-lists="checkedFileLists"
        />
        <!-- 选择素材 d -->
    </div>
</template>

<style lang="scss" scoped>
@include b(homeSwiperForm-add) {
    margin-top: 20px;
    width: 100%;
    margin-bottom: 10px;
    :deep(.el-upload) {
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        width: 100%;
        height: 180px;
    }
    span {
        color: #3088f0;
        cursor: pointer;
    }
    p {
        font-size: 12px;
        color: #a7a7a7;
    }
}

.imgContent {
    display: flex;
    justify-content: center;
    align-items: center;
}

@include b(homeSwiperForm-form) {
    border: 1px solid #e4e4e4;
    padding: 10px;
}
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
        font-size: 14px;
        line-height: 24px;
    }
}
</style>
