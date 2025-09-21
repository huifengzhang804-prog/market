<script setup lang="ts">
import { defineProps, defineEmits, PropType } from 'vue'
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
    <el-option v-for="(item, key) in props.list" :key="key" :label="item.label" :value="item.value" />
  </el-select>
</template>

<style scoped lang="scss"></style>
