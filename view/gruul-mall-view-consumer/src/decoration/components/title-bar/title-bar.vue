<script setup lang="ts">
import type { PropType } from 'vue'
import linkNavTo from '@/libs/linkNavTo'
import type { TitleFormData } from '../types'

const $props = defineProps({
  decorationProperties: {
    type: Object as PropType<TitleFormData>,
    default() {
      return {}
    },
  },
})

const handleClick = () => {
  if ($props.decorationProperties.link?.url) {
    linkNavTo($props.decorationProperties.link)
  }
}
</script>

<template>
  <view
    :style="
      'color:' +
      $props.decorationProperties.color +
      ';background-color:' +
      $props.decorationProperties.backgroundColor +
      ';text-align:center; font-size: 14px;font-weight: 800;position:relative'
    "
  >
    <view
      v-if="$props.decorationProperties.showStyle === 'is-style-one'"
      class="title"
      :style="{ color: $props.decorationProperties.color, background: $props.decorationProperties.backgroundColor }"
      >{{ $props.decorationProperties.titleName }}</view
    >
    <view v-else class="spellpre__header" :style="'color: ' + $props.decorationProperties.color" @tap.stop="handleClick">
      <text class="spellpre__header--title">{{ $props.decorationProperties.titleName }}</text>
      <text class="spellpre__header--more">查看更多 ></text>
      <view class="add__line"></view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
@include b(spellpre) {
  box-sizing: border-box;
  background-color: #fff;

  @include e(header) {
    color: #333;
    height: 40px;
    line-height: 40px;
    position: relative;
    text-align: center;

    @include m(title) {
      float: left;
      font-size: 18px;
      font-weight: 700;
      margin-left: 20px;
    }

    @include m(more) {
      font-size: 14px;
      font-weight: 400;
      float: right;
      margin-right: 20px;
    }

    .add__line {
      height: 100%;
      width: 4px;
      background-color: red;
      box-sizing: border-box;
      position: absolute;
      left: 0;
      top: 0;
    }
  }
}

@include b(contact-style) {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  opacity: 0;
}
@include b(title) {
  height: 80rpx;
  text-align: center;
  line-height: 80rpx;
}
</style>
