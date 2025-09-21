<script lang="ts" setup>
import { defineExpose, defineProps, onBeforeMount, PropType, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import PageManage from '@/components/BetterPageManage/BetterPageManage.vue'
import { doGetApplyDiscountList, doPutSellOff } from '../apis'
import { ElMessage } from 'element-plus'
import { AllStatus, PageParams, PromotionFullReduction, SearchParam, Status, ViolationParam } from '../index'
import ElTableEmpty from '@/components/element-plus/el-table/ElTableEmpty/index.vue'

const router = useRouter()
const $props = defineProps({
  search: {
    type: Object as PropType<SearchParam>,
    default: () => ({}),
  },
})
const discountActiveList = ref<PromotionFullReduction[]>([])
const multipleTableRef = ref()
const pageConfig = reactive<PageParams>({
  size: 10,
  current: 1,
  total: 0,
})
const pageConfigTotal = ref(0)
const chooseList = ref<PromotionFullReduction[]>([])

const illegalReasonFormRef = ref<any>(null)
const illegalReasonForm = ref<ViolationParam>({
  shopId: null,
  id: '',
  violation: null,
})
const dialogVisible = ref(false)

async function initDiscountActiveList() {
  const params = { ...pageConfig, ...$props.search }
  const { code, data } = await doGetApplyDiscountList(params)
  if (code !== 200) return ElMessage.error('获取满减列表失败')
  discountActiveList.value = data.records
  pageConfig.current = data.current
  pageConfig.size = data.size
  pageConfigTotal.value = data.total
}

const toHandleOff = (row: PromotionFullReduction) => {
  illegalReasonForm.value.shopId = row.shopId
  illegalReasonForm.value.id = row.id
  dialogVisible.value = true
}

const handleClose = () => {
  illegalReasonFormRef.value.resetFields()
}
/**
 * 违规下架
 */
const handleOff = async () => {
  const validate = await illegalReasonFormRef.value.validate()
  if (!validate) {
    return
  }
  const { code, msg } = await doPutSellOff(illegalReasonForm.value)
  if (code !== 200) {
    ElMessage.error(msg)
    return
  }
  ElMessage.success('下架成功')
  const discountActiveItem = discountActiveList.value.find((item) => item.id === illegalReasonForm.value.id)
  if (discountActiveItem) {
    // 下架
    discountActiveItem.status = Status.VIOLATION_OFF_SHELF
  }
  dialogVisible.value = false
}
const handleNavToView = (row: PromotionFullReduction) => {
  router.push({
    name: 'applyDiscountBaseinfo',
    query: { id: row.id, shopId: row.shopId },
  })
}

/**
 * 分页器
 */
const handleSizeChange = (value: number) => {
  pageConfig.size = value
  initDiscountActiveList()
}
const handleCurrentChange = (value: number) => {
  pageConfig.current = value
  initDiscountActiveList()
}

onBeforeMount(() => {
  initDiscountActiveList()
})
defineExpose({ chooseList, initDiscountActiveList })
</script>

<template>
  <div style="padding: 0 16px" class="table_container">
    <el-table
      ref="multipleTableRef"
      :cell-style="{ color: '#333333' }"
      :data="discountActiveList"
      :header-cell-style="{ background: '#f6f8fa', height: '48px' }"
      :header-row-style="{ color: '#000' }"
      @selection-change="chooseList = $event"
    >
      <!-- 暂无数据 -->
      <template #empty>
        <ElTableEmpty imageIndex="7" />
      </template>
      <el-table-column label="店铺名称" width="200">
        <template #default="{ row }: { row: PromotionFullReduction }">
          <div class="name">{{ row.shopName }}</div>
        </template>
      </el-table-column>
      <el-table-column label="活动名称" width="200">
        <template #default="{ row }: { row: PromotionFullReduction }">
          <div class="name">{{ row.name }}</div>
        </template>
      </el-table-column>
      <el-table-column label="类型" width="100">
        <template #default="{ row }: { row: PromotionFullReduction }">
          <div class="name"><span v-if="row.firstRuleType === 'FULL_REDUCTION'">满减</span><span v-else>满折</span></div>
        </template>
      </el-table-column>
      <el-table-column label="使用规则" width="210">
        <template #default="{ row }: { row: PromotionFullReduction }">
          <div class="name">{{ row.firstRuleDesc }}</div>
        </template>
      </el-table-column>
      <el-table-column label="状态" prop="status" width="120">
        <template #default="{ row }: { row: PromotionFullReduction }">
          <span
            :style="{
              color: [Status.VIOLATION_OFF_SHELF].includes(row.status)
                ? '#F54319'
                : [Status.FINISHED, Status.OFF_SHELF].includes(row.status)
                ? '#999'
                : '#333',
            }"
            >{{ AllStatus[row.status]?.label }}</span
          >
        </template>
      </el-table-column>
      <el-table-column label="活动时间" width="250">
        <template #default="{ row }: { row: PromotionFullReduction }">
          <p>起：{{ row.startTime }}</p>
          <p>止：{{ row.endTime }}</p>
        </template>
      </el-table-column>
      <el-table-column label="活动商品" width="120">
        <template #default="{ row }: { row: PromotionFullReduction }">
          <div class="name">
            {{
              row.productType === 'ALL_PRODUCT' ? '全部' : `${row.product}${row.productType === 'SPECIFIED_PRODUCT_NOT_PARTICIPATE' ? '(除外)' : ''}`
            }}
          </div>
        </template>
      </el-table-column>
      <el-table-column label="支付单数" width="120">
        <template #default="{ row }: { row: PromotionFullReduction }">
          <div class="name">{{ row.order }}</div>
        </template>
      </el-table-column>
      <el-table-column fixed="right" label="操作" align="right" width="170">
        <template #default="{ row }: { row: PromotionFullReduction }">
          <el-tooltip
            v-if="Status.VIOLATION_OFF_SHELF === row.status"
            class="box-item"
            effect="dark"
            :content="row.violation || '暂无违规原因'"
            placement="bottom-end"
          >
            <el-link :underline="false" size="small" type="primary"> 违规原因 </el-link>
          </el-tooltip>
          <el-link
            v-if="[Status.NOT_STARTED, Status.IN_PROGRESS].includes(row.status)"
            :underline="false"
            size="small"
            type="danger"
            @click="toHandleOff(row)"
          >
            违规下架
          </el-link>
          <el-link :underline="false" size="small" type="primary" @click="handleNavToView(row)">查看</el-link>
        </template>
      </el-table-column>
    </el-table>
  </div>
  <div class="pagination">
    <!-- 好用的分页器 -->
    <PageManage
      :load-init="true"
      :page-num="pageConfig.current"
      :page-size="pageConfig.size"
      :total="pageConfigTotal"
      @handle-current-change="handleCurrentChange"
      @handle-size-change="handleSizeChange"
    />
  </div>
  <el-dialog v-model="dialogVisible" center title="违规下架" width="500" @close="handleClose">
    <el-form
      ref="illegalReasonFormRef"
      :model="illegalReasonForm"
      :rules="{
        violation: [{ required: true, message: '请输入违规原因', trigger: 'blur' }],
      }"
      status-icon
    >
      <el-form-item label="" prop="violation">
        <el-input
          v-model="illegalReasonForm.violation"
          maxlength="50"
          placeholder="请输入违规原因，必填"
          resize="none"
          show-word-limit
          type="textarea"
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
</template>

<style lang="scss" scoped>
@include b(name) {
  @include utils-ellipsis;
}

.flex_link_container {
  display: flex;
  align-items: center;
}

:deep.el-link + .el-link {
  margin-left: 10px;
}

:deep(.el-dialog__body) {
  padding-bottom: 0 !important;
}
</style>
