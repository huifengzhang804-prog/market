<script setup lang="ts">
import type { PropType } from 'vue'
import LinkSelect from '@/components/link-select/link-select.vue'
import { useVModel } from '@vueuse/core'
import swiperFormData from './swiper'
import type { SwiperFormDataType, SwiperListItem } from './swiper'

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
    if (parameterId.value === 'img') {
        const swiperListItem = {
            title: '',
            img: val,
            link: {
                id: '',
                type: '',
                name: '',
                url: '',
                append: '',
            },
            linkName: '',
        }
        formData.value.swiperList.push(swiperListItem)
    } else currentSwiperItem.value!.img = val
}
const checkedFileLists = (val: string[]) => {
    if (parameterId.value === 'img') {
        const swiperListItem = {
            title: '',
            img: val?.shift() || '',
            link: {
                id: '',
                type: '',
                name: '',
                url: '',
                append: '',
            },
            linkName: '',
        }
        formData.value.swiperList.push(swiperListItem)
    } else currentSwiperItem.value!.img = val?.shift() || ''
}
// 选择素材 d

const $props = defineProps({
    formData: {
        type: Object as PropType<SwiperFormDataType>,
        default() {
            return swiperFormData
        },
    },
})
const $emit = defineEmits(['update:formData'])
const formData = useVModel($props, 'formData', $emit)
const currentSwiperItem = ref<SwiperListItem>()
const dragStarIndex = ref(-1)

const handleImgChange = async (imgSrc: string) => {
    const swiperListItem = {
        title: '',
        img: imgSrc,
        link: {
            id: '',
            type: '',
            name: '',
            url: '',
            append: '',
        },
        linkName: '',
    }
    formData.value.swiperList.push(swiperListItem)
    console.log('formData', formData.value.swiperList)
}
/**
 * 替换轮播图片
 */
const handleItemImgChange = (imgSrc: string) => {
    currentSwiperItem.value!.img = imgSrc
}
/**
 * 记录当前操作轮播item
 */
const handleSaveSwiperItem = (item: SwiperListItem) => {
    currentSwiperItem.value = item
}
const handleDelSwiperItem = (idx: number) => {
    formData.value.swiperList.splice(idx, 1)
}
const handleDragstart = (i: number) => {
    dragStarIndex.value = i
}
/**
 * 阻止默认行为，否则drop事件不会触发
 * @param {*} e
 */
const handleDragover = (e: DragEvent) => {
    e.preventDefault()
}
/**
 * 被放置的容器触发事件，交换两个组件的位置
 * @param {number} i
 */
const handleDrop = (i: number) => {
    if (dragStarIndex.value === i) {
        return false
    }
    const temp = formData.value.swiperList.splice(dragStarIndex.value, 1, formData.value.swiperList[i])
    formData.value.swiperList.splice(i, 1, ...temp)
}

const heightType = formData.value.height >= 1200 ? ref(2) : ref(1)
const heightTypeChanged = (val: any) => {
    if (val === 1) {
        formData.value.height = 600
    } else {
        formData.value.height = 1500
    }
}
</script>

