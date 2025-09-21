<script setup lang="ts">
import liveRoom from './live'
import { ElMessage } from 'element-plus'
import { useVModel } from '@vueuse/core'
import type { PropType } from 'vue'
const $props = defineProps({
    formData: {
        type: Object as PropType<typeof liveRoom>,
        default() {
            return liveRoom
        },
    },
})
const $emit = defineEmits(['update:formData'])
const formData = useVModel($props, 'formData', $emit)
</script>

<template>
    <el-form>
        <el-form-item label="列表标题">
            <el-input v-model="formData.listTitle" :maxlength="14" show-word-limit />
        </el-form-item>
        <el-form-item label="展示直播数量">
            <el-input-number v-model="formData.listNum" step-strictly :max="12" :min="1" />
        </el-form-item>
        <el-form-item label="列表样式">
            <el-row :gutter="24" style="width: 100%">
                <el-radio-group v-model="formData.codeStyle">
                    <el-col :span="8">
                        <el-radio :value="1">大图模式</el-radio>
                    </el-col>
                    <el-col :span="8">
                        <el-radio :value="2">一行两个</el-radio>
                    </el-col>
                    <el-col :span="8"> <el-radio :value="3">横向滑动</el-radio></el-col>
                </el-radio-group>
            </el-row>
        </el-form-item>
        <el-form-item label="页面边距">
            <el-row :gutter="24" style="width: 100%">
                <el-col :span="12"> <el-slider v-model="formData.listMargin" :max="30" :min="0" /></el-col>
                <el-col :span="12"> <el-input-number v-model="formData.listMargin" step-strictly :max="30" :min="0" /></el-col>
            </el-row>
        </el-form-item>
        <el-form-item label="上下间距">
            <el-row :gutter="24" style="width: 100%">
                <el-col :span="12"> <el-slider v-model="formData.upDownMargin" :max="30" :min="0" /></el-col>
                <el-col :span="12">
                    <el-input-number v-model="formData.upDownMargin" step-strictly :max="30" :min="0" />
                </el-col>
            </el-row>
        </el-form-item>
        <el-form-item label="组件圆角">
            <el-radio-group v-model="formData.borderRadius">
                <el-radio :value="true">圆角</el-radio>
                <el-radio :value="false">直角</el-radio>
            </el-radio-group>
        </el-form-item>
    </el-form>
</template>

<style lang="scss" scoped></style>
