<script setup lang="ts">
import { ref } from 'vue'

const $props = defineProps({
  time: { type: String, default: '' },
  status: { type: String, default: '' },
  context: { type: String, default: '' },
  bgColor: { type: String, default: '' },
})
const $emit = defineEmits(['click'])
</script>

<template>
  <view class="tracking" :style="{ background: $props.bgColor }" @click="$emit('click')">
    <view class="tracking__left">
      <q-icon name="icon-dingdanxiangqing-wuliu" size="20px" />
      <view class="tracking__left-text">
        <text class="tracking__left--info">
          <text v-show="$props.status">[{{ $props.status }}]</text>
          {{ $props.context }}
        </text>
        <text v-show="$props.time" class="tracking__left--date">{{ $props.time }}</text>
      </view>
    </view>
    <q-icon name="icon-xiajiantou" class="tracking__icon" size="20px" />
  </view>
</template>

<style scoped lang="scss">
@include b(tracking) {
  box-sizing: border-box;
  @include flex(space-between);
  padding: 20rpx;
  border-bottom: 1px solid #f5f5f5;
  @include e(left) {
    @include flex(flex-start, flex-start);
    @include m(info) {
      font-size: 26rpx;
      color: #36bb28;
    }
    @include m(date) {
      font-size: 24rpx;
      color: #959595;
    }
  }
  @include e(left-text) {
    @include flex(space-between, flex-start);
    flex-direction: column;
  }
  @include e(icon) {
    transform: rotate(-90deg);
  }
}
</style>
