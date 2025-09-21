<script setup lang="ts">
import { ref, computed, inject } from 'vue'
import type { LvData } from '../../../apis/CreateLive/model'

const lv = inject('lvData') as LvData
let active = ref(0)
const list = [
  { lable: '美颜', value: 'skinCareData' },
  {
    lable: '美白',
    value: 'whiteningData',
  },
]

const isActive = computed(() => (index: number) => lv[list[active.value].value as keyof LvData].index === index)
const emit = defineEmits(['lv-change', 'close'])

const handleSkinCare = (e: number) => {
  active.value = e
}
const handleChooseLv = (index: number) => {
  lv[list[active.value].value as keyof LvData].index = index
  emit('lv-change', { [list[active.value].value]: index })
}
const reset = () => {
  lv[list[active.value].value as keyof LvData].index = 0
}
const confirm = () => {
  emit('close')
}
</script>

<template>
  <!-- <live-progress></live-progress> -->
  <view class="skin-care">
    <view class="skin-care__top">
      <view class="skin-care__top_left flex">
        <view v-for="(item, index) in list" :key="index" class="flex skin-care__top_left_box" @click="handleSkinCare(index)">
          <text class="skin-care--text" :style="{ fontWeight: active === index ? 700 : 400 }">{{ item.lable }}</text>
          <view v-if="active === index" class="skin-care--slider"></view>
          <view v-else class="skin-care--slider" style="background: transparent"></view>
        </view>
      </view>
      <view class="skin-care__top_right flex">
        <text class="skin-care--text" @click="reset">重置</text>
        <text class="skin-care--text" @click="confirm">确定</text>
      </view>
    </view>
    <view class="skin-care__footer">
      <!-- 美颜 -->
      <view v-for="(item, ind) in list" :key="ind" :style="{ flex: ind === active ? 1 : 0, width: 0 }" class="skin-care__footer_box">
        <text
          v-for="(i, index) in 10"
          :key="index"
          class="skin-care__footer--chunk"
          :style="{ color: isActive(index) ? '#fff' : '#000', background: isActive(index) ? 'red' : '#fff' }"
          @click="handleChooseLv(index)"
        >
          {{ index }}
        </text>
      </view>
      <!-- 美白 -->
    </view>
  </view>
</template>

<style scoped>
.flex {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-direction: row;
}
.skin-care {
  position: fixed;
  bottom: 0;
  width: 750rpx;
  height: 200rpx;
  background: #404040;
  z-index: 98;
}
.skin-care__top {
  width: 750rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-direction: row;
  height: 90rpx;
}
.skin-care__top_left {
  padding-left: 50rpx;
  width: 250rpx;
}
.skin-care__top_right {
  width: 250rpx;
  padding-right: 50rpx;
}
.skin-care--text {
  color: #fff;
  margin-bottom: 10rpx;
}
.skin-care__top_left_box {
  flex-direction: column;
}
.skin-care--slider {
  width: 65rpx;
  height: 5rpx;
  background: #fff;
  border-radius: 31rpx;
}
.skin-care__footer {
  width: 670rpx;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 0;
  margin: 0 40rpx;
  overflow: hidden;
}
.skin-care__footer--chunk {
  height: 56rpx;
  width: 56rpx;
  border-radius: 4rpx;
  background: #fff;
  text-align: center;
  line-height: 56rpx;
  font-size: 25rpx;
}
.skin-care__footer_box {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
  overflow: hidden;
}
</style>
