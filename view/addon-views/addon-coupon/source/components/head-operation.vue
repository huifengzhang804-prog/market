<script lang="ts" setup>
import { defineProps, defineEmits, PropType } from 'vue'
import { useVModel } from '@vueuse/core'
import SchemaForms from '@/components/SchemaForm.vue'

export interface Operation {
  status: string
  type: string
  // date: string
  keywords: string
}

// 表单配置项
const columns = [
  {
    label: '活动名称',
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
    options: [
      {
        label: '全部状态',
        value: '',
      },
      {
        label: '未开始',
        value: 'NOT_OPEN',
      },
      {
        label: '进行中',
        value: 'OPEN',
      },
      {
        label: '已结束',
        value: 'CLOSED',
      },
      {
        label: '已下架',
        value: 'SHOP_BANED',
      },
      {
        label: '违规下架',
        value: 'BANED',
      },
    ],
    fieldProps: {
      placeholder: '请选择',
    },
  },
  {
    label: '优惠券类型',
    labelWidth: '90',
    prop: 'type',
    valueType: 'select',
    options: [
      {
        label: '全部优惠劵',
        value: '',
      },
      {
        label: '无门槛折扣券',
        value: 'PRICE_DISCOUNT',
      },
      {
        label: '无门槛现金券',
        value: 'PRICE_REDUCE',
      },
      {
        label: '满折券',
        value: 'REQUIRED_PRICE_DISCOUNT',
      },
      {
        label: '满减券',
        value: 'REQUIRED_PRICE_REDUCE',
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
  Object.keys(_modelValue.value as KnownType).forEach((key) => ((_modelValue.value as KnownType)[key] = ''))
  emit('search')
}
</script>

<template>
  <SchemaForms v-model="_modelValue" :columns="columns" @searchHandle="emit('search')" @handleReset="handleReset" />
  <div class="grey_bar"></div>
</template>
