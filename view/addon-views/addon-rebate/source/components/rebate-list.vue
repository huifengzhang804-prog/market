<script lang="ts" setup>
import SplitTable from './order-split-table/SplitTable'
import SplitTableColumn from './order-split-table/split-table-column.vue'
import { ArrowDown } from '@element-plus/icons-vue'
import { ref, defineProps, defineEmits } from 'vue'
import PageManage from '@/components/PageManage.vue'
import DateUtil from '@/utils/date'
import RemarkPopup from '@/components/remark/remark-popup.vue'
import type { ApiOrder } from '@/views/order/types/order'
import { doGetRebateOrder } from '../apis'
import { type RebateOrderList, PaidStatus } from './types/index'
import Decimal from 'decimal.js'
import { type PropType, computed, watch, reactive, nextTick } from 'vue'
import useConvert from '@/composables/useConvert'
import RebateData from './rebate-data.vue'
import ElTableEmpty from '@/components/element-plus/el-table/ElTableEmpty/index.vue'
import DescribeDialog from './describe-dialog.vue'
import QIcon from '@/components/q-icon/q-icon.vue'

const TabsName = ['近三个月订单', '近一个月订单', '全部订单']
const $props = defineProps({
  searchForm: {
    type: Object,
    default: () => ({}),
  },
  statistic: {
    type: Object as PropType<{ totalExpired: string; totalPendingSettlement: string; totalRebate: string }>,
    default: () => ({}),
  },
})
const $emit = defineEmits(['update:statistic'])

const statisticData = computed({
  get() {
    return $props.statistic
  },
  set(val) {
    $emit('update:statistic', val)
  },
})

const { divTenThousand } = useConvert()
const FirstTabsName = ref('全部订单')
const TabNavArr: any[] = []
const dropdownRef = ref()
const isDropdown = ref(false)
const searchStasusData = ref<{ title: string; name: string }[]>([])
// 消费返利订单说明
const showDescriptionDialog = ref(false)
// tab切换部分 当前高亮
const activeTabName = ref(' ')
// 当前备注的订单号ids
const currentRemarkIds = ref<string[]>([])
const remarkDialog = ref(false)
// tab表格
const orderInfoArr = ref<ApiOrder[]>([])
const multiSelect = ref<ApiOrder[]>([])
// 备注文本
const textarea = ref('')
const pageConfig = reactive({
  size: 20,
  current: 1,
  total: 0,
})
const dateTool = new DateUtil()
// 条件查询
const queryConditionsTabs = reactive({
  params: {
    buyerNickname: '',
    receiverName: '',
    orderNo: '',
    productName: '',
    startTime: '', // 开始时间
    endTime: '', // 结束时间}
  },
})

initOrderList()
initTabs()
initSearchStasusData()

/**
 * 获取订单列表 UN_RECEIVE
 */
function initOrderList() {
  nextTick(() => {
    const { params } = queryConditionsTabs
    const query: any = {
      ...pageConfig,
      ...$props.searchForm,
      status: activeTabName.value.trim(),
    }
    if (FirstTabsName.value === '近一个月订单') {
      query.monthOrders = 'ONE_MONTH_AGO'
    } else if (FirstTabsName.value === '近三个月订单') {
      query.monthOrders = 'THREE_MONTHS_AGO'
    }
    doGetRebateOrder(query).then(({ data }) => {
      const staticData = data?.statistic
      Object.keys(statisticData.value).forEach((key) => {
        const statisticDataKey = key as keyof typeof statisticData.value
        statisticData.value[statisticDataKey] = staticData?.[statisticDataKey] || ''
      })
      pageConfig.current = data.current
      pageConfig.size = data.size
      pageConfig.total = data.total
      orderInfoArr.value = data.records
    })
  })
}

/**
 * 初始化tab
 */
function initTabs() {
  const queryStatusMap = {
    UNPAID: '待付款',
    PAID: '已付款',
    COMPLETED: '已完成',
    CLOSED: '已关闭',
  }
  for (const [key, value] of Object.entries(queryStatusMap)) {
    TabNavArr.push({
      key,
      value,
    })
  }
}

function initSearchStasusData() {
  const newTabNavArr = TabNavArr.map((item) => ({ title: item.value, name: item.key }))
  newTabNavArr.unshift({ title: '全部订单', name: ' ' })
  searchStasusData.value = newTabNavArr
}

/**
 * 近一个月/三个月/全部（下拉选择）
 */
