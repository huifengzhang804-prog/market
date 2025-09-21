<script setup lang="ts">
import { ref, PropType, watch } from 'vue'
import RemarkImage from './components/remark-image.vue'
import RemarkDate from './components/remark-date.vue'
import type { ApishopDealSetting } from './types/index'

const $props = defineProps({
  recordErrGoods: { type: String, default: '' },
  idx: { type: Number, default: 0 },
  shopId: { type: String, default: '' },
  shopDealSetting: { type: Object as PropType<ApishopDealSetting>, required: true },
  shopRemark: { type: Object as PropType<{ [key: string]: { [key: string]: string }[] }>, required: true },
})

const formRef = ref()

/**
 * @description: 输入框表单判断 公民身份证 数量
 */
function isInputItem(type: string): boolean {
  return ['MOBILE', 'CITIZEN_ID', 'TEXT', 'NUMBER', 'REMARK'].indexOf(type) !== -1
}
/**
 * @description: 时间表单判断
 */
function isDateItem(type: string): boolean {
  return ['DATE', 'DATETIME', 'TIME'].indexOf(type) !== -1
}

const shopRemarkRef = ref<{ [key: string]: { [key: string]: string }[] } | any>({})
const $emit = defineEmits(['update:modelValue'])
watch(
  $props,
  () => {
    if ($props.shopId) {
      shopRemarkRef.value = $props.shopRemark[$props.shopId]
    }
  },
  { deep: true },
)
const remarkDateChange = (e: string, index: number, key: string) => {
  shopRemarkRef.value[index][key] = e
  $emit('update:modelValue', shopRemarkRef.value)
}
const remarkImageChange = (e: string, index: number, key: string) => {
  shopRemarkRef.value[index][key] = e
  $emit('update:modelValue', shopRemarkRef.value)
}
const remarkInputChange = (index: number, key: string) => {
  $emit('update:modelValue', shopRemarkRef.value)
}
</script>

<template>
  <el-form ref="formRef" :model="shopDealSetting[$props.idx]">
    <el-form-item
      v-for="(item, inde) in $props.shopDealSetting[$props.shopId]"
      :key="item.key + new Date().getTime()"
      :label="item.key"
      :prop="item.key"
      :required="item.required"
      :show-message="false"
      :border-bottom="!($props.shopDealSetting[$props.shopId].length - 1 === inde)"
    >
      <el-input
        v-if="isInputItem(item.type)"
        v-model.trim="shopRemarkRef[inde][item.key]"
        :placeholder="item.placeholder"
        :maxlength="item.type === 'MOBILE' ? 11 : 140"
        @blur="remarkInputChange(inde, item.key)"
      />
      <remark-date
        v-else-if="isDateItem(item.type)"
        :model-value="shopRemarkRef[inde][item.key]"
        :form-type="item.type"
        @update:model-value="(e) => remarkDateChange(e, inde, item.key)"
      />
      <remark-image
        v-else
        :model-value="shopRemarkRef[inde][item.key]"
        class="form__item"
        @update:model-value="(e) => remarkImageChange(e, inde, item.key)"
      />
    </el-form-item>
  </el-form>
</template>

<style lang="scss" scoped>
@include b(form) {
  font-size: 28upx;
  color: #000;
  @include e(item) {
    width: 100%;
  }
  @include e(item-right) {
  }
}
:deep(.el-form-item__label) {
  justify-content: start;
}
:deep(.el-form-item__label) {
  min-width: 70px; /* 设置最小宽度为 auto */
  max-width: auto; /* 设置最大宽度为 100px */
}
</style>
