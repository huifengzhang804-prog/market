import type { ApiOrder, ORDERSTATUS, PACKAGESTATUS, QUERYORDERSTATUS, ShopOrder, SHOPORDERSTATUS } from '@/pluginPackage/order/orderList/types'

export enum IcStatus {
  '' = '',
  /**
   * 运费价格计算 待发单（未发货）
   * 这种状态的单子可以随时丢弃
   */
  PRICE_PADDING = '待发货',
  /**
   * 待接单
   */
  PENDING = '待接单',
  /**
   * 已接单待到店
   */
  TAKEN = '待到店',
  /**
   * 已到店 待取货
   */
  ARRIVED_SHOP = '待取货',
  /**
   * 已取货 配送中
   */
  PICKUP = '配送中',
  /**
   * 已送达
   */
  DELIVERED = '已送达',
  /**
   * 异常 配送异常
   */
  ERROR = '配送异常',
}

export const UPLOAD_URL = import.meta.env.VITE_BASE_URL + import.meta.env.VITE_UPLOAD_URI
type OptionalStatus = keyof typeof ORDERSTATUS | keyof typeof SHOPORDERSTATUS | keyof typeof PACKAGESTATUS
// 订单详情图标配置
export const orderDetailIconConfig = {
  待支付: 'icon-a-Frame7',
  待发货: 'icon-a-Frame8',
  待收货: 'icon-a-Frame9',
  交易成功: 'icon-jiaoyichenggong',
  待评价: 'icon-jiaoyichenggong',
  已关闭: 'icon-a-Frame11',
  已完成: 'icon-a-Frame10',
  拼团中: 'icon-zhangdan',
  拼团失败: 'icon-jiaoyichenggong',
  待接单: 'icon-shenhezhong',
  待到店: 'icon-shanghugenjin',
  待取货: 'icon-daiquhuo',
  配送中: 'icon-peisongguanli',
  已送达: 'icon-qianshou-',
  配送异常: 'icon-yichang',
} as const

/**
 * 订单状态 超级版
 */
export interface OrderStatusPlus {
  //订单状态 描述
  desc: string
  //是否已关闭
  isClosed: boolean
  //关闭时间
  closeTime?: string
}

export enum OrderStatusZh {
  WAITING_FOR_DELIVER = '待发货',
  WAITING_FOR_RECEIVE = '待收货',
  WAITING_FOR_DELIVER_PART = '待发货 (部分发货)',
  COMMENTED_COMPLETED = '已完成',
  WAITING_FOR_COMMENT = '待评价',
}

/**
 * 根据订单详情获取订单状态
 * @param order 订单对象
 * @param shopId 可选的店铺ID，用于多店铺订单
 * @returns 订单状态信息
 */
export const getOrderDetailStatusPlus = (order?: ApiOrder, shopId?: Long): OrderStatusPlus => {
  // 空订单检查
  if (!order) return { desc: '', isClosed: false }
  // 未支付订单直接返回状态
  if (order.status !== 'PAID') {
    const statusPlus = OrderStatusPlusHandler[order.status]
    // 为关闭状态添加关闭时间
    if (['已关闭', '拼团失败'].includes(statusPlus.desc)) {
      statusPlus.closeTime = order.updateTime
    }
    return statusPlus
  }
  // 获取店铺订单
  const shopOrders = order.shopOrders || []
  if (!shopOrders.length) return { desc: '', isClosed: false }
  // 默认使用第一个店铺订单
  let shopOrder = shopOrders[0]
  // 如果指定了店铺ID，查找对应的店铺订单
  if (shopId && shopId !== shopOrder.shopId) {
    const targetShopOrder = shopOrders.find((order) => order && shopId === order.shopId)
    if (targetShopOrder) {
      shopOrder = targetShopOrder
    }
  }
  // 处理店铺订单状态
  return orderStatusPlus(shopOrder, order)
}

/**
 * 获取店铺订单的详细状态
 * @param shopOrder 店铺订单
 * @param order 主订单
 * @param showIc 是否显示同城配送状态
 * @returns 订单状态信息
 */
export function orderStatusPlus(shopOrder: ShopOrder, order: ApiOrder, showIc = true): OrderStatusPlus {
  // 初始化状态为已关闭
  const statusPlus: OrderStatusPlus = {
    desc: '已关闭',
    isClosed: true, // 是否展示评价按钮
  }
  // 如果店铺订单状态不是OK，则返回已关闭状态
  if (shopOrder?.status !== 'OK') {
    return {
      ...statusPlus,
      closeTime: shopOrder?.updateTime,
    }
  }
  // 设置为未关闭状态
  statusPlus.isClosed = false
  // 处理同城配送订单
  if (order.icStatus !== void 0 && order.extra?.distributionMode === 'INTRA_CITY_DISTRIBUTION' && showIc) {
    return handleIntraCityOrder(shopOrder, order, statusPlus)
  }
  // 处理普通订单
  return handleNormalOrder(shopOrder, statusPlus)
}

