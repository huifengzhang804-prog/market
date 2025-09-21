<script setup lang="ts">
import { ref, defineProps, defineEmits, PropType } from 'vue'
import { Search } from '@element-plus/icons-vue'
import QDropdownBtn from '@/components/q-btn/q-dropdown-btn.vue'
import { useVModel } from '@vueuse/core'

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
  btnOption: { type: Array as PropType<{ label: string; name: string }[]>, default: () => [] },
  batchDisabled: {
    type: Boolean,
    default: false,
  },
  selectOption: {
    type: Array as PropType<{ label: string; value: string }[]>,
    default: () => [],
  },
  showLeftBtn: {
    type: Boolean,
    default: true,
  },
  leftBtnText: {
    type: String,
    default: '批量上架',
  },
})
const emit = defineEmits([
  'update:modelValue',
  'search',
  'batchLeftClick',
  'batchSellDel',
  'batchSellOff',
  'addIntegralGoods',
  'batchDelivery',
  'statusChange',
])
const _modelValue = useVModel(props, 'modelValue', emit)

/**
 * 批量上下架
 */
const handleDropdownRight = (e: 'DELETE' | 'SELL_OFF' | 'DELIVERY') => {
  console.log('e', e)
  switch (e) {
    case 'DELETE':
      emit('batchSellDel', e)
      break
    case 'SELL_OFF':
      emit('batchSellOff', e)
      break
    case 'DELIVERY':
      emit('batchDelivery', e)
      break
    default:
      break
  }
}
</script>

<template>
  <el-row :gutter="24" justify="space-between" style="margin-bottom: 15px">
    <el-col :span="12">
      <el-space v-if="showLeftBtn">
        <el-button round :disabled="$props.batchDisabled" type="primary" @click="emit('addIntegralGoods')">新增积分商品</el-button>
      </el-space>
      <el-space>
        <slot name="btn">
          <q-dropdown-btn
            size="default"
            round
            :disabled="$props.batchDisabled"
            :title="leftBtnText"
            :option="props.btnOption"
            @left-click="emit('batchLeftClick')"
            @right-click="handleDropdownRight"
          >
          </q-dropdown-btn>
        </slot>
      </el-space>
    </el-col>
    <el-col :span="6">
      <el-space>
        <el-select v-model="_modelValue.status" @change="emit('statusChange')">
          <el-option v-for="item in selectOption" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-space>
    </el-col>
    <el-col :span="6">
      <el-input v-model="_modelValue.keywords" placeholder="输入关键词">
        <template #append>
          <el-button :icon="Search" @click="emit('search')" />
        </template>
      </el-input>
    </el-col>
  </el-row>
</template>

<style scoped></style>
