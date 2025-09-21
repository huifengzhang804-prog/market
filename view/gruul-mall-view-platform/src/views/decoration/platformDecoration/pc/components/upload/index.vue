<script setup lang="ts">
import selectMaterial from '@/views/material/selectMaterial.vue'

import { Plus } from '@element-plus/icons-vue'

const props = defineProps({
    modelValue: {
        type: String,
        default: '',
    },
    width: {
        type: String,
        default: '194px',
    },
    height: {
        type: String,
        default: '62px',
    },
    iconSize: {
        type: String,
        default: '28px',
    },
    formRef: {
        type: Object,
        default: () => ({}),
    },
    validateKey: {
        type: String,
        default: 'img',
    },
})

// 上传url
const dialogVisible = ref(false)
const buttonlFn = () => {
    dialogVisible.value = true
}

/**
 * 选择
 */
const selectMaterialFn = (val: boolean) => {
    dialogVisible.value = val
}

const emit = defineEmits(['update:modelValue', 'change'])

/**
 * 裁剪完毕事件
 */
const croppedFileChange = (val: string) => {
    emit('update:modelValue', val)
    props.formRef?.clearValidate?.(props.validateKey)
}

/**
 * 选择完毕事件
 */
const checkedFileLists = (val: string[]) => {
    emit('update:modelValue', val[0] || '')

    props.formRef?.clearValidate?.(props.validateKey)
}
</script>

<template>
    <div class="uploader cp" :style="{ height, width }" @click="buttonlFn">
        <img v-if="modelValue" :src="modelValue" class="uploader__avatar" />
        <el-icon v-else class="uploader__icon" :size="iconSize" @click="buttonlFn"><Plus /></el-icon>
    </div>

    <!-- 选择素材 e -->
    <selectMaterial
        :dialog-visible="dialogVisible"
        :upload-files="1"
        @select-material-fn="selectMaterialFn"
        @cropped-file-change="croppedFileChange"
        @checked-file-lists="checkedFileLists"
    />
    <!-- 选择素材 d -->
</template>

<style scoped lang="scss">
@include b(uploader) {
    display: flex;
    align-items: center;
    justify-content: center;
    border: 1px solid rgb(220, 218, 226);
    border-radius: 2px;
    background: rgb(246, 246, 250);

    &:hover {
        border-color: var(--el-color-primary);
    }

    @include e(avatar) {
        display: block;
        height: 100%;
        width: 100%;
    }

    @include e(icon) {
        color: #8c939d;
        text-align: center;
    }
}
</style>
