<script setup lang="ts">
import { SuccessFilled, ArrowRight, Warning } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useConversionPrice } from '@/utils/useConversionPrice'
import { orderStatusHandle, packageStatusHandle, isAllItemClosed } from '@/utils/OrderStatusHelper'
import {
  doGetFirstDeliveryPage,
  doGetOrderInfo,
  doGetLogisticsTrajectoryByWaybillNo,
  doPutCloseOrderByOrderNo,
  doGetIntegralOrderDetail,
  doGetOrderGetCodeByStoreId,
} from '@/apis/order'
import { doPutCompleteIntegralOrder, doDelIntegralOrder } from '@/apis/integral/index'
import { getAfsStatusCn } from '@/utils/useAfssStatus'
import { doconfirmGoods } from '@/apis/afs'
import Decimal from 'decimal.js'
import Countdown from '@/components/countdown.vue'
import storage from '@/libs/storage'
import NormalOrderDiscount from './components/normal-order-discount'
import { getOrderDetailStatusPlus, type ApiLogistics01 } from './types'
import type { ApiOrder, OrderInfo, ShopOrderItem, ShopOrder, Remark } from '@/views/personalcenter/order/myorder/types'
import useCalculateDiscount from './hooks/useCalculateDiscount'
import useConvert from '@/composables/useConvert'
import useClipboard from 'vue-clipboard3'
import RemarkView from '@/components/remark-view.vue'

const { divTenThousand } = useConvert()
const { toClipboard } = useClipboard()

const $route = useRoute()
const $router = useRouter()
const { discountDataByType, calucateDiscountDetails, shopDiscontMap } = useCalculateDiscount()
const info = ref<ApiOrder>()
const showAllLocation = ref(false)

const remark = ref<Remark>({
  //订单播报通知
  orderNotify: false,
  //平台备注
  platform: '',
  //店铺备注
  shop: '',
  //供应商备注
  supplier: '',
  //表单备注项
  items: [],
})
const pathOrderInfo = ref({
  orderNo: '',
  shopId: '',
  packageId: '',
})
// 第一个包裹的订单快递信息（快递公司，快递单号等）
const logistics = ref<ApiLogistics01>()
//  物流轨迹
const orderLocation = ref([{ areaCode: '', areaName: '', context: '', ftime: '', status: '', time: '' }])

init()

async function init() {
  const orderNo = $route.query.orderNo as string
  const packageId = $route.query.packageId as string
  const shopId = $route.query.shopId as string

  if (!orderNo) return $router.back()
  pathOrderInfo.value = {
    orderNo,
    shopId: shopId || '',
    packageId: packageId || '',
  }
  await initOrderData(orderNo, shopId, packageId)
  if (
    info.value &&
    info.value.extra.shopStoreId &&
    packageStatusHandle[info.value?.shopOrders?.[0].shopOrderItems?.[0].packageStatus].desp !== '待发货'
  ) {
    orderGetCodeByStoreId()
  }
}
/**
 * @description: 订单的数据
 * @param {*} orderNo
 * @param {*} shopId
 * @returns {*}
 */
async function initOrderData(orderNo: string, shopId?: string, packageId?: string) {
  const { code, data } = await doGetOrderInfo(orderNo, shopId, packageId)
  if (code !== 200) return ElMessage.error('获取订单详情失败')
  const order = data as ApiOrder
  if (order && order.distributionMode === 'INTRA_CITY_DISTRIBUTION') {
    ElMessage.warning('同城订单请移步至移动端查看详情')
  }
  // order.orderDiscounts.forEach((item: OrderDiscount) => {
  //     couponTypeData[item.sourceType].price = couponTypeData[item.sourceType].price.add(
  //         item.discountItems.map((item) => new Decimal(item.discountAmount)).reduce((pre, current) => current.add(pre)),
  //     )
  // })
  // couponTypeData.freightPrice.price = order.shopOrders
  //     .flatMap((shopOrder) => shopOrder.shopOrderItems)
  //     .map((shopOrderItem) => new Decimal(shopOrderItem.freightPrice))
  //     .reduce((pre, current) => current.add(pre))
  calucateDiscountDetails(order?.orderDiscounts || [], order?.shopOrders || [])
  info.value = order
  if (order?.shopOrders?.[0]?.remark) remark.value = order?.shopOrders[0]?.remark
  // orderDetailsConfig.value = getOrderDetailsConfig(order)
  if (whetherTheDelivery(order)) {
    initFirstDeliveryPage(data)
  }
}
/**
 * @description: 是否已经发货
 * @param {ApiOrder} order
 * @returns {boolean}boolean
 */