const handleDropdownCommand = ($event: string) => {
  FirstTabsName.value = $event
  handleStartTimeAndstartEnd()
}

/**
 * 处理开始时间开始结束
 */
function handleStartTimeAndstartEnd() {
  if (FirstTabsName.value === '近一个月订单') {
    const startTime = dateTool.getLastMonth()
    loadHandleTabClick(startTime)
  } else if (FirstTabsName.value === '近三个月订单') {
    const startTime = dateTool.getLastThreeMonth()
    loadHandleTabClick(startTime)
  } else {
    queryConditionsTabs.params.startTime = ''
    queryConditionsTabs.params.endTime = ''
    initOrderList()
  }
}

/**
 * Tabs 条件查询
 */
const loadHandleTabClick = async (startTime: string) => {
  const endTime = dateTool.getYMDs()
  queryConditionsTabs.params.startTime = startTime
  queryConditionsTabs.params.endTime = endTime
  initOrderList()
}
/**
 * tab栏点击
 */
const handleTabClick = async () => {
  if (activeTabName.value !== ' ') {
    queryConditionsTabs.params.endTime = ''
    queryConditionsTabs.params.startTime = ''
    initOrderList()
    return
  } else {
    handleDropdownCommand(FirstTabsName.value)
  }
}
/**
 * 全部/季度/月份下拉
 */
const handleOpen = () => {
  isDropdown.value = !isDropdown.value
  isDropdown.value ? dropdownRef.value.handleOpen() : dropdownRef.value.handleClose()
}
/**
 * 默认展示第一个商品的单价
 */
const unitPrice = computed(() => (shopOrderItems: RebateOrderList['rebateOrderItems']) => divTenThousand(shopOrderItems[0].salePrice))
/**
 * 商品总数量展示
 */
const num = computed(() => (shopOrderItems: RebateOrderList['rebateOrderItems']) => shopOrderItems.reduce((pre, item) => item.num + pre, 0))
const handleSizeChange = (value: number) => {
  pageConfig.size = value
  initOrderList()
}
const handleCurrentChange = (value: number) => {
  pageConfig.current = value
  initOrderList()
}
const handleSuccess = () => {
  multiSelect.value = []
  initOrderList()
}

const hasItems = (shopItems: RebateOrderList['rebateOrderItems'], targetStatus: 'PENDING_SETTLEMENT' | 'EXPIRED' | 'SETTLED') => {
  const item = shopItems.find((shopItem) => shopItem.settlementStatus === targetStatus)
  if (item) return true
  return false
}
defineExpose({
  initOrderList,
  multiSelect,
})
</script>

