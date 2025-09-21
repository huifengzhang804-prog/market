<script setup lang="ts">
const btnOptions = [
  {
    color: '#fff',
    backgroundColor: '#333333',
    text: '加入购物车',
    type: 'JOINCART',
  },
  {
    color: '#fff',
    backgroundColor: '#DE3224',
    text: '立即购买',
    type: 'BUY',
  },
]
const $emit = defineEmits(['btnClick'])
</script>

<template>
  <view class="btns-container">
    <view
      v-for="(option, index) in btnOptions"
      :key="index"
      :style="{ backgroundColor: option.backgroundColor, color: option.color }"
      class="item"
      :class="{ 'buy-now': btnOptions.length === 1, black_: index === 0 }"
      @click="$emit('btnClick', option.type)"
    >
      {{ option.text }}
    </view>
  </view>
</template>

<style scoped lang="scss">
@include b(btns-container) {
  @include flex;
  margin: 20rpx 0;
}
@include b(item) {
  width: 50%;
  text-align: center;
  background: #f54319;
  color: #fff;
  height: 80rpx;
  line-height: 80rpx;
  border-top-right-radius: 6rpx;
  border-bottom-right-radius: 6rpx;
}
@include b(black_) {
  background: #606266;
  border-top-left-radius: 6rpx;
  border-bottom-left-radius: 6rpx;
  border-top-right-radius: 0;
  border-bottom-right-radius: 0;
}
@include b(buy-now) {
  width: 100%;
  border-radius: 6rpx;
  background: #f54319;
}
</style>
