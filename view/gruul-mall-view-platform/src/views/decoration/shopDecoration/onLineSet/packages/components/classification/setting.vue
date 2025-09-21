<script setup lang="ts">
import type { PropType } from 'vue'
import { useVModel } from '@vueuse/core'
import classification from './classification'
import type { DeCategoryType } from './classification'

const $props = defineProps({
    formData: {
        type: Object as PropType<DeCategoryType>,
        default() {
            return classification
        },
    },
})
const $emit = defineEmits(['update:formData'])
const formModel = useVModel($props, 'formData', $emit)
const dialogShow = ref(false)
// 类目拖动下角标
const classDragIndex = ref(-1)

// const handleSelectedCategory = (e) => {
//     formModel.value.categoryList = e
// }
// const handleDel = (index: number) => {
//     formModel.value.categoryList.splice(index, 1)
// }
/**
 * 开始拖动，记录拖动的组件下角标
 * @param {*} i
 */
const handleDragClass = (i: number) => {
    classDragIndex.value = i
}
/**
 *  阻止默认行为，否则drop事件不会触发
 */
const handleDragoverClass = (e: Event) => {
    e.preventDefault()
}
/**
 * 被放置的容器触发事件，交换两个组件的位置
 * @param {*} i
 */
const handleDropClass = (i: number) => {
    if (classDragIndex.value === i) {
        return false
    }
    const temp = formModel.value.categoryList.splice(classDragIndex.value, 1, formModel.value.categoryList[i])
    formModel.value.categoryList.splice(i, 1, ...temp)
}
</script>

<template>
    <el-form v-model="formModel">
        <el-form-item label="样式">
            <el-radio-group v-model="formModel.style">
                <el-radio :value="1">纯分类</el-radio>
                <el-radio :value="2">分类商品</el-radio>
                <el-radio :value="3">大图商品</el-radio>
                <el-radio :value="4">商品列表</el-radio>
                <el-radio :value="5">金刚区分类</el-radio>
            </el-radio-group>
        </el-form-item>
        <!-- <el-form-item v-if="formModel.style > 1" label="列表样式">
            <el-radio-group v-model="formModel.listStyle">
                <el-radio :value="1">大图模式</el-radio>
                <el-radio :value="2">列表模式</el-radio>
            </el-radio-group>
        </el-form-item>
        <el-form-item v-if="formModel.style > 1" label="购买按钮">
            <el-radio-group v-model="formModel.buyBtnStyle">
                <el-radio :value="1">样式一</el-radio>
                <el-radio :value="2">样式二</el-radio>
            </el-radio-group>
        </el-form-item>
        <el-form-item v-if="formModel.style > 1" label="商品边距">
            <el-slider v-model="formModel.goodsMargin" :show-tooltip="false" :show-input="true" :max="30"></el-slider>
        </el-form-item>
        <el-form-item label="字体选中">
            <el-color-picker v-model="formModel.fontSelected"></el-color-picker>
        </el-form-item>
        <el-form-item label="背景选中">
            <el-color-picker v-model="formModel.bgSelected"></el-color-picker>
        </el-form-item>
        <el-form-item label="字体未选">
            <el-color-picker v-model="formModel.fontNotSelected"></el-color-picker>
        </el-form-item>
        <el-form-item label="背景未选">
            <el-color-picker v-model="formModel.bgNotSelected"></el-color-picker>
        </el-form-item> -->
    </el-form>
</template>

<style lang="scss" scoped>
@include b(category) {
    width: 363px;
    box-sizing: border-box;
    border: 1px solid #ccc;
    border-radius: 4px;
    padding: 20px;
    font-size: 14px;
    @include e(header) {
        @include flex(space-between);
        color: #ccc;
        margin-bottom: 4px;
    }
    @include e(list) {
        min-height: 100px;
        max-height: 200px;
        overflow-y: auto;
    }
    @include e(item) {
        @include flex(space-between);
        margin-bottom: 3px;
        @include m(del) {
            color: #ff0000;
            cursor: pointer;
        }
    }
}
</style>
