<script setup lang="ts">
import type { PropType } from 'vue'
type Context = { area: string[]; areaName: string; context: string; ftime: string; status: string; time: string }

const $props = defineProps({
  context: {
    type: Array as PropType<Context[]>,
    default: () => [{ area: [] as string[], areaName: '', context: '', ftime: '', status: '', time: '' }],
  },
  time: { type: String, default: '' },
  loading: {
    type: Boolean,
    default: false,
  },
})
</script>

<template>
  <view v-if="$props.context[0].context" class="logistics">
    <u-time-line class="logistics__time-line">
      <u-time-line-item v-for="(item, index) in $props.context" :key="index" node-top="6">
        <template #content>
          <view :class="{ active: index === 0 }">
            <view class="u-order-title">{{ item.status }}</view>
            <view class="u-order-desc">
              {{ item.context }}
            </view>
            <view class="u-order-time">{{ item.time }}</view>
          </view>
        </template>
      </u-time-line-item>
    </u-time-line>
  </view>
  <view v-else-if="loading" style="text-align: center" class="logistics">
    <u-loading mode="flower" size="60"></u-loading>
    <view style="margin-top: 20rpx; color: #999"> 加载中。。。 </view>
  </view>
  <u-empty v-else class="full-screen-empty" text="暂无物流信息" mode="address"></u-empty>
</template>

<style scoped lang="scss">
.full-screen-empty {
  height: 40vh;
}
@include b(logistics) {
  background: #fff;
  overflow-y: scroll;
  padding: 40rpx;
}
@include b(active) {
  color: #fe4e63;
}
</style>
