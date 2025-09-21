<script setup lang="ts">
import { doGetOrderDetail, doGetGroupOrderUser } from '../apis'
import PageManage from '@/components/PageManage.vue'
import { ElMessage } from 'element-plus'
import type { ApiGroupActivity, ApiGroupOrderStatus, ApiGroupUser } from '../'
import useConvert from '@/composables/useConvert'
import { useRoute } from 'vue-router'
import { ref, reactive } from 'vue'

const { divTenThousand } = useConvert()
const $route = useRoute()
const groupInfo = ref<ApiGroupActivity>()
const userList = ref<ApiGroupUser[]>([])
const pageConfig = reactive({
  current: 1,
  size: 10,
  total: 0,
})

initGroupOrderInfo()
initGroupOrderUser()

const convertOrderStatus = (status: ApiGroupOrderStatus) => {
  const statusObj = {
    SUCCESS: '拼团成功',
    FAIL: '拼团失败',
    ING: '拼团中',
  }
  return statusObj[status]
}
async function initGroupOrderInfo() {
  const teamNo = $route.query.teamNo as string
  const { code, data } = await doGetOrderDetail(teamNo)
  if (code !== 200) return ElMessage.error('获取拼团信息失败')
  groupInfo.value = data
}
async function initGroupOrderUser() {
  const teamNo = $route.query.teamNo as string
  const { total, ...other } = pageConfig
  const { code, data } = await doGetGroupOrderUser(teamNo, other)
  if (code !== 200) return ElMessage.error('获取拼团用户信息失败')
  userList.value = data.records
  pageConfig.total = data.total
}
const handleSizeChange = (e: number) => {
  pageConfig.size = e
  initGroupOrderUser()
}
const handleCurrentChange = (e: number) => {
  pageConfig.current = e
  initGroupOrderUser()
}
</script>

<template>
  <div class="groupDetail">
    <div class="groupDetail__info">
      <div class="groupDetail__info-row">
        <div class="groupDetail__info-row--label">开团人:</div>
        <div class="groupDetail__info-row--right">
          <el-image :src="groupInfo?.commanderAvatar" style="width: 36px; height: 36px" />
          <span class="mg">{{ groupInfo?.commanderNickname }}</span>
        </div>
      </div>
      <div class="groupDetail__info-row">
        <div class="groupDetail__info-row--label">活动名称:</div>
        <div class="groupDetail__info-row--right">{{ groupInfo?.name }}</div>
      </div>
      <div class="groupDetail__info-row">
        <div class="groupDetail__info-row--label">开团时间:</div>
        <div class="groupDetail__info-row--right">{{ groupInfo?.openTime }}</div>
      </div>
      <div class="groupDetail__info-row">
        <div class="groupDetail__info-row--label">参团人数:</div>
        <div class="groupDetail__info-row--right">{{ groupInfo?.currentNum }}/{{ groupInfo?.totalNum }}</div>
      </div>
      <div class="groupDetail__info-row">
        <div class="groupDetail__info-row--label">拼团商品:</div>
        <div class="groupDetail__info-row--right">
          <el-image :src="groupInfo?.productImage" style="width: 36px; height: 36px" />
          <span class="mg">{{ groupInfo?.productName }}</span>
        </div>
      </div>
      <div class="groupDetail__info-row">
        <div class="groupDetail__info-row--label">购买件数:</div>
        <div class="groupDetail__info-row--right">{{ groupInfo?.buyNum }}</div>
      </div>
      <div class="groupDetail__info-row">
        <div class="groupDetail__info-row--label">拼团状态:</div>
        <div class="groupDetail__info-row--right">{{ groupInfo?.status && convertOrderStatus(groupInfo?.status) }}</div>
      </div>
    </div>
    <el-table :data="userList" width="100%">
      <el-table-column label="拼团人信息" width="200">
        <template #default="{ row }">
          <el-row justify="center" align="middle">
            <el-col :span="6">
              <el-image :src="row.avatar" style="width: 36px; height: 36px" />
            </el-col>
            <el-col :span="18">
              <div class="ellipsis">{{ row.nickname }}</div>
            </el-col>
          </el-row>
        </template>
      </el-table-column>
      <el-table-column label="拼团价" align="center">
        <template #default="{ row }">{{ divTenThousand(row.price) }}</template>
      </el-table-column>
      <el-table-column label="实付金额" align="center">
        <template #default="{ row }">{{ divTenThousand(row.amount) }}</template>
      </el-table-column>
      <el-table-column label="团员身份" align="center">
        <template #default="{ row }">
          <div>{{ row.commander ? '团长' : '团员' }}</div>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="180" align="center"></el-table-column>
      <el-table-column label="订单号" align="center" width="220" fixed="right">
        <template #default="{ row }">
          <div>{{ row.orderNo }}</div>
        </template>
      </el-table-column>
    </el-table>
    <el-row justify="end" align="middle">
      <page-manage
        :page-size="pageConfig.size"
        :page-num="pageConfig.current"
        :total="pageConfig.total"
        @handle-size-change="handleSizeChange"
        @handle-current-change="handleCurrentChange"
      />
    </el-row>
  </div>
</template>

<style lang="scss" scoped>
@include b(groupDetail) {
  @include e(info) {
    border: 1px solid #d5d5d5;
    font-size: 14px;
    color: #333333;
    padding-top: 25px;
  }
  @include e(info-row) {
    @include flex(flex-start);
    @include m(label) {
      width: 104px;
      text-align: right;
    }
    @include m(right) {
      @include flex;
      margin-left: 11px;
    }
    margin-bottom: 30px;
  }
}
@include b(mg) {
  margin-left: 10px;
}
@include b(ellipsis) {
  @include utils-ellipsis;
}
</style>
