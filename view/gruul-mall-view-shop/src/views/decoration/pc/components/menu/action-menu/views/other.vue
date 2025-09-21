<script setup lang="ts">
import type { ComponentItem } from '../../../packages/index/formModel'

const startComponents = inject('startComponents', []) as ComponentItem[]
</script>

<template>
    <div class="other">
        <el-form ref="formRef">
            <el-form-item v-for="item in startComponents" :key="item.id" :label="item.label" class="other__switch">
                <el-switch v-model="item.show" />
            </el-form-item>
        </el-form>
    </div>
</template>

<style lang="scss" scoped>
@include b(other) {
    padding: 18px 13px;

    @include e(switch) {
        :deep(.el-form-item__content) {
            justify-content: flex-end;
            .el-form-item__error {
                padding-top: 0px;
                top: 96%;
            }
        }

        .el-switch {
            --el-switch-on-color: #f54319;
        }
    }
}

@include b(text) {
    color: #999;
    line-height: 24px;
}

@include b(el-form-item) {
    margin-bottom: 12px;

    :deep(.el-form-item__label) {
        font-size: 12px;
        padding-right: 6px;
    }

    :deep(.el-form-item__error) {
        padding-top: 0px;
        top: 95%;
    }
}

@include b(title) {
    font-size: 14px;
    color: #333;
    margin-bottom: 8px;
    font-weight: 700;
}

@include b(el-scrollbar) {
    margin-right: -13px;
    padding-right: 13px;
}
</style>
