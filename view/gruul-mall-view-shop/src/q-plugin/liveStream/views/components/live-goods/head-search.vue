<script setup lang="ts">
import type { PropType } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { useVModel } from '@vueuse/core'
import SchemaForm from '@/components/SchemaForm.vue'

export interface Operation {
    keywords: string
}

const props = defineProps({
    modelValue: {
        type: Object as PropType<Operation>,
        default() {
            return {}
        },
    },
})

const columns = [
    {
        label: '活动名称',
        prop: 'keywords',
        valueType: 'copy',
        fieldProps: {
            placeholder: '请输入活动名称',
        },
    },
]

const handleReset = () => {
    emit('update:modelValue', {})
}

const emit = defineEmits(['update:modelValue', 'search'])
const _modelValue = useVModel(props, 'modelValue', emit)
</script>

<template>
    <SchemaForm v-model="_modelValue" :columns="columns" @searchHandle="emit('search')" @handleReset="handleReset" />
</template>

<style scoped lang="scss"></style>