/**
 * 处理同城配送订单状态
 */
function handleIntraCityOrder(shopOrder: ShopOrder, order: ApiOrder, statusPlus: OrderStatusPlus): OrderStatusPlus {
  // 设置状态描述为同城状态
  statusPlus.desc = IcStatus[order.icStatus!]
  const shopOrderItems = shopOrder.shopOrderItems || []
  // 处理配送异常状态
  if (order.icStatus === 'ERROR') {
    const firstItem = shopOrderItems[0]
    if (firstItem?.status === 'OK' && firstItem.packageStatus === 'WAITING_FOR_DELIVER') {
      statusPlus.desc = IcStatus['PRICE_PADDING'] // 异常转待发货
    }
    return statusPlus
  }
  // 处理已送达状态: 待收货 / 待评价 / 已完成
  if (order.icStatus === 'DELIVERED') {
    const packageStatuses = shopOrderItems.map((item) => item.packageStatus)
    // 按优先级判断状态
    if (packageStatuses.includes('WAITING_FOR_RECEIVE')) {
      statusPlus.desc = '待收货'
    }
    // 待评价状态
    if (packageStatuses.some((status) => ['BUYER_WAITING_FOR_COMMENT', 'SYSTEM_WAITING_FOR_COMMENT'].includes(status))) {
      statusPlus.desc = '待评价'
    }
    // 已完成状态
    if (packageStatuses.some((status) => ['BUYER_COMMENTED_COMPLETED', 'SYSTEM_COMMENTED_COMPLETED'].includes(status))) {
      statusPlus.desc = '已完成'
      statusPlus.isClosed = true
    }
  }
  return statusPlus
}

/**
 * 处理普通订单状态
 */
function handleNormalOrder(shopOrder: ShopOrder, statusPlus: OrderStatusPlus): OrderStatusPlus {
  const shopOrderItems = shopOrder.shopOrderItems || []
  // 统计各种状态的数量
  const stats = {
    deliverNum: 0, // 待收货数量
    unDeliverNum: 0, // 待发货数量
    okNum: 0, // 正常状态数量
    evaluation: 0, // 已评价数量
  }
  // 计算最新的关闭时间
  let latestCloseTime = statusPlus.closeTime
  // 统计各状态数量
  for (const item of shopOrderItems) {
    if (item.status === 'OK') {
      stats.okNum++
      if (item.packageStatus === 'WAITING_FOR_DELIVER') {
        stats.unDeliverNum++
      }
      if (item.packageStatus === 'WAITING_FOR_RECEIVE') {
        stats.deliverNum++
      }
      if (['BUYER_COMMENTED_COMPLETED', 'SYSTEM_COMMENTED_COMPLETED'].includes(item.packageStatus)) {
        stats.evaluation++
      }
    }
    // 更新关闭时间
    if (item.updateTime) {
      if (!latestCloseTime) {
        latestCloseTime = item.updateTime
      } else if (item.updateTime > latestCloseTime) {
        latestCloseTime = item.updateTime
      }
    }
  }
  statusPlus.closeTime = latestCloseTime
  // 如果没有正常状态的订单项，直接返回
  if (!stats.okNum) return statusPlus
  // 状态判断优先级：
  // 1. 全部待发货 > 2. 全部待收货 > 3. 部分发货 > 4. 全部已评价 > 5. 默认待评价
  if (stats.unDeliverNum === stats.okNum) {
    // 所有订单项均未发货
    statusPlus.desc = OrderStatusZh.WAITING_FOR_DELIVER
  } else if (stats.deliverNum === stats.okNum) {
    // 所有订单项均已发货待收货
    statusPlus.desc = OrderStatusZh.WAITING_FOR_RECEIVE
  } else if (stats.deliverNum + stats.unDeliverNum === stats.okNum) {
    // 存在混合发货状态 (部分已发/部分未发)
    statusPlus.desc = OrderStatusZh.WAITING_FOR_DELIVER_PART
  } else if (stats.evaluation === stats.okNum) {
    // 所有订单项已完成评价
    statusPlus.desc = OrderStatusZh.COMMENTED_COMPLETED
    statusPlus.isClosed = true
  } else {
    // 默认：存在待评价订单项
    statusPlus.desc = OrderStatusZh.WAITING_FOR_COMMENT
  }
  return statusPlus
}

/**
 * UNPAID: '等待买家付款',
 * PAID: '已支付',
 * BUYER_CLOSED: '买家关闭订单',
 * SYSTEM_CLOSED: '系统关闭订单',
 * SELLER_CLOSED: '卖家关闭订单',
 */
