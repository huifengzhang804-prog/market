<script setup lang="ts">
import { PropType } from 'vue'
import { useVModel } from '@vueuse/core'

const props = defineProps({
  modelValue: {
    type: String,
    default: '',
  },
  placeholder: {
    type: String,
    default: '',
  },
  list: {
    type: Array as PropType<{ label: string; value: string }[]>,
    default: () => [{ label: '1', value: '2' }],
  },
})
const emit = defineEmits(['update:modelValue', 'change'])
const modelValue = useVModel(props, 'modelValue', emit)
</script>

<template>
  <el-select v-model="modelValue" :placeholder="props.placeholder" style="width: 150px" @change="emit('change', $event)">
    <slot></slot>
    <el-option v-for="(item, index) in props.list" :key="index" :label="item.label" :value="item.value" />
  </el-select>
</template>

<style scoped lang="scss"></style>
