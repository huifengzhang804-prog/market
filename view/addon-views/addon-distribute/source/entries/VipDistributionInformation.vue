<script lang="ts" setup>
import { ref, defineProps, watch, PropType } from 'vue'
import { ElMessage } from 'element-plus'
import { doGetParentDisInfo } from '../apis'

interface PropertiesType {
  userId: string
}

const props = defineProps({
  properties: {
    type: Object as PropType<PropertiesType>,
    required: true,
  },
})
const info = ref({
  name: '',
  createTime: '',
})

watch(
  () => props.properties.userId,
  (value) => {
    value && initInfo(value)
  },
  { immediate: true },
)

async function initInfo(userId: string) {
  const { code, data, msg } = await doGetParentDisInfo(userId)
  if (code !== 200) return ElMessage.error(msg || '获取分销信息失败')
  if (data) {
    info.value = data
  }
}
</script>

<template>
  <el-tab-pane label="分销信息" name="distributionOfInformation">
    <span class="f14 color60" style="margin-right: 80px">邀请人：{{ info.name || '--' }}</span>
    <span class="f14 color60">加入时间：{{ info.createTime || '--' }}</span>
  </el-tab-pane>
</template>

<style scoped>
.color60 {
  color: #606266;
}
</style>