function whetherTheDelivery(order?: ApiOrder) {
  if (!order) return false
  return (
    order.status === 'PAID' && order.shopOrders[0].status === 'OK' && order.shopOrders[0].shopOrderItems[0].packageStatus !== 'WAITING_FOR_DELIVER'
  )
}
/**
 * @description:  查询第一个已发货的包裹收货地址并查询物流轨迹
 * @param {*} params
 * @returns {*}
 */
async function initFirstDeliveryPage(params: OrderInfo) {
  const { code, data } = await doGetFirstDeliveryPage(params.no, params.shopOrders[0].no, params.shopOrders[0].shopOrderItems[0].packageId as string)
  if (code !== 200) return ElMessage.error('获取包裹收货地址失败')
  logistics.value = data
  if (!logistics.value) return
  const { expressCompanyCode, expressNo, type, receiverMobile } = logistics.value
  if (type === 'WITHOUT') return // 无需物流发货 不需要调接口
  const { code: status, data: res } = await doGetLogisticsTrajectoryByWaybillNo(expressCompanyCode, expressNo, receiverMobile)
  if (status !== 200) return ElMessage.error('物流轨迹获取失败')
  if (!res.status || res.status !== '200') {
    orderLocation.value[0].context = res.message
    return
  }
  orderLocation.value = res.data.reverse()
}
/**
 * @description: 导航去我的订单
 * @returns {*}
 */
const navmyorder = () => {
  if ($route.query?.orderNo && $route.query?.intOrd) {
    $router.push({
      path: '/personalcenter/assets/integral',
      query: {},
    })
  } else {
    $router.push({
      path: '/personalcenter/order/myorder',
      query: {},
    })
  }
}
/**
 * @description: 去支付
 * @returns {*}
 */
const handlePay = (orderNo: any, boolean: boolean) => {
  if (!orderNo) return
  if (boolean)
    $router.push({
      path: '/pay',
      query: {
        no: orderNo,
      },
    })
  else
    $router.push({
      path: '/pay',
      query: {
        no: orderNo?.no,
        integral: orderNo?.price,
        ruleId: orderNo?.id,
        price: divTenThousand(+orderNo?.freightPrice + +orderNo?.salePrice) as any,
      },
    })
}
/**
 * @description: 导航去评价
 * @returns {*}
 */
const navGoAssess = (orderItem?: ShopOrderItem, order?: ApiOrder) => {
  if (!order || !orderItem) return
  const orderNo = order.no
  const shopId = orderItem.shopId
  const packageId = orderItem.packageId
  $router.push({
    path: '/personalcenter/order/writecomments',
    query: { orderNo, shopId, packageId },
  })
}
/**
 * @description: 取消订单
 * @returns {*}
 */
const handleCancelOrder = async (orderNo?: string) => {
  if (!orderNo) return
  ElMessageBox.confirm(`是否需要取消？`, '提示', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    const { code, data, msg } = await doPutCloseOrderByOrderNo(orderNo)

    if (code !== 200) return ElMessage.error(`${msg ? msg : '取消订单失败'}`)
    ElMessage.success(`订单已取消`)
    if (!info.value) return
    info.value.status = 'BUYER_CLOSED'
  })
}
/**
 * @description: 确认收货
 * @returns {*}
 */
const handleConfirmGoods = () => {
  if (!info.value) return
  const showModalProps = {
    content: '是否确认收货',
    showClose: true,
    isSubmit: true,
  }
  const shopOrder = info.value.shopOrders[0]
  const packageId = shopOrder.shopOrderItems[0].packageId as string
  const shopOrderNo = shopOrder.no
  const orderNo = info.value.no
  const shopId = shopOrder.shopId
  // 通过每一个商品的售后得到 可以改变包状态的数组
  if (!shopOrder.shopOrderItems.every((order) => getAfsStatusCn(order.afsStatus).canChangePackageStatus)) {
    showModalProps.content = '该订单中存在退款宝贝，等待商家确认收货'
    showModalProps.isSubmit = false
    showModalProps.showClose = false
  } else if (!shopOrder.shopOrderItems.every((order) => !getAfsStatusCn(order.afsStatus).canChangePackageStatusText)) {
    showModalProps.content = '该订单中存在退款宝贝，确认收货将关闭退款'
  }
  // 该订单中存在退款宝贝，等待商家确认收货  该订单中存在退款宝贝，确认收货将关闭退款

  ElMessageBox.confirm(`${showModalProps.content}`, '提示', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    if (!showModalProps.isSubmit) return
    const { code, msg } = await doconfirmGoods(orderNo, shopOrderNo, shopId as string)

    if (code !== 200) return ElMessage.error(`${msg ? msg : '确认收货失败'}`)
    $router.push({
      path: '/personalcenter/order/writecomments',
      query: { orderNo, shopId, packageId },
    })
  })
}
/**
 * @description: 商品价格计算
 * @param {*} data
 * @returns {*}
 */
