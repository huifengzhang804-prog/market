<script setup lang="ts">
import { defineProps, defineEmits, PropType } from 'vue'
import { useVModel } from '@vueuse/core'
import SchemaForm from '@/components/SchemaForm.vue'

export interface Operation {
  status: string
  keywords: string
}

const props = defineProps({
  modelValue: {
    type: Object as PropType<Operation>,
    default() {
      return {}
    },
  },
  batchDisabled: {
    type: Boolean,
    default: true,
  },
  showBatchDelBtn: {
    type: Boolean,
    default: true,
  },
  selectOptions: {
    type: Object,
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
    value: 'OPENING',
    label: '未开始',
  },
  {
    value: 'OPEN',
    label: '进行中',
  },
  {
    value: 'FINISHED',
    label: '已结束',
  },
  {
    value: 'SHOP_OFF_SHELF',
    label: '已下架',
  },
  {
    value: 'VIOLATION',
    label: '违规下架',
  },
]
// 表单配置项
const columns = [
  {
    label: '活动名称',
    labelWidth: 75,
    prop: 'keywords',
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
const emit = defineEmits(['update:modelValue', 'batchDel', 'search'])
const _modelValue = useVModel(props, 'modelValue', emit)

const resetHandle = () => {
  Object.keys(_modelValue.value).forEach((key) => (_modelValue.value[key] = ''))
  emit('search')
}
</script>

<template>
  <el-config-provider :empty-values="[undefined, null]">
    <SchemaForm v-model="_modelValue" :columns="columns" style="margin-bottom: 30px" @searchHandle="emit('search')" @handleReset="resetHandle">
      <template #otherOperations>
        <el-button v-if="showBatchDelBtn" round plain @click="emit('batchDel')">批量移除</el-button>
      </template>
    </SchemaForm>
  </el-config-provider>
</template>

<style scoped lang="scss"></style>
