<script lang="ts" setup>
import { defineProps, defineEmits } from 'vue'
import { useVModel } from '@vueuse/core'
import { secondsKillStatus } from '../index'

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
    type: Object,
    default: () => ({}),
  },
})
const emit = defineEmits(['update:modelValue', 'change'])
const modelValue = useVModel(props, 'modelValue', emit)
</script>

<template>
  <el-select v-model="modelValue" :placeholder="props.placeholder" style="width: 150px" @change="emit('change', $event)">
    <slot></slot>
    <el-option v-for="(item, key) in Object.values(props.list).length ? props.list : secondsKillStatus" :key="key" :label="item" :value="key" />
  </el-select>
</template>

<style lang="scss" scoped></style>
