<script lang="ts" setup>
import { defineProps, PropType, ref, onMounted, watch, onActivated } from 'vue'
import { useRouter } from 'vue-router'
import LogisticsSetting from '../components/logistics-setting.vue'
import { ApiFreightTemplate } from '@/views/freight/components/types'
import { doGetLogisticsTemplateList } from '../apis'

interface PropertiesType {
  disable: boolean
  templateId: string
  templateChange: (templateId: string) => void
}

const props = defineProps({
  properties: {
    type: Object as PropType<PropertiesType>,
    required: true,
  },
})
const router = useRouter()
const currentTemplateId = ref<string>('0')
const templateMap = ref<Map<string, ApiFreightTemplate>>(new Map())

onActivated(async () => {
  const forwardPath: string = router.options.history.state.forward as string
  if (forwardPath === '/freight/logistics?from=releaseGoods') {
    initLogisticsTemplateList()
  }
  initLogisticsTemplateList()
})
onMounted(() => {
  initLogisticsTemplateList()
})
watch(
  () => props.properties.templateId,
  (val) => {
    currentTemplateId.value = val
  },
  { immediate: true },
)

async function initLogisticsTemplateList() {
  const { code, data } = await doGetLogisticsTemplateList(1, 1000)
  if (code !== 200 || !data) return
  const records = data.records as ApiFreightTemplate[]
  if (!records || !records.length) return
  records.forEach((item: ApiFreightTemplate) => {
    templateMap.value.set(item.id, item)
  })
}

const templateChange = (val) => {
  props.properties.templateChange(val)
}
</script>
<template>
  <el-checkbox :disabled="properties.disable" value="EXPRESS">
    <span style="margin-right: 10px" @click.stop>运费模板选择</span>
    <el-select v-model="currentTemplateId" class="inputWidth" placeholder="请选择运费模板" @change="templateChange" @click.prevent>
      <el-option :value="'0'" label="商家包邮" />
      <el-option v-for="item in Array.from(templateMap.values())" :key="item.id" :label="item.templateName" :value="item.id" />
    </el-select>
    <el-link
      :underline="false"
      style="margin-left: 15px"
      type="primary"
      @click.stop.prevent="
        router.push({
          name: 'freightLogisticsIndex',
          query: {
            from: 'releaseGoods',
          },
        })
      "
    >
      前往设置
    </el-link>
  </el-checkbox>
  <br />
  <!-- <LogisticsSetting :data="templateMap.get(currentTemplateId)" /> -->
</template>
