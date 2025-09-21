<script setup lang="ts">
import { ElMessage, ElMessageBox } from 'element-plus'
import QTable from '@/components/qszr-core/packages/q-table/QTable'
import QTableColumn from '@/components/qszr-core/packages/q-table/q-table-column.vue'
import PageManage from '@/components/PageManage.vue'
import { doGetDistributorList, doPutDistributorApply, doGetDisMember, doGetDisRank, doGetRejectReason } from '../apis'
import type { ApiDistributorItem } from '../index'
import { ref, reactive } from 'vue'
import useConvert from '@/composables/useConvert'
import SchemaForm from '@/components/SchemaForm.vue'
import QIcon from '@/components/q-icon/q-icon.vue'

const searchConfig = reactive({
  name: '',
  mobile: '',
  date: '',
  startTime: '',
  endTime: '',
  status: 'SUCCESS',
})
const { divTenThousand } = useConvert()
const pageConfig = reactive({
  current: 1,
  size: 10,
  total: 0,
})
const distributorList = ref<ApiDistributorItem[]>([])
const selectedList = ref<ApiDistributorItem[]>([])
// 分销成员弹窗
const memberDialog = ref(false)
// 分销成员弹窗table
const memberInfo = reactive({
  current: 1,
  total: 0,
  size: 10,
  list: [],
  userId: '',
})
// 排行榜弹窗
const rankingDialog = ref(false)
// 排行榜弹窗table
const rankingInfo = reactive({
  current: 1,
  total: 0,
  size: 10,
  list: [],
  userId: '',
  rank: '',
})
// 表单配置项
const columns = [
  {
    label: '姓名',
    labelWidth: 45,
    prop: 'name',
    valueType: 'copy',
    fieldProps: {
      placeholder: '请输入姓名',
      maxlength: 20,
    },
  },
  {
    label: '手机号',
    prop: 'mobile',
    labelWidth: 60,
    valueType: 'copy',
    fieldProps: {
      placeholder: '请输入手机号',
      maxlength: 30,
    },
  },
  {
    label: '申请时间',
    prop: 'date',
    valueType: 'date-picker',
    fieldProps: {
      type: 'datetimerange',
      startPlaceholder: '开始时间',
      endPlaceholder: '结束时间',
      valueFormat: 'YYYY-MM-DD HH:mm:ss',
    },
  },
]

initDistributors()

const handleChangeTab = () => {
  initDistributors()
}
const handleChangeCurrent = (e: number) => {
  pageConfig.current = e
  initDistributors()
}
const handleChangeSize = (e: number) => {
  pageConfig.current = 1
  pageConfig.size = e
  initDistributors()
}

const handleSearch = () => {
  pageConfig.current = 1
  if (searchConfig.date.length > 0) {
    console.log('searchConfig.date', searchConfig.date)
    searchConfig.startTime = searchConfig.date[0]
    searchConfig.endTime = searchConfig.date[1]
  }
  initDistributors()
}
/**
 * 同意申请
 */
const handleConsentApply = (userId: string) => {
  consentOrRefuseApply(true, [userId])
}
/**
 * 批量同意
 */
const handleBatchConsentApply = () => {
  if (!selectedList.value.length) {
    ElMessage.warning('请勾选分销商')
  } else {
    consentOrRefuseApply(
      true,
      selectedList.value.map((item) => item.id),
    )
  }
}
/**
 * 拒绝
 */
const refuseVisible = ref(false)
const queryId = ref()
const refuse = ref('')
const handleRefuseApply = (ids: string[]) => {
  refuseVisible.value = true
  queryId.value = ids
}

/**
 * 拒绝原因
 */
const rejectReason = ref('')
const reasonRefusal = async (row: any) => {
  if (!row.rejectReason) {
    const { code, data, msg } = await doGetRejectReason(row.id)
    if (code === 200 && data) {
      rejectReason.value = data
      row.rejectReason = true
    } else {
      msg && ElMessage.warning(msg)
      rejectReason.value = ''
      row.rejectReason = true
    }
  }
}
const handleMouseLeave = (row: any) => {
  row.rejectReason = false
}

