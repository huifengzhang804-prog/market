<script setup lang="ts">
import { onBeforeMount, ref } from 'vue'
import HeadSearch from '../components/head-search.vue'
import { doGetAddBargain, doPutSellOfBargain, doDelAddBargain, doGetIllegalReason } from '../apis'
import { AddBargainStatus, type DoGetAddBargainQuery, type DoGetBargainListResponse } from '../index'
import PageManage from '@/components/PageManage.vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import ElTableEmpty from '@/components/element-plus/el-table/ElTableEmpty/index.vue'

const searchParams = ref({
  keywords: '',
  status: '' as DoGetAddBargainQuery['status'],
})
const pageConfig = ref({
  size: 10,
  current: 1,
})
const total = ref(0)
const router = useRouter()
const activityList = ref<DoGetBargainListResponse[]>([])
const chooseList = ref<DoGetBargainListResponse[]>([])
const statusReflect = {
  NOT_STARTED: '未开始',
  PROCESSING: '进行中',
  OVER: '已结束',
  ILLEGAL_SELL_OFF: '违规下架',
  SHOP_SELL_OFF: '已下架',
}
const illegalReasonForm = ref({ shopId: '', bargainId: '', violationReason: '' })
const illegalReasonFormRef = ref()
const dialogVisible = ref(false)

const toHandleOff = (row: DoGetBargainListResponse) => {
  dialogVisible.value = true
  illegalReasonForm.value.shopId = row.shopId
  illegalReasonForm.value.bargainId = row.id
}

