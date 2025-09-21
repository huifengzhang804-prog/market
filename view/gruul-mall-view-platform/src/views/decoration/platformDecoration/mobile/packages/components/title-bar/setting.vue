<script setup lang="ts">
import { useVModel } from '@vueuse/core'
import LinkSelect from '@/components/link-select/link-select.vue'
import defaultTitleBarData from './title-bar'
import type { PropType } from 'vue'

const $props = defineProps({
    formData: {
        type: Object as PropType<typeof defaultTitleBarData>,
        default() {
            return defaultTitleBarData
        },
    },
})
const $emit = defineEmits(['update:formData'])
const formData = useVModel($props, 'formData', $emit)
const showStyles = [
    {
        label: '样式一',
        value: 'is-style-one',
    },
    {
        label: '样式二',
        value: 'is-style-two',
    },
]
</script>

<template>
    <el-form :model="formData" label-width="100px">
        <el-form-item label="选择样式">
            <el-radio-group v-model="formData.showStyle">
                <el-radio v-for="item in showStyles" :key="item.value" :value="item.value">{{ item.label }}</el-radio>
            </el-radio-group>
        </el-form-item>
        <el-form-item label="标题名称">
            <el-input v-model="formData.titleName" :maxlength="10" placeholder="请输入标题名称"></el-input>
        </el-form-item>
        <el-form-item label="背景颜色">
            <el-color-picker v-model="formData.backgroundColor"></el-color-picker>
        </el-form-item>
        <el-form-item label="文字颜色">
            <el-color-picker v-model="formData.color"></el-color-picker>
        </el-form-item>
        <el-form-item v-if="formData.showStyle === 'is-style-two'" label="跳转路径">
            <link-select :inner="true" :link="formData.link"></link-select>
            <!-- <div v-if="formData.link.name">
                <span style="color: #9797a1">{{ formData.link.name }}</span>
            </div> -->
        </el-form-item>
    </el-form>
</template>

<style lang="scss" scoped></style>
