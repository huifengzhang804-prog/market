<script setup lang="ts">
import type { PropType } from 'vue'
import QIcon from '@/components/q-icon/q-icon.vue'

const $props = defineProps({
  modelValue: {
    type: Boolean,
    default() {
      return false
    },
  },
  stockModalData: {
    type: Object as PropType<any>,
    default: () => ({
      data: { title: '', content: '' },
    }),
  },
})
const emit = defineEmits(['update:modelValue', 'init'])
const cancelFn = () => {
  // TODO:添加一个init方法
  emit('update:modelValue', false)
  emit('init')
}
</script>

<template>
  <u-modal
    :model-value="$props.modelValue"
    :title-style="{ color: 'red' }"
    :show-title="false"
    confirm-color="#333333"
    confirm-text="我知道了"
    @cancel="cancelFn"
    @confirm="cancelFn"
  >
    <!-- title-s -->
    <view class="ordermodal-title u-border-bottom">
      <q-icon name="icon-a-item_icon1" size="40rpx" color="red" />
      &nbsp;{{ stockModalData?.data?.title }}
    </view>
    <!-- title-e -->
    <!-- content-s -->
    <view class="ordermodal-content">{{ stockModalData?.data?.content }}</view>
    <!-- content-e -->
  </u-modal>
</template>

<style scoped lang="scss">
@include b(ordermodal-title) {
  @include flex;
  font-weight: 600;
  text-align: center;
  line-height: 88rpx;
}
@include b(ordermodal-content) {
  padding: 40rpx;
  font-size: 28rpx;
  text-align: center;
  bottom: 1px solid red;
}
</style>
