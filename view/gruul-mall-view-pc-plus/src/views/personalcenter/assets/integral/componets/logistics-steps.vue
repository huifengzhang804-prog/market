<script setup lang="ts">
import { PropType } from 'vue'
type Context = { areaCode: string; areaName: string; context: string; ftime: string; status: string; time: string }

const $props = defineProps({
  context: {
    type: Array as PropType<Context[]>,
    default: () => [{ areaCode: '', areaName: '', context: '', ftime: '', status: '', time: '' }],
  },
  time: { type: String, default: '' },
  loading: {
    type: Boolean,
    default: false,
  },
})
</script>

<template>
  <div v-if="$props.context[0].context" class="logistics">
    <el-steps direction="vertical" class="logistics__time-line">
      <el-steps v-for="(item, index) in $props.context" :key="index" node-top="6">
        <template #content>
          <div :class="{ active: index === 0 }">
            <div class="u-order-title">
              {{ item.status }}
            </div>
            <div class="u-order-desc">
              {{ item.context }}
            </div>
            <div class="u-order-time">
              {{ item.time }}
            </div>
          </div>
        </template>
      </el-steps>
    </el-steps>
  </div>
  <div v-else-if="loading" style="text-align: center" class="logistics">
    <u-loading mode="flower" size="60" />
    <div style="margin-top: 20rpx; color: #999">加载中 . . .</div>
  </div>
  <u-empty v-else class="full-screen-empty" text="暂无物流信息" mode="address" />
</template>

<style scoped lang="scss">
@include b(logistics) {
  background: #fff;
  overflow-y: scroll;
  padding: 40rpx;
}
@include b(active) {
  color: #fe4e63;
}
</style>
