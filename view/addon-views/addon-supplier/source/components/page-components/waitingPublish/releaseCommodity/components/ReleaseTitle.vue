<script lang="ts" setup>
withDefaults(defineProps<{ title: string }>(), {
  title: '',
})
</script>

<template>
  <div class="title">
    <div class="title__release">
      {{ title }}
    </div>
    <slot></slot>
  </div>
</template>

<style lang="scss" scoped>
@include b(title) {
  @include flex(flex-start, center);
  @include e(release) {
    height: 20px;
    color: #333;
    font-size: 16px;
    line-height: 20px;
    border-left: 4px solid #4b80ff;
    padding-left: 8px;
  }
}
</style>