<template>
  <div class="fdc1 overh">
    <rebate-data style="margin: 0 16px" :rebate-detail-statistic="statistic"></rebate-data>
    <div class="tab_container" style="position: relative">
      <el-tabs v-model="activeTabName" @tab-change="handleTabClick">
        <el-tab-pane name=" ">
          <template #label>
            <span>{{ FirstTabsName }}</span>
            <el-icon class="el-icon--right-top">
              <arrow-down />
            </el-icon>
            <el-dropdown ref="dropdownRef" placement="bottom-end" trigger="click" @command="handleDropdownCommand">
              <span class="el-dropdown-link" style="height: 40px" @click.stop="handleOpen">
                <el-icon class="el-icon--right">
                  <arrow-down />
                </el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item v-for="NameItem in TabsName" :key="NameItem" :command="NameItem">{{ NameItem }} </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-tab-pane>
        <el-tab-pane v-for="item in TabNavArr" :key="item.key" :label="item.value" :name="item.key"></el-tab-pane>
      </el-tabs>
      <QIcon
        name="icon-wenhao"
        size="18px"
        color="#333"
        style="position: absolute; right: 20px; top: 14px; cursor: pointer"
        @click="showDescriptionDialog = true"
      />
    </div>

    <SplitTable v-model:checkedItem="multiSelect" selection header-selection class="orderIndex-table tableUp" :data="orderInfoArr" no-border>
      <template #noData>
        <ElTableEmpty />
      </template>
      <template #header="{ row }: { row: RebateOrderList }">
        <div class="header-table">
          <span style="margin-left: 14px">订单号：{{ row.orderNo }}</span>
          <span>下单时间：{{ row.orderTime }}</span>
          <span
            >买家：{{ row.buyerNickname }}（实付款：{{
              row.rebateOrderItems.reduce((pre: any, item: any) => pre.plus(divTenThousand(+item.dealPrice * item.num)), new Decimal(0)).toFixed(2)
            }}）</span
          >
          <span>店铺：{{ row.shopName }}</span>
          <span
            :style="{
              color: PaidStatus[row.status] === '已关闭' ? '#999' : PaidStatus[row.status] === '待付款' ? '#F54319' : '',
            }"
            >{{ PaidStatus[row.status] }}</span
          >
        </div>
      </template>
      <SplitTableColumn prop="name" label="商品" width="400px">
        <template #default="{ shopOrderItems }">
          <div class="orderIndex-table__commodity">
            <div class="orderIndex-table__img-box">
              <el-image
                v-for="item in shopOrderItems.slice(0, 1)"
                :key="item.skuId"
                fits="cover"
                style="width: 68px; height: 68px"
                shape="square"
                size="large"
                :src="item.image"
                :title="item.productName"
              />
              <div class="order-info" :style="{ justifyContent: shopOrderItems?.[0]?.specs?.join('、').length < 1 ? 'center' : 'space-between' }">
                <el-tooltip
                  v-if="shopOrderItems?.[0]?.productName.length >= 35"
                  effect="dark"
                  :content="shopOrderItems?.[0]?.productName"
                  placement="top-start"
                >
                  <p class="order-info__name">
                    <span v-if="shopOrderItems?.[0]?.productType === 'VIRTUAL_PRODUCT'" class="virtual_product">虚拟</span>
                    {{ shopOrderItems?.[0]?.productName }}
                  </p>
                </el-tooltip>
                <p v-else class="order-info__name">
                  <span v-if="shopOrderItems?.[0]?.productType === 'VIRTUAL_PRODUCT'" class="virtual_product">虚拟</span>
                  {{ shopOrderItems?.[0]?.productName }}
                </p>
                <el-tooltip v-if="shopOrderItems?.[0]?.specs?.join('、').length >= 20" effect="dark" placement="bottom-start">
                  <template #content>
                    <p style="max-width: 440px">{{ shopOrderItems?.[0]?.specs?.join('、') }}</p>
                  </template>
                  <p class="order-info__spec">{{ shopOrderItems?.[0]?.specs?.join('、') }}</p>
                </el-tooltip>
                <p v-else class="order-info__spec">{{ shopOrderItems?.[0]?.specs?.join('、') }}</p>
              </div>
            </div>
            <div class="orderIndex-table__img-mask">
              <span style="font-size: 12px">￥{{ unitPrice(shopOrderItems)?.toFixed(2) }}</span>
              <span style="color: #838383; font-size: 10px">x{{ num(shopOrderItems) }}</span>
            </div>
          </div>
        </template>
      </SplitTableColumn>
      <!-- <SplitTableColumn prop="age" label="实付款(元)" width="130" align="left">
                <template #default="{ row }">
                    {{
                        row.rebateOrderItems
                            .reduce((pre: any, item: any) => pre.plus(divTenThousand(+item.dealPrice * item.num)), new Decimal(0))
                            .toFixed(2)
                    }}
                </template>
            </SplitTableColumn> -->
      <SplitTableColumn prop="age" label="平台服务费(元)" width="110" align="left">
        <template #default="{ shopOrderItems }">
          {{ divTenThousand(shopOrderItems?.[0]?.platformServiceFee) }}
        </template>
      </SplitTableColumn>
      <SplitTableColumn prop="deliveryStatus" label="结算状态" width="100" align="left">
        <template #default="{ shopOrderItems }">
          <div class="delivery">
            <span v-if="shopOrderItems?.[0]?.settlementStatus === 'PENDING_SETTLEMENT'" style="color: #f54319">待结算</span>
            <span v-else-if="shopOrderItems?.[0]?.settlementStatus === 'EXPIRED'" style="color: #999999">已失效</span>
            <span v-else-if="shopOrderItems?.[0]?.settlementStatus === 'SETTLED'" style="color: #00bb2c">已返</span>
          </div>
        </template>
      </SplitTableColumn>
      <SplitTableColumn prop="orderStatus" label="预计返利(元)" width="140" align="left">
        <template #default="{ shopOrderItems }">
          <span>{{ shopOrderItems?.[0]?.rebateCalculation.split('=')[1] }}</span>
          <el-tooltip class="box-item" effect="dark" :content="shopOrderItems?.[0]?.rebateCalculation" placement="bottom">
            <QIcon name="icon-wenhao1" color="#999" style="margin-left: 5px; cursor: pointer" size="16px" />
          </el-tooltip>
        </template>
      </SplitTableColumn>
      <SplitTableColumn prop="sex" label="佣金结算(元)" width="120" :is-mixed="true" align="left" fixed="right">
        <template #default="{ row, shopOrderItems }">
          <div>
            <div v-if="hasItems(shopOrderItems, 'PENDING_SETTLEMENT')">待结算：{{ divTenThousand(row?.pendingSettlement) }}</div>
            <div v-if="hasItems(shopOrderItems, 'SETTLED')">已返：{{ divTenThousand(row?.settled) }}</div>
            <div v-if="hasItems(shopOrderItems, 'EXPIRED')">已失效：{{ divTenThousand(row?.expired) }}</div>
          </div>
        </template>
      </SplitTableColumn>
    </SplitTable>
    <page-manage
      :page-size="pageConfig.size"
      :page-num="pageConfig.current"
      :total="pageConfig.total"
      @reload="initOrderList"
      @handle-size-change="handleSizeChange"
      @handle-current-change="handleCurrentChange"
    />
    <RemarkPopup
      v-model:isShow="remarkDialog"
      v-model:ids="currentRemarkIds"
      v-model:remark="textarea"
      remark-type="GOODS"
      @success="handleSuccess"
    />

    <el-dialog v-model="showDescriptionDialog" title="消费返利订单说明" width="650px" center>
      <describe-dialog />
    </el-dialog>
  </div>
