<script setup lang="ts">
import blankPaceholder from './blankPaceholder'
import { useVModel } from '@vueuse/core'
import type { PropType } from 'vue'

const $props = defineProps({
    formData: {
        type: Object as PropType<typeof blankPaceholder>,
        default() {
            return blankPaceholder
        },
    },
})
const $emit = defineEmits(['update:formData'])
const formData = useVModel($props, 'formData', $emit)

const predefineColors = ref([
    '#ff4500',
    '#ff8c00',
    '#ffd700',
    '#90ee90',
    '#00ced1',
    '#1e90ff',
    '#c71585',
    'rgba(255, 69, 0, 0.68)',
    'rgb(255, 120, 0)',
    'hsv(51, 100, 98)',
    'hsva(120, 40, 94, 0.5)',
    'hsl(181, 100%, 37%)',
    'hsla(209, 100%, 56%, 0.73)',
    '#c7158577',
])
const handleRadioChange = () => {
    formData.value.height = 0
}
</script>

<template>
    <el-form :model="formData" label-position="top">
        <el-form-item label="占位高度设置">
            <el-radio-group v-model="formData.isExtendStatusBarHeight" @change="handleRadioChange">
                <el-radio :label="true">自适应(可根据手机型号调整状态栏高度，适用于页面顶部设计)</el-radio>
                <div style="width: 100%; display: flex">
                    <el-col :span="6">
                        <el-radio :label="false">自定义</el-radio>
                    </el-col>
                    <el-slider
                        v-if="!formData.isExtendStatusBarHeight"
                        v-model="formData.height"
                        style="width: 150px"
                        :max="100"
                        :min="0"
                        :show-tooltip="false"
                    ></el-slider>
                    <div v-if="!formData.isExtendStatusBarHeight" style="width: 80px; margin-left: 15px; color: rgb(96, 98, 102); font-size: 14px">
                        {{ formData.height }}像素
                    </div>
                </div>
            </el-radio-group>
        </el-form-item>
        <!-- 背景色 -->
        <el-form-item label="背景色">
            <el-color-picker v-model="formData.backgroundColor" show-alpha :predefine="predefineColors" />
        </el-form-item>
    </el-form>
</template>

<style lang="scss" scoped></style>
