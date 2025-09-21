<script lang="ts" setup>
withDefaults(defineProps<{ type: string; sale: number; scribe: number }>(), {
  type: '',
  sale: 0,
  scribe: 0,
})
</script>

<template>
  <template v-if="type === 'RATE'">
    <el-form-item prop="sale">
      <span>销售价 = 供货价 + （供货价 * &nbsp;</span>
      <el-input-number :model-value="sale" :controls="false" :precision="2" disabled />
      <span>&nbsp;% ）</span>
    </el-form-item>
    <el-form-item prop="scribe">
      <span>划线价 = 销售价 + （销售价 * &nbsp;</span>
      <el-input-number :model-value="scribe" :controls="false" :precision="2" disabled />
      <span>&nbsp;% ）</span>
    </el-form-item>
  </template>
  <template v-else-if="type === 'REGULAR'">
    <el-form-item prop="sale">
      <span>销售价 = 供货价 + &nbsp;</span>
      <el-input-number :model-value="sale" :controls="false" :precision="2" disabled />
      <span>&nbsp;元</span>
    </el-form-item>
    <el-form-item prop="scribe">
      <span>划线价 = 销售价 + &nbsp;</span>
      <el-input-number :model-value="scribe" :controls="false" :precision="2" disabled />
      <span>&nbsp;元</span>
    </el-form-item>
  </template>
  <template v-else></template>
</template>
