<script setup lang="ts">
import { ref } from 'vue'

const conditions = [
  { label: '全部', name: '' },
  { label: '收入', name: 'INCREASE' },
  { label: '支出', name: 'REDUCE' },
  { label: '筛选', name: '' },
]
const activeIndex = ref(0)
const $emit = defineEmits(['click'])

const handleClickConditions = (
  index: number,
  item: {
    label: string
    name: string
  },
) => {
  if (index !== 3) {
    activeIndex.value = index
  }
  $emit('click', item)
}
</script>

<template>
  <view class="conditions">
    <view
      v-for="(item, index) in conditions"
      :key="item.label"
      class="conditions__item"
      :class="{ activeIndex: activeIndex === index }"
      @click="handleClickConditions(index, item)"
      >{{ item.label }}</view
    >
  </view>
</template>

<style scoped lang="scss">
@include b(conditions) {
  padding: 15rpx 0;
  background: #fff;
  @include flex;
  @include e(item) {
    width: 25%;
    text-align: center;
    font-size: 26rpx;
    color: #000;
    &:last-child {
      @include flex;
      &::after {
        content: '';
        height: 0;
        width: 0;
        border-left: 10rpx transparent solid;
        border-right: 10rpx transparent solid;
        border-top: 10rpx solid #000;
        margin-left: 5rpx;
      }
    }
  }
}
@include b(activeIndex) {
  font-weight: bold;
  color: red;
  &::after {
    border-top-color: red !important;
  }
}
</style>
