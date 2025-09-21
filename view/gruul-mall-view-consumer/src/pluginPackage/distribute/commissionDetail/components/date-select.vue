<script setup lang="ts">
import { computed } from 'vue'
import QIcon from '@/components/q-icon/q-icon.vue'

const $emit = defineEmits(['click'])
const $props = defineProps({
  queryTime: { type: String, default: '' },
})

const initQueryTime = computed(() => {
  if (!$props.queryTime) return
  return $props.queryTime.replace('-', '年') + '月'
})
</script>

<template>
  <view class="date-select" @click="$emit('click')">
    <view class="date-select__month"
      ><text>{{ initQueryTime }}</text
      ><q-icon name="icon-xiajiantou" color="#000" />
    </view>
    <!-- <view class="date-select__detail">
        
        </view> -->
  </view>
</template>

<style scoped lang="scss">
@include b(date-select) {
  @include flex;
  justify-content: space-between;
  padding: 30rpx 46rpx 30rpx 13rpx;
  @include e(detail) {
    color: rgba(153, 153, 153, 1);
    font-size: 24rpx;
    & > text:nth-child(1) {
      margin-right: 5rpx;
    }
  }
  @include e(month) {
    @include flex;
    color: rgba(16, 16, 16, 1);
    font-size: 26rpx;
  }
}
</style>
