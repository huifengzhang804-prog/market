<script setup lang="ts">
import { ref, PropType, watch, computed } from 'vue'
import { log } from 'console'

const $props = defineProps({
  formType: { type: String, required: true },
  modelValue: { type: String, default: '' },
})
const $emit = defineEmits(['update:modelValue'])
const showCalendar = ref('')
const paramsConfig = computed(() => {
  return $props.formType === 'DATE' ? { mode: 'date', 'value-format': 'YYYY-MM-DD' } : { mode: 'datetime', 'value-format': 'YYYY-MM-DD hh:mm:ss' }
})

const confirm = (e: number | string | Date | [number, number] | [string, string] | [Date, Date]) => {
  $emit('update:modelValue', e)
}
</script>

<template>
  <div>
    <el-time-picker v-if="formType === 'TIME'" v-model="showCalendar" value-format="hh:mm:ss" placeholder="请选择时间" @change="confirm" />
    <el-date-picker
      v-else
      v-model="showCalendar"
      placeholder="请选择时间"
      :value-format="paramsConfig['value-format']"
      :mode="paramsConfig.mode"
      @change="confirm"
    />
  </div>
</template>

<style scoped lang="scss"></style>
