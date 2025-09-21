<script setup lang="ts">
import HeadSearch from '../bargainList/head-search.vue'
import PageManage from '@/components/PageManage.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { doGetAddBargain, doDelAddBargain, doPutBargainsellOf, doPutBargainIllegalReason, doPutBargainsellOfParamsType } from '../../apis'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import { DoGetBargainListResponse, DoGetAddBargainQuery, AddBargainStatus } from '../../index'
import { reactive, ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import Storage from '@/utils/Storage'

const searchParam = reactive({
  keyword: '',
  status: '' as DoGetAddBargainQuery['status'],
})
const router = useRouter()
const productNumComputed = computed(() => (val: string | number) => Number(val) === 0 ? '全部' : val + '')
const activeList = ref<DoGetBargainListResponse[]>([])
const pageConfig = reactive({
  size: 10,
  current: 1,
  total: 0,
})
const dialogVisible = ref(false)
const multipleTableRef = ref()
const content = ref('')
initBargainList()
async function initBargainList() {
  const { status, keyword } = searchParam
  const params = { ...pageConfig, keyword, shopId: useShopInfoStore().shopInfo.id, status }
  const { code, data } = await doGetAddBargain(params)
  if (code !== 200) return ElMessage.error('获取活动列表失败')
  activeList.value = data.records
  pageConfig.current = data.current
  pageConfig.size = data.size
  pageConfig.total = data.total
}
const handleDel = async (activeitem: DoGetBargainListResponse) => {
  try {
    const isValidate = await ElMessageBox.confirm('确定删除该活动?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    if (!isValidate) return
    const { shopId, id: activityId } = activeitem
    const { code, data, msg } = await doDelAddBargain([{ activityId, shopId }])
    if (code !== 200) {
      ElMessage.error(msg || '删除失败')
      return
    }
    ElMessage.success('删除成功')
    activeList.value = activeList.value.filter((item) => item.id !== activeitem.id)
    pageConfig.total--
  } catch (error) {
    error
  }
}
const selectionItems = ref<Array<DoGetBargainListResponse>>([])

// tableCheck选中
const handleSelectionChange = (selection: DoGetBargainListResponse[]) => {
  selectionItems.value = selection
}

const handleSizeChange = (value: number) => {
  pageConfig.size = value
  initBargainList()
}
const handleCurrentChange = (value: number) => {
  pageConfig.current = value
  initBargainList()
}
const leftBtnClick = () => {
  const storage = new Storage()
  if (storage.getItem('shopStore')?.shopMode === 'O2O') {
    ElMessage.warning('O2O店铺暂不支持该活动')
    return
  }
  router.push({
    name: 'bargainBaseinfo',
  })
}
const handleBatchDel = () => {
  ElMessageBox.confirm('确定删除选中活动?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
    callback: async (action: string) => {
      console.log('action', action)
      if (action === 'cancel') {
        return
      }
      const ids = selectionItems.value.filter((item) => item.status !== 'PROCESSING').map((item) => ({ shopId: item.shopId, activityId: item.id }))
      const { code } = await doDelAddBargain(ids)
      if (code !== 200) {
        ElMessage.error('删除失败')
        return
      }
      ElMessage.success('删除成功')
      initBargainList()
    },
  })
}
const handleEdit = (item: DoGetBargainListResponse, isLookUp: boolean) => {
  const { id: activityId, shopId } = item
  router.push({
    name: 'bargainBaseinfo',
    query: {
      activityId,
      shopId,
      isLookUp: isLookUp.toString(),
    },
  })
}
const handleOff = (data: doPutBargainsellOfParamsType) => {
  ElMessageBox.confirm('确定下架选中活动?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
    callback: async (action: string) => {
      if (action === 'cancel') {
        return
      }
      const { code } = await doPutBargainsellOf(data)
      if (code !== 200) {
        ElMessage.error('下架失败')
        return
      }
      ElMessage.success('下架成功')
      initBargainList()
    },
  })
}

const handleIllegal = async (id: string) => {
  const { code, data } = await doPutBargainIllegalReason(id)
  if (code !== 200) {
    ElMessage.error('违规原因获取失败')
  }
  content.value = data as string
  dialogVisible.value = true
}
</script>

<template>
  <el-config-provider :empty-values="[undefined, null]">
    <HeadSearch v-model="searchParam" @search="initBargainList" />
  </el-config-provider>

  <div class="handle_container" style="padding-top: 16px">
    <el-button round type="primary" @click="leftBtnClick">新增砍价</el-button>
    <el-button round :disabled="!selectionItems.length" @click="handleBatchDel">批量删除</el-button>
  </div>
  <div class="table_container f1">
    <el-table
      ref="multipleTableRef"
      :data="activeList"
      :header-cell-style="{ background: '#f6f8fa', height: '48px' }"
      :header-row-style="{ color: '#333' }"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="30" fixed :selectable="(row:DoGetBargainListResponse) => row.status !== 'PROCESSING'" />
      <el-table-column label="活动名称">
        <template #default="{ row }">{{ row.name }}</template>
      </el-table-column>
      <el-table-column label="状态" width="180">
        <template #default="{ row }: { row: DoGetBargainListResponse }">
          <span
            :style="{
              color: ['ILLEGAL_SELL_OFF'].includes(row.status) ? '#F54319' : ['OVER', 'SHOP_SELL_OFF'].includes(row.status) ? '#999' : '#333',
            }"
          >
            {{ AddBargainStatus[row.status] }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="活动时间">
        <template #default="{ row }">
          <div>起：{{ row.startTime }}</div>
          <div>止：{{ row.endTime }}</div>
        </template>
      </el-table-column>
      <el-table-column label="活动商品" width="150">
        <template #default="{ row }">{{ productNumComputed(row.productNum) }}</template>
      </el-table-column>
      <el-table-column label="支付单数" width="150">
        <template #default="{ row }">
          <span>{{ row.payOrder || 0 }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" align="right" width="200">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row, true)">查看</el-button>
          <el-button v-if="row.status === 'PROCESSING'" type="primary" link @click="handleOff({ bargainId: row.id, shopId: row.shopId })"
            >下架</el-button
          >
          <el-button v-if="row.status === 'ILLEGAL_SELL_OFF'" type="primary" link @click="handleIllegal(row.id)">违规原因</el-button>
          <el-button v-if="row.status !== 'PROCESSING'" type="danger" link @click="handleDel(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
  <pageManage
    :page-size="pageConfig.size"
    :page-num="pageConfig.current"
    :total="pageConfig.total"
    @handle-current-change="handleCurrentChange"
    @handle-size-change="handleSizeChange"
  />
  <!-- 违规原因 s -->
  <el-dialog v-model="dialogVisible" title="违规原因" width="500" center>
    <span>{{ content }}</span>
  </el-dialog>
  <!-- 违规原因 e -->
</template>

<style scoped lang="scss">
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