const confirmRejection = () => {
  const refuseTest = refuse.value
  if (refuseTest === '') {
    ElMessage.warning('请填写拒绝原因')
    return
  }
  doPutDistributorApply(false, queryId.value, refuseTest).then((res) => {
    if (res.code && res.code === 200) {
      ElMessage.success('操作成功')
      refuseVisible.value = false
      initDistributors()
    } else {
      ElMessage.error('操作失败')
    }
  })
}
/**
 * 批量拒绝
 */
const handleBatchRefuseApply = () => {
  if (!selectedList.value.length) {
    ElMessage.warning('请勾选分销商')
  } else {
    handleRefuseApply(selectedList.value.map((item) => item.id))
  }
}
/**
 * 同意/拒绝操作
 */
async function consentOrRefuseApply(status: boolean, userId: string[]) {
  ElMessageBox.confirm('确定需要继续操作?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    const { code } = await doPutDistributorApply(status, userId)
    if (code && code === 200) {
      ElMessage.success('操作成功')
      initDistributors()
    } else {
      ElMessage.error('操作失败')
    }
  })
}

const handleShowDisMember = (userId: string) => {
  if (searchConfig.status === 'SUCCESS') {
    memberDialog.value = true
    memberInfo.userId = userId
    initDisMemberList(userId)
  }
}
const handleShowDisranking = () => {
  rankingDialog.value = true
  initDisRankList()
}
const handleCloseMember = () => {
  memberInfo.current = 1
  memberInfo.size = 10
  memberInfo.list = []
  memberInfo.userId = ''
}
const handleCloseranking = () => {
  rankingInfo.current = 1
  rankingInfo.size = 10
  rankingInfo.list = []
  rankingInfo.userId = ''
  rankingInfo.rank = ''
}
const handleChangeMemberCur = (e: number) => {
  memberInfo.current = e
  initDisMemberList()
}
const handleChangeMemberSize = (e: number) => {
  memberInfo.current = 1
  memberInfo.size = e
  initDisMemberList()
}
const handleChangeRankCur = (e: number) => {
  rankingInfo.current = e
  initDisRankList()
}
const handleChangeRankSize = (e: number) => {
  rankingInfo.current = 1
  rankingInfo.size = e
  initDisRankList()
}

async function initDistributors() {
  const { code, data } = await doGetDistributorList({ ...searchConfig, ...pageConfig })
  if (code === 200) {
    distributorList.value = data.records.map((v: any) => {
      return {
        ...v,
        rejectReason: false,
        flag: false,
      }
    })
    pageConfig.total = data.total
  } else {
    ElMessage.error('获取分销商失败')
  }
}
async function initDisMemberList(userId?: string) {
  const { code, data } = await doGetDisMember({
    userId: userId || memberInfo.userId,
    current: memberInfo.current,
    size: memberInfo.size,
  })
  if (code && code === 200) {
    memberInfo.list = data.records
    memberInfo.total = data.total
    memberDialog.value = true
  } else {
    ElMessage.error('获取下线失败')
    memberDialog.value = false
  }
}
async function initDisRankList(userId?: string) {
  const { code, data } = await doGetDisRank({
    userId: userId || rankingInfo.userId,
    current: rankingInfo.current,
    size: rankingInfo.size,
  })
  if (code && code === 200) {
    rankingInfo.list = data.records
    rankingInfo.total = data.total
    rankingInfo.rank = data.rank
    rankingDialog.value = true
  } else {
    ElMessage.error('获取排行失败')
    rankingDialog.value = false
  }
}
/**
 * 重置搜索条件
 */
const handleReset = () => {
  pageConfig.current = 1
  searchConfig.date = ''
  searchConfig.endTime = ''
  searchConfig.startTime = ''
  searchConfig.mobile = ''
  searchConfig.name = ''
  initDistributors()
}
</script>

