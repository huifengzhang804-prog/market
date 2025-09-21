<script setup lang="ts">
import { ElMessage, TabPaneName } from 'element-plus'
import OrderTable from './DistributionOrderTableP.vue'
import PageManage from '@/components/PageManage.vue'
import { doExportDisOrder, doGetDisOrder } from '../apis'
import DateUtil from '@/utils/date'
import { ref, reactive, nextTick, watch } from 'vue'
import useConvert from '@/composables/useConvert'
import { ArrowDownBold } from '@element-plus/icons-vue'
import SchemaForm from '@/components/SchemaForm.vue'
import ElTableEmpty from '@/components/element-plus/el-table/ElTableEmpty/index.vue'
import { DistributionList, Statistic } from '..'
import QIcon from '@/components/q-icon/q-icon.vue'
import DistributionDialog from './DistributionDialog.vue'

const dateTool = new DateUtil()
const { divTenThousand } = useConvert()
const searchForm = reactive({
  orderNo: '',
  productName: '',
  shopName: '',
  buyerNickname: '',
  date: '',
  startTime: '',
  endTime: '',
  status: '',
  purchase: '',
  distributorPhone: '',
})
const pageConfig = reactive({
  current: 1,
  size: 10,
  total: 0,
})
const distributorList = ref<DistributionList[]>([])
const distributorCount = ref<Statistic>({
  total: '',
  unsettled: '',
  invalid: '',
})

// 分销订单说明
const dialogVisible = ref(false)

// 全选状态
const isAllSelected = ref(false)
// 部分选中状态
const isIndeterminate = ref(false)

