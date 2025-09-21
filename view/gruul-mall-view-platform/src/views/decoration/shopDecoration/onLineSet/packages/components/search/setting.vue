<script setup lang="ts">
import type { PropType } from 'vue'
import { useVModel } from '@vueuse/core'
import defaultSearchData from './search'
import { ElMessage } from 'element-plus'
const $props = defineProps({
    formData: {
        type: Object as PropType<typeof defaultSearchData>,
        default: defaultSearchData,
    },
})
const $emit = defineEmits(['update:formData'])
const formData = useVModel($props, 'formData', $emit)

const handleAdd = () => {
    if (formData.value.hotWord.length === 10) {
        ElMessage.warning('搜索热词最多为10个！')
        return
    }
    formData.value.hotWord.push('预设搜索热词')
}
const handleRemove = (i: number) => {
    if (formData.value.hotWord.length === 1) {
        ElMessage.warning('搜索热词最少为1个！')
        return
    }
    formData.value.hotWord.splice(i, 1)
}
</script>

<template>
    <el-form :model="formData" label-width="100px">
        <el-form-item label="预设关键字">
            <el-input v-model="formData.keyWord" :maxlength="8" placeholder="最多显示8个字" clearable></el-input>
        </el-form-item>
        <el-form-item label="热门搜索">
            <div v-for="(item, i) in formData.hotWord" :key="i" class="item">
                <el-icon><i-ep-remove class="icon" color="red" size="20" @click="handleRemove(i)" /></el-icon>
                <!-- <span class="el-icon-remove-outline icon" @click="handleRemove(i)"></span> -->
                <el-input v-model="formData.hotWord[i]" :maxlength="8" placeholder="最多显示8个字" clearable style="margin: 0 10px"></el-input>
                <el-icon><i-ep-circlePlus class="icon" color="blue" size="20" @click="handleAdd" /></el-icon>
                <!-- <span class="el-icon-circle-plus-outline icon" @click="handleAdd"></span> -->
            </div>
            <div>{{ `搜索热词关键字(${formData.hotWord.length}/10)` }}</div>
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
        <el-form-item label="边框圆角">
            <el-slider v-model="formData.borderRadius" :show-tooltip="false" :show-input="true" :max="30"></el-slider>
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
