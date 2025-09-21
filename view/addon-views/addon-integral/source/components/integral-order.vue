<script setup lang="ts">
import QTable from '@/components/qszr-core/packages/q-table/QTable'
import QTableColumn from '@/components/qszr-core/packages/q-table/q-table-column.vue'
import PageManage from '@/components/PageManage.vue'
import OrderShipment from './deliveryList/order-shipment.vue'
import { doGetIntegralOrderList, doPetIntegralOrderWarning, doPutIntegralOrderDeliverComplete } from '../apis'
import { ElMessage, ElMessageBox } from 'element-plus'
import { IntegralOrderStatusCn } from './ts'
import type { IntegralOrderItem } from './types/order'
import { cloneDeep } from 'lodash-es'
import DateUtil from '@/utils/date'
import useClipboard from 'vue-clipboard3'
import { reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import useConvert from '@/composables/useConvert'
import { ArrowDown } from '@element-plus/icons-vue'
import remarkFlag from '@/components/remark/remark-flag.vue'
import remarkPopup from '@/components/remark/remark-popup.vue'
import SchemaForm from '@/components/SchemaForm.vue'
import Storage from '@/utils/Storage'

useClipboard()

const pageConfig = reactive({
  size: 20,
  current: 1,
  total: 0,
})
const searchParams = ref({
  no: '',
  consignee: '',
  productName: '',
  date: [],
  status: '',
  // keywords: '',
  affiliationPlatform: '',
})
const { divTenThousand } = useConvert()
const remarkDialog = ref(false)
const currentRemarkIds = ref<string[]>([])
const textarea = ref('')
const $router = useRouter()
const tableList = ref<IntegralOrderItem[]>([])
const tableSelectedArr = ref<IntegralOrderItem[]>([])
//发货弹窗
const deliverDialog = ref(false)
// 当前订单号
const currentNo = ref('')
// const tableHeight = ref('calc(100vh - 330px)')
const tableHeight = ref('calc(100vh - 310px)')
// 表单配置项
const columns = [
  {
    label: '订单号',
    labelWidth: 60,
    prop: 'no',
    valueType: 'copy',
    fieldProps: {
      placeholder: '请输入订单号',
      maxlength: 20,
    },
  },
  {
    label: '收货人姓名',
    prop: 'consignee',
    labelWidth: 90,
    valueType: 'copy',
    fieldProps: {
      placeholder: '请输入收货人姓名',
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
    label: '创建时间',
    prop: 'date',
    valueType: 'date-picker',
    fieldProps: {
      type: 'daterange',
      startPlaceholder: '开始时间',
      endPlaceholder: '结束时间',
      valueFormat: 'YYYY-MM-DD',
    },
  },
  {
    label: '所属渠道',
    prop: 'affiliationPlatform',
    valueType: 'select',
    options: [
      { label: '全部', value: '' },
      { label: '小程序', value: 'WECHAT_MINI_APP' },
      { label: 'PC商城', value: 'PC' },
      { label: 'H5商城', value: 'H5' },
      { label: 'IOS', value: 'IOS' },
      { label: '安卓', value: 'ANDROID' },
      { label: '鸿蒙', value: 'HARMONY' },
    ],
    fieldProps: {
      placeholder: '请选择',
    },
  },
]
initOrderList()
const waitDeliveryNum = ref('0')
async function initOrderList() {
  const cloneSearchParams: any = cloneDeep(searchParams.value)
  if (Array.isArray(cloneSearchParams.date)) {
    cloneSearchParams.startTime = cloneSearchParams.date?.[0]
    cloneSearchParams.endTime = cloneSearchParams.date?.[1]
  }
  delete cloneSearchParams.date
  const { code, data, msg } = await doGetIntegralOrderList({ ...pageConfig, ...cloneSearchParams })
  if (code !== 200) {
    ElMessage.error(msg || '获取订单失败')
    return
  }
  tableList.value = data.records
  pageConfig.total = data.total
  waitDeliveryNum.value = data.waitDeliveryNum
}

const handleReset = () => {
  Object.keys(searchParams.value).forEach((key) => ((searchParams.value as any)[key] = ''))
  initOrderList()
}
/**
 * 点击发货
 */
const handleDelivery = (row: IntegralOrderItem) => {
  currentNo.value = row.no
  deliverDialog.value = true
}
/**
 * 批量备注
 */
const handleBatchNote = async () => {
  if (!tableSelectedArr.value.length) {
    ElMessage.error('请选择商品')
    return
  }
  currentRemarkIds.value = tableSelectedArr.value.map((item) => item.no)
  remarkDialog.value = true
}

// 单个备注
const handleRemark = (row: IntegralOrderItem) => {
  currentRemarkIds.value = [row.no]
  if (row.sellerRemark) {
    textarea.value = row.sellerRemark
  }
  remarkDialog.value = true
}
const handleSizeChange = (val: number) => {
  pageConfig.size = val
  initOrderList()
}
const handleCurrentChange = (val: number) => {
  pageConfig.current = val
  initOrderList()
}
const handleSuccess = () => {
  tableSelectedArr.value = []
  initOrderList()
}
/**
 * 查看性情
 */
const handleCheckDetails = (no: string) => {
  $router.push({
    name: 'integralMallOrderDetail',
    query: {
      no,
    },
  })
}
const CloseOrder = async (row: IntegralOrderItem) => {
  try {
    await ElMessageBox.confirm('确认关闭订单?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '关闭',
      type: 'warning',
    })
    const { code, msg, data } = await doPutIntegralOrderDeliverComplete(row.no)
    if (code !== 200) {
      ElMessage.error(msg || '关闭订单失败')
      return
    }
    ElMessage.success('关闭订单成功')
    row.status = 'SYSTEM_CLOSED'
  } catch (error) {
    error
  }
}
// 展开、收起搜素
const ShowMCard = ref(false)
const $emit = defineEmits(['search-data', 'changeShow'])
watch(
  () => ShowMCard.value,
  (val) => {
    tableHeight.value = val ? 'calc(100vh - 450px)' : 'calc(100vh - 310px)'
  },
)
const miniDeliveryCount = ref('0')
// TAB筛选部分
const dateTool = new DateUtil()
const activeTabName = ref(' ')
const quickSearchTabName = ref('全部订单')
const handleTabChange = async (name: string | number) => {
  searchParams.value.status = name as string
  initOrderList()
  if (name === 'PAID') {
    const { code, data, msg } = await doPetIntegralOrderWarning()
    if (code !== 200) return ElMessage.error(msg || '获取待发货订单总数失败')
    else miniDeliveryCount.value = data
  }
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
    searchParams.value.date = []
    initOrderList()
  }
}
const loadHandleTabClick = async (startTime: string) => {
  const endTime = dateTool.getYMDs(new Date())
  searchParams.value.date = [startTime, endTime] as any
  initOrderList()
}

