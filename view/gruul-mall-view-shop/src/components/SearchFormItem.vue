<template>
    <component
        :is="`el-${column?.valueType === 'copy' ? 'input' : column?.valueType}`"
        v-bind="{ ...handleSearchProps, ...placeholder, clearable }"
        ref="SearchFormItemRef"
        v-model.trim="_modelValue"
        :data="[]"
        :options="Array.isArray(column?.options) ? column?.options : column?.options?.value || []"
        :style="{ width: '100%' }"
    >
        <template v-if="column?.valueType === 'cascader'" #default="{ data }">
            <span>{{ data[fieldNames.label] }}</span>
        </template>
        <template v-if="column?.valueType === 'select'">
            <component
                :is="`el-option`"
                v-for="(col, index) in Array.isArray(column?.options) ? column?.options : column?.options?.value || []"
                :key="index"
                :label="col[fieldNames.label]"
                :value="col[fieldNames.value]"
            ></component>
        </template>
        <slot v-else></slot>
    </component>
</template>

<script setup lang="ts" name="SearchFormItem">
import { computed } from 'vue'
import { SearchProps } from './types'
import { useVModel } from '@vueuse/core'

interface SearchFormItem {
    column: SearchProps
    // 搜索表单绑定值
    modelValue: Obj
}
const props = defineProps<SearchFormItem>()
const $emit = defineEmits(['update:modelValue'])
const _modelValue = useVModel(props, 'modelValue', $emit)

const SearchFormItemRef = ref()
watch(
    () => _modelValue.value,
    () => {
        if (props.column?.valueType === 'cascader') {
            SearchFormItemRef.value?.togglePopperVisible()
        }
    },
)

const fieldNames = computed(() => {
    return {
        label: props.column?.fieldProps?.props?.label ?? 'label',
        value: props.column?.fieldProps?.props?.value ?? 'value',
    }
})

const handleSearchProps = computed(() => {
    return props.column?.fieldProps ?? {}
})

// 处理默认 placeholder
const placeholder = computed(() => {
    const search = props.column
    if (['datetimerange', 'daterange', 'monthrange'].includes(search?.fieldProps?.type) || search?.fieldProps?.isRange) {
        return {
            rangeSeparator: search?.fieldProps?.rangeSeparator ?? '至',
            startPlaceholder: search?.fieldProps?.startPlaceholder ?? '开始时间',
            endPlaceholder: search?.fieldProps?.endPlaceholder ?? '结束时间',
        }
    }
    const placeholder = search?.fieldProps?.placeholder ?? (search?.valueType?.includes('copy') ? '请输入' : '请选择') + (search?.label || '')
    return { placeholder }
})

// 是否有清除按钮 (当搜索项有默认值时，清除按钮不显示)
const clearable = computed(() => {
    const search = props.column
    return search?.fieldProps?.clearable ?? (search?.defaultValue === null || search?.defaultValue === void 0)
})
</script>
<style lang="scss" scoped>
/* 隐藏Element UI输入框类型为number时的增减按钮 */
:deep(.el-input__inner[type='number']::-webkit-inner-spin-button, .el-input__inner[type='number']::-webkit-outer-spin-button) {
    -webkit-appearance: none;
    margin: 0;
}
/* 对于Firefox */
:deep(.el-input__inner [type='number']) {
    -moz-appearance: textfield;
}
</style>
