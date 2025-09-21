<script lang="ts" setup>
import { ref, defineProps, watch, PropType, defineExpose } from 'vue'
import { useRouter } from 'vue-router'
import { couponIndexType, CouponType, CouponJointType, CouponDTO } from '../index'
import PageManage from '@/components/BetterPageManage/BetterPageManage.vue'
import useConvert from '@/composables/useConvert'
import { doGetCouponList, doDelCoupon, doPutBanCouponSingle, doGetIllegalReason } from '../apis'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { Operation } from './head-operation.vue'

const router = useRouter()
const $props = defineProps({
  search: {
    type: Object as PropType<Operation>,
    default: () => ({}),
  },
  batchDisabled: {
    type: Boolean,
    default: true,
  },
})
const { divTenThousand } = useConvert()
const couponList = ref<CouponDTO[]>([])
const multipleTableRef = ref()
const pageConfig = ref({
  size: 10,
  current: 1,
  total: 0,
})
const chooseList = ref<CouponDTO[]>([])
const emit = defineEmits(['addCoupon', 'delCoupon'])

function compareObjects(newObj, oldObj) {
  return Object.keys(newObj).filter((key) => !(newObj[key] === oldObj[key]))
}

watch(
  () => ({ ...$props.search }),
  (newVal, oldVal) => {
    if (compareObjects(newVal, oldVal).includes('keywords')) return
    // 搜索数据
    initCouponList()
  },
  {
    deep: true,
  },
)

async function initCouponList() {
  const { status, type, keywords } = $props.search
  let searchObj = {
    status,
    type,
    keywords,
  }
  const params = { ...pageConfig.value, ...searchObj }
  const { code, data } = await doGetCouponList(params)
  if (code !== 200) return ElMessage.error('获取优惠券列表失败')
  couponList.value = data.records
  pageConfig.value.current = data.current
  pageConfig.value.size = data.size
  pageConfig.value.total = data.total
}
initCouponList()

/**
 * 导航去编辑页面
 */
const handleNavToEditPage = (row: CouponDTO, isLookUp: 'true' | 'false' | '') => {
  router.push({
    path: '/coupons/baseInfo',
    query: { id: row.id, shopId: row.shopId, isLookUp },
  })
}
const reasonViolation = ref('')
const handleShowIllegalReason = async (row: CouponDTO) => {
  const { data } = await doGetIllegalReason(row.id)
  reasonViolation.value = data
  // ElMessageBox.alert(data || '暂无违规原因', '违规原因', {
  //     center: true,
  //     showConfirmButton: false,
  // })
}
/**
 * 商家下架优惠券
 */
const handlePopupOff = async (row: CouponDTO) => {
  const isValidate = await ElMessageBox.confirm('确定下架该优惠券?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
  if (!isValidate) return
  const { shopId, id: couponId } = row
  const { success } = await doPutBanCouponSingle({ shopId, couponId })
  if (success) {
    ElMessage.success('下架成功')
    initCouponList()
  } else {
    ElMessage.error('下架失败')
  }
}
/**
 * 单个删除
 */
const handleDelClick = async (id: string) => {
  const isValidate = await ElMessageBox.confirm('确定删除该优惠券?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
  if (!isValidate) return
  const { msg, code, data } = await doDelCoupon([id])
  if (code !== 200) {
    ElMessage.error(msg || '删除失败')
    return
  }
  ElMessage.success('删除成功')
  couponList.value = couponList.value.filter((item) => item.id !== id)
}

// tableCheck选中
const handleTableSelect = (selection: CouponDTO[]) => {
  chooseList.value = selection
}

/**
 * 批量删除
 */
const handleDelBatch = async (ids: string[]) => {
  const { msg, code, data } = await doDelCoupon(ids)
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
  pageConfig.value.size = value
  initCouponList()
}
const handleCurrentChange = (value: number) => {
  pageConfig.value.current = value
  initCouponList()
}
defineExpose({ chooseList, handleDelBatch, initCouponList, multipleTableRef })
</script>

<template>
  <div class="handle_container" style="padding-top: 16px">
    <el-button round type="primary" @click="emit('addCoupon')">新增优惠券</el-button>
    <el-button :disabled="$props.batchDisabled" round @click="emit('delCoupon')">批量删除</el-button>
  </div>
  <div class="table_container">
    <el-table
      ref="multipleTableRef"
      :cell-style="{ color: '#333333' }"
      :data="couponList"
      :header-cell-style="{ background: '#F7F8FA', height: '48px' }"
      :header-row-style="{ color: '#333' }"
      @selection-change="handleTableSelect"
    >
      <el-table-column type="selection" width="40" fixed="left" :selectable="(row) => !['进行中'].includes(row.statusText)" />
      <el-table-column label="优惠券名称" width="180">
        <template #default="{ row }">
          <div class="name">{{ row.name }}</div>
        </template>
      </el-table-column>
      <el-table-column label="优惠券类型" width="130">
        <template #default="{ row }">
          <span>{{ couponIndexType[row.type as CouponJointType] }}</span>
        </template>
      </el-table-column>
      <el-table-column label="使用规则" width="170">
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
      <el-table-column label="状态" prop="address" width="90">
        <template #default="{ row }">
          <div
            :style="{
              color: row.statusText === '违规下架' ? '#F54319' : ['已下架', '已结束'].includes(row.statusText) ? '#999' : '#333',
            }"
          >
            {{ row.statusText }}
          </div>
        </template>
      </el-table-column>
      <el-table-column label="有效期" width="210">
        <template #default="{ row }">
          <div v-if="row.startDate" class="fdc">
            <div>起：{{ row.startDate }}</div>
            <div>止：{{ row.endDate }}</div>
          </div>
          <div v-else>
            <div>领券当日起{{ row.days }}天内可用</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="已领取" prop="date">
        <template #default="{ row }">
          <span>{{ row.num - row.stock }}</span>
        </template>
      </el-table-column>
      <el-table-column label="剩余" prop="date">
        <template #default="{ row }">
          <span>{{ row.stock }}</span>
        </template>
      </el-table-column>
      <el-table-column label="已使用" prop="usedCount" />
      <el-table-column fixed="right" label="操作" prop="address" align="right" width="180">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleNavToEditPage(row, 'true')">查看</el-button>
          <el-popover
            v-if="row.statusText === '违规下架'"
            placement="bottom-start"
            :width="reasonViolation.length > 32 ? 440 : ''"
            trigger="hover"
            effect="dark"
            :content="reasonViolation"
            @show="handleShowIllegalReason(row)"
          >
            <template #reference>
              <el-button link type="primary">违规原因</el-button>
            </template>
          </el-popover>

          <el-button v-if="['进行中'].includes(row.statusText)" link type="primary" @click="handlePopupOff(row)">下架 </el-button>
          <el-button v-else link type="danger" @click="handleDelClick(row.id)">删除</el-button>
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
</template>

<style scoped>
.name {
  width: 120px;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}
:deep(.el-button.is-link) {
  padding: 0;
}
:deep(.el-button + .el-button) {
  margin-left: 10px;
}
</style>
