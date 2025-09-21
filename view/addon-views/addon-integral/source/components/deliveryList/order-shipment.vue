<script setup lang="ts">
import { defineProps, defineEmits } from 'vue'
import { useVModel } from '@vueuse/core'
import NotShipment from './notShipment.vue'

const $props = defineProps({
  isShow: {
    type: Boolean,
    default: false,
  },
  currentNo: {
    type: String,
    default: '',
  },
})
const $emit = defineEmits(['update:isShow', 'upload-list'])
const _isShow = useVModel($props, 'isShow', $emit)

const handleDeliverDialogClose = () => {
  _isShow.value = false
}
</script>
<template>
  <el-dialog v-model="_isShow" width="700" :before-close="handleDeliverDialogClose" destroy-on-close>
    <template #header="{ titleId, titleClass }">
      <div class="my-header">
        <h4 :id="titleId" :class="titleClass">商品发货</h4>
      </div>
    </template>
    <not-shipment v-model:is-show="_isShow" :current-no="$props.currentNo" @upload-list="$emit('upload-list')" />
  </el-dialog>
</template>

<style lang="scss" scoped>
.demo-tabs > .el-tabs__content {
  padding: 32px;
  color: #6b778c;
  font-size: 32px;
  font-weight: 600;
}
</style>
