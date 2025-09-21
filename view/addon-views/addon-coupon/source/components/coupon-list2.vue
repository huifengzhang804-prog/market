<script lang="ts" setup>
import { ref, defineProps, PropType, defineExpose } from 'vue'
import { useRouter } from 'vue-router'
import { CouponType, couponIndexType } from '../index'
import PageManage from '@/components/BetterPageManage/BetterPageManage.vue'
import { doGetCouponList, doDelCouponByPlatform, doIllegalDelisting, doGetIllegalReason } from '../apis'
import { ElMessage } from 'element-plus'
import type { Operation } from './head-operation2.vue'
import type { CouponDTO } from '../index'
import useConvert from '@/composables/useConvert'
import { Coupon } from '../types'
import ElTableEmpty from '@/components/element-plus/el-table/ElTableEmpty/index.vue'

const router = useRouter()
const $props = defineProps({
  search: {
    type: Object as PropType<Operation>,
    default: () => ({}),
  },
})
const { divTenThousand } = useConvert()
const couponList = ref<Coupon[]>([])
const multipleTableRef = ref()
const pageConfig = ref({
  size: 10,
  current: 1,
  total: 0,
})
const chooseList = ref<Operation[]>([])
const illegalReasonForm = ref({ shopId: '', couponId: '', violationReason: '' })
const illegalReasonFormRef = ref()
const dialogVisible = ref(false)

initCouponList()

async function initCouponList() {
  const params = { ...pageConfig.value, ...$props.search }
  const { code, data } = await doGetCouponList(params)
  if (code !== 200) return ElMessage.error('获取优惠券列表失败')
  couponList.value = data.records
  pageConfig.value.current = data.current
  pageConfig.value.size = data.size
  pageConfig.value.total = data.total
}

const toHandleOff = (row: CouponDTO) => {
  dialogVisible.value = true
  illegalReasonForm.value.shopId = row.shopId
  illegalReasonForm.value.couponId = row.id
}

const handleClose = () => {
  illegalReasonFormRef.value.resetFields()
}

/**
 * 下架
 */
const handleOff = async () => {
  await illegalReasonFormRef.value.validate(async (valid: boolean, fields: string) => {
    if (valid) {
      const { code, msg } = await doIllegalDelisting(illegalReasonForm.value)
      if (code !== 200) {
        ElMessage.error(msg)
        return
      }
      ElMessage.success('下架成功')
      const coupon = couponList.value.find((item) => item.id === illegalReasonForm.value.couponId)
      if (coupon) {
        // 下架
        coupon.status = 'BANED'
      }
      dialogVisible.value = false
      initCouponList()
    } else {
      console.log('error submit!', fields)
    }
  })
}
const handleNavToView = (row: CouponDTO) => {
  router.push({
    name: 'couponBaseInfo',
    query: { id: row.id, shopId: row.shopId },
  })
}
const reasonViolation = ref('')
const rowId = ref('')
const handleShowIllegalReason = async (row: CouponDTO) => {
  if (rowId.value !== row.id) {
    rowId.value = row.id
    const { data } = await doGetIllegalReason(row.id)
    reasonViolation.value = data || '暂无违规原因'
  }
}
/**
 * 批量删除
 */
const handleDelBatch = async (couponKeys: { shopId: string; couponId: string }[]) => {
  const { code, msg } = await doDelCouponByPlatform(couponKeys)
  if (code !== 200) {
    ElMessage.error(msg || '删除失败')
    return
  }
  ElMessage.success('删除成功')
  initCouponList()
}
/**
 * 分页器
 */
const handleSizeChange = (value: number) => {
  pageConfig.value.current = 1
  pageConfig.value.size = value
  initCouponList()
}
const handleCurrentChange = (value: number) => {
  pageConfig.value.current = value
  initCouponList()
}
defineExpose({ chooseList, handleDelBatch, initCouponList })
</script>

