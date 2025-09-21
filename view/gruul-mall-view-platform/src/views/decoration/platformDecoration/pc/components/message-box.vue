<script setup lang="ts">
import { WarningFilled, Close } from '@element-plus/icons-vue'

withDefaults(
    defineProps<{
        title: string
        message: string
        width?: number
        min?: boolean
    }>(),
    {
        width: 480,
        min: false,
    },
)

const emit = defineEmits(['confirm'])

const dialogVisible = ref(false)

const open = () => {
    dialogVisible.value = true
}

const close = () => {
    dialogVisible.value = false
}

const confirm = () => {
    dialogVisible.value = false
    emit('confirm')
}

defineExpose({
    open,
})
</script>

<template>
    <el-dialog
        v-model="dialogVisible"
        :width="width"
        :close-on-click-modal="false"
        :close-on-press-escape="false"
        :show-close="false"
        append-to-body
        :class="{ min }"
        class="decoration-message-box"
    >
        <div class="title">
            <span class="title__icon">
                <el-icon :size="22" style="top: 1px" class="m-r-8" color="#F49D07"><WarningFilled /></el-icon>
                {{ title }}
            </span>

            <el-icon class="cp" @click="close"><Close /></el-icon>
        </div>
        <div class="message">{{ message }}</div>
        <template #footer>
            <div class="dialog-footer">
                <el-button @click="close">取消</el-button>
                <el-button type="primary" @click="confirm"> 确定 </el-button>
            </div>
        </template>
    </el-dialog>
</template>

<style lang="scss" scoped>
@include b(title) {
    display: flex;
    justify-content: space-between;
    font-size: 16px;
    color: #000000;

    @include e(icon) {
        display: flex;
        align-items: center;
    }
}

@include b(message) {
    margin-top: 24px;
    margin-left: 30px;
    color: #4e5969;
    font-size: 13px;
    height: 48px;
}
</style>

<style lang="scss">
@include b(min) {
    .el-dialog__body {
        padding-bottom: 0 !important;
    }
}

@include b(decoration-message-box) {
    .el-dialog__header {
        display: none;
    }
    .el-dialog__body {
        padding: 24px;
    }
    .el-dialog__footer {
        padding: 0px 24px 24px;
        .el-button {
            --el-button-font-weight: 400;
        }
    }
}
</style>
