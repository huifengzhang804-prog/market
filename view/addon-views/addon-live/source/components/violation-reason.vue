<script lang="ts" setup>
import { ref, watch, defineProps } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { doGetReason } from '../apis'

/**
 * @param YELLOW_INVOLVEMENT 涉黄
 * @param DRUG_RELATED 涉毒
 * @param SENSITIVE_TOPIC 敏感话题
 * @param OTHER 其它
 */
enum categoryTypes {
  YELLOW_INVOLVEMENT,
  DRUG_RELATED,
  SENSITIVE_TOPIC,
  OTHER,
}

const getcategoryTypes = {
  YELLOW_INVOLVEMENT: '涉黄',
  DRUG_RELATED: '涉毒',
  SENSITIVE_TOPIC: '敏感话题',
  OTHER: '其它',
}

interface reasonType {
  qualityInspector: string
  sourceId: string
  shopId: string
  type: string
  categoryTypes: string
  reason: string
  relevantEvidence: string
  createTime: string
}

const $props = defineProps({
  id: {
    type: String,
    default: '',
  },
  type: {
    type: String,
    default: '',
  },
})
const reason = ref<reasonType>({
  qualityInspector: '',
  sourceId: '',
  shopId: '',
  type: '',
  categoryTypes: '',
  reason: '',
  relevantEvidence: '',
  createTime: '',
})

watch($props, (newVal) => {
  initReason()
})
initReason()

async function initReason() {
  const { code, data } = await doGetReason($props.id, $props.type)
  if (code !== 200) return ElMessage.error('获取直播间列表失败')
  data.relevantEvidence = data.relevantEvidence.split(',')
  data.categoryTypes = data.categoryTypes.map((item: categoryTypes) => getcategoryTypes[item]).join(',')
  reason.value = data
}
</script>

<template>
  <div class="reason">
    <el-row :gutter="8">
      <el-col :span="12">检查员：{{ reason.qualityInspector }}</el-col>
      <el-col :span="12">检查时间：{{ reason.createTime }}</el-col>
    </el-row>
    <el-row :gutter="8">
      <el-col :span="24">类型：{{ reason.categoryTypes }}</el-col>
    </el-row>
    <el-row :gutter="8">
      <el-col :span="24">原因：{{ reason.reason }}</el-col>
    </el-row>
    <el-row :gutter="8">
      相关证据：
      <el-col :span="24">
        <img v-for="item in reason.relevantEvidence" :key="item" :src="item" style="width: 60px; height: 60px" />
      </el-col>
    </el-row>
  </div>
</template>

<style lang="scss" scoped>
.el-row {
  margin-bottom: 8px;

  img + img {
    margin-left: 10px;
    width: 50px;
    height: 50px;
    vertical-align: top;
    object-fit: cover;
  }
}
</style>
