<script lang="ts" setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import 'uno.css'
import PageManage from '@/components/BetterPageManage/BetterPageManage.vue'
import { doGetCouponRecord, doPostExportCouponRecord } from '../apis'
import SchemaForm from '@/components/SchemaForm.vue'
import QIcon from '@/components/q-icon/q-icon.vue'
import { VoucherRecordSearchForm, VoucherRecordType } from '../types'

const searchForm = ref<VoucherRecordSearchForm>({
  status: '',
  associatedOrderNo: '',
  name: '',
  userPhone: '',
  couponUserId: '',
  giftUserPhone: '',
})
const pageConfig = ref({
  size: 10,
  current: 1,
  total: 0,
})
// 表单配置项
const columns = [
  {
    label: '手机号',
    prop: 'userPhone',
    valueType: 'input', // 改为input类型
    fieldProps: {
      placeholder: '',
      maxlength: 11,
      type: 'text', // 使用text类型配合v-model.number
      'v-model.number': '', // 只允许输入数字
      oninput: "value=value.replace(/[^\\d]/g,'')", // 只允许输入数字
      pattern: '^1[3-9]\\d{9}$', // 手机号码格式验证
    },
  },
  {
    label: '关联订单',
    prop: 'associatedOrderNo',
    valueType: 'copy',
    fieldProps: {
      placeholder: '',
    },
  },
  {
    label: '优惠券名称',
    prop: 'name',
    valueType: 'copy',
    fieldProps: {
      placeholder: '',
    },
  },
  {
    label: '状态',
    prop: 'status',
    valueType: 'select',
    options: [
      {
        label: '全部状态',
        value: '',
      },
      {
        label: '未使用',
        value: 'UNUSED',
      },
      {
        label: '已使用',
        value: 'USED',
      },
      {
        label: '已过期',
        value: 'EXPIRED',
      },
    ],
    fieldProps: {
      placeholder: '请选择',
    },
  },
  {
    label: '优惠券号',
    prop: 'couponUserId',
    valueType: 'copy',
    fieldProps: {
      placeholder: '',
    },
  },
  {
    label: '赠券用户',
    prop: 'giftUserPhone',
    valueType: 'copy',
    fieldProps: {
      placeholder: '',
    },
  },
]

const couponIndexType = {
  PRICE_DISCOUNT: '无门槛折扣券',
  PRICE_REDUCE: '无门槛现金券',
  REQUIRED_PRICE_DISCOUNT: '满折券',
  REQUIRED_PRICE_REDUCE: '满减券',
} as const
// 优惠券列表
const couponList = ref<VoucherRecordType[]>([])
// 选中的优惠券
const checkedData = ref<VoucherRecordType[]>([])
const handleReset = () => {
  searchForm.value = {
    status: '',
    associatedOrderNo: '',
    name: '',
    userPhone: '',
    giftUserPhone: '',
    couponUserId: '',
  }
  handleGetCouponRecord()
}

const handleGetCouponRecord = async () => {
  const { code, data } = await doGetCouponRecord({ ...searchForm.value, page: pageConfig.value.current, size: pageConfig.value.size })
  if (code === 200) {
    couponList.value = data.records
    pageConfig.value.total = data.total
  }
}
handleGetCouponRecord()

// 处理选择变化
function handleSelectionChange(selection: VoucherRecordType[]) {
  checkedData.value = selection
}

const handleExport = async () => {
  let params: any = {}
  const { status, userPhone, associatedOrderNo, name, giftUserPhone, couponUserId } = searchForm.value
  params = { status, userPhone, associatedOrderNo, name, giftUserPhone, couponUserId }
  params.exportCouponUserIds = checkedData.value?.map((item: any) => item.couponUserId) || []
  const { code, msg } = await doPostExportCouponRecord(params)
  if (code === 200) {
    ElMessage.success({ message: msg || '导出成功' })
  } else {
    ElMessage.error({ message: msg || '导出失败' })
  }
}

const handleCurrentChange = (current: number) => {
  pageConfig.value.current = current
  handleGetCouponRecord()
}