</template>

<style lang="scss" scoped>
* {
  font-size: 14px;
}
@include b(goods) {
  @include e(content) {
    @include flex();
  }
}
@include b(shop-name) {
  width: 120px;
  @include utils-ellipsis($line: 1);
}

@include b(orderIndex-table) {
  position: relative;
  overflow-x: auto;
  transition: height 0.5s;
  @include e(top) {
    @include flex(space-between);
    width: 100%;
  }
  @include e(top-time) {
    @include flex;
    & > div:nth-child(2) {
      padding: 0 60px;
    }
  }
  @include e(commodity) {
    width: 100%;
    display: flex;
    height: 92px;
  }
  @include e(img-box) {
    width: 320px;
    display: flex;
    align-items: center;
    font-size: 14px;
    justify-content: space-between;
  }

  @include e(img) {
    flex-shrink: 0;
    border-radius: 5px;
    position: relative;
  }

  @include e(img-mask) {
    display: flex;
    flex-direction: column;
    justify-content: space-around;
    align-items: end;
    font-size: 12px;
    color: #666666;
    margin-left: 20px;
  }
}

.caozuo_btn:hover {
  color: #fff;
  background: #309af3 !important;
}

@include b(money_text) {
  font-size: 12px;
  color: #000000;
  @include utils-ellipsis($line: 1);
}

@include b(avatar_text) {
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  @include e(bottom) {
    margin-bottom: 5px;
  }
}

@include b(el-icon--right-top) {
  margin-left: 5px;
}

@include b(el-icon--right) {
  top: 10px;
  position: absolute;
  left: -20px;
  opacity: 0;
}

@include b(header-table) {
  width: 100%;
  @include flex('', 'center');
  span::after {
    content: '|';
    margin: 0 10px;
    color: #999;
  }
  span:last-child::after {
    content: '';
    margin: 0;
  }
}
@include b(order-info) {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-around;
  height: 68px;
  margin: 0 8px;
  word-break: break-all;
  line-height: 1.5;
  overflow: hidden;
  width: 0;
  @include e(name) {
    @include utils-ellipsis(2);
    line-height: 18px;
    color: #333;
  }
  @include e(spec) {
    text-overflow: ellipsis;
    white-space: nowrap;
    overflow: hidden;
    color: #999;
  }
}
@include b(customer) {
  text-align: left;
  width: 100%;
  line-height: 1.3;
  @include e(copy) {
    text-align: right;
    margin-bottom: 8px;
  }
}
@include b(copy) {
  color: #1890ff;
  margin-left: 8px;
  cursor: pointer;
}
</style>
<style scoped lang="scss">
.example-showcase .el-dropdown-link {
  cursor: pointer;
  color: var(--el-color-primary);
  display: flex;
  align-items: center;
}
.el-popper.is-customized {
  /* Set padding to ensure the height is 32px */
  padding: 6px 12px;
  background: linear-gradient(90deg, rgb(159, 229, 151), rgb(204, 229, 129));
}

.el-popper.is-customized .el-popper__arrow::before {
  background: linear-gradient(45deg, #b2e68d, #bce689);
  right: 0;
}
/* *---------------- */
</style>
