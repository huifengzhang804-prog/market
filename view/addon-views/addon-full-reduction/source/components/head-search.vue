<script lang="ts" setup>
import { defineProps, defineEmits, PropType } from 'vue'
import { useVModel } from '@vueuse/core'
import { SearchParam } from '../index'
import SchemaForms from '@/components/SchemaForm.vue'

const props = defineProps({
  modelValue: {
    type: Object as PropType<SearchParam>,
    default() {
      return {}
    },
  },
})

// 表单配置项
const columns = [
  {
    label: '活动名称',
    prop: 'name',
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
        value: 'NOT_STARTED',
        label: '未开始',
      },
      {
        value: 'IN_PROGRESS',
        label: '进行中',
      },
      {
        value: 'FINISHED',
        label: '已结束',
      },
      {
        value: 'OFF_SHELF',
        label: '已下架',
      },
      {
        value: 'VIOLATION_OFF_SHELF',
        label: '违规下架',
      },
    ],
    fieldProps: {
      placeholder: '请选择',
    },
  },
]

const emit = defineEmits(['update:modelValue', 'search'])
const _modelValue = useVModel(props, 'modelValue', emit)
type KnownType = { [key: string]: any }

const handleReset = () => {
  Object.keys(_modelValue.value).forEach((key) => {
    ;(_modelValue.value as KnownType)[key] = ''
  })
  emit('search')
}
</script>

<template>
  <SchemaForms v-model="_modelValue" :columns="columns" @searchHandle="emit('search')" @handleReset="handleReset" />
  <div class="grey_bar"></div>
</template>

<style lang="scss" scoped>
@include b(col-last) {
  display: flex;
  justify-content: flex-end;
}

@include b(m-l-20) {
  margin-left: 20px;
}
</style>
