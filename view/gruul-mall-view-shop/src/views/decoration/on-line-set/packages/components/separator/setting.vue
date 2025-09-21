<script setup lang="ts">
import type { PropType } from 'vue'
import { useVModel } from '@vueuse/core'
import defaultSeparatorData from './separator'

const $props = defineProps({
    formData: {
        type: Object as PropType<typeof defaultSeparatorData>,
        default() {
            return defaultSeparatorData
        },
    },
})
const $emit = defineEmits(['update:formData'])
const formData = useVModel($props, 'formData', $emit)
const borderStyles = [
    {
        label: '实线',
        value: 'solid',
    },
    {
        label: '虚线',
        value: 'dashed',
    },
    {
        label: '点线',
        value: 'dotted',
    },
]

const handleChangeColor = () => {
    if (!formData.value.borderColor) {
        formData.value.borderColor = '#EEEEEE'
    }
}
</script>

<template>
    <el-form :model="formData">
        <el-form-item label="颜色">
            <el-color-picker v-model="formData.borderColor" @change="handleChangeColor" />
        </el-form-item>
        <el-form-item label="边距">
            <el-radio-group v-model="formData.hasMargin">
                <el-radio :value="false">无边距</el-radio>
                <el-radio :value="true">左右留边</el-radio>
            </el-radio-group>
        </el-form-item>
        <el-form-item label="样式">
            <el-radio-group v-model="formData.borderStyle">
                <el-radio v-for="item in borderStyles" :key="item.value" :label="item.value">{{ item.label }}</el-radio>
            </el-radio-group>
        </el-form-item>
    </el-form>
</template>

<style lang="scss" scoped></style>
