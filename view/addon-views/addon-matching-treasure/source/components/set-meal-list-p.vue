<script setup lang="ts">
import { ref, reactive, defineProps, onBeforeMount, watch, PropType, defineExpose } from 'vue'
import { useRouter } from 'vue-router'
import { AddSerMealStatus, MealListType, DoPostSetMealQuery } from '../index'
import PageManage from '@/components/PageManage.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { doGetSetMeal, doDelSetMeal, doPostSetMealDel } from '../apis'
import ElTableEmpty from '@/components/element-plus/el-table/ElTableEmpty/index.vue'

const router = useRouter()
const $props = defineProps({
  search: {
    type: Object as PropType<any>,
    default: () => ({}),
  },
  showOffShelfBtn: {
    type: Boolean,
    default: true,
  },
})
const activeList = ref<MealListType[]>([])
const multipleTableRef = ref()
const pageConfig = reactive({
  size: 10,
  current: 1,
  total: 0,
})
const chooseList = ref<any[]>([])
const illegalReasonForm = ref({ shopId: '', setMealId: '', violationExplain: '' })
const illegalReasonFormRef = ref()
const dialogVisible = ref(false)

watch(
  () => $props.search,
  (val) => {
    // 搜索数据
    initactiveList()
  },
  {
    deep: true,
  },
)

async function initactiveList() {
  const { setMealStatus, keyword } = $props.search
  let searchObj = {
    setMealStatus,
    keyword,
  }
  const params = { ...pageConfig, ...searchObj }
  const { code, data } = await doGetSetMeal(params)
  if (code !== 200) return ElMessage.error('获取套餐列表失败')
  activeList.value = data.records
  pageConfig.current = data.current
  pageConfig.size = data.size
  pageConfig.total = data.total
}
/**
 * 导航去编辑页面
 */
const handleNavToEditPage = (row: MealListType, isLookUp: 'true' | 'false' | '') => {
  router.push({
    name: 'bundlePriceBaseinfo',
    query: { setMealId: row.id, shopId: row.shopId, isLookUp },
  })
}
/**
 * 单个删除
 */
