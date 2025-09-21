<script setup lang="ts">
import { ref, defineProps, defineEmits, PropType } from 'vue'
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
const statusList = [
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
    options: statusList,
    fieldProps: {
      placeholder: '请选择',
    },
  },
]

const emit = defineEmits(['update:modelValue', 'batchDel', 'search'])
const _modelValue = useVModel(props, 'modelValue', emit)
type KnownType = { [key: string]: any }

const resetHandle = () => {
  Object.keys(_modelValue.value).forEach((key) => ((_modelValue.value as KnownType)[key] = ''))
  emit('search')
}
</script>

<template>
  <el-config-provider :empty-values="[undefined, null]">
    <schema-form v-model="_modelValue" :columns="columns" @searchHandle="emit('search')" @handleReset="resetHandle">
      <template #otherOperations>
        <el-button v-if="!showBatchDelBtn" :disabled="batchDisabled" round plain @click="emit('batchDel')">批量移除</el-button>
      </template>
    </schema-form>
  </el-config-provider>
</template>

<style scoped lang="scss"></style>
