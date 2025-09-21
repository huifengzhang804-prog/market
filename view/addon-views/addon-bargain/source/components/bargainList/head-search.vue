<script setup lang="ts">
import { defineProps, defineEmits, PropType } from 'vue'
import { useVModel } from '@vueuse/core'
import SchemaForms from '@/components/SchemaForm.vue'

export interface Operation {
  keyword: string
  status: string
}
const props = defineProps({
  modelValue: {
    type: Object as PropType<Operation>,
    default() {
      return {}
    },
  },
})
const fullReductionStatusList = [
  {
    value: '',
    label: '全部状态',
  },
  {
    value: 'NOT_STARTED',
    label: '未开始',
  },
  {
    value: 'PROCESSING',
    label: '进行中',
  },
  {
    value: 'OVER',
    label: '已结束',
  },
  {
    value: 'SHOP_SELL_OFF',
    label: '已下架',
  },
  {
    value: 'ILLEGAL_SELL_OFF',
    label: '违规下架',
  },
]
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
    prop: 'status',
    valueType: 'select',
    options: fullReductionStatusList,
    fieldProps: {
      placeholder: '请选择',
    },
  },
]

const emit = defineEmits(['update:modelValue', 'search'])
const _modelValue = useVModel(props, 'modelValue', emit)
type KnownType = { [key: string]: any }

const handleReset = () => {
  Object.keys(_modelValue.value as KnownType).forEach((key) => ((_modelValue.value as KnownType)[key] = ''))
  emit('search')
}
</script>

<template>
  <SchemaForms v-model="_modelValue" :columns="columns" @searchHandle="emit('search')" @handleReset="handleReset" />
  <div class="grey_bar"></div>
</template>

<style scoped lang="scss"></style>