const OrderStatusPlusHandler: Record<keyof typeof ORDERSTATUS, OrderStatusPlus> = {
  UNPAID: {
    desc: '待支付',
    isClosed: false,
  },
  PAID: {
    desc: '待发货',
    isClosed: false,
  },
  BUYER_CLOSED: {
    desc: '已关闭',
    isClosed: true,
  },
  SYSTEM_CLOSED: {
    desc: '已关闭',
    isClosed: true,
  },
  SELLER_CLOSED: {
    desc: '已关闭',
    isClosed: true,
  },
  TEAMING: {
    desc: '拼团中',
    isClosed: false,
  },
  TEAM_FAIL: {
    desc: '拼团失败',
    isClosed: true,
  },
}

export const queryStatus: Record<keyof typeof QUERYORDERSTATUS, string> = {
  UNPAID: '待付款',
  UN_DELIVERY: '待发货',
  UN_RECEIVE: '待收货',
  COMPLETED: '已完成',
  UN_COMMENT: '待评价',
  CLOSED: '已关闭',
}
const orderStatus: Record<keyof typeof ORDERSTATUS, string> = {
  UNPAID: '等待买家付款',
  PAID: '已支付',
  BUYER_CLOSED: '已关闭',
  SYSTEM_CLOSED: '已关闭',
  SELLER_CLOSED: '已关闭',
  TEAMING: '拼团中',
  TEAM_FAIL: '拼团失败',
}

interface OrderDetailsConfig {
  isShowAfsBtn: boolean
  isShowFooterBtn: boolean
  isShowFooterRightBtnText: string
  isShowModifyAddressBtn: boolean
}

const unpaid = (orderDetailsConfig: OrderDetailsConfig) => {
  orderDetailsConfig.isShowAfsBtn = false
  orderDetailsConfig.isShowFooterRightBtnText = '立即付款'
  return orderDetailsConfig
}
// * @param NONE 无售后
// * @param REFUND_REQUEST 申请退款
// * @param SYSTEM_REFUND_AGREE 系统自动同意退款申请
// * @param REFUND_AGREE 已同意退款申请
// * @param REFUND_REJECT 拒绝了退款申请
// * @param REFUNDED 退款已到账
// * @param RETURN_REFUND_REQUEST 申请退货退款
// * @param SYSTEM_RETURN_REFUND_AGREE 系统自动同意退货退款申请
// * @param RETURN_REFUND_AGREE 已同意退货退款申请
// * @param RETURN_REFUND_REJECT 拒绝了退货退款申请
// * @param SYSTEM_RETURNED_REFUND_CONFIRM 退货退款 系统自动确认收货
// * @param RETURNED_REFUND   退货已发出
// * @param SYSTEM_CLOSED 系统自动关闭
// * @param RETURNED_REFUND_CONFIRM 确认退货已收到
// * @param RETURNED_REFUND_REJECT 已拒绝收货
// * @param  RETURNED_REFUNDED 退货退款已完成
// * @param  BUYER_CLOSED  主动撤销了售后申请
const handledSeparately = (orderDetailsConfig: OrderDetailsConfig) => {
  // 此订单处于关闭状态 说明退款了 需要展示退款成功按钮
  orderDetailsConfig.isShowAfsBtn = true
}
const completed = (shopOrders: ShopOrder, orderDetailsConfig: OrderDetailsConfig) => {
  const isCompleted = shopOrders.shopOrderItems.some((orderItem) => orderItem.packageStatus === 'BUYER_COMMENTED_COMPLETED')
  const isCompletedSystem = shopOrders.shopOrderItems.some((orderItem) => orderItem.packageStatus === 'SYSTEM_COMMENTED_COMPLETED')
  if (isCompleted || isCompletedSystem) {
    orderDetailsConfig.isShowFooterBtn = false
    orderDetailsConfig.isShowFooterRightBtnText = ''
    orderDetailsConfig.isShowAfsBtn = false
    return orderDetailsConfig
  }

  orderDetailsConfig.isShowFooterBtn = false
  orderDetailsConfig.isShowFooterRightBtnText = ''
  orderDetailsConfig.isShowAfsBtn = false
  return orderDetailsConfig
}
// 已发货
const sendGoodsNumNext = (shopOrders: ShopOrder, orderDetailsConfig: OrderDetailsConfig) => {
  const confirmGoods = shopOrders.shopOrderItems.some((orderItem) => orderItem.packageStatus === 'WAITING_FOR_RECEIVE')

  if (confirmGoods) {
    orderDetailsConfig.isShowAfsBtn = true
    orderDetailsConfig.isShowFooterRightBtnText = '确认收货'
    return orderDetailsConfig
  }
  const comment = shopOrders.shopOrderItems.some((orderItem) => orderItem.packageStatus === 'BUYER_WAITING_FOR_COMMENT')
  const commentSystem = shopOrders.shopOrderItems.some((orderItem) => orderItem.packageStatus === 'SYSTEM_WAITING_FOR_COMMENT')
  if (comment || commentSystem) {
    orderDetailsConfig.isShowFooterRightBtnText = '评价'
    orderDetailsConfig.isShowAfsBtn = true
    return orderDetailsConfig
  }
  // 已完成
  return completed(shopOrders, orderDetailsConfig)
}
const pagesStatus = (shopOrders: ShopOrder, orderDetailsConfig: OrderDetailsConfig) => {
  const len = shopOrders.shopOrderItems.length
  // sendGoodsNum 找出待发货的订单个数
  const sendGoodsNum = shopOrders.shopOrderItems.filter((orderItem) => orderItem.packageStatus === 'WAITING_FOR_DELIVER').length
  if (sendGoodsNum === len) {
    orderDetailsConfig.isShowModifyAddressBtn = true
    orderDetailsConfig.isShowAfsBtn = true
    orderDetailsConfig.isShowFooterRightBtnText = ''
    return orderDetailsConfig
  }

  return sendGoodsNumNext(shopOrders, orderDetailsConfig)
}
const shopOrdersOk = (shopOrders: ShopOrder, orderDetailsConfig: OrderDetailsConfig) => {
  const okNum = shopOrders.shopOrderItems.filter((orderItem) => orderItem.status === 'OK').length
  if (okNum === shopOrders.shopOrderItems.length) {
    // 所有的订单状态都是'OK' 查看包状态
    return pagesStatus(shopOrders, orderDetailsConfig)
  }
  // 订单状态不一致分开处理
  handledSeparately(orderDetailsConfig)
  return orderDetailsConfig
}

