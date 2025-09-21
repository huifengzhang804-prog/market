<script setup lang="ts">
import { ref, type PropType, watch } from 'vue'
import storage from '@/utils/storage'
import type { CouponQueryStatusJointType, TagItem } from '@/apis/plugin/coupon/model'

const $props = defineProps({
  total: {
    type: Object as PropType<{ name: CouponQueryStatusJointType; total: number }>,
    default: () => ({ name: 'UNUSED', total: 0 }),
  },
  current: {
    type: Number,
    default: 0,
  },
})
const emit = defineEmits(['active'])
const tagList = ref<TagItem[]>([
  {
    name: '待使用',
    key: 'UNUSED',
  },
  {
    name: '已使用',
    key: 'USED',
  },
  {
    name: '已过期',
    key: 'EXPIRED',
  },
])
const currentKey = ref('UNUSED')

watch(
  () => $props.current,
  (val, old) => {
    if (val !== old) {
      currentKey.value = 'UNUSED'
    }
  },
)

function initText(item: TagItem) {
  const storageData = storage.get(item.key + $props.current)
  return item.key === storageData?.name ? item.name + (storageData.total ? storageData.total : '') : item.name
}
const handleClick = (item: TagItem) => {
  currentKey.value = item.key
  emit('active', item)
}
</script>

<template>
  <view class="choose-nav">
    <u-tag
      v-for="item in tagList"
      :key="item.name"
      :text="initText(item)"
      mode="dark"
      :bg-color="item.key === currentKey ? '#FCECED' : '#EEEEEE'"
      :color="item.key === currentKey ? '#F32F22' : '#333333'"
      :class="item.key === currentKey && 'active'"
      @click="handleClick(item)"
    />
  </view>
</template>

<style scoped lang="scss">
@include b(choose-nav) {
  display: flex;
  justify-content: space-around;
}
@include b(active) {
  font-weight: 700;
}
</style>
