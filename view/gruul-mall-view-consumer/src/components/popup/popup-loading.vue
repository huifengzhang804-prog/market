<script setup lang="ts">
const props = defineProps({
  show: {
    type: Boolean,
    default: false,
  },
})
</script>

<template>
  <view class="loading">
    <u-loading :show="props.show" mode="flower" size="100" />
  </view>
</template>

<style scoped lang="scss">
@include b(loading) {
  @include flex;
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
}
</style>