<template>
    <div class="homeSwiperForm">
        <el-form label-position="left" label-width="70px">
            <el-form-item label="添加图片">
                <span style="color: #999">可拖拽调整顺序</span>
            </el-form-item>
        </el-form>
        <div
            v-for="(item, index) in formData.swiperList"
            :key="index"
            :draggable="true"
            class="homeSwiperForm-item"
            @click.capture="handleSaveSwiperItem(item)"
            @dragstart="handleDragstart(index)"
            @dragover="handleDragover"
            @drop="handleDrop(index)"
        >
            <el-icon class="remove__swiper--item" @click="handleDelSwiperItem(index)"><i-ep-circleClose /></el-icon>
            <div class="homeSwiperForm-item__right">
                <el-form :model="item" label-width="70px">
                    <el-form-item label="图片标题">
                        <el-input v-model="item.title" placeholder="图片标题" maxlength="10"></el-input>
                    </el-form-item>
                    <el-form-item label="图片上传">
                        <!-- <q-upload :src="item.img" @change="handleItemImgChange"></q-upload> -->

                        <div v-if="!item.img" class="selectMaterialStyle" style="width: 60px; height: 60px" @click="buttonlFn('img')">
                            <span class="selectMaterialStyle__span"
                                >+ 添加背景图 <br />
                                <span style="font-size: 12px; color: #a7a7a7; margin-left: -3px">建议宽度750像素</span>
                            </span>
                        </div>
                        <img v-else alt="" class="selectMaterialStyle" style="width: 60px; height: 60px" :src="item.img" @click="buttonlFn()" />
                    </el-form-item>
                    <!-- <el-form-item label="导航链接" v-if="item.link"> -->
                    <link-select :link="item.link" />
                    <!-- </el-form-item> -->
                    <!-- <el-form-item v-if="item.link && item.link.type !== 6">
                        <span style="color: #9797a1">{{ item.link.name }}</span>
                    </el-form-item> -->
                </el-form>
            </div>
        </div>
        <!-- <q-upload class="homeSwiperForm-add" @change="handleImgChange">
            <span>+ 添加背景图</span>
            <p style="margin-top: 5px">建议宽度750像素</p>
        </q-upload> -->
        <div class="selectMaterialStyle" style="margin-bottom: 10px" @click="buttonlFn('img')">
            <span class="selectMaterialStyle__span"
                >+ 添加背景图 <br />
                <span style="font-size: 12px; color: #a7a7a7; margin-left: -3px">建议宽度750像素</span>
            </span>
        </div>
        <div class="homeSwiperForm-form">
            <el-form label-position="left" :model="formData" label-width="70px">
                <el-form-item label="边距">
                    <el-row :gutter="15">
                        <!-- <el-slider v-model="formData.padding"></el-slider> -->
                        <el-slider v-model="formData.padding" :show-tooltip="false" show-input :max="30"></el-slider>
                    </el-row>
                </el-form-item>
                <el-form-item label="高度">
                    <el-row :gutter="15">
                        <el-radio-group v-model="heightType" @change="heightTypeChanged">
                            <el-radio :value="1">不铺满(最高1200)</el-radio>
                            <el-radio :value="2">铺满全屏</el-radio>
                        </el-radio-group>
                        <el-slider
                            v-if="heightType === 1"
                            v-model="formData.height"
                            :show-tooltip="false"
                            show-input
                            :max="1200"
                            :min="300"
                        ></el-slider>
                    </el-row>
                </el-form-item>
                <el-form-item label="图片样式">
                    <el-radio-group v-model="formData.imageStyle">
                        <el-radio :value="1">常规</el-radio>
                        <el-radio :value="2">投影</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="图片倒角">
                    <el-radio-group v-model="formData.imageAngle">
                        <el-radio :value="1">直角</el-radio>
                        <el-radio :value="2">圆角</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="指示器">
                    <el-radio-group v-model="formData.indicator">
                        <el-radio :value="1">样式一</el-radio>
                        <el-radio :value="2">样式二</el-radio>
                        <el-radio :value="3">样式三</el-radio>
                    </el-radio-group>
                </el-form-item>
            </el-form>
        </div>
    </div>
    <!-- 选择素材 e -->
    <selectMaterial
        :upload-files="1"
        :dialog-visible="dialogVisible"
        @select-material-fn="selectMaterialFn"
        @cropped-file-change="croppedFileChange"
        @checked-file-lists="checkedFileLists"
    />
    <!-- 选择素材 d -->
</template>

<style lang="scss" scoped>
@include b(homeSwiperForm-item) {
    position: relative;
    display: flex;
    border: 1px solid #e4e4e4;
    padding: 10px;
    margin-bottom: 10px;
    @include e(right) {
        flex: 1;
        padding-left: 10px;
    }
    @include e(uploader) {
        width: 80px;
        height: 80px;
        display: block;
        .el-upload {
            border: 1px dashed #d9d9d9;
            border-radius: 6px;
            cursor: pointer;
            position: relative;
            overflow: hidden;
        }
        .el-upload:hover {
            border-color: #409eff;
        }
    }

    @include e(img) {
        width: 80px;
        height: 80px;
        display: block;
    }

    @include e(plus) {
        font-size: 28px;
        color: #8c939d;
        width: 80px;
        height: 80px;
        line-height: 80px;
        text-align: center;
    }

    .remove__swiper--item {
        display: none;
    }

    &:hover {
        .remove__swiper--item {
            display: block;
            position: absolute;
            font-size: 20px;
            right: -8px;
            top: -8px;
            width: 20px;
            height: 20px;
            z-index: 20;
            color: #e4e4e4;
            background-color: #fff;
            cursor: pointer;
        }
    }
}

@include b(homeSwiperForm-add) {
    width: 100%;
    height: 100px;
    margin-bottom: 10px;
    :deep(.el-upload) {
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        width: inherit;
        height: 100px;
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

@include b(homeSwiperForm-form) {
    border: 1px solid #e4e4e4;
    padding: 10px;
}
@include b(selectMaterialStyle) {
    width: 360px;
    height: 100px;
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
        color: #3088f0;
        line-height: 24px;
    }
}
</style>