/**
 * 订单详情状态页面配置
 * @param {*} order
 */
export const getOrderDetailsConfig = (order: ApiOrder) => {
  const orderDetailsConfig = {
    isShowAfsBtn: false,
    isShowFooterBtn: true,
    isShowFooterRightBtnText: '',
    isShowModifyAddressBtn: false,
  }
  if (order.status === 'UNPAID') return unpaid(orderDetailsConfig)
  // 拼团判断
  if (['TEAM'].includes(order.type) && order.status !== 'TEAM_FAIL') {
    const newOrderDetailsConfig = shopOrdersOk(order.shopOrders[0], orderDetailsConfig)
    newOrderDetailsConfig.isShowFooterBtn = true
    newOrderDetailsConfig.isShowFooterRightBtnText = '我的拼团'
    return newOrderDetailsConfig
  }
  if (order.shopOrders[0].status === 'OK') return shopOrdersOk(order.shopOrders[0], orderDetailsConfig)
  return orderDetailsConfig
}

/**
 * 如果是等待付款就展示付款按钮
 * @param {OptionalStatus} status
 */
export const isUnpaidOrder = (status: OptionalStatus) => {
  return ['UNPAID'].includes(status)
}
/**
 * 未付款订单状态改中文
 * @param {keyof} str
 */
export const getOrdercn = (str: keyof typeof ORDERSTATUS) => {
  return orderStatus[str]
}
// 拼团判断 展示我的拼团按钮
export const isTeam = (order: ApiOrder) => {
  return ['TEAM'].includes(order.type) && order.status !== 'TEAM_FAIL' && order.status !== 'UNPAID'
}
/**
 * 是否支付订单
 * @param {OptionalStatus} status 订单下所有状态
 */
export const isPayOrder = (status: OptionalStatus) => {
  return !['UNPAID'].includes(status)
}
/**
 * 是否待收货
 * @param {OptionalStatus} status 订单下所有状态
 */
export const isReceive = (status: OptionalStatus) => {
  return ['WAITING_FOR_RECEIVE', 'BUYER_COMMENTED_COMPLETED'].includes(status)
}

/**
 * 是否已完成
 * @param {OptionalStatus} status 订单下所有状态
 */
export const isCompleted = (status: OptionalStatus) => {
  return ['BUYER_COMMENTED_COMPLETED', 'SYSTEM_COMMENTED_COMPLETED'].includes(status)
}
/**
 * 是否待评价
 * @param {OptionalStatus} status 订单下所有状态
 */
export const isComment = (status: OptionalStatus) => {
  return ['BUYER_WAITING_FOR_COMMENT', 'SYSTEM_WAITING_FOR_COMMENT'].includes(status)
}
/**
 * 关闭订单 展示删除订单按钮
 * @param {OptionalStatus} status 订单下所有状态
 */
export const isCloseOrder = (status: OptionalStatus) => {
  return ['BUYER_CLOSED', 'SYSTEM_CLOSED', 'SELLER_CLOSED'].includes(status)
}