<template>
  <div class="table_container">
    <el-table
      ref="multipleTableRef"
      :cell-style="{ fontSize: '12px', color: '#333333' }"
      :data="couponList"
      style="width: 100%"
      :header-cell-style="{ background: '#f6f8fa', height: '50px' }"
      :header-row-style="{ fontSize: '14px', color: '#000' }"
    >
      <template #empty>
        <ElTableEmpty />
      </template>
      <!-- <el-table-column type="selection" width="55" /> -->
      <el-table-column label="店铺名称" width="220">
        <template #default="{ row }">
          <div class="name">{{ row.shopName }}</div>
        </template>
      </el-table-column>
      <el-table-column label="活动名称" width="220">
        <template #default="{ row }">
          <div class="name">{{ row.name }}</div>
        </template>
      </el-table-column>
      <el-table-column label="类型" width="100">
        <template #default="{ row }">{{ couponIndexType[(row as Coupon).type] }}</template>
      </el-table-column>
      <el-table-column label="使用规则" width="210">
        <template #default="{ row }">
          <div v-if="row.type === CouponType.PRICE_REDUCE">
            <span>{{ row.amount && divTenThousand(row.amount) }}</span>
            <span>元，无门槛使用</span>
          </div>
          <div v-if="row.type === CouponType.PRICE_DISCOUNT">
            <span>{{ row.discount }}</span>
            <span>折，无门槛使用</span>
          </div>
          <div v-if="row.type === CouponType.REQUIRED_PRICE_DISCOUNT">
            <span>满</span>
            <span>{{ row.requiredAmount && divTenThousand(row.requiredAmount) }}</span>
            <span>元，打</span>
            <span> {{ row.discount }}</span>
            <span>折</span>
          </div>
          <div v-if="row.type === CouponType.REQUIRED_PRICE_REDUCE">
            <span>满</span>
            <span>{{ row.requiredAmount && divTenThousand(row.requiredAmount) }}</span>
            <span>元，减</span>
            <span> {{ row.amount && divTenThousand(row.amount) }}</span>
            <span>元</span>
          </div>
          <span>{{ row.rules }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" prop="address" width="100">
        <template #default="{ row }">
          <span
            :style="{
              color: row.statusText === '违规下架' ? '#F54319' : ['已下架', '已结束'].includes(row.statusText) ? '#999' : '#333',
            }"
            >{{ row.statusText }}</span
          >
        </template>
      </el-table-column>
      <el-table-column label="有效期" width="200">
        <template #default="{ row }">
          <div v-if="row.startDate">
            <div>起：{{ row.startDate }}</div>
            <div>止：{{ row.endDate }}</div>
          </div>
          <div v-else>
            <div>领券当日起{{ row.days }}天内可用</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="已领取" width="120">
        <template #default="{ row }">
          {{ row.num - row.stock }}
        </template>
      </el-table-column>
      <el-table-column label="剩余" prop="stock" width="120">
        <template #default="{ row }">
          {{ row.stock }}
        </template>
      </el-table-column>
      <el-table-column label="已使用" prop="usedCount" width="120" />
      <el-table-column align="right" fixed="right" class="el-table__fixed" label="操作" width="200">
        <template #default="{ row }">
          <el-link :underline="false" size="small" style="padding: 0 5px 0 0" type="primary" @click="handleNavToView(row)"> 查看 </el-link>
          <el-tooltip v-if="['BANED'].includes(row.status)" class="box-item" effect="dark" :content="reasonViolation" placement="bottom-end">
            <el-link :underline="false" size="small" style="padding: 0 5px" type="primary" @mouseover="handleShowIllegalReason(row)">
              违规原因
            </el-link>
          </el-tooltip>
          <el-link
            v-if="['未开始', '进行中'].includes(row.statusText)"
            :underline="false"
            size="small"
            style="padding: 0 5px"
            type="danger"
            @click="toHandleOff(row)"
            >违规下架
          </el-link>
        </template>
      </el-table-column>
    </el-table>
  </div>
  <div class="pagination">
    <!-- 好用的分页器 -->
    <page-manage
      :page-size="pageConfig.size"
      :page-num="pageConfig.current"
      :total="pageConfig.total"
      :load-init="true"
      @handle-size-change="handleSizeChange"
      @handle-current-change="handleCurrentChange"
    />
  </div>
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
</template>

<style lang="scss" scoped>
* {
  font-size: 14px;
}
@include b(name) {
  @include utils-ellipsis;
}
:deep(.el-dialog__body) {
  padding-bottom: 0 !important;
}
</style>