// 表单配置项
const columns = [
  {
    label: '订单号',
    labelWidth: 60,
    prop: 'orderNo',
    valueType: 'copy',
    fieldProps: {
      placeholder: '请输入订单号',
      maxlength: 20,
    },
  },
  {
    label: '店铺名称',
    prop: 'shopName',
    valueType: 'copy',
    fieldProps: {
      placeholder: '请输入店铺名称',
      maxlength: 30,
    },
  },
  {
    label: '手机号',
    prop: 'distributorPhone',
    valueType: 'input', // 改为input类型
    fieldProps: {
      placeholder: '各级分销商手机号',
      maxlength: 11,
      type: 'text', // 使用text类型配合v-model.number
      'v-model.number': '', // 只允许输入数字
      oninput: "value=value.replace(/[^\\d]/g,'')", // 只允许输入数字
      pattern: '^1[3-9]\\d{9}$', // 手机号码格式验证
    },
  },
  {
    label: '买家昵称',
    prop: 'buyerNickname',
    valueType: 'copy',
    fieldProps: {
      placeholder: '请输入买家昵称',
      maxlength: 30,
    },
  },
  {
    label: '商品名称',
    prop: 'productName',
    valueType: 'copy',
    fieldProps: {
      placeholder: '请输入商品名称',
      maxlength: 30,
    },
  },
  {
    label: '内购',
    prop: 'purchase',
    valueType: 'select',
    options: [
      {
        label: '全部',
        value: '',
      },
      { label: '内购', value: 'true' },
      { label: '非内购', value: 'false' },
    ],
    fieldProps: {
      placeholder: '请选择',
    },
  },
  {
    label: '下单时间',
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
initDisOrderList()
async function initDisOrderList() {
  const { code, data, msg } = await doGetDisOrder({
    current: pageConfig.current,
    size: pageConfig.size,
    ...searchForm,
  })
  if (code === 200 && data) {
    distributorList.value = data.page.records
    distributorCount.value = data.statistic
    pageConfig.total = data.page.total
  } else {
    ElMessage.error(msg || '获取分销订单失败')
  }
}

const handleSearch = () => {
  pageConfig.current = 1
  if (searchForm?.date.length > 0) {
    searchForm.startTime = searchForm.date[0]
    searchForm.endTime = searchForm.date[1]
  }
  initDisOrderList()
}
/**
 * 重置搜索条件
 */
const handleReset = () => {
  searchForm.orderNo = ''
  searchForm.productName = ''
  searchForm.shopName = ''
  searchForm.buyerNickname = ''
  searchForm.date = ''
  searchForm.startTime = ''
  searchForm.endTime = ''
  pageConfig.current = 1

  initDisOrderList()
}

/**
 * 导出
 */
const handleExport = async () => {
  let params: any = {}
  if (getSelectedItems().length) {
    params.orderNos = getSelectedItems().map((item: any) => item.orderNo) || []
  } else {
    const { orderNo, productName, shopName, buyerNickname, startTime, endTime, status } = searchForm
    params = { orderNo, productName, shopName, buyerNickname, startTime, endTime, status }
  }
  const { code, msg } = await doExportDisOrder(params)
  if (code === 200) {
    ElMessage.success({ message: msg || '导出成功' })
  } else {
    ElMessage.error({ message: msg || '导出失败' })
  }
}

// 处理全选/取消全选
const handleSelectAll = (val: boolean) => {
  distributorList.value.forEach((item) => {
    item.checked = val
  })
  isIndeterminate.value = false
}

// 处理单个选项变化
const handleItemChange = () => {
  const checkedCount = distributorList.value.filter((item) => item.checked).length
  isAllSelected.value = checkedCount === distributorList.value.length
  isIndeterminate.value = checkedCount > 0 && checkedCount < distributorList.value.length
}

// 获取选中的项目（可以供父组件使用）
const getSelectedItems = () => {
  return distributorList.value.filter((item) => item.checked)
}

// 如果需要向父组件通知选中状态变化
const emit = defineEmits(['selection-change'])

// 监听选中状态变化并通知父组件
watch(
  () => distributorList.value.map((item) => item.checked),
  () => {
    emit('selection-change', getSelectedItems())
  },
  { deep: true },
)

// 分页部分

const handleChangeCurrent = (e: number) => {
  pageConfig.current = e
  initDisOrderList()
}
const handleChangeSize = (e: number) => {
  pageConfig.current = 1
  pageConfig.size = e
  initDisOrderList()
}

// TAB筛选部分
const activeTabName = ref(' ')
const quickSearchTabName = ref('全部订单')
const handleTabChange = (name: TabPaneName) => {
  searchForm.status = name as string
  initDisOrderList()
}
const handleQuickSearchCommand = (command: string) => {
  activeTabName.value = ' '
  quickSearchTabName.value = command
  if (quickSearchTabName.value === '近一个月订单') {
    const startTime = dateTool.getLastMonth(new Date())
    loadHandleTabClick(startTime)
  } else if (quickSearchTabName.value === '近三个月订单') {
    const startTime = dateTool.getLastThreeMonth(new Date())
    loadHandleTabClick(startTime)
  } else {
    // 请求全部订单清空时间
    searchForm.startTime = ''
    searchForm.endTime = ''
    initDisOrderList()
  }
}
const loadHandleTabClick = async (startTime: string) => {
  const endTime = dateTool.getYMDs(new Date())
  searchForm.startTime = startTime
  searchForm.endTime = endTime
  initDisOrderList()
}

const toolsDisabled = ref(false)
const onScroll = async () => {
  toolsDisabled.value = true
  await nextTick()
  toolsDisabled.value = false
}
</script>

<template>
  <el-config-provider :empty-values="[undefined, null]">
    <schema-form v-model="searchForm" :columns="columns" @searchHandle="handleSearch" @handleReset="handleReset">
      <template #otherOperations>
        <el-button type="primary" @click="handleExport">导出</el-button>
      </template>
    </schema-form>
  </el-config-provider>
  <div style="padding: 0 16px">
    <div class="count">
      <span
        >累计总佣金：
        <text
          >￥ <text style="font-size: 16px">{{ divTenThousand(distributorCount.total).toFixed(2) }}</text></text
        >
      </span>
      <span
        >待结算总佣金：
        <text style="color: red"
          >￥ <text style="font-size: 16px">{{ divTenThousand(distributorCount.unsettled).toFixed(2) }} </text></text
        ></span
      >
      <span
        >已失效总佣金：<text style="color: #ccc"
          >￥ <text style="font-size: 16px">{{ divTenThousand(distributorCount.invalid).toFixed(2) }}</text></text
        ></span
      >
    </div>
  </div>

  <div style="position: relative">
    <el-tabs v-model="activeTabName" style="padding: 0 16px" @tab-change="handleTabChange">
      <el-tab-pane name=" ">
        <template #label>
          <span>{{ quickSearchTabName }}</span>
          <el-dropdown placement="bottom-end" trigger="click" @command="handleQuickSearchCommand">
            <span class="el-dropdown-link" style="height: 40px" @click.stop="() => {}">
              <el-icon class="el-icon--right" style="position: relative; top: 12px">
                <el-icon><ArrowDownBold /></el-icon>
              </el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item v-for="NameItem in ['近一个月订单', '近三个月订单', '全部订单']" :key="NameItem" :command="NameItem">{{
                  NameItem
                }}</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
      </el-tab-pane>
      <el-tab-pane label="已付款" name="PAID" />
      <el-tab-pane label="已完成" name="COMPLETED" />
      <el-tab-pane label="已关闭" name="CLOSED" />
    </el-tabs>
    <QIcon
      name="icon-wenhao"
      size="18px"
      color="#666"
      style="position: absolute; right: 16px; top: 12px; cursor: pointer"
      @click="dialogVisible = true"
    />
  </div>
  <div class="distribution_order_table_container f1" @scroll="onScroll">
    <div class="tbhead">
      <el-checkbox v-model="isAllSelected" style="margin-left: 16px" :indeterminate="isIndeterminate" @change="handleSelectAll" />
      <div class="tbhead__goods">商品信息</div>
      <div class="tbhead__detail">分佣详情</div>
      <div class="tbhead__state">结算状态</div>
      <div class="tbhead__total">佣金结算</div>
    </div>
    <div v-if="distributorList.length === 0" class="">
      <ElTableEmpty />
    </div>
    <OrderTable
      v-for="item in distributorList"
      :key="item.orderNo"
      v-model:checked="item.checked"
      :tools-disabled="toolsDisabled"
      :order-info="item"
      @change="handleItemChange"
    ></OrderTable>
  </div>
  <page-manage
    :page-num="pageConfig.current"
    :page-size="pageConfig.size"
    :total="pageConfig.total"
    @handle-current-change="handleChangeCurrent"
    @handle-size-change="handleChangeSize"
  />
  <el-dialog v-model="dialogVisible" title="分销订单说明" width="790px" center>
    <DistributionDialog />
  </el-dialog>
</template>

<style lang="scss" scoped>
.distribution_order_table_container {
  overflow: auto;
  position: relative;
  border-left: 16px solid transparent;
  border-right: 16px solid transparent;
  .tbhead {
    background-color: #f2f2f2;
    position: sticky;
    top: 0;
    z-index: 100;
  }
}
@include b(count) {
  height: 48px;
  line-height: 48px;
  span {
    margin-right: 30px;
    text {
      font-weight: bold;
      font-size: 12px;
    }
  }
}
@include b(tbhead) {
  width: 1256px;
  display: flex;
  align-items: center;
  height: 48px;
  font-weight: bold;
  background-color: rgba(242, 242, 242, 0.5);
  @include e(goods) {
    margin-left: 16%;
  }
  @include e(state) {
    margin-left: 23%;
  }
  @include e(detail) {
    margin-left: 29%;
  }
  @include e(total) {
    margin-left: 8%;
  }
}
.ml {
  margin-left: 30px;
}
</style>
