<script setup lang="ts">
import type { PropType } from 'vue'
import type { Joint_Live_List_Status } from '@/pluginPackage/liveModule/apis/CreateLive/model'
import { statusConfig } from './config'

defineProps({
  model: {
    type: String as PropType<Joint_Live_List_Status>,
    default: 'NOT_STARTED',
  },
})
</script>

<template>
  <view
    :style="{
      backgroundColor: statusConfig[model].bg,
    }"
    class="tag"
  >
    {{ statusConfig[model].text }}
  </view>
</template>

<style scoped lang="scss">
.tag {
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
  width: 101rpx;
  height: 36rpx;
  border-radius: 56rpx;
  color: #fff;
}
</style>
