<script setup lang="ts">
import { ref, type PropType, watch, computed } from 'vue'
import type { FormType } from '@/pluginPackage/order/confirmOrder/types'

const $props = defineProps({
  formType: { type: String as PropType<FormType>, required: true },
  placeholder: { type: String, default: '' },
  modelValue: { type: String, default: '' },
})
const $emit = defineEmits(['update:modelValue'])
const showCalendar = ref(false)
const paramsConfig = computed(() => {
  if ($props.formType === 'DATE') {
    return {
      year: true,
      month: true,
      day: true,
      hour: false,
      minute: false,
      second: false,
    }
  }
  if ($props.formType === 'TIME') {
    return {
      year: false,
      month: false,
      day: false,
      hour: true,
      minute: true,
      second: true,
    }
  }
  return {
    year: true,
    month: true,
    day: true,
    hour: true,
    minute: true,
    second: true,
  }
})

const handleChooseDate = () => {
  showCalendar.value = true
}
const confirm = (e: { year: string; month: string; day: string; hour: string; minute: string; second: string }) => {
  if ($props.formType === 'DATE') {
    const { day, month, year } = e
    $emit('update:modelValue', `${year}-${month}-${day}`)
    return
  }
  if ($props.formType === 'TIME') {
    const { hour, minute, second } = e
    $emit('update:modelValue', `${hour}:${minute}:${second}`)
    return
  }
  if ($props.formType === 'DATETIME') {
    const { hour, minute, second, day, month, year } = e
    console.log(`${year}-${month}-${day} ${hour}:${minute}:${second}`)
    $emit('update:modelValue', `${year}-${month}-${day} ${hour}:${minute}:${second}`)
    return
  }
}
const cancel = () => {
  $emit('update:modelValue', '')
}
</script>

<template>
  <view class="date-choose" @click="handleChooseDate">
    <text v-if="!$props.modelValue" style="color: #c0c4cc">{{ $props.placeholder }}</text>
    <text class="date-choose__value">{{ $props.modelValue }}</text>
  </view>
  <u-picker v-model="showCalendar" mode="time" :params="paramsConfig" cancel-text="重置" @confirm="confirm" @cancel="cancel" />
</template>

<style scoped lang="scss">
@include b(date-choose) {
  width: 100%;
  display: flex;
  @include e(value) {
    color: #409eff;
  }
}
</style>
