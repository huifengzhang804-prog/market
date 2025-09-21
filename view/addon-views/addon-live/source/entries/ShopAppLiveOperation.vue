<template>
  <el-row :gutter="8">
    <el-col :span="8">
      <Live />
    </el-col>
    <el-col :span="8">
      <Audience />
    </el-col>
    <el-col :span="8">
      <Commodity />
    </el-col>
  </el-row>
</template>

<script lang="ts" setup>
import Live from '../components/operation/live/index.vue'
import Audience from '../components/operation/audience/index.vue'
import Commodity from '../components/operation/commodity/index.vue'
</script>

<style lang="scss" scoped>
.el-row {
  height: 600px;
}
</style>
