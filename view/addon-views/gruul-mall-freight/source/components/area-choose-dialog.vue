<script lang="ts" setup>
import { defineProps, defineEmits, computed, PropType } from 'vue'
import QAreaChoose from '@/components/q-area-choose/q-area-choose.vue'
import { useVModel } from '@vueuse/core'

const $props = defineProps({
  modelValue: {
    type: Boolean,
    default: false,
  },
  allArea: {
    type: Map as PropType<Map<string, number>>,
    default() {
      return new Map()
    },
  },
  currentArea: {
    type: Map as PropType<Map<string, number>>,
    default() {
      return new Map()
    },
  },
})
const $emit = defineEmits(['update:modelValue', 'choose'])
const show = useVModel($props, 'modelValue', $emit)
let currentRowArea = null
let newAllArea = null

const handleCloseDialog = () => {
  $emit('update:modelValue', false)
}
const handleConfirm = () => {
  $emit('update:modelValue', false)
  $emit('choose', {
    newAllArea,
    currentRowArea,
  })
}
const handleChoosedArea = (e) => {
  newAllArea = e
}
</script>

<template>
  <el-dialog v-model="show" @close="handleCloseDialog">
    <QAreaChoose :all-area="$props.allArea" :current-area="$props.currentArea" @choose="handleChoosedArea" />
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleCloseDialog">取消</el-button>
        <el-button type="primary" @click="handleConfirm">确定</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<style lang="scss" scoped></style>
