<script setup lang="ts">
defineProps({
  list: {
    type: Array as PropType<Array<any>>,
    default: () => [],
  },
})
</script>

<template>
  <el-steps direction="vertical">
    <el-step v-for="(item, index) in list" :key="index" :title="item.status" :description="item.context + '' + item.time">
      <template #icon>
        <slot name="icon"></slot>
      </template>
    </el-step>
  </el-steps>
</template>

<style lang="scss" scoped></style>
