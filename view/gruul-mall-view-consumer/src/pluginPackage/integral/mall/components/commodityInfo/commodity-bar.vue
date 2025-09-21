<script setup lang="ts">
import { ref, computed } from 'vue'
import { doGetUserIntegralSystemtotal } from '@pluginPackage/integral/api'

const props = defineProps({
  integral: {
    type: String,
    default: '',
  },
})

const integralTotal = ref('0')
// 获取用户积分
initUserIntegralSystemtotal()
async function initUserIntegralSystemtotal() {
  const { code, data, msg } = await doGetUserIntegralSystemtotal()
  if (code !== 200 || (!data && data !== 0)) {
    uni.showToast({ title: `${msg || '获取用户积分失败'}`, icon: 'none' })
    return
  }
  integralTotal.value = data
}
const unActive = computed(() => +integralTotal.value < +props.integral)

const $emit = defineEmits(['exchange'])
const exchange = () => {
  if (unActive.value) return uni.showToast({ title: '您的积分不足，无法兑换商品！', icon: 'none' })
  $emit('exchange')
}
</script>

<template>
  <view class="bar buy-now" :class="{ unActive }" @click="exchange"> 立即兑换 </view>
  <view style="height: 100rpx"></view>
</template>

<style lang="scss" scoped>
@import '@/assets/css/mixins/mixins.scss';
@include b(bar) {
  position: fixed;
  width: 100vw;
  height: 98rpx;
  bottom: 0;
  background-color: #fff;
  z-index: 9;
  text-align: center;
  background: #f54319;
  color: #fff;
  line-height: 98rpx;
  border-radius: 6rpx;
  font-size: 38rpx;
}
@include b(buy-now) {
  width: 100%;
  border-radius: 6rpx;
  background: #f54319;
}
.unActive {
  background: #ccc;
  color: #999999;
}
</style>
