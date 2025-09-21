<script setup lang="ts">
import { defineProps, defineEmits, PropType } from 'vue'
import { useVModel } from '@vueuse/core'
import SchemaForm from '@/components/SchemaForm.vue'

export interface Operation {
  setMealStatus: string
  keyword: string
}

const props = defineProps({
  modelValue: {
    type: Object as PropType<Operation>,
    default() {
      return {}
    },
  },
})
// 表单配置项
const columns = [
  {
    label: '活动名称',
    prop: 'keyword',
    valueType: 'copy',
    fieldProps: {
      placeholder: '请输入活动名称',
    },
  },
  {
    label: '活动状态',
    prop: 'setMealStatus',
    valueType: 'select',
    options: [
      { label: '全部状态', value: '' },
      { label: '未开始', value: 'NOT_STARTED' },
      { label: '进行中', value: 'PROCESSING' },
      { label: '已结束', value: 'OVER' },
      { label: '已下架', value: 'MERCHANT_SELL_OFF' },
      { label: '违规下架', value: 'ILLEGAL_SELL_OFF' },
    ],
    fieldProps: {
      placeholder: '请选择',
    },
  },
]
const emit = defineEmits(['update:modelValue', 'search'])
const _modelValue = useVModel(props, 'modelValue', emit)

const handleSearch = () => {
  emit('search')
}
const resetHandle = () => {
  Object.keys(_modelValue.value).forEach((key) => (_modelValue.value[key] = ''))
  emit('search')
}
</script>

<template>
  <SchemaForm v-model="_modelValue" :columns="columns" @searchHandle="handleSearch" @handleReset="resetHandle"> </SchemaForm>
</template>

<style scoped></style>
