<script lang="ts" setup>
import { type Ref, ref, inject, watch } from 'vue'
import { Shop } from '@element-plus/icons-vue'
import QTable from '@/components/qszr-core/packages/q-table/QTable'
import QTableColumn from '@/components/qszr-core/packages/q-table/q-table-column.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import storage from '@/utils/Storage'
import QTooltip from '@/components/q-tooltip/q-tooltip.vue'
import { doChangeStatus, doDelShop, doShopAudit, doReAudit, doGetShopRejectReason } from '@/apis/shops'
import useConvert from '@/composables/useConvert'

const parentTabChangeHandle = inject('parentTabChangeHandle') as (v: string) => void
const parentTabChoose = inject('parentTabChoose') as Ref
const $router = useRouter()
const { divTenThousand } = useConvert()
const $props = defineProps({
  tableList: {
    type: Array,
    default() {
      return []
    },
  },
  currentTabChoose: {
    type: String,
    required: true,
  },
  isSelect: {
    type: Boolean,
  },
  isExamineShop: {
    type: Boolean,
    default: false,
  },
  tabRadio: {
    type: String,
    default: () => 'supplierList',
  },
})
const SUPPLIER_TYPE = { SELF_OWNED: '自营供应商', PREFERRED: '优选供应商', ORDINARY: '普通供应商' }
const dialogFormVisible = ref(false)
const refuseVisible = ref(false)
const refuse = ref('')
const shopId = ref('')
const $emit = defineEmits(['getStoreStatus', 'refresh'])
const oneLineDisplay = (str: string) => {
  return /^\d+$/.test(str) || /^[a-zA-Z]+$/.test(str) || /^[a-zA-Z0-9]+$/.test(str)
}
const multipleSelection = ref<any[]>([])

/**
 * 待审核列表的批量拒绝
 */
const shopAudit = (shopId: string, isPass: boolean) => {
  ElMessageBox.confirm(`确认${isPass ? '通过' : '拒绝'}当前供应商审核?`).then(() => {
    const query = { shopId: shopId, passFlag: isPass }
    doShopAudit(query).then((res) => {
      if (res.code !== 200) {
        ElMessage.error(res.msg || '更新失败')
        return
      }
      ElMessage.success('已更新')
      $emit('refresh', 'UNDER_REVIEW')
      parentTabChangeHandle('UNDER_REVIEW')
    })
  })
}

const refuseHandle = (id: string) => {
  refuseVisible.value = true
  shopId.value = id
}
/**
 * 店铺审核拒绝
 */
const confirmRejection = () => {
  const refuseTest = refuse.value
  if (refuseTest === '') {
    ElMessage.warning('请填写拒绝原因')
    return
  }
  const id = shopId.value
  const query = { shopId: id, passFlag: false, reasonForRejection: refuseTest }
  doShopAudit(query).then((res) => {
    if (res.code !== 200) {
      ElMessage.error('更新')
      return
    }
    ElMessage.success('已更新')
    refuseVisible.value = false
    $emit('refresh', 'UNDER_REVIEW')
    parentTabChangeHandle('UNDER_REVIEW')
    $emit('getStoreStatus')
  })
}

/**
 * 重新审核
 */
const reAudit = async (id: string) => {
  dialogFormVisible.value = true
  shopId.value = id
}
/**
 * 店铺审核拒绝
 */
const confirmReAudit = () => {
  const id = shopId.value
  const query = { shopId: id }
  doReAudit(query).then((res) => {
    if (res.code !== 200) {
      ElMessage.error('更新')
      return
    }
    ElMessage.success('已更新')
    dialogFormVisible.value = false
    $emit('refresh', 'REJECT')
    parentTabChangeHandle('REJECT')
    $emit('getStoreStatus')
  })
}
/**
 * 单一状态切换
 */
const changeStatus = (shopsId: string, status: string) => {
  const toBoolean = status === 'NORMAL'
  doChangeStatus([shopsId], toBoolean)
}
/**
 * 供应商列表单个删除
 */
