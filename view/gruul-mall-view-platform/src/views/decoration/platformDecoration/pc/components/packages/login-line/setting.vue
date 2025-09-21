<script setup lang="ts">
import { useVModel } from '@vueuse/core'
import loginLineData from './loginLine'
import type { PropType } from 'vue'

const props = defineProps({
    formData: {
        type: Object as PropType<typeof loginLineData>,
        default: loginLineData,
    },
})

const emit = defineEmits(['update:formData'])
const formData = useVModel(props, 'formData', emit)
</script>

<template>
    <el-form :model="formData" label-width="70">
        <el-form-item label="欢迎语">
            <el-input v-model="formData.message" maxlength="35" show-word-limit placeholder="请输入欢迎语~" clearable></el-input>
        </el-form-item>

        <el-form-item label="商家入驻">
            <el-switch v-model="formData.settledIn" />
        </el-form-item>
    </el-form>
</template>

<style lang="scss" scoped></style>
