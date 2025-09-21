<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import DateUtil from '@/utils/date'
import { doGetOrder, doGetMyCount } from '@/apis/order'
import { ApiOrder, ShopOrderItem, ShopOrder } from './types'
import ToTopGoCar from '@/views/toTopGoCar/index.vue'
import { OrderCount } from '@/apis/order/types'

const activeName = ref('')
const startTime = ref('')
const statusList = [
  {
    value: '',
    label: '全部状态',
  },
  {
    value: 'UNPAID',
    label: '等待买家付款',
  },
  {
    value: 'UN_DELIVERY',
    label: '待发货',
  },
  {
    value: 'UN_RECEIVE',
    label: '已发货',
  },
  {
    value: 'UN_COMMENT',
    label: '待评价',
  },
  {
    value: 'COMPLETED',
    label: '已完成',
  },
  {
    value: 'CLOSED',
    label: '订单关闭',
  },
]
const timeList = [
  {
    value: '',
    label: '全部订单',
  },
  {
    value: new DateUtil().getLastMonth(new Date()),
    label: '近一个月',
  },
  {
    value: new DateUtil().getLastThreeMonth(new Date()),
    label: '近三个月',
  },
]
const keywords = ref('')
const orderList = ref<ApiOrder[]>([])
const myOrderCount = ref<OrderCount>({
  uncommented: 0,
  undelivered: 0,
  unhandledAfs: 0,
  unpaid: 0,
  unreceived: 0,
})
const pageConfig = shallowReactive({
  size: 10,
  current: 1,
  total: 0,
})

initList()
initOrderCount()

async function initList() {
  // isEmpty.value = false
  const { size, current } = pageConfig
  const { code, data, msg } = await doGetOrder({ size, current, status: activeName.value, keywords: keywords.value, startTime: startTime.value })
  if (code !== 200) return ElMessage.error(msg || '获取订单列表失败')
  const orders: ApiOrder[] = []
  data.records.forEach((order: ApiOrder) => {
    const newShopOrders: ShopOrder[] = []
    const closedShoOrders: ShopOrder[] = []
    const shopOrders = order.shopOrders
    for (let i = 0; i < shopOrders.length; i++) {
      const shopOrder = shopOrders[i]
      if (shopOrder.status === 'SELLER_CLOSED') {
        closedShoOrders.push(shopOrder)
        continue
      }
      const packMap = initOrder(shopOrder)
      const packItems = packMap.get('')
      if (packItems?.length) {
        shopOrder.shopOrderItems = packItems
        newShopOrders.push(shopOrder)
      }
      packMap.delete('')
      for (let [key, value] of packMap.entries()) {
        const cloneShopOrder = { ...shopOrder }
        cloneShopOrder.shopOrderItems = value
        newShopOrders.push(cloneShopOrder)
      }
    }
    if (newShopOrders.length > 0) {
      order.shopOrders = newShopOrders
      orders.push(order)
    }
    if (closedShoOrders.length > 0) {
      const newOrder = { ...order }
      newOrder.status = 'SELLER_CLOSED'
      newOrder.shopOrders = closedShoOrders
      orders.push(newOrder)
    }
  })
  orderList.value = orders
  pageConfig.total = data.total
}
async function initOrderCount() {
  const { code, data } = await doGetMyCount()
  if (code === 200 && data) {
    myOrderCount.value = data
  }
}

/**
 * @description: 订单按发货 | 未发货区分
 * @param {*} order
 * @returns {*}
 */
function initOrder(shopOrder: ShopOrder) {
  const map = new Map<string, ShopOrderItem[]>()
  const items = shopOrder.shopOrderItems
  map.set('', [])
  for (let i = 0; i < items.length; i++) {
    const item = items[i]
    if (item.packageStatus === 'WAITING_FOR_DELIVER') {
      map.get('')?.push(item)
      continue
    }
    const packageId = item.packageId
    const packageItems = map.get(packageId as string)
    if (packageItems) {
      packageItems.push(item)
      continue
    }
    map.set(packageId as string, [item])
  }
  return map
}
/**
 * @description: 调用initList
 */
const callInitList = () => {
  pageConfig.current = 1
  initList()
}
/**
 * @description: 分页切换
 */
const handleCurrentChange = (val: number) => {
  pageConfig.current = val
  initList()
}
</script>

<template>
  <div bg-white c-bc-ccc style="margin-bottom: -20px">
    <div c-w-1190 ma flex>
      <center-nav />
      <div c-w-1024 c-ml-49>
        <el-tabs v-model="activeName" @tab-change="callInitList">
          <el-tab-pane name="" c-w-116>
            <template #label>
              <span class="custom-tabs-label" c-fs-18 e-c-3 fw-700> <span>所有订单</span></span>
            </template>
          </el-tab-pane>
          <el-tab-pane name="UNPAID">
            <template #label>
              <span class="custom-tabs-label" c-fs-18 e-c-3 fw-700>
                <span>待付款</span>
                <span c-c-e31436 c-ml-11>{{ myOrderCount.unpaid }}</span>
              </span>
            </template>
          </el-tab-pane>
          <el-tab-pane name="UN_DELIVERY">
            <template #label>
              <span class="custom-tabs-label" c-fs-18 e-c-3 fw-700>
                <span>待发货</span>
                <span c-c-e31436 c-ml-11>{{ myOrderCount.undelivered }}</span>
              </span>
            </template>
          </el-tab-pane>
          <el-tab-pane name="UN_RECEIVE">
            <template #label>
              <span class="custom-tabs-label" c-fs-18 e-c-3 fw-700>
                <span>待收货</span>
                <span c-c-e31436 c-ml-11>{{ myOrderCount.unreceived }}</span>
              </span>
            </template>
          </el-tab-pane>
          <el-tab-pane name="UN_COMMENT">
            <template #label>
              <span class="custom-tabs-label" c-fs-18 e-c-3 fw-700>
                <span>待评价</span>
                <span c-c-e31436 c-ml-11>{{ myOrderCount.uncommented }}</span>
              </span>
            </template>
          </el-tab-pane>
        </el-tabs>
        <!-- <div flex justify-between items-center>
          <div flex items-center c-w-303 bg-white b-1 c-bc-DCDCDC c-fs-12>
            <el-input v-model="keywords" placeholder="请输入商品名称或订单号搜索 " />
            <div cursor-pointer c-w-48 c-h-32 c-lh-32 c-bg-eee b-l c-bc-DCDCDC @click="callInitList">搜索</div>
          </div>
          <div c-fs-12 e-c-3 flex items-center>
            <div>下单时间:</div>
            <el-select
              v-model="startTime"
              placeholder="全部订单"
              size="small"
              style="width: 120px; border: 1px solid rgba(220, 220, 220, 1); border-radius: 3px"
              @change="callInitList"
            >
              <el-option v-for="item in timeList" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
            <div c-ml-27>全部状态:</div>
            <el-select
              v-model="activeName"
              placeholder="全部状态"
              size="small"
              style="width: 120px; border: 1px solid rgba(220, 220, 220, 1); border-radius: 3px"
              @change="callInitList"
            >
              <el-option v-for="item in statusList" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </div>
        </div> -->
        <orderTable :order-list="orderList" />
        <div c-mt-28 c-mb-43 flex justify-center>
          <el-pagination
            background
            layout="prev, pager, next, total"
            :total="+pageConfig.total"
            :current-page="pageConfig.current"
            @current-change="handleCurrentChange"
          />
        </div>
        <ToTopGoCar />
      </div>
    </div>
  </div>
</template>

<style scoped></style>
