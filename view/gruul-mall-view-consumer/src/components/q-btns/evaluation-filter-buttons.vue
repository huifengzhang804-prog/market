<script setup lang="ts">
import { ref, reactive, computed } from 'vue'

const $props = defineProps({
  totalCount: { type: [String, Number], required: true },
  contentCount: { type: [String, Number], required: true },
  mediaCount: { type: [String, Number], required: true },
})
const btnlist = reactive([
  { name: '全部', label: computed(() => $props.totalCount), key: '' },
  { name: '有内容', label: computed(() => $props.contentCount), key: 'CONTENT' },
  { name: '有图片', label: computed(() => $props.mediaCount), key: 'IMAGE' },
])
const active = ref(0)
const $emit = defineEmits(['click'])

const handleClick = (index: number, currentItem: { name: string; label: string | number; key: string }) => {
  active.value = index
  $emit('click', currentItem)
}
</script>

<template>
  <view class="tags">
    <view
      v-for="(item, index) in btnlist"
      :key="index"
      class="tags__tag"
      :class="{ 'is-active': active === index }"
      @click="handleClick(index, item)"
    >
      {{ item.name }}({{ item.label }})
    </view>
  </view>
</template>

<style scoped lang="scss">
@include b(tags) {
  display: flex;
  padding: 34rpx 36rpx;
  background: #fff;
  @include e(tag) {
    min-width: 136rpx;
    margin-right: 15rpx;
    padding: 0 5rpx;
    height: 58rpx;
    background: #fdeeeb;
    border-radius: 30rpx;
    font-size: 26rpx;
    font-weight: normal;
    text-align: CENTER;
    color: #333333;
    line-height: 58rpx;
  }
  @include when(active) {
    color: #ffffff;
    background: #ff2e27;
  }
}
</style>