<template>
  <SchemaForm v-model="searchConfig" :columns="columns" @searchHandle="handleSearch" @handleReset="handleReset"> </SchemaForm>
  <div class="tab_container" style="position: relative">
    <el-tabs v-model="searchConfig.status" class="demo-tabs" @tab-change="handleChangeTab">
      <el-tab-pane label="分销列表" name="SUCCESS" />
      <el-tab-pane label="待审核" name="APPLYING" />
      <el-tab-pane label="已拒绝" name="CLOSED" />
    </el-tabs>
    <el-link style="position: absolute; top: 11px; right: 20px; color: #555cfd" @click="handleShowDisranking">佣金排行榜</el-link>
  </div>
  <div v-if="searchConfig.status === 'APPLYING'" class="handle_container">
    <el-button plain round @click="handleBatchConsentApply">批量同意</el-button>
    <el-button plain round @click="handleBatchRefuseApply">批量拒绝</el-button>
  </div>
  <q-table v-model:checked-item="selectedList" :selection="searchConfig.status === 'APPLYING'" :data="distributorList" no-border class="q-table">
    <q-table-column label="分销商信息" width="300" align="left">
      <template #default="{ row }">
        <div style="display: flex; height: 83px; align-items: center">
          <el-image :src="row.avatar" style="width: 52px; height: 52px" />
          <div class="f12 color51" style="margin-left: 6px">
            <div class="ellipsis">{{ row.name }}</div>
            <div>{{ row.mobile }}</div>
          </div>
        </div>
      </template>
    </q-table-column>
    <q-table-column v-if="searchConfig.status === 'SUCCESS'" label="团队成员" width="100" align="left">
      <template #default="{ row }">
        <div class="column" @click="handleShowDisMember(row.userId)">
          <el-link type="primary" link>一级：{{ row.one }}</el-link>
          <el-link type="primary" link>二级：{{ row.two }}</el-link>
          <el-link type="primary" link>三级：{{ row.three }}</el-link>
        </div>
      </template>
    </q-table-column>
    <q-table-column v-if="searchConfig.status === 'SUCCESS'" label="累计佣金(元)" width="150" align="left">
      <template #default="{ row }">
        <span>{{ divTenThousand(row.total) }}</span>
      </template>
    </q-table-column>
    <q-table-column v-if="searchConfig.status === 'SUCCESS'" label="待提现佣金(元)" width="150" align="left">
      <template #default="{ row }">
        <span>{{ divTenThousand(row.undrawn) }}</span>
      </template>
    </q-table-column>
    <q-table-column v-if="searchConfig.status === 'SUCCESS'" label="待结算佣金(元)" width="150" align="left">
      <template #default="{ row }">
        <span>{{ divTenThousand(row.unsettled) }}</span>
      </template>
    </q-table-column>
    <q-table-column label="累积消费金额(元)" width="150" align="left">
      <template #default="{ row }">
        <span>{{ divTenThousand(row.consumption) }}</span>
      </template>
    </q-table-column>
    <q-table-column label="邀请人" width="150" align="left">
      <template #default="{ row }">
        <span>{{ row.referrer || '平台' }}</span>
      </template>
    </q-table-column>
    <q-table-column label="申请时间" width="200" align="left">
      <template #default="{ row }">
        <span>{{ row.createTime }}</span>
      </template>
    </q-table-column>
    <q-table-column v-if="searchConfig.status !== 'APPLYING'" label="审批时间" width="200" align="left">
      <template #default="{ row }">
        <span>{{ row.passTime }}</span>
      </template>
    </q-table-column>
    <q-table-column label="" width="1" />
    <q-table-column v-if="searchConfig.status === 'APPLYING'" label="操作" width="150" align="right">
      <template #default="{ row }">
        <span style="cursor: pointer; color: #555cfd; margin-left: 10px" @click="handleConsentApply(row.id)">通过</span>
        <span style="cursor: pointer; color: #f54319; margin-left: 10px" @click="handleRefuseApply([row.id])">拒绝</span>
      </template>
    </q-table-column>
    <q-table-column v-if="searchConfig.status === 'CLOSED'" label="操作" width="150" align="right">
      <template #default="{ row }">
        <el-tooltip :visible="rejectReason !== '' && row.rejectReason">
          <template #content>
            <span>{{ rejectReason }}</span>
          </template>
          <el-button type="primary" link :underline="false" @mouseover="reasonRefusal(row)" @mouseleave="handleMouseLeave(row)"> 拒绝原因 </el-button>
        </el-tooltip>
      </template>
    </q-table-column>
  </q-table>
  <PageManage
    :page-num="pageConfig.current"
    :page-size="pageConfig.size"
    :total="pageConfig.total"
    @handle-current-change="handleChangeCurrent"
    @handle-size-change="handleChangeSize"
  />
  <!-- 分销成员弹窗 -->
  <el-dialog v-model="memberDialog" width="554px" title="团队成员" @close="handleCloseMember">
    <el-table
      :data="memberInfo.list"
      :header-cell-style="{ background: '#f6f8fa', fontSize: '12px', color: '#909399', 'font-weight': 'bold' }"
      :cell-style="{
        fontSize: '12px',
        color: '#333333',
      }"
      height="400"
    >
      <el-table-column label="姓名" align="center">
        <template #default="{ row }">
          {{ row.name ? row.name : row.nickname }}
        </template>
      </el-table-column>
      <el-table-column label="层级" align="center">
        <template #default="{ row }">
          {{ row.level === 'ONE' ? '一级' : row.level === 'TWO' ? '二级' : '三级' }}
        </template>
      </el-table-column>
      <el-table-column label="累计消费" align="center" prop="consumption">
        <template #default="{ row }">
          {{ divTenThousand(row.consumption) }}
        </template>
      </el-table-column>
      <el-table-column label="订单数" align="center" prop="orderCount" />
    </el-table>
    <page-manage
      :page-num="memberInfo.current"
      :page-size="memberInfo.size"
      :total="memberInfo.total"
      @handle-current-change="handleChangeMemberCur"
      @handle-size-change="handleChangeMemberSize"
    />
  </el-dialog>
  <!-- 佣金排行榜弹窗 -->
  <el-dialog v-model="rankingDialog" width="717px" class="ranking" style="margin-top: 70px" title="佣金排行榜" @close="handleCloseranking">
    <el-table
      :data="rankingInfo.list"
      :header-cell-style="{ background: '#f6f8fa', fontSize: '14px', color: '#333', 'font-weight': 'bold' }"
      :cell-style="{
        fontSize: '12px',
        color: '#333333',
      }"
    >
      <el-table-column label="排名" align="center" type="index" width="140">
        <template #default="{ $index }">
          <QIcon v-if="$index === 0" name="icon-svg2" svg style="width: 24px; height: 26px"></QIcon>
          <QIcon v-else-if="$index === 1" name="icon-a-svg1" svg style="width: 24px; height: 26px"></QIcon>
          <QIcon v-else-if="$index === 2" name="icon-a-svg2" svg style="width: 24px; height: 26px"></QIcon>
          <span v-else>{{ $index + 1 }}</span>
        </template>
      </el-table-column>
      <el-table-column label="用户昵称" prop="name">
        <template #default="{ row }">
          <div style="display: flex; align-items: center">
            <img :src="row.avatar" alt="" style="width: 50px; height: 50px; margin-right: 5px" />
            <span>{{ row.name }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="累计佣金(元)" align="center" prop="total">
        <template #default="{ row }">
          {{ divTenThousand(row.total).toFixed(2) }}
        </template>
      </el-table-column>
    </el-table>
  </el-dialog>
  <!-- 拒绝原因 -->
  <el-dialog v-model="refuseVisible" title="分销商审核" width="690">
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
</template>

<style lang="scss" scoped>
* {
  font-size: 14px;
}

@include b(q-table) {
  width: 100%;
  overflow: auto;
}
.color51 {
  color: #515151;
  height: 52px;
  display: flex;
  flex-direction: column;
  justify-content: space-around;
}
.color58 {
  color: #586884;
}
.color33 {
  color: #333333;
}
.column {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  justify-content: flex-start;
  & button {
    margin: 0;
  }
}
.ellipsis {
  width: 135px;
  @include utils-ellipsis;
  font-size: 14px;
}
.ml {
  margin-left: 30px;
}
</style>
