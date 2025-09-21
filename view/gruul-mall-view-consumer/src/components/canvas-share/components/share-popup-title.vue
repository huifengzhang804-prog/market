<script setup lang="ts">
const $emit = defineEmits(['click'])
const $props = defineProps({
  text: { type: String, default: '选择退款方式' },
  icon: {
    type: String,
    default: 'close',
  },
})
</script>

<template>
  <view class="title">
    <text>{{ $props.text }}</text>
    <slot name="icon">
      <u-icon class="title--item" color="#000" :name="$props.icon" size="30rpx" @click="$emit('click')" />
    </slot>
  </view>
</template>

<style scoped lang="scss">
@include b(title) {
  position: relative;
  height: 100rpx;
  font-size: 30rpx;
  text-align: center;
  color: #000;
  font-weight: 700;
  line-height: 100rpx;
  @include m(item) {
    position: absolute;
    right: 40rpx;
    top: 50%;
    transform: translateY(-50%);
  }
}
</style>
