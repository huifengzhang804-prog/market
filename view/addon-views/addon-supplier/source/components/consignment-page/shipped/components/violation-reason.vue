<script lang="ts" setup>
import { PropType, computed } from 'vue'
import { ShippedGoodsList } from '../types'

const violationTypeMap = {
  PROHIBITED: '违禁品',
  COUNTERFEIT: '假冒伪劣',
  EXCESSIVE_PLATFORM_INTERVENTION: '平台介入率太高',
  TITLE_IRREGULARITY: '标题有问题',
  OTHER: '其他',
}
const $props = defineProps({
  productViolation: {
    type: Object as PropType<ShippedGoodsList['extra']['productViolation']>,
    default: () => ({}),
  },
})
const explainData = computed(() => {
  // 重组数据便于模板编译
  return {
    ...$props.productViolation,
    violationType: violationTypeMap[$props.productViolation?.violationType as unknown as keyof typeof violationTypeMap],
    violationEvidence: $props.productViolation?.violationEvidence?.split(','),
  }
})
</script>

<template>
  <div style="line-height: 30px" class="explainData">
    <div>
      <p class="explainData_label">检查员：</p>
      {{ explainData.rummager }}
    </div>
    <div>
      <p class="explainData_label">检查时间：</p>
      {{ explainData.examineDateTime }}
    </div>
    <div>
      <p class="explainData_label">类型：</p>
      {{ explainData.violationType }}
    </div>
    <div>
      <p class="explainData_label">原因：</p>
      {{ explainData.violationExplain }}
    </div>
    <div>
      <p class="explainData_label">相关证据：</p>
      <img v-for="(pic, picIndex) in explainData.violationEvidence" :key="picIndex" :src="pic" class="violation-evidence" />
    </div>
  </div>
</template>

<style scoped lang="scss">
.explainData {
  > div {
    display: flex;
    margin-top: 8px;
    img {
      margin-top: 6px;
    }
  }
  .explainData_label {
    width: 80px;
    text-align: right;
    margin-right: 8px;
  }
}

.violation-evidence {
  width: 100px;
  height: 100px;
  vertical-align: top;
}
.violation-evidence + .violation-evidence {
  margin-left: 5px;
}
</style>
