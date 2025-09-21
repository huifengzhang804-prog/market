<template>
  <scroll-view style="height: 500rpx" scroll-y>
    <u-grid :col="10" :border="false">
      <u-grid-item v-for="emoji in emojis" :key="emoji" @click="$emit('expressionSelect', emoji)">
        <text class="emoji--item">{{ emoji }}</text>
      </u-grid-item>
    </u-grid>
  </scroll-view>
</template>

<script setup lang="ts">
import { emojis } from './expression'

const $emit = defineEmits(['expressionSelect'])
</script>

<style scoped lang="scss">
@include b(emoji) {
  display: flex;
  flex-wrap: wrap;
  @include m(item) {
    font-size: 40rpx;
  }
}
</style>
