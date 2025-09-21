<template>
  <view v-if="show" class="tip"> {{ text }} </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
const props = defineProps({
  text: {
    type: String,
    default: '',
  },
  item: {
    type: Object,
    default: () => ({}),
  },
})
const emit = defineEmits(['hideMuted'])
const show = ref(true)
setTimeout(() => {
  emit('hideMuted', props.item.ID)
}, 1000)
</script>

<style scoped>
.tip {
  padding: 0 48rpx;
  height: 108rpx;
  font-size: 24rpx;
  background: rgba(0, 0, 0, 0.9);
  color: #ffffff;
  position: fixed;
  top: 40%;
  left: 50%;
  transform: translateX(-50%);
  border-radius: 12rpx;
  line-height: 108rpx;
  text-align: center;
}
</style>