const affiliationPlatform = {
  WECHAT_MINI_APP: '小程序',
  WECHAT_MP: '公众号',
  PC: 'PC商城',
  H5: 'H5商城',
  IOS: 'IOS',
  ANDROID: '安卓',
  HARMONY: '鸿蒙',
}
/**
 * 批量发货
 */
const handleBatchDelivery = () => {
  console.log(tableSelectedArr.value)
  if (!tableSelectedArr.value.length) return ElMessage.error('请选择商品')
  const filterArr = tableSelectedArr.value.filter((item) => item.status === 'PAID')
  if (!filterArr.length) return ElMessage.error('暂无待发货订单')
  new Storage().setItem('integralOrderSendGoods', filterArr, 60 * 60 * 24)
  // 批量发货
  $router.push({
    name: 'integralMallOrderDelivery',
  })
}
</script>
<template>
  <el-config-provider :empty-values="[undefined, null]">
    <SchemaForm v-model="searchParams" :columns="columns" @searchHandle="initOrderList" @handleReset="handleReset"> </SchemaForm>
  </el-config-provider>
  <el-tabs v-model="activeTabName" class="demo-tabs tab_container" @tab-change="handleTabChange">
    <el-tab-pane name=" ">
      <template #label>
        <span>{{ quickSearchTabName }}</span>
        <el-dropdown placement="bottom-end" trigger="click" @command="handleQuickSearchCommand">
          <span class="el-dropdown-link" style="height: 40px" @click.stop="() => {}">
            <el-icon class="el-icon--right" style="position: relative; top: 12px">
              <arrow-down />
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
    <el-tab-pane label="待付款" name="UNPAID" />
    <el-tab-pane name="PAID">
      <template #label>
        <el-badge :value="waitDeliveryNum" class="item">
          <span>待发货</span>
        </el-badge>
      </template>
    </el-tab-pane>
    <el-tab-pane label="待收货" name="ON_DELIVERY" />
    <el-tab-pane label="已完成" name="ACCOMPLISH" />
    <el-tab-pane label="已关闭" name="SYSTEM_CLOSED" />
  </el-tabs>
  <div class="handle_container">
    <el-button @click="handleBatchDelivery">批量发货</el-button>
  </div>
  <q-table
    v-model:checked-item="tableSelectedArr"
    :data="tableList"
    class="base-vip-table"
    :style="{ height: tableHeight }"
    header-selection
    no-border
  >
    <template #header="{ row }: { row: IntegralOrderItem }">
      <div class="base-vip-table-top">
        <div class="base-vip-table-top__left">
          <span>订单号 : {{ row.no }} </span>
          <span>创建时间 : {{ row.createTime }}</span>
          <span>{{ affiliationPlatform[row.affiliationPlatform as keyof typeof affiliationPlatform] }}</span>
        </div>
        <remark-flag :content="row.sellerRemark" @see-remark="handleRemark(row)" />
      </div>
    </template>
    <q-table-column label="商品" width="390" align="left">
      <template #default="{ row }: { row: IntegralOrderItem }">
        <div class="goods-Infor">
          <el-image class="customer-Infor__img" fit="cover" style="width: 68px; height: 68px; margin-right: 10px" :src="row.image" />
          <div class="goods-Infor__name">
            <div class="ellipsis">
              <el-tooltip v-if="row.productName.length >= 35" effect="dark" :content="row.productName" placement="top-start">
                <span>
                  <span v-if="row.productType === 'VIRTUAL_PRODUCT'" class="virtual_product">虚拟</span>
                  {{ row.productName }}</span
                ></el-tooltip
              >
              <span v-else>
                <span v-if="row.productType === 'VIRTUAL_PRODUCT'" class="virtual_product">虚拟</span>
                {{ row.productName }}
              </span>
            </div>
            <div class="goods-Infor__name--integral">
              <!-- 积分：{{ row.price }} -->
              <div class="commodity__right--price">
                <span>{{ row.price }}</span
                ><span>积分</span
                ><span v-if="row.salePrice !== '0'"
                  >+<span>￥</span><span>{{ divTenThousand(row.salePrice) }}</span></span
                >
              </div>
            </div>
          </div>
        </div>
      </template>
    </q-table-column>
    <q-table-column label="客户信息" align="left" width="250">
      <template #default="{ row }: { row: IntegralOrderItem }">
        <div class="content">
          <div style="width: 200px">
            <span style="width: 100px; overflow: hidden; white-space: nowrap; text-overflow: ellipsis"
              >{{ row.integralOrderReceiverVO.name ? row.integralOrderReceiverVO.name : row.buyerNickname }}
            </span>
            <span v-if="row.integralOrderReceiverVO.mobile">({{ row.integralOrderReceiverVO.mobile }})</span>
          </div>
          <div style="width: 200px; overflow: hidden; white-space: nowrap; text-overflow: ellipsis">
            {{ row.integralOrderReceiverVO.area?.join('') }}
            {{ row.integralOrderReceiverVO.address }}
          </div>
        </div>
      </template>
    </q-table-column>
    <q-table-column prop="sex" label="交易信息" align="left" width="230" class="rate_size">
      <template #default="{ row }: { row: IntegralOrderItem }">
        <div style="display: inline; color: #ecb789">
          <div class="commodity__right--price content">
            <div>
              {{ row.price }}积分
              <span v-if="row.salePrice !== '0'"
                >+<span>￥</span><span>{{ divTenThousand(row.salePrice) }}</span></span
              >
            </div>
            <div>(运费{{ row.freightPrice && divTenThousand(row.freightPrice).toFixed(2) }}元)</div>
          </div>
        </div>
      </template>
    </q-table-column>
    <q-table-column label="订单状态" width="120" align="left">
      <template #default="{ row }: { row: IntegralOrderItem }">
        <el-row justify="space-between" align="middle">
          <el-row justify="space-between" align="middle" style="flex-direction: column">
            <div class="money_text" style="margin-bottom: 5px">{{ IntegralOrderStatusCn[row.status] }}</div>
          </el-row>
        </el-row>
      </template>
    </q-table-column>
    <q-table-column label="操作" align="right" fixed="right" width="160">
      <template #default="{ row }: { row: IntegralOrderItem }">
        <el-link v-if="['PAID'].includes(row.status)" :underline="false" style="margin-left: 15px" type="primary" @click="handleDelivery(row)">
          发货
        </el-link>
        <el-link :underline="false" style="margin-left: 15px" type="primary" @click="handleCheckDetails(row.no)">查看详情</el-link>

        <el-link v-if="['UNPAID'].includes(row.status)" :underline="false" style="margin-left: 15px" type="danger" @click="CloseOrder(row)">
          关闭订单
        </el-link>
      </template>
    </q-table-column>
  </q-table>
  <page-manage
    :page-size="pageConfig.size"
    :page-num="pageConfig.current"
    :total="pageConfig.total"
    @handle-size-change="handleSizeChange"
    @handle-current-change="handleCurrentChange"
  />
  <!-- 备注弹窗 -->
  <remark-popup
    v-model:isShow="remarkDialog"
    v-model:ids="currentRemarkIds"
    v-model:remark="textarea"
    remark-type="INTEGRAL"
    @success="handleSuccess"
  />
  <OrderShipment v-model:isShow="deliverDialog" :current-no="currentNo" @upload-list="initOrderList" />
