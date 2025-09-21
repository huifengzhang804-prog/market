<script setup lang="ts">
import { useVModel } from '@vueuse/core'
import defaultSearchData from './search'
import { ElMessage } from 'element-plus'
import type { PropType } from 'vue'
const $props = defineProps({
    formData: {
        type: Object as PropType<typeof defaultSearchData>,
        default: defaultSearchData,
    },
})
const $emit = defineEmits(['update:formData'])
const formData = useVModel($props, 'formData', $emit)

// const handleAdd = () => {
//     if (formData.value.hotWord.length === 10) {
//         ElMessage.warning('搜索热词最多为10个！')
//         return
//     }
//     formData.value.hotWord.push('预设搜索热词')
// }
// const handleRemove = (i) => {
//     if (formData.value.hotWord.length === 1) {
//         ElMessage.warning('搜索热词最少为1个！')
//         return
//     }
//     formData.value.hotWord.splice(i, 1)
// }
</script>

<template>
    <el-form :model="formData" label-width="100px">
        <el-form-item label="是否展示">
            <el-radio-group v-model="formData.show">
                <el-radio :value="true">展示</el-radio>
                <el-radio :value="false">不展示</el-radio>
            </el-radio-group>
        </el-form-item>
        <el-form-item label="预设关键字">
            <el-input v-model="formData.keyWord" :maxlength="8" placeholder="最多显示8个字" clearable></el-input>
        </el-form-item>
        <el-form-item label="字体颜色">
            <el-color-picker v-model="formData.color" />
        </el-form-item>
        <el-form-item label="背景颜色">
            <el-color-picker v-model="formData.background" />
        </el-form-item>
        <el-form-item label="边框颜色">
            <el-color-picker v-model="formData.borderColor" />
        </el-form-item>
        <el-form-item label="按钮颜色">
            <el-color-picker v-model="formData.btnBorderColor" />
        </el-form-item>
        <el-form-item label="按钮字体颜色">
            <el-color-picker v-model="formData.btnFontColor" />
        </el-form-item>
        <el-form-item label="搜索框高度">
            <el-slider v-model="formData.height" :show-tooltip="false" :show-input="true" :max="40" :min="30"></el-slider>
        </el-form-item>
        <el-form-item label="边框圆角">
            <el-slider v-model="formData.borderRadius" :show-tooltip="false" :show-input="true" :max="60"></el-slider>
        </el-form-item>
        <el-form-item label="按钮圆角">
            <el-slider v-model="formData.btnBorderRadius" :show-tooltip="false" :show-input="true" :max="60"></el-slider>
        </el-form-item>
    </el-form>
</template>

<style lang="scss" scoped>
.item {
    display: flex;
    margin-bottom: 5px;
    align-items: center;
}
.icon {
    cursor: pointer;
}
</style>
