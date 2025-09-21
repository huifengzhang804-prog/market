<script lang="ts" setup>
import HeadSearch from '../components/head-search.vue'
import { doDelApplyDiscountShopBatch, doGetApplyDiscountList, doPutSellOff } from '../apis'
import { AllStatus, Long, PageParams, PromotionFullReduction, SearchParam, Status, FULL_REDUCTION_RULE } from '../index'
import PageManage from '@/components/PageManage.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import { reactive, ref } from 'vue'

const router = useRouter()
const activeList = ref<PromotionFullReduction[]>([])
const searchParam = ref<SearchParam>({
  name: '',
  status: '',
})
const pageConfig = reactive<PageParams>({
  size: 10,
  current: 1,
  total: 0,
})
const multipleTableRef = ref()

initApplyDiscountList()

async function initApplyDiscountList() {
  console.log(searchParam.value, 'searchParam')

  const params = { ...pageConfig, ...searchParam.value }
  const { code, data } = await doGetApplyDiscountList(params)
  if (code !== 200) return ElMessage.error('获取活动列表失败')
  activeList.value = data.records
  pageConfig.current = data.current
  pageConfig.size = data.size
  pageConfig.total = data.total
}

const handleDel = async (id: string) => {
  try {
    const isValidate = await ElMessageBox.confirm('确定删除该活动?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    if (!isValidate) return

    const { code } = await doDelApplyDiscountShopBatch([id])
    if (code !== 200) {
      ElMessage.error('删除失败')
      return
    }
    ElMessage.success('删除成功')
    activeList.value = activeList.value.filter((item) => item.id !== id)
    pageConfig.total--
  } catch (error) {
    console.error('isValidate', error)
  }
}
const handleSizeChange = (value: number) => {
  pageConfig.size = value
  initApplyDiscountList()
}
const handleCurrentChange = (value: number) => {
  pageConfig.current = value
  initApplyDiscountList()
}
const selectionItems = ref<Array<PromotionFullReduction>>([])
/**
 * 查看违规原因
 */
const openReason = ref(false)
const reason = ref('违规原因')
const checkReason = (violation: string) => {
  reason.value = violation
  openReason.value = true
}
/**
 * 下架商品
 */
const handleSelloff = async (id: Long) => {
  const isValidate = await ElMessageBox.confirm('确定删除该活动?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
  if (!isValidate) return
  const { msg, code } = await doPutSellOff({ id })
  if (code !== 200) return ElMessage.error(msg ?? '下架失败！')
  ElMessage.success('下架成功！')
  initApplyDiscountList()
}
const handleEdit = (id: Long, isLookUp: 'true' | 'false' | '') => {
  router.push({
    name: 'applyDiscountBaseinfo',
    query: { id, isLookUp },
  })
}
const handleBacthDel = () => {
  ElMessageBox.confirm('确定删除选中活动?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
    callback: async (action: string) => {
      if (action === 'cancel') {
        return
      }
      const ids = selectionItems.value.filter((item) => item.status !== Status.IN_PROGRESS).map((item) => item.id)
      const { code } = await doDelApplyDiscountShopBatch(ids)
      if (code !== 200) {
        ElMessage.error('删除失败')
        return
      }
      ElMessage.success('删除成功')
      initApplyDiscountList()
    },
  })
}

// tableCheck选中
const handleSelectionChange = (selection: PromotionFullReduction[]) => {
  selectionItems.value = selection
}

const handleClose = () => {
  reason.value = ''
  openReason.value = false
}
</script>

<template>
  <div class="q_plugin_container pt15">
    <div class="search_container">
      <el-config-provider :empty-values="[undefined, null]">
        <HeadSearch v-model="searchParam" @search="initApplyDiscountList" />
      </el-config-provider>
    </div>
    <div class="handle_container" style="padding-top: 16px">
      <el-button round type="primary" @click="router.push({ name: 'applyDiscountBaseinfo' })"> 新增满减 </el-button>
      <el-button :disabled="!selectionItems.length" round @click="handleBacthDel">批量删除</el-button>
    </div>
    <div class="table_container">
      <!-- 满减列表 s -->
      <el-table
        ref="multipleTableRef"
        :data="activeList"
        :header-cell-style="{ background: '#f6f8fa', height: '48px' }"
        :header-row-style="{ color: '#333' }"
        @selection-change="handleSelectionChange"
      >
        <el-table-column :selectable="(row:PromotionFullReduction) => row.status !== Status.IN_PROGRESS" fixed type="selection" width="30" />
        <el-table-column label="活动名称" width="180">
          <template #default="{ row }">{{ row.name }}</template>
        </el-table-column>
        <el-table-column label="类型" width="80">
          <template #default="{ row }: { row: PromotionFullReduction }">
            {{ FULL_REDUCTION_RULE[row.firstRuleType] }}
          </template>
        </el-table-column>
        <el-table-column label="使用规则" width="180">
          <template #default="{ row }">
            <div>{{ row.firstRuleDesc }}</div>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="120">
          <template #default="{ row }: { row: PromotionFullReduction }">
            <div
              :style="{
                color: ['VIOLATION_OFF_SHELF'].includes(row.status) ? '#F54319' : ['OFF_SHELF', 'FINISHED'].includes(row.status) ? '#999' : '#333',
              }"
            >
              {{ AllStatus[row.status] && AllStatus[row.status].label }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="活动时间" width="220">
          <template #default="{ row }">
            <div>起：{{ row.startTime }}</div>
            <div>止：{{ row.endTime }}</div>
          </template>
        </el-table-column>
        <el-table-column label="活动商品">
          <template #default="{ row }"
            >{{
              row.productType === 'ALL_PRODUCT' ? '全部' : `${row.product}${row.productType === 'SPECIFIED_PRODUCT_NOT_PARTICIPATE' ? '(除外)' : ''}`
            }}
          </template>
        </el-table-column>
        <el-table-column label="支付单数">
          <template #default="{ row }"
            ><span>{{ row.order || 0 }}</span></template
          >
        </el-table-column>

        <el-table-column fixed="right" label="操作" width="200" align="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row.id, 'true')">查看</el-button>
            <el-button v-if="row.status === Status.IN_PROGRESS" link type="primary" @click="handleSelloff(row.id)"> 下架 </el-button>
            <el-button v-if="row.status === Status.VIOLATION_OFF_SHELF" link type="primary" @click="checkReason(row.violation)">违规原因 </el-button>
            <el-button v-if="row.status !== Status.IN_PROGRESS" link type="danger" @click="handleDel(row.id)"> 删除 </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 好用的分页器 -->
    <PageManage
      :load-init="true"
      :page-num="pageConfig.current"
      :page-size="pageConfig.size"
      :total="pageConfig.total"
      @reload="initApplyDiscountList"
      @handle-size-change="handleSizeChange"
      @handle-current-change="handleCurrentChange"
    />
    <el-dialog v-model="openReason" center title="违规原因" width="600px" @close="handleClose">
      <div class="reason">{{ reason }}</div>
    </el-dialog>
  </div>
</template>

<style lang="scss" scoped>
@include b(container) {
  overflow-y: scroll;
}

@include b(reason) {
  padding: 10px 10px 20px;
}

@include b(status) {
  color: #f54319;
}

@include b('grey') {
  color: #999;
}

:deep(.el-button.is-link) {
  padding: 0;
}

:deep(.el-button + .el-button) {
  margin-left: 10px;
}
</style>
