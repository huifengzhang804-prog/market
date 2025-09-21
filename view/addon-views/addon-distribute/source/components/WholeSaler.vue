<script setup lang="ts">
import { ElMessage } from 'element-plus'
import QTable from '@/components/qszr-core/packages/q-table/QTable'
import QTableColumn from '@/components/qszr-core/packages/q-table/q-table-column.vue'
import PageManage from '@/components/PageManage.vue'
import { doGetDistributorList, doGetDisMember, doGetDisRank } from '../apis'
import type { ApiDistributorItem } from '../'
import useConvert from '@/composables/useConvert'
import { ref, reactive } from 'vue'
import SchemaForms from '@/components/SchemaForm.vue'
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

const handleShowDisMember = (userId: string) => {
  if (searchConfig.status === 'SUCCESS') {
    memberDialog.value = true
    memberInfo.userId = userId
    initDisMemberList(userId)
  }
}
const handleShowDisranking = () => {
  if (searchConfig.status === 'SUCCESS') {
    rankingDialog.value = true
    initDisRankList()
  }
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

async function initDistributors() {
  const { code, data } = await doGetDistributorList({ ...searchConfig, ...pageConfig })
  if (code === 200) {
    distributorList.value = data.records
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
async function initDisRankList() {
  const { code, data } = await doGetDisRank({
    userId: rankingInfo.userId,
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
  <SchemaForms v-model="searchConfig" :columns="columns" @searchHandle="handleSearch" @handleReset="handleReset" />
  <div class="tab_container">
    <div v-if="searchConfig.status === 'SUCCESS'" style="color: #0f40f5; cursor: pointer; float: right" @click="handleShowDisranking">佣金排行榜</div>
  </div>
  <q-table v-model:checked-item="selectedList" :data="distributorList" no-border class="q-table">
    <q-table-column label="分销商信息" width="300">
      <template #default="{ row }">
        <div>
          <el-row justify="space-between">
            <el-image :src="row.avatar" style="width: 52px; height: 52px" />
            <div class="f12 color51" style="margin-left: 6px">
              <div class="ellipsis">{{ row.name }}</div>
              <div>{{ row.mobile }}</div>
            </div>
          </el-row>
        </div>
      </template>
    </q-table-column>
    <q-table-column v-if="searchConfig.status === 'SUCCESS'" label="团队成员" width="140" align="left">
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
    <q-table-column v-if="searchConfig.status === 'SUCCESS'" label="已失效佣金(元)" width="150" align="left">
      <template #default="{ row }">
        <span>{{ divTenThousand(row.invalid) }}</span>
      </template>
    </q-table-column>
    <q-table-column v-if="searchConfig.status === 'SUCCESS'" label="上级" width="150">
      <template #default="{ row }">
        <div>{{ row.referrer || '平台' }}</div>
      </template>
    </q-table-column>
    <q-table-column label="申请时间" width="200" align="left">
      <template #default="{ row }">
        <span>{{ row.createTime }}</span>
      </template>
    </q-table-column>
    <q-table-column v-if="searchConfig.status === 'SUCCESS'" label="成为分销商时间" width="200" align="right">
      <template #default="{ row }">
        <span>{{ row.passTime }}</span>
      </template>
    </q-table-column>
  </q-table>
  <page-manage
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
  <el-dialog v-model="rankingDialog" width="717" title="佣金排行榜" @close="handleCloseranking">
    <el-table
      :data="rankingInfo.list"
      :header-cell-style="{ background: '#F7F8FA', fontSize: '14px', color: '#333', 'font-weight': 'bold', height: '48px' }"
      :cell-style="{
        color: '#333333',
      }"
      height="500"
    >
      <el-table-column label="排名" align="center" type="index" width="120">
        <template #default="{ $index }">
          <span v-if="$index === 0"><QIcon svg name="icon-svg2" style="width: 34px; height: 27px" /></span>
          <span v-if="$index === 1"><QIcon svg name="icon-a-svg1" style="width: 34px; height: 27px" /></span>
          <span v-if="$index === 2"><QIcon svg name="icon-a-svg2" style="width: 34px; height: 27px" /></span>
        </template>
      </el-table-column>
      <el-table-column label="用户昵称" prop="name">
        <template #default="{ row }">
          <div style="display: flex; align-items: center">
            <img :src="row.avatar" alt="" style="margin-right: 5px; width: 50px; height: 50px; border-radius: 50%" />
            <span>{{ row.name }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="累计佣金(元)" prop="total" width="160">
        <template #default="{ row }">
          <div style="text-align: left">
            <div style="width: 80px; text-align: right">
              {{ divTenThousand(row.total) }}
            </div>
          </div>
        </template>
      </el-table-column>
    </el-table>
  </el-dialog>
</template>

<style lang="scss" scoped>
* {
  font-size: 14px !important;
}
@include b(tool) {
  width: 100%;
  height: 56px;
  background: #fff;
  position: relative;
  @include e(btn) {
    position: absolute;
    left: 0;
    @include m(drop) {
      width: 120px;
    }
  }
  @include e(input) {
    width: 250px;
    font-size: 14px;
    position: absolute;
    right: 0;
    top: 50%;
    margin-top: -24px;
  }
}
@include b(q-table) {
  width: 100%;
  overflow: auto;
}
.color51 {
  color: #515151;
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
}
.ml {
  margin-left: 30px;
}
</style>
