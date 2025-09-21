<script lang="ts" setup>
import { ref, reactive } from 'vue'
import { getSupplierSearchList, doGetPurchaseExpenditure } from '../apis'
import { Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import PageManageTwo from '@/components/PageManage.vue'
import useConvert from '@/composables/useConvert'

const searchParams = reactive({
  startDate: '',
  endDate: '',
  tradeNo: '',
  supplierId: '',
})
const { divTenThousand } = useConvert()
const dateRange = ref('')
const supplierData = ref<any[]>([])

const purchaseExpenditureList = ref<any[]>([])
const fetchSupplierList = async (supplierName = '') => {
  const result = await getSupplierSearchList({ supplierName })
  if (result.data && result.data?.length) {
    supplierData.value = result.data
  }
}
const handleChangeDate = (e: string[] | null) => {
  if (e) {
    searchParams.startDate = e[0]
    searchParams.endDate = e[1]
  } else {
    searchParams.startDate = ''
    searchParams.endDate = ''
  }
  initPurchaseExpenditure()
}

const pageConfig = reactive({
  size: 10,
  current: 1,
  total: 0,
})

const initPurchaseExpenditure = async () => {
  const { code, data, msg } = await doGetPurchaseExpenditure({ ...pageConfig, ...searchParams })
  if (code === 200) {
    purchaseExpenditureList.value = data.records
    pageConfig.total = data.total
  } else {
    ElMessage.error(msg ? msg : '获取提现列表失败')
  }
}
initPurchaseExpenditure()

const handleChangeCurrent = (e: number) => {
  pageConfig.current = e
  initPurchaseExpenditure()
}
const handleChangeSize = (e: number) => {
  pageConfig.current = 1
  pageConfig.size = e
  initPurchaseExpenditure()
}
</script>

<template>
  <el-tab-pane label="采购支出" name="purchase">
    <div style="display: flex; justify-content: space-between">
      <div>
        <el-date-picker
          v-model="dateRange"
          format="YYYY/MM/DD"
          value-format="YYYY-MM-DD"
          type="daterange"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          style="margin-bottom: 14px"
          @change="handleChangeDate"
        />
      </div>
      <div style="margin-left: 80px">
        <el-select v-model="searchParams.supplierId" remote filterable clearable :remote-method="fetchSupplierList" placeholder="请选择供应商">
          <el-option v-for="supplier in supplierData" :key="supplier.id" :value="supplier.id" :label="supplier.name" />
        </el-select>
      </div>
      <div style="flex: 1"></div>
      <div>
        <el-input v-model="searchParams.tradeNo" placeholder="交易流水号" type="text">
          <template #append>
            <el-button :icon="Search" @click="initPurchaseExpenditure" />
          </template>
        </el-input>
      </div>
    </div>

    <el-table :data="purchaseExpenditureList" :header-cell-style="{ background: '#F6F8FA' }" :row-style="{ height: '68px' }">
      <el-table-column label="交易流水号" prop="tradeNo" align="center"></el-table-column>
      <el-table-column label="金额" align="center">
        <template #default="{ row }">
          <div>{{ divTenThousand(row.amount) }}</div>
        </template>
      </el-table-column>
      <el-table-column label="供应商名称" prop="supplierName" align="center" />
      <el-table-column label="采购订单号" prop="purchaseOrderNo" align="center" />
      <el-table-column label="支付时间" prop="paidTime" align="center" />
    </el-table>
    <page-manage-two
      :page-num="pageConfig.current"
      :page-size="pageConfig.size"
      :total="pageConfig.total"
      @handle-current-change="handleChangeCurrent"
      @handle-size-change="handleChangeSize"
    />
  </el-tab-pane>
</template>
