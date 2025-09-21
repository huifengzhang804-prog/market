<script setup lang="ts">
import { ElMessage } from 'element-plus'
import PageManage from '@/components/PageManage.vue'
import { doGetGroupOrderList } from '../apis'
import type { ApiGroupOrderParams } from '../'
import type { ApiGroupOrderItem, ApiGroupOrderStatus } from '../'
import { useRouter } from 'vue-router'
import { reactive, ref } from 'vue'
import SchemaForms from '@/components/SchemaForm.vue'
import useConvert from '@/composables/useConvert'

const $router = useRouter()
const searchParam = reactive<ApiGroupOrderParams>({
  status: 'SUCCESS',
  commander: '',
  productName: '',
})
const pageConfig = reactive({
  current: 1,
  size: 10,
  total: 0,
})
const orderList = ref<ApiGroupOrderItem[]>([])
const { divTenThousand } = useConvert()
// 表单配置项
const columns = [
  {
    label: '拼团状态',
    prop: 'status',
    valueType: 'select',
    options: [
      {
        label: '拼团成功',
        value: 'SUCCESS',
      },
      {
        label: '拼团失败',
        value: 'FAIL',
      },
      {
        label: '拼团中',
        value: 'ING',
      },
    ],
    fieldProps: {
      placeholder: '请选择',
    },
  },
  {
    label: '发起人',
    prop: 'commander',
    valueType: 'copy',
    fieldProps: {
      placeholder: '请输入发起人姓名',
    },
  },
  {
    label: '商品名称',
    prop: 'productName',
    valueType: 'copy',
    fieldProps: {
      placeholder: '请输入商品名称',
    },
  },
]

initGroupList()

const convertOrderStatus = (status: ApiGroupOrderStatus) => {
  const statusObj = {
    SUCCESS: '拼团成功',
    FAIL: '拼团失败',
    ING: '拼团中',
  }
  return statusObj[status]
}
const handleNavDetail = (teamNo: string) => {
  $router.push({
    name: 'marketingAppGroupOrderDetail',
    query: {
      teamNo,
    },
  })
}
const handleSizeChange = (value: number) => {
  pageConfig.size = value
  initGroupList()
}
const handleCurrentChange = (value: number) => {
  pageConfig.current = value
  initGroupList()
}
const handleSearch = () => {
  initGroupList()
}
type KnownType = { [key: string]: any }
const handleReset = () => {
  Object.keys(searchParam as KnownType).forEach((key) => ((searchParam as KnownType)[key] = ''))
  initGroupList()
}
async function initGroupList() {
  const params = { ...pageConfig, ...searchParam }
  const { data, code } = await doGetGroupOrderList(params)
  if (code !== 200) return ElMessage.error('获取拼团订单失败')
  orderList.value = data.records
  pageConfig.total = data.total
}
</script>

<template>
  <SchemaForms v-model="searchParam" :columns="columns" :showNumber="3" @searchHandle="handleSearch" @handleReset="handleReset" />

  <div class="table_container" style="padding-top: 26px">
    <el-table :data="orderList" :header-cell-style="{ height: '48px', background: '#F7F8FA' }" :header-row-style="{ color: '#333' }">
      <el-table-column label="发起人" width="180">
        <template #default="{ row }">
          <div class="group__userInfo">
            <el-image :src="row.commanderAvatar" style="width: 30px; height: 30px" />
            <div class="group__userInfo--text">{{ row.commanderNickname }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="商品信息" width="210">
        <template #default="{ row }">
          <div class="group__goodInfo">
            <el-image :src="row.productImage" style="width: 36px; height: 36px" />
            <div class="group__goodInfo--text">{{ row.productName }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="开团时间" prop="openTime" />
      <el-table-column label="参团人数" width="100">
        <template #default="{ row }">
          <div>{{ row.currentNum }}/{{ row.totalNum }}</div>
        </template>
      </el-table-column>
      <el-table-column label="订单总金额(元)" width="160">
        <template #default="{ row }">
          <div>{{ divTenThousand(row.amount).toFixed(2) }}</div>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <div :style="{ color: row.status === 'SUCCESS' ? '#00BB2C' : row.status === 'FAIL' ? '#999999' : '' }">
            {{ convertOrderStatus(row.status) }}
          </div>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="right" width="100" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleNavDetail(row.teamNo)">拼团详情</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
  <!-- 好用的分页器 -->
  <PageManage
    :page-size="pageConfig.size"
    :page-num="pageConfig.current"
    :total="pageConfig.total"
    @handle-size-change="handleSizeChange"
    @handle-current-change="handleCurrentChange"
  />
</template>

<style lang="scss" scoped>
@include b(group) {
  @include e(head) {
    @include flex(flex-end);
    @include m(center) {
      margin: 0 20px;
    }
  }
  @include e(userInfo) {
    display: flex;
    align-items: center;
    @include m(text) {
      width: 86px;
      @include utils-ellipsis;
      margin-left: 5px;
    }
  }
  @include e(goodInfo) {
    display: flex;
    align-items: center;
    @include m(text) {
      width: 154px;
      @include utils-ellipsis(2);
      margin-left: 5px;
    }
  }
  @include e(content) {
    margin-top: 10px;
  }
}
</style>
