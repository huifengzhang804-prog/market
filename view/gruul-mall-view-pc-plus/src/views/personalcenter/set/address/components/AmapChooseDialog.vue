<script setup lang="ts">
import { Checked, Coordinate } from '@/apis/address/types'
import AmapChoose from './AmapChoose.vue'
import { useVModel } from '@vueuse/core'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false,
  },
  initLocation: {
    type: Array as unknown as PropType<Coordinate>,
    default: () => [],
  },
})
const emit = defineEmits(['placeChoose', 'update:modelValue'])
const modelValue = useVModel(props, 'modelValue', emit)

const checked = ref<Checked>({
  longitude: '',
  latitude: '',
  address: '',
  name: '',
})
const placeChoosed = (place: Checked) => {
  checked.value = place
}
const cancel = () => {
  checked.value = {
    longitude: '',
    latitude: '',
    address: '',
    name: '',
  }
  modelValue.value = false
}
const confrom = () => {
  emit('placeChoose', checked.value)
  cancel()
}
</script>

<template>
  <el-dialog v-model="modelValue" title="定位地址" width="1314">
    <AmapChoose v-if="modelValue" :initLocation="initLocation" @placeChoose="placeChoosed"></AmapChoose>
    <template #footer>
      <div class="ccenter">
        <el-button @click="modelValue = false">取 消</el-button>
        <el-button type="primary" @click="confrom">确 定</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<style lang="scss" scoped></style>
