<script setup lang="ts">
import { ref, watch } from 'vue'

const $props = defineProps({
  modelValue: {
    type: Boolean,
    defalut: false,
  },
  borderRadius: {
    type: String,
    default: '30rpx',
  },
  background: {
    type: String,
    default: '#fff',
  },
})
const emit = defineEmits(['update:modelValue', 'close'])

const livePusherLayout = ref({
  width: 0,
  height: 0,
})
uni.getSystemInfo({
  success: (res) => {
    const { windowWidth, windowHeight } = res
    livePusherLayout.value.width = windowWidth
    livePusherLayout.value.height = windowHeight
  },
})
watch(
  () => $props.modelValue,
  (val) => {
    console.log(' $props.modelValue', val)
  },
  {
    immediate: true,
  },
)

const handleClick = () => {
  emit('close')
  emit('update:modelValue', false)
}
</script>

<template>
  <view
    class="goods_popup"
    :style="{
      width: livePusherLayout.width + 'px',
      height: livePusherLayout.height + 'px',
      transform: `translateY(${modelValue ? 0 : livePusherLayout.height}px)`,
    }"
    @click="handleClick"
  >
    <view
      :style="{ borderRadius: borderRadius, width: livePusherLayout.width + 'px', background: background }"
      class="goods_popup_context"
      @click.stop="() => {}"
    >
      <slot>
        <view> 启山科技 </view>
      </slot>
    </view>
  </view>
</template>

<style scoped>
.goods_popup {
  /* transform: translateY(100rpx); */
  color: #fff;
  transition: transform 0.3s;
  z-index: 999;
  position: fixed;
  top: 0;
  bottom: 0;
}
.goods_popup_context {
  padding: 20rpx;
  position: absolute;
  bottom: 0;
  z-index: 999;
}
</style>