const handleSizeChange = (size: number) => {
  pageConfig.value.size = size
  handleGetCouponRecord()
}

// 描述弹窗
const showDescriptionDialog = ref(false)

const configData = [
  {
    status: '待使用',
    description: '该优惠券未核销',
  },
  {
    status: '已使用',
    description: '该优惠券已使用',
  },
  {
    status: '已过期',
    description: '优惠券未使用并且超过有效期',
  },
  {
    status: '平台赠送',
    description: '平台运营人员赠送的会员优惠券',
  },
  {
    status: '店铺赠送',
    description: '店铺运营人员赠送的会员优惠券',
  },
  {
    status: '手动领取',
    description: '用户自行领取的优惠券',
  },
  {
    status: '关联订单',
    description: '优惠券在哪笔订单中使用',
  },
  {
    status: '赠券用户',
    description: '该优惠券是由哪个系统用户赠送会员的',
  },
]
</script>

<template>
  <div class="q_plugin_container f1 overh">
    <div class="q_plugin_container">
      <el-config-provider :empty-values="[undefined, null]">
        <SchemaForm v-model="searchForm" :columns="columns" @searchHandle="handleGetCouponRecord" @handleReset="handleReset">
          <template #otherOperations>
            <el-button type="primary" @click="handleExport">导出</el-button>
          </template>
        </SchemaForm>
      </el-config-provider>
      <div class="handle_container df">
        <div class="export cup">
          <QIcon name="icon-wenhao" size="18px" @click="showDescriptionDialog = true" />
        </div>
      </div>
      <div class="table_container">
        <el-table
          ref="multipleTableRef"
          :cell-style="{ color: '#333333', height: '60px' }"
          :data="couponList"
          :header-cell-style="{ background: '#F7F8FA', height: '48px' }"
          :header-row-style="{ color: '#333' }"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="30" fixed="left" label="全选" />
          <el-table-column label="优惠券号" prop="couponUserId" width="180" />
          <el-table-column label="用户" prop="userNickname" width="120" />
          <el-table-column label="手机号" prop="userPhone" width="120" />
          <el-table-column label="优惠券名称" prop="name" width="120" />
          <el-table-column label="面值" prop="parValue" width="90" />
          <el-table-column label="状态" prop="queryStatus" width="120">
            <template #default="{ row }: { row: VoucherRecordType }">
              <span>{{ row.queryStatus === 'UNUSED' ? '未使用' : row.queryStatus === 'USED' ? '已使用' : '已过期' }}</span>
            </template>
          </el-table-column>
          <el-table-column label="优惠券类型" prop="type" width="120">
            <template #default="{ row }: { row: VoucherRecordType }">
              <span>{{ couponIndexType[row.type as keyof typeof couponIndexType] }}</span>
            </template>
          </el-table-column>
          <el-table-column label="优惠券规则" prop="typeDescription" width="150" />
          <el-table-column label="领取方式" prop="collectTypeText" width="100" />
          <el-table-column label="关联订单" prop="associatedOrderNo" width="200" />
          <el-table-column label="领券时间" prop="createTime" width="160" />
          <el-table-column label="到期时间" prop="endDate" width="160" />
          <el-table-column label="赠券用户" prop="giftUserPhone" width="120" />
        </el-table>
      </div>
      <!-- 好用的分页器 -->
      <pageManage
        :page-size="pageConfig.size"
        :page-num="pageConfig.current"
        :total="pageConfig.total"
        @handle-current-change="handleCurrentChange"
        @handle-size-change="handleSizeChange"
      />

      <el-dialog v-model="showDescriptionDialog" title="用券记录说明" center width="573px">
        <el-table :data="configData" border class="description_table" :header-cell-style="{ background: '#BEBEBE', color: '#000', height: '36px' }">
          <template #empty> <ElTableEmpty /> </template>
          <el-table-column prop="status" label="字段" width="120" />
          <el-table-column prop="description" label="说明" />
        </el-table>
      </el-dialog>
    </div>
  </div>
</template>
<style lang="scss" scoped>
@include b(export) {
  margin-left: auto;
}
</style>