function computeOrderPrice(data?: ShopOrder) {
  if (!data) return 0
  return data.shopOrderItems.reduce((pre, cur) => {
    return pre + cur.num * Number(useConversionPrice(cur.salePrice))
  }, 0)
}
/**
 * @description: 运费价格计算
 * @param {*} data
 * @returns {*}
 */
function computefreightPrice(data?: ShopOrder) {
  if (!data) return 0
  return data.shopOrderItems.reduce((pre, cur) => {
    return pre + Number(useConversionPrice(cur.freightPrice))
  }, 0)
}
/**
 * @description: 实付金额计算
 * @param {*} data
 * @returns {*}
 */
function TotalPrice() {
  if (!info.value) return
  const price = (computeOrderPrice(info.value.shopOrders?.[0]) + computefreightPrice(info.value.shopOrders?.[0])).toFixed(2)
  return price
}
// 积分订单
const intOrd = ref(false)
const integralOrder = ref()
const integralOrderDetail = async () => {
  if ($route.query?.orderNo && $route.query?.intOrd) {
    intOrd.value = true
    const { data, code, msg } = await doGetIntegralOrderDetail($route.query.orderNo as string)
    if (code === 200) integralOrder.value = data
    else ElMessage.error(msg || '订单详情获取失败')
  } else intOrd.value = false
}
if ($route.query?.orderNo && $route.query?.intOrd) integralOrderDetail()
enum payType {
  '' = '积分',
  BALANCE = '积分+余额支付',
  WECHAT = '积分+微信支付',
  ALIPAY = '积分+支付宝支付',
}
// 确认收货
const confirmReceipt = () => {
  if (!integralOrder.value) return
  const showModalProps = {
    content: '是否确认收货',
    showClose: true,
    isSubmit: true,
  }
  ElMessageBox.confirm(`${showModalProps.content}`, '提示', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    if (!integralOrder.value?.no) return
    const { code, msg } = await doPutCompleteIntegralOrder(integralOrder.value?.no)
    if (code !== 200) return ElMessage.error(`${msg ? msg : '确认收货失败'}`)
    integralOrderDetail()
  })
}
// 取消订单
const integralOrderFn = async () => {
  if (!integralOrder.value) return
  const showModalProps = {
    content: '是否取消订单',
    showClose: true,
    isSubmit: true,
  }
  ElMessageBox.confirm(`${showModalProps.content}`, '提示', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    if (!integralOrder.value?.no) return
    const { code, msg } = await doDelIntegralOrder(integralOrder.value?.no)
    if (code !== 200) return ElMessage.error(`${msg ? msg : '取消订单失败'}`)
    integralOrderDetail()
  })
}
/**
 * @description: 没发货加上运费 发货后不计运费  dealPrice*num - fixPrice <= 0  （freightPrice）
 * @param {*} info 0 元 购买 不展示售后按钮
 * @returns {*}
 */
function isAfsPriceComputed(info: ShopOrderItem) {
  const { dealPrice, num, fixPrice, packageId, freightPrice } = info
  const price = new Decimal(dealPrice).div(num).sub(fixPrice)
  if (packageId) {
    return useConversionPrice(price).toNumber() <= 0
  } else {
    return useConversionPrice(price.add(freightPrice)).toNumber() <= 0
  }
}
/**
 * @description: 导航去申请售后
 * @returns {*}
 */
