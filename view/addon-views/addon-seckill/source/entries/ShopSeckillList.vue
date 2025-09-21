<script lang="ts" setup>
import { reactive, ref, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import HeadSearch from '../components/head-search.vue'
import PageManage from '@/components/BetterPageManage/BetterPageManage.vue'
import { ApiSecondSkillItem, Long, PageParams, SearchParam, SeckillQueryStatus, SeckillStatus } from '../index'
import { doDelShopSeckill, doGetsecKillList, doPutsecKillOff } from '../apis'
import { ElMessage, ElMessageBox } from 'element-plus'
import 'uno.css'

const router = useRouter()
const route = useRoute()
const content = ref('')
const scondsKillList = ref<ApiSecondSkillItem[]>([])
const dialogVisible = ref(false)
const multipleTableRef = ref()
const searchParam = reactive<SearchParam>({
  name: '',
  status: SeckillQueryStatus.ALL,
})
const pageConfig = reactive<PageParams>({
  size: 10,
  current: 1,
  total: 0,
})

watch(
  () => route.query.flag,
  (val) => {
    if (val) {
      initSecKillList()
    }
  },
)

async function initSecKillList() {
  const { code, data } = await doGetsecKillList({
    ...searchParam,
    ...pageConfig,
  })
  if (code !== 200) return ElMessage.error('获取活动列表失败')
  scondsKillList.value = data.records
  pageConfig.current = data.current
  pageConfig.size = data.size
  pageConfig.total = data.total
}
initSecKillList()

const handleDel = (activityIds: Long[]) => {
  if (!activityIds?.length) {
    return
  }
  ElMessageBox.confirm('确定删除选中的活动?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
    callback: async (action: string) => {
      if (action === 'cancel') {
        return
      }
      const { code } = await doDelShopSeckill(activityIds)
      if (code !== 200) {
        ElMessage.error('删除失败')
        return
      }
      const idSet = new Set(activityIds)
      scondsKillList.value = scondsKillList.value.filter((item) => !idSet.has(item.activityId))
      pageConfig.total -= idSet.size
      ElMessage.success('删除成功')
    },
  })
}

const selectionItems = ref<Array<ApiSecondSkillItem>>([])

// tableCheck选中
const handleSelectionChange = (selection: ApiSecondSkillItem[]) => {
  selectionItems.value = selection
}
const handleSizeChange = (value: number) => {
  pageConfig.size = value
  initSecKillList()
}
const handleCurrentChange = (value: number) => {
  pageConfig.current = value
  initSecKillList()
}
const handleOff = (row: ApiSecondSkillItem) => {
  ElMessageBox.confirm('确定下架选中活动?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
    callback: async (action: string) => {
      if (action === 'cancel') {
        return
      }
      const { code } = await doPutsecKillOff({ activityId: row.activityId })
      if (code !== 200) {
        ElMessage.error('下架失败')
        return
      }
      ElMessage.success('下架成功')
      row.status = SeckillQueryStatus.OFF_SHELF
    },
  })
}
const handleIllegal = async (violation: string) => {
  content.value = violation
  dialogVisible.value = true
}
const handleEdit = (shopId?: Long, secKillId?: Long) => {
  router.push({
    name: 'seckillCreate',
    query: {
      shopId,
      secKillId,
    },
  })
}
</script>

<template>
  <div class="q_plugin_container">
    <el-config-provider :empty-values="[undefined, null]">
      <HeadSearch v-model="searchParam" @search="initSecKillList" />
    </el-config-provider>
    <div class="handle_container" style="padding-top: 16px">
      <el-button round type="primary" @click="() => handleEdit()">新增秒杀</el-button>
      <el-button
        :disabled="!selectionItems.length"
        round
        @click="
          () =>
            handleDel(
              selectionItems
                .filter((row:ApiSecondSkillItem) => row.status !== SeckillQueryStatus.IN_PROGRESS)
                .map((row) => row.activityId)
            )
        "
        >批量删除
      </el-button>
    </div>
    <div class="table_container">
      <el-table
        ref="multipleTableRef"
        :header-cell-style="{ background: '#f6f8fa', height: '48px' }"
        :header-row-style="{ color: '#333' }"
        :data="scondsKillList"
        @selection-change="handleSelectionChange"
      >
        <el-table-column
          :selectable="(row:ApiSecondSkillItem) => row.status !== SeckillQueryStatus.IN_PROGRESS"
          align="center"
          fixed
          type="selection"
          width="30"
        />
        <el-table-column label="活动名称" width="220">
          <template #default="{ row }">{{ row.name }}</template>
        </el-table-column>
        <el-table-column label="状态" width="160">
          <template #default="{ row }: { row: ApiSecondSkillItem }">
            <span
              :style="{
                color: [SeckillQueryStatus.VIOLATION_OFF_SHELF].includes(row.status)
                  ? '#F54319'
                  : [SeckillQueryStatus.OFF_SHELF, SeckillQueryStatus.FINISHED].includes(row.status)
                  ? '#999'
                  : '#333',
              }"
            >
              {{ SeckillStatus[row.status] }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="活动时间" width="300">
          <template #default="{ row }">
            <div>起：{{ row.startTime }}</div>
            <div>止：{{ row.endTime }}</div>
          </template>
        </el-table-column>
        <el-table-column label="活动商品" width="140">
          <template #default="{ row }">{{ row.product }}</template>
        </el-table-column>
        <el-table-column label="支付单数" width="140">
          <template #default="{ row }"
            ><span>{{ row.payOrder || 0 }}</span></template
          >
        </el-table-column>

        <el-table-column fixed="right" label="操作" align="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row.shopId, row.activityId)">查看 </el-button>
            <el-button v-if="row.status === SeckillQueryStatus.IN_PROGRESS" link type="primary" @click="handleOff(row)">下架 </el-button>
            <el-button v-if="row.status === SeckillQueryStatus.VIOLATION_OFF_SHELF" link type="primary" @click="handleIllegal(row?.violation)"
              >违规原因
            </el-button>
            <el-button v-if="row.status !== SeckillQueryStatus.IN_PROGRESS" link type="danger" @click="handleDel([row.activityId])">删除 </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 好用的分页器 -->
    <PageManage
      :page-size="pageConfig.size"
      :total="pageConfig.total"
      :page-num="pageConfig.current"
      @handle-size-change="handleSizeChange"
      @handle-current-change="handleCurrentChange"
    />
    <el-dialog v-model="dialogVisible" center title="违规原因" width="500" @close="() => (content = '')">
      <span>{{ content }}</span>
    </el-dialog>
  </div>
</template>

<style lang="scss" scoped>
@include b(container) {
  overflow-y: scroll;
}

:deep(.el-button.is-link) {
  padding: 0;
}

:deep(.el-button + .el-button) {
  margin-left: 10px;
}
</style>
