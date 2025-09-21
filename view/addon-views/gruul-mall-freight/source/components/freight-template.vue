<script lang="ts" setup>
import { ref, reactive, provide } from 'vue'
import FreightTemplateDialog from './freight-template-dialog.vue'
import FreightTemplateTable from './freight-template-table.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import PageManageTwo from '@/components/PageManage.vue'
import { doGetLogisticsTemplateList, doDelLogisticsTemplate } from '../apis'
import type { ApiFreightTemplate } from '@/views/freight/components/types'
import ElTableEmpty from '@/components/element-plus/el-table/ElTableEmpty/index.vue'

const flagDialog = ref(false)
const tableData = ref<ApiFreightTemplate[]>([])
const pageConfig = reactive({
  size: 20,
  current: 1,
})
const query = useRoute().query
const $router = useRouter()
const pageConfigTotal = ref(0)
const currentId = ref('')

initLogisticsTemplateList()

async function initLogisticsTemplateList() {
  const { code, data } = await doGetLogisticsTemplateList(pageConfig.current, pageConfig.size)
  if (code !== 200) return ElMessage.error('获取物流列表失败')
  tableData.value = data.records
  pageConfigTotal.value = data.total
}

const handleSizeChange = (value: number) => {
  pageConfig.size = value
  initLogisticsTemplateList()
}
const handleCurrentChange = (value: number) => {
  pageConfig.current = value
  initLogisticsTemplateList()
}
const handleAddTemplate = () => {
  currentId.value = ''
  flagDialog.value = true
}
const handleEditTemplate = (id: string) => {
  currentId.value = id
  flagDialog.value = true
}
/**
 * 删除模板
 */
const handleDelTemplate = (id: string) => {
  ElMessageBox.confirm('确定要删除此模板吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      const { code, data, msg } = await doDelLogisticsTemplate(id)
      if (code !== 200) return ElMessage.error(msg || '删除失败')
      ElMessage.success('删除成功')
      initLogisticsTemplateList()
    })
    .catch(() => {})
}
provide('parentLogisticsTemplateList', initLogisticsTemplateList)
</script>

<template>
  <div class="handle_container">
    <el-row class="btn">
      <el-button v-if="query.from" round @click="$router.back()">返回发布商品</el-button>
      <el-button round type="primary" @click="handleAddTemplate">新增运费模板</el-button>
    </el-row>
  </div>
  <div class="template-table table_container">
    <div v-if="tableData.length === 0"><ElTableEmpty /></div>
    <FreightTemplateTable :table-data="tableData" @handleEditTemplate="handleEditTemplate" @handle-del-template="handleDelTemplate" />
  </div>
  <PageManageTwo
    :page-size="pageConfig.size"
    :page-num="pageConfig.current"
    :total="pageConfigTotal"
    @handle-size-change="handleSizeChange"
    @handle-current-change="handleCurrentChange"
  />
  <FreightTemplateDialog v-if="flagDialog" :id="currentId" v-model:is-show="flagDialog" @close="initLogisticsTemplateList" />
</template>

<style lang="scss" scoped>
@include b(btn) {
  font-size: 12px;
  margin-bottom: 10px;
}

@include b(template-table) {
  height: calc(100vh - 300px);
  overflow: auto;
}
</style>