const navapplyaftersales = (afsOrderInfo: any, orderNo: string) => {
  const refundAmount = new Decimal(afsOrderInfo.dealPrice).mul(afsOrderInfo.num).sub(afsOrderInfo.fixPrice)
  const applyAfterSalesOrder = {
    ...afsOrderInfo,
    itemId: afsOrderInfo.id,
    orderNo: orderNo ? orderNo : info.value?.no,
    refundAmount,
  }
  new storage().setItem('CLIENT-applyAfterSalesOrder', applyAfterSalesOrder, 60 * 60 * 2)
  $router.push({
    path: '/personalcenter/order/applyaftersales',
    query: {},
  })
}
const codeByStoreIdList = ref()
// 到点自提的码
const orderGetCodeByStoreId = async () => {
  if (info.value) {
    const { code, data, msg } = await doGetOrderGetCodeByStoreId(info.value.extra.shopStoreId, info.value.no)
    if (code !== 200) ElMessage.error(msg)
    codeByStoreIdList.value = data
  }
}
const orderStatus = computed(() => getOrderDetailStatusPlus(info.value))
const shouldWriteOff = computed(() => {
  if (orderStatus.value.isClosed === false && ['待发货', '待收货'].includes(orderStatus.value.desc)) return true
  return false
})
// 复制核销码
const copyCode = async (code: string) => {
  try {
    await toClipboard(code)
    ElMessage.success('复制成功')
  } catch (e) {
    ElMessage.error('复制失败')
  }
}
</script>

