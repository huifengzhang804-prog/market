<script setup lang="ts">
import { useVModel } from '@vueuse/core'

const props = defineProps({
    modelValue: {
        type: Number,
        required: true,
    },
    controls: {
        type: Boolean,
        default() {
            return true
        },
    },
    placeholder: { type: String, default: '' },
    disabled: {
        type: Boolean,
        default() {
            return false
        },
    },
    min: {
        type: Number,
        default: -Infinity,
    },
    max: {
        type: Number,
        default: Infinity,
    },
    // eslint-disable-next-line vue/require-default-prop
    precision: {
        type: Number,
        validator: (val: number) => val >= 0 && val === Number.parseInt(`${val}`, 10),
    },
    width: {
        type: [Number, String],
        default: '150',
    },
})
const emit = defineEmits(['update:modelValue'])
const _modelValue = useVModel(props, 'modelValue', emit)
</script>

<template>
    <div class="mo-input--number">
        <el-input-number
            v-model="_modelValue"
            :style="{ width: props.width + 'px' }"
            :controls="controls"
            :placeholder="placeholder"
            controls-position="right"
            :disabled="disabled"
            :min="min"
            :max="max"
            :precision="precision"
        >
        </el-input-number>
        <div class="define-append">
            <slot name="append">万元</slot>
        </div>
    </div>
</template>

<style lang="scss" scoped>
/* 自定义数字输入框append  */
.mo-input--number {
    border: 1px solid #dcdfe6;
    display: flex;
    border-radius: 4px;
    .el-input-number--mini {
        flex: 1;
    }
    :deep(.el-input__inner) {
        border: none !important;
    }
}

.define-append {
    width: 40px;
    display: inline-block;
    background: #f5f7fa;
    padding: 0px 3px;
    border-left: none;
    height: 32px;
    line-height: 32px;
    color: #909399;
    font-size: 12px;
    text-align: center;
}
</style>