const handleDelClick = async (row: any) => {
  if (row.setMealStatus === 'PROCESSING') {
    ElMessage.warning('进行中的活动不能被删除')
    return
  }
  try {
    const isValidate = await ElMessageBox.confirm('确定删除该套餐?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    if (!isValidate) return
    const { code } = await doDelSetMeal([{ setMealId: row.id, shopId: row.shopId }])
    if (code !== 200) {
      ElMessage.error('删除失败')
      return
    }
    ElMessage.success('删除成功')
    initactiveList()
  } catch (error) {
    return
  }
}
/**
 * 批量删除
 */
const handleDelBatch = async (ids: string[]) => {
  const { code } = await doDelSetMeal(ids)
  if (code !== 200) {
    ElMessage.error('删除失败')
    return
  }
  ElMessage.success('删除成功')
  initactiveList()
}
/**
 * 分页器
 */
const handleSizeChange = (value: number) => {
  pageConfig.current = 1
  pageConfig.size = value
  initactiveList()
}
const handleCurrentChange = (value: number) => {
  pageConfig.current = value
  initactiveList()
}
const toHandleOff = (row: MealListType) => {
  dialogVisible.value = true
  illegalReasonForm.value.shopId = row.shopId
  illegalReasonForm.value.setMealId = row.id
}

const handleClose = () => {
  illegalReasonFormRef.value.resetFields()
}
/**
 * 违规下架
 */
const handlePostClick = () => {
  illegalReasonFormRef.value.validate(async (valid: boolean, fields: string) => {
    if (valid) {
      const { code, msg } = await doPostSetMealDel(illegalReasonForm.value)
      if (code !== 200) {
        ElMessage.error(msg)
        return
      }
      ElMessage.success('下架成功')
      initactiveList()
      dialogVisible.value = false
    } else {
      console.log('error submit!', fields)
    }
  })
}
initactiveList()

defineExpose({ chooseList, handleDelBatch })
</script>

<template>
  <div class="table_container">
    <el-table
      ref="multipleTableRef"
      class="table_container"
      :data="activeList"
      :header-row-style="{ color: '#333' }"
      :header-cell-style="{ background: '#f6f8fa', height: '50px' }"
      :cell-style="{ color: '#333333' }"
    >
      <!-- 暂无数据 -->
      <template #empty>
        <ElTableEmpty imageIndex="7" />
      </template>
      <el-table-column label="店铺名称" width="200">
        <template #default="{ row }: { row: DoPostSetMealQuery }">
          <div class="name">{{ row.shopName }}</div>
        </template>
      </el-table-column>
      <el-table-column label="套餐名称" width="230px">
        <template #default="{ row }">
          <div class="origin">
            <el-image style="width: 52px; height: 52px" :src="row.setMealMainPicture" />
            <div class="origin--name" style="width: 180px; margin-left: 10px">{{ row.setMealName }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="套餐类型" width="150">
        <template #default="{ row }"> {{ row.setMealType === 'OPTIONAL_PRODUCT' ? '自选套餐' : '固定套餐' }} </template>
      </el-table-column>
      <el-table-column label="状态" width="150">
        <template #default="{ row }: { row: DoPostSetMealQuery }">
          <span
            :style="{
              color: ['ILLEGAL_SELL_OFF'].includes(row.setMealStatus)
                ? '#F54319'
                : ['OVER', 'MERCHANT_SELL_OFF'].includes(row.setMealStatus)
                ? '#999'
                : '#333',
            }"
            >{{ AddSerMealStatus[row.setMealStatus] }}</span
          >
        </template>
      </el-table-column>
      <el-table-column label="活动时间" width="230px">
        <template #default="{ row }">
          <p>起：{{ row.startTime }}</p>
          <p>止：{{ row.endTime }}</p>
        </template>
      </el-table-column>
      <el-table-column label="活动商品" width="120">
        <template #default="{ row }">{{ row.productCount }}</template>
      </el-table-column>
      <el-table-column label="支付单数" width="120">
        <template #default="{ row }">{{ row.orderCount }}</template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" align="right" width="150">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleNavToEditPage(row, 'true')">查看</el-button>
          <el-button v-if="['NOT_STARTED', 'PROCESSING'].includes(row.setMealStatus)" link type="danger" @click="toHandleOff(row)"
            >违规下架</el-button
          >

          <el-tooltip
            v-if="row.setMealStatus === 'ILLEGAL_SELL_OFF'"
            class="box-item"
            effect="dark"
            :content="row.violationExplain"
            placement="bottom-end"
          >
            <el-button link type="primary">违规原因</el-button>
          </el-tooltip>
        </template>
      </el-table-column>
    </el-table>
  </div>

  <!-- 好用的分页器 -->
  <PageManage
    class="pagination"
    :page-num="pageConfig.current"
    :load-init="true"
    :page-size="pageConfig.size"
    :total="pageConfig.total"
    @reload="initactiveList"
    @handle-size-change="handleSizeChange"
    @handle-current-change="handleCurrentChange"
  />
  <el-dialog v-model="dialogVisible" title="违规下架" width="500" center @close="handleClose">
    <el-form
      ref="illegalReasonFormRef"
      :model="illegalReasonForm"
      :rules="{
        violationExplain: [{ required: true, message: '请输入违规原因', trigger: 'blur' }],
      }"
      status-icon
    >
      <el-form-item label="" prop="violationExplain">
        <el-input
          v-model="illegalReasonForm.violationExplain"
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
        <el-button type="primary" @click="handlePostClick">确定</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<style scoped lang="scss">
@include b(name) {
  @include utils-ellipsis;
}
@include b(origin) {
  @include flex(start);
  @include m(name) {
    @include utils-ellipsis(2);
  }
}
:deep(.el-dialog__body) {
  padding-bottom: 0 !important;
}
:deep(.el-button.is-link) {
  padding: 0;
}
:deep(.el-button + .el-button) {
  margin-left: 10px;
}
</style>