const deleteShop = async (
  row: {
    shopBalance: {
      total: string
      uncompleted: string
      undrawn: string
    }
    id: string
  },
  type?: string,
) => {
  ElMessageBox.confirm('确定要删除该供应商吗，此操作不可逆?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      const uncompleted = divTenThousand(row.shopBalance.uncompleted).toNumber()
      const undrawn = divTenThousand(row.shopBalance.undrawn).toNumber()
      if (uncompleted || undrawn) {
        ElMessage.warning({
          message: '该供应商存在未（提现/结算）金额，禁止删除',
          duration: 4000,
        })
        return
      }
      const { code, msg } = await doDelShop([row.id])
      if (code === 200) {
        ElMessage({
          type: 'success',
          message: '删除成功',
        })
        type ? parentTabChangeHandle(type) : parentTabChangeHandle(' ')
        return
      }
      ElMessage({
        type: 'error',
        message: msg,
      })
    })
    .catch(() => {})
}
/**
 * （供应商列表||已关闭）批量删除
 */
const batchDeleteShop = () => {
  if (!multipleSelection.value.length) {
    return ElMessage.error('请勾选列表')
  }
  ElMessageBox.confirm('确定要删除该供应商吗，此操作不可逆?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      const tempArr = multipleSelection.value.map((item) => item.id)
      const { code, success } = await doDelShop(tempArr)
      if (code === 200 && success) {
        parentTabChoose.value !== 'REJECT' ? parentTabChangeHandle(' ') : parentTabChangeHandle('REJECT')
        ElMessage({
          type: 'success',
          message: '删除成功',
        })
      }
    })
    .catch(() => {})
}
/**
 * 批量启用禁用方法
 * @param {boolean} status NORMAL启用 FORBIDDEN 禁用
 */

const batchChangeStatus = async (status: string) => {
  const toBoolean = status === 'NORMAL'
  if (!checkVaild(status)) {
    ElMessage.error('请勾选列表')
    return false
  }
  const tempArr = multipleSelection.value.map((item) => {
    return item.id
  })
  if (status === 'refusedTo') {
    // 批量拒绝
    return shopAudit(tempArr.join(','), false)
  }
  const { code, success } = await doChangeStatus(tempArr, toBoolean)
  if (code === 200 && success === true) {
    ElMessage.success('操作成功')
  }
  return true
}
/**
 * 跳转至对账单
 */
const handleNavToWithdraw = () => {}

/**
 * 判断选中row是否符合批量操作
 * @param {boolean} status NORMAL启用 FORBIDDEN 禁用
 */
function checkVaild(status: string): boolean {
  let flag = true
  if (!multipleSelection.value.length) {
    flag = false
  }
  return flag
}

/**
 * 待审核列表的通过
 */
const navToEdit = (rows: any, type: 'EDIT' | 'through') => {
  new storage().setItem('SHOPITEM', rows, 60 * 60 * 2)
  $router.push({
    path: '/supplier/editSupplier',
    query: {
      shopId: rows.id,
      type,
    },
  })
}

const previewShop = (rows: any) => {
  new storage().setItem('SHOPITEM', rows, 60 * 60 * 2)
  $router.push({
    path: '/supplier/previewSupplier',
    query: { shopId: rows.id, tabRadio: $props.tabRadio },
  })
}

const reasonRefusal = ref('')
const handleRefusalReason = async (row: any) => {
  if (row.rejectReason && reasonRefusal) {
    return
  }
  const { data } = await doGetShopRejectReason(row.id)
  reasonRefusal.value = data || '暂无拒绝原因'
  row.rejectReason = true
}

defineExpose({
  changeStatus,
  deleteShop,
  batchChangeStatus,
  batchDeleteShop,
})
</script>

