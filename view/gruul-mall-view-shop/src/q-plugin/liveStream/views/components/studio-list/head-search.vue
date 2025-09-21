<script setup lang="ts">
import type { PropType } from 'vue'
import { useVModel } from '@vueuse/core'
import SchemaForm from '@/components/SchemaForm.vue'

export interface Operation {
    status: string
    liveRoomName: string
}

const columns = [
    {
        label: '活动名称',
        prop: 'liveRoomName',
        valueType: 'copy',
        fieldProps: {
            placeholder: '请输入活动名称',
        },
    },
    {
        label: '活动状态',
        prop: 'status',
        valueType: 'select',
        options: [
            {
                value: '',
                label: '全部状态',
            },
            {
                value: 'LIVE_BROADCAST',
                label: '进行中',
            },
            {
                value: 'NOT_STARTED',
                label: '未开始',
            },
            {
                value: 'CLOSED',
                label: '已结束',
            },
        ],
        fieldProps: {
            placeholder: '请选择',
        },
    },
]

const props = defineProps({
    modelValue: {
        type: Object as PropType<Operation>,
        default() {
            return {}
        },
    },
})

const emit = defineEmits(['update:modelValue', 'search'])
const _modelValue = useVModel(props, 'modelValue', emit)
type KnownType = { [key: string]: any }

const handleReset = () => {
    Object.keys(props.modelValue as KnownType).forEach((key) => ((props.modelValue as KnownType)[key] = ''))
    emit('search')
}
</script>

<template>
    <SchemaForm v-model="_modelValue" :columns="columns" @searchHandle="emit('search')" @handleReset="handleReset" />
</template>

<style scoped lang="scss"></style>
