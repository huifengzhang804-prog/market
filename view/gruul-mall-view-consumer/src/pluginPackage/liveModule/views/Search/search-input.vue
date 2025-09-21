<script setup lang="ts">
import { ref } from 'vue'

const $props = defineProps({
  height: {
    type: Number,
    default: 122,
  },
  isGoods: { type: Boolean, default: true },
  modelValue: {
    type: String,
    default: '',
  },
  placeholder: { type: String, default: '请填写关键词' },
})
const emit = defineEmits(['change-label', 'update:modelValue', 'custom', 'focus', 'change', 'blur', 'search'])
const value = ref($props.modelValue)

const custom = (e: string) => {
  emit('update:modelValue', e)
  emit('custom', e)
}
const focus = (e: any) => {
  emit('focus', e)
}
const change = (e: string) => {
  emit('update:modelValue', e)
  emit('change', e)
}
const blur = (e: any) => {
  emit('blur', e)
}
const search = (e: string) => {
  emit('update:modelValue', e)
  emit('search', e)
}
function setValue(val: string) {
  value.value = val
}
defineExpose({ setValue })
</script>

<template>
  <view class="search_top" :style="{ height: height + 'rpx' }">
    <view style="display: flex; align-items: center" @click="emit('change-label')">
      {{ isGoods ? '商品' : '直播' }}
      <view style="padding: 0 10rpx">
        <q-icon name="icon-qiehuan" size="30rpx" />
      </view>
    </view>
    <u-search
      v-model="value"
      shape="round"
      :placeholder="placeholder"
      action-text="搜索"
      bg-color="#F6F7F9"
      @custom="custom"
      @focus="focus"
      @change="change"
      @blur="blur"
      @search="search"
    />
  </view>
</template>

<style scoped lang="scss">
@include b(search_top) {
  padding: 0 40rpx;
  height: 122rpx;
  @include flex;
  background: #fff;
  justify-content: space-between;
}
</style>
