<script setup lang="ts">
import HeadSearch from './head-search.vue'
import PageManage from '@/components/PageManage.vue'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import { ElMessage, ElMessageBox } from 'element-plus'
import { doGetGroupList, doDelGroupActivity, doShopShelf, doGetReason } from '../apis'
import { useRouter, useRoute } from 'vue-router'
import type { ApiGroupList } from '../'
import { ref, reactive, onBeforeMount, watch } from 'vue'
import Storage from '@/utils/Storage'

const router = useRouter()
const $route = useRoute()
const activeList = ref<ApiGroupList[]>([])
const searchParam = ref({
  keyword: '',
  status: '',
  date: '',
})
const multipleTableRef = ref()
const pageConfig = reactive({
  size: 10,
  current: 1,
  total: 0,
})
const statusConfig = {
  OPENING: '未开始',
  PREHEAT: '预售中',
  OPEN: '进行中',
  FINISHED: '已结束',
  SHOP_OFF_SHELF: '已下架',
  VIOLATION: '违规下架',
}
const modeType = {
  COMMON: '普通拼团',
  STAIRS: '阶梯拼团',
}
onBeforeMount(() => {
  initGroupList()
})

watch(
  () => $route.query.flag,
  () => {
    initGroupList()
  },
)

const handleEdit = (id: string, isLookUp: 'true' | 'false' | '') => {
  router.push({
    name: 'marketingAppGroupForm',
    query: {
      id,
      isLookUp,
    },
  })
}
async function initGroupList() {
  const params = { ...pageConfig, ...searchParam.value, shopId: useShopInfoStore().shopInfo.id }
  const { code, data } = await doGetGroupList(params)
  if (code !== 200) return ElMessage.error('获取活动列表失败')
  activeList.value = data.records
  pageConfig.total = data.total
}

const handleCurrentChange = (e: number) => {
  pageConfig.current = e
  initGroupList()
}
const handleSizeChange = (e: number) => {
  pageConfig.size = e
  initGroupList()
}

interface ApiGroupListAdd extends ApiGroupList {
  activityId: string
}
/**
 * 删除
 */
const handleDel = async (param: ApiGroupList | ApiGroupList[], msg?: string) => {
  const title = msg ?? '确定删除该活动?'

  try {
    const isValidate = await ElMessageBox.confirm(title, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    if (!isValidate) return
    const params: ApiGroupListAdd[] = Array.isArray(param)
      ? param.map((item) => ({ ...item, activityId: item.id }))
      : [{ ...param, activityId: param.id }]
    params.forEach((item) => {
      item.activityId = item.id
    })
    const { code, data } = await doDelGroupActivity(params)
    if (code !== 200) {
      ElMessage.error('删除失败')
      return
    }
    ElMessage.success('删除成功')
    initGroupList()
  } catch (error) {
    error
  }
}

/**
 * 批量删除
 */

const checked = ref<ApiGroupList[]>([])
const batchDel = async () => {
  const delData = checked.value.filter((item) => item.status !== 'OPEN')

  const delL = delData.length
  const checkedL = checked.value.length
  const title = delL === checkedL ? `确定批量删除活动?` : `确定批量删除活动? 其中有${checkedL - delL}条数据为进行中,无法进行删除 已自动剔除！`

  handleDel(checked.value, title)
}

// tableCheck选中
const handleSelectionChange = (selection: ApiGroupList[]) => {
  checked.value = selection
}

/**
 * 下架
 */
const handleCellOff = async (id: string) => {
  try {
    const isValidate = await ElMessageBox.confirm('确定下架商品吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    if (!isValidate) return
    const { msg, code } = await doShopShelf(id)
    if (code !== 200) return ElMessage.error(msg ?? '下架失败')
    initGroupList()
  } catch (error) {
    console.log('error', error)
  }
}

/**
 * 查看违规原因
 */
const openReason = ref(false)
const reason = ref('违规原因')
const checkReason = async (id: string) => {
  try {
    const { msg, data, code } = await doGetReason(id)
    if (code !== 200 || !data) return ElMessage.error(msg ?? '获取下架原因失败！')
    reason.value = data
    openReason.value = true
  } catch (error) {
    console.log('error', error)
  }
}
const handelClose = () => {
  reason.value = ''
  openReason.value = false
}
const handleAdd = () => {
  const storage = new Storage()
  if (storage.getItem('shopStore')?.shopMode === 'O2O') {
    ElMessage.warning('O2O店铺暂不支持该活动')
    return
  }
  router.push({ name: 'marketingAppGroupNewForm' })
}
</script>

<template>
  <el-config-provider :empty-values="[undefined, null]">
    <HeadSearch v-model="searchParam" @search="initGroupList" />
  </el-config-provider>

  <div class="handle_container" style="padding-top: 16px">
    <el-button round type="primary" @click="handleAdd">新增拼团</el-button>
    <el-button :disabled="!checked.length" round @click="batchDel"> 批量删除 </el-button>
  </div>
  <div class="table_container">
    <el-table
      ref="multipleTableRef"
      :data="activeList"
      :header-cell-style="{ background: '#f6f8fa', height: '48px' }"
      :header-row-style="{ color: '#333' }"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="30" fixed :selectable="(row:ApiGroupList) => row.status !== 'OPEN'" />
      <el-table-column label="活动名称" width="220">
        <template #default="{ row }">{{ row.name }}</template>
      </el-table-column>
      <el-table-column label="拼团模式" width="140">
        <template #default="{ row }">{{ modeType[row.mode as keyof typeof modeType] }}</template>
      </el-table-column>
      <el-table-column label="状态" width="140">
        <template #default="{ row }: { row: ApiGroupList }">
          <span
            :style="{
              color: ['VIOLATION'].includes(row.status) ? '#F54319' : ['SHOP_OFF_SHELF', 'FINISHED'].includes(row.status) ? '#999' : '#333',
            }"
          >
            {{ statusConfig[row.status] }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="活动时间" width="240">
        <template #default="{ row }">
          <div>起：{{ row.startTime }}</div>
          <div>止：{{ row.endTime }}</div>
        </template>
      </el-table-column>
      <el-table-column label="活动商品" width="120">
        <template #default="{ row }">{{ row.productNum || 0 }}</template>
      </el-table-column>
      <el-table-column label="支付单数" width="120">
        <template #default="{ row }">{{ row.orders || 0 }}</template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" align="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row.id, 'true')">查看</el-button>
          <!-- <el-button v-if="row.status === 'OPEN'" type="primary" link @click="handleEdit(row.id)">编辑</el-button> -->
          <el-button v-if="row.status === 'OPEN'" type="primary" link @click="handleCellOff(row.id)">下架</el-button>
          <el-button v-if="row.status === 'VIOLATION'" type="primary" link @click="checkReason(row.id)">违规原因</el-button>
          <el-button v-if="row.status !== 'OPEN'" type="danger" link @click="handleDel(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>

  <!-- 好用的分页器 -->
  <page-manage
    :page-size="pageConfig.size"
    :page-num="pageConfig.current"
    :total="pageConfig.total"
    @handle-current-change="handleCurrentChange"
    @handle-size-change="handleSizeChange"
  />
  <el-dialog v-model="openReason" title="违规原因" width="600px" center @close="handelClose">
    <div class="reason">{{ reason }}</div>
  </el-dialog>
</template>

<style scoped lang="scss">
@include b(container) {
  overflow-y: scroll;
}

@include b(reason) {
  padding: 10px 10px 20px;
}
:deep(.el-button.is-link) {
  padding: 0;
}
:deep(.el-button + .el-button) {
  margin-left: 10px;
}
</style>