<template>
  <q-table v-model:checked-item="multipleSelection" :data="tableList" :selection="isSelect" class="q-table up-table">
    <template #header="{ row }">
      <div class="header">
        <div v-if="tabRadio !== 'supplierAudit'" class="header__content">
          <span>供应商ID：{{ row.no }}</span>
          <span v-if="row.status === 'NORMAL'"
            >供应商评分：<text style="color: red">{{ row.score }}</text></span
          >
          <span v-if="currentTabChoose === ''">入驻时间：{{ new Date(row.auditTime).toISOString().split('T')[0] }}</span>
          <span v-if="['UNDER_REVIEW', 'REJECT'].includes(currentTabChoose)">申请时间：{{ row.createTime }}</span>
          <span v-if="currentTabChoose === 'REJECT'">审批时间：{{ row.auditTime }}</span>
          <span v-if="currentTabChoose === ''">管理员账号：{{ row.userMobile }}</span>
        </div>
        <div v-else class="header__content">
          <span class="vertical_bar">供应商ID：{{ row.no }}</span>
          <span class="vertical_bar">申请人：{{ row.userName }}({{ row.userMobile }})</span>
          <span class="vertical_bar">申请时间：{{ row.createTime && row.createTime.split(' ')[0] }}</span>
          <span v-if="currentTabChoose !== 'UNDER_REVIEW' && row.extra" class="vertical_bar">
            审核员：{{ row.extra.operatorName }}({{ row.extra.operatorPhone }})
          </span>
          <span v-if="currentTabChoose !== 'UNDER_REVIEW'" class="vertical_bar">审批时间：{{ row.auditTime && row.auditTime!.split(' ')[0] }}</span>
        </div>
      </div>
    </template>
    <q-table-column label="供应商信息" :width="currentTabChoose !== '' ? 380 : 350">
      <template #default="{ row }">
        <div class="shop">
          <el-avatar :icon="Shop" :size="60" :src="row.logo" shape="square" style="vertical-align: middle; margin: 0 10px 0 0; flex-shrink: 0" />
          <div class="shop__content">
            <div :class="[oneLineDisplay(row.name) ? 'shop__content--twoLineDisplay' : 'shop__content--name']">
              <q-tooltip v-if="row.name.length > 20" :content="row.name" maxWidth="210px" width="auto">
                <span>{{ row.name }}</span>
              </q-tooltip>
              <span v-else>{{ row.name }}</span>
            </div>
            <div class="shop__content--phone">{{ row.contractNumber }}</div>
          </div>
        </div>
      </template>
    </q-table-column>
    <q-table-column v-if="tabRadio === 'supplierAudit'" label="供应商地址" width="280" align="left">
      <template #default="{ row }">
        <el-tooltip :content="row.address" placement="top">
          <div>
            <p class="address">{{ row.address.split('~')[0] }}</p>
            <p>{{ row.address.split('~').slice(1).join('~') }}</p>
          </div>
        </el-tooltip>
      </template>
    </q-table-column>
    <q-table-column v-if="tabRadio === 'supplierAudit'" label="状态" width="150" align="left">
      <template #default>
        <span v-if="currentTabChoose === 'UNDER_REVIEW'" style="color: #ff860b">待审核</span>
        <span v-else-if="currentTabChoose === ''">已通过</span>
        <span v-else style="color: #666">已拒绝</span>
      </template>
    </q-table-column>
    <q-table-column v-if="currentTabChoose === '' && tabRadio !== 'supplierAudit'" align="left" label="供应商余额(元)" width="180">
      <template #default="{ row }">
        <el-row class="row_balance" @click="handleNavToWithdraw">
          <div class="col_row_balance" style="color: red">
            <span style="font-size: 16px">{{ divTenThousand(row.shopBalance.undrawn).toFixed(2).split('.')[0] }}</span>
            <span v-if="+divTenThousand(row.shopBalance.undrawn).toFixed(2) > 0"
              >.{{ divTenThousand(row.shopBalance.undrawn).toFixed(2).split('.')[1] }}</span
            >
          </div>
        </el-row>
      </template>
    </q-table-column>
    <q-table-column v-if="currentTabChoose === '' && tabRadio !== 'supplierAudit'" label="供应商类型" width="160" align="left">
      <template #default="{ row }">
        <div style="padding-left: 4px">{{ SUPPLIER_TYPE[row.shopType as keyof typeof SUPPLIER_TYPE] }}</div>
      </template>
    </q-table-column>
    <q-table-column v-if="currentTabChoose === '' && tabRadio !== 'supplierAudit'" label="提佣类型" width="130" align="left">
      <template #default="{ row }">
        <div style="padding-left: 4px">
          {{ row.extractionType === 'CATEGORY_EXTRACTION' ? '类目抽佣' : row.extractionType === 'ORDER_SALES_EXTRACTION' ? '订单金额提佣' : '' }}
        </div>
      </template>
    </q-table-column>
    <q-table-column v-if="currentTabChoose === '' && tabRadio !== 'supplierAudit'" label="状态" prop="status" align="left" width="100">
      <template #default="{ row }">
        <div style="padding-left: 4px">
          <el-switch
            v-if="row.status === 'FORBIDDEN' || row.status === 'NORMAL'"
            v-model="row.status"
            active-value="NORMAL"
            inactive-value="FORBIDDEN"
            inline-prompt
            active-text="启用"
            inactive-text="禁用"
            @change="changeStatus(row.id, row.status)"
          ></el-switch>
          <div v-show="row.status === 'REJECT'">
            <span>已拒绝</span>
          </div>
        </div>
      </template>
    </q-table-column>
    <q-table-column label="操作" :width="currentTabChoose === '' ? 130 : 215" align="right" fixed="right">
      <template #default="{ row }">
        <el-link :underline="false" class="mr-20" type="primary" @click="previewShop(row)"> 查看 </el-link>
        <div v-show="tabRadio !== 'supplierAudit' && (row.status === 'FORBIDDEN' || row.status === 'NORMAL')" class="btn">
          <el-link :underline="false" type="primary" @click="navToEdit(row, 'EDIT')">编辑 </el-link>
        </div>
        <div v-show="row.status === 'UNDER_REVIEW'">
          <el-link :underline="false" class="mr-20" type="primary" @click="shopAudit(row.id, true)">通过 </el-link>
          <el-link :underline="false" type="danger" @click="refuseHandle(row.id)">拒绝 </el-link>
        </div>
        <div v-show="row.status === 'REJECT'">
          <el-link :underline="false" class="mr-20" type="primary" @click="reAudit(row.id)">重新审核 </el-link>
          <el-tooltip :visible="row.rejectReason">
            <template #content>
              <span>{{ reasonRefusal }}</span>
            </template>
            <el-link type="primary" size="small" :underline="false" @mouseenter="handleRefusalReason(row)" @mouseleave="row.rejectReason = false">
              拒绝原因
            </el-link>
          </el-tooltip>
        </div>
      </template>
    </q-table-column>
  </q-table>
  <el-dialog v-model="refuseVisible" title="供应商审核" width="690">
    <div style="display: flex; align-items: center; padding: 10px 0">
      <div style="color: #666666; width: 85px">拒绝原因</div>
      <el-input v-model="refuse" show-word-limit type="text" maxlength="20" placeholder="请输入拒绝原因" />
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="refuseVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmRejection">确认</el-button>
      </div>
    </template>
  </el-dialog>
  <el-dialog v-model="dialogFormVisible" title="重新审核" width="500" center>
    <div>请确认是否将【已拒绝】状态，变更为待审核！！！</div>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmReAudit"> 确定 </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<style lang="scss" scoped>