<template>
  <div bg-white c-pb-30>
    <el-breadcrumb :separator-icon="ArrowRight" c-pt-24 c-w-1190 ma c-mb-24>
      <el-breadcrumb-item cursor-pointer @click="navmyorder"> 个人中心 </el-breadcrumb-item>
      <el-breadcrumb-item cursor-pointer @click="navmyorder"> 订单中心 </el-breadcrumb-item>
      <el-breadcrumb-item cursor-pointer @click="navmyorder"> 我的订单 </el-breadcrumb-item>
      <el-breadcrumb-item>订单详情</el-breadcrumb-item>
    </el-breadcrumb>

    <div c-w-1190 ma b-1 c-bc-eee c-pl-36 c-pt-25 c-pb-15>
      <div flex items-center justify-between>
        <div>
          <div e-c-3 c-fs-18 fw-700 flex items-center>
            <el-icon c-mr-7>
              <Warning />
            </el-icon>
            <template v-if="intOrd">
              当前状态：{{ integralOrder && orderStatusHandle[integralOrder?.status as keyof typeof orderStatusHandle]?.desp }}
            </template>
            <template v-else-if="info && orderStatusHandle[info.status]?.next">
              <template v-if="isAllItemClosed(info?.shopOrders?.[0].shopOrderItems)"> 当前状态：已取消 </template>
              <template v-else> 当前状态：{{ packageStatusHandle[info?.shopOrders?.[0].shopOrderItems?.[0].packageStatus].desp }} </template>
            </template>
            <template v-else> 当前状态：{{ info && orderStatusHandle[info?.status]?.desp }} </template>
          </div>

          <template v-if="intOrd">
            <div
              v-if="intOrd && orderStatusHandle[integralOrder?.status as keyof typeof orderStatusHandle]?.reason"
              style="color: #999; font-size: 14px; margin-top: 5px"
            >
              关闭原因： {{ orderStatusHandle[integralOrder?.status as keyof typeof orderStatusHandle]?.reason }}
            </div>
            <template v-if="integralOrder?.status === 'UNPAID'">
              <div c-fs-14 e-c-3 c-ml-25 c-mt-13 c-mb-14 flex>
                还有 <Countdown :order="integralOrder" c-c-e31436 /> 进行付款，若未及时付款，系统将自动取消订单
              </div>
              <div flex items-center c-ml-25 c-mb-42>
                <div cursor-pointer c-w-80 c-h-30 c-bg-e31436 e-c-f c-fs-14 c-lh-30 c-mr-46 @click="handlePay(integralOrder, false)">立即付款</div>
                <div cursor-pointer c-c-101010 c-fs-12 hover-c-c-e31436 hover-underline @click="integralOrderFn">取消订单</div>
              </div>
            </template>
            <template v-if="integralOrder?.status === 'ON_DELIVERY'">
              <div c-ml-25 c-mb-42 c-mt-14>
                <div c-w-80 c-h-30 c-bg-e31436 e-c-f c-fs-14 c-lh-30 c-mr-46 cursor-pointer @click="confirmReceipt">确认收货</div>
              </div>
            </template>
          </template>
          <template v-if="info && orderStatusHandle[info.status]?.toPaid">
            <div c-fs-14 e-c-3 c-ml-25 c-mt-13 c-mb-14 flex>
              还有 <Countdown :order="info" c-c-e31436 /> 进行付款，若未及时付款，系统将自动取消订单
            </div>
            <div flex items-center c-ml-25 c-mb-42>
              <div cursor-pointer c-w-80 c-h-30 c-bg-e31436 e-c-f c-fs-14 c-lh-30 c-mr-46 @click="handlePay(info?.no, true)">立即付款</div>
              <div cursor-pointer c-c-101010 c-fs-12 hover-c-c-e31436 hover-underline @click="handleCancelOrder(info?.no)">取消订单</div>
            </div>
          </template>
          <template v-if="info && orderStatusHandle[info.status]?.reason">
            <div c-fs-14 e-c-3 c-ml-25 c-mt-13 c-mb-14>关闭原因：{{ orderStatusHandle[info.status].reason }}</div>
          </template>
          <template v-if="info && orderStatusHandle[info.status]?.next && isAllItemClosed(info.shopOrders[0].shopOrderItems)">
            <div c-fs-14 e-c-3 c-ml-25 c-mt-13 c-mb-14>关闭原因：退款成功，款项已原路返还</div>
          </template>
          <template
            v-if="
              info &&
              packageStatusHandle[info.shopOrders[0].shopOrderItems[0].packageStatus].Confirmreceipt &&
              info.distributionMode !== 'INTRA_CITY_DISTRIBUTION'
            "
          >
            <div c-ml-25 c-mb-42 c-mt-14>
              <div c-w-80 c-h-30 c-bg-e31436 e-c-f c-fs-14 c-lh-30 c-mr-46 cursor-pointer @click="handleConfirmGoods">确认收货</div>
            </div>
          </template>

          <template v-if="info && packageStatusHandle[info.shopOrders?.[0]?.shopOrderItems?.[0]?.packageStatus]?.evaluate">
            <div flex items-center c-ml-25 c-mb-42 c-mt-14>
              <div
                c-w-80
                c-h-30
                c-fs-14
                c-lh-30
                c-mr-46
                b-1
                c-bc-E9E9E9
                cursor-pointer
                @click="navGoAssess(info?.shopOrders?.[0]?.shopOrderItems?.[0], info)"
              >
                评价
              </div>
            </div>
          </template>
        </div>
        <el-steps v-if="intOrd" c-w-677 :active="-1" align-center>
          <el-step title="提交订单" :icon="SuccessFilled" />
          <el-step
            title="支付订单"
            :icon="
              integralOrder?.status === 'PAID' || integralOrder?.status === 'ON_DELIVERY' || integralOrder?.status === 'ACCOMPLISH'
                ? SuccessFilled
                : ''
            "
          />
          <el-step title="商家发货" :icon="integralOrder?.status === 'ON_DELIVERY' || integralOrder?.status === 'ACCOMPLISH' ? SuccessFilled : ''" />
          <el-step title="完成收货" :icon="integralOrder?.status === 'ACCOMPLISH' ? SuccessFilled : ''" />
        </el-steps>
        <el-steps v-else c-w-677 :active="-1" align-center>
          <el-step title="提交订单" :icon="SuccessFilled" />
          <el-step
            title="支付订单"
            :icon="
              info &&
              packageStatusHandle[info.shopOrders?.[0]?.shopOrderItems?.[0]?.packageStatus]?.steps > 1 &&
              orderStatusHandle[info.status]?.next &&
              !isAllItemClosed(info.shopOrders?.[0]?.shopOrderItems)
                ? SuccessFilled
                : ''
            "
          />
          <el-step
            title="商家发货"
            :icon="
              info &&
              packageStatusHandle[info.shopOrders?.[0].shopOrderItems?.[0].packageStatus]?.steps > 2 &&
              !isAllItemClosed(info.shopOrders?.[0]?.shopOrderItems)
                ? SuccessFilled
                : ''
            "
          />
          <el-step
            title="完成收货"
            :icon="
              info &&
              packageStatusHandle[info.shopOrders?.[0].shopOrderItems?.[0].packageStatus]?.steps > 3 &&
              !isAllItemClosed(info.shopOrders?.[0]?.shopOrderItems)
                ? SuccessFilled
                : ''
            "
          />
        </el-steps>
      </div>
      <div v-if="whetherTheDelivery(info) && logistics?.expressCompanyName" e-c-3 c-fs-14 text-left>
        <div c-mt-21>物流公司：{{ logistics?.expressCompanyName }} 运单号：{{ logistics?.expressNo }}</div>
        <div c-mt-17 c-mb-23>温馨提示：订单较多，物流信息会稍有延迟，您可前往快递官网自助查询</div>
        <div :class="showAllLocation ? '' : 'overflow-hidden c-h-190'">
          <template v-for="(item, index) in orderLocation" :key="index">
            <div flex e-c-9 items-center>
              <div c-w-136>
                {{ item?.time }}
              </div>
              <div v-if="index === 0" e-br c-w-15 c-h-15 c-bg-e31436 c-mr-16 c-ml-10 e-c-f text-center c-lh-15 fw-700>></div>
              <div v-else e-br c-w-9 c-h-9 c-bg-999 c-mr-19 c-ml-13 />
              <div :class="index === 0 ? 'c-c-e31436' : ''" c-w-900>
                {{ item?.context }}
              </div>
            </div>
            <div v-if="index !== orderLocation.length - 1" b-l-1 c-bc-bbb c-h-38 c-ml-153 />
          </template>
        </div>
        <div
          v-if="!showAllLocation && orderLocation.length > 4"
          c-ml-75
          cursor-pointer
          hover-c-c-e31436
          hover-underline
          @click="showAllLocation = true"
        >
          显示全部
        </div>
      </div>
    </div>
    <div v-if="codeByStoreIdList" c-w-1190 ma b-1 c-bc-eee c-mt-13 c-fs-14 e-c-3 text-left c-pb-19 flex>
      <div style="width: 50%">
        <div c-h-48 c-lh-48 e-c-9 b-b c-bc-eee c-pl-37>自提点信息</div>
        <div c-mt-16 c-pl-37>门店名称：{{ codeByStoreIdList?.shopStore?.storeName }}</div>
        <div c-mt-16 c-pl-37>联系电话：{{ codeByStoreIdList?.shopStore?.functionaryPhone }}</div>
        <div c-mt-16 c-pl-37>门店地址：{{ codeByStoreIdList?.shopStore?.detailedAddress }}</div>
        <div c-mt-16 c-pl-37 c-c-999>配送方式：<span>门店自提</span></div>
        <RemarkView :remark="remark?.items || []" c-mt-10 c-pl-37 class="remark" />
      </div>
      <div style="width: 50%">
        <div c-h-48 c-lh-48 e-c-9 b-b c-bc-eee c-pl-37 flex justify-between>
          <span>提货人信息</span><span c-c-000>请按时到店自提</span>
          <span c-pr-37 c-c-e31436>{{ shouldWriteOff ? '待核销' : '已核销' }}</span>
        </div>
        <div c-mt-16 c-pl-37 c-c-e31436 fw-700>
          核销码：{{ codeByStoreIdList?.code }}
          <span c-ml-90 cursor-pointer c-c-005cf4 fw-400 @click="copyCode(codeByStoreIdList.code)">复制</span>
        </div>
        <div c-mt-16 c-pl-37>提货人：{{ info?.orderReceiver?.name }}</div>
        <div c-mt-16 c-pl-37>联系电话：{{ info?.orderReceiver?.mobile }}</div>
        <div c-mt-16 c-pl-37>提货时间：{{ codeByStoreIdList?.getPickUpTime }}</div>
      </div>
    </div>

    <div v-if="intOrd" c-w-1190 ma b-1 c-bc-eee c-mt-13 c-fs-14 e-c-3 text-left c-pb-19>
      <div c-h-48 c-lh-48 e-c-9 b-b c-bc-eee c-pl-37>订单信息</div>
      <div c-mt-16 c-pl-37>
        <span v-if="integralOrder?.productType !== 'VIRTUAL_PRODUCT'" fw-700>收货地址:</span>
        {{ integralOrder?.integralOrderReceiver?.name }} {{ integralOrder?.integralOrderReceiver?.mobile }}
        <q-address :address="integralOrder?.integralOrderReceiver?.areaCode" />
        {{ integralOrder?.integralOrderReceiver?.address }}
      </div>
      <div c-mt-10 c-pl-37>订单编号: {{ integralOrder?.no }}</div>
      <div c-mt-10 c-pl-37>下单时间: {{ integralOrder?.createTime }}</div>
      <div v-if="integralOrder?.shopOrderPackages" c-mt-10 c-pl-37>发货时间: {{ integralOrder?.deliveryTime }}</div>
      <div
        v-if="integralOrder && packageStatusHandle[integralOrder.shopOrders?.[0]?.shopOrderItems?.[0]?.packageStatus as keyof typeof packageStatusHandle]?.steps === 4"
        c-mt-10
        c-pl-37
      >
        成交时间: {{ integralOrder?.payTime }}
      </div>
      <div v-if="integralOrder?.status !== 'SYSTEM_CLOSED'" c-mt-10 c-pl-37>
        支付方式: {{ payType[(integralOrder?.integralOrderPayment?.secPayType || '') as keyof typeof payType] }}
      </div>
    </div>
    <div v-else-if="!intOrd && !codeByStoreIdList" c-w-1190 ma b-1 c-bc-eee c-mt-13 c-fs-14 e-c-3 text-left c-pb-19>
      <div c-h-48 c-lh-48 e-c-9 b-b c-bc-eee c-pl-37>订单信息</div>
      <div c-mt-16 c-pl-37>
        <span fw-700>收货地址:</span>
        {{ info?.orderReceiver?.name }} {{ info?.orderReceiver?.mobile }}
        <!-- <q-address :address="info?.orderReceiver?.areaCode" />  -->
        {{ info?.orderReceiver?.address }}
      </div>
      <div c-mt-10 c-pl-37>订单编号: {{ info?.no }}</div>
      <div c-mt-10 c-pl-37>下单时间: {{ info?.createTime }}</div>
      <RemarkView :remark="remark?.items || []" c-mt-10 c-pl-37 class="remark" />
      <div v-if="info?.shopOrderPackages" c-mt-10 c-pl-37>发货时间: {{ info?.shopOrderPackages?.[0]?.createTime }}</div>
      <div v-if="info && packageStatusHandle[info.shopOrders?.[0]?.shopOrderItems?.[0]?.packageStatus]?.steps === 4" c-mt-10 c-pl-37>
        成交时间: {{ info?.updateTime }}
      </div>
    </div>
    <div c-w-1190 ma b-1 c-bc-eee c-mt-13 c-fs-12 e-c-3>
      <div c-fs-14 e-c-9 b-b c-bc-eee c-h-48 flex items-center>
        <div c-ml-101>商品信息</div>
        <div c-ml-460>售价（元）</div>
        <div c-ml-270>数量</div>
        <!-- <div c-ml-114>优惠</div> -->
      </div>
      <div v-if="intOrd" c-pl-37 text-left>
        <div fw-700 c-mt-10 c-mb-18 />
        <div flex items-center c-mb-26>
          <img :src="integralOrder?.image" c-w-80 c-h-80 />
          <div c-ml-9>
            <div c-w-354 c-ellipsis-2>
              {{ integralOrder?.productName }}
            </div>
            <div c-w-354 c-mt-19>
              {{ integralOrder?.specs }}
            </div>
          </div>
          <div c-w-240 c-ml-50 style="text-align: center">
            {{ integralOrder?.price }}积分
            <span v-if="integralOrder?.salePrice !== '0'"> + ￥{{ divTenThousand(integralOrder?.salePrice).toFixed(2) }}</span>
          </div>
          <div c-w-50 c-ml-180 text-center>
            {{ integralOrder?.num }}
          </div>
          <!-- <div c-w-80 c-ml-77 text-center>满20减5</div> -->
        </div>
      </div>
      <div v-else c-pl-37 text-left>
        <template v-for="item in info?.shopOrders || []" :key="item.no">
          <div fw-700 c-mt-10 c-mb-18>
            {{ item.shopName }}
          </div>
          <div v-for="ite in item.shopOrderItems" :key="ite.id" flex items-center c-mb-26>
            <img :src="ite.image" c-w-80 c-h-80 />
            <div c-ml-9>
              <div c-w-354 c-ellipsis-2>
                {{ ite?.productName }}
              </div>
              <div c-w-354 c-mt-19>
                {{ ite?.specs && ite?.specs.join(',') }}
              </div>
            </div>
            <div c-w-80 c-ml-152>
              {{ useConversionPrice(ite?.salePrice).toFixed(2) }}
            </div>
            <div c-w-50 c-ml-235 text-center>
              {{ ite?.num }}
            </div>

            <!-- <div c-ml-90 cursor-pointer style="border: 1px solid #ccc; padding: 2px 5px">申请售后</div> -->
            <!-- <div c-w-80 c-ml-77 text-center>满20减5</div> -->
            <div
              v-if="
                ite?.packageStatus === 'BUYER_COMMENTED_COMPLETED' ||
                ite?.packageStatus === 'SYSTEM_COMMENTED_COMPLETED' ||
                ite?.packageStatus === 'WAITING_FOR_DELIVER' ||
                !getAfsStatusCn(ite?.afsStatus).applyForAgain ||
                isAfsPriceComputed(ite)
              "
            />
            <div v-else-if="isAllItemClosed(item?.shopOrderItems)" c-ml-90 cursor-pointer>查看退款</div>
            <div
              v-if="
                !isAfsPriceComputed(ite) &&
                info?.status === 'PAID' &&
                ite.afsStatus !== 'REFUND_REQUEST' &&
                (ite.afsStatus === 'NONE' || ite.afsStatus === 'BUYER_CLOSED' || ite.afsStatus === 'SYSTEM_CLOSED') &&
                info.distributionMode !== 'INTRA_CITY_DISTRIBUTION'
              "
              c-ml-90
              cursor-pointer
              @click="navapplyaftersales(ite, item?.orderNo)"
            >
              申请售后
            </div>
          </div>
          <div flex justify-between align-center c-mt-10 c-mb-18 c-pr-25>
            <span>店铺优惠</span>
            <span c-red fw-700>-{{ shopDiscontMap.get(item.shopId)?.toFixed(2) || '0.00' }}</span>
          </div>
        </template>
      </div>
    </div>
    <div v-if="intOrd" c-mt-30 c-w-1190 ma b-1 c-bc-eee e-c-3 c-fs-12 c-pr-25 c-pt-18 c-pb-8 c-bg-f7f7f7>
      <div flex justify-end c-mb-9>
        <div>商品总价：</div>
        <div c-w-230 flex justify-end>
          {{ integralOrder?.price }}积分
          <span v-if="integralOrder?.salePrice !== '0'"> + ￥{{ divTenThousand(integralOrder?.salePrice).toFixed(2) }}</span>
        </div>
      </div>
      <div flex justify-end c-mb-9>
        <div>运费：</div>
        <div c-w-230 flex justify-end>￥{{ divTenThousand(integralOrder?.freightPrice).toFixed(2) }}</div>
      </div>
      <div flex justify-end c-mb-9>
        <div>实付金额：</div>
        <div c-w-230 flex justify-end c-fs-16 c-c-e31436 fw-700>
          {{ integralOrder?.price }}积分
          <span v-if="+integralOrder?.freightPrice + +integralOrder?.salePrice !== 0">
            + ￥{{ divTenThousand(+integralOrder?.freightPrice + +integralOrder?.salePrice).toFixed(2) }}</span
          >
        </div>
      </div>
    </div>
    <div v-else c-mt-30 c-w-1190 ma b-1 c-bc-eee e-c-3 c-fs-12 c-pr-25 c-pt-18 c-pb-8 c-bg-f7f7f7>
      <div flex justify-end c-mb-9>
        <div>商品总价：</div>
        <div c-w-96 flex justify-end>￥{{ computeOrderPrice(info?.shopOrders?.[0])?.toFixed(2) }}</div>
      </div>
      <!-- <div flex justify-end c-mb-9>
                <div>运费：</div>
                <div c-w-96 flex justify-end>￥{{ computefreightPrice(info?.shopOrders?.[0])?.toFixed(2) }}</div>
            </div> -->
      <normal-order-discount :discount-data-by-type="discountDataByType" />
      <div flex justify-end c-mb-9>
        <div>实付金额：</div>
        <div c-w-96 flex justify-end c-fs-18 c-c-e31436 fw-700>￥{{ divTenThousand(info?.orderPayment?.payAmount)?.toFixed(2) || '0.00' }}</div>
      </div>
    </div>
  </div>
</template>

<style scoped>
:deep(.el-icon svg) {
  color: #e31436;
}

:deep(.el-step__title.is-wait) {
  color: #333333;
  font-size: 14px;
}

:deep(.el-step.is-center .el-step__head) {
  height: 31.5px;
}

:deep(.el-step.is-horizontal .el-step__line) {
  height: 1px;
}
:deep(.remark .el-descriptions__cell) {
  display: flex;
}
</style>
