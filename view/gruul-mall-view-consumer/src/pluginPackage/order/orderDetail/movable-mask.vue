<script setup lang="ts">
import { ref, reactive } from 'vue'
import QIcon from '@/components/q-icon/q-icon.vue'

const $props = defineProps({
  // 距离顶部的距离
  topDistance: { type: Number, default: 45 },
  // 距离底部的距离
  bottomDistance: { type: Number, default: 50 },
  x: { type: Number, default: 600 },
  y: { type: Number, default: 480 },
})
const height = ref(750)
const move = reactive({
  x: $props.x,
  y: $props.y,
  old: {
    x: 0,
    y: 0,
  },
})

uni.getSystemInfo({
  success: (res) => {
    height.value = res.windowHeight - $props.bottomDistance
  },
  fail(result) {
    uni.showToast({
      title: `${result}`,
      icon: 'none',
    })
  },
})
function wx_h5_compatibleWith(e: number) {
  // #ifdef H5
  return e
  // #endif
  // #ifndef H5
  // eslint-disable-next-line no-unreachable
  return 0
  // #endif
}
function onChange(e: { detail: { x: number; y: number } }) {
  move.old.x = e.detail.x
  move.old.y = e.detail.y
}
</script>

<template>
  <movable-area class="movable-area" :style="{ height: height + 'px', top: wx_h5_compatibleWith(topDistance) + 'px' }">
    <movable-view class="movable-view" :x="move.x" :y="move.y" direction="all" @on-change="onChange">
      <slot><q-icon name="icon-shouye" size="50rpx"></q-icon></slot>
    </movable-view>
  </movable-area>
</template>

<style scoped lang="scss">
$all_width: 96rpx;
$all_height: 96rpx;
.movable-area {
  width: 750rpx;
  position: fixed;
  pointer-events: none; //此处要加，鼠标事件可以渗透
  .movable-view {
    width: $all_width;
    height: $all_height;
    pointer-events: auto; //恢复鼠标事件
  }
}
</style>
