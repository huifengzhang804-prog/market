<script lang="ts" setup>
import { PropType, computed } from 'vue'
import { ReleaseList } from '../types'

const violationTypeMap = {
  PROHIBITED: '违禁品',
  COUNTERFEIT: '假冒伪劣',
  EXCESSIVE_PLATFORM_INTERVENTION: '平台介入率太高',
  TITLE_IRREGULARITY: '标题有问题',
  OTHER: '其他',
}
const $props = defineProps({
  productViolation: {
    type: Object as PropType<ReleaseList['extra']['productViolation']>,
    default: () => ({}),
  },
})
const explainData = computed(() => {
  // 重组数据便于模板编译
  return {
    ...$props.productViolation,
    violationType: violationTypeMap[$props.productViolation.violationType as unknown as keyof typeof violationTypeMap],
    violationEvidence: $props.productViolation.violationEvidence?.split(','),
  }
})
</script>

<template>
  <div style="line-height: 30px">
    <el-row :gutter="8">
      <el-col :span="12">
        <div>检查员：{{ explainData.rummager }}</div>
      </el-col>
      <el-col :span="12">
        <div>检查时间：{{ explainData.examineDateTime }}</div>
      </el-col>
    </el-row>
    <div>类型：{{ explainData.violationType }}</div>
    <div>原因：{{ explainData.violationExplain }}</div>
    <div>
      相关证据：
      <img v-for="(pic, picIndex) in explainData.violationEvidence" :key="picIndex" :src="pic" class="violation-evidence" />
    </div>
  </div>
</template>

<style scoped>
img {
  vertical-align: top;
}
</style>