</template>

<style scoped lang="scss">
* {
  font-size: 14px;
}
@include b(goods-Infor) {
  @include flex();
  align-items: flex-start;
  @include e(name) {
    height: 68px;
    @include flex();
    flex-direction: column;
    justify-content: space-around;
    align-items: flex-start;
    overflow: hidden;
    @include m(integral) {
      color: #ff7417;
    }
  }
}
@include b(base-vip-table) {
  overflow: auto;
}
@include b(base-vip-table-top) {
  @include flex();
  justify-content: space-between;
  width: 100%;
  @include e(left) {
    color: #333333;
    span::after {
      content: '|';
      padding: 0 5px;
    }
    span:last-child::after {
      content: '';
    }
  }
}
@include b(operation) {
  text-align: right;
  display: flex;
}
.ellipsis {
  width: 250px;
  @include utils-ellipsis(2);
  white-space: normal;
  text-overflow: unset;
  word-break: break-all;
}
@include b(content) {
  display: flex;
  flex-direction: column;
  height: 45px;
  justify-content: space-around;
}
@include b(fix) {
  width: 100%;
  cursor: pointer;
  @include flex();
  height: 40px;
  background: #f9f9f9;
  @include e(arrow) {
    margin-left: 4px;
  }
  @include e(down) {
    transform: rotate(180deg);
  }
}
// @include b(rate) {
//     display: flex;
//     flex-wrap: wrap;
//     align-items: center;
//     @include e(size) {
//     }
// }
@include b(warning) {
  line-height: 24px;
  padding: 14px;
  margin-bottom: 10px;
  background: rgb(233, 0, 0, 0.05);
  color: rgb(233, 0, 0);
  font-size: 14px;
  span {
    font-size: 18px;
    font-weight: bold;
    color: #f72020;
  }
}
@include b(item) {
  height: 25px;
  line-height: 25px;
}
</style>