const handleClose = () => {
  illegalReasonFormRef.value.resetFields()
}
const handleOff = () => {
  illegalReasonFormRef.value.validate(async (valid: boolean, fields: string) => {
    if (valid) {
      const { code, msg } = await doPutSellOfBargain(illegalReasonForm.value)
      if (code !== 200) {
        ElMessage.error(msg)
        return
      }
      ElMessage.success('下架成功')
      const currentActivity = activityList.value.find((item) => item.id === illegalReasonForm.value.bargainId)
      if (currentActivity) {
        currentActivity.status = 'ILLEGAL_SELL_OFF'
      }
      dialogVisible.value = false
    } else {
      console.log('error submit!', fields)
    }
  })
}
const handleBatchDel = async () => {
  if (!chooseList.value.length) {
    ElMessage.info('请选择需要删除的活动')
    return
  }
  const isValidate = await ElMessageBox.confirm('确定删除活动?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
  if (!isValidate) return
  const params = chooseList.value.map((item) => {
    return {
      shopId: item.shopId,
      activityId: item.id,
    }
  })
  const { code, data } = await doDelAddBargain(params)
  if (code !== 200) {
    ElMessage.error('删除失败')
    return
  }
  ElMessage.success('删除成功')
  initActiveList()
}
const handleEditClick = (row: DoGetBargainListResponse) => {
  router.push({
    name: 'bargainBaseinfo',
    query: { activityId: row.id, shopId: row.shopId },
  })
}
const reasonViolation = ref('')
const handleShowIllegalReason = async (row: DoGetBargainListResponse) => {
  const { data } = await doGetIllegalReason(row.id)
  reasonViolation.value = data || '暂无违规原因'
}
const handleSearch = () => {
  initActiveList()
}
async function initActiveList() {
  const { status, keywords: keyword } = searchParams.value
  const params = { ...pageConfig.value, status, keyword }
  const { code, data } = await doGetAddBargain(params)
  if (code !== 200) return ElMessage.error('获取活动列表失败')
  activityList.value = data.records
  pageConfig.value.current = data.current
  pageConfig.value.size = data.size
  total.value = data.total
}
/**
 * 删除
 */
const handleDelClick = async (row: DoGetBargainListResponse) => {
  try {
    const isValidate = await ElMessageBox.confirm('确定删除该活动?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    if (!isValidate) return
    const { code, data } = await doDelAddBargain([{ shopId: row.shopId, activityId: row.id }])
    if (code !== 200) {
      ElMessage.error('删除失败')
      return
    }
    ElMessage.success('删除成功')
    activityList.value = activityList.value.filter((item) => item.id !== row.id)
    total.value--
  } catch (error) {
    error
  }
}
const handleSizeChange = (val: number) => {
  pageConfig.value.size = val
  initActiveList()
}
const handleCurrentChange = (val: number) => {
  pageConfig.value.current = val
  initActiveList()
}
onBeforeMount(() => {
  initActiveList()
})
</script>

<template>
  <div class="q_plugin_container">
    <head-search v-model="searchParams" @batch-del="handleBatchDel" @search="handleSearch" />
    <div class="table_container">
      <el-table
        ref="multipleTableRef"
        :data="activityList"
        :header-row-style="{ fontSize: '14px', color: '#333' }"
        :header-cell-style="{ background: '#f6f8fa', height: '50px' }"
        :cell-style="{ fontSize: '14px', color: '#333333' }"
        @selection-change="chooseList = $event"
      >
        <template #empty>
          <ElTableEmpty />
        </template>
        <el-table-column label="店铺名称" width="200">
          <template #default="{ row }: { row: DoGetBargainListResponse }">
            <span class="goods">{{ row.shopName }}</span>
          </template>
        </el-table-column>
        <el-table-column label="活动名称" width="200">
          <template #default="{ row }">
            <span class="goods">{{ row.name }}</span>
          </template>
        </el-table-column>
        <el-table-column label="活动状态" width="120">
          <template #default="{ row }: { row: DoGetBargainListResponse }">
            <span
              :style="{
                color: ['ILLEGAL_SELL_OFF'].includes(row.status) ? '#F54319' : ['OVER', 'SHOP_SELL_OFF'].includes(row.status) ? '#999' : '#333',
              }"
              >{{ statusReflect[row.status] }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="活动时间">
          <template #default="{ row }">
            <div>起：{{ row.startTime }}</div>
            <div>止：{{ row.endTime }}</div>
          </template>
        </el-table-column>
        <el-table-column label="活动商品" width="140">
          <template #default="{ row }">
            <span>{{ row.productNum }}</span>
          </template>
        </el-table-column>
        <el-table-column label="支付单数" width="140">
          <template #default="{ row }">
            <span>{{ row.payOrder }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="right" fixed="right">
          <template #default="{ row }: { row: DoGetBargainListResponse }">
            <el-tooltip
              v-if="['ILLEGAL_SELL_OFF'].includes(row.status)"
              class="box-item"
              effect="dark"
              :content="reasonViolation"
              placement="bottom-start"
            >
              <el-link :underline="false" type="primary" size="small" @mouseenter.once="handleShowIllegalReason(row)"> 违规原因 </el-link>
            </el-tooltip>
            <el-link
              v-if="['NOT_STARTED', 'PROCESSING'].includes(row.status)"
              :underline="false"
              type="danger"
              size="small"
              @click="toHandleOff(row)"
            >
              违规下架
            </el-link>
            <el-link :underline="false" type="primary" size="small" @click="handleEditClick(row)"> 查看 </el-link>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <!-- 好用的分页器 -->
    <page-manage
      v-model="pageConfig"
      :load-init="true"
      :total="total"
      @handle-size-change="handleSizeChange"
      @handle-current-change="handleCurrentChange"
      @reload="initActiveList"
    />
    <el-dialog v-model="dialogVisible" title="违规下架" width="500" center @close="handleClose">
      <el-form
        ref="illegalReasonFormRef"
        :model="illegalReasonForm"
        :rules="{
          violationReason: [{ required: true, message: '请输入违规原因', trigger: 'blur' }],
        }"
        status-icon
      >
        <el-form-item label="" prop="violationReason">
          <el-input
            v-model="illegalReasonForm.violationReason"
            placeholder="请输入违规原因，必填"
            type="textarea"
            maxlength="50"
            show-word-limit
            resize="none"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleOff">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style lang="scss" scoped>
@include b(goods) {
  @include utils-ellipsis;
}
:deep(.el-dialog__body) {
  padding-bottom: 0 !important;
}
:deep(.el-link + .el-link) {
  margin-left: 10px;
}
</style>