.row_balance {
  display: flex;
  width: 100%;
  flex-direction: column;
  align-items: left;
  padding-left: 4px;
  color: #ff860b;
  .col_row_balance + .col_row_balance {
    margin-top: 5px;
  }
}

@include b(header) {
  font-size: 14px;
  color: #838383;
  height: 46px;
  border-radius: 10px 10px 0 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  overflow: hidden;
  @include e(content) {
    margin-left: 18px;
    color: #333;
    span::after {
      content: '|';
      position: relative;
      margin: 0 12px;
      top: -1px;
    }
    span:last-child::after {
      content: '';
    }
  }
}

@include b(mr-20) {
  margin-right: 20px;
}

@include b(shop) {
  height: 68px;
  width: 100%;
  padding-left: 10px;
  display: flex;
  align-items: center;
  @include e(title) {
    width: 130px;
    margin-left: 10px;
    @include utils-ellipsis;
  }
  @include e(content) {
    height: 60px;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    @include m(name) {
      align-items: center;
      font-weight: bold;
      font-size: 14px;
      color: #333;
      width: 150px;
      display: -webkit-box;
      -webkit-box-orient: vertical;
      -webkit-line-clamp: 2;
      overflow: hidden;
    }
    @include m(twoLineDisplay) {
      font-weight: bold;
      font-size: 14px;
      color: #333;
    }
  }

  @include b(tag) {
    @include e(autarky) {
      flex-shrink: 0;
      margin-right: 5px;
      padding: 0 5px;
      background-color: #fd0505;
      color: #fff;
      border-radius: 4px;
      font-size: 0.7em;
    }
    @include e(optimize) {
      flex-shrink: 0;
      margin-right: 5px;
      padding: 0 5px;
      background-color: #7728f5;
      color: #fff;
      border-radius: 4px;
      font-size: 0.8em;
    }
  }
}
@include b(address) {
  font-size: 15px;
  font-weight: bold;
}
@include b(btn) {
  color: #2e99f3;
  cursor: pointer;
}

@include b(q-table) {
  overflow: auto;
  transition: height 0.5s;
}

@include b(up-table) {
  overflow: auto;
}
</style>
