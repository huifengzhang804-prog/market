<script setup lang="ts">
import { ref, onBeforeMount } from 'vue'
import HeadSearch from '../components/head-search-p.vue'
import PageManage from '@/components/PageManage.vue'
import { doGetGroupList, doDelGroupActivity, doPutGroupActivityStatus, doGetIllegalReason } from '../apis'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ApiGroupList } from '../index'
import 'uno.css'
import ElTableEmpty from '@/components/element-plus/el-table/ElTableEmpty/index.vue'

const searchParams = ref({
  keywords: '',
  status: '',
})
const pageConfig = ref({
  size: 10,
  current: 1,
})
const total = ref(0)
const router = useRouter()
const activityList = ref<ApiGroupList[]>([])
const chooseList = ref<ApiGroupList[]>([])
const statusReflect = {
  OPENING: '未开始',
  PREHEAT: '预热中',
  OPEN: '进行中',
  FINISHED: '已结束',
  VIOLATION: '违规下架',
  SHOP_OFF_SHELF: '已下架',
}
const modeType = {
  COMMON: '普通拼团',
  STAIRS: '阶梯拼团',
}
const illegalReasonForm = ref({ shopId: '', teamActivityId: '', violationReason: '' })
const illegalReasonFormRef = ref()
const dialogVisible = ref(false)

const toHandleOff = (row: ApiGroupList) => {
  dialogVisible.value = true
  illegalReasonForm.value.shopId = row.shopId as any
  illegalReasonForm.value.teamActivityId = row.id
}

const handleClose = () => {
  illegalReasonFormRef.value.resetFields()
}
const handleOff = () => {
  illegalReasonFormRef.value.validate(async (valid: boolean, fields: string) => {
    if (valid) {
      const { code, msg } = await doPutGroupActivityStatus(illegalReasonForm.value)
      if (code !== 200) {
        ElMessage.error(msg)
        return
      }
      ElMessage.success('下架成功')
      const currentActivity = activityList.value.find((item) => item.id === illegalReasonForm.value.teamActivityId)
      if (currentActivity) {
        currentActivity.status = 'VIOLATION'
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
  const { code, data } = await doDelGroupActivity(params)
  if (code !== 200) {
    ElMessage.error('删除失败')
    return
  }
  ElMessage.success('删除成功')
  initGroupList()
}
const handleEditClick = (row: ApiGroupList) => {
  router.push({
    name: 'groupDetail',
    query: { activityId: row.id, shopId: row.shopId },
  })
}
const reasonViolation = ref('')
const handleShowIllegalReason = async (row: ApiGroupList) => {
  if (row.rejectReason && reasonViolation) {
    return
  }
  const { data } = await doGetIllegalReason(row.id)
  reasonViolation.value = data || '暂无违规原因'
  row.rejectReason = true
}
const handleSearch = () => {
  initGroupList()
}
async function initGroupList() {
  const { status, keywords: keyword } = searchParams.value
  const params = { ...pageConfig.value, status, keyword }
  const { code, data } = await doGetGroupList(params)
  if (code !== 200) return ElMessage.error('获取活动列表失败')
  activityList.value = data.records.map((item) => {
    return {
      ...item,
      rejectReason: false,
    }
  })
  pageConfig.value.current = data.current
  pageConfig.value.size = data.size
  total.value = data.total
}

/**
 * 删除
 */
const handleDelClick = async (row: ApiGroupList) => {
  try {
    const isValidate = await ElMessageBox.confirm('确定删除该活动?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    if (!isValidate) return
    const { code, data } = await doDelGroupActivity([{ shopId: row.shopId, activityId: row.id }])
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
  initGroupList()
}
const handleCurrentChange = (val: number) => {
  pageConfig.value.current = val
  initGroupList()
}

onBeforeMount(() => {
  initGroupList()
})
</script>

<template>
  <div class="q_plugin_container">
    <head-search v-model="searchParams" :showBatchDelBtn="false" @batch-del="handleBatchDel" @search="handleSearch" />
    <div class="table_container">
      <el-table
        ref="multipleTableRef"
        :data="activityList"
        :header-row-style="{ fontSize: '14px', color: '#333' }"
        :header-cell-style="{ background: '#f6f8fa', height: '50px' }"
        :cell-style="{ fontSize: '14px', color: '#333333' }"
        @selection-change="chooseList = $event"
      >
        <!-- 暂无数据 -->
        <template #empty>
          <ElTableEmpty imageIndex="7" />
        </template>
        <el-table-column label="店铺名称" width="180">
          <template #default="{ row }">
            <span class="goods">{{ row.shopName }}</span>
          </template>
        </el-table-column>
        <el-table-column label="活动名称" width="180">
          <template #default="{ row }">
            <span>{{ row.name }}</span>
          </template>
        </el-table-column>
        <el-table-column label="拼团模式" width="120">
          <template #default="{ row }">{{ modeType[row.mode as keyof typeof modeType] }}</template>
        </el-table-column>
        <el-table-column label="状态" width="120">
          <template #default="{ row }: { row: ApiGroupList }">
            <span
              :style="{
                color: ['VIOLATION'].includes(row.status) ? '#F54319' : ['FINISHED', 'SHOP_OFF_SHELF'].includes(row.status) ? '#999' : '#333',
              }"
              >{{ statusReflect[row.status as keyof typeof statusReflect] }}</span
            >
          </template>
        </el-table-column>
        <el-table-column label="活动时间" width="210">
          <template #default="{ row }">
            <div>起：{{ row.startTime }}</div>
            <div>止：{{ row.endTime }}</div>
          </template>
        </el-table-column>
        <el-table-column label="活动商品" width="120">
          <template #default="{ row }">
            <span>{{ row.productNum }}</span>
          </template>
        </el-table-column>
        <el-table-column label="支付单数" align="center" width="120">
          <template #default="{ row }">
            <span>{{ row.orders }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="right" width="150px" fixed="right">
          <template #default="{ row }">
            <el-link :underline="false" type="primary" size="small" @click="handleEditClick(row)"> 查看 </el-link>
            <el-tooltip v-if="['VIOLATION'].includes(row.status)" :visible="row.rejectReason">
              <template #content>
                <span>{{ reasonViolation }}</span>
              </template>
              <el-link
                type="primary"
                size="small"
                :underline="false"
                @mouseenter="handleShowIllegalReason(row)"
                @mouseleave="row.rejectReason = false"
              >
                违规原因
              </el-link>
            </el-tooltip>
            <el-link
              v-if="['OPENING', 'PREHEAT', 'OPEN'].includes(row.status)"
              :underline="false"
              type="danger"
              size="small"
              @click="toHandleOff(row)"
            >
              违规下架
            </el-link>
            <!-- <el-link :underline="false" type="primary" size="small" @click="handleDelClick(row)"> 删除 </el-link> -->
          </template>
        </el-table-column>
      </el-table>
    </div>
    <!-- 好用的分页器 -->
    <page-manage
      class="pagination"
      :page-size="pageConfig.size"
      :page-num="pageConfig.current"
      :load-init="true"
      :total="total"
      @handle-size-change="handleSizeChange"
      @handle-current-change="handleCurrentChange"
      @reload="initGroupList"
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

<style scoped>
:deep(.el-dialog__body) {
  padding-bottom: 0 !important;
}
:deep(.el-link + .el-link) {
  margin-left: 10px;
}
</style>
