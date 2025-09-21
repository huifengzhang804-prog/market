<script setup lang="ts"></script>

<template>
  <view class="q_time_line">
    <slot></slot>
  </view>
</template>

<style lang="scss" scoped>
.q_time_line {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
}
</style>
