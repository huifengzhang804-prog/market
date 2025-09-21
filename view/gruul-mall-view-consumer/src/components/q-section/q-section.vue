<script setup lang="ts">
import QIcon from '@/components/q-icon/q-icon.vue'

const $props = defineProps({
  title: {
    type: String,
    default: '协商历史',
  },
  subTitle: {
    type: String,
    default: '',
  },
  line: { type: Boolean, default: false },
  customStyle: {
    type: Object,
    default() {
      return {}
    },
  },
  rightIcon: {
    type: Boolean,
    default: true,
  },
})
const $emit = defineEmits(['click'])
</script>

<template>
  <view class="qSection" :style="$props.customStyle" @click="$emit('click')">
    <view class="qSection__title">{{ $props.title }}</view>
    <slot></slot>
    <view v-if="$props.subTitle" class="qSection__sub">{{ $props.subTitle }}</view>
    <!-- <u-icon v-else name="arrow-right" label-size="0" /> -->
    <q-icon v-if="rightIcon" name="icon-youjiantou_huaban1" color="#333" style="flex-shrink: 0" />
  </view>
  <view v-if="$props.line" class="line" />
</template>

<style scoped lang="scss">
@include b(qSection) {
  width: 100vw;
  height: 80rpx;
  background: #fff;
  padding: 0 20rpx;
  font-size: 26rpx;
  color: rgb(51, 51, 51);
  @include flex(space-between);
  @include e(title) {
    flex-shrink: 0;
  }
  @include e(sub) {
    margin-left: 30rpx;
    flex: 1;
  }
}
@include b(line) {
  width: 100%;
  height: 0;
  border-bottom: 1px solid #f4f4f4;
}
</style>
