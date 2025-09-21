<script setup lang="ts">
import type { PropType } from 'vue'
import type { DeCategoryType } from '@/pages/platform/types'

const $props = defineProps({
  height: {
    type: Number,
    default: 1000,
  },
  config: {
    type: Object as PropType<DeCategoryType>,
    default() {
      return {}
    },
  },
  currentChooseIndex: {
    type: Number,
    default: 0,
  },
})
const $emit = defineEmits(['changeTab'])

const handleChangeTab = (index: number) => {
  $emit('changeTab', index)
}
</script>

<template>
  <scroll-view scroll-y class="slide" :style="{ height: $props.height + 'px' }">
    <view
      v-for="(item, index) in $props.config.categoryList"
      :key="item.platformCategoryFirstId"
      class="slide__unit"
      :style="{
        color: index === $props.currentChooseIndex ? '#FA3534' : '#000000',
        fontSize: index === $props.currentChooseIndex ? '32rpx' : '26rpx',
        fontWeight: index === $props.currentChooseIndex ? 'bold' : 'normal',
      }"
      :class="index === $props.currentChooseIndex ? 'br' : ''"
      @click="handleChangeTab(index)"
    >
      <!-- <div class="word">{{ item.platformCategoryFirstName }}</div> -->
      <!-- 截取前五个字符 -->
      <view class="word">
        <template v-if="item && item.platformCategoryFirstName!.length > 5">
          <text>{{ item.platformCategoryFirstName?.slice(0, 5) }}</text>
          <text>{{ item.platformCategoryFirstName?.slice(5) }}</text>
        </template>
        <text v-else>{{ item.platformCategoryFirstName }}</text>
      </view>
    </view>
  </scroll-view>
</template>

<style lang="scss" scoped>
@include b(slide) {
  width: 200rpx;
  overflow-y: auto;
  background: #f4f4f4;
  @include e(unit) {
    height: 110rpx;
    font-size: 14px;
    color: #333;
    position: relative;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    // border-radius: 0 18rpx 18rpx 0;
  }
  .word {
    width: 183rpx;
    overflow: hidden;
    display: flex;
    flex-direction: column;
    text-align: center;
  }
}
</style>
