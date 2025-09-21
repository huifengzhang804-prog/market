<script lang="ts" setup>
import { defineProps, defineEmits, PropType } from 'vue'
import { useVModel } from '@vueuse/core'
import SchemaForm from '@/components/SchemaForm.vue'

export interface Operation {
  status: string
  type: string
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
  showBatchBtn: {
    type: Boolean,
    default: true,
  },
})
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
    label: '优惠券状态',
    labelWidth: 95,
    prop: 'status',
    valueType: 'select',
    options: [
      {
        value: '',
        label: '全部状态',
      },
      {
        value: 'NOT_OPEN',
        label: '未开始',
      },
      {
        value: 'OPEN',
        label: '进行中',
      },
      {
        value: 'CLOSED',
        label: '已结束',
      },
      {
        value: 'SHOP_BANED',
        label: '已下架',
      },
      {
        value: 'BANED',
        label: '违规下架',
      },
    ],
    fieldProps: {
      placeholder: '请选择',
    },
  },
  {
    label: '优惠券类型',
    labelWidth: 95,
    prop: 'type',
    valueType: 'select',
    options: [
      {
        value: '',
        label: '全部优惠券',
      },
      {
        value: 'PRICE_DISCOUNT',
        label: '无门槛折扣券',
      },
      {
        value: 'PRICE_REDUCE',
        label: '无门槛现金券',
      },
      {
        value: 'REQUIRED_PRICE_DISCOUNT',
        label: '满折券',
      },
      {
        value: 'REQUIRED_PRICE_REDUCE',
        label: '满减券',
      },
    ],
    fieldProps: {
      placeholder: '请选择',
    },
  },
]
const emit = defineEmits(['update:modelValue', 'delCoupon', 'search'])
const _modelValue = useVModel(props, 'modelValue', emit)
const resetHandle = () => {
  Object.keys(_modelValue.value).forEach((key) => (_modelValue.value[key] = ''))
  emit('search')
}
</script>

<template>
  <el-config-provider :empty-values="[undefined, null]">
    <SchemaForm v-model="_modelValue" :columns="columns" @searchHandle="emit('search')" @handleReset="resetHandle">
      <template #otherOperations>
        <el-button v-if="showBatchBtn" :disabled="$props.batchDisabled" round @click="emit('delCoupon')">批量移除</el-button>
      </template>
    </SchemaForm>
  </el-config-provider>
</template>

<style scoped>
.flex {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}
</style>
