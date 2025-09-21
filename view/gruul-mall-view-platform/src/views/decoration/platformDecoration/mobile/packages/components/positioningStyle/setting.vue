<template>
    <el-form :model="formData" label-width="100px">
        <el-form-item label="展示数据">
            <el-select v-model="formData.showType">
                <el-option value="province" label="省市区" />
                <el-option value="city" label="市区" />
                <el-option value="cityDetails" label="市区定位地址" />
                <el-option value="details" label="定位地址" />
            </el-select>
        </el-form-item>
        <el-form-item label="地址颜色">
            <el-color-picker v-model="formData.color" />
        </el-form-item>
    </el-form>
</template>

<script setup lang="ts">
import type { PropType } from 'vue'
import positionStyleData from './positioningStyle'
import { useVModel } from '@vueuse/core'

const $props = defineProps({
    formData: {
        type: Object as PropType<typeof positionStyleData>,
        default: positionStyleData,
    },
})
const $emit = defineEmits(['update:formData'])
const formData = useVModel($props, 'formData', $emit)
</script>

<style scoped></style>
